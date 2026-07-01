package example.com.fielthyapps.Feature.Physical

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.lifecycleScope
import example.com.fielthyapps.R
import example.com.fielthyapps.Service.DataLayerListenerService
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.Locale

class HealthConnectActivity : AppCompatActivity() {

    private lateinit var healthConnectClient: HealthConnectClient
    private lateinit var iVKembali: ImageView
    private lateinit var tvStep: TextView
    private lateinit var tvHeartBeat: TextView
    private lateinit var tvActiveCalories: TextView
    private lateinit var tvTipsAktivitas: TextView

    private val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class)
    )

    private val requestPermissions =
        registerForActivityResult(PermissionController.createRequestPermissionResultContract()) { granted ->
            if (granted.containsAll(permissions)) {
                initHealthConnectClient()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_connect)

        startService(Intent(this, DataLayerListenerService::class.java))

        initUI()
    }

    override fun onResume() {
        super.onResume()
        initHealthConnect()
    }

    private fun initUI() {
        iVKembali = findViewById(R.id.iV_kembali)
        tvStep = findViewById(R.id.tvStep)
        tvHeartBeat = findViewById(R.id.tvHeartBeat)
        tvActiveCalories = findViewById(R.id.tvActiveCalories)
        tvTipsAktivitas = findViewById(R.id.tV_tips_aktivitas)

        iVKembali.setOnClickListener {
            finish()
        }
    }

    private fun initHealthConnect() {

        val providerPackageName = "com.google.android.apps.healthdata"

        val sdkStatus =
            HealthConnectClient.getSdkStatus(
                applicationContext,
                providerPackageName
            )

        when (sdkStatus) {

            HealthConnectClient.SDK_UNAVAILABLE -> {

                Toast.makeText(
                    this,
                    "Perangkat ini tidak mendukung Health Connect",
                    Toast.LENGTH_LONG
                ).show()

                finish()
                return
            }

            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {

                val uri =
                    Uri.parse(
                        "market://details?id=$providerPackageName&url=healthconnect%3A%2F%2Fonboarding"
                    )

                startActivity(
                    Intent(Intent.ACTION_VIEW, uri)
                )

                finish()
                return
            }

            HealthConnectClient.SDK_AVAILABLE -> {

                try {

                    healthConnectClient =
                        HealthConnectClient.getOrCreate(applicationContext)

                } catch (e: IllegalStateException) {

                    Toast.makeText(
                        this,
                        "Health Connect tidak tersedia",
                        Toast.LENGTH_LONG
                    ).show()

                    finish()
                    return
                }
            }
        }

        lifecycleScope.launch {

            val granted =
                healthConnectClient.permissionController
                    .getGrantedPermissions()

            if (granted.containsAll(permissions)) {
                initHealthConnectClient()
            } else {
                requestPermissions.launch(permissions)
            }
        }
    }

    private fun initHealthConnectClient() {

        lifecycleScope.launch {

            val now = ZonedDateTime.now(ZoneId.systemDefault())

            val startTime =
                now.truncatedTo(ChronoUnit.DAYS).toInstant()

            val endTime =
                now.plusDays(1)
                    .truncatedTo(ChronoUnit.DAYS)
                    .toInstant()

            readHealthConnectRecord(startTime, endTime)
        }
    }

    private suspend fun readHealthConnectRecord(
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            val stepResponse = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )

            val totalSteps = stepResponse.records.sumOf { it.count }
            tvStep.text = totalSteps.toString()
            setRandomTips(totalSteps)

            getSharedPreferences("health_data", MODE_PRIVATE)
                .edit()
                .putInt("total_step", totalSteps.toInt())
                .apply()

            val estimatedCalories = totalSteps * 0.05f
            tvActiveCalories.text = String.format(Locale.US, "%.1f", estimatedCalories)

            try {

                val heartBeatResponse = healthConnectClient.readRecords(
                    ReadRecordsRequest(
                        HeartRateRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                    )
                )

                val latestHeartBeat = heartBeatResponse.records
                    .flatMap { it.samples }
                    .maxByOrNull { it.time }
                    ?.beatsPerMinute

                tvHeartBeat.text = latestHeartBeat?.toString() ?: "-"

            } catch (e: Exception) {

                tvHeartBeat.text = "-"
            }

        } catch (e: Exception) {
            Log.e("HealthConnect", "Gagal membaca data Health Connect", e)
            Toast.makeText(this, "Gagal membaca data Health Connect", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setRandomTips(totalSteps: Long) {

        val tipsRendah = arrayOf(
            "Cobalah berjalan kaki 10–15 menit untuk meningkatkan aktivitas harian Anda.",
            "Mulailah bergerak lebih sering dengan berdiri setiap 1 jam sekali.",
            "Kurangi waktu duduk terlalu lama dengan berjalan singkat di sekitar Anda."
        )

        val tipsRingan = arrayOf(
            "Aktivitas Anda sudah mulai meningkat, pertahankan konsistensinya.",
            "Tambahkan 500–1000 langkah lagi untuk meningkatkan kebugaran tubuh.",
            "Cobalah berjalan santai setelah makan untuk menambah aktivitas harian."
        )

        val tipsSedang = arrayOf(
            "Aktivitas Anda cukup baik hari ini.",
            "Pertahankan pola aktivitas fisik agar kesehatan tetap terjaga.",
            "Lakukan peregangan ringan setelah beraktivitas."
        )

        val tipsAktif = arrayOf(
            "Bagus! Anda sudah mencapai tingkat aktivitas yang baik.",
            "Tetap jaga hidrasi tubuh selama beraktivitas.",
            "Konsistensi lebih penting daripada intensitas sesaat."
        )

        val tipsTinggi = arrayOf(
            "Luar biasa! Aktivitas fisik Anda sangat baik hari ini.",
            "Jangan lupa beristirahat dan memenuhi kebutuhan cairan tubuh.",
            "Aktivitas yang tinggi membantu menjaga kesehatan jantung."
        )

        val tipsOptimal = arrayOf(
            "Hebat! Anda telah mencapai tingkat aktivitas yang optimal.",
            "Pertahankan gaya hidup aktif ini untuk kesehatan jangka panjang.",
            "Tubuh Anda mendapatkan manfaat besar dari aktivitas hari ini."
        )

        val selectedTip = when {
            totalSteps <= 1000 -> tipsRendah.random()
            totalSteps <= 3000 -> tipsRingan.random()
            totalSteps <= 6000 -> tipsSedang.random()
            totalSteps <= 10000 -> tipsAktif.random()
            totalSteps <= 15000 -> tipsTinggi.random()
            else -> tipsOptimal.random()
        }

        tvTipsAktivitas.text = selectedTip
    }
}
package example.com.fielthyapps.Service

import android.content.Context
import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.Metadata
import java.time.Instant
import java.time.ZoneOffset

class WearDataRepository(
    context: Context
) {
    private val client = HealthConnectClient.getOrCreate(context)

    suspend fun saveHeartRate(heartRate: String) {
       try {
           val time = Instant.parse(heartRate.split("_")[0])
           val value = heartRate.split("_")[1]
           val hrRecord = HeartRateRecord(
               startTime = time.minusSeconds(1),
               endTime = time,
               startZoneOffset = ZoneOffset.UTC,
               endZoneOffset = ZoneOffset.UTC,
               samples = listOf(
                   HeartRateRecord.Sample(
                       time = time, beatsPerMinute = value.toLong(),
                   )
               ),
               metadata = Metadata(
                   clientRecordId = "fielthyapps",
                   clientRecordVersion = System.currentTimeMillis()
               )
           )
           client.insertRecords(listOf(hrRecord))
       } catch (e: Exception) {
           e.printStackTrace()
       }
    }

    suspend fun saveSteps(dailySteps: String) {
        try {
            Log.d("TAG", "saveSteps: $dailySteps")
            val time = Instant.parse(dailySteps.split("_")[0])
            val value = dailySteps.split("_")[1]
            val stepRecord = StepsRecord(
                startTime = time.truncatedTo(java.time.temporal.ChronoUnit.DAYS).atZone(java.time.ZoneOffset.UTC).toInstant(),
                endTime = time,
                startZoneOffset = ZoneOffset.UTC,
                endZoneOffset = ZoneOffset.UTC,
                count = value.toLong(),
                metadata = Metadata(
                    clientRecordId = "fielthyapps",
                    clientRecordVersion = System.currentTimeMillis()
                )
            )
            client.insertRecords(listOf(stepRecord))
            Log.d("TAG", stepRecord.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
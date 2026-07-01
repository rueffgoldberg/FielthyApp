package example.com.fielthyapps.Feature.Physical

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object HealthConnectHelper {

    fun getTodaySteps(
        context: Context,
        onResult: (Int) -> Unit
    ) {

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val status =
                    HealthConnectClient.getSdkStatus(
                        context,
                        "com.google.android.apps.healthdata"
                    )

                if (status != HealthConnectClient.SDK_AVAILABLE) {

                    withContext(Dispatchers.Main) {
                        onResult(0)
                    }

                    return@launch
                }

                val client =
                    HealthConnectClient.getOrCreate(context)

                val grantedPermissions =
                    client.permissionController
                        .getGrantedPermissions()

                val stepPermission =
                    HealthPermission.getReadPermission(
                        StepsRecord::class
                    )

                // Belum ada izin
                if (!grantedPermissions.contains(stepPermission)) {

                    withContext(Dispatchers.Main) {
                        onResult(-1)
                    }

                    return@launch
                }

                val now =
                    ZonedDateTime.now(
                        ZoneId.systemDefault()
                    )

                val startTime =
                    now.truncatedTo(
                        ChronoUnit.DAYS
                    ).toInstant()

                val endTime =
                    now.plusDays(1)
                        .truncatedTo(
                            ChronoUnit.DAYS
                        )
                        .toInstant()

                val response =
                    client.readRecords(
                        ReadRecordsRequest(
                            StepsRecord::class,
                            timeRangeFilter =
                                TimeRangeFilter.between(
                                    startTime,
                                    endTime
                                )
                        )
                    )

                val totalSteps =
                    response.records.sumOf {
                        it.count
                    }.toInt()

                withContext(Dispatchers.Main) {
                    onResult(totalSteps)
                }

            } catch (e: Exception) {

                e.printStackTrace()

                withContext(Dispatchers.Main) {
                    onResult(0)
                }
            }
        }
    }
}
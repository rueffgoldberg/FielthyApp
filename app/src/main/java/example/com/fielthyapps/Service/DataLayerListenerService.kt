package example.com.fielthyapps.Service

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch

class DataLayerListenerService: WearableListenerService() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private lateinit var repository: WearDataRepository

    override fun onCreate() {
        super.onCreate()
        repository = WearDataRepository(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {

        dataEvents.forEach { event ->
            when (event.type) {
                DataEvent.TYPE_CHANGED -> {
                    event.dataItem.run {
                        if (uri.path?.compareTo("/heartrate") == 0) {
                            val heartRate = DataMapItem.fromDataItem(this)
                                .dataMap.getString("heartrate")
                            scope.launch {
                                heartRate?.let { repository.saveHeartRate(it) }
                            }
                        } else if (uri.path?.compareTo("/steps") == 0) {
                            val dailySteps = DataMapItem.fromDataItem(this)
                                .dataMap.getString("steps")
                            Log.d("DataLayerListenerService",
                                "New daily steps value received: $dailySteps")
                            scope.launch {
                                dailySteps?.let { repository.saveSteps(it) }
                            }
                        } else { }
                    }
                }

                DataEvent.TYPE_DELETED -> {
                    // DataItem deleted
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancelChildren()
    }
}
package com.example.sensorcsv

import android.hardware.HardwareBuffer
import android.hardware.Sensor
import android.hardware.SensorDirectChannel
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sensorcsv.ui.theme.SensorCSVTheme
import java.io.File

class MainActivity : ComponentActivity(), SensorEventListener {
	companion object {
		init {
			System.loadLibrary("sensorcsv")
		}
	}
	private external fun startNativeCsvRecording(buffer: HardwareBuffer, path: String)
	private external fun stopNativeCsvRecording()

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var accelState by mutableStateOf(Triple(0f, 0f, 0f))
    private var lastTimestamp by mutableLongStateOf(0)
    private var period = 0L

	// hardware buffer only if supported
	private var directChannel: SensorDirectChannel? = null
	private var hardwareBuffer: HardwareBuffer? = null
	//private var memoryFile: MemoryFile? = null
	private var isRecording by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val isMemoryFileSupported = accelerometer?.isDirectChannelTypeSupported(SensorDirectChannel.TYPE_MEMORY_FILE)
        val isHardwareBufferSupported = accelerometer?.isDirectChannelTypeSupported(SensorDirectChannel.TYPE_HARDWARE_BUFFER)

        setContent {
            SensorCSVTheme() {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Accelerometer (m/s²)")
                    Text("X: ${accelState.first}")
                    Text("Y: ${accelState.second}")
                    Text("Z: ${accelState.third}")
                    Text("hertz: ${1000000000/period}")
                    Text("milliseconds: ${period/1000000}")
                    Text("microseconds: ${period/1000}")
					HorizontalDivider()
                    Text("MemoryFile: $isMemoryFileSupported")
                    Text("HardwareBuffer: $isHardwareBufferSupported")
					HorizontalDivider()
					if(isHardwareBufferSupported == true) {
						Text("CSV Recorder using Hardware Buffer")
						Button( onClick = {
							if (isRecording) {
								stopDirectChannelRecording()
							} else {
								startDirectChannelRecording()
							}
						}) {
							Text(if (isRecording) "Stop Recording" else "Start Recording")
						}
					}
				}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val sensor = accelerometer ?: return
        sensorManager.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )
	}

    override fun onPause() {
        super.onPause()
		sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accelState = Triple(
                event.values[0],
                event.values[1],
                event.values[2]
            )
            period = event.timestamp - lastTimestamp
            lastTimestamp = event.timestamp
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

	private fun startDirectChannelRecording() {
		hardwareBuffer = HardwareBuffer.create(8192, 1, HardwareBuffer.BLOB, 1, HardwareBuffer.USAGE_SENSOR_DIRECT_DATA)
		directChannel = sensorManager.createDirectChannel(hardwareBuffer)
		directChannel!!.configure(
			accelerometer,
			SensorDirectChannel.RATE_VERY_FAST
		)
		val downloadsPath = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.path
		val csvPath = "$downloadsPath/recorded_data.csv"
		Log.d("marco", "csvPath: $csvPath")
		startNativeCsvRecording(hardwareBuffer!!, csvPath)
		isRecording = true
	}

	private fun stopDirectChannelRecording() {
		accelerometer?.let {
			directChannel?.configure(it, SensorDirectChannel.RATE_STOP)
		}
		directChannel?.close()
		hardwareBuffer?.close()
		directChannel = null
		hardwareBuffer = null
		stopNativeCsvRecording()
		isRecording = false
	}
}
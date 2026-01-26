package com.example.sensorcsv

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.sensorcsv.ui.theme.SensorCSVTheme
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var acc by mutableStateOf(Triple(0f, 0f, 0f))
    private var prevTimestamp by mutableLongStateOf(0L)
    private var actualPeriod = 1L

	private var isRecording by mutableStateOf(false)
	private var file: File? = null
	private var writer: BufferedWriter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        setContent {
            SensorCSVTheme() {
				val config = mutableStateOf(InjectionConfiguration())
				MainScreen(
					configuration = config,
            		actualPeriod = actualPeriod,
					startRecording = { start(config.value) },
					stopRecording = { stop() },
					isRecording = isRecording
            	)
            }
        }
    }

	fun start(config: InjectionConfiguration) {
		val date = SimpleDateFormat("MM/dd-HH:mm", Locale.getDefault()).format(Date())

		val sensor = accelerometer ?: return
		sensorManager.registerListener(
			this,
			sensor,
			config.sensorDelay
		)

		val fileName = "${date}_$config.csv"
		file = File(applicationContext.getExternalFilesDir(null), fileName)

		writer = BufferedWriter(FileWriter(file, true))
		writer?.write("# ${config.magnitude}\n")
		writer?.write("# ${config.injectionFrequency}\n")
		writer?.write("# ${config.sensorDelay}\n")
		writer?.write("# ${config.iteration}\n")
		writer?.write("timestamp,ax,ay,az\n")
		isRecording = true
	}

	fun stop() {
		writer?.flush()
		writer?.close()
		isRecording = false
	}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            acc = Triple(
                event.values[0],
                event.values[1],
                event.values[2]
            )
			val ts = currentTimeMillis() + (SystemClock.elapsedRealtimeNanos() - event.timestamp)/1000000
			writer?.write("$ts,${acc.first},${acc.second},${acc.third}\n")

			actualPeriod = event.timestamp - prevTimestamp
			prevTimestamp = event.timestamp
        }
    }

	override fun onResume() {
		super.onResume()
	}

	override fun onPause() {
		super.onPause()
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
package com.example.sensorcsv

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Debug
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
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

    private var prevTimestamp by mutableLongStateOf(0L)
    private var actualPeriod by mutableLongStateOf(0L)

	private var config by mutableStateOf(InjectionConfiguration())
	private var isRecording by mutableStateOf(false)
	private var file: File? = null
	private var writer: FileWriter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
		accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

		//resetRecords(fromA, toB)

		config.iteration = countRecords()

        setContent {
            SensorCSVTheme() {
				MainScreen(
					configuration = config,
            		onChange = {
						config = it
						config.iteration = countRecords()
					},
            		actualPeriod = actualPeriod,
					startRecording = { start() },
					stopRecording = { stop() },
					cancelRecording = { stop(abort = true) },
					isRecording = isRecording
            	)
            }
        }
    }

	fun start() {
		val sensor = accelerometer ?: return
		sensorManager.registerListener(
			this,
			sensor,
			config.sensorDelay
		)

		file = File(applicationContext.getExternalFilesDir(null), config.toFileName())

		file!!.createNewFile()

		writer = FileWriter(file)
		writer?.write("# ${config.magnitude}\n")
		writer?.write("# ${config.injectionFrequency}\n")
		writer?.write("# ${config.sensorDelay}\n")
		writer?.write("# recv\n")
		writer?.write("# ${config.iteration}\n")
		writer?.write("timestamp,ax,ay,az\n")
		isRecording = true
	}

	fun stop(abort: Boolean = false) {
		sensorManager.unregisterListener(this)
		//actualPeriod = 0L

		writer?.flush()
		writer?.close()
		writer = null
		isRecording = false
		if(abort) {
			file?.delete()
		}else{
			config.iteration++
			// upload file to firebase storage?
		}
		file = null
	}

	fun countRecords(): Int {
		val dir = applicationContext.getExternalFilesDir(null) ?: return 0
		val files = dir.listFiles() ?: return 0

		return files.count {
			it.name.startsWith(config.toString())
		}
	}

	fun resetRecords(start: Int, end: Int){
		val dir = applicationContext.getExternalFilesDir(null) ?: return
		val files = dir.listFiles() ?: return


		for (m in Magnitudes) {
			for (f in InjectionFrequencies) {
				for (d in SensorDelays) {
					val tmp = InjectionConfiguration(m,f,d)
					for (file in files){
						for (i in start..end){
							if(file.name.startsWith(
								tmp.toString() + "_" + i + "_"
							)) {
								Log.w("deleted", file.name)
								file.delete()
							}
						}
					}
				}
			}
		}
	}

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {

			val ts = currentTimeMillis() - (SystemClock.elapsedRealtimeNanos() - event.timestamp)/1000000

			writer?.write("$ts,${event.values[0]},${event.values[1]},${event.values[2]}\n")

			actualPeriod = event.timestamp - prevTimestamp
			prevTimestamp = event.timestamp
        }
    }

	override fun onResume() {
		super.onResume()
		Toast.makeText(this, "Application had paused", Toast.LENGTH_LONG).show()
	}

	override fun onPause() {
		super.onPause()
	}

	override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
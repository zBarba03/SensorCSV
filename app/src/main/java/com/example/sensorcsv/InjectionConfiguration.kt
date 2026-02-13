package com.example.sensorcsv

import android.hardware.SensorManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val Magnitudes = listOf("Lower", "Normal", "Higher")
val InjectionFrequencies = listOf(50, 100, 200, 500, 1000, 10000)
val SensorDelays = listOf(SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_FASTEST)
val Origins = listOf("recv", "real")

data class InjectionConfiguration(
	var magnitude: String = Magnitudes[1],
	var injectionFrequency: Int = InjectionFrequencies[2],
	var sensorDelay: Int = SensorDelays[1],
	var origin: String = Origins[0],
	var iteration: Int = 0,
){
	override fun toString(): String {
		val sensorDelayString = if (sensorDelay==SensorManager.SENSOR_DELAY_GAME) "DELAY-GAME" else "DELAY-FASTEST"
		return "${magnitude}_${injectionFrequency}_${sensorDelayString}_${origin}"

		// "recv" => receiving the injection
		// injection script will save files with "send" instead
		// when the test is repeated on a faster computer,
		// these will be changed to "recv1" and "send1"

		// "real" => recording a real walk
		// magnitude = "Walk"
		// injectionFrequency = 0
	}

	fun toFileName(): String {
		val time = SimpleDateFormat("MMMddHHmm", Locale.ENGLISH).format(Date())
		return toString() + "_${iteration}_${time}.csv"
	}
}

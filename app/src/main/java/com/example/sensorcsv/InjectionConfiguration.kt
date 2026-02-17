package com.example.sensorcsv

import android.hardware.SensorManager
import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val Origins = listOf("recv", "real")

val Magnitudes = listOf("Lower", "Normal", "Higher")
val InjectionFrequencies = listOf(50, 100, 200, 500, 1000, 10000)
val SensorDelays = listOf(SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_FASTEST)

val Activities = listOf("Walk", "Run", "Downhill", "Uphill", "Irregular", "Baby")
val Positions = listOf("Shoulder", "Hand", "Pocket")

data class InjectionConfiguration(
	var activity: String = Activities[0],
	var position: String = Positions[0],
	var magnitude: String = Magnitudes[1],
	var injectionFrequency: Int = InjectionFrequencies[2],
	var sensorDelay: Int = SensorDelays[1],
	var origin: String = Origins[0],
	var iteration: Int = 0,
){
	override fun toString(): String {
		val sensorDelayString = if (sensorDelay==SensorManager.SENSOR_DELAY_GAME) "DELAY-GAME" else "DELAY-FASTEST"
		val delayNewString = if (sensorDelay==SensorManager.SENSOR_DELAY_GAME) "Game" else "Fastest"

		return if(origin.startsWith("recv"))
			"${magnitude}_${injectionFrequency}_${sensorDelayString}_${origin}"
		else "${activity}_${position}_${delayNewString}_real${Build.MODEL}"

		// "recv" => receiving the injection
		// injection script will save files with "send" instead
		// when the test is repeated on a faster computer,
		// these will be changed to "recv1" and "send1"

		// "real" => recording a real walk
		// magnitude = "Walk"
		// injectionFrequency = 0
	}

	fun toFileName(): String {
		val date = SimpleDateFormat("MMMdd-HH:mm", Locale.ENGLISH).format(Date())
		return toString() + "_${iteration}_${date}.csv"
	}
}

fun activityToString(act: String): String{
	return when (act) {
		"Walk" -> "Plain walking"
		"Irregular" -> "Irregular steps"
		"Baby" -> "Baby steps"
		else -> act
	}
}

fun positionToString(pos: String): String{
	return pos
	/*
	return when (pos) {
		"Shoulder" -> "Sulle spalle"
		"Hand" -> "In mano"
		"Pocket" -> "In tasca"
		else -> pos
	}
	*/
}

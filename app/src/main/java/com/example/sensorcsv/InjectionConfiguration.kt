package com.example.sensorcsv

import android.hardware.SensorManager

val Magnitudes = listOf("Low", "Normal", "High")
val InjectionFrequencies = listOf(50, 100, 200, 500, 1000)
val SensorDelays = listOf(SensorManager.SENSOR_DELAY_GAME, SensorManager.SENSOR_DELAY_FASTEST)

data class InjectionConfiguration(
	var magnitude: String = Magnitudes[1],
	var injectionFrequency: Int = InjectionFrequencies[2],
	var sensorDelay: Int = SensorDelays[1],
	var iteration: Int = 0
){
	override fun toString(): String {
		val delay = if (sensorDelay==SensorManager.SENSOR_DELAY_GAME) "DELAY-GAME" else "DELAY-FASTEST"
		return "A:${magnitude}_InjFreq:${injectionFrequency}_${delay}_rep:${iteration}"
	}
}

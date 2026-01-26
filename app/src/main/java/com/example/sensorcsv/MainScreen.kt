package com.example.sensorcsv

import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
	configuration: MutableState<InjectionConfiguration>,
	actualPeriod: Long,
	startRecording: () -> Unit,
	stopRecording: () -> Unit,
	isRecording: Boolean
){
	Column(
		modifier = Modifier.fillMaxSize(),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		//Text("Accelerometer (m/s²)")
		//Text("X: ${String.format("%.2f", acc.first)}")
		//Text("Y: ${String.format("%.2f", acc.second)}")
		//Text("Z: ${String.format("%.2f", acc.third)}")

		Text("Injection parameters", style = MaterialTheme.typography.headlineMedium)
		Text("Magnitude of acceleration", style = MaterialTheme.typography.labelLarge)
		ChoiceGroup(
			options = Magnitudes,
			selected = configuration.value.magnitude,
			onClick = {configuration.value.magnitude = it}
		)
		Text("Frequency", style = MaterialTheme.typography.labelLarge)
		ChoiceGroup(
			options = InjectionFrequencies,
			selected = configuration.value.injectionFrequency,
			onClick = {configuration.value.injectionFrequency = it},
			getLabel = { "${it}Hz" }
		)

		HorizontalDivider(modifier = Modifier.padding(16.dp))

		Text("Android parameters", style = MaterialTheme.typography.headlineMedium)
		Text("Sensor Sampling Delay", style = MaterialTheme.typography.labelLarge)
		// - FASTEST 10ms 100hz
		// - GAME 20ms 50hz
		ChoiceGroup(
			options = SensorDelays,
			selected = configuration.value.sensorDelay,
			onClick = {configuration.value.sensorDelay = it},
			getLabel = {
				if (it==SensorManager.SENSOR_DELAY_GAME) "DELAY-GAME"
				else "DELAY-FASTEST"
			}
		)
		Text("current actual frequency: ${1000000000/actualPeriod}", style = MaterialTheme.typography.labelLarge)
		Text("period (ms): ${actualPeriod/1000000}", style = MaterialTheme.typography.labelLarge)

		HorizontalDivider(modifier = Modifier.padding(16.dp))

		Button(onClick={
			if(!isRecording){
				startRecording()
			}else{
				stopRecording()
			}
		}){
			if(isRecording) Text("Stop recording")
			else Text("Start recording")
		}
	}
}
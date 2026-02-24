package com.example.sensorcsv

import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ReportFragment

@Composable
fun MainScreen(
	configuration: InjectionConfiguration,
	onChange: (InjectionConfiguration) -> Unit,
	actualPeriod: Long,
	startRecording: () -> Unit,
	stopRecording: () -> Unit,
	cancelRecording: () -> Unit,
	isRecording: Boolean
){
	Scaffold { contentPadding ->
		Column(
			modifier = Modifier.fillMaxSize().padding(16.dp).padding(contentPadding),
			verticalArrangement = Arrangement.Top,
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			FlowChoiceGroup(
				options = Origins,
				selected = configuration.origin,
				onClick = { onChange(configuration.copy(origin = it)) },
				label = "Usage Mode:",
				getLabel = { if (it.startsWith("recv")) "Injection" else "Walk recording" }
			)
			if(configuration.origin.startsWith("recv")) {
				Text(
					"Injection parameters",
					color = MaterialTheme.colorScheme.onBackground,
					style = MaterialTheme.typography.headlineMedium,
					modifier = Modifier.padding(16.dp)
				)
				FlowChoiceGroup(
					options = Magnitudes,
					selected = configuration.magnitude,
					onClick = { onChange(configuration.copy(magnitude = it)) },
					label = "Magnitude of acceleration:",
					enabled = configuration.origin == "recv"
				)
				FlowChoiceGroup(
					options = InjectionFrequencies,
					selected = configuration.injectionFrequency,
					onClick = { onChange(configuration.copy(injectionFrequency = it)) },
					label = "Frequency:",
					getLabel = { if (it==0) "maxHz" else "${it}Hz" }
				)
			}else if(configuration.origin.startsWith("real")) {
				Text(
					"Walk parameters",
					color = MaterialTheme.colorScheme.onBackground,
					style = MaterialTheme.typography.headlineMedium,
					modifier = Modifier.padding(16.dp)
				)
				FlowChoiceGroup(
					options = Activities,
					selected = configuration.activity,
					onClick = { onChange(configuration.copy(activity = it)) },
					label = "Type of activity:",
					getLabel = { activityToString(it) }
				)
				FlowChoiceGroup(
					options = Positions,
					selected = configuration.position,
					onClick = { onChange(configuration.copy(position = it)) },
					label = "Phone position:",
					getLabel = { positionToString(it) }
				)
			}
			Text(
				"Android parameters",
				style = MaterialTheme.typography.headlineMedium,
				modifier = Modifier.padding(16.dp)
			)
			ChoiceGroup(
				options = SensorDelays,
				selected = configuration.sensorDelay,
				onClick = { onChange(configuration.copy(sensorDelay = it)) },
				label = "Sensor Sampling Delay:",
				getLabel = {
					if (it == SensorManager.SENSOR_DELAY_GAME) "Game"
					else "Fastest"
				}
			)

			Text(
				"Recorded files for this configuration: ${configuration.iteration}",
				modifier = Modifier.padding(16.dp)
			)
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.Start
			) {
				Button(
					modifier = Modifier.fillMaxWidth(0.5f).padding(4.dp),
					onClick = {
						if (!isRecording) startRecording()
						else stopRecording()
					}) {
					if (isRecording) Text("Stop and Save")
					else Text("Start Recording")
				}
				if (isRecording) {
					Button(
						modifier = Modifier.fillMaxWidth().padding(4.dp),
						colors = ButtonDefaults.buttonColors(
							containerColor = MaterialTheme.colorScheme.error,
							contentColor = MaterialTheme.colorScheme.onError
						),
						onClick = { cancelRecording() }
					) {
						Text("Cancel")
					}
				}
			}
			if (isRecording && actualPeriod != 0L) {
				Text(
					"current actual frequency: ${1000000000 / actualPeriod}Hz",
					style = MaterialTheme.typography.labelLarge
				)
				Text(
					"period (ms): ${actualPeriod / 1000000.0}",
					style = MaterialTheme.typography.labelLarge
				)
			}
		}
	}
}
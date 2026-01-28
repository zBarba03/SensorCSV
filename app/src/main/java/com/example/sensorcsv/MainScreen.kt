package com.example.sensorcsv

import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sensorcsv.ui.theme.SensorCSVTheme

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
	Column(
		modifier = Modifier.fillMaxSize().padding(16.dp),
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Text(
			"Injection parameters",
			style = MaterialTheme.typography.headlineMedium,
			modifier = Modifier.padding(16.dp))
		Text("Magnitude of acceleration", style = MaterialTheme.typography.labelLarge)
		FlowChoiceGroup(
			options = Magnitudes,
			selected = configuration.magnitude,
			onClick = { onChange(configuration.copy(magnitude = it)) }
		)
		Text("Frequency", style = MaterialTheme.typography.labelLarge)
		FlowChoiceGroup(
			options = InjectionFrequencies,
			selected = configuration.injectionFrequency,
			onClick = { onChange(configuration.copy(injectionFrequency = it)) },
			getLabel = { "${it}Hz" }
		)

		HorizontalDivider()
		Text(
			"Android parameters",
			style = MaterialTheme.typography.headlineMedium,
			modifier = Modifier.padding(16.dp))
		Text("Sensor Sampling Delay", style = MaterialTheme.typography.labelLarge)
		ChoiceGroup(
			options = SensorDelays,
			selected = configuration.sensorDelay,
			onClick = { onChange(configuration.copy(sensorDelay = it)) },
			getLabel = {
				if (it==SensorManager.SENSOR_DELAY_GAME) "DELAY-GAME"
				else "DELAY-FASTEST"
			}
		)
		HorizontalDivider()

		Text("Recorded files for this configuration: ${configuration.iteration}",
			modifier = Modifier.padding(16.dp))
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
			if( isRecording ){
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
		if(isRecording && actualPeriod!=0L) {
			Text("current actual frequency: ${1000000000 / actualPeriod}Hz",
				style = MaterialTheme.typography.labelLarge)
			Text("period (ms): ${actualPeriod/1000000.0}",
				style = MaterialTheme.typography.labelLarge)
		}
	}
}
package com.example.sensorcsv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun <T> ChoiceGroup(
	options: List<T>,
	selected: T,
	onClick: (T) -> Unit,
	label: String,
	getLabel: (T) -> String = { it.toString() },
	enabled: Boolean = true
){
	Text(label, style = MaterialTheme.typography.labelLarge)
	SingleChoiceSegmentedButtonRow(
		modifier = Modifier.fillMaxWidth()
	) {
		options.forEach {
			SegmentedButton(
				selected = it == selected,
				onClick = { onClick(it) },
				shape = SegmentedButtonDefaults.itemShape(
					index = options.indexOf(it),
					count = options.size
				),
				label = { Text(getLabel(it)) },
				enabled = enabled
			)
		}
	}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun <T> FlowChoiceGroup(
	options: List<T>,
	selected: T,
	onClick: (T) -> Unit,
	label: String,
	getLabel: (T) -> String = { it.toString() },
	enabled: Boolean = true,
){
	ElevatedCard(
		modifier = Modifier.padding(vertical = 8.dp)
	) {
		Text(
			modifier = Modifier.padding(8.dp, bottom = 0.dp),
			text = label,
			style = MaterialTheme.typography.labelLarge
		)
		FlowRow(
			modifier = Modifier.fillMaxWidth().padding(8.dp, top=0.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp),
			maxItemsInEachRow = 4
		) {
			options.forEach {
				FilterChip(
					selected = it == selected,
					onClick = { onClick(it) },
					label = { Text(getLabel(it)) },
					enabled = enabled
				)
			}
		}
	}
}

@Preview
@Composable
fun Preview(){

	Surface {
		MainScreen(
			configuration = InjectionConfiguration(origin = Origins[0]),
			onChange = {},
			actualPeriod = 20000000L,
			startRecording = {},
			stopRecording = {},
			cancelRecording = {},
			isRecording = false
		)
	}
}
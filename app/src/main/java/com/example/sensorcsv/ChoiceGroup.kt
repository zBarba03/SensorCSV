package com.example.sensorcsv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
	getLabel: (T) -> String = { it.toString() }
){
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
				label = { Text(getLabel(it)) }
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
	getLabel: (T) -> String = { it.toString() }
){
	FlowRow(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(8.dp),
		maxItemsInEachRow = 4
	){
		options.forEach {
			FilterChip(
				selected = it == selected,
				onClick = { onClick(it) },
				label = { Text(getLabel(it)) }
			)
		}
	}
}

@Preview
@Composable
fun preview(){

	Surface {
		MainScreen(
			configuration = InjectionConfiguration(),
			onChange = {},
			actualPeriod = 20000000L,
			startRecording = {},
			stopRecording = {},
			cancelRecording = {},
			isRecording = false
		)
	}
}
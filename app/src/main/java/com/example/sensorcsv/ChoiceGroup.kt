package com.example.sensorcsv

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

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
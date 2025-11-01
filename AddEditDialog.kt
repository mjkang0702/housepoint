package com.example.accordionapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.accordionapp.model.AccordionItem
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction

// Update your AddEditDialog component
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDialog(
    item: AccordionItem? = null,
    onDismiss: () -> Unit,
    onSave: (String, String, Double) -> Unit // Updated to include numerical value
) {
    var title by remember(item) { mutableStateOf(item?.title ?: "") }
    var content by remember(item) { mutableStateOf(item?.content ?: "") }
    var numericalValue by remember(item) {
        mutableStateOf(
            when {
                item?.numericalValue == null -> ""
                item.numericalValue == 0.0 -> ""
                else -> item.numericalValue.toString()
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (item == null) "Add New Item" else "Edit Item")
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = numericalValue,
                    onValueChange = { newValue ->
                        // Only allow valid number input
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            numericalValue = newValue
                        }
                    },
                    label = { Text("Point Value") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    placeholder = { Text("0") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val numValue = if (numericalValue.isBlank()) {
                        0.0
                    } else {
                        numericalValue.toDoubleOrNull() ?: 0.0
                    }
                    onSave(title.trim(), content.trim(), numValue)
                    onDismiss()
                },
                enabled = title.isNotBlank()
            ) {
                Text(if (item == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

package com.example.growpath.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun NoteDialog(
    noteContent: String,
    onNoteContentChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismissClick) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Add Note",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = noteContent,
                    onValueChange = onNoteContentChanged,
                    label = { Text("Note content") },
                    placeholder = { Text("Write your note...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissClick) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onSaveClick,
                        enabled = noteContent.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

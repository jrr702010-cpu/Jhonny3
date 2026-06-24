package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var silentNotifications by remember { mutableStateOf(false) }
    var vibrationEnabled by remember { mutableStateOf(true) }
    var randomMode by remember { mutableStateOf(true) } // true for random, false for custom

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SettingsSectionTitle(title = "PREFERENCIAS DE LA APP", icon = Icons.Default.Palette)
            SettingsCard {
                SettingsSwitchRow(
                    title = "Tema Oscuro",
                    checked = true, // Hook to viewmodel
                    onCheckedChange = {}
                )
            }
        }

        item {
            SettingsSectionTitle(title = "SINCRONIZACIÓN DE DATOS", icon = Icons.Default.Sync)
            SettingsCard {
                SettingsSliderRow(
                    title = "Frecuencia de actualización",
                    value = 15f,
                    valueRange = 15f..120f,
                    onValueChange = {},
                    label = "15 min"
                )
            }
        }

        item {
            SettingsSectionTitle(title = "ALERTAS Y NOTIFICACIONES", icon = Icons.Default.Notifications)
            SettingsCard {
                SettingsSwitchRow(
                    title = "Cualquier Cambio de Valor",
                    checked = true,
                    onCheckedChange = {}
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingsSwitchRow(
                    title = "Fluctuaciones Fuertes (> 1%)",
                    checked = false,
                    onCheckedChange = {}
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                SettingsSwitchRow(
                    title = "Alertas del Euro",
                    checked = true,
                    onCheckedChange = {}
                )
            }
        }

        item {
            SettingsCard {
                SettingsSwitchRow(
                    title = "Notificaciones Silenciosas",
                    checked = silentNotifications,
                    onCheckedChange = { silentNotifications = it }
                )
                AnimatedVisibility(visible = !silentNotifications) {
                    Column {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        SettingsSwitchRow(
                            title = "Vibración Habilitada",
                            checked = vibrationEnabled,
                            onCheckedChange = { vibrationEnabled = it }
                        )
                    }
                }
            }
        }

        item {
            SettingsCard {
                Text(
                    text = "Reportes y Entregas Programadas",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    SegmentedButton(
                        selected = randomMode,
                        onClick = { randomMode = true },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("Modo Aleatorio")
                    }
                    SegmentedButton(
                        selected = !randomMode,
                        onClick = { randomMode = false },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("Modo Personalizado")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (randomMode) {
                    SettingsSliderRow(
                        title = "Alertas diarias (8:00 AM - 9:00 PM)",
                        value = 3f,
                        valueRange = 1f..10f,
                        steps = 8,
                        onValueChange = {},
                        label = "3 alertas"
                    )
                } else {
                    CustomTimePickerSection()
                }
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsSwitchRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsSliderRow(title: String, value: Float, valueRange: ClosedFloatingPointRange<Float>, steps: Int = 0, onValueChange: (Float) -> Unit, label: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
fun CustomTimePickerSection() {
    var customTimes by remember { mutableStateOf(listOf("09:00", "14:30")) }
    var hour by remember { mutableStateOf(12) }
    var minute by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            TimeSpinner(value = hour, onIncrease = { hour = (hour + 1) % 24 }, onDecrease = { hour = (hour - 1 + 24) % 24 })
            Text(":", style = MaterialTheme.typography.headlineMedium)
            TimeSpinner(value = minute, onIncrease = { minute = (minute + 15) % 60 }, onDecrease = { minute = (minute - 15 + 60) % 60 })
            
            IconButton(onClick = { 
                val newTime = String.format("%02d:%02d", hour, minute)
                if (!customTimes.contains(newTime)) customTimes = customTimes + newTime 
            }) {
                Icon(Icons.Default.AddCircle, contentDescription = "Añadir hora", tint = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        customTimes.sorted().forEach { time ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = time, style = MaterialTheme.typography.bodyLarge)
                IconButton(onClick = { customTimes = customTimes.filter { it != time } }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun TimeSpinner(value: Int, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onIncrease) { Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Subir") }
        Text(text = String.format("%02d", value), style = MaterialTheme.typography.headlineSmall)
        IconButton(onClick = onDecrease) { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Bajar") }
    }
}

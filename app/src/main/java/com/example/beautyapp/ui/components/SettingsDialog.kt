package com.example.beautyapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beautyapp.data.ColorBlindMode
import com.example.beautyapp.data.FontSize
import com.example.beautyapp.data.Settings

// Main settings dialog that shows all accessibility options
// onDismiss: called when user clicks outside dialog or cancel button
// settings: current settings from ViewModel
// onUpdateDarkMode, onUpdateFontSize, etc: callbacks to save changes
@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    settings: Settings,
    onUpdateDarkMode: (Boolean) -> Unit,
    onUpdateFontSize: (FontSize) -> Unit,
    onUpdateColorBlindMode: (ColorBlindMode) -> Unit,
    onUpdateReduceAnimations: (Boolean) -> Unit
) {
    // State for showing/hiding the font size dropdown menu
    var fontSizeExpanded by remember { mutableStateOf(false) }

    // State for showing/hiding the color blind mode dropdown menu
    var colorBlindExpanded by remember { mutableStateOf(false) }

    // AlertDialog provides the popup window with dismiss behavior
    AlertDialog(
        onDismissRequest = onDismiss,  // Called when user taps outside dialog
        title = {
            // Dialog header with settings icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color(0xFFF472B6)  // Pink color matching app theme
                )
                Text(
                    text = "Accessibility Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            // Main content area with all settings options
            // verticalScroll allows scrolling if content is too tall
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // SETTING 1: Dark Mode Toggle
                // Shows a switch to toggle between light and dark theme
                SettingItem(
                    icon = if (settings.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    title = "Theme",
                    description = if (settings.isDarkMode) "Dark mode" else "Light mode"
                ) {
                    // Switch component - changes color when toggled
                    Switch(
                        checked = settings.isDarkMode,  // Current state from settings
                        onCheckedChange = onUpdateDarkMode,  // Save when user toggles
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFF472B6),  // Pink when ON
                            checkedTrackColor = Color(0xFFF472B6).copy(alpha = 0.5f)  // Light pink track when ON
                        )
                    )
                }

                Divider()  // Visual separator between settings

                // SETTING 2: Font Size Dropdown
                // Shows current font size and opens menu to change it
                SettingItem(
                    icon = Icons.Default.FormatSize,
                    title = "Font Size",
                    description = settings.fontSize.name.lowercase().replaceFirstChar { it.uppercase() }  // "MEDIUM" -> "Medium"
                ) {
                    // Box container for the dropdown button
                    Box {
                        // Button that shows current selection and opens dropdown
                        TextButton(onClick = { fontSizeExpanded = true }) {
                            Text(
                                text = settings.fontSize.name,  // Show "SMALL", "MEDIUM", or "LARGE"
                                color = Color(0xFFF472B6)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand",
                                tint = Color(0xFFF472B6)
                            )
                        }

                        // Dropdown menu with all font size options
                        DropdownMenu(
                            expanded = fontSizeExpanded,  // Show/hide based on state
                            onDismissRequest = { fontSizeExpanded = false }  // Close when user taps outside
                        ) {
                            // Create a menu item for each font size option
                            FontSize.values().forEach { fontSize ->
                                DropdownMenuItem(
                                    text = { Text(fontSize.name) },  // Display name
                                    onClick = {
                                        onUpdateFontSize(fontSize)  // Save selection
                                        fontSizeExpanded = false  // Close menu
                                    },
                                    // Show checkmark next to currently selected size
                                    trailingIcon = if (settings.fontSize == fontSize) {
                                        { Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFF472B6)) }
                                    } else null
                                )
                            }
                        }
                    }
                }

                Divider()  // Visual separator

                // SETTING 3: Color Blind Mode Dropdown
                // Helps users with color vision deficiencies see makeup colors better
                SettingItem(
                    icon = Icons.Default.Palette,
                    title = "Color Blind Mode",
                    description = when (settings.colorBlindMode) {
                        ColorBlindMode.NONE -> "None"
                        ColorBlindMode.PROTANOPIA -> "Protanopia (Red-weak)"
                        ColorBlindMode.DEUTERANOPIA -> "Deuteranopia (Green-weak)"
                    }
                ) {
                    Box {
                        // Button to open color blind mode dropdown
                        TextButton(onClick = { colorBlindExpanded = true }) {
                            Text(
                                text = settings.colorBlindMode.name,
                                color = Color(0xFFF472B6)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Expand",
                                tint = Color(0xFFF472B6)
                            )
                        }

                        // Dropdown menu with all color blind mode options
                        DropdownMenu(
                            expanded = colorBlindExpanded,
                            onDismissRequest = { colorBlindExpanded = false }
                        ) {
                            // Create menu item for each mode
                            ColorBlindMode.values().forEach { mode ->
                                DropdownMenuItem(
                                    text = {
                                        // Show user-friendly description
                                        Column {
                                            Text(mode.name)
                                            if (mode != ColorBlindMode.NONE) {
                                                Text(
                                                    text = when (mode) {
                                                        ColorBlindMode.PROTANOPIA -> "Red-weak"
                                                        ColorBlindMode.DEUTERANOPIA -> "Green-weak"
                                                        else -> ""
                                                    },
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        onUpdateColorBlindMode(mode)  // Save selection
                                        colorBlindExpanded = false  // Close menu
                                    },
                                    // Checkmark on selected mode
                                    trailingIcon = if (settings.colorBlindMode == mode) {
                                        { Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFF472B6)) }
                                    } else null
                                )
                            }
                        }
                    }
                }

                Divider()  // Visual separator

                // SETTING 4: Reduce Animations Toggle
                // Disables animations for users sensitive to motion
                SettingItem(
                    icon = Icons.Default.Animation,
                    title = "Reduce Animations",
                    description = if (settings.reduceAnimations) "Animations disabled" else "Animations enabled"
                ) {
                    Switch(
                        checked = settings.reduceAnimations,
                        onCheckedChange = onUpdateReduceAnimations,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFFF472B6),
                            checkedTrackColor = Color(0xFFF472B6).copy(alpha = 0.5f)
                        )
                    )
                }
            }
        },
        confirmButton = {
            // Close button at bottom of dialog
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFF472B6)
                )
            ) {
                Text("Close")
            }
        }
    )
}

// Reusable component for each setting row
// Displays icon, title, description, and action (switch/dropdown)
@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,  // Icon to show on left
    title: String,  // Main setting name
    description: String,  // Current value or explanation
    action: @Composable () -> Unit  // Switch or dropdown on the right
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,  // Space between left content and right action
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: Icon and text
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)  // Take up remaining space
        ) {
            // Setting icon (e.g., moon for dark mode)
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFF6B7280),  // Gray color
                modifier = Modifier.size(24.dp)
            )

            // Setting title and description
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        // Right side: The action component (switch or dropdown button)
        action()
    }
}
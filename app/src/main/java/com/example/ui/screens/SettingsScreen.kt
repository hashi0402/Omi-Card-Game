package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.UserPreferences
import com.example.ui.components.CarvedWoodPlaque
import com.example.ui.components.JungleBackdrop
import com.example.ui.theme.OmiColors

@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    onPreferencesChange: (UserPreferences) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var language by remember { mutableStateOf(preferences.language) }
    var musicEnabled by remember { mutableStateOf(preferences.musicEnabled) }
    var musicVolume by remember { mutableStateOf(preferences.musicVolume) }
    var soundEnabled by remember { mutableStateOf(preferences.soundEnabled) }
    var soundVolume by remember { mutableStateOf(preferences.soundVolume) }
    var parallaxLevel by remember { mutableStateOf(preferences.parallaxLevel) }
    var hapticsEnabled by remember { mutableStateOf(preferences.hapticsEnabled) }
    var hapticsLevel by remember { mutableStateOf(preferences.hapticsLevel) }
    var undoWindowSeconds by remember { mutableStateOf(preferences.undoWindowSeconds) }
    var selectedFeltIndex by remember { mutableStateOf(preferences.selectedTableFeltIndex) }

    fun notifyChange() {
        onPreferencesChange(
            preferences.copy(
                language = language,
                musicEnabled = musicEnabled,
                musicVolume = musicVolume,
                soundEnabled = soundEnabled,
                soundVolume = soundVolume,
                parallaxLevel = parallaxLevel,
                hapticsEnabled = hapticsEnabled,
                hapticsLevel = hapticsLevel,
                undoWindowSeconds = undoWindowSeconds,
                selectedTableFeltIndex = selectedFeltIndex
            )
        )
    }

    val isSi = language == Language.SINHALA

    JungleBackdrop(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            CarvedWoodPlaque(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("settings_plaque")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header with Title & Close button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSi) "භාෂාව සහ සැකසුම් (Language)" else "Language",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF45220C),
                            fontFamily = FontFamily.Serif
                        )

                        // Close "X" Button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFF5D2E14))
                                .clickable {
                                    notifyChange()
                                    onClose()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✕",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Language Selector Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Language Dropdown Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFF7ED))
                                .border(1.5.dp, Color(0xFF9E6534), RoundedCornerShape(12.dp))
                                .clickable {
                                    language = if (language == Language.ENGLISH) Language.SINHALA else Language.ENGLISH
                                    notifyChange()
                                }
                                .padding(horizontal = 14.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (language == Language.ENGLISH) "English" else "සිංහල",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E1F0B)
                                )
                                Text(text = "▼", fontSize = 12.sp, color = Color(0xFF5D2E14))
                            }
                        }

                        // Badge EN / SI
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2E6B47))
                                .border(1.5.dp, Color(0xFF6B9F7D), RoundedCornerShape(12.dp))
                                .clickable {
                                    language = if (language == Language.ENGLISH) Language.SINHALA else Language.ENGLISH
                                    notifyChange()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (language == Language.ENGLISH) "EN" else "SI",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Music Control Row
                    SettingsPillSliderRow(
                        iconEmoji = "🎵",
                        label = if (isSi) "සංගීතය" else "Music",
                        isEnabled = musicEnabled,
                        onToggle = {
                            musicEnabled = !musicEnabled
                            notifyChange()
                        },
                        sliderValue = musicVolume,
                        onSliderChange = {
                            musicVolume = it
                            notifyChange()
                        },
                        displayPercent = "${(musicVolume * 100).toInt()}%"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sound Control Row
                    SettingsPillSliderRow(
                        iconEmoji = "🔔",
                        label = if (isSi) "ශබ්ද" else "Sound",
                        isEnabled = soundEnabled,
                        onToggle = {
                            soundEnabled = !soundEnabled
                            notifyChange()
                        },
                        sliderValue = soundVolume,
                        onSliderChange = {
                            soundVolume = it
                            notifyChange()
                        },
                        displayPercent = "${(soundVolume * 100).toInt()}%"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Parallax Slider Row
                    SettingsSliderOnlyRow(
                        iconEmoji = "🌿",
                        label = if (isSi) "පැරලැක්ස්" else "Parallax",
                        sliderValue = parallaxLevel,
                        onSliderChange = {
                            parallaxLevel = it
                            notifyChange()
                        },
                        displayPercent = "${(parallaxLevel * 100).toInt()}%"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Haptics Control Row
                    SettingsPillSliderRow(
                        iconEmoji = "📳",
                        label = if (isSi) "කම්පන" else "Haptics",
                        isEnabled = hapticsEnabled,
                        onToggle = {
                            hapticsEnabled = !hapticsEnabled
                            notifyChange()
                        },
                        sliderValue = hapticsLevel,
                        onSliderChange = {
                            hapticsLevel = it
                            notifyChange()
                        },
                        displayPercent = "${(hapticsLevel * 100).toInt()}%"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Undo Window Row
                    SettingsSliderOnlyRow(
                        iconEmoji = "↩",
                        label = if (isSi) "ආපසු හැරවීම" else "Undo window",
                        sliderValue = undoWindowSeconds / 30f,
                        onSliderChange = {
                            undoWindowSeconds = (it * 30).toInt().coerceIn(3, 30)
                            notifyChange()
                        },
                        displayPercent = "${undoWindowSeconds}s"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Data Sync Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .shadow(3.dp, RoundedCornerShape(14.dp))
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE8BA82))
                            .border(1.dp, Color(0xFFB07A4B), RoundedCornerShape(14.dp))
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "☁", fontSize = 16.sp, color = Color(0xFF45220C))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isSi) "දත්ත සමමුහුර්තය" else "Data sync",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF45220C)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Sync icon button
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF3E0))
                                    .clickable { notifyChange() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "⟳", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }

                            // 5m interval chip
                            Box(
                                modifier = Modifier
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFF3E0))
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "5m ▼",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3E1F0B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Table Felt Customization Row
                    Text(
                        text = if (isSi) "මේසය සකසන්න · Table felt" else "Customize · Table felt",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF45220C)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val feltColors = listOf(
                            Color(0xFF1E5E3A), // Green
                            Color(0xFF1A385E), // Blue
                            Color(0xFF5A1C29), // Wine
                            Color(0xFF55351A), // Teak
                            Color(0xFF6B5824)  // Gold
                        )

                        feltColors.forEachIndexed { index, color ->
                            val isSelected = selectedFeltIndex == index
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .shadow(4.dp, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color(0xFFFFD700) else Color(0x66FFFFFF),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedFeltIndex = index
                                        notifyChange()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Text(text = "✓", color = Color.White, fontWeight = FontWeight.Bold)
                                } else if (index > 2) {
                                    Text(text = "🔒", fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsPillSliderRow(
    iconEmoji: String,
    label: String,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    displayPercent: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(3.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE8BA82))
            .border(1.dp, Color(0xFFB07A4B), RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Icon + Label + Switch
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(0.48f)
        ) {
            Text(text = iconEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF45220C)
            )
            Spacer(modifier = Modifier.weight(1f))
            // Pill Switch
            Box(
                modifier = Modifier
                    .width(42.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isEnabled) Color(0xFF2E7D32) else Color(0xFF5D3921))
                    .clickable { onToggle() }
                    .padding(2.dp),
                contentAlignment = if (isEnabled) Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF8E7))
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Right: Slider + Percentage text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(0.52f)
        ) {
            Slider(
                value = sliderValue,
                onValueChange = onSliderChange,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFFF3D6),
                    activeTrackColor = Color(0xFF43A047),
                    inactiveTrackColor = Color(0xFF8D6846)
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = displayPercent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF45220C),
                modifier = Modifier.width(36.dp)
            )
        }
    }
}

@Composable
private fun SettingsSliderOnlyRow(
    iconEmoji: String,
    label: String,
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    displayPercent: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(3.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE8BA82))
            .border(1.dp, Color(0xFFB07A4B), RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(0.48f)
        ) {
            Text(text = iconEmoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF45220C)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(0.52f)
        ) {
            Slider(
                value = sliderValue,
                onValueChange = onSliderChange,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFFFF3D6),
                    activeTrackColor = Color(0xFF43A047),
                    inactiveTrackColor = Color(0xFF8D6846)
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = displayPercent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF45220C),
                modifier = Modifier.width(36.dp)
            )
        }
    }
}

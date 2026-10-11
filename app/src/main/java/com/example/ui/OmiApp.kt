package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.db.OmiDatabase
import com.example.data.repository.OmiRuleRepository
import com.example.model.GamePhase
import com.example.model.UserPreferences
import com.example.ui.screens.GameTableScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.MultiplayerScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RulesScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen

@Composable
fun OmiApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val database = remember { OmiDatabase.getDatabase(context) }
    val ruleRepository = remember { OmiRuleRepository(database.omiRuleDao()) }

    var currentScreen by remember { mutableStateOf(GamePhase.SPLASH) }
    var preferences by remember { mutableStateOf(UserPreferences()) }

    // Ensure Room database is seeded with official Omi rules
    LaunchedEffect(Unit) {
        ruleRepository.ensureDefaultRulesPopulated()
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            GamePhase.SPLASH -> {
                SplashScreen(
                    onLoadingComplete = {
                        currentScreen = GamePhase.MAIN_MENU
                    }
                )
            }

            GamePhase.MAIN_MENU -> {
                MainMenuScreen(
                    preferences = preferences,
                    onPlayClick = {
                        currentScreen = GamePhase.PLAYING_CARDS
                    },
                    onMultiplayerClick = {
                        currentScreen = GamePhase.MULTIPLAYER_LOBBY
                    },
                    onInviteFriendsClick = {
                        currentScreen = GamePhase.PRIVATE_ROOM
                    },
                    onTournamentsClick = {
                        currentScreen = GamePhase.MULTIPLAYER_LOBBY
                    },
                    onSettingsClick = {
                        currentScreen = GamePhase.SETTINGS
                    },
                    onProfileClick = {
                        currentScreen = GamePhase.PROFILE
                    },
                    onRulesClick = {
                        currentScreen = GamePhase.RULES
                    }
                )
            }

            GamePhase.PROFILE -> {
                BackHandler {
                    currentScreen = GamePhase.MAIN_MENU
                }
                ProfileScreen(
                    preferences = preferences,
                    onBack = {
                        currentScreen = GamePhase.MAIN_MENU
                    },
                    onViewRules = {
                        currentScreen = GamePhase.RULES
                    }
                )
            }

            GamePhase.RULES -> {
                BackHandler {
                    currentScreen = GamePhase.MAIN_MENU
                }
                RulesScreen(
                    rulesFlow = ruleRepository.allRules,
                    preferences = preferences,
                    onBack = {
                        currentScreen = GamePhase.MAIN_MENU
                    }
                )
            }

            GamePhase.SETTINGS -> {
                BackHandler {
                    currentScreen = GamePhase.MAIN_MENU
                }
                SettingsScreen(
                    preferences = preferences,
                    onPreferencesChange = { updated ->
                        preferences = updated
                    },
                    onClose = {
                        currentScreen = GamePhase.MAIN_MENU
                    }
                )
            }

            GamePhase.MULTIPLAYER_LOBBY -> {
                BackHandler {
                    currentScreen = GamePhase.MAIN_MENU
                }
                MultiplayerScreen(
                    preferences = preferences,
                    isInviteFriendsMode = false,
                    onStartGame = {
                        currentScreen = GamePhase.PLAYING_CARDS
                    },
                    onBack = {
                        currentScreen = GamePhase.MAIN_MENU
                    }
                )
            }

            GamePhase.PRIVATE_ROOM -> {
                BackHandler {
                    currentScreen = GamePhase.MAIN_MENU
                }
                MultiplayerScreen(
                    preferences = preferences,
                    isInviteFriendsMode = true,
                    onStartGame = {
                        currentScreen = GamePhase.PLAYING_CARDS
                    },
                    onBack = {
                        currentScreen = GamePhase.MAIN_MENU
                    }
                )
            }

            GamePhase.PLAYING_CARDS, GamePhase.TRUMP_SELECTION, GamePhase.ROUND_SUMMARY -> {
                BackHandler {
                    currentScreen = GamePhase.MAIN_MENU
                }
                GameTableScreen(
                    preferences = preferences,
                    onExitGame = {
                        currentScreen = GamePhase.MAIN_MENU
                    }
                )
            }
        }
    }
}

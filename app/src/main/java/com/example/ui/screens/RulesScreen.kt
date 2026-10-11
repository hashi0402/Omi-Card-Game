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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.db.OmiRuleEntity
import com.example.model.Language
import com.example.model.UserPreferences
import com.example.ui.components.CarvedWoodPlaque
import com.example.ui.components.JungleBackdrop
import kotlinx.coroutines.flow.Flow

@Composable
fun RulesScreen(
    rulesFlow: Flow<List<OmiRuleEntity>>,
    preferences: UserPreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSi = preferences.language == Language.SINHALA
    val rules by rulesFlow.collectAsState(initial = emptyList())

    JungleBackdrop(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .testTag("btn_rules_back")
                        .size(40.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(Color(0xFF5D2E14))
                        .border(1.5.dp, Color(0xFFC7985D), CircleShape)
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "←", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = if (isSi) "නිල ඔමි නීති පද්ධතිය" else "Official Omi Rules & Scoring",
                    color = Color(0xFFFFF2D6),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Serif
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2E6B47)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📜", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            CarvedWoodPlaque(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(rules) { rule ->
                        RuleCard(rule = rule, isSi = isSi)
                    }
                }
            }
        }
    }
}

@Composable
private fun RuleCard(rule: OmiRuleEntity, isSi: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFFDF8))
            .border(1.5.dp, Color(0xFFDEC49D), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSi) rule.titleSi else rule.titleEn,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF381D06),
                    fontFamily = FontFamily.Serif
                )

                if (rule.tokenValue > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFFD54F))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "+${rule.tokenValue} Tokens",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF5D3600)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isSi) rule.summarySi else rule.summaryEn,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF8C5C00)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isSi) rule.detailsSi else rule.detailsEn,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = Color(0xFF42382F)
            )
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.OmiColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * Multi-layer 3D Tropical Jungle Backdrop with animated drifting fireflies,
 * god-ray sunlight beams, and 3D ancient carved stone pillars.
 */
@Composable
fun JungleBackdrop(
    modifier: Modifier = Modifier,
    parallaxFactor: Float = 0.1f,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "jungle_dynamic")

    // Leaf sway
    val leafSway by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "leaf_sway"
    )

    // Sunbeam intensity pulse
    val sunbeamPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sunbeam"
    )

    // Fireflies floating motion
    val fireflyMotion by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f, // 2*PI
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "firefly"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Deep tropical atmospheric background
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        OmiColors.JungleDark,
                        OmiColors.JungleMedium,
                        Color(0xFF1E5230),
                        OmiColors.JungleDark
                    )
                )
            )

            // 2. Dynamic 3D Sunbeams from jungle canopy
            val rayAlpha = 0.14f * sunbeamPulse
            val sunRayPath1 = Path().apply {
                moveTo(w * 0.40f, 0f)
                lineTo(w * 0.08f, h)
                lineTo(w * 0.28f, h)
                lineTo(w * 0.52f, 0f)
                close()
            }
            drawPath(sunRayPath1, color = Color(0xFFFFE082).copy(alpha = rayAlpha))

            val sunRayPath2 = Path().apply {
                moveTo(w * 0.54f, 0f)
                lineTo(w * 0.68f, h)
                lineTo(w * 0.92f, h)
                lineTo(w * 0.62f, 0f)
                close()
            }
            drawPath(sunRayPath2, color = Color(0xFFFFE082).copy(alpha = rayAlpha))

            // 3. 3D Ancient Carved Stone Pillars on sides
            val pillarW = w * 0.18f

            // Left Pillar (3D multi-tone bevel)
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF263029), Color(0xFF5D7063), Color(0xFF323F36)),
                    startX = 0f,
                    endX = pillarW
                ),
                topLeft = Offset(0f, 0f),
                size = Size(pillarW, h)
            )
            // Left Pillar 3D Highlights and Capital trims
            drawRect(
                color = Color(0xFF758B7C),
                topLeft = Offset(0f, h * 0.14f),
                size = Size(pillarW * 1.12f, 18f)
            )
            drawRect(
                color = Color(0xFF1C241E),
                topLeft = Offset(0f, h * 0.14f + 18f),
                size = Size(pillarW * 1.12f, 4f)
            )
            drawRect(
                color = Color(0xFF758B7C),
                topLeft = Offset(0f, h * 0.86f),
                size = Size(pillarW * 1.12f, 22f)
            )

            // Right Pillar (3D multi-tone bevel)
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF323F36), Color(0xFF5D7063), Color(0xFF263029)),
                    startX = w - pillarW,
                    endX = w
                ),
                topLeft = Offset(w - pillarW, 0f),
                size = Size(pillarW, h)
            )
            // Right Pillar 3D Highlights
            drawRect(
                color = Color(0xFF758B7C),
                topLeft = Offset(w - pillarW * 1.12f, h * 0.14f),
                size = Size(pillarW * 1.12f, 18f)
            )
            drawRect(
                color = Color(0xFF1C241E),
                topLeft = Offset(w - pillarW * 1.12f, h * 0.14f + 18f),
                size = Size(pillarW * 1.12f, 4f)
            )
            drawRect(
                color = Color(0xFF758B7C),
                topLeft = Offset(w - pillarW * 1.12f, h * 0.86f),
                size = Size(pillarW * 1.12f, 22f)
            )

            // 4. Floating 3D Fireflies / Golden Jungle Spores
            val fireflyPoints = listOf(
                Pair(0.25f, 0.35f),
                Pair(0.70f, 0.28f),
                Pair(0.40f, 0.55f),
                Pair(0.82f, 0.65f),
                Pair(0.18f, 0.75f),
                Pair(0.55f, 0.82f)
            )
            fireflyPoints.forEachIndexed { i, p ->
                val fx = (p.first * w) + sin(fireflyMotion + i) * 24f
                val fy = (p.second * h) + cos(fireflyMotion + i * 1.5f) * 18f
                val glow = (sin(fireflyMotion * 2f + i) + 1f) * 0.5f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFEE58).copy(alpha = 0.8f * glow),
                            Color(0x33FFB300),
                            Color.Transparent
                        ),
                        center = Offset(fx, fy),
                        radius = 16f
                    ),
                    radius = 16f,
                    center = Offset(fx, fy)
                )
            }

            // 5. Swaying Top Canopy Foliage
            val canopyBrush = Brush.radialGradient(
                colors = listOf(Color(0xFF3E9B50), Color(0xFF165228), Color(0xFF0C2C15)),
                center = Offset(w * 0.5f, 0f),
                radius = w * 0.65f
            )
            drawCircle(brush = canopyBrush, radius = w * 0.52f, center = Offset(w * 0.22f, -h * 0.06f + leafSway))
            drawCircle(brush = canopyBrush, radius = w * 0.58f, center = Offset(w * 0.78f, -h * 0.07f - leafSway))
        }

        content()
    }
}

/**
 * 3D Carved Wood Plaque with dual bevels, metallic gold bolts, and realistic wood grain.
 */
@Composable
fun CarvedWoodPlaque(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(20.dp, RoundedCornerShape(cornerRadius), spotColor = Color(0x88000000))
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        OmiColors.WoodPlaqueLight,
                        OmiColors.WoodPlaqueBase,
                        OmiColors.WoodPlaqueDark
                    )
                )
            )
            // 3D Carved border bevel (top-left highlight, bottom-right shadow)
            .border(
                width = 3.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFDE6C2), Color(0xFF75451D), Color(0xFF381D06)),
                    start = Offset(0f, 0f),
                    end = Offset(400f, 600f)
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height

            // 3D Inset bevel line
            drawRoundRect(
                color = Color(0x24000000),
                topLeft = Offset(8f, 8f),
                size = Size(w - 16f, h - 16f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(40f, 40f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
            )

            // Wood Grain subtle grooves
            for (i in 1..8) {
                val y = h * (i / 9f)
                drawLine(
                    color = Color(0x1545220C),
                    start = Offset(16f, y),
                    end = Offset(w - 16f, y),
                    strokeWidth = 1.8f
                )
            }

            // 3D Corner Rivets with specular glints
            val boltRadius = 6f
            val cornerOffsets = listOf(
                Offset(20f, 20f),
                Offset(w - 20f, 20f),
                Offset(20f, h - 20f),
                Offset(w - 20f, h - 20f)
            )
            cornerOffsets.forEach { pos ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFD54F), Color(0xFF75451D), Color(0xFF321B08)),
                        center = pos,
                        radius = boltRadius
                    ),
                    radius = boltRadius,
                    center = pos
                )
                drawCircle(Color(0xCCFFFFFF), radius = 2f, center = Offset(pos.x - 1.5f, pos.y - 1.5f))
            }
        }

        content()
    }
}

/**
 * 3D Physical Wood Menu Button with real tactile press depression,
 * embossed 3D gold typography, and dynamic specular shine.
 */
@Composable
fun WoodMenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "wood_button"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val yOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = tween(70),
        label = "btn_press"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "btn_glint")
    val glintOffset by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glint"
    )

    Box(
        modifier = modifier
            .testTag(testTag)
            .offset(y = yOffset)
            .height(58.dp)
            .shadow(if (isPressed) 3.dp else 9.dp, RoundedCornerShape(18.dp), spotColor = Color(0xAA000000))
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isPressed) {
                        listOf(Color(0xFF552A0F), Color(0xFF3D1C08))
                    } else {
                        listOf(Color(0xFF8C4C21), Color(0xFF633211), Color(0xFF4A230A))
                    }
                )
            )
            // 3D Carved bevel border
            .border(
                width = 2.2.dp,
                brush = Brush.verticalGradient(
                    colors = if (isPressed) {
                        listOf(Color(0xFF381805), Color(0xFF8C4C21))
                    } else {
                        listOf(Color(0xFFDF9B60), Color(0xFF6B3612), Color(0xFF2E1303))
                    }
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Specular glint line sweeping across button
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val gx = glintOffset * w
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Transparent, Color(0x33FFFFFF), Color.Transparent),
                    start = Offset(gx - 30f, 0f),
                    end = Offset(gx + 30f, h)
                )
            )
        }

        // Embossed 3D Gold Typography with dual highlight & shadow
        Box(contentAlignment = Alignment.Center) {
            // Drop shadow text
            Text(
                text = text,
                color = Color(0xDD000000),
                fontSize = 19.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = 1.5.dp)
            )
            // Foreground text
            Text(
                text = text,
                color = Color(0xFFFFF7ED),
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 3D Spinning Gold Coin Chip.
 */
@Composable
fun CoinsChip(
    coins: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "coin_spin")
    val coinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "coin_rot"
    )

    Box(
        modifier = modifier
            .testTag("coins_chip")
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFFFFD700))
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8C28E), Color(0xFFC7985D))
                )
            )
            .border(2.dp, Color(0xFF8B572A), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 3D Spinning Gold Coin
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .graphicsLayer {
                        rotationY = coinAngle
                        cameraDistance = 12f * 2.5f
                    }
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFEE58), Color(0xFFFFB300), Color(0xFF9E6500))
                        )
                    )
                    .border(2.dp, Color(0xFFFFD700), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🏛", fontSize = 16.sp, modifier = Modifier.shadow(2.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            androidx.compose.foundation.layout.Column {
                Text(
                    text = "Coins",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D3613)
                )
                Text(
                    text = String.format("%,d", coins),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF381D06)
                )
            }
        }
    }
}

/**
 * 3D Profile Chip with metallic rim and glossy avatar.
 */
@Composable
fun ProfileChip(
    playerName: String = "HASIYA",
    playerId: String = "#000",
    initial: String = "H",
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .testTag("profile_chip")
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8C28E), Color(0xFFC7985D))
                )
            )
            .border(2.dp, Color(0xFF8B572A), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF43A047), Color(0xFF1B5E20))
                        )
                    )
                    .border(1.8.dp, Color(0xFFFFD54F), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    modifier = Modifier.shadow(2.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            androidx.compose.foundation.layout.Column {
                Text(
                    text = playerName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF381D06)
                )
                Text(
                    text = playerId,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D3613)
                )
            }
        }
    }
}

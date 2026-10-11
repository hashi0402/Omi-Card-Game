package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Card
import com.example.model.PlayerPosition
import com.example.model.Suit
import com.example.ui.theme.OmiColors

/**
 * Ultra-polished 3D playing card with physical card thickness bevels,
 * 3D perspective tilt, holographic specular light sweep, and dynamic elevation.
 */
@Composable
fun PlayingCardView(
    card: Card,
    isPlayable: Boolean = true,
    isSelected: Boolean = false,
    width: Dp = 54.dp,
    height: Dp = 82.dp,
    rotationZ: Float = 0f,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val suitColor = if (card.suit.isRed) OmiColors.SuitRed else OmiColors.SuitBlack

    // Dynamic 3D elevation and vertical lift
    val yOffset by animateDpAsState(
        targetValue = if (isSelected) (-16).dp else if (isPressed) (-4).dp else 0.dp,
        animationSpec = tween(140, easing = FastOutSlowInEasing),
        label = "3d_lift"
    )

    // 3D perspective pitch tilt when selected or hovered
    val rotX by animateFloatAsState(
        targetValue = if (isSelected) -10f else 0f,
        animationSpec = tween(160),
        label = "3d_rot_x"
    )

    // Holographic specular light sweep
    val infiniteTransition = rememberInfiniteTransition(label = "hologram_sweep")
    val sheenProgress by infiniteTransition.animateFloat(
        initialValue = -1.2f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sheen_anim"
    )

    Box(
        modifier = modifier
            .testTag("card_${card.id}")
            .offset(y = yOffset)
            .graphicsLayer {
                this.rotationZ = rotationZ
                this.rotationX = rotX
                this.cameraDistance = 16f * density
                this.shadowElevation = if (isSelected) 18.dp.toPx() else 6.dp.toPx()
            }
            .size(width, height)
            .shadow(
                elevation = if (isSelected) 14.dp else 4.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = if (isSelected) Color(0xFFFFD700) else Color(0x66000000)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFFDF8), Color(0xFFF9F5EA))
                )
            )
            // 3D physical card edge bevel (Golden rim on playable, double bevel on selected)
            .border(
                width = if (isSelected) 2.5.dp else 1.2.dp,
                brush = if (isSelected) {
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFFEE58), Color(0xFFFFB300), Color(0xFFFFF9C4))
                    )
                } else if (!isPlayable) {
                    Brush.linearGradient(listOf(Color(0xFFD4C7B0), Color(0xFFB5A488)))
                } else {
                    Brush.linearGradient(listOf(Color(0xFFFFF2D6), Color(0xFFCBB696)))
                },
                shape = RoundedCornerShape(10.dp)
            )
            .alpha(if (isPlayable) 1.0f else 0.58f)
            .then(
                if (onClick != null && isPlayable) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .padding(4.dp)
    ) {
        // Holographic Specular Light Sweep Overlay
        if (isPlayable) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val w = size.width
                val h = size.height
                val sheenX = sheenProgress * w
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x28FFFFFF),
                            Color(0x55FFFFFF),
                            Color(0x28FFFFFF),
                            Color.Transparent
                        ),
                        start = Offset(sheenX - 40f, 0f),
                        end = Offset(sheenX + 40f, h)
                    )
                )

                // 3D Physical Card Edge Bevel lines (top highlight & bottom shadow)
                drawLine(
                    color = Color(0x66FFFFFF),
                    start = Offset(0f, 0f),
                    end = Offset(w, 0f),
                    strokeWidth = 2f
                )
                drawLine(
                    color = Color(0x33000000),
                    start = Offset(0f, h),
                    end = Offset(w, h),
                    strokeWidth = 2f
                )
            }
        }

        // Top-left Rank & Suit with 3D drop shadow
        Column(
            modifier = Modifier.align(Alignment.TopStart),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.rank.label,
                color = suitColor,
                fontSize = if (width > 60.dp) 16.sp else 12.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.shadow(1.dp)
            )
            Text(
                text = card.suit.symbol,
                color = suitColor,
                fontSize = if (width > 60.dp) 15.sp else 11.sp,
                lineHeight = 12.sp
            )
        }

        // Center 3D Embossed Suit Emblem
        Box(
            modifier = Modifier.align(Alignment.Center),
            contentAlignment = Alignment.Center
        ) {
            // Subtle 3D background emblem watermark
            Text(
                text = card.suit.symbol,
                color = suitColor.copy(alpha = 0.12f),
                fontSize = if (width > 60.dp) 44.sp else 34.sp
            )
            // Primary foreground 3D suit
            Text(
                text = card.suit.symbol,
                color = suitColor,
                fontSize = if (width > 60.dp) 28.sp else 22.sp,
                modifier = Modifier.shadow(2.dp)
            )
        }

        // Bottom-right mirrored Suit
        Column(
            modifier = Modifier.align(Alignment.BottomEnd),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = card.suit.symbol,
                color = suitColor,
                fontSize = if (width > 60.dp) 15.sp else 11.sp,
                lineHeight = 12.sp
            )
            Text(
                text = card.rank.label,
                color = suitColor,
                fontSize = if (width > 60.dp) 16.sp else 12.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.shadow(1.dp)
            )
        }
    }
}

/**
 * 3D Ornamental Card Back with realistic lacquer finish and gold filigree.
 */
@Composable
fun CardBackView(
    width: Dp = 44.dp,
    height: Dp = 64.dp,
    feltThemeIndex: Int = 0,
    rotationZ: Float = 0f,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
    val primaryColor = when (feltThemeIndex % 4) {
        0 -> Color(0xFF144525) // Deep Emerald
        1 -> Color(0xFF152F4F) // Royal Navy
        2 -> Color(0xFF4A1420) // Velvet Ruby
        else -> Color(0xFF523718) // Antique Wood
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                this.rotationZ = rotationZ
                this.cameraDistance = 14f * density
            }
            .size(width, height)
            .shadow(6.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.85f), primaryColor, Color(0xFF0A180E))
                )
            )
            .border(
                width = 1.8.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFEE58), Color(0xFFB78103), Color(0xFFFFD54F))
                ),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 3D Diamond lattice with gold luster
            val step = 11f
            var x = 0f
            while (x < w) {
                drawLine(
                    color = Color(0x3BFFD700),
                    start = Offset(x, 0f),
                    end = Offset(x + h * 0.5f, h),
                    strokeWidth = 1.2f
                )
                drawLine(
                    color = Color(0x3BFFD700),
                    start = Offset(x, 0f),
                    end = Offset(x - h * 0.5f, h),
                    strokeWidth = 1.2f
                )
                x += step
            }

            // Center 3D embossed medallion
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x99FFD700), Color(0x44B8860B), Color(0x11000000))
                ),
                radius = w * 0.28f,
                center = Offset(w * 0.5f, h * 0.5f)
            )
            drawCircle(
                color = Color(0x88FFD700),
                radius = w * 0.28f,
                center = Offset(w * 0.5f, h * 0.5f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
            )
        }
    }
}

/**
 * 3D Player seat indicator with rotating 3D turn beacon and depth pedestal.
 */
@Composable
fun PlayerSeatBadge(
    name: String,
    avatarEmoji: String,
    position: PlayerPosition,
    tricksWon: Int,
    isCurrentTurn: Boolean,
    cardCount: Int = 8,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "turn_beacon")
    val beaconPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beacon_scale"
    )

    Column(
        modifier = modifier.testTag("seat_${position.name}"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Name pill with 3D drop shadow
        Box(
            modifier = Modifier
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCC1A1624))
                .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFF1F8E9)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Avatar 3D Circle Pedestal
        Box(
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer {
                    if (isCurrentTurn) {
                        scaleX = beaconPulse
                        scaleY = beaconPulse
                    }
                }
                .shadow(if (isCurrentTurn) 12.dp else 6.dp, CircleShape, spotColor = Color(0xFFFFD700))
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (position.isPlayerTeam) {
                            listOf(Color(0xFF388E3C), Color(0xFF1B5E20), Color(0xFF0D3312))
                        } else {
                            listOf(Color(0xFF7E22CE), Color(0xFF4C1D95), Color(0xFF2E1065))
                        }
                    )
                )
                .border(
                    width = if (isCurrentTurn) 3.dp else 1.8.dp,
                    brush = if (isCurrentTurn) {
                        Brush.sweepGradient(
                            colors = listOf(Color(0xFFFFEE58), Color(0xFFFF9800), Color(0xFFFFEE58))
                        )
                    } else {
                        Brush.linearGradient(listOf(Color(0xFFE0E0E0), Color(0xFF757575)))
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = avatarEmoji,
                fontSize = 25.sp,
                modifier = Modifier.shadow(2.dp)
            )
        }

        // 3D Tricks badge
        Box(
            modifier = Modifier
                .offset(y = (-5).dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2A241D), Color(0xFF140F09))
                    )
                )
                .border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(8.dp))
                .padding(horizontal = 7.dp, vertical = 1.dp)
        ) {
            Text(
                text = "$tricksWon",
                color = Color(0xFFFFD54F),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

/**
 * 3D Carved Gemstone Trump indicator badge.
 */
@Composable
fun TrumpBadge(
    trumpSuit: Suit,
    modifier: Modifier = Modifier,
    labelPrefix: String = "Trump"
) {
    val suitColor = if (trumpSuit.isRed) OmiColors.SuitRed else Color(0xFF1F2421)

    Box(
        modifier = modifier
            .testTag("trump_badge")
            .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFFFFD700))
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF9E6), Color(0xFFF5DEB3), Color(0xFFE2C28D))
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFFFEE58), Color(0xFFB78103), Color(0xFFFFD54F))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 14.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = labelPrefix,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF45220C),
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = trumpSuit.symbol,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = suitColor,
                modifier = Modifier.shadow(2.dp)
            )
        }
    }
}

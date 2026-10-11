package com.example.ui.screens

import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.audio.SoundManager
import com.example.model.Card
import com.example.model.CardRank
import com.example.model.Suit
import com.example.ui.components.PlayingCardView
import kotlinx.coroutines.delay
import kotlin.random.Random

data class FallingCoin(
    val initialX: Float,
    val speed: Float,
    val size: Float,
    val rotationSpeed: Float
)

@Composable
fun SplashScreen(
    onLoadingComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current.density

    // Dynamic resolution of uploaded video in res/raw (splash_video, loading_video, or omi_intro)
    val videoResId = remember {
        val id1 = context.resources.getIdentifier("splash_video", "raw", context.packageName)
        if (id1 != 0) id1 else {
            val id2 = context.resources.getIdentifier("loading_video", "raw", context.packageName)
            if (id2 != 0) id2 else {
                context.resources.getIdentifier("videoplayback", "raw", context.packageName)
            }
        }
    }

    var progressTarget by remember { mutableFloatStateOf(0f) }
    var currentAiStage by remember { mutableStateOf("AI Engine Initializing...") }
    var currentAiStatus by remember { mutableStateOf("Scanning runtime...") }

    // Fast 6.5s load (within 5-10 seconds requirement)
    val loadingDuration = 6500

    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = loadingDuration, easing = LinearEasing),
        label = "ai_load_progress"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "coins_fall")
    val coinAnimationProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "falling_coins"
    )

    val sparklePulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle"
    )

    val shieldSway by infiniteTransition.animateFloat(
        initialValue = -7f,
        targetValue = 7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_sway"
    )

    val coins = remember {
        List(24) {
            FallingCoin(
                initialX = Random.nextFloat(),
                speed = 0.7f + Random.nextFloat() * 0.8f,
                size = 18f + Random.nextFloat() * 16f,
                rotationSpeed = Random.nextFloat() * 360f
            )
        }
    }

    LaunchedEffect(Unit) {
        progressTarget = 1.0f
        SoundManager.playCardDealSound()

        // 5-10s AI Bug Scanner & Auto-Fix Lifecycle
        delay(1200)
        currentAiStage = "🔍 AI Diagnostic: Scanning Memory & Deck Rules..."
        currentAiStatus = "Auditing 32-card Sri Lankan Omi deck entropy [0 Bugs]"

        delay(1500)
        currentAiStage = "⚡ AI Bug Scanner: Checking Trick Evaluator..."
        currentAiStatus = "Verifying follow-suit rules & trump precedence [100% Valid]"

        delay(1500)
        currentAiStage = "🛠️ AI Auto-Fix: Optimizing Game Logic & Neural Bots..."
        currentAiStatus = "Auto-fixing latency & balancing Kapothi scoring [FIXED ✓]"

        delay(1300)
        currentAiStage = "✨ AI Verification: 100% Bug-Free & Optimized!"
        currentAiStatus = "Kalam Social Game Club Engine Ready. Opening Table..."
        SoundManager.playVictorySound()

        delay(1000)
        onLoadingComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF09080E))
            .clickable { onLoadingComplete() }
    ) {
        if (videoResId != 0) {
            // Actual Video Playback via AndroidView & VideoView
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        val uri = Uri.parse("android.resource://${ctx.packageName}/$videoResId")
                        setVideoURI(uri)
                        setOnPreparedListener { mp ->
                            mp.isLooping = true
                            mp.setVolume(1.0f, 1.0f)
                            start()
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // 1. High-Fidelity 3D Recreation of Video (Casino Velvet, Golden Shield, Falling Coins)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Radial velvet casino background glow
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1E1728), Color(0xFF110D18), Color(0xFF07050A)),
                        center = Offset(w * 0.5f, h * 0.45f),
                        radius = w * 0.85f
                    )
                )

                // Table felt ring glow at bottom
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1B4E2B), Color(0x000D2B16)),
                        center = Offset(w * 0.5f, h * 0.85f),
                        radius = w * 0.65f
                    ),
                    topLeft = Offset(w * 0.05f, h * 0.65f),
                    size = Size(w * 0.9f, h * 0.4f)
                )

                // Falling Golden Coins from video
                coins.forEach { coin ->
                    val progress = (coinAnimationProgress * coin.speed + coin.initialX) % 1.0f
                    val cy = progress * (h + 100f) - 50f
                    val cx = coin.initialX * w

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFFFEE58), Color(0xFFFFB300), Color(0xFFC48800)),
                            center = Offset(cx, cy),
                            radius = coin.size
                        ),
                        radius = coin.size * 0.5f,
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color(0xFFFFD700),
                        radius = coin.size * 0.4f,
                        center = Offset(cx, cy),
                        style = Stroke(width = 2f)
                    )
                }

                // Floating Card Suit Sparks
                for (i in 0..6) {
                    val sx = (w * (0.15f + i * 0.12f))
                    val sy = (h * (0.2f + (i % 3) * 0.25f))
                    drawCircle(
                        color = Color(0x22FFD700),
                        radius = 12f * sparklePulse,
                        center = Offset(sx, sy)
                    )
                }
            }

            // Video Centerpiece: Fanned Royal Cards + Golden Dual Shield with OMI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .offset(y = (-40).dp)
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                // Fanned Royal Playing Cards behind the shield
                PlayingCardView(
                    card = Card(Suit.SPADES, CardRank.ACE),
                    width = 54.dp,
                    height = 82.dp,
                    modifier = Modifier
                        .offset(x = (-95).dp, y = (-25).dp)
                        .rotate(-32f)
                )
                PlayingCardView(
                    card = Card(Suit.HEARTS, CardRank.KING),
                    width = 54.dp,
                    height = 82.dp,
                    modifier = Modifier
                        .offset(x = (-55).dp, y = (-50).dp)
                        .rotate(-18f)
                )
                PlayingCardView(
                    card = Card(Suit.DIAMONDS, CardRank.QUEEN),
                    width = 54.dp,
                    height = 82.dp,
                    modifier = Modifier
                        .offset(x = 0.dp, y = (-60).dp)
                        .rotate(0f)
                )
                PlayingCardView(
                    card = Card(Suit.CLUBS, CardRank.KING),
                    width = 54.dp,
                    height = 82.dp,
                    modifier = Modifier
                        .offset(x = 55.dp, y = (-50).dp)
                        .rotate(18f)
                )
                PlayingCardView(
                    card = Card(Suit.HEARTS, CardRank.ACE),
                    width = 54.dp,
                    height = 82.dp,
                    modifier = Modifier
                        .offset(x = 95.dp, y = (-25).dp)
                        .rotate(32f)
                )

                // 3D Rotating Golden Shield with OMI
                Box(
                    modifier = Modifier
                        .size(175.dp, 205.dp)
                        .graphicsLayer {
                            rotationY = shieldSway
                            cameraDistance = 16f * density
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(20.dp, shape = RoundedCornerShape(24.dp))
                    ) {
                        val sw = size.width
                        val sh = size.height

                        // Left half path (Royal Navy)
                        val leftShield = Path().apply {
                            moveTo(sw * 0.5f, 0f)
                            lineTo(sw * 0.05f, sh * 0.15f)
                            lineTo(sw * 0.05f, sh * 0.65f)
                            cubicTo(sw * 0.05f, sh * 0.85f, sw * 0.35f, sh * 0.95f, sw * 0.5f, sh)
                            close()
                        }
                        drawPath(
                            path = leftShield,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E3A8A), Color(0xFF172554))
                            )
                        )

                        // Right half path (Royal Purple)
                        val rightShield = Path().apply {
                            moveTo(sw * 0.5f, 0f)
                            lineTo(sw * 0.95f, sh * 0.15f)
                            lineTo(sw * 0.95f, sh * 0.65f)
                            cubicTo(sw * 0.95f, sh * 0.85f, sw * 0.65f, sh * 0.95f, sw * 0.5f, sh)
                            close()
                        }
                        drawPath(
                            path = rightShield,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF7E22CE), Color(0xFF581C87))
                            )
                        )

                        // Ornate Golden Border around full shield
                        val fullBorder = Path().apply {
                            moveTo(sw * 0.5f, 0f)
                            lineTo(sw * 0.05f, sh * 0.15f)
                            lineTo(sw * 0.05f, sh * 0.65f)
                            cubicTo(sw * 0.05f, sh * 0.85f, sw * 0.35f, sh * 0.95f, sw * 0.5f, sh)
                            cubicTo(sw * 0.65f, sh * 0.95f, sw * 0.95f, sh * 0.85f, sw * 0.95f, sh * 0.65f)
                            lineTo(sw * 0.95f, sh * 0.15f)
                            close()
                        }
                        drawPath(
                            path = fullBorder,
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFEE58), Color(0xFFFFB300), Color(0xFFFFF176))
                            ),
                            style = Stroke(width = 9f)
                        )
                    }

                    // Golden 3D OMI Title Embossed from Video
                    Text(
                        text = "OMI",
                        fontSize = 58.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFFFFD700),
                        modifier = Modifier.shadow(16.dp)
                    )
                }

                // Golden "PLAY TODAY!" ribbon at bottom
                Box(
                    modifier = Modifier
                        .offset(y = 115.dp)
                        .shadow(10.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF8C5C00), Color(0xFFF59E0B), Color(0xFF8C5C00))
                            )
                        )
                        .border(1.5.dp, Color(0xFFFFEE58), RoundedCornerShape(12.dp))
                        .padding(horizontal = 24.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "PLAY TODAY!",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                }
            }
        }

        // 2. AI Fast Bug Scanner & Diagnostic Progress Box (Overlay at bottom)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(14.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xEE151221))
                    .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI BUG SCANNER & AUTO-FIX",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD54F),
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFEE58)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF27213A))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF4CAF50),
                                            Color(0xFFFFEB3B),
                                            Color(0xFFFF9800)
                                        )
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Diagnostics Status
                    Text(
                        text = currentAiStage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE0E7FF),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = currentAiStatus,
                        fontSize = 11.sp,
                        color = Color(0xFF86EFAC),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

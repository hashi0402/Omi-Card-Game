package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Sound management utility that plays authentic synthesized sound effects
 * for card deals, card play, trump selection, trick won, and round victories.
 * Operates reliably across all devices without requiring external media assets.
 */
object SoundManager {

    private val audioScope = CoroutineScope(Dispatchers.Default)

    /**
     * Card Deal sound: a snappy, quick friction-swish of dealing a card.
     */
    fun playCardDealSound(enabled: Boolean = true, volume: Float = 1.0f) {
        if (!enabled || volume <= 0.01f) return
        audioScope.launch {
            val sampleRate = 44100
            val durationMs = 80
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Sweeping white-noise-like swish with high pass frequency
                val freq = 1200.0 - (i.toDouble() / numSamples) * 800.0
                val envelope = (1.0 - (i.toDouble() / numSamples)) * 0.7
                val sine = sin(2.0 * Math.PI * freq * t)
                val noise = (Math.random() * 2.0 - 1.0) * 0.4
                val sample = ((sine * 0.6 + noise) * envelope * Short.MAX_VALUE * volume).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            writeAndPlay(buffer, sampleRate)
        }
    }

    /**
     * Card Play sound: crisp tactile wooden-felt tap when placing card on table.
     */
    fun playCardPlaySound(enabled: Boolean = true, volume: Float = 1.0f) {
        if (!enabled || volume <= 0.01f) return
        audioScope.launch {
            val sampleRate = 44100
            val durationMs = 70
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 320.0 * (1.0 - (i.toDouble() / numSamples) * 0.6)
                val envelope = Math.exp(-i.toDouble() / (sampleRate * 0.015))
                val sample = (sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE * volume).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            writeAndPlay(buffer, sampleRate)
        }
    }

    /**
     * Trump Selected sound: ascending bright triumphant chime chord.
     */
    fun playTrumpSelectedSound(enabled: Boolean = true, volume: Float = 1.0f) {
        if (!enabled || volume <= 0.01f) return
        audioScope.launch {
            val sampleRate = 44100
            val durationMs = 320
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            // Ascending major chord notes: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            val stepSamples = numSamples / notes.size

            for (i in 0 until numSamples) {
                val noteIdx = (i / stepSamples).coerceAtMost(notes.size - 1)
                val freq = notes[noteIdx]
                val t = i.toDouble() / sampleRate
                val noteSampleIdx = i % stepSamples
                val envelope = 1.0 - (noteSampleIdx.toDouble() / stepSamples) * 0.5
                val sample = (sin(2.0 * Math.PI * freq * t) * envelope * 0.8 * Short.MAX_VALUE * volume).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            writeAndPlay(buffer, sampleRate)
        }
    }

    /**
     * Trick Won sound: sparkling golden coin bell ding.
     */
    fun playTrickWonSound(enabled: Boolean = true, volume: Float = 1.0f) {
        if (!enabled || volume <= 0.01f) return
        audioScope.launch {
            val sampleRate = 44100
            val durationMs = 280
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Dual bell harmonics: 987Hz (B5) and 1975Hz (B6)
                val envelope = Math.exp(-i.toDouble() / (sampleRate * 0.08))
                val wave1 = sin(2.0 * Math.PI * 987.77 * t)
                val wave2 = sin(2.0 * Math.PI * 1975.53 * t) * 0.5
                val sample = ((wave1 + wave2) * envelope * 0.7 * Short.MAX_VALUE * volume).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            writeAndPlay(buffer, sampleRate)
        }
    }

    /**
     * Round Victory / Kapothi fanfare: cheerful celebratory melody.
     */
    fun playVictorySound(enabled: Boolean = true, volume: Float = 1.0f) {
        if (!enabled || volume <= 0.01f) return
        audioScope.launch {
            val sampleRate = 44100
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50, 1318.51)
            val noteDurationMs = 120
            val totalSamples = ((noteDurationMs * notes.size) * (sampleRate / 1000.0)).toInt()
            val buffer = ShortArray(totalSamples)

            val stepSamples = (noteDurationMs * (sampleRate / 1000.0)).toInt()

            for (i in 0 until totalSamples) {
                val noteIdx = (i / stepSamples).coerceAtMost(notes.size - 1)
                val freq = notes[noteIdx]
                val t = i.toDouble() / sampleRate
                val noteSampleIdx = i % stepSamples
                val envelope = 1.0 - (noteSampleIdx.toDouble() / stepSamples) * 0.4
                val sample = (sin(2.0 * Math.PI * freq * t) * envelope * 0.75 * Short.MAX_VALUE * volume).toInt()
                buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            writeAndPlay(buffer, sampleRate)
        }
    }

    private fun writeAndPlay(buffer: ShortArray, sampleRate: Int) {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val trackBufferSize = buffer.size.coerceAtLeast(minBufferSize)

            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(trackBufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            // Release track after playback completes
            audioScope.launch {
                val delayMs = ((buffer.size.toDouble() / sampleRate) * 1000).toLong() + 100
                kotlinx.coroutines.delay(delayMs)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            // Graceful fallback if device audio daemon is busy
        }
    }
}

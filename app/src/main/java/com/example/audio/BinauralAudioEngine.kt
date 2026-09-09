package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

enum class BrainwaveMode(
    val titleAr: String,
    val descriptionAr: String,
    val baseFrequencyHz: Double,
    val beatFrequencyHz: Double // Difference between left & right ear
) {
    ALPHA(
        titleAr = "موجات ألفا (10 هرتز)",
        descriptionAr = "حالة الاسترخاء الواعي والتركيز الفائق وحفظ المعلومات السريع",
        baseFrequencyHz = 220.0,
        beatFrequencyHz = 10.0
    ),
    THETA(
        titleAr = "موجات ثيتا (6 هرتز)",
        descriptionAr = "تثبيت الذاكرة العميقة والتأمل في قصر الذاكرة والاستحضار",
        baseFrequencyHz = 180.0,
        beatFrequencyHz = 6.0
    ),
    BETA(
        titleAr = "موجات بيتا (18 هرتز)",
        descriptionAr = "شحذ التنبيه العقلي السريع لحل المسائل المعقدة والامتحانات",
        baseFrequencyHz = 250.0,
        beatFrequencyHz = 18.0
    )
}

class BinauralAudioEngine {
    private var audioTrack: AudioTrack? = null
    private var generatorJob: Job? = null
    private val sampleRate = 44100

    var isPlaying = false
        private set

    var currentMode: BrainwaveMode = BrainwaveMode.ALPHA
        private set

    fun start(scope: CoroutineScope, mode: BrainwaveMode = BrainwaveMode.ALPHA) {
        stop()
        currentMode = mode
        isPlaying = true

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = minBufferSize * 2

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val format = AudioFormat.Builder()
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .build()

        val track = AudioTrack.Builder()
            .setAudioAttributes(attributes)
            .setAudioFormat(format)
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack = track
        track.play()

        generatorJob = scope.launch(Dispatchers.Default) {
            val leftFreq = mode.baseFrequencyHz
            val rightFreq = mode.baseFrequencyHz + mode.beatFrequencyHz
            val chunkSize = 2048
            val buffer = ShortArray(chunkSize * 2) // Stereo: left, right interleaved

            var leftPhase = 0.0
            var rightPhase = 0.0
            val leftPhaseInc = 2.0 * Math.PI * leftFreq / sampleRate
            val rightPhaseInc = 2.0 * Math.PI * rightFreq / sampleRate
            val amplitude = 0.18 // Comfortable soothing volume level

            while (isActive && isPlaying) {
                for (i in 0 until chunkSize) {
                    val leftSample = (sin(leftPhase) * amplitude * Short.MAX_VALUE).toInt().toShort()
                    val rightSample = (sin(rightPhase) * amplitude * Short.MAX_VALUE).toInt().toShort()

                    buffer[i * 2] = leftSample
                    buffer[i * 2 + 1] = rightSample

                    leftPhase += leftPhaseInc
                    rightPhase += rightPhaseInc
                    if (leftPhase >= 2.0 * Math.PI) leftPhase -= 2.0 * Math.PI
                    if (rightPhase >= 2.0 * Math.PI) rightPhase -= 2.0 * Math.PI
                }

                try {
                    audioTrack?.write(buffer, 0, buffer.size)
                } catch (e: Exception) {
                    break
                }
            }
        }
    }

    fun stop() {
        isPlaying = false
        generatorJob?.cancel()
        generatorJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignore
        }
        audioTrack = null
    }
}

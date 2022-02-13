package com.example.webunihw006_mediaplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.webunihw006_mediaplayer.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener {

    lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.forget)

        mediaPlayer.setVolume(0.5f, 0.5f)

        binding.seekbarVolume.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        val volumeNum = progress / 100.0f
                        mediaPlayer.setVolume(volumeNum, volumeNum)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )

        binding.buttonSong1.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer = MediaPlayer.create(
                this@MainActivity,
                R.raw.forget
            )
            mediaPlayer.setOnPreparedListener(this@MainActivity)
        }

        binding.buttonSong2.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer = MediaPlayer.create(
                this@MainActivity,
                R.raw.relax_and_sleep
            )
            mediaPlayer.setOnPreparedListener(this@MainActivity)
        }

        binding.buttonSong3.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer = MediaPlayer.create(
                this@MainActivity,
                R.raw.penguinmusic_modern_chillout
            )
            mediaPlayer.setOnPreparedListener(this@MainActivity)
        }

        binding.buttonStop.setOnClickListener {
            mediaPlayer?.stop()
        }

        binding.buttonPause.setOnClickListener {
            mediaPlayer.pause()
        }

        binding.buttonPlay.setOnClickListener {
            mediaPlayer.start()
        }


        Thread(Runnable {
            while (mediaPlayer != null) {
                try {
                    var msg = Message()
                    msg.what = mediaPlayer.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()

    }//ONCREATE


    @SuppressLint("SetTextI18n")
    override fun onPrepared(mp: MediaPlayer) {
        mediaPlayer.start()

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            binding.seekbarPosition.progress = mediaPlayer.currentPosition
            // Update Labels
            val elapsedTime = createTimeLabel(binding.seekbarPosition.progress)
            binding.textviewElapsed.text = elapsedTime
            val remainingTime = createTimeLabel(mediaPlayer.duration - binding.seekbarPosition.progress)
            binding.textviewRemaining.text = "-$remainingTime"
        }, 1000)

        binding.seekbarPosition.max = mediaPlayer.duration
        binding.seekbarPosition.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )
    }

    override fun onStop() {
        mediaPlayer.stop()
        mediaPlayer?.seekTo(0)
        super.onStop()
    }


    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        @SuppressLint("HandlerLeak", "SetTextI18n")
        override fun handleMessage(msg: Message) {
            val currentPosition = msg.what
            // Update positionBar
            binding.seekbarPosition.progress = currentPosition
            // Update Labels
            binding.textviewElapsed.text = createTimeLabel(currentPosition)
            binding.textviewRemaining.text = "-" + createTimeLabel(mediaPlayer.duration - currentPosition)
        }
    }

    // time format
    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60
        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }
}

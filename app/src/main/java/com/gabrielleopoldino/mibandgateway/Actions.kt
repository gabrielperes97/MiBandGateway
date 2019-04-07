package com.gabrielleopoldino.mibandgateway

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent

object Actions {

    val actionList = mapOf(
        "Start/Stop Music" to this::startStopMusic,
        "Next Music" to this::nextMusic,
        "Previous Music" to this::previousMusic
    )

    fun startStopMusic(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sendMediaButton(am, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
    }

    fun nextMusic(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sendMediaButton(am, KeyEvent.KEYCODE_MEDIA_NEXT)
    }

    fun previousMusic(context: Context) {
        val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sendMediaButton(am, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
        sendMediaButton(am, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
    }

    private fun sendMediaButton(am: AudioManager, keyCode: Int) {
        var keyEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        am.dispatchMediaKeyEvent(keyEvent)

        keyEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
        am.dispatchMediaKeyEvent(keyEvent)
    }
}
package com.example.android.socketexamples.http

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * Created by rbmenke on 4/8/18.
 */
class LocationEmitter : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        webSocket?.let {
            it.send("onOpen called")
            it.send("Hello from android!")
        }
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        Log.d("xxx socket failure", t?.message)
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        webSocket?.send("Closing connection")
        Log.d("xxx socket closing", code.toString())
    }

    override fun onMessage(webSocket: WebSocket?, text: String?) {
        Log.d("xxx received text", text)
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString?) {
        bytes?.let {
            Log.d("xxx received bytes", String(it.toByteArray()))
        }
    }

    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        Log.d("xxx socket closed", code.toString())
    }
}
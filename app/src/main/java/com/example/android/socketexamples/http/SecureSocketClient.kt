package com.example.android.socketexamples.http

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager

/**
 * Created by rbmenke on 4/8/18.
 */
class SecureSocketClient {

    private val socket = getSocket()

    init {
        socket.connect()
    }


    fun sendString(message : String) {
        socket.send(message)
    }

    /**
     * Publicly expose a method of adding events to the socket
     */
    fun addEvent(eventName : String, listener : Emitter.Listener) : Emitter {
        return socket.on(eventName, listener)
    }

    /**
     * Configure a secure socket
     */
    private fun getSocket() : Socket {
        val options = IO.Options()
        val httpClient = getHttpClient()
        options.callFactory = httpClient
        options.webSocketFactory = httpClient
        options.port = 2323
        options.forceNew = true
        options.reconnection = true
        options.reconnectionDelay = 2000L
        options.secure = true //TODO: Test https/wss over ngrok

        return IO.socket("https://localhost", options)
    }

    /**
     * Create the
     */
    private fun getHttpClient() : OkHttpClient {
        val sslContext = getSSLContext()
        val trustManager = getX509TrustManager()

        return OkHttpClient
            .Builder()
            .hostnameVerifier(getHostnameVerifier())
            .sslSocketFactory(sslContext.socketFactory, trustManager)
            .build()
    }

    /**
     * This trust manager will currently trust all certificate issuers.
     *
     * TODO: Edit this based on your app's security needs
     */
    private fun getX509TrustManager() : X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }

    /**
     * This hostname verifier will simply trust all hostnames & sessions at the moment
     *
     * TODO: Edit this based on your app's security needs
     */
    private fun getHostnameVerifier() : HostnameVerifier {
        return HostnameVerifier { hostname : String?, session : SSLSession? -> true }
    }

    /**
     * Create and return an SSLContext
     */
    private fun getSSLContext() : SSLContext {
        val context = SSLContext.getInstance("SSL")
        context.init(null, arrayOf(getX509TrustManager()), SecureRandom())

        return context
    }

    companion object {

        /**
         * Initialize the socket with a set list of events
         */
        fun withEvents(events : Map<String, Emitter.Listener>) : SecureSocketClient {
            val client = SecureSocketClient()
            events.forEach {
                client.addEvent(it.key, it.value)
            }

            return client
        }
    }
}
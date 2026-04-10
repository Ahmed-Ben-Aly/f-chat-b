package com.ahmed.familychat.network

import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.json.JSONObject
import java.net.URI

object SocketManager {

    // ⚠️ Replace this URL with your actual server URL when deploying
    private const val SERVER_URL = "http://localhost:3000"
    private const val TAG = "SocketManager"

    private var socket: Socket? = null

    // Flow for incoming messages
    private val _messageFlow = MutableSharedFlow<JSONObject>(extraBufferCapacity = 50)
    val messageFlow: SharedFlow<JSONObject> = _messageFlow

    // Flow for registration result
    private val _registrationResultFlow = MutableSharedFlow<JSONObject>(extraBufferCapacity = 1)
    val registrationResultFlow: SharedFlow<JSONObject> = _registrationResultFlow

    // Flow for user status changes
    private val _userStatusFlow = MutableSharedFlow<JSONObject>(extraBufferCapacity = 50)
    val userStatusFlow: SharedFlow<JSONObject> = _userStatusFlow

    // WebRTC Signaling Flows
    private val _incomingCallFlow = MutableSharedFlow<JSONObject>(extraBufferCapacity = 1)
    val incomingCallFlow: SharedFlow<JSONObject> = _incomingCallFlow

    private val _callAcceptedFlow = MutableSharedFlow<JSONObject>(extraBufferCapacity = 1)
    val callAcceptedFlow: SharedFlow<JSONObject> = _callAcceptedFlow

    private val _callRejectedFlow = MutableSharedFlow<JSONObject>(extraBufferCapacity = 1)
    val callRejectedFlow: SharedFlow<JSONObject> = _callRejectedFlow

    private val _callEndedFlow = MutableSharedFlow<JSONObject>(extraBufferCapacity = 1)
    val callEndedFlow: SharedFlow<JSONObject> = _callEndedFlow

    private val _webrtcOfferFlow = MutableSharedFlow<JSONObject>(extraBufferCapacity = 1)
    val webrtcOfferFlow: SharedFlow<JSONObject> = _webrtcOfferFlow

    private val _webrtcAnswerFlow = MutableSharedFlow<JSONObject>(extraBufferCapacity = 1)
    val webrtcAnswerFlow: SharedFlow<JSONObject> = _webrtcAnswerFlow

    private val _webrtcIceCandidateFlow = MutableSharedFlow<JSONObject>(extraBufferCapacity = 10)
    val webrtcIceCandidateFlow: SharedFlow<JSONObject> = _webrtcIceCandidateFlow

    fun connect() {
        if (socket?.connected() == true) return
        try {
            val options = IO.Options.builder()
                .setReconnection(true)
                .setReconnectionDelay(1000)
                .build()

            socket = IO.socket(URI.create(SERVER_URL), options)

            socket?.on(Socket.EVENT_CONNECT) {
                println("SocketManager: Connected to server")
            }

            socket?.on(Socket.EVENT_DISCONNECT) {
                println("SocketManager: Disconnected from server")
            }

            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                println("SocketManager: Connection error: ${args[0]}")
            }

            // Listen for incoming messages
            socket?.on("receive_message") { args ->
                if (args.isNotEmpty()) {
                    _messageFlow.tryEmit(args[0] as JSONObject)
                }
            }

            // Registration success
            socket?.on("registration_success") { args ->
                if (args.isNotEmpty()) {
                    _registrationResultFlow.tryEmit(args[0] as JSONObject)
                }
            }

            // User status changed
            socket?.on("user_status_changed") { args ->
                if (args.isNotEmpty()) {
                    _userStatusFlow.tryEmit(args[0] as JSONObject)
                }
            }

            // WebRTC: Incoming call
            socket?.on("incoming_call") { args ->
                if (args.isNotEmpty()) _incomingCallFlow.tryEmit(args[0] as JSONObject)
            }

            // WebRTC: Call accepted
            socket?.on("call_accepted") { args ->
                if (args.isNotEmpty()) _callAcceptedFlow.tryEmit(args[0] as JSONObject)
            }

            // WebRTC: Call rejected
            socket?.on("call_rejected") { args ->
                if (args.isNotEmpty()) _callRejectedFlow.tryEmit(args[0] as JSONObject)
            }

            // WebRTC: Call ended
            socket?.on("call_ended") { args ->
                if (args.isNotEmpty()) _callEndedFlow.tryEmit(args[0] as JSONObject)
            }

            // WebRTC Offer
            socket?.on("webrtc_offer") { args ->
                if (args.isNotEmpty()) _webrtcOfferFlow.tryEmit(args[0] as JSONObject)
            }

            // WebRTC Answer
            socket?.on("webrtc_answer") { args ->
                if (args.isNotEmpty()) _webrtcAnswerFlow.tryEmit(args[0] as JSONObject)
            }

            // WebRTC ICE Candidate
            socket?.on("webrtc_ice_candidate") { args ->
                if (args.isNotEmpty()) _webrtcIceCandidateFlow.tryEmit(args[0] as JSONObject)
            }

            socket?.connect()

        } catch (e: Exception) {
            println("SocketManager: Socket connection failed: ${e.message}")
        }
    }

    fun registerUser(name: String, phoneNumber: String) {
        val data = JSONObject().apply {
            put("name", name)
            put("phoneNumber", phoneNumber)
        }
        socket?.emit("register_user", data)
    }

    fun sendTextMessage(senderId: String, receiverId: String?, content: String) {
        val data = JSONObject().apply {
            put("senderId", senderId)
            put("receiverId", receiverId ?: JSONObject.NULL)
            put("content", content)
            put("type", "text")
        }
        socket?.emit("send_message", data)
    }

    fun initiateCall(callerId: String, receiverId: String, callerName: String) {
        val data = JSONObject().apply {
            put("callerId", callerId)
            put("receiverId", receiverId)
            put("callerName", callerName)
        }
        socket?.emit("initiate_call", data)
    }

    fun acceptCall(callerId: String, receiverId: String) {
        val data = JSONObject().apply {
            put("callerId", callerId)
            put("receiverId", receiverId)
        }
        socket?.emit("accept_call", data)
    }

    fun rejectCall(callerId: String, receiverId: String) {
        val data = JSONObject().apply {
            put("callerId", callerId)
            put("receiverId", receiverId)
        }
        socket?.emit("reject_call", data)
    }

    fun endCall(targetId: String) {
        val data = JSONObject().apply {
            put("targetId", targetId)
        }
        socket?.emit("end_call", data)
    }

    fun sendWebRtcOffer(targetId: String, sdp: String) {
        val data = JSONObject().apply {
            put("targetId", targetId)
            put("sdp", sdp)
        }
        socket?.emit("webrtc_offer", data)
    }

    fun sendWebRtcAnswer(targetId: String, sdp: String) {
        val data = JSONObject().apply {
            put("targetId", targetId)
            put("sdp", sdp)
        }
        socket?.emit("webrtc_answer", data)
    }

    fun sendIceCandidate(targetId: String, candidate: JSONObject) {
        val data = JSONObject().apply {
            put("targetId", targetId)
            put("candidate", candidate)
        }
        socket?.emit("webrtc_ice_candidate", data)
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }

    fun isConnected(): Boolean = socket?.connected() == true
}

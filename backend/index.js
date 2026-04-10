const express = require('express');
const http = require('http');
const { Server } = require('socket.io');
const cors = require('cors');
const mongoose = require('mongoose');
require('dotenv').config();

const User = require('./models/User');
const Message = require('./models/Message');

const app = express();
app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 3000;
const server = http.createServer(app);
const io = new Server(server, {
    cors: {
        origin: '*',
        methods: ['GET', 'POST']
    }
});

const PORT = process.env.PORT || 3000;
const MONGODB_URI = process.env.MONGODB_URI;

// Connect to MongoDB
mongoose.connect(MONGODB_URI)
    .then(() => console.log('✅ Connected to MongoDB Atlas'))
    .catch(err => console.error('❌ MongoDB Connection Error:', err));

app.get('/', (req, res) => {
    res.send('FamilyChat Backend running (MongoDB Mode).');
});

// Helper function to update user status
async function updateUserStatus(userId, status, socketId = null) {
    try {
        await User.findByIdAndUpdate(userId, { 
            status, 
            socketId,
            lastSeen: new Date()
        });
        io.emit('user_status_changed', { userId, status });
    } catch (err) {
        console.error('Error updating user status:', err);
    }
}

io.on('connection', (socket) => {
    console.log(`User connected: ${socket.id}`);

    socket.on('register_user', async (data) => {
        const { phoneNumber, name } = data;
        try {
            let user = await User.findOne({ phoneNumber });

            if (!user) {
                user = new User({ phoneNumber, name });
                await user.save();
                console.log(`New user created: ${name}`);
            } else {
                user.name = name; // Update name if it changed
                await user.save();
                console.log(`Existing user logged in: ${name}`);
            }

            user.socketId = socket.id;
            user.status = 'online';
            await user.save();

            socket.join(user._id.toString());
            socket.emit('registration_success', { userId: user._id, name: user.name });
            
            // Notify others
            socket.broadcast.emit('user_status_changed', { userId: user._id, status: 'online' });

        } catch (err) {
            console.error('Registration error:', err);
        }
    });

    socket.on('send_message', async (data) => {
        const { senderId, receiverId, content, type } = data;
        try {
            const newMessage = new Message({
                senderId,
                receiverId: receiverId || null,
                content,
                type,
                createdAt: new Date()
            });

            await newMessage.save();
            
            // Emit back to sender
            socket.emit('message_sent', newMessage);

            // Emit to receiver or broadcast
            if (receiverId) {
                io.to(receiverId).emit('receive_message', newMessage);
            } else {
                // If it's a global/family message
                socket.broadcast.emit('receive_message', newMessage);
            }
        } catch (err) {
            console.error('Send message error:', err);
        }
    });

    socket.on('initiate_call', (data) => {
        const { callerId, receiverId, callerName } = data;
        io.to(receiverId).emit('incoming_call', { callerId, callerName });
    });

    socket.on('accept_call', (data) => {
        io.to(data.callerId).emit('call_accepted', { receiverId: data.receiverId });
    });

    socket.on('reject_call', (data) => {
        io.to(data.callerId).emit('call_rejected', { receiverId: data.receiverId });
    });

    socket.on('webrtc_offer', (data) => {
        io.to(data.targetId).emit('webrtc_offer', { senderId: socket.id, sdp: data.sdp });
    });

    socket.on('webrtc_answer', (data) => {
        io.to(data.targetId).emit('webrtc_answer', { senderId: socket.id, sdp: data.sdp });
    });

    socket.on('webrtc_ice_candidate', (data) => {
        io.to(data.targetId).emit('webrtc_ice_candidate', { senderId: socket.id, candidate: data.candidate });
    });

    socket.on('end_call', (data) => {
        io.to(data.targetId).emit('call_ended', { senderId: socket.id });
    });

    socket.on('disconnect', async () => {
        try {
            const user = await User.findOne({ socketId: socket.id });
            if (user) {
                user.status = 'offline';
                user.socketId = null;
                user.lastSeen = new Date();
                await user.save();
                io.emit('user_status_changed', { userId: user._id, status: 'offline' });
                console.log(`User offline: ${user.name}`);
            }
        } catch (err) {
            console.error('Disconnect error:', err);
        }
        console.log(`Socket disconnected: ${socket.id}`);
    });
});

server.listen(PORT, () => {
    console.log(`Server listening on port ${PORT}`);
    console.log('--- READY FOR MOBILE APP CONNECTION (MONGODB AGENT) ---');
});


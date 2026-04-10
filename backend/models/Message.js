const mongoose = require('mongoose');

const messageSchema = new mongoose.Schema({
    senderId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        required: true,
    },
    receiverId: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User',
        default: null, // If null, it can be a group/family message (optional future feature)
    },
    content: {
        type: String,
        required: false, // Could be empty if it's purely a voice message
    },
    type: {
        type: String,
        enum: ['text', 'voice', 'call_log'],
        default: 'text',
    },
    mediaUrl: {
        type: String,
        default: null, // URL for voice note
    },
    duration: {
        type: Number,
        default: 0, // Duration of voice note or call in seconds
    },
    isDelivered: {
        type: Boolean,
        default: false,
    },
    isRead: {
        type: Boolean,
        default: false
    }
}, { timestamps: true });

module.exports = mongoose.model('Message', messageSchema);

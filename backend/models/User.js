const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    trim: true,
  },
  phoneNumber: {
    type: String,
    required: true,
    unique: true,
    trim: true,
  },
  status: {
    type: String,
    default: 'offline', // "online", "offline", "in-call"
  },
  lastSeen: {
    type: Date,
    default: Date.now,
  },
  socketId: {
     type: String,
     default: null
  }
}, { timestamps: true });

module.exports = mongoose.model('User', userSchema);

var mongoose = require('mongoose');

var tootSchema = new mongoose.Schema({
  origin: { type: String, default:'' },
  destination: { type: String, default:'' },
  classification: { 
    type: String,
    required: true,
    default: 'arrival',
    enum: ['arrival', 'otw', 'beer']
  },
  eta: { 
    type: Number, 
    default: 0
  }
});

var userSchema = new mongoose.Schema({
  username: { type: String, default:'' },
  password: { type: String, default:'password' },
  registrationId: { 
    type: String, 
    default: 0
  },
  friends: { 
    type: [String],
    default: []
  }
});


module.exports = {
    Toot: mongoose.model('Toot', tootSchema),
    User: mongoose.model('User', userSchema)
};
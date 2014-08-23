var mongoose = require('mongoose');

var tootSchema = new mongoose.Schema({
  origin: { type: String, default:'' },
  destination: { type: String, default:'' },
  classification: { 
    type: String,
    required: true,
    default: 'arrival',
    enum: ['arrival', 'otw']
  },
  eta: { 
    type: Number, 
    default: 0
  }
});

module.exports = mongoose.model('Toot', tootSchema);
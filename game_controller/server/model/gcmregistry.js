var mongoose = require('mongoose');
var ErrorHandler = require('../controllers/errorhandler')
var registry_prefix = "[Registry]"

var RegIdSchema = new mongoose.Schema({
  email: {
    type: String,
    unique: true,
    required: true
  },
  reg_id: {
    type: String,
    unique: true,
    required: true    
  },
  nickname: {
    type: String,
    unique: true,
    required: true    
  }
});

var RegIdModel = mongoose.model('RegId', RegIdSchema);

exports.getRegIdByEmail = function(email, callback) {
    var reg_id = {'email': email};
    RegIdModel.find(reg_id, function (err, reg_ids) {
        ErrorHandler.handleError(registry_prefix, err);
        if (!err)
            callback(reg_ids[0]);
    });
}

exports.getRegIdByNickname = function(nickname, callback) {
    var reg_id = {'nickname': nickname};
    RegIdModel.find(reg_id, function (err, reg_ids) {
        ErrorHandler.handleError(registry_prefix, err);
        if (!err)
            callback(reg_ids[0]);
    });
}

exports.addRegId = function(reg_id, callback) {
    var reg_id_model = new RegIdModel(reg_id);
    reg_id_model.save(function(err) {
        ErrorHandler.handleError(registry_prefix, err);
        if (!err)
            callback();
    });
}

exports.removeRegIdByEmail = function(email, callback) {
    var reg_id = {'email': email};
    RegIdModel.remove(reg_id, function (err) {
        ErrorHandler.handleError(registry_prefix, err);
        if (!err)
            callback();
    });
}

/*
 * Module dependencies
 */
var mongoose    = require("mongoose");
var moment    = require('moment');

/*
*
*Create a model for this
*
*/

var DEBUG = 1;

var uristring =
process.env.MONGOLAB_URI ||
process.env.MONGOHQ_URL ||
'mongodb://localhost/BomberMap'+DEBUG;

var db = mongoose.connect(uristring, function (err, res) {
  if (err) {
  console.log ('ERROR connecting to: ' + uristring + '. ' + err);
  } else {
  console.log ('Succeeded connected to: ' + uristring);
  }
});

var Schema = mongoose.Schema;

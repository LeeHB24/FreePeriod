var express = require('express');
var app = express();
var ejs = require('ejs');
var http = require('http').Server(app); //1
var io = require('socket.io')(http);

app.set('view engine', 'html');
app.engine('html', ejs.renderFile);
app.get('/', function(req, res){
   res.render('post.html');
})

var count=1;
io.on('connection', function(socket){ //3
  console.log('user connected: ', socket.id);  //3-1
  var name = "user" + count++;                 //3-1
  io.to(socket.id).emit('change name',name);   //3-1

  socket.on('disconnect', function(){ //3-2
    console.log('user disconnected: ', socket.id);
  });

  socket.on('message', function(text){ //3-3
    var message = JSON.stringify(text);
    var msg = name + ' : ' + message;
    console.log(msg);
    io.sockets.emit('recMsg', text);
  }
}


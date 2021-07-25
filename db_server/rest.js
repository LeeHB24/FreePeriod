var express = require('express');
var router = express.Router();
var mysql = require('mysql');
var bodyParser = require('body-parser');
var app = express();
var http = require('http');
var ejs = require('ejs');

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));
app.set('view engine', 'html');
app.engine('html', ejs.renderFile);

var connection = mysql.createConnection({
    host: 'localhost',
    port: '3306',
    user: 'fanta',
    password: '4628',
    database: 'Test'
});

connection.connect();   

app.get('/', function(req, res){
   res.render('post.html');
})

app.get('/user', function(req, res){
    connection.query('SELECT * from User', function(err, rows, fields){
    if(!err){
       res.send(rows);
    }
    else
        console.log('Error while performing Query.', err);
    });
});

app.post('/images', function(req, res){
    console.log('이미지 받기 완료!');
});

http.createServer(app).listen(8008, function(){
	console.log("express start : %d", 8008);
});

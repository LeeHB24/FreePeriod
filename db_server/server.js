var express = require('express');
var router = express.Router();
var mysql = require('mysql');
var bodyParser = require("body-parser");
var nodemailer = require("nodemailer");
var app = express();
var ejs = require('ejs');
var FCM = require('fcm-node');
var http = require('http').Server(app); //
var io = require('socket.io')(http);
//var fcm = new FCM("FCM에서 발급받은 서버키");
var serverKey = 'AAAAg_-ngQo:APA91bGvwrv3fPDWB1HM3PsETOyRUyEByaJ-YY4Y8icU0_GEKoi7CzLnhKSenOvgyahxh2ZwgOzdsopiV--FUFOUsDPkA_hfxHMAvU2BvXOp-PWjU7ISQudMfBFeUDjl8y9h9mjrqiPE';
var fcm = new FCM(serverKey);
var fs = require('fs');

var smtpTransport = nodemailer.createTransport({
    service: "Gmail",
    auth: {
        user: "crush0727@gmail.com",
        pass: "zxcv@6882"
    }
});

var rand, mailOptions, host, link, userEmail;

//app.use(bodyParser.json());
//app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json({limit: "50mb"}));
app.use(bodyParser.urlencoded({limit: "50mb", extended: true, parameterLimit:50000}));
app.set('view engine', 'html');
app.engine('html', ejs.renderFile);

app.get('/', function (req, res) {
    res.render('post.html');
});

var connection = mysql.createConnection({
    host: 'localhost',
    port: '3306',
    user: 'fanta',
    password: '4628',
    database: 'hwan'
});

connection.connect();



app.get('/test', function (req, res) {
    res.send("Hello baby");
});

app.get("/timetable_parties", function (req, res) {
    var userId = req.query.user_id;
    if (userId == -1) {
        userId = 34;
    }
    console.log('유저의 파티리스트'+userId);

    connection.query('select userPartyIdList from user where userId = ' + userId + '', function (err, rows, fields) {
        if (!err) {
            var partyList = [];
            var list = JSON.stringify(rows[0]);
            var obj = JSON.parse(list);
            var idList = JSON.parse(obj.userPartyIdList);
            for (var i = 0; i < idList.length; i++) {
                connection.query('select * from party where partyId = ' + idList[i] + '', function (err, rows, fields) {
                    rows[0].partyCover = null;
                    partyList.push(rows[0]);
                    if (rows[rows.length - 1].partyId == idList[idList.length - 1]) {
                        res.send(partyList);
                    }
                });
            }
        }
        else {
            console.log('query error : ' + err);
        }
    });
});

app.get("/user_lecture_id", function (req, res) {
    var userId = req.query.user_id;
    console.log('유저의 강의 목록 불러오기 ' + userId);
    if (userId == -1) {
        userId = 34;
    }

    connection.query('SELECT lecture_list from lecturelist where user_id = ' + userId + '', function (err, rows, fields) {
        if (!err) {
            var list = JSON.parse(rows[0].lecture_list).lecture_id_list;
            res.send(list);
        }
        else {
            console.log('query error : ' + err);
        }
    });
});

app.get("/lectures", function (req, res) {
    connection.query('select * from lecture', function (err, rows, fields) {
        if (!err) {
            res.send(rows);
        }
        else {
            console.log('query error: ' + err);
        }
    });
});

app.post("/update_lecture", function (req, res) {
    var userId = req.query.user_id;
    var flag = req.query.flag;
    var lectureId = req.query.lecture_id;
    console.log(userId);

    connection.query('SELECT lecture_list from lecturelist where user_id = ' + userId + '', function (err, rows, fields) {
        if (!err) {
            var list = JSON.stringify(rows[0].lecture_list);
            var obj = JSON.parse(list);
            var json = JSON.parse(obj);
            var tmp = json.lecture_id_list;
            var idList = JSON.parse(tmp);
            if (flag == 'ADD') {
                idList.push(lectureId);
                connection.query("update lecturelist set lecture_list = json_set(lecture_list,'$.lecture_id_list', '[" + idList + "]') where user_id = " + userId + "", function (err, rows, fields) {
                    if (!err) {
                        console.log('강의 추가 완료!');
                        res.send({ strReceiveMessage: "success" });
                    }
                    else {
                        console.log('query error : ' + err);
                        res.send({ strReceiveMessage: "fail" });
                    }
                });
            }
            else if (flag == 'DEL') {
                for (var i = 0; i < idList.length; i++) {
                    if (idList[i] == lectureId) {
                        idList.splice(i, 1);
                        break;
                    }
                }
                console.log(idList);
                connection.query("update lecturelist set lecture_list = json_set(lecture_list,'$.lecture_id_list', '[" + idList + "]') where user_id = " + userId + "", function (err, rows, fields) {
                    if (!err) {
                        console.log('강의 삭제 완료!');
                        res.send({ strReceiveMessage: "success" });
                    }
                    else {
                        console.log('query error : ' + err);
                        res.send({ strReceiveMessage: "fail" });
                    }
                });
            }
        }
        else {
            console.log('query error : ' + err);
        }
    });
});

app.post("/set_user_token", function (req, res) {
    var userId = req.query.user_id;
    var userToken = req.query.user_token;

    connection.query('UPDATE user set token = "' + userToken + '" where userId = ' + userId + '', function (err, rows, fields) {
        if (!err) {
            console.log('토큰 등록 완료!');
        }
        else {
            console.log('query error : ' + err);
        }

    });
});

app.get("/login", function (req, res) {
    var success = 0;
    var email = req.query.email;
    var password = req.query.password;
    var id = 0;
    var name;

    connection.query('SELECT * from user', function (err, rows, fields) {
        if (!err) {
            console.log(email);
            console.log(password);
            for (var i = 0; i < rows.length; i++) {
                if (rows[i].userEmail == email && rows[i].userPassword == password && rows[i].userAuth == 1) {
                    success = 1;
                    console.log("찾았습니다.");
                    id = rows[i].userId;
                    name = rows[i].userName;
                    break;
                }
                else if (rows[i].userEmail == email && rows[i].userPassword == password && rows[i].userAuth == -1){
                    success = 3;
                    id = rows[i].userId;
                    break;
                }
                else if (rows[i].userEmail == email && rows[i].userPassword == password) {
                    success = 2;
                    console.log("찾았습니다.");
                    id = rows[i].userId;
                    break;
                }
            }
            if (success == 1) {
                var response = { userId: id, strReceiveMessage: "success", userName: name };
                res.send(response);
                console.log(response);
            }
            else if(success == 2){
                var response = { userId: id, strReceiveMessage: "no_validate" };
                res.send(response);
            }
            else if(success == 3){
                var response = { userId: id, strReceiveMessage: "block_user" };
                res.send(response);
            }
            else {
                console.log("못 찾았습니다.");
                var response2 = { userId: id, strReceiveMessage: "fail" };
                res.send(response2);
            }
        }
        else {
            console.log('query error : ' + err);
        }
    });
});

app.post("/register", function (req, res) {
    var email = req.body.userEmail;
    var password = req.body.userPassword;
    var school = req.body.userSchool;
    var name = req.body.userName;

    connection.query("INSERT INTO user(userName, userEmail, userPassword, userSchool, userAuth, userReportedCount, userRatingAverage, userPartyIdList, mLeaderPartyList, waitingPartyList) values ('" + name + "','" + email + "','" + password + "','" + school + "',"+0+","+0+","+0+",'[]','[]','[]')", function (err, rows, fields) {
        if (!err) {
            var response = { message: "success" };
            console.log('등록 완료!');
            res.send(response);
            connection.query('select userId from user where userEmail = "'+email+'"', function(err, rows, fields){
                var id = rows[0].userId;

                connection.query('INSERT INTO lecturelist VALUES ('+id+', \'{"lecture_id_list": "[]"}\')', function(err, rows, fields){
                    if(!err){
                        console.log('강의 리스트 추가 완료!');
                    }
                    else{
                        console.log('query error :' + err);
                    }
                });
            });
        }
        else {
            var response = { message: "fail" };
            console.log('query error :' + err);
            res.send(response);
        }
    });
    
});

app.get('/send', function (req, res) {
    rand = Math.floor((Math.random() * 100) + 54);
    host = "49.236.132.218:8000";
    link = "http://" + host + "/verify?id=" + rand;
    userEmail = req.query.email;
    mailOptions = {
        to: userEmail,
        subject: "공강때 뭐해? 이메일 인증을 해주세요.",
        html: "안녕하세요,<br> 이메일 인증을 위해 아래의 링크를 눌러주세요<br><a href=" + "http://49.236.132.218:8000/verify?id="+rand+ ">이메일 인증링크</a>"
    }
    console.log(mailOptions);
    smtpTransport.sendMail(mailOptions, function (error, response) {
        if (error) {
            console.log(error);
            res.end("error");
        }
        else {
            console.log("Message sent: " + response.message);
            res.end("sent");
        }
    });
});

app.get('/verify', function (req, res) {
    if ((req.protocol + "://" + host) == ("http://" + host)) {
        console.log("Domain is matched. Information is from Authentic email");
        if (req.query.id == rand) {
            var authEmail;
            console.log("email is verified");
            res.end("<h1>Congratulations!! " + mailOptions.to + "!! your email is verified");
            connection.query('SELECT * from user', function (err, rows, fields) {
                for (var i = 0; i < rows.length; i++) {
                    if (rows[i].userEmail == userEmail) {
                        authEmail = userEmail;
                        connection.query("update user set userAuth = true where userEmail = '" + authEmail + "'", function (err, rows, fields) {
                            if (!err) {
                                console.log('인증 완료!!');
                            }
                            else {
                                console.log('query error : ' + err);
                            }
                        });
                        break;
                    }
                }
            });
        }
        else {
            console.log("email is not verified");
            res.end("<h1>Bad Request</h1>");
        }
    }
    else {
        res.end("<h1>Request is from unknown source");
    }
});

app.post("/new_party", function (req, res) {
    var name = req.body.partyName;
    var startTime = req.body.partyStartTime;
    var endTime = req.body.partyEndTime;
    var maxCapacity = req.body.capacityMax;
    var curCapacity = req.body.capacityCurrent;
    var expiredTime = req.body.partyCloseTime;
    var type = req.body.partyType;
    var status = req.body.partyStatus;
    var tag = req.body.partyTag;
    var cover = req.body.partyCover;
    var userId = req.query.user_id;
    if (userId == -1) {
        userId = 34;
    }
    console.log(userId);

    connection.query("INSERT INTO party(partyName, partyStartTime, partyEndTime, capacityMax, capacityCurrent, partyCloseTime, partyCover, partyType, partyTag, partyStatus, partyLeader, partyMemberList, waitingMemberList) values ('" + name + "','" + startTime + "','" + endTime + "','" + maxCapacity + "','" + curCapacity + "','" + expiredTime + "','" + cover + "','" + type + "','" + tag + "','" + status + "'," + userId + ", '[]', '[]')", function (err, rows, fields) {
        if (!err) {
            console.log('파티 등록 완료!');
            connection.query("select * from party", function (err, rows, fields) {
                var partyId = rows[rows.length - 1].partyId;

                connection.query('INSERT INTO chatroom(id, party_id) values (' + partyId + ',' + partyId + ')', function (err, rows, fields) {
                    if (!err) {
                        console.log('채팅방 생성완료');
                    }
                    else {
                        console.log('query error : ' + err);
                    }
                });

                connection.query('select userPartyIdList from user where userId = ' + userId + '', function (err, rows, fields) {
                    if (!err) {
                        var partyList = [];
                        var party, chatroomid;
                        var list = JSON.stringify(rows[0]);
                        var obj = JSON.parse(list);
                        var idList = JSON.parse(obj.userPartyIdList);
                        idList.push(partyId);
                        connection.query("update user set userPartyIdList = '[" + idList + "]' where userId = " + userId + "", function (err, rows, fields) {
                            if (!err) {
                                pushNewRoom_func(partyId, null, name, maxCapacity,curCapacity,userId,userId);
                                console.log(+userId + '번째 유저 파티 등록 및 참가 완료!');
                            }
                            else {
                                console.log('query error : ' + err);
                            }
                        });
                    }
                    else {
                        console.log('query error : ' + err);
                    }
                });
                connection.query('select partyMemberList from party where partyId =' + partyId + '', function (err, rows, fields) {
                    if (!err) {
                        var partyMemberList = JSON.parse(rows[0].partyMemberList);
                        partyMemberList.push(userId);
                        connection.query('update party set partyMemberList = "[' + partyMemberList + ']" where partyId = ' + partyId + '', function (err, rows, fields) {
                            if (!err) {
                                console.log('파티 멤버 리스트에 등록 완료!');
                            }
                            else {
                                console.log('query error : ' + err);
                            }
                        });
                    }
                });
                connection.query('select mLeaderPartyList from user where userId =' + userId + '', function (err, rows, fields) {
                    if (!err) {
                        var mLeaderPartyList = JSON.parse(rows[0].mLeaderPartyList);
                        mLeaderPartyList.push(partyId);
                        connection.query('update user set mLeaderPartyList = "[' + mLeaderPartyList + ']" where userId = ' + userId + '', function (err, rows, fields) {
                            if (!err) {
                                console.log('리더리스트에 등록 완료!');
                            }
                            else {
                                console.log('query error : ' + err);
                            }
                        });
                    }
                });
            });
        }
        else {
            console.log('query error :' + err);
        }
    });

});
function pushNewRoom_func(partyId,image, name,maxCapacity, curCapacity,partyLeader, userId){
    party = { "roomId": partyId, "image": "", "roomName": name, "capacityMax": maxCapacity, "capacityCurrent": curCapacity, "partyLeader": partyLeader };
    connection.query('select token from user where userId =' + userId + '', function (err, rows, fields) {
        var token = rows[0].token;
        push_newRoom(party, token);
    });
}

app.get("/get_partylist", function (req, res) {
    connection.query("SELECT * from party", function (err, rows, fields) {
        /*if (!err) {
            for(var i = 0; i < rows.length; i++){
                if(rows[i].partyCover != null){
                    rows[i].partyCover = null;
                }
            }
            res.send(rows);
        }*/
        if (!err) {
            var byteArray = [];
            if(rows.length == 0){
                res.send([]);
            }
            else{
                for(var i = 0; i < rows.length; i++){
                 if(rows[i].partyCover != null){
                       rows[i].partyCover = JSON.parse('['+rows[i].partyCover+']');
                 }
             }
             res.send(rows);
            }
        }
        else {
            console.log("query error :" + err);
        }
    });
});

app.get("/get_chatroomlist", function (req, res) {
    var userId = req.query.user_id;
    if (userId == -1) {
        userId = 34;
    }
    console.log(userId);
    console.log('채팅방 가져오기 ' + userId);
    connection.query('select userPartyIdList from user where userId = ' + userId + '', function (err, rows, fields) {
        if (!err) {
            var partyList = [];
            var party, chatroomid;
            var list = JSON.stringify(rows[0]);
            var obj = JSON.parse(list);
            var idList = JSON.parse(obj.userPartyIdList);
            console.log('파티 아이디 리스트 ' + idList);

            for (var index = 0; index < idList.length; index++) {
                connection.query("select * from party where partyId = " + idList[index] + "", function (err, rows, fields) {
                    chatroomid = rows[0].partyId;
                    if(rows[0].partyCover != null){
                        rows[0].partyCover = JSON.parse('['+rows[0].partyCover+']');
                    }
                    party = { "roomId": chatroomid, "partyCover": rows[0].partyCover, "partyLeader": rows[0].partyLeader, "roomName": rows[0].partyName, "capacityMax": rows[0].capacityMax, "capacityCurrent": rows[0].capacityCurrent, "status": rows[0].partyStatus };
                    partyList.push(party);
                    if (chatroomid == idList[idList.length - 1]) {
                        console.log(partyList);
                        res.send(partyList);
                    }
                });
            }
        }
        else {
            console.log('query error : ' + err);
        }
    });
});

app.get("/get_chatroom_userlist", function (req, res) {
    var roomId = req.query.room_id;
    var userList = [];
    // console.log(roomId);
    connection.query('select partyMemberList from party where partyId = ' + roomId + '', function (err, rows, fields) {
        if (!err) {
            var list = [];
            list = JSON.parse(rows[0].partyMemberList);
            // console.log(list);
            for (var i = 0; i < list.length; i++) {
                connection.query('select userId,userName from user where userId = ' + list[i] + '', function (err, rows, fields) {

                    var item = JSON.stringify(rows[0]);
                    var realItem = JSON.parse(item);
                    userList.push(realItem);

                    if (userList.length == list.length) {
                        console.log(userList);
                        res.send(userList);
                    }
                });
            }
        }
    });
    // console.log(userList);
});

app.post("/leave_chat_room", function(req,res){

    
    var userId = req.query.user_id;
    var roomId = req.query.room_id;
    connection.query('select capacityCurrent, partyMemberList from party where partyId = ' + roomId + '', function (err, rows, fields) {
        var memberList = JSON.parse(rows[0].partyMemberList);
        var capacityCurrent = JSON.parse(rows[0].capacityCurrent);
        var index;
        for(var index =0; index<memberList.length; index++){
            if(memberList[index] == userId){
                memberList.splice(index,1);
                break;
            }
        }
        var updatedMemberList = JSON.stringify(memberList);
        connection.query("update party set partyMemberList = '"+updatedMemberList+"' where partyId = " + roomId + '', function (err, rows, fields){
            if(!err){
                console.log("방나가기 성공");
                // res.send(null);
                updateUserPartyIdList(userId, roomId);
                res.send(null);
                // console.log(userPartyIdList);
                // res.send(null);
            }
            else{

                console.log(err);
                console.log("방나가기 실패")
            }
        });
        connection.query('update party set capacityCurrent = ' + (capacityCurrent - 1) + ' where partyId = ' + roomId + '', function (err, rows, fields) {
            if (!err) {

            }
            else {
                console.log('query error : ' + err);
            }
        });
    });
});
function updateUserPartyIdList(userId, roomId){
    connection.query('select userPartyIdList from user where userId = ' + userId + '', function (err, rows, fields){   
    if(!err){
        var partyList = JSON.parse(rows[0].userPartyIdList);
        var index;
        for(var index =0; index<partyList.length; index++){
            if(partyList[index] == roomId){
                partyList.splice(index,1);
                break;
            }
        }
        var updatedPartyList = JSON.stringify(partyList);
        connection.query("update user set userPartyIdList = '"+updatedPartyList+"' where userId = " + userId + '', function (err, rows, fields){
            if(!err){
                console.log("유저 테이블 업데이트 성공");
            }
            else{
                console.log("유저 테이블 업데이트 실패");
            }
        });
    }
    else{
        return null;
    }
    }); 
}

app.post("/response_party_request", function(req,res){
    var acceptance = req.query.acceptance;
    console.log("ACCEPTANCE: "+ acceptance);
    var userId = req.query.user_id;
    var partyId = req.query.party_id;
    
    /**************PUSH******************/
    connection.query('select token from user where userId =' + userId + '', function (err, rows, fields) {
        var token = rows[0].token;
        connection.query("select * from party where partyId = " + partyId + "", function (err, rows, fields) {
            chatroomid = rows[0].partyId;
             party = { "roomId": chatroomid, "image": "", "roomName": rows[0].partyName, "capacityMax": rows[0].capacityMax, "capacityCurrent": rows[0].capacityCurrent, "partyLeader": rows[0].partyLeader };
            if(acceptance == 'true'){
                connection.query('select capacityMax, capacityCurrent from party where partyId ='+partyId+'', function(err, rows, fields){
                    var thisParty = rows[0];
            
                    if(thisParty.capacityCurrent < thisParty.capacityMax){
                        push_newRoom(party, token);
                    }
                });
            }
            else{
                rejectPartyRequest(party,token);
            }
        });
    });
    /********************************/
    if(acceptance == 'true'){
        connection.query('select capacityMax, capacityCurrent from party where partyId ='+partyId+'', function(err, rows, fields){
            var party = rows[0];
    
            if(party.capacityCurrent < party.capacityMax){
                connection.query('select capacityCurrent, partyMemberList from party where partyId =' + partyId + '', function (err, rows, fields) {
                    if (!err) {
                        var partyMemberList = JSON.parse(rows[0].partyMemberList);
                        var capacityCurrent = JSON.parse(rows[0].capacityCurrent);
                        console.log(capacityCurrent + 1);
                        partyMemberList.push(userId);
                        connection.query('update party set partyMemberList = "[' + partyMemberList + ']" where partyId = ' + partyId + '', function (err, rows, fields) {
                            if (!err) {
                                console.log('파티 멤버 리스트에 등록 완료!');
                            }
                            else {
                                console.log('query error : ' + err);
                            }
                        });
                        connection.query('update party set capacityCurrent = ' + (capacityCurrent + 1) + ' where partyId = ' + partyId + '', function (err, rows, fields) {
                            if (!err) {
                                // connection.query('select capacityMax from party where partyId =' + partyId + '',function(err, rows, fields){
                                //     if(rows[0].capacityMax == capacityCurrent+1){
                                //       updatePartyStatus(partyId, 'Closed');                                
                                //     }
                                // });
                            }
                            else {
                                console.log('query error : ' + err);
                            }
                        });
                    }
                });
            
                connection.query('select userPartyIdList from user where userId = ' + userId + '', function (err, rows, fields) {
                    if (!err) {
                        var partyList = [];
                        var party, chatroomid;
                        var list = JSON.stringify(rows[0]);
                        var obj = JSON.parse(list);
                        var idList = JSON.parse(obj.userPartyIdList);
                        idList.push(partyId);
                        connection.query("update user set userPartyIdList = '[" + idList + "]' where userId = " + userId + "", function (err, rows, fields) {
                            if (!err) {
                                console.log('파티 참가 완료!');
                            }
                            else {
                                console.log('query error : ' + err);
                            }
                        });
                    }
                    else {
                        console.log('query error : ' + err);
                    }
                });
            }
        });
    }
    
    connection.query('select capacityMax, capacityCurrent from party where partyId ='+partyId+'', function(err, rows, fields){
        var party = rows[0];

        if(party.capacityCurrent < party.capacityMax){
            connection.query('select waitingPartyList from user where userId = '+userId+'', function(err, rows, fields){
                var waitingList = JSON.parse(rows[0].waitingPartyList);
    
                for (var i = 0; i < waitingList.length; i++) {
                    if (waitingList[i] == partyId) {
                        waitingList.splice(i, 1);
                        break;
                    }
                }
    
                connection.query('update user set waitingPartyList = "['+waitingList+']" where userId = '+userId+'', function(err, rows, fields){
                    if(err){
                        console.log('waitingPartyList 삭제 에러 : ' + err);
                    }
                });
            });
    
            connection.query('select waitingMemberList from party where partyId = '+partyId+'', function(err, rows, fields){
                var memberList = JSON.parse(rows[0].waitingMemberList);
    
                for (var j = 0; j < memberList.length; j++) {
                    if (memberList[j] == userId) {
                        memberList.splice(j, 1);
                        break;
                    }
                }
    
                connection.query('update party set waitingMemberList = "['+memberList+']" where partyId = '+partyId+'', function(err, rows, fields){
                    if(!err){
                        console.log("RESSEND");
                        res.send(null);
                    }
                    else{
                        console.log('waitingMemberList 삭제 에러 : ' + err);
                    }
                });
            });
        }
    });
});

function pushJoinRequest_func(partyId){
    connection.query('select partyLeader from party where partyId =' + partyId + '', function (err, rows, fields) {
        var partyLeader = JSON.parse(rows[0].partyLeader);
        connection.query('select token from user where userId =' + partyLeader + '', function (err, rows, fields) {
            var leaderToken = rows[0].token;
            push_joinRequest(leaderToken);
        });
    });
}

app.post("/join_party", function (req, res) {
    var userId = req.query.user_id;
    if (userId == -1) {
        userId = 34;
    }
    console.log(userId);
    var partyId = req.query.party_id;
    var partyIdList;
    var duplicated = 0;

    /**************PUSH***************/


    /******************************/
    
    //모임 중복참가 처리해야함
    //추후 대기리스트에 추가하는 부분으로 바꿔줘야함
    //모임 중복참가 체크
    connection.query('select userPartyIdList from user where userId = '+userId+'', function(err, rows, fields){
        var partyIdList = JSON.parse(rows[0].userPartyIdList);

        for(var partyIndex = 0; partyIndex < partyIdList.length; partyIndex++){
            if(partyIdList[partyIndex] == partyId){
                duplicated = 1;
            }
        }
        connection.query('select waitingPartyList from user where userId = '+userId+'', function(err, rows, fields){
            var waitingList = JSON.parse(rows[0].waitingPartyList);
    
            for(var listIndex = 0; listIndex < waitingList.length; listIndex++){
                if(waitingList[listIndex] == partyId){
                    duplicated = 1;
                }
            }

            if(duplicated == 1){
                console.log('중복 신청입니다.');
                res.send({joinResult : 2}); 
            }
            else{
                connection.query('select capacityMax, capacityCurrent from party where partyId ='+partyId+'', function(err, rows, fields){
                    var party = rows[0];
            
                    if(party.capacityCurrent >= party.capacityMax){
                        console.log('파티에 참가할수 없습니다.');
                        res.send({joinResult : 3});
                    }
                    else{
                        pushJoinRequest_func(partyId);
                        res.send({joinResult : 1});
            
                        connection.query('select waitingPartyList from user where userId = '+userId+'', function(err, rows, fields){
                            var waitingList = JSON.parse(rows[0].waitingPartyList);
                            waitingList.push(partyId);
            
                            connection.query('update user set waitingPartyList = "['+waitingList+']" where userId = '+userId+'', function(err, rows, fields){
                                if(err){
                                  console.log('query error : ' + err);
                                }
                            });
                        });
            
                        connection.query('select waitingMemberList from party where partyId ='+partyId+'', function(err, rows, fields){
                            var memberList = JSON.parse(rows[0].waitingMemberList);
                            memberList.push(userId);
            
                            connection.query('update party set waitingMemberList = "['+memberList+']" where partyId = '+partyId+'', function(err, rows, fields){
                                if(err){
                                    console.log('query error : ' + err);
                                }
                            });
                        });
                    }
                });
            }
        });
    });
});

app.post("/write_review", function (req, res) {
    var writer = req.body.reviewWriter;
    var target = req.body.reviewTarget;
    var partyId = req.body.partyId;
    var content = req.body.content;
    var rating = req.body.rating;
    var timestamp = req.body.timestamp;
    console.log(content);

    connection.query('INSERT INTO review(reviewWriter, reviewTarget, partyId, content, rating, timestamp) values ("' + writer + '","' + target + '","' + partyId + '","' + content + '","' + rating + '","' + timestamp + '")', function (err, rows, fields) {
        if (!err) {
            var response = { confirm: "Success" };
            res.send(response);
        }
        else {
            console.log('query error : ' + err);
            var failResponse = { confirm: "fail" };
            res.send(failResponse);
        }
    });
});

app.post("/report_review", function(req, res){
    var reviewIndex = req.query.review_index;

    connection.query('update review set isReported = 1 where reviewIndex = '+reviewIndex+'', function(err, rows, fields){
        if(!err){
            console.log('리뷰 신고가 접수되었습니다.');
            res.send(null);
        }
        else{
            console.log('query error : ' + err);
        }
    });
});

app.get("/my_review", function (req, res) {
    var userId = req.query.user_id;

    connection.query('select * from review where reviewTarget = ' + userId + '', function (err, rows, fields) {
        if (!err) {
            res.send(rows);
        }
        else {
            console.log('query error : ' + err);
        }
    });
});

app.get("/get_waiting_party_list", function(req, res){
    var userId = req.query.user_id;
    var waitingList = [];

    connection.query('select waitingPartyList from user where userId = '+userId+'', function(err, rows, fields){
        var partyList = JSON.parse(rows[0].waitingPartyList);

        if(partyList.length == 0){
            res.send(waitingList);
        }

        for(var i = 0; i < partyList.length; i++){
            connection.query('select * from party where partyId = '+partyList[i]+'', function(err, rows, fields){
                var party = rows[0];
                if(party.partyCover != null){
                    party.partyCover = JSON.parse('['+party.partyCover+']');
                }

                waitingList.push(party);
                if(party.partyId == partyList[partyList.length-1]){
                    res.send(waitingList);
                }
            });
        }
    });
});
app.post("/close_party", function(req,res){
    var partyId = req.query.party_id;
    var status = 'Closed';
    connection.query('update party set partyStatus = "'+status+'" where partyId = '+partyId+'', function(err, rows, fields){
        if(!err){
            console.log("PARTY STATUS 업데이트 성공");
            res.send(null);
        }
        else{
            console.log("PARTY STATUS 업데이트 실패");
        }
    });                            
});
app.get("/get_request_list", function(req, res){
    var userId = req.query.user_id;
    var requestList = [];

    connection.query('select mLeaderPartyList from user where userId = '+userId+'', function(err, rows, fields){
        var partyList = JSON.parse(rows[0].mLeaderPartyList);

        if(partyList.length == 0){
            res.send(requestList);
        }
        for(var partyIndex = 0; partyIndex < partyList.length; partyIndex++){
            connection.query('select * from party where partyId = '+partyList[partyIndex]+'', function(err, rows, fields){
                var party = rows[0];
                if(party.partyCover != null){
                    party.partyCover = JSON.parse('['+party.partyCover+']');
                }
                var waitingMemberList = JSON.parse(rows[0].waitingMemberList);
    
                for(var memberIndex = 0; memberIndex < waitingMemberList.length; memberIndex++){
                    var request = {requestUserId : waitingMemberList[memberIndex], party : party};
                    requestList.push(request);
                }
                
                if(party.partyId == partyList[partyList.length-1]){
                    res.send(requestList);
                }
            });
        }
    });
});

app.get("/get_joined_party_list", function(req ,res){
    var userId = req.query.user_id;
    var joinedPartyList = [];
    
    connection.query('select userPartyIdList from user where userId = '+userId+'', function(err, rows, fields){
       var partyList = JSON.parse(rows[0].userPartyIdList);

        if(partyList.length == 0){
            res.send(joinedPartyList);
        }
       
       for(var i = 0 ; i < partyList.length; i++){
            connection.query('select * from party where partyId ='+partyList[i]+'', function(err, rows, fields){
                var party = rows[0];
                if(party.partyCover != null){
                    party.partyCover = JSON.parse('['+party.partyCover+']');
                }
                joinedPartyList.push(party);
                
                if(party.partyId == partyList[partyList.length-1]){
                    res.send(joinedPartyList);
                }
            });
       }
    }); 
});

app.post("/cancel_join_party", function(req, res){
    var userId = req.query.user_id;
    var partyId = req.query.party_id;

    connection.query('select waitingPartyList from user where userId = '+userId+'', function(err, rows, fields){
        var waitingList = JSON.parse(rows[0].waitingPartyList);

        for (var i = 0; i < waitingList.length; i++) {
            if (waitingList[i] == partyId) {
                waitingList.splice(i, 1);
                break;
            }
        }

        connection.query('update user set waitingPartyList = "['+waitingList+']" where userId = '+userId+'', function(err, rows, fields){
            if(err){
                console.log('waitingPartyList 삭제 에러 : ' + err);
            }
        });
        res.send(null);
    });

    connection.query('select waitingMemberList from party where partyId = '+partyId+'', function(err, rows, fields){
        var memberList = JSON.parse(rows[0].waitingMemberList);

        for (var j = 0; j < memberList.length; j++) {
            if (memberList[j] == userId) {
                memberList.splice(j, 1);
                break;
            }
        }

        connection.query('update party set waitingMemberList = "['+memberList+']" where partyId = '+partyId+'', function(err, rows, fields){
            if(err){
                console.log('waitingMemberList 삭제 에러 : ' + err);
            }
        });
    });
});

app.get("/get_appointment_point", function(req,res){
    var roomId = req.query.room_id;
    connection.query('select latitude,longitude from chatroom where party_id = '+roomId+'', function(err, rows, fields){  
        if(!err){
        console.log(rows[0]);
        res.send(rows[0]);
    }
    else{
        console.log("error getting appointment Point");
    }
    });  
});
app.post("/post_appointment_point", function(req,res){
    var roomId = req.query.room_id;
    var latitude = req.query.latitude;
    var longitude = req.query.longitude;
    connection.query('update chatroom set latitude = ' + latitude +' where party_id = '+roomId+'', function(err, rows, fields){
        if(!err){
            connection.query('update chatroom set longitude = ' + longitude +' where party_id = '+roomId+'', function(err, rows, fields){
                if(!err){
                    res.send(null);
                }
            });
        }
    });
    

});

app.post("/write_report", function(req,res){
    var report_writer = req.body.report_writer;
    var report_target = req.body.report_target;
    var partyId = req.body.partyId;
    var content = req.body.content;
    var timestamp = req.body.timestamp;

    console.log(content);

    connection.query("INSERT INTO report(report_writer, report_target, party_id, content, timestamp) values (" + report_writer + "," + report_target + "," + partyId + ",'" + content + "','" + timestamp + "')", function (err, rows, fields) {
        if (!err) {
            res.send(null);
            reportUser_func(report_target);
        }
        else {
            console.log('신고 에러 : ' + err);
        }
    });
});

function reportUser_func(targetId){
    connection.query('select token from user where userId =' + targetId + '', function (err, rows, fields) {
        var token = rows[0].token;
        push_reportUser(token);
    });
}

//유저 신고 푸쉬 write_report
function push_reportUser(client_token) {
    //파티 수락시
    var push_reportUser = {
        // 수신대상
        to: client_token,
        // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
        // 메시지 중요도
        priority: "high",
        // App 패키지 이름
        restricted_package_name: "xyz.capsaicine.freeperiod",
        // App에게 전달할 데이터
        data: {
            type: "reportUser",
            title: "유저로부터 신고를 당하셨습니다.",
        }
    };

    fcm.send(push_reportUser, function (err, response) {
        if (err) {
            console.error('Push메시지 발송에 실패했습니다.');
            console.error(err);
            return;
        }

        console.log('Push메시지가 발송되었습니다.');
        console.log(response);
    });
}
//참가 신청 푸쉬 join_room
function push_joinRequest(client_token) {
    //파티 수락시
    var push_joinRequest = {
        // 수신대상
        to: client_token,
        // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
        // 메시지 중요도
        priority: "high",
        // App 패키지 이름
        restricted_package_name: "xyz.capsaicine.freeperiod",
        // App에게 전달할 데이터
        data: {
            type: "joinRequest",
            title: "새로운 유저가 참가신청을 했습니다",
        }
    };

    fcm.send(push_joinRequest, function (err, response) {
        if (err) {
            console.error('Push메시지 발송에 실패했습니다.');
            console.error(err);
            return;
        }

        console.log('Push메시지가 발송되었습니다.');
        console.log(response);
    });

}

io.sockets.on('connection', function (socket) {
    console.log('user connected: ', socket.id);  //3-1
    socket.on('socketID', function (text) {
        socket.ID = text.userID;
        // console.log(text);
        //해당 SOCKET.NAME에 해당하는 USER의 SOCKET STATUS 상태를 ON(1)으로 만들어야함
        connection.query('update user set socket_status = ' + true + ' where userId = ' + socket.ID +
            '', function (err, rows, fields) {
                if (!err) {
                    console.log('소켓 연결 확인!');
                }
                else {
                    console.log('query error: ' + err);
                }
            });

    });
    socket.on('connectRoom', function (text) {

        var room = text.roomId;
        // socket.name = text.userID;
        console.log("userId:" + socket.ID + " joined " + room);
        socket.join(room);
    });

    socket.on('message', function (text) { //3-3
        var message = JSON.stringify(text);
        var msg = message;
        var room = text.roomId;

        //client_token 
        connection.query('select partyMemberList from party where partyId = ' + room + '', function (err, rows, fields) {
            if (!err) {
                var idList = JSON.parse(rows[0].partyMemberList);
                for (var i = 0; i < idList.length; i++) {
                    connection.query('select socket_status,token from user where userId = ' + idList[i] + '', function (err, rows, fields) {
                        if (!err) {
                            console.log(rows[0].socket_status);
                            if (!rows[0].socket_status) {
                                var client_token = rows[0].token;
                                sendNewMsg(msg, client_token);
                            }
                        }
                    });
                }
            }
        });

        io.sockets.in(room).emit('receiveMsg', text);
        console.log("Send Back");
    });

    socket.on('notification', function (text) { //3-3
        var message = JSON.stringify(text);
        var msg = message;

        var room = text.roomId;
        console.log("Received" + msg);
        io.sockets.in(room).emit('receiveNotification', text);
        console.log("Send Back");
    });

    socket.on('leaveRoom', function (text) {
        var room = text.roomId;
        socket.leave(room);
    });


    socket.on('disconnect', function () { //3-2
        console.log('user disconnected: ', socket.ID);

        connection.query('update user set socket_status = ' + false + ' where userId = ' + socket.ID +
            '', function (err, rows, fields) {
                if (!err) {
                    console.log('소켓 끊어짐!');
                }
                else {
                    console.log('query error: ' + err);
                }
            });
        //USERID = SOCKET.ID
        //USERDB에서 SOCKET.NAME을 ID로 갖는 USER의 Socket.Connection CLOSE로 바꿈
    });
});

function push_newRoom(content, client_token) {
    //파티 수락시
    var push_newRoom = {
        // 수신대상
        to: client_token,
        // App이 실행중이지 않을 때 상태바 알림으로 등록할 내용
        // 메시지 중요도
        priority: "high",
        // App 패키지 이름
        restricted_package_name: "xyz.capsaicine.freeperiod",
        // App에게 전달할 데이터
        data: {
            type: "enterChatRoom",
            title: "새로운 채팅방에 참가하셨습니다",
            data: content
        }
    };

    fcm.send(push_newRoom, function (err, response) {
        if (err) {
            console.error('Push메시지 발송에 실패했습니다.');
            console.error(err);
            return;
        }

        console.log('Push메시지가 발송되었습니다.');
        console.log(response);
    });

}

function sendNewMsg(msg, client_token) {
    var push_newMsg = {
        to: client_token,
        // 메시지 중요도
        priority: "high",
        // App 패키지 이름
        restricted_package_name: "xyz.capsaicine.freeperiod",
        // App에게 전달할 데이터
        data: {
            type: "newMessage",
            title: "새로운 메세지가 도착했습니다",
            data: msg
        }
    }

    fcm.send(push_newMsg, function (err, response) {
        if (err) {
            console.error('Push메시지 발송에 실패했습니다.');
            console.error(err);
            return;
        }

        console.log('Push메시지가 발송되었습니다.');
        console.log(response);
    });
}

function rejectPartyRequest(content,client_token) {
    var push_newMsg = {
        to: client_token,
        // 메시지 중요도
        priority: "high",
        // App 패키지 이름
        restricted_package_name: "xyz.capsaicine.freeperiod",
        // App에게 전달할 데이터
        data: {
            type: "rejectPartyRequest",
            title: "파티 참가 요청이 거절되셨습니다",
            data: content
        }
    }
    fcm.send(push_newMsg, function (err, response) {
        if (err) {
            console.error('Push메시지 발송에 실패했습니다.');
            console.error(err);
            return;
        }

        console.log('Push메시지가 발송되었습니다.');
        console.log(response);
    });
}

function checkJoinPartyAvailable(partyId){
    connection.query('select capacityMax, capacityCurrent from party where partyId ='+partyId+'', function(err, rows, fields){
        var party = rows[0];

        if(party.capacityCurrent >= party.capacityMax){
            return false;
        }
        else{
            return true;
        }
    });
}

//app.listen(8000, function () {
//    console.log("express start at port : %d", 8000);
//});

http.listen(8000, function () {
    console.log("express start at port : %d", 8000);
});

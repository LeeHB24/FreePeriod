
# coding=utf8
from flask import Flask, flash, redirect, render_template, request, session, abort, url_for
from flask_restful import Resource, Api
from flask_restful import reqparse
from flaskext.mysql import MySQL
import urllib
import httplib
import os
import pymysql.cursors
import json

conn = pymysql.connect(host = 'localhost',
        user = 'fanta', password = '4628', db='hwan', charset='utf8mb4')

app = Flask(__name__)
api = Api(app)


@app.route('/ad_register') 
def register_page():
    return render_template('ad_register.html')

@app.route('/ad_register_request', methods=['POST'])
def register_request():
    if request.method == 'POST':
        email = request.form['email']
        ad_content = request.form['ad_content']
        print "content : " + email + " " + ad_content

        # TODO store advertisement info to DB server of fanta

    return redirect(url_for('register_success'))

@app.route('/ad_register_success')
def register_success():
    return render_template('ad_register_success.html')

@app.route('/User')
@app.route('/User/<content_of_users>')
def userMaintenance_page(content_of_users=None):
    try:
        with conn.cursor() as cursor:
            sql = 'SELECT userId, userName, userEmail, userSchool, userReportedCount, userRatingAverage FROM user'
            cursor.execute(sql)
            result = cursor.fetchall()
            cursor.close()
    finally:
        print "fetching user complete!"

    return render_template('User.html', content_of_users=result)
 
@app.route('/update_user', methods=['POST'])
def update_user():
    cursor2=conn.cursor()
    email = request.form['email']
    sql="UPDATE user SET userAuth = %s WHERE userEmail = %s"
    val=("-1",email)

    cursor2.execute(sql,val)
    cursor2.close
    return render_template('index.html')




@app.route('/show_review_popup', methods=["POST"])
def showreviewPopup():
    user_id = request.form['param1']
    print user_id
    sql = "SELECT content FROM review WHERE reviewTarget = " + str(user_id)
    print sql
    cursor=conn.cursor()
    cursor.execute(sql)
    result = cursor.fetchall()
    return json.dumps({"result":result})
    # return ren''request.form['user_id']
    #print "asdasd"+user_ids

# @app.route('/show_review')
def show_review(user_id):
    cursor=conn.cursor()

    # user_id = request.form['user_id']
    #print "asdasd"+user_ids
    sql = "SELECT content FROM review WHERE reviewTarget = " + str(user_id)
    cursor.execute(sql)
    result = cursor.fetchall()
    
    return render_template('review.html',content_of_reviews=result)

@app.route('/lecture')
@app.route('/lecture/<content_of_lectures>')
def lectureMaintenance_page(content_of_lectures=None):
    try:
        with conn.cursor() as cursor:
            sql = 'SELECT * FROM lecture'
            cursor.execute(sql)
            result = cursor.fetchall()
            cursor.close()
    finally:
        print "fetching lecture complete!"
    return render_template('lecture.html', content_of_lectures=result)

@app.route('/add_lecture_popup')
def addLecturePopup():
    return render_template('add_lecture_popup.html')

@app.route('/addLectureToDB', methods=['POST'])
def addLectureToDB():
    if request.method == "POST":
        data = (request.form['lecture']).encode('utf-8')
        try:
            with conn.cursor() as cursor:
                sql = 'INSERT INTO lecture(lecture_info) VALUES (\'' + str(data) + '\')'
                cursor.execute(sql)
                conn.commit()
                cursor.close()
                
        finally:
            print "insert lecture complete!"

    return data

@app.route('/deleteLectureFromDB', methods=['POST'])
def deleteLectureFromDB():
    if request.method == "POST":
        data = (request.form['lectureId'])
        try:
            with conn.cursor() as cursor:
                sql = 'DELETE FROM lecture WHERE id=' + str(data)
                print sql
                cursor.execute(sql)
                conn.commit()
                cursor.close()

        finally:
            print "delete lecture complete!"

    return data

@app.route('/login',methods=['GET','POST'])
def do_admin_login():
    if request.method == "POST":
        if not session.get('logged_in'):
            if request.form['password'] == 'a' and request.form['username'] =='admin':
                session['logged_in'] = True
            else:
                flash('wrong password!')
        else:
            "Hi Boss! <a href='/logout'>Logout</a>"
    return home()

@app.route("/logout")
def logout():
    session['logged_in'] = False
    return home()

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/index') 
def home():
    if not session.get('logged_in'):
        return render_template('login.html')
    else:
        return render_template('index.html')
 
if __name__=='__main__':
    app.secret_key = os.urandom(12)
    # test_api('49.236.132.218', 8000, "JuneKyu")
    app.run(host = '0.0.0.0', port=8080, debug=True)


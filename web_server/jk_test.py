# -*- coding: utf-8 -*-

from flask import Flask, flash, redirect, render_template, request, session, abort, url_for
from flask_restful import Resource, Api
from flask_restful import reqparse
from flaskext.mysql import MySQL
import urllib
import httplib
import os
import pymysql.cursors
conn = pymysql.connect(host = 'localhost',
        user = 'fanta', password = '4628', db='hwan', charset='utf8mb4')

app = Flask(__name__)
api = Api(app)
# mysql = MySQL()
# app.config['MYSQL_DATABASE_USER']='fanta'
# app.config['MYSQL_DATABASE_PASSWORD']=''

# def test_api(host, port, userid):
#     params = urllib.urlencode({'userid': userid})
#     headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
#     conn = httplib.HTTPSConnection(host = host, port = port)
#     conn.request("POST", "/ad_register", params, headers)
#     response = conn.getresponse()
#     data = response.read()
#     conn.close()
    # rdict = json.loads(data)
    # return rdict

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
def userMaintenance_page():
    try:
        with conn.cursor() as cursor:
            sql = 'SELECT userName, userEmail, userSchool, userReportedCount, userRatingAverage FROM user'
            cursor.execute(sql)
            result = cursor.fetchall()
            print(result[0])
    finally:
        print('fetching user complete!')

    return render_template('User.html')

@app.route('/lecture')
def lectureMaintenance_page(content_of_lectures=None):
    try:
        with conn.cursor() as cursor:
            sql = 'SELECT * FROM lecture'
            cursor.execute(sql)
            result = cursor.fetchall()
            print result
    finally:
        print('fetching lecture complete!')

    return render_template('lecture.html', content_of_lectures=result)

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
    app.run(host = '0.0.0.0', port=8008, debug=True)

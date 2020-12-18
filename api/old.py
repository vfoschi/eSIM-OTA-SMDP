import time
from flask import Flask
from flask import request
# from http.server import BaseHTTPRequestHandler, HTTPServer
import logging
import ssl
import json
import ecdsa
import hashlib
import secrets
import asn1tools
import base64
import binascii
import pymysql

app = Flask(__name__)

validTables = ['users', 'pdn', 'updatequeue']

@app.route('/gsma/rsp2/es9plus/initiateAuthentication', methods=['POST'])
def initiateAuthentication():
    obj = request.get_json()
    print('obj in initiateAuthentication:')
    print(obj)
    return 

# dump all data in a table
@app.route('/api/db/table/<string:tablename>')
def log_all(tablename):
    if(tablename not in validTables):
        raise Exception("nonexistent tablename")
    return log_All(tablename)

# get all column names of a table
@app.route('/api/db/columns/<string:tablename>')
def columns(tablename):
    if(tablename not in validTables):
        raise Exception("nonexistent tablename")
    return get_Columns(tablename)

# create a user in DB
@app.route('/api/db/adduser', methods=['POST'])
def add_user():
    obj = request.get_json()
    # if not request.form['imsi'] or not request.form['msisdn'] or not request.form['imei']:
    if 'imsi' not in obj or 'msisdn' not in obj or 'imei' not in obj or 'sqn' not in obj or 'rand' not in obj or 'active' not in obj or 'location' not in obj:
        print('missing arguments')
        return "error"
    return add_User(obj['imsi'], obj['msisdn'], obj['imei'], obj['active'] if obj['active'] else '0', obj['location'], obj['sqn'], obj['rand'])

# delete a user from DB
@app.route('/api/db/deleteuser', methods=['POST'])
def delete_user():
    obj = request.get_json()
    # if not request.form['imsi'] or not request.form['msisdn'] or not request.form['imei']:
    if 'imsi' not in obj:
        print('missing arguments')
        return "error"
    return delete_User(obj['imsi'])

# update a user in DB
@app.route('/api/db/updateuser', methods=['POST'])
def update_user():
    obj = request.get_json()
    if 'imsi' not in obj:
        print('missing imsi')
        return "error"
    imsi = obj['imsi']
    imei = obj['imei'] if 'imei' in obj else ''
    active = obj['active'] if 'active' in obj else ''
    location = obj['location'] if 'location' in obj else ''
    ue_ambr_ul = obj['ue_ambr_ul'] if 'ue_ambr_ul' in obj else ''
    updateObj = {'imsi': imsi, 'imei': imei,
                 'active': active, 'location': location, 'ue_ambr_ul': ue_ambr_ul}
    print(updateObj)
    return update_User(imsi, updateObj)

# use reaches this to retrieve its updates
@app.route('/api/updatequeue/<string:imsi>', methods=['GET', 'POST'])
def retrieve_from_update_queue(imsi):
    result = retrieve_From_Update_Queue(imsi)
    print('result is:')
    print(result)
    if len(result) == 0:
        return json.dumps(result)
    mark_Retrieved_In_Update_Queue(imsi, time.strftime('%Y-%m-%d %H:%M:%S'))
    apply_DB(imsi, result[-1])
    return json.dumps(result)

# admin reaches this to queue a change to a user
@app.route('/api/db/updatequeue', methods=['POST'])
def insert_update_queue():
    obj = request.get_json()
    if 'imsi' not in obj:
        print('missing imsi')
        return "error"
    imsi = obj['imsi']
    imei = obj['imei'] if 'imei' in obj else ''
    active = obj['active'] if 'active' in obj else ''
    location = obj['location'] if 'location' in obj else ''
    ue_ambr_ul = obj['ue_ambr_ul'] if 'ue_ambr_ul' in obj else ''
    addedtime = time.strftime('%Y-%m-%d %H:%M:%S')
    return insert_Update_Queue(imsi, active, location, ue_ambr_ul, addedtime)

# set all imsis in a location active/inactive
@app.route('/api/db/locationactive', methods=['POST'])
def location_active():
    obj = request.get_json()
    if 'location' not in obj:
        print('missing location')
        return "error"
    location = obj['location']
    active = obj['active'] if obj['active'] else ''
    ue_ambr_ul = obj['ue_ambr_ul'] if obj['ue_ambr_ul'] else ''
    return location_Active(location, active, ue_ambr_ul)


def connect_to_db():
    return pymysql.connect("localhost", "root", "asddfdlxx", "esim")


def log_All(tablename):
    tableData = []
    connection = connect_to_db()
    try:
        with connection.cursor() as cursor:
            # columns = get_Columns('users')
            # query = ''
            # for col in columns:
            # query += "\'%s\', %s, " % (col, col)
            # cursor.execute("SELECT JSON_ARRAYAGG(JSON_OBJECT(%s)) from users" % query)
            cursor.execute("SELECT * from %s" % tablename)
            results = cursor.fetchall()
            for i in range(len(results)):
                tableData.append([])
                for col in results[i]:
                    tableData[i].append(str(col))
    finally:
        connection.close()
    return json.dumps(tableData)


def get_Columns(tablename):
    columns = []
    connection = connect_to_db()
    try:
        with connection.cursor() as cursor:
            cursor.execute("SHOW COLUMNS FROM %s" % tablename)
            results = cursor.fetchall()
            for row in results:
                columns.append(row[0])
            # converted_list = [str(element) for element in columns]
            # columns = ",".join(converted_list)
    finally:
        connection.close()
    return json.dumps(columns)


def add_User(imsi, msisdn, imei, active, location, sqn, rand):
    connection = connect_to_db()
    failed = False
    try:
        with connection.cursor() as cursor:
            cursor.execute(
                "INSERT INTO `users` (`imsi`, `msisdn`, `imei`, `active`, `location`, `sqn`, `rand`) VALUES (%s, %s, %s, %s, %s, %s, %s)", (imsi, msisdn, imei, active, location, sqn, rand))
        connection.commit()
    except Exception as inst:
        print_inst(inst)
        failed = True
    finally:
        connection.close()
    return 'error' if failed else 'success'


def delete_User(imsi):
    connection = connect_to_db()
    failed = False
    try:
        with connection.cursor() as cursor:
            cursor.execute(
                "DELETE FROM `users` WHERE `imsi` = %s", (imsi,))
        connection.commit()
    except Exception as inst:
        print_inst(inst)
        failed = True
    finally:
        connection.close()
    return 'error' if failed else 'success'


def update_User(imsi, updateObj):
    connection = connect_to_db()
    failed = False
    newImei = updateObj['imei']
    newActive = updateObj['active']
    newLocation = updateObj['location']
    newAMBRUL = updateObj['ue_ambr_ul']
    try:
        with connection.cursor() as cursor:
            if newImei and newImei != 'None':
                cursor.execute(
                    "UPDATE `users` SET `imei` = %s WHERE `imsi` = %s", (newImei, imsi))
            if (newActive and newActive != "None") or newActive == 0:
                cursor.execute(
                    "UPDATE `users` SET `active` = %s WHERE `imsi` = %s", (newActive, imsi))
            if newLocation and newLocation != 'None':
                cursor.execute(
                    "UPDATE `users` SET `location` = %s WHERE `imsi` = %s", (newLocation, imsi))
            if newAMBRUL and newAMBRUL != 'None':
                cursor.execute(
                    "UPDATE `users` SET `ue_ambr_ul` = %s WHERE `imsi` = %s", (newAMBRUL, imsi))
        connection.commit()
    except Exception as inst:
        print_inst(inst)
        failed = True
    finally:
        connection.close()
    return 'error' if failed else 'success'

# insert into updatequeue table
def insert_Update_Queue(imsi, active, location, ue_ambr_ul, addedtime):
    connection = connect_to_db()
    failed = False
    try:
        with connection.cursor() as cursor:
            cursor.execute(
                "INSERT INTO `updatequeue` (`imsi`, `active`, `location`, `ue_ambr_ul`, `addedtime`) VALUES (%s, %s, %s, %s, %s)",
                (imsi, active if active else None, location if location else None, ue_ambr_ul if ue_ambr_ul else None, addedtime))
        connection.commit()
    except Exception as inst:
        print_inst(inst)
        failed = True
    finally:
        connection.close()
    return 'error' if failed else 'success'

# retrieve related updates from db
# send delete if imsi not in db's queue table
def retrieve_From_Update_Queue(imsi):
    connection = connect_to_db()
    tableData = []
    try:
        with connection.cursor() as cursor:
            cursor.execute(
                "SELECT `imei`, `active`, `location`, `ue_ambr_ul`, `addedtime` FROM `updatequeue` WHERE `imsi` = %s AND `retrievedtime` IS NULL", (imsi))
            results = cursor.fetchall()
            for i in range(len(results)):
                tableData.append({})
                tableData[i]['imei'] = results[i][0]
                tableData[i]['active'] = str(results[i][1])
                tableData[i]['location'] = str(results[i][2])
                tableData[i]['ue_ambr_ul'] = str(results[i][3])
                tableData[i]['addedtime'] = str(results[i][4])
    finally:
        connection.close()
    # print('dumping:')
    # print(json.dumps(tableData))
    return tableData


def mark_Retrieved_In_Update_Queue(imsi, retrievedtime):
    connection = connect_to_db()
    failed = False
    try:
        with connection.cursor() as cursor:
            cursor.execute(
                "UPDATE `updatequeue` SET `retrievedtime` = %s WHERE `imsi` = %s AND `retrievedtime` IS NULL", (retrievedtime, imsi))
        connection.commit()
    except Exception as inst:
        print_inst(inst)
        failed = True
    finally:
        connection.close()
    return 'error' if failed else 'success'


def apply_DB(imsi, updateObj):
    if not updateObj:
        print('no updateObj')
        return
    update_User(imsi, updateObj)


def location_Active(location, active, ue_ambr_ul):
    connection = connect_to_db()
    imsis = []
    failed = False
    try:
        with connection.cursor() as cursor:
            cursor.execute(
                "SELECT `imsi` FROM `users` WHERE `location` = %s", (location))
            imsis = cursor.fetchall()
    finally:
        connection.close()

    for imsi in imsis:
        result = insert_Update_Queue(
            imsi, active, location, ue_ambr_ul, time.strftime('%Y-%m-%d %H:%M:%S'))
        if result == 'error':
            print('failed for queuing changing %s to %s' % (imsi, active))
            Failed = True
    return 'error' if failed else 'success'


def print_inst(inst):
    print('type of exception:')
    print(type(inst))
    print('exception text:')
    print(inst)

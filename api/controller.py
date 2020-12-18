from api import models
from api import app
import time
from flask import request
import logging
import ssl
import json
import ecdsa
import hashlib
import secrets
import asn1tools
import base64
import pymysql

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
    return models.log_All(tablename)

# get all column names of a table
@app.route('/api/db/columns/<string:tablename>')
def columns(tablename):
    if(tablename not in validTables):
        raise Exception("nonexistent tablename")
    return models.get_Columns(tablename)

# create a user in DB
@app.route('/api/db/adduser', methods=['POST'])
def add_user():
    obj = request.get_json()
    if 'imsi' not in obj or 'msisdn' not in obj or 'imei' not in obj or 'sqn' not in obj or 'rand' not in obj:
        print('missing arguments')
        return "error"
    return models.add_User(request_to_user(obj))

# delete a user from DB
@app.route('/api/db/deleteuser', methods=['POST'])
def delete_user():
    obj = request.get_json()
    # if not request.form['imsi'] or not request.form['msisdn'] or not request.form['imei']:
    if 'imsi' not in obj:
        print('missing arguments')
        return "error"
    return models.delete_User(obj['imsi'])

# update a user in DB
@app.route('/api/db/updateuser', methods=['POST'])
def update_user():
    obj = request.get_json()
    if 'imsi' not in obj:
        print('missing imsi')
        return "error"
    return models.update_User(obj['imsi'], request_to_update(obj))


# cache changes for all imsis in this location
@app.route('/api/db/location', methods=['POST'])
def location_active():
    obj = request.get_json()
    if 'location' not in obj:
        print('missing location')
        return "error"
    return models.location_Active(request_to_location_update(obj))

# batch updates in DB
@app.route('/api/db/batchupdate', methods=['POST'])
def batch_update():
    batch = request.get_json()
    print('received batch in batch_update:')
    print(batch)
    for updateObj in batch:
        if 'imsi' not in updateObj:
            print('missing imsi')
            return "error"
        updated = models.update_User(updateObj['imsi'], request_to_update(updateObj))
        if updated != 'success':
            return 'error'
    return 'success'

def request_to_user(obj):
    imsi = obj['imsi']
    msisdn = obj['msisdn']
    imei = obj['imei']
    sqn = obj['sqn']
    rand = obj['rand']
    active = obj['active'] if 'active' in obj and obj['active'] else 0
    location = obj['location'] if 'location' in obj and obj['location'] else "90024"
    userObj = {"imsi": imsi, "msisdn": msisdn, "imei": imei, "sqn": sqn, "rand": rand, "active": active, "location": location}
    return userObj

def request_to_update(obj):
    imsi = obj['imsi']
    imei = obj['imei'] if 'imei' in obj else ''
    active = obj['active'] if 'active' in obj else ''
    location = obj['location'] if 'location' in obj else ''
    ue_ambr_ul = obj['ue_ambr_ul'] if 'ue_ambr_ul' in obj else ''
    updateObj = {'imsi': imsi, 'imei': imei,
                 'active': active, 'location': location, 'ue_ambr_ul': ue_ambr_ul}
    return updateObj

def request_to_location_update(obj):
    location = obj['location']
    active = obj['active'] if obj['active'] else ''
    ue_ambr_ul = obj['ue_ambr_ul'] if obj['ue_ambr_ul'] else ''
    locationUpdateObj = {'location': location, 'active': active, 'ue_ambr_ul': ue_ambr_ul}
    return locationUpdateObj
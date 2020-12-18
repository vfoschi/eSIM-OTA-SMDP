import time
from flask import Flask
from flask import request
import json
import pymysql
import threading
import requests

validTables = ['users', 'pdn', 'updatequeue']
# UPDATE_SERVER_INTERVAL = 5
listOfUpdates = []
# listLock = threading.Lock()
# print('set up thread!')
# update_server_thread = threading.Thread(target=update_server, name='update_server_thread', daemon=True)
# update_server_thread.start()
def update_server(listOfUpdates, UPDATE_SERVER_INTERVAL, listLock):
        # global listOfUpdates
        # global update_server_thread
        while(1):
            if len(listOfUpdates) > 0:
                print('sending held updates to centre server...')
                with listLock:
                    # send requests to centre server
                    r = requests.post('http://localhost:5000/api/db/batchupdate', json=listOfUpdates)
                    if r.status_code != 200:
                        print('failed to send to centre server:')
                        print(listOfUpdates)
                        print(r.status_code)
                    del listOfUpdates[:]
            time.sleep(UPDATE_SERVER_INTERVAL)

def create_flask():
    global listOfUpdates
    UPDATE_SERVER_INTERVAL = 10
    listLock = threading.Lock()
    update_server_thread = threading.Thread(target=update_server, args=(listOfUpdates, UPDATE_SERVER_INTERVAL, listLock), daemon=True)
    
    app = Flask(__name__)
    update_server_thread.start()
    # atexit.register(interrupt)
    return app

def connect_to_db():
    return pymysql.connect("localhost", "root", "asddfdlxx", "queue")


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
    finally:
        connection.close()
    return json.dumps(columns)

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
                tableData[i]['imsi'] = imsi
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


def location_Update(imsis, location, active, ue_ambr_ul):
    connection = connect_to_db()
    failed = False
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

app = create_flask()
# dump all data in a table
@app.route('/queue/db/table/<string:tablename>')
def log_all(tablename):
    if(tablename not in validTables):
        raise Exception("nonexistent tablename")
    return log_All(tablename)

# get all column names of a table
@app.route('/queue/db/columns/<string:tablename>')
def columns(tablename):
    if(tablename not in validTables):
        raise Exception("nonexistent tablename")
    return get_Columns(tablename)

# user reaches this to retrieve its updates
@app.route('/queue/retrieve/<string:imsi>', methods=['GET', 'POST'])
def retrieve_from_update_queue(imsi):
    global listOfUpdates
    result = retrieve_From_Update_Queue(imsi)
    if len(result) == 0:
        return json.dumps(result)
    mark_Retrieved_In_Update_Queue(imsi, time.strftime('%Y-%m-%d %H:%M:%S'))
    # do the async work here
    # apply_DB(imsi, result[-1])
    listOfUpdates.append(result[-1])

    return json.dumps(result)

# admin reaches this to queue a change to a user
@app.route('/queue/insert', methods=['POST'])
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

# admin reaches this to queue changes to a location
@app.route('/queue/location/update', methods=['POST'])
def location_update():
    obj = request.get_json()
    if 'location' not in obj:
        print('missing location')
        return "error"
    location = obj['location']
    active = obj['active'] if obj['active'] else ''
    ue_ambr_ul = obj['ue_ambr_ul'] if obj['ue_ambr_ul'] else ''
    imsis = obj['imsis'] if obj['imsis'] else []
    return location_Update(imsis, location, active, ue_ambr_ul)
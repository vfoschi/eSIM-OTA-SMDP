import time
from flask import Flask
from flask import request
import json
import pymysql
import threading
import atexit

UPDATE_SERVER_INTERVAL = 5
listOfUpdates = []
listLock = threading.lock()
update_server_thread = threading.Thread()

# def thread_function():
#     time.sleep(2)
#     print('in thread_function execution')

# x = threading.Thread(target=thread_function, args=(1,), daemon=True)

app = Flask(__name__)

validTables = ['users', 'pdn', 'updatequeue']

# dump all data in a table
@app.route('/queue/db/table/<string:tablename>')
def log_all(tablename):
    if(tablename not in validTables):
        raise Exception("nonexistent tablename")
    return log_All(tablename)

# get all column names of a table
@app.route('/queue/db/columns/<string:tablename>')
def columns(tablename):
    print('inside queueserver!')
    if(tablename not in validTables):
        raise Exception("nonexistent tablename")
    return get_Columns(tablename)

# user reaches this to retrieve its updates
@app.route('/queue/retrieve/<string:imsi>', methods=['GET', 'POST'])
def retrieve_from_update_queue(imsi):
    result = retrieve_From_Update_Queue(imsi)
    print('retrieved result is:')
    print(result)
    if len(result) == 0:
        return json.dumps(result)
    mark_Retrieved_In_Update_Queue(imsi, time.strftime('%Y-%m-%d %H:%M:%S'))
    # do the async work here
    # apply_DB(imsi, result[-1])
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

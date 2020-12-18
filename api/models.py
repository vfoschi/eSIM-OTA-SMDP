import time
import json
import ecdsa
import pymysql
import requests

def connect_to_db():
    return pymysql.connect("localhost", "root", "asddfdlxx", "esim")

def log_All(tablename):
    tableData = []
    connection = connect_to_db()
    try:
        with connection.cursor() as cursor:
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


def add_User(obj):
    connection = connect_to_db()
    failed = False
    try:
        with connection.cursor() as cursor:
            cursor.execute(
                "INSERT INTO `users` (`imsi`, `msisdn`, `imei`, `active`, `location`, `sqn`, `rand`) VALUES (%s, %s, %s, %s, %s, %s, %s)", 
                (obj['imsi'], obj['msisdn'], obj['imei'],
                 obj['active'], obj['location'], obj['sqn'], obj['rand']))
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
    oldImei = ""
    newImei = updateObj['imei']
    newActive = updateObj['active']
    newLocation = updateObj['location']
    newAMBRUL = updateObj['ue_ambr_ul']
    try:
        with connection.cursor() as cursor:
            cursor.execute("SELECT `imei` FROM `users` WHERE `imsi` = %s", (imsi))
            oldImei = cursor.fetchone()
            oldImei = oldImei[0] if oldImei else ''
            # Detected violation! In eSIM, an IMSI should be bound to an IMEI forever
            if oldImei and oldImei != 'None' and oldImei != newImei:
                print('Detected Invalid Behavior for {imsi}. Deleted!'.format(imsi=imsi))
                print('imei should be {oldImei} but {newImei}'.format(oldImei=oldImei, newImei=newImei))
                cursor.execute("DELETE FROM `users` WHERE `imsi` = %s", (imsi,))
            else:
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


def location_Active(locationUpdateObj):
    connection = connect_to_db()
    imsis = []
    try:
        with connection.cursor() as cursor:
            cursor.execute(
                "SELECT `imsi` FROM `users` WHERE `location` = %s", (locationUpdateObj['location']))
            imsis = cursor.fetchall()
    finally:
        connection.close()

    locationUpdateObj['imsis'] = imsis
    # send to edge server
    r = requests.post('http://localhost:5001/queue/location/update', json=locationUpdateObj)
    return 'error' if r != "success" else 'success'


def print_inst(inst):
    print('type of exception:')
    print(type(inst))
    print('exception text:')
    print(inst)

# Getting Started with eSIM-SMDP+

## PIP Dependencies
ecdsa asn1tools pymysql utils flask python-dotenv requests

## Database Setup

### For SM-DP+ central server
Create a database named eSIM
Load eSIM_db.sql into mysql

### For Simulating edge server
Create a database named queue
Load edge_db.sql into mysql

## Running the server

In the project directory, you can run:

### `yarn start` in the project directory

Launches the frontend.
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.


### `yarn start-api` in the project directory

Launches the simulate the SM-DP+ central server.
Hosted on http://localhost:5000

### `yarn start-edge` in the project directory

Launches the simulate the SM-DP+ central server.
Hosted on http://localhost:5001

### Use the LPA app

Clone and install https://github.com/JinghaoZhao/LPA-App-dev.
Then, overwrite the following three files in LPA-App-dev/src with respective ones in /LPA-changes folder:
#### app/src/main/AndroidManifest.xml
#### app/src/main/java/com/example/LPA_app/MainActivity.java
#### app/src/main/res/layout/content_main.xml
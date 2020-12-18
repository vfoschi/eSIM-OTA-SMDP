# Getting Started with eSIM-SMDP+

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

## PIP install
ecdsa asn1tools pymysql utils flask python-dotenv requests

## Database Setup

### For SM-DP+ central server
Create a database named eSIM
Load eSIM_db.sql into mysql

### For Edge server
Create a database named queue
Load edge_db.sql into mysql

## Available Scripts

In the project directory, you can run:

### `yarn start`

Runs the app in the development mode.
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.


### `yarn start-api`

Launches the simulate the SM-DP+ central server.
Hosted on http://localhost:5000

### `yarn start-edge`

Launches the simulate the SM-DP+ central server.
Hosted on http://localhost:5001
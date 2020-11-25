import React from "react";
import { Switch, Route } from "react-router-dom";
import App from "./App";
import Layout from "./containers/Layout";
import routesJSON from "./constants/routes.json";
import Home from "./containers/Home";
import Users from "./components/Users";
import PDNs from "./components/PDNs";
import Setting from "./components/Setting";

interface RoutesObject {
  [index: string]: string;
}
const routes: RoutesObject = routesJSON;

export default function Routes() {
  return (
    <App>
      <Layout>
        <Switch>
          <Route path={`${routes["Users"]}`} component={Users} />

          <Route path={`${routes["PDNs"]}`} component={PDNs} />

          <Route path={`${routes["Setting"]}`} component={Setting} />

          <Route path={`${routes["Home"]}`} component={Home} />
        </Switch>
      </Layout>
    </App>
  );
}

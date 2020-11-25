import React, { ReactNode } from "react";
import "./App.css";

type Props = {
  children: ReactNode;
};

function App(props: Props) {
  const { children } = props;
  return <div>{children}</div>;
}

export default App;

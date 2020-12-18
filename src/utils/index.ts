import { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import axios from "axios";

const useGetTableAPI = (
  endpoint: string,
  tableName: string
): [Array<Array<string>>, boolean, boolean] => {
  const [data, setData] = useState([[""]]);
  const [isLoading, setIsLoading] = useState(false);
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    async function getData() {
      setIsError(false);
      setIsLoading(true);
      try {
        const res = await axios.get(`/${endpoint}/db/table/${tableName}`);
        setData(res.data);
      } catch (err) {
        console.log("err is:" + err);
        setIsError(true);
      }
      setIsLoading(false);
    }

    getData();
  }, [endpoint, tableName]);
  return [data, isLoading, isError];
};

const useGetColumnsAPI = (
  endpoint: string,
  tableName: string
): [Array<string>, boolean, boolean] => {
  const [data, setData] = useState([""]);
  const [isLoading, setIsLoading] = useState(false);
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    async function getData() {
      setIsError(false);
      setIsLoading(true);
      try {
        const res = await axios.get(`/${endpoint}/db/columns/${tableName}`);
        setData(res.data);
      } catch (err) {
        console.log("err is:" + err);
        setIsError(true);
      }
      setIsLoading(false);
    }

    getData();
  }, [endpoint, tableName]);
  return [data, isLoading, isError];
};

function useRouteRefresh() {
  const history = useHistory();
  // const forceRefresh = () => {
  //   history.push({ pathname: "/empty" });
  //   history.goBack();
  // };
  // return forceRefresh;
  return () => {
    history.go(0);
  };
}

export { useGetTableAPI, useGetColumnsAPI, useRouteRefresh };

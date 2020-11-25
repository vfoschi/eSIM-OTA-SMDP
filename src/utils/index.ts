import { useState, useEffect } from "react";
import axios from "axios";

const useGetTableAPI = (
  url: string
): [Array<Array<string>>, boolean, boolean] => {
  const [data, setData] = useState([[""]]);
  const [isLoading, setIsLoading] = useState(false);
  const [isError, setIsError] = useState(false);

  useEffect(() => {
    async function getData() {
      setIsError(false);
      setIsLoading(true);
      try {
        const res = await axios.get(url);
        setData(res.data);
      } catch (err) {
        console.log("err is:" + err);
        setIsError(true);
      }
      setIsLoading(false);
    }

    getData();
  }, [url]);
  return [data, isLoading, isError];
};

const useGetColumnsAPI = (
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
        const res = await axios.get("/api/db/columns/" + tableName);
        setData(res.data);
      } catch (err) {
        console.log("err is:" + err);
        setIsError(true);
      }
      setIsLoading(false);
    }

    getData();
  }, [tableName]);
  return [data, isLoading, isError];
};

export { useGetTableAPI, useGetColumnsAPI };

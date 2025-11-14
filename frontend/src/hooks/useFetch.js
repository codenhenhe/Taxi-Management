
import { useState, useEffect, useCallback } from "react";
import apiClient from "../api/apiClient";



export default function useFetch(url) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // CHỈ phụ thuộc URL → không infinite loop
  const fetchData = useCallback(async () => {
    setLoading(true);
    try {
      const res = await apiClient.get(url);
      setData(res.data);
    } catch (err) {
      setError(err);
    } finally {
      setLoading(false);
    }
  }, [url]);

  useEffect(() => {
    if (url) fetchData();
  }, [url, fetchData]);

  return { data, loading, error, refetch: fetchData };
}

// src/hooks/useFetch.js
import { useState, useEffect, useCallback } from "react";

export default function useFetch(url, options = {}) {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Dùng useCallback để memoize fetchData → tránh tạo hàm mới mỗi render
  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetch(url, {
        ...options,
        headers: {
          "Content-Type": "application/json",
          ...options.headers,
        },
      });

      const contentType = res.headers.get("content-type");
      if (!contentType || !contentType.includes("application/json")) {
        const text = await res.text();
        throw new Error(`Không phải JSON: ${text.substring(0, 100)}...`);
      }

      if (!res.ok) {
        const json = await res.json().catch(() => ({}));
        throw new Error(json.message || `HTTP ${res.status}`);
      }

      const json = await res.json();
      setData(json);
    } catch (err) {
      setError(err.message);
      setData(null);
    } finally {
      setLoading(false);
    }
  }, [url, options]); // ← Thêm dependencies đúng

  // Gọi khi mount hoặc url/options thay đổi
  useEffect(() => {
    if (url) fetchData();
  }, [url, fetchData]); // ← Thêm fetchData vào dependency

  return { data, loading, error, refetch: fetchData };
}

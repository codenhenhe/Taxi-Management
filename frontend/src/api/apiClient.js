// src/api/apiClient.js
import axios from "axios";
import { useAuthStore } from "../store/authStore";

// Tạo một instance axios với cấu hình cơ bản
const apiClient = axios.create({
  baseURL: "/", // Hoặc base URL của API nếu khác,
  headers: {
    "Content-Type": "application/json",
  },
});

// --- Đây là phần quan trọng: Interceptor (Bộ đánh chặn) ---
// 1. Request Interceptor: Tự động đính kèm token vào MỌI request
apiClient.interceptors.request.use(
  (config) => {
    // Lấy token từ authStore (dùng getState vì ta đang ở ngoài React component)
    const token = useAuthStore.getState().token;

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 2. Response Interceptor (Tùy chọn nhưng rất nên dùng):
// Xử lý lỗi 401 (Unauthorized) - tức là token hết hạn hoặc không hợp lệ
apiClient.interceptors.response.use(
  (response) => response, // Trả về response nếu không có lỗi
  (error) => {
    // Nếu lỗi là 401
    if (error.response && error.response.status === 401) {
      // Gọi hàm logout từ authStore
      useAuthStore.getState().logout();
      // Chuyển hướng người dùng về trang đăng nhập
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default apiClient;

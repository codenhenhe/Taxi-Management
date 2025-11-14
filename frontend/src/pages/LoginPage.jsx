// src/pages/LoginPage.jsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";
import bgImage from "/background.jpg";


export default function LoginPage() {
  const [tenDangNhap, setTenDangNhap] = useState("");
  const [matKhau, setMatKhau] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);


  const login = useAuthStore((state) => state.login);
  const navigate = useNavigate();

  const togglePassword = () => setShowPassword(!showPassword);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!tenDangNhap && !matKhau) {
      setError("Vui lòng nhập Tên đăng nhập và Mật khẩu.");
      return;
    }
    if (!tenDangNhap) {
      setError("Vui lòng nhập thêm Tên đăng nhập.");
      return;
    }
    if (!matKhau) {
      setError("Vui lòng nhập thêm Mật khẩu.");
      return;
    }
    setError("");
    setLoading(true);

    try {
      const res = await fetch("/api/qtv/dangnhap", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ tenDangNhap, matKhau }),
      });
      const data = await res.json();
      if (!res.ok) throw new Error(data.message || "Đăng nhập thất bại");

      login(data);
      navigate("/", { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    // Nền
    <div
      className="w-screen h-screen flex justify-center items-center bg-no-repeat bg-center bg-cover"
      style={{ backgroundImage: `url(${bgImage})` }}
    >
      <form
        className="w-full max-w-md bg-white p-10 md:p-12 rounded-2xl shadow-xl"
        onSubmit={handleSubmit}
        noValidate
      >
        {/* title */}
        <h2 className="text-center mb-6 text-2xl font-bold text-gray-800">
          Đăng nhập Admin
        </h2>

        {/* form-error */}
        {error && (
          <p className="text-red-600 bg-red-100 border border-red-500 p-3 rounded-md text-center mb-4">
            {error}
          </p>
        )}

        {/* input-group */}
        <div className="mb-5">
          <label className="font-medium block text-sm mb-2 text-gray-700">
            Tên đăng nhập
          </label>
          <input
            type="text"
            placeholder="Nhập username..."
            value={tenDangNhap}
            onChange={(e) => setTenDangNhap(e.target.value)}
            required
            autoComplete="username"
            className="w-full p-3 rounded-lg bg-gray-50 border border-gray-200 transition duration-200 
                       placeholder:text-gray-400
                       focus:outline-none focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>

        {/* input-group */}
        <div className="mb-6">
          <label className="font-medium block text-sm mb-2 text-gray-700">
            Mật khẩu
          </label>
          {/* password-wrapper */}
          <div className="relative">
            <input
              type={showPassword ? "text" : "password"}
              placeholder="Nhập mật khẩu..."
              value={matKhau}
              onChange={(e) => setMatKhau(e.target.value)}
              required
              autoComplete="current-password"
              className="w-full p-3 rounded-lg bg-gray-50 border border-gray-200 transition duration-200 
                         placeholder:text-gray-400
                         focus:outline-none focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
            {/* --- SỬA Ở ĐÂY: Thêm z-10 --- */}
            <button
              type="button"
              className="absolute top-1/2 right-4 -translate-y-1/2 bg-transparent border-0 
                         text-sm text-gray-500 font-medium cursor-pointer transition 
                         hover:text-blue-600 z-10"
              onClick={togglePassword}
            >
              {showPassword ? "Ẩn" : "Hiện"}
            </button>
          </div>
        </div>

        {/* login-btn */}
        <button
          type="submit"
          className="w-full py-3 bg-blue-600 text-white text-base font-medium rounded-lg cursor-pointer 
                     transition duration-200 shadow-sm
                     hover:bg-blue-700 hover:shadow-md
                     focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-500 focus-visible:ring-offset-2
                     disabled:opacity-50 disabled:cursor-not-allowed"
          disabled={loading}

        >
          {loading ? "Đang đăng nhập..." : "Đăng nhập"}
        </button>
      </form>
    </div>
  );
}

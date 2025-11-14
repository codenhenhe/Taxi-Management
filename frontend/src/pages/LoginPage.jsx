import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";
import "../Login.css";

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

    // 1. Tự kiểm tra xem có rỗng không
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

    // Nếu không rỗng, tiếp tục như bình thường
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
    <div className="login-background">
      {/* Thêm noValidate để tắt pop-up mặc định của trình duyệt */}
      <form
        className="login-container fade-in"
        onSubmit={handleSubmit}
        noValidate 
      >
        <h2 className="title">Đăng nhập Admin</h2>

        {error && (
          <p className="form-error">{error}</p>
        )}

        <div className="input-group">
          <label>Tên đăng nhập</label>
          <input
            type="text"
            placeholder="Nhập username..."
            value={tenDangNhap}
            onChange={(e) => setTenDangNhap(e.target.value)}
            required
            autoComplete="username"
          />
        </div>

        <div className="input-group">
          <label>Mật khẩu</label>
          <div className="password-wrapper">
            <input
              type={showPassword ? "text" : "password"}
              placeholder="Nhập mật khẩu..."
              value={matKhau}
              onChange={(e) => setMatKhau(e.target.value)}
              required
              autoComplete="current-password"
            />
            <button
              type="button"
              className="toggle-btn"
              onClick={togglePassword}
            >
              {showPassword ? "Ẩn" : "Hiện"}
            </button>
          </div>
        </div>

        <button
          type="submit"
          className="login-btn"
          disabled={loading}
        >
          {loading ? "Đang đăng nhập..." : "Đăng nhập"}
        </button>
      </form>
    </div>
  );
}
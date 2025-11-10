<<<<<<< HEAD
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";

createRoot(document.getElementById("root")).render(
  <StrictMode>
    <App />
  </StrictMode>
);
=======
// src/main.jsx (Hoặc file khởi chạy chính)

import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App.jsx';

// NHẬP KHẨU BOOTSTRAP CSS (Bước quan trọng nhất)
import 'bootstrap/dist/css/bootstrap.min.css';

// NHẬP KHẨU BOOTSTRAP JS (Nếu cần dùng các components tương tác như modal, dropdown)
import 'bootstrap/dist/js/bootstrap.bundle.min'; 
// Hoặc chỉ cần import '@popperjs/core' nếu bạn muốn quản lý JS thủ công hơn

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
>>>>>>> ecbde1844b625dd610b480ad957a790f9b080f64

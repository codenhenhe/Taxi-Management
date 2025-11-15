// src/layouts/AdminLayout.jsx
import React from "react";
import Sidebar from "../components/layout/Sidebar";
import { Outlet } from "react-router-dom";

const AdminLayout = () => {
  return (
    <div className="flex h-screen bg-gray-100 overflow-hidden">
      {/* Sidebar – full height */}
      <Sidebar />

      {/* Main content – chiếm hết không gian còn lại */}
      <main className="flex-1 p-6 bg-white">
        <Outlet />
      </main>
    </div>
  );
};

export default AdminLayout;

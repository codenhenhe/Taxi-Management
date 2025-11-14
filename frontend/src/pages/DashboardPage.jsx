// src/pages/DashboardPage.jsx
import { useEffect, useState, useMemo } from "react";

import useFetch from "../hooks/useFetch";

import {
  Car,
  Users,
  DollarSign,
  Activity,
  TrendingUp,
  Clock,
  MapPin,
  Wrench, // <-- THÊM ICON MỚI
} from "lucide-react";
// THÊM CÁC COMPONENT BIỂU ĐỒ
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

// === COMPONENT ĐỒNG HỒ (Giữ nguyên) ===
const LiveClock = () => {
  const [time, setTime] = useState(new Date());
  useEffect(() => {
    let handle = null;
    const update = () => {
      setTime(new Date());
      handle = setTimeout(update, 60000);
    };
    handle = setTimeout(update, 60000 - (Date.now() % 60000));
    return () => handle && clearTimeout(handle);
  }, []);

  return (
    <div className="flex items-center gap-2 text-sm text-gray-600">
      <Clock size={16} />
      <span className="font-medium">
        {time.toLocaleTimeString("vi-VN", {
          hour: "2-digit",
          minute: "2-digit",
        })}
      </span>
    </div>
  );
};



// Dữ liệu mặc định
const DEFAULT_STATS = {
  totalVehicles: 0,
  activeVehicles: 0, // Xe đang chạy
  onlineDrivers: 0, // Tài xế đang làm việc
  activeDrivers: 0, // Tài xế rảnh
  todayTrips: 0,
  todayRevenue: 0,
  tripComparison: 0, // So sánh chuyến hôm qua
};

export default function DashboardPage() {
  // === SỬA LẠI API CALLS ===
  
  // (5 API cũ cho 4 ô và danh sách chuyến đi - ĐÃ ĐỔI SANG /api/stats/)
  const { data: xeStatsData, loading: loadingXe } = useFetch(
    "/api/thong-ke/xe-stats" // ĐÚNG
  );
  const { data: taiXeStatsData, loading: loadingTaiXe } = useFetch(
    "/api/thong-ke/tai-xe-stats" // ĐÚNG
  );
  const { data: tripStatsData, loading: loadingTripStats } = useFetch(
    "/api/thong-ke/so-sanh-hom-qua" // ĐÚNG
  );
  const { data: revenueData, loading: loadingRevenue } = useFetch(
    "/api/thong-ke/doanh-thu-hom-nay" // ĐÚNG
  );
  const { data: recentTrips, loading: loadingTrips } = useFetch(
    "/api/thong-ke/chuyen-di-gan-day?soChuyen=5" // ĐÚNG
  );

  // 6. GỌI API BẢO TRÌ MỚI (E8)
  const { data: maintenanceStats, loading: loadingMaintenance } = useFetch(
    "/api/bao-tri-xe/chi-phi-bao-tri?year=2025" // (Tạm lấy năm 2025)
  );

  const loadingStats =
    loadingXe || loadingTaiXe || loadingTripStats || loadingRevenue;

  // === (useMemo cho stats, lastUpdate, tripPercentage giữ nguyên) ===
  const stats = useMemo(() => {
    if (loadingStats) return DEFAULT_STATS;
    const xeStats = xeStatsData?.[0];
    const taiXeStats = taiXeStatsData?.[0];
    const tripStats = tripStatsData?.[0];
    return {
      totalVehicles: xeStats?.tongSoXe || 0,
      activeVehicles: xeStats?.dangChay || 0,
      onlineDrivers: taiXeStats?.dangHoatDong || 0,
      activeDrivers: xeStats?.dangRanh || 0,
      todayTrips: tripStats?.soHomNay || 0,
      todayRevenue: revenueData || 0,
      tripComparison: tripStats?.soChuyenSoVoiHomQua || 0,
    };
  }, [
    xeStatsData,
    taiXeStatsData,
    tripStatsData,
    revenueData,
    loadingStats,
  ]);

  const lastUpdate = useMemo(() => {
    if (!loadingStats) return new Date().toLocaleString("vi-VN");
    return "Đang tải...";
  }, [loadingStats]);

  const tripPercentage = useMemo(() => {
    if (!stats.tripComparison || stats.tripComparison === 0) return "+0%";
    const perc = (stats.tripComparison - 1) * 100;
    return `${perc > 0 ? "+" : ""}${perc.toFixed(0)}% so với hôm qua`;
  }, [stats.tripComparison]);

  // === (Helper mới để format data cho biểu đồ bảo trì) ===
  const formattedMaintenanceData = useMemo(() => {
    if (!maintenanceStats) return [];
    // Chuyển { thang_bao_tri: 11, tong_chi_phi: 750000 }
    // thành { name: 'T11', "Chi phí": 750000 }
    return maintenanceStats.map(item => ({
      name: `T${item.thang_bao_tri}`, // Đảm bảo khớp DTO
      "Chi phí": item.tong_chi_phi, // Đảm bảo khớp DTO
    }));
  }, [maintenanceStats]);


  return (
    <div className="space-y-6">
      {/* Tiêu đề + đồng hồ (Giữ nguyên) */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-800">Dashboard</h1>
          <p className="text-sm text-gray-600 mt-1">
            Cập nhật lúc: {lastUpdate}
          </p>
        </div>

        <LiveClock />
      </div>

      {/* 4 Ô thống kê chính (Giữ nguyên) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {/* Tổng xe */}
        <div className="bg-linear-to-br from-blue-500 to-blue-600 text-white p-6 rounded-xl shadow-lg hover:scale-105 transition">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-blue-100 text-sm">Tổng phương tiện</p>
              <p className="text-3xl font-bold mt-1">
                {loadingStats ? "..." : stats.totalVehicles}
              </p>
            </div>
            <Car size={36} className="opacity-80" />
          </div>
          <p className="text-xs mt-3 opacity-90">
            {stats.activeVehicles || 0} đang hoạt động
          </p>
        </div>

        {/* Tài xế online */}
        <div className="bg-linear-to-br from-green-500 to-green-600 text-white p-6 rounded-xl shadow-lg hover:scale-105 transition">
          <div className="flex items-center justify-between">
            <div>

              <p className="text-green-100 text-sm">Tổng số tài xế</p>
              <p className="text-3xl font-bold mt-1">
                {loadingStats ? "..." : stats.onlineDrivers}
              </p>
            </div>
            <Users size={36} className="opacity-80" />
          </div>
          <p className="text-xs mt-3 opacity-90">
          </p>
        </div>

        {/* Chuyến hôm nay */}
        <div className="bg-linear-to-br from-purple-500 to-purple-600 text-white p-6 rounded-xl shadow-lg hover:scale-105 transition">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-purple-100 text-sm">Chuyến hôm nay</p>
              <p className="text-3xl font-bold mt-1">
                {loadingStats ? "..." : stats.todayTrips}
              </p>
            </div>
            <Activity size={36} className="opacity-80" />
          </div>
          <p className="text-xs mt-3 opacity-90">

            <TrendingUp size={14} className="inline" /> {tripPercentage}

          </p>
        </div>

        {/* Doanh thu */}
        <div className="bg-linear-to-br from-red-500 to-red-600 text-white p-6 rounded-xl shadow-lg hover:scale-105 transition">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-red-100 text-sm">Doanh thu hôm nay</p>
              <p className="text-3xl font-bold mt-1">
                {loadingStats
                  ? "..."
                  : (stats.todayRevenue / 1_000_000).toFixed(1) + "M"}
              </p>
            </div>
            <DollarSign size={36} className="opacity-80" />
          </div>
          <p className="text-xs mt-3 opacity-90">

            Đã thu hết

          </p>
        </div>
      </div>


      {/* Phần còn lại */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Chuyến đi gần đây (Giữ nguyên) */}
        <div className="lg:col-span-2 bg-white p-6 rounded-xl shadow-sm border">
          <h3 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <MapPin size={20} className="text-blue-600" />
            Chuyến đi gần đây
          </h3>
          {loadingTrips ? (
            <p className="text-center text-gray-500 py-8">Đang tải...</p>
          ) : recentTrips && recentTrips.length > 0 ? (
            <div className="space-y-3 max-h-80 overflow-y-auto">

              {recentTrips.map((trip) => (
                <div
                  key={trip.maChuyen}
                  className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition"
                >
                  <div className="flex-1">
                    <p className="font-medium text-sm">{trip.maChuyen}</p>
                    <p className="text-xs text-gray-600">
                      {trip.diemDon} → {trip.diemTra}
                    </p>
                  </div>
                  <div className="text-right text-sm">
                    <p className="font-medium">

                      {trip.cuocPhi.toLocaleString()}đ
                    </p>
                    <p className="text-xs text-gray-500">
                      {new Date(trip.tgDon).toLocaleTimeString("vi-VN")}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-center text-gray-500 py-8">Chưa có chuyến nào</p>
          )}
        </div>


        {/* ============================================== */}
        {/* === THAY THẾ Ô CẢNH BÁO BẰNG BIỂU ĐỒ MỚI === */}
        {/* ============================================== */}
        <div className="bg-white p-6 rounded-xl shadow-sm border">
          <h3 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <Wrench size={20} className="text-orange-600" />
            Chi phí bảo trì (2025)
          </h3>
          {loadingMaintenance ? (
            <p className="text-center text-gray-500 py-8">Đang tải...</p>
          ) : formattedMaintenanceData && formattedMaintenanceData.length > 0 ? (
            <ResponsiveContainer width="100%" height={280}>
              <BarChart data={formattedMaintenanceData} margin={{ top: 5, right: 0, left: 0, bottom: 0 }}>
                <XAxis dataKey="name" fontSize={12} />
                <YAxis 
                  fontSize={12} 
                  tickFormatter={(value) => `${value / 1000}k`} // Hiển thị 750k
                  />
                <Tooltip 
                  formatter={(value) => `${value.toLocaleString()}đ`} // Hiển thị 750,000đ
                />
                <Bar 
                  dataKey="Chi phí" 
                  fill="#F59E0B" // Màu cam
                  radius={[4, 4, 0, 0]} 
                />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <p className="text-center text-gray-500 py-8">
              Không có dữ liệu bảo trì.
            </p>
          )}
        </div>
      </div>

    </div>
  );
}

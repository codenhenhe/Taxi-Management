// src/pages/DashboardPage.jsx
import { useEffect, useState, useMemo } from "react";
import useFetch from "../hooks/useFetch"; // Dùng custom hook của bạn
import {
  Car,
  Users,
  DollarSign,
  Activity,
  AlertCircle,
  TrendingUp,
  Clock,
  MapPin,
} from "lucide-react";

// === COMPONENT ĐỒNG HỒ RIÊNG (Giữ nguyên) ===
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
  // (Chúng ta bỏ /api/dashboard/stats và gọi 4 API riêng lẻ)

  // 1. Gọi API E3 (/api/stats/xe-stats)
  const { data: xeStatsData, loading: loadingXe } = useFetch(
    "/api/thong-ke/xe-stats"
  );
  // 2. Gọi API E2 (/api/stats/tai-xe-stats)
  const { data: taiXeStatsData, loading: loadingTaiXe } = useFetch(
    "/api/thong-ke/tai-xe-stats"
  );
  // 3. Gọi API E1 (/api/stats/so-sanh-hom-qua)
  const { data: tripStatsData, loading: loadingTripStats } = useFetch(
    "/api/thong-ke/so-sanh-hom-qua"
  );
  // 4. Gọi API E5 (/api/stats/doanh-thu-hom-nay)
  const { data: revenueData, loading: loadingRevenue } = useFetch(
    "/api/thong-ke/doanh-thu-hom-nay"
  );
  // 5. Gọi API E4 (/api/stats/chuyen-di-gan-day)
  const { data: recentTrips, loading: loadingTrips } = useFetch(
    "/api/thong-ke/chuyen-di-gan-day?soChuyen=5"
  );
  // // 6. (API Cảnh báo này VẪN BỊ LỖI 404, vì bạn chưa tạo)
  // const { data: warnings, loading: loadingWarnings } = useFetch(
  //   "/api/maintenance/warnings" 
  // );

  const loadingStats =
    loadingXe || loadingTaiXe || loadingTripStats || loadingRevenue;

  // === TỔNG HỢP DỮ LIỆU TỪ 4 API VÀO 1 OBJECT ===
  const stats = useMemo(() => {
    if (loadingStats) return DEFAULT_STATS;

    // Lấy dữ liệu từ List (vì SP trả về List 1 phần tử)
    const xeStats = xeStatsData?.[0];
    const taiXeStats = taiXeStatsData?.[0];
    const tripStats = tripStatsData?.[0];

    return {
      totalVehicles: xeStats?.tongSoXe || 0,
      activeVehicles: xeStats?.dangChay || 0,
      onlineDrivers: taiXeStats?.dangHoatDong || 0,
      activeDrivers: xeStats?.dangRanh || 0, // Giả sử 'Tài xế sẵn sàng' là 'Xe đang rảnh'
      todayTrips: tripStats?.soHomNay || 0,
      todayRevenue: revenueData || 0, // API E5 trả về 1 con số
      tripComparison: tripStats?.soChuyenSoVoiHomQua || 0,
    };
  }, [
    xeStatsData,
    taiXeStatsData,
    tripStatsData,
    revenueData,
    loadingStats,
  ]);

  // Cập nhật "Cập nhật lúc"
  const lastUpdate = useMemo(() => {
    if (!loadingStats) return new Date().toLocaleString("vi-VN");
    return "Đang tải...";
  }, [loadingStats]);

  // Tính % so sánh (dùng dữ liệu động từ E1)
  const tripPercentage = useMemo(() => {
    if (!stats.tripComparison || stats.tripComparison === 0) return "+0%";
    const perc = (stats.tripComparison - 1) * 100;
    return `${perc > 0 ? "+" : ""}${perc.toFixed(0)}% so với hôm qua`;
  }, [stats.tripComparison]);

  return (
    <div className="space-y-6">
      {/* Tiêu đề + đồng hồ */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-800">Dashboard</h1>
          <p className="text-sm text-gray-600 mt-1">
            Cập nhật lúc: {lastUpdate}
          </p>
        </div>
        <LiveClock />
      </div>

      {/* 4 Ô thống kê chính (Sửa lại binding) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        {/* Tổng xe (Từ E3) */}
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

        {/* Tài xế online (Từ E2) */}
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

        {/* Chuyến hôm nay (Từ E1) */}
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

        {/* Doanh thu (Từ E5) */}
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
            {/* (API của bạn không có 'pendingRevenue') */}
          </p>
        </div>
      </div>

      {/* Phần còn lại (Sửa binding cho recentTrips) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Chuyến đi gần đây (Từ E4) */}
        <div className="lg:col-span-2 bg-white p-6 rounded-xl shadow-sm border">
          <h3 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <MapPin size={20} className="text-blue-600" />
            Chuyến đi gần đây
          </h3>
          {loadingTrips ? (
            <p className="text-center text-gray-500 py-8">Đang tải...</p>
          ) : recentTrips && recentTrips.length > 0 ? (
            <div className="space-y-3 max-h-80 overflow-y-auto">
              {/* API E4 trả về ChuyenDiDTO, ta dùng các trường DTO */}
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
{/* 
        {/* Cảnh báo bảo trì (Giữ nguyên, vì không có API thay thế) *
        <div className="bg-white p-6 rounded-xl shadow-sm border">
          <h3 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <AlertCircle size={20} className="text-orange-600" />
            Cảnh báo bảo trì
          </h3>
          {/* {loadingWarnings ? (
            <p className="text-center text-gray-500 py-8">Đang tải...</p>
          ) : warnings && warnings.length > 0 ? (
            <div className="space-y-3">
              {warnings.map((w) => (
                <div
                  key={w.bien_so}
                  className="p-3 bg-orange-50 border border-orange-200 rounded-lg"
                >
                  <p className="font-medium text-sm text-orange-800">
                    {w.bien_so}
                  </p>
                  <p className="text-xs text-orange-700 mt-1">
                    Bảo trì sau: <strong>{w.ngay_con_lai} ngày</strong>
                  </p>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-center text-green-600 py-8">
              Tất cả xe đều ổn định
            </p>
          )} *
        </div> */}
      </div>

      {/* (Phần Phân công đang chờ đã bị xóa vì API E không cung cấp) */}
    </div>
  );
}
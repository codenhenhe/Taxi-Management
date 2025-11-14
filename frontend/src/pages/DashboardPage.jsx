// src/pages/DashboardPage.jsx
import { useEffect, useState, useMemo } from "react";
import useFetch from "../hooks/useFetch";
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

// === COMPONENT ĐỒNG HỒ RIÊNG – CHỈ RE-RENDER NÓ ===
const LiveClock = () => {
  const [time, setTime] = useState(new Date());

  useEffect(() => {
    let handle = null;

    const update = () => {
      setTime(new Date());
      // Cập nhật mỗi 60s (1 phút)
      handle = setTimeout(update, 60000);
    };

    handle = setTimeout(update, 60000 - (Date.now() % 60000)); // Đồng bộ đầu phút

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
  totalVehicles: 48,
  activeDrivers: 32,
  todayTrips: 23,
  todayRevenue: 12500000,
  onlineDrivers: 28,
  pendingDispatch: 5,
};

export default function DashboardPage() {
  // Lấy dữ liệu từ API
  const { data: statsData, loading: loadingStats } = useFetch(
    "/api/dashboard/stats"
  );
  const { data: recentTrips, loading: loadingTrips } =
    useFetch("/api/trips/recent");
  const { data: warnings, loading: loadingWarnings } = useFetch(
    "/api/maintenance/warnings"
  );

  // Memoize stats → chỉ thay đổi khi data thay đổi
  const stats = useMemo(() => statsData || DEFAULT_STATS, [statsData]);

  // Cập nhật "Cập nhật lúc" chỉ khi stats thay đổi
  const lastUpdate = useMemo(() => {
    if (statsData) return new Date().toLocaleString("vi-VN");
    return "Đang tải...";
  }, [statsData]);

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
        <LiveClock /> {/* Chỉ phần này re-render */}
      </div>

      {/* 4 Ô thống kê chính */}
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
              <p className="text-green-100 text-sm">Tài xế online</p>
              <p className="text-3xl font-bold mt-1">
                {loadingStats ? "..." : stats.onlineDrivers}
              </p>
            </div>
            <Users size={36} className="opacity-80" />
          </div>
          <p className="text-xs mt-3 opacity-90">
            {stats.activeDrivers} đang sẵn sàng
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
            <TrendingUp size={14} className="inline" /> +12% so với hôm qua
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
            {stats.pendingRevenue > 0
              ? `${(stats.pendingRevenue / 1_000_000).toFixed(
                  1
                )}M chưa thanh toán`
              : "Đã thu hết"}
          </p>
        </div>
      </div>

      {/* Phần còn lại giữ nguyên */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Chuyến đi gần đây */}
        <div className="lg:col-span-2 bg-white p-6 rounded-xl shadow-sm border">
          <h3 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <MapPin size={20} className="text-blue-600" />
            Chuyến đi gần đây
          </h3>
          {loadingTrips ? (
            <p className="text-center text-gray-500 py-8">Đang tải...</p>
          ) : recentTrips && recentTrips.length > 0 ? (
            <div className="space-y-3 max-h-80 overflow-y-auto">
              {recentTrips.slice(0, 5).map((trip) => (
                <div
                  key={trip.ma_chuyen}
                  className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition"
                >
                  <div className="flex-1">
                    <p className="font-medium text-sm">{trip.ma_chuyen}</p>
                    <p className="text-xs text-gray-600">
                      {trip.diem_don} → {trip.diem_tra}
                    </p>
                  </div>
                  <div className="text-right text-sm">
                    <p className="font-medium">
                      {trip.cuoc_phi.toLocaleString()}đ
                    </p>
                    <p className="text-xs text-gray-500">
                      {new Date(trip.thoi_gian_nhan).toLocaleTimeString(
                        "vi-VN"
                      )}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-center text-gray-500 py-8">Chưa có chuyến nào</p>
          )}
        </div>

        {/* Cảnh báo bảo trì */}
        <div className="bg-white p-6 rounded-xl shadow-sm border">
          <h3 className="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <AlertCircle size={20} className="text-orange-600" />
            Cảnh báo bảo trì
          </h3>
          {loadingWarnings ? (
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
          )}
        </div>
      </div>

      {/* Phân công đang chờ */}
      {stats.pendingDispatch > 0 && (
        <div className="bg-yellow-50 border border-yellow-300 p-4 rounded-xl flex items-center justify-between">
          <div className="flex items-center gap-3">
            <AlertCircle size={24} className="text-yellow-600" />
            <div>
              <p className="font-semibold text-yellow-800">
                {stats.pendingDispatch} yêu cầu phân công đang chờ
              </p>
              <p className="text-sm text-yellow-700">
                Vui lòng xử lý tại trang Phân công xe
              </p>
            </div>
          </div>
          <button className="bg-yellow-600 text-white px-5 py-2 rounded-lg hover:bg-yellow-700 transition text-sm font-medium">
            Xử lý ngay
          </button>
        </div>
      )}
    </div>
  );
}

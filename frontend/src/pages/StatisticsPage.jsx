// src/pages/StatisticsPage.jsx
import { useState, useMemo, useRef } from "react";
import useFetch from "../hooks/useFetch";
import {
  BarChart,
  Bar,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";
import {
  Calendar,
  Download,
  FileSpreadsheet,
  FileText,
  TrendingUp,
  DollarSign,
  Car,
  Users,
} from "lucide-react";

// Thư viện xuất file
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";
// import html2canvas from "html2canvas";
// import jsPDF from "jspdf";

const COLORS = ["#3B82F6", "#10B981", "#F59E0B", "#EF4444", "#8B5CF6"];

export default function StatisticsPage() {
  const [dateRange, setDateRange] = useState("7days");
  const [isExporting, setIsExporting] = useState(false);
  const printRef = useRef();

  // === LẤY DỮ LIỆU ===
  const { data: revenueData, loading: loadingRevenue } = useFetch(
    `/api/thong-ke/revenue?range=${dateRange}`
  );
  const { data: tripData, loading: loadingTrips } = useFetch(
    `/api/thong-ke/trips?range=${dateRange}`
  );
  // const { data: vehicleTypeData } = useFetch("/api/thong-ke/vehicle-types");
  const { data: driverPerformance } = useFetch(
    "/api/thong-ke/driver-performance"
  );

  // === TÍNH TOÁN TỔNG ===
  const totalRevenue = useMemo(
    () => revenueData?.reduce((s, d) => s + d.value, 0) || 0,
    [revenueData]
  );
  const totalTrips = useMemo(
    () => tripData?.reduce((s, d) => s + d.trips, 0) || 0,
    [tripData]
  );
  const avgPerTrip = totalTrips > 0 ? totalRevenue / totalTrips : 0;

  // === XUẤT EXCEL ===
  const exportToExcel = async () => {
    setIsExporting(true);
    try {
      const wb = XLSX.utils.book_new();

      // Sheet 1: Tổng quan
      const summaryData = [
        ["Báo cáo thống kê", `Từ: ${new Date().toLocaleDateString("vi-VN")}`],
        [""],
        ["Chỉ tiêu", "Giá trị"],
        ["Tổng doanh thu", totalRevenue],
        ["Tổng chuyến đi", totalTrips],
        ["Trung bình/chuyến", Math.round(avgPerTrip)],
        ["Tài xế hoạt động", driverPerformance?.length || 0],
      ];
      const ws1 = XLSX.utils.aoa_to_sheet(summaryData);
      XLSX.utils.book_append_sheet(wb, ws1, "Tổng quan");

      // Sheet 2: Doanh thu theo ngày
      if (revenueData?.length) {
        const revenueSheet = revenueData.map((d) => ({
          Ngày: d.date,
          "Doanh thu (đ)": d.value,
        }));
        const ws2 = XLSX.utils.json_to_sheet(revenueSheet);
        XLSX.utils.book_append_sheet(wb, ws2, "Doanh thu");
      }

      // Sheet 3: Chuyến đi theo ngày
      if (tripData?.length) {
        const tripSheet = tripData.map((d) => ({
          Ngày: d.date,
          "Số chuyến": d.trips,
        }));
        const ws3 = XLSX.utils.json_to_sheet(tripSheet);
        XLSX.utils.book_append_sheet(wb, ws3, "Chuyến đi");
      }

      // Sheet 4: Loại xe
      // if (vehicleTypeData?.length) {
      //   const ws4 = XLSX.utils.json_to_sheet(
      //     vehicleTypeData.map((v) => ({
      //       "Loại xe": v.ten_loai,
      //       "Số lượng": v.count,
      //     }))
      //   );
      //   XLSX.utils.book_append_sheet(wb, ws4, "Loại xe");
      // }

      // Sheet 5: Top tài xế
      if (driverPerformance?.length) {
        const topDrivers = driverPerformance.slice(0, 10).map((d) => ({
          "Mã TX": d.ma_tai_xe,
          "Họ tên": d.ho_ten,
          Chuyến: d.tong_chuyen,
          "Doanh thu": d.doanh_thu,
        }));
        const ws5 = XLSX.utils.json_to_sheet(topDrivers);
        XLSX.utils.book_append_sheet(wb, ws5, "Tài xế");
      }

      const excelBuffer = XLSX.write(wb, { bookType: "xlsx", type: "array" });
      const fileName = `BaoCao_ThongKe_${dateRange}_${new Date()
        .toISOString()
        .slice(0, 10)}.xlsx`;
      saveAs(new Blob([excelBuffer]), fileName);
    } catch {
      alert("Lỗi khi xuất Excel!");
    } finally {
      setIsExporting(false);
    }
  };

  // // === XUẤT PDF ===
  // const exportToPDF = async () => {
  //   setIsExporting(true);
  //   try {
  //     const element = printRef.current;
  //     const canvas = await html2canvas(element, {
  //       scale: 2,
  //       useCORS: true,
  //       backgroundColor: "#ffffff",
  //     });
  //     const imgData = canvas.toDataURL("image/png");
  //     const pdf = new jsPDF("p", "mm", "a4");
  //     const imgWidth = 190;
  //     const pageHeight = 297;
  //     const imgHeight = (canvas.height * imgWidth) / canvas.width;
  //     let heightLeft = imgHeight;
  //     let position = 15;

  //     pdf.setFont("helvetica", "bold");
  //     pdf.setFontSize(16);
  //     pdf.text("BÁO CÁO THỐNG KÊ", 105, 10, { align: "center" });

  //     pdf.addImage(imgData, "PNG", 10, position, imgWidth, imgHeight);
  //     heightLeft -= pageHeight;

  //     while (heightLeft >= 0) {
  //       position = heightLeft - imgHeight + 20;
  //       pdf.addPage();
  //       pdf.addImage(imgData, "PNG", 10, position, imgWidth, imgHeight);
  //       heightLeft -= pageHeight;
  //     }

  //     const fileName = `BaoCao_ThongKe_${dateRange}_${new Date()
  //       .toISOString()
  //       .slice(0, 10)}.pdf`;
  //     pdf.save(fileName);
  //   } catch {
  //     alert("Lỗi khi xuất PDF!");
  //   } finally {
  //     setIsExporting(false);
  //   }
  // };

  return (
    <div className="space-y-6" ref={printRef}>
      {/* HEADER + BỘ LỌC + XUẤT FILE */}
      <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-800">
            Thống kê chi tiết
          </h1>
          <p className="text-sm text-gray-600 mt-1">
            Phân tích doanh thu, chuyến đi và hiệu suất hoạt động
          </p>
        </div>

        <div className="flex flex-wrap gap-2 items-center">
          {/* Bộ lọc thời gian */}
          {["7days", "month", "year"].map((range) => (
            <button
              key={range}
              onClick={() => setDateRange(range)}
              disabled={isExporting}
              className={`px-4 py-2 rounded-lg text-sm font-medium cursor-pointer transition ${
                dateRange === range
                  ? "bg-blue-600 text-white"
                  : "bg-white border border-gray-300 text-gray-700 hover:bg-gray-50"
              } ${isExporting ? "opacity-50 cursor-not-allowed" : ""}`}
            >
              {range === "7days"
                ? "7 ngày"
                : range === "30days"
                ? "30 ngày"
                : range === "month"
                ? "Tháng này"
                : "Năm nay"}
            </button>
          ))}

          {/* Nút xuất file */}
          <div className="flex gap-2 ml-2">
            <button
              onClick={exportToExcel}
              disabled={isExporting}
              className={`flex items-center gap-2 px-4 py-2 cursor-pointer rounded-lg text-sm font-medium transition ${
                isExporting
                  ? "bg-gray-400 text-white cursor-not-allowed"
                  : "bg-green-600 text-white hover:bg-green-700"
              }`}
            >
              <FileSpreadsheet size={16} />
              {isExporting ? "Đang xuất..." : "Excel"}
            </button>
            {/* <button
              onClick={exportToPDF}
              disabled={isExporting}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition ${
                isExporting
                  ? "bg-gray-400 text-white cursor-not-allowed"
                  : "bg-red-600 text-white hover:bg-red-700"
              }`}
            >
              <FileText size={16} />
              {isExporting ? "Đang xuất..." : "PDF"}
            </button> */}
          </div>
        </div>
      </div>

      {/* TỔNG QUAN NHANH */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="bg-white p-6 rounded-xl shadow-sm border">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Tổng doanh thu</p>
              <p className="text-2xl font-bold text-blue-600">
                {(totalRevenue / 1_000_000).toFixed(1)}M
              </p>
            </div>
            <DollarSign size={32} className="text-blue-600 opacity-80" />
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-sm border">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Tổng chuyến đi</p>
              <p className="text-2xl font-bold text-green-600">{totalTrips}</p>
            </div>
            <Car size={32} className="text-green-600 opacity-80" />
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-sm border">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Trung bình/chuyến</p>
              <p className="text-2xl font-bold text-purple-600">
                {avgPerTrip > 0 ? Math.round(avgPerTrip / 1000) + "k" : "0"}
              </p>
            </div>
            <TrendingUp size={32} className="text-purple-600 opacity-80" />
          </div>
        </div>

        <div className="bg-white p-6 rounded-xl shadow-sm border">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Tài xế hoạt động</p>
              <p className="text-2xl font-bold text-orange-600">
                {driverPerformance?.length || 0}
              </p>
            </div>
            <Users size={32} className="text-orange-600 opacity-80" />
          </div>
        </div>
      </div>

      {/* BIỂU ĐỒ */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Doanh thu */}
        <div className="bg-white p-6 rounded-xl shadow-sm border">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">
            Doanh thu theo ngày
          </h3>
          {loadingRevenue ? (
            <p className="text-center py-8 text-gray-500">Đang tải...</p>
          ) : (
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={revenueData || []}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip formatter={(v) => `${(v / 1000).toFixed(0)}k`} />
                <Line
                  type="monotone"
                  dataKey="value"
                  stroke="#3B82F6"
                  strokeWidth={2}
                  dot={{ fill: "#3B82F6" }}
                />
              </LineChart>
            </ResponsiveContainer>
          )}
        </div>
        {/* Top 5 tài xế */}
        <div className="bg-white p-6 rounded-xl shadow-sm border">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">
            Top 10 tài xế xuất sắc
          </h3>
          <div className="space-y-3 max-h-80 overflow-y-auto custom-scrollbar">
            {(driverPerformance || []).slice(0, 10).map((driver, index) => (
              <div
                key={driver.ma_tai_xe}
                className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition"
              >
                <div className="flex items-center gap-3">
                  <div
                    className={`w-8 h-8 rounded-full flex items-center justify-center text-white font-bold text-sm ${
                      index === 0
                        ? "bg-yellow-500"
                        : index === 1
                        ? "bg-gray-400"
                        : index === 2
                        ? "bg-orange-600"
                        : "bg-blue-600"
                    }`}
                  >
                    {index + 1}
                  </div>
                  <div>
                    <p className="font-medium text-sm">{driver.ho_ten}</p>
                    <p className="text-xs text-gray-600">{driver.ma_tai_xe}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="font-semibold text-sm">
                    {driver.tong_chuyen} chuyến
                  </p>
                  <p className="text-xs text-gray-600">
                    {(driver.doanh_thu / 1_000_000).toFixed(1)}M
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* PHÂN BỐ + TOP TÀI XẾ */}
      <div className="gap-6">
        {/* Chuyến đi */}
        <div className="bg-white p-6 rounded-xl shadow-sm border">
          <h3 className="text-lg font-semibold text-gray-800 mb-4">
            Chuyến đi theo ngày
          </h3>
          {loadingTrips ? (
            <p className="text-center py-8 text-gray-500">Đang tải...</p>
          ) : (
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={tripData || []}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="trips" fill="#10B981" radius={[8, 8, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          )}
        </div>
      </div>
    </div>
  );
}

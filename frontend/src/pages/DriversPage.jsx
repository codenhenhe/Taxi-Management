// src/pages/DriversPage.jsx
import PageLayout from "../components/common/PageLayout";
import useFetch from "../hooks/useFetch";
import { Star, Phone, IdCard } from "lucide-react";

export default function DriversPage() {
  const { data, loading, error } = useFetch("/api/drivers");

  const searchFields = [
    { key: "ma_tai_xe", placeholder: "MÃ TÀI XẾ" },
    { key: "ho_ten", placeholder: "HỌ TÊN" },
  ];

  const columns = [
    { key: "ma_tai_xe", header: "Mã TX" },
    { key: "ho_ten", header: "Họ tên" },
    {
      key: "trang_thai",
      header: "Trạng thái",
      render: (item) => (
        <span
          className={`px-2 py-1 rounded-full text-xs ${
            item.trang_thai === "active"
              ? "bg-green-100 text-green-800"
              : "bg-gray-100 text-gray-800"
          }`}
        >
          {item.trang_thai === "active" ? "Đang rảnh" : "Đang chạy"}
        </span>
      ),
    },
    {
      key: "danh_gia",
      header: "Đánh giá",
      render: (item) => (
        <div className="flex items-center gap-1">
          <Star size={14} className="text-yellow-500 fill-current" />
          <span>{item.danh_gia || 0}</span>
        </div>
      ),
    },
  ];

  const detailFields = [
    { key: "ma_tai_xe", label: "MÃ TÀI XẾ", readOnly: true },
    { key: "ho_ten", label: "HỌ TÊN" },
    { key: "so_dien_thoai", label: "SỐ ĐIỆN THOẠI", type: "tel" },
    { key: "so_cmt", label: "SỐ CMND/CCCD" },
    { key: "bien_so_xe", label: "BIỂN SỐ XE HIỆN TẠI", readOnly: true },
    {
      key: "trang_thai",
      label: "TRẠNG THÁI",
      type: "select",
      options: ["active", "busy"],
      optionLabels: { active: "Đang rảnh", busy: "Đang chạy" },
    },
  ];

  return (
    <PageLayout
      title="Quản lý TÀI XẾ"
      searchFields={searchFields}
      data={data || []}
      columns={columns}
      detailFields={detailFields}
      loading={loading}
      error={error}
      onAdd={() => alert("Thêm tài xế mới")}
      onSave={(data) => alert("Lưu: " + JSON.stringify(data))}
    />
  );
}

// src/pages/TripsPage.jsx
import PageLayout from "../components/common/PageLayout";
import useFetch from "../hooks/useFetch";

export default function TripsPage() {
  const { data, loading, error } = useFetch("/api/trips");

  const searchFields = [
    { key: "ma_chuyen", placeholder: "MÃ CHUYẾN" },
    { key: "ma_khach_hang", placeholder: "MÃ KH" },
  ];

  const columns = [
    { key: "ma_chuyen", header: "Mã chuyến" },
    { key: "ma_khach_hang", header: "Mã KH" },
    { key: "ma_xe", header: "Mã xe" },
    { key: "diem_don", header: "Điểm đón" },
    { key: "diem_tra", header: "Điểm trả" },
    { key: "so_km", header: "KM" },
    { key: "cuoc_phi", header: "Cước phí" },
  ];

  const detailFields = [
    { key: "ma_chuyen", label: "MÃ CHUYẾN", readOnly: true },
    { key: "ma_khach_hang", label: "MÃ KHÁCH HÀNG" },
    { key: "ma_xe", label: "MÃ XE" },
    { key: "diem_don", label: "ĐIỂM ĐÓN" },
    { key: "diem_tra", label: "ĐIỂM TRẢ" },
    { key: "thoi_gian_nhan", label: "TG NHẬN", type: "datetime-local" },
    { key: "thoi_gian_tra", label: "TG TRẢ", type: "datetime-local" },
    { key: "so_km", label: "SỐ KM", type: "number" },
    { key: "cuoc_phi", label: "CƯỚC PHÍ", type: "number" },
  ];

  return (
    <PageLayout
      title="QUẢN LÝ CHUYẾN ĐI"
      searchFields={searchFields}
      data={data || []}
      columns={columns}
      detailFields={detailFields}
      loading={loading}
      error={error}
      onAdd={() => alert("Tạo chuyến mới")}
      onSave={(data) => alert("Cập nhật: " + JSON.stringify(data))}
    />
  );
}

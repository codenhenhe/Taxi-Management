// src/pages/CustomersPage.jsx
import PageLayout from "../components/common/PageLayout";
import useFetch from "../hooks/useFetch";

export default function CustomersPage() {
  const { data, loading, error } = useFetch("/api/customers");

  const searchFields = [
    { key: "ma_khach_hang", placeholder: "MÃ KH" },
    { key: "ho_ten", placeholder: "HỌ TÊN" },
  ];

  const columns = [
    { key: "ma_khach_hang", header: "Mã KH" },
    { key: "ho_ten", header: "Họ tên" },
    { key: "so_dien_thoai", header: "SĐT" },
    { key: "tong_chuyen", header: "Tổng chuyến" },
  ];

  const detailFields = [
    { key: "ma_khach_hang", label: "MÃ KH", readOnly: true },
    { key: "ho_ten", label: "HỌ TÊN" },
    { key: "so_dien_thoai", label: "SỐ ĐIỆN THOẠI" },
    { key: "dia_chi_thuong_dung", label: "ĐỊA CHỈ THƯỜNG DÙNG" },
  ];

  return (
    <PageLayout
      title="QUẢN LÝ KHÁCH HÀNG"
      searchFields={searchFields}
      data={data || []}
      columns={columns}
      detailFields={detailFields}
      loading={loading}
      error={error}
      onAdd={() => alert("Thêm khách hàng")}
      onSave={(data) => alert("Lưu: " + JSON.stringify(data))}
    />
  );
}

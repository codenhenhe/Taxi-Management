// src/pages/DispatchPage.jsx
import PageLayout from "../components/common/PageLayout";
import useFetch from "../hooks/useFetch";

export default function DispatchPage() {
  const { data, loading, error } = useFetch("/api/dispatch");

  const searchFields = [{ key: "ma_chuyen", placeholder: "MÃ CHUYẾN" }];

  const columns = [
    { key: "ma_chuyen", header: "Mã chuyến" },
    { key: "khach_hang", header: "Khách hàng" },
    { key: "diem_don", header: "Điểm đón" },
    {
      key: "trang_thai",
      header: "Trạng thái",
      render: (item) => (
        <span className="px-2 py-1 bg-blue-100 text-blue-800 text-xs rounded-full">
          Đang chờ
        </span>
      ),
    },
  ];

  const detailFields = [
    { key: "ma_chuyen", label: "MÃ CHUYẾN", readOnly: true },
    { key: "khach_hang", label: "KHÁCH HÀNG" },
    { key: "diem_don", label: "ĐIỂM ĐÓN" },
    { key: "diem_tra", label: "ĐIỂM TRẢ" },
    { key: "tai_xe_goi_y", label: "GỢI Ý TÀI XẾ" },
  ];

  return (
    <PageLayout
      title="PHÂN CÔNG XE"
      searchFields={searchFields}
      data={data || []}
      columns={columns}
      detailFields={detailFields}
      loading={loading}
      error={error}
      onAdd={() => alert("Tạo yêu cầu mới")}
      onSave={(data) => alert("Giao xe: " + JSON.stringify(data))}
    />
  );
}

// src/pages/VehiclesPage.jsx
import PageLayout from "../components/common/PageLayout";
import useFetch from "../hooks/useFetch";

export default function VehiclesPage() {
  const { data, loading, error } = useFetch("/api/vehicles");

  const searchFields = [
    { key: "bien_so", placeholder: "BIỂN SỐ" },
    { key: "ma_loai", placeholder: "LOẠI XE" },
  ];

  const columns = [
    { key: "bien_so", header: "Biển số" },
    { key: "hang_xe", header: "Hãng xe" },
    { key: "nam_san_xuat", header: "Năm SX" },
    {
      key: "trang_thai",
      header: "Trạng thái",
      render: (item) => (
        <span
          className={`px-2 py-1 rounded-full text-xs ${
            item.trang_thai === "active"
              ? "bg-green-100 text-green-800"
              : "bg-red-100 text-red-800"
          }`}
        >
          {item.trang_thai === "active" ? "Hoạt động" : "Bảo trì"}
        </span>
      ),
    },
  ];

  const detailFields = [
    { key: "bien_so", label: "BIỂN SỐ", readOnly: true },
    { key: "ma_loai", label: "LOẠI XE" },
    { key: "hang_xe", label: "HÃNG XE" },
    { key: "nam_san_xuat", label: "NĂM SẢN XUẤT", type: "number" },
    { key: "ma_tai_xe", label: "TÀI XẾ HIỆN TẠI" },
  ];

  return (
    <PageLayout
      title="Quản lý XE"
      searchFields={searchFields}
      data={data || []}
      columns={columns}
      detailFields={detailFields}
      loading={loading}
      error={error}
      onAdd={() => alert("Thêm xe mới")}
      onSave={(data) => alert("Lưu: " + JSON.stringify(data))}
    />
  );
}

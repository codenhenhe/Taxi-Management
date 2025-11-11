import PageLayout from "../components/common/PageLayout";
import useFetch from "../hooks/useFetch";

export default function VehicleTypesPage() {
  const { data, loading, error } = useFetch("/api/vehicle-types");

  const searchFields = [
    { key: "ma_loai", placeholder: "MÃ LOẠI" },
    { key: "ten_loai", placeholder: "TÊN LOẠI" },
  ];

  const columns = [
    { key: "ma_loai", header: "Mã Loại" },
    { key: "ten_loai", header: "Tên Loại" },
    { key: "so_xe", header: "Số Xe", render: (item) => item.so_xe || 0 },
    {
      key: "trang_thai",
      header: "Trạng Thái",
      render: (item) => (
        <span
          className={`px-2 py-1 rounded-full text-xs ${
            item.trang_thai === "active"
              ? "bg-green-100 text-green-800"
              : "bg-yellow-100 text-yellow-800"
          }`}
        >
          {item.trang_thai === "active" ? "Hoạt động" : "Tạm dừng"}
        </span>
      ),
    },
  ];

  const detailFields = [
    { key: "ma_loai", label: "MÃ LOẠI", readOnly: true },
    { key: "ten_loai", label: "TÊN LOẠI" },
    { key: "so_xe", label: "SỐ XE", readOnly: true },
    {
      key: "trang_thai",
      label: "TRẠNG THÁI",
      type: "select",
      options: ["active", "inactive"],
    },
  ];

  return (
    <PageLayout
      title="Quản lý LOẠI XE"
      searchFields={searchFields}
      data={data || []} // ← QUAN TRỌNG: không để null
      columns={columns}
      detailFields={detailFields}
      loading={loading}
      error={error}
      onAdd={() => alert("Thêm mới")}
      onSave={() => alert("Lưu")}
    />
  );
}

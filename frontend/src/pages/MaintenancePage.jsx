// src/pages/MaintenancePage.jsx
import PageLayout from "../components/common/PageLayout";
import useFetch from "../hooks/useFetch";

export default function MaintenancePage() {
  const { data, loading, error } = useFetch("/api/maintenance");

  const searchFields = [{ key: "bien_so", placeholder: "BIỂN SỐ" }];

  const columns = [
    { key: "bien_so", header: "Biển số" },
    { key: "ngay_bao_tri", header: "Ngày bảo trì" },
    { key: "chi_phi", header: "Chi phí" },
    { key: "mo_ta", header: "Mô tả" },
  ];

  const detailFields = [
    { key: "bien_so", label: "BIỂN SỐ", readOnly: true },
    { key: "ngay_bao_tri", label: "NGÀY BẢO TRÌ", type: "date" },
    { key: "chi_phi", label: "CHI PHÍ (đ)", type: "number" },
    { key: "mo_ta", label: "MÔ TẢ", type: "textarea" },
  ];

  return (
    <PageLayout
      title="BẢO TRÌ XE"
      searchFields={searchFields}
      data={data || []}
      columns={columns}
      detailFields={detailFields}
      loading={loading}
      error={error}
      onAdd={() => alert("Thêm lịch bảo trì")}
      onSave={(data) => alert("Lưu: " + JSON.stringify(data))}
    />
  );
}

// src/pages/PricingPage.jsx
import PageLayout from "../components/common/PageLayout";
import useFetch from "../hooks/useFetch";

export default function PricingPage() {
  const { data, loading, error } = useFetch("/api/pricing");

  const searchFields = [{ key: "ma_loai", placeholder: "LOẠI XE" }];

  const columns = [
    { key: "ma_loai", header: "Loại xe" },
    { key: "gia_mo_cua", header: "Giá mở cửa" },
    { key: "gia_km_dau", header: "KM đầu" },
    { key: "gia_km_sau", header: "KM sau" },
  ];

  const detailFields = [
    { key: "ma_loai", label: "LOẠI XE", readOnly: true },
    { key: "gia_mo_cua", label: "GIÁ MỞ CỬA (đ)", type: "number" },
    { key: "gia_km_dau", label: "GIÁ KM ĐẦU (đ/km)", type: "number" },
    { key: "gia_km_sau", label: "GIÁ KM SAU (đ/km)", type: "number" },
  ];

  return (
    <PageLayout
      title="BẢNG GIÁ"
      searchFields={searchFields}
      data={data || []}
      columns={columns}
      detailFields={detailFields}
      loading={loading}
      error={error}
      onAdd={() => alert("Thêm bảng giá")}
      onSave={(data) => alert("Lưu: " + JSON.stringify(data))}
    />
  );
}

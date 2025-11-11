// src/components/common/PageLayout.jsx
import { useState } from "react";
import SearchBox from "./SearchBox";
import DataTable from "./DataTable";
import DetailPanel from "./DetailPanel";

export default function PageLayout({
  title,
  searchFields = [],
  data = [], // ← Mặc định là mảng rỗng
  columns = [],
  detailFields = [],
  onAdd,
  onSave,
  loading = false,
  error = null,
}) {
  const [search, setSearch] = useState({});
  const [selectedItem, setSelectedItem] = useState(null);

  // Đảm bảo data luôn là mảng
  const safeData = Array.isArray(data) ? data : [];

  // Lọc an toàn
  const filteredData = safeData.filter((item) =>
    Object.keys(search).every((key) =>
      String(item[key] || "")
        .toLowerCase()
        .includes(search[key].toLowerCase())
    )
  );

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold text-gray-800">{title}</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* CỘT TRÁI */}
        <div className="lg:col-span-2 space-y-6">
          <SearchBox fields={searchFields} onSearch={setSearch} />
          <DataTable
            data={filteredData}
            columns={columns}
            loading={loading}
            error={error}
            onRowClick={setSelectedItem}
          />
        </div>

        {/* CỘT PHẢI */}
        <div>
          <DetailPanel
            item={selectedItem}
            fields={detailFields}
            onSave={onSave}
            onAdd={onAdd}
          />
        </div>
      </div>
    </div>
  );
}

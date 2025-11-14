// src/components/common/PageLayout.jsx
import SearchBox from "./SearchBox";
import { Plus } from "lucide-react";

// 1. Xóa hết props không cần thiết: data, columns, detailFields, onSave, loading, error
// 2. Thêm prop mới: onAddClick, searchValues, onSearch, children
export default function PageLayout({
  title,
  searchFields = [],
  onAddClick, // Hàm để gọi khi nhấn nút Thêm mới
  searchValues,
  onSearch,
  children, // Prop đặc biệt để render DataTable
}) {
  return (
    <div className="p-6 space-y-4">
      {/* CẤP 1: TIÊU ĐỀ & NÚT THÊM MỚI */}
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold text-gray-800">{title}</h1>
        <button
          onClick={onAddClick} // <-- 3. Gọi prop
          className="flex items-center cursor-pointer gap-2 bg-green-600 text-white px-5 py-2 rounded-md hover:bg-green-700 shadow-sm"
        >
          <Plus size={18} />
          Thêm mới
        </button>
      </div>

      {/* CẤP 2: THANH TÌM KIẾM */}
      <SearchBox
        fields={searchFields}
        onSearch={onSearch}
        searchValues={searchValues}
      />

      {/* CẤP 3: DỮ LIỆU */}
      <div className="mt-4">
        {children} {/* <-- 4. Render DataTable ở đây */}
      </div>
    </div>
  );
}

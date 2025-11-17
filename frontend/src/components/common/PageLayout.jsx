// src/components/common/PageLayout.jsx
import { Plus, Download } from "lucide-react";

export default function PageLayout({
  title,
  onAddClick,
  onExport,
  children, // Prop đặc biệt để render SearchBox và DataTable
}) {
  return (
    <div className="p-5 space-y-4">
      {/* CẤP 1: TIÊU ĐỀ & NÚT THÊM MỚI */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <h1 className="text-2xl font-bold text-gray-800">{title}</h1>
        <div className="flex gap-3">
          {onExport && (
            <button
              onClick={onExport}
              className="flex items-center cursor-pointer gap-2 bg-white text-green-700 border border-green-600 px-4 py-2 rounded-md hover:bg-green-50 shadow-sm transition-colors"
            >
              <Download size={18} />
              Xuất Excel
            </button>
          )}
          <button
            onClick={onAddClick}
            className="flex items-center cursor-pointer gap-2 bg-green-600 text-white px-5 py-2 rounded-md hover:bg-green-700 shadow-sm"
          >
            <Plus size={18} />
            Thêm mới
          </button>
        </div>
      </div>

      {/* CẤP 2 & 3: DỮ LIỆU (Nội dung sẽ được đưa vào đây) */}
      <div className="mt-4 space-y-4">
        {" "}
        {/* Thêm space-y-4 */}
        {children}
      </div>
    </div>
  );
}

// src/components/common/Pagination.jsx
import { ChevronLeft, ChevronRight } from "lucide-react";

export default function Pagination({
  currentPage, // Trang hiện tại (bắt đầu từ 0)
  totalPages, // Tổng số trang (từ API)
  onPageChange, // Hàm gọi khi nhấn nút (newPage) => {}
}) {
  if (totalPages <= 1) {
    return null; // Ẩn nếu chỉ có 1 trang
  }

  const handlePrev = () => {
    if (currentPage > 0) {
      onPageChange(currentPage - 1);
    }
  };

  const handleNext = () => {
    if (currentPage < totalPages - 1) {
      onPageChange(currentPage + 1);
    }
  };

  return (
    <div className="flex items-center justify-center gap-4 py-4">
      <button
        onClick={handlePrev}
        disabled={currentPage === 0}
        className="flex items-center gap-1 px-3 py-1.5 text-sm font-medium
                   bg-white border border-gray-300 rounded-md
                   hover:bg-gray-200 cursor-pointer
                   disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <ChevronLeft size={16} />
        Trước
      </button>

      <span className="text-sm text-gray-700">
        Trang
        <strong className="font-medium px-1">{currentPage + 1}</strong>/
        <strong className="font-medium px-1">{totalPages}</strong>
      </span>

      <button
        onClick={handleNext}
        disabled={currentPage === totalPages - 1}
        className="flex items-center gap-1 px-3 py-1.5 text-sm font-medium
                   bg-white border border-gray-300 rounded-md
                   hover:bg-gray-200 cursor-pointer
                   disabled:opacity-50 disabled:cursor-not-allowed"
      >
        Sau
        <ChevronRight size={16} />
      </button>
    </div>
  );
}

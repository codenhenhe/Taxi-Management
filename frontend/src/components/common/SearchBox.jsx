// src/components/common/SearchBox.jsx
import { useState } from "react";
import {
  Search,
  Filter,
  ArrowDownUp,
  RotateCcw,
  ArrowUp,
  ArrowDown,
} from "lucide-react";

export default function SearchBox({
  searchFields = [],
  sortFields = [],
  onFilterAndSort,
  initialParams = { filters: {}, sort: {} },
}) {
  const [filters, setFilters] = useState(initialParams.filters);
  const [sortBy, setSortBy] = useState(
    initialParams.sort.by || (sortFields[0] ? sortFields[0].key : "")
  );
  const [sortDir, setSortDir] = useState(initialParams.sort.dir || "desc");

  const toggleSortDir = () => {
    setSortDir((prev) => (prev === "asc" ? "desc" : "asc"));
  };

  const handleFilterChange = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onFilterAndSort({
      filters,
      sort: { by: sortBy, dir: sortDir },
    });
  };

  const handleReset = () => {
    const defaultSortBy = sortFields[0] ? sortFields[0].key : "";
    const defaultSortDir = "desc";
    setFilters({});
    setSortBy(defaultSortBy);
    setSortDir(defaultSortDir);
    onFilterAndSort({
      filters: {},
      sort: { by: defaultSortBy, dir: defaultSortDir },
    });
  };

  return (
    <form
      onSubmit={handleSubmit}
      // Sửa: Tăng padding từ p-3 lên p-4 cho đẹp hơn
      className="bg-white p-4 rounded-xl border border-gray-200 shadow-sm"
    >
      {/* Hàng 1: Bộ lọc (Filters) */}
      <div className="flex flex-wrap items-center gap-3">
        <span className="text-sm font-medium text-gray-500 shrink-0">
          <Filter size={16} className="inline mr-1" />
          BỘ LỌC:
        </span>

        {/* --- ĐÂY LÀ PHẦN SỬA --- */}
        {searchFields.map((field) => (
          <div key={field.key}>
            {field.type === "select" ? (
              // 1. NẾU LÀ "SELECT"
              <select
                value={filters[field.key] || ""}
                onChange={(e) => handleFilterChange(field.key, e.target.value)}
                className="
                  px-3 py-1.5 text-sm rounded-md
                  bg-gray-100 border border-gray-200
                  focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500
                "
              >
                <option value="">-- {field.label} --</option>
                {field.options.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
            ) : field.type === "date" ? ( // 2. (ELSE IF) NẾU LÀ "DATE"
              <div className="flex items-center gap-1.5">
                {field.label && (
                  <label className="text-sm text-gray-600 shrink-0">
                    {field.label}:
                  </label>
                )}
                <input
                  type="date"
                  value={filters[field.key] || ""}
                  onChange={(e) =>
                    handleFilterChange(field.key, e.target.value)
                  }
                  className="
                    px-3 py-1.5 text-sm rounded-md
                    bg-gray-100 border border-gray-200
                    focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500
                  "
                />
              </div>
            ) : (
              // 3. (ELSE) MẶC ĐỊNH LÀ "TEXT"
              <div className="relative">
                <input
                  type="text"
                  placeholder={field.placeholder}
                  value={filters[field.key] || ""}
                  onChange={(e) =>
                    handleFilterChange(field.key, e.target.value)
                  }
                  className="
                    pl-8 pr-3 py-1.5 text-sm rounded-md w-48
                    bg-gray-100 border border-gray-200
                    focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500
                  "
                />
                <Search
                  size={16}
                  className="absolute left-2.5 top-1/2 -translate-y-1/2 text-gray-400"
                />
              </div>
            )}
          </div>
        ))}
        {/* --- KẾT THÚC SỬA --- */}
      </div>

      <hr className="my-3 border-gray-200" />

      {/* Hàng 2: Sắp xếp & Nút hành động */}
      <div className="flex flex-wrap justify-between items-center gap-3">
        {/* Sắp xếp (Giữ nguyên logic của bạn) */}
        <div className="flex items-center gap-2">
          <span className="text-sm font-medium text-gray-500">
            <ArrowDownUp size={16} className="inline mr-1" />
            SẮP XẾP:
          </span>
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="
              px-3 py-1.5 text-sm rounded-md
              bg-gray-100 border border-gray-200
              focus:bg-white focus:outline-none focus:ring-2 focus:ring-blue-500
            "
          >
            {sortFields.map((field) => (
              <option key={field.key} value={field.key}>
                {field.label}
              </option>
            ))}
          </select>

          <button
            type="button"
            onClick={toggleSortDir}
            className="
              flex items-center gap-1.5 px-3 py-1.5 text-sm rounded-md
              bg-gray-100 text-gray-700 border border-gray-200
              hover:bg-gray-200 transition-colors
            "
            title="Đổi chiều sắp xếp"
          >
            {sortDir === "asc" ? (
              <>
                <ArrowUp size={16} />
                <span>Tăng dần</span>
              </>
            ) : (
              <>
                <ArrowDown size={16} />
                <span>Giảm dần</span>
              </>
            )}
          </button>
        </div>

        {/* Nút hành động (Giữ nguyên) */}
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={handleReset}
            className="
              flex items-center gap-1.5 px-3 py-1.5 text-sm rounded-md
              bg-white text-gray-700 border border-gray-300
              hover:bg-gray-50
            "
          >
            <RotateCcw size={14} />
            Reset
          </button>
          <button
            type="submit"
            className="
              flex items-center gap-1.5 px-4 py-1.5 text-sm rounded-md
              bg-blue-600 text-white
              hover:bg-blue-700 shadow-sm
            "
          >
            <Filter size={14} />
            Lọc
          </button>
        </div>
      </div>
    </form>
  );
}

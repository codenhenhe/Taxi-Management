// src/components/common/SearchBox.jsx
import { Search } from "lucide-react";

export default function SearchBox({ fields, onSearch, searchValues }) {
  const handleChange = (key, value) => {
    onSearch((prev) => ({
      ...prev,
      [key]: value,
    }));
  };

  return (
    // 1. Giảm padding container (từ p-3 -> p-2)
    <div className="bg-white p-2 rounded-lg shadow-sm border">
      {/* 2. Giảm gap (từ gap-3 -> gap-2) */}
      <div className="flex flex-wrap items-center gap-2">
        <Search size={18} className="text-gray-400 ml-1" />

        {fields.map((field) => (
          <input
            key={field.key}
            type="text"
            placeholder={field.placeholder}
            value={searchValues[field.key] || ""}
            onChange={(e) => handleChange(field.key, e.target.value)}
            // 3. Giảm padding dọc của input (từ py-2 -> py-1)
            className="px-3 py-1 border rounded-md text-sm
                       focus:ring-2 focus:ring-green-500 outline-none"
          />
        ))}
      </div>
    </div>
  );
}

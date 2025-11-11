// src/components/common/SearchBox.jsx
import { Search, RotateCcw } from "lucide-react";

export default function SearchBox({ fields, onSearch }) {
  const handleChange = (key, value) => {
    onSearch((prev) => ({ ...prev, [key]: value }));
  };

  const handleReset = () => onSearch({});

  return (
    <div className="bg-white p-5 rounded-lg shadow-sm border">
      <h3 className="text-lg font-semibold text-green-600 mb-4 flex items-center gap-2">
        <Search size={20} /> TÌM KIẾM
      </h3>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
        {fields.map((field) => (
          <input
            key={field.key}
            type="text"
            placeholder={field.placeholder}
            onChange={(e) => handleChange(field.key, e.target.value)}
            className="px-3 py-2 border rounded-md focus:ring-2 focus:ring-green-500 outline-none"
          />
        ))}
      </div>
      <div className="mt-4 flex gap-3">
        <button className="flex items-center gap-2 bg-green-600 text-white px-5 py-2 rounded-md hover:bg-green-700">
          <Search size={16} /> Tìm kiếm
        </button>
        <button
          onClick={handleReset}
          className="flex items-center gap-2 bg-pink-600 text-white px-5 py-2 rounded-md hover:bg-pink-700"
        >
          <RotateCcw size={16} /> Reset
        </button>
      </div>
    </div>
  );
}

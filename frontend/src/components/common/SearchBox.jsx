import { Search } from "lucide-react";

export default function SearchBox({ fields, onSearch, searchValues }) {
  const handleChange = (key, value) => {
    onSearch((prev) => ({
      ...prev,
      [key]: value,
    }));
  };

  return (
    <div className="bg-white p-4 rounded-xl border border-gray-500 shadow-md">
      <div className="flex flex-wrap gap-3">
        {fields.map((field) => (
          <div
            key={field.key}
            className="
              flex items-center gap-2
              px-3 py-2
              bg-gray-50
              border border-gray-500
              rounded-lg
              hover:bg-gray-200
              transition-colors
              focus-within:border-green-1000
            "
          >
            <Search size={16} className="text-gray-500" />

            <input
              type="text"
              placeholder={field.placeholder}
              value={searchValues[field.key] || ""}
              onChange={(e) => handleChange(field.key, e.target.value)}
              className="
                bg-transparent
                text-sm
                placeholder:text-gray-400
                focus:outline-none
                w-40
              "
            />
          </div>
        ))}
      </div>
    </div>
  );
}

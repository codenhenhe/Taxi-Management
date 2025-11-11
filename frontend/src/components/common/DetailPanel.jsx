// src/components/common/DetailPanel.jsx
import { useState, useEffect } from "react";

export default function DetailPanel({ item, fields, onSave, onAdd }) {
  const [formData, setFormData] = useState({});

  // Cập nhật form khi item thay đổi
  useEffect(() => {
    if (item) {
      setFormData(item);
    } else {
      setFormData({});
    }
  }, [item]);

  const handleChange = (key, value) => {
    setFormData((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSave?.(formData);
  };

  return (
    <div className="bg-white p-5 rounded-lg shadow-sm border">
      <h3 className="text-lg font-semibold text-blue-600 mb-4">
        THÔNG TIN CHI TIẾT
      </h3>
      <form onSubmit={handleSubmit} className="space-y-3 text-sm">
        {fields.map((field) => (
          <div key={field.key}>
            <label className="block text-gray-600">{field.label}</label>
            <input
              type={field.type || "text"}
              value={formData[field.key] || ""}
              onChange={(e) => handleChange(field.key, e.target.value)}
              readOnly={field.readOnly}
              className={`w-full mt-1 px-3 py-2 border rounded-md ${
                field.readOnly ? "bg-gray-50" : ""
              } focus:outline-none focus:ring-2 focus:ring-blue-500`}
            />
          </div>
        ))}

        <div className="flex gap-2">
          <button
            type="submit"
            className="flex-1 bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
          >
            Lưu
          </button>
          <button
            type="button"
            onClick={() => setFormData(item || {})}
            className="flex-1 border py-2 rounded hover:bg-gray-50"
          >
            Hủy
          </button>
        </div>

        <button
          type="button"
          onClick={onAdd}
          className="w-full border border-dashed border-blue-400 text-blue-600 py-2 rounded hover:bg-blue-50"
        >
          + Thêm mới
        </button>
      </form>
    </div>
  );
}

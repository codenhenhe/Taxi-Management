// src/components/common/EditModal.jsx
import { useState, useEffect } from "react";

export default function EditModal({
  isOpen,
  onClose,
  onSave,
  fields,
  item,
  title,
}) {
  const [formData, setFormData] = useState({});
  const [isSaving, setIsSaving] = useState(false);

  useEffect(() => {
    // Chỉ cập nhật form nếu modal đang mở và có item
    if (isOpen && item) {
      setFormData(item);
    }
  }, [item, isOpen]);

  const handleChange = (key, value) => {
    setFormData((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSaving(true);
    const success = await onSave(formData);
    setIsSaving(false);
    if (success) {
      onClose();
    }
  };

  // --- THAY ĐỔI CHÍNH BẮT ĐẦU TỪ ĐÂY ---

  // 1. Xóa: if (!isOpen) return null;

  return (
    // Lớp nền mờ
    <div
      className={`
        fixed inset-0 z-40 flex justify-center items-center 
        bg-black/50
        transition-opacity duration-300 ease-in-out 
        ${
          isOpen
            ? "opacity-100 pointer-events-auto"
            : "opacity-0 pointer-events-none"
        }
      `}
      onClick={onClose} // <-- Đóng khi nhấn vào nền
    >
      {/* Nội dung Modal */}
      <div
        className={`
          bg-white p-6 rounded-lg shadow-xl z-50 w-full max-w-md 
          transition-all duration-300 ease-in-out 
          ${isOpen ? "opacity-100 scale-100" : "opacity-0 scale-95"}
        `}
        onClick={(e) => e.stopPropagation()} // <-- Ngăn đóng khi nhấn vào modal
      >
        <h3 className="text-2xl text-center font-semibold text-blue-600 mb-7">
          {title}
        </h3>

        <form onSubmit={handleSubmit} className="space-y-4">
          {/* ... (Nội dung form giữ nguyên) ... */}
          {fields.map((field) => (
            <div
              key={field.key}
              className="grid grid-cols-3 gap-2 items-center"
            >
              <label className="col-span-1 block text-sm font-medium text-gray-700">
                {field.label}
              </label>
              <div className="col-span-2">
                {" "}
                {/* Thêm div bọc input/select */}
                {field.type === "select" ? (
                  <select
                    name={field.key}
                    value={formData[field.key] || ""}
                    onChange={(e) => handleChange(field.key, e.target.value)}
                    className={`w-full px-3 py-2 border rounded-md ${
                      field.readOnly ? "bg-gray-50" : ""
                    }`}
                    disabled={field.readOnly} // Thêm disabled
                  >
                    <option value="">-- Chọn --</option>
                    {field.options.map((opt) => (
                      <option key={opt} value={opt}>
                        {field.optionLabels[opt] || opt}
                      </option>
                    ))}
                  </select>
                ) : (
                  <input
                    type={field.type || "text"}
                    name={field.key}
                    value={formData[field.key] || ""}
                    onChange={(e) => handleChange(field.key, e.target.value)}
                    readOnly={field.readOnly}
                    className={`w-full px-3 py-2 border rounded-md ${
                      field.readOnly ? "bg-gray-50" : ""
                    }`}
                  />
                )}
              </div>
            </div>
          ))}

          <div className="flex gap-3 pt-4">
            <button
              type="submit"
              disabled={isSaving}
              className="flex-1 bg-blue-600 cursor-pointer text-white py-2 rounded-md hover:bg-blue-800 disabled:opacity-50"
            >
              {isSaving ? "Đang lưu..." : "Lưu thay đổi"}
            </button>
            <button
              type="button"
              onClick={onClose}
              className="flex-1 border py-2 bg-red-600 cursor-pointer text-white rounded-md hover:bg-red-800"
            >
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

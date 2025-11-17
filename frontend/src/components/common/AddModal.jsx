// src/components/common/AddModal.jsx
import { useState, useEffect, useCallback } from "react";

export default function AddModal({ isOpen, onClose, onSave, fields, title }) {
  const [formData, setFormData] = useState({});
  const [isSaving, setIsSaving] = useState(false);

  // Hàm tạo form rỗng với giá trị mặc định
  const createEmptyForm = useCallback(() => {
    return fields.reduce((acc, field) => {
      // Sử dụng defaultValue nếu có, ngược lại để chuỗi rỗng
      acc[field.key] =
        field.defaultValue !== undefined ? field.defaultValue : "";
      return acc;
    }, {});
  }, [fields]);

  // Reset form mỗi khi modal được mở
  useEffect(() => {
    if (isOpen) {
      setFormData(createEmptyForm());
    }
  }, [isOpen, createEmptyForm]);

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

  if (!isOpen) return null;

  return (
    // Lớp nền mờ
    <div
      className={`
        fixed inset-0 z-40 flex justify-center items-center 
        bg-black/50 
        transition-opacity duration-300 ease-in-out 
        opacity-100 pointer-events-auto
      `}
      onClick={onClose} // Đóng khi nhấn vào nền
    >
      {/* Nội dung Modal */}
      <div
        className={`
          bg-white p-6 rounded-lg shadow-xl z-50 w-full max-w-md 
          transition-all duration-300 ease-in-out 
          opacity-100 scale-100
        `}
        onClick={(e) => e.stopPropagation()} // Ngăn đóng khi nhấn vào modal
      >
        <h3 className="text-2xl font-semibold text-center text-blue-600 mb-7">
          {title}
        </h3>

        <form onSubmit={handleSubmit} className="space-y-4">
          {fields.map((field) => (
            <div
              key={field.key}
              className="grid grid-cols-3 gap-2 items-center"
            >
              <label className="col-span-1 block text-sm font-medium text-gray-700">
                {field.label}
              </label>
              <div className="col-span-2">
                {field.type === "select" ? (
                  <select
                    name={field.key}
                    value={formData[field.key] || ""}
                    onChange={(e) => handleChange(field.key, e.target.value)}
                    disabled={field.readOnly} // Vô hiệu hóa nếu là readOnly
                    className={`w-full px-3 py-2 border rounded-md ${
                      field.readOnly ? "bg-gray-100 cursor-not-allowed" : ""
                    }`}
                  >
                    {/* Ẩn tùy chọn mặc định nếu field là readOnly và đã có giá trị */}
                    {!(field.readOnly && field.defaultValue) && (
                      <option value="">-- Chọn --</option>
                    )}
                    {field.options &&
                      field.options.map((opt) => {
                        // Xử lý linh hoạt cho cả mảng string và mảng object {value, label}
                        const value = typeof opt === "object" ? opt.value : opt;
                        const label = field.optionLabels
                          ? field.optionLabels[value]
                          : opt.label || opt;
                        return (
                          <option key={value} value={value}>
                            {label}
                          </option>
                        );
                      })}
                  </select>
                ) : (
                  <input
                    type={field.type || "text"}
                    name={field.key}
                    value={formData[field.key] || ""}
                    onChange={(e) => handleChange(field.key, e.target.value)}
                    readOnly={field.readOnly}
                    className={`w-full px-3 py-2 border rounded-md ${
                      field.readOnly ? "bg-gray-100 cursor-not-allowed" : ""
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
              {isSaving ? "Đang lưu..." : "Lưu"}
            </button>
            <button
              type="button"
              onClick={onClose}
              className="flex-1 border cursor-pointer bg-red-600 text-white py-2 rounded-md hover:bg-red-800"
            >
              Hủy
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

// src/pages/DriversPage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import EditModal from "../components/common/EditModal"; // <-- Import
import AddModal from "../components/common/AddModal"; // <-- Import
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { Star, Pencil, Trash2 } from "lucide-react"; // <-- Thêm icon

export default function DriversPage() {
  // State dữ liệu
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState({});

  // State quản lý Modal
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  // Cấu hình
  const searchFields = [
    { key: "maTaiXe", placeholder: "MÃ TÀI XẾ" },
    { key: "tenTaiXe", placeholder: "HỌ TÊN" },
  ];

  // --- 1. CẬP NHẬT COLUMNS (Thêm cột "Hành động") ---
  const columns = [
    { key: "maTaiXe", header: "Mã tài xế" },
    { key: "tenTaiXe", header: "Họ tên" },
    { key: "ngaySinh", header: "Ngày sinh" },
    { key: "soDienThoai", header: "Số điện thoại" },
    { key: "soHieuGPLX", header: "Giấy phép lái xe" },
    {
      key: "trangThai",
      header: "Trạng thái",
      render: (item) => (
        <span
          className={`px-2 py-1 rounded-full text-xs ${
            item.trangThai === "DANG_LAM_VIEC"
              ? "bg-green-100 text-green-800"
              : "bg-gray-100 text-gray-800"
          }`}
        >
          {item.trangThai === "DANG_LAM_VIEC"
            ? "Đang làm việc"
            : "Đã nghỉ việc"}
        </span>
      ),
    },
    // {
    //   key: "danh_gia",
    //   header: "Đánh giá",
    //   render: (item) => (
    //     <div className="flex justify-center items-center gap-1">
    //       <Star size={14} className="text-yellow-500 fill-current" />
    //       <span>{item.danh_gia || 0}</span>
    //     </div>
    //   ),
    // },
    {
      key: "actions", // <-- Cột mới
      header: "Hành động",
      render: (item) => (
        <div className="flex items-center justify-center gap-3">
          <button
            onClick={() => handleOpenEditModal(item)}
            className="text-white px-4 py-1 rounded-md bg-blue-500 cursor-pointer hover:bg-blue-800"
            title="Sửa"
          >
            {/* <Pencil size={18} /> */}
            Sửa
          </button>
          <button
            onClick={() => handleDelete(item.maTaiXe)}
            className="text-white bg-red-500 px-4 py-1 rounded-md cursor-pointer hover:bg-red-800"
            title="Xóa"
          >
            {/* <Trash2 size={18} /> */}
            Xóa
          </button>
        </div>
      ),
    },
  ];

  const detailFields = [
    // Mã tài xế sẽ chỉ readOnly khi Sửa, và ẩn khi Thêm
    { key: "maTaiXe", label: "MÃ TÀI XẾ", readOnly: true }, // Vẫn để đây cho EditModal
    { key: "tenTaiXe", label: "HỌ TÊN" },
    { key: "soDienThoai", label: "SỐ ĐIỆN THOẠI", type: "tel" },
    { key: "soHieuGPLX", label: "SỐ GIẤY PHÉP LÁI XE" },
    { key: "ngaySinh", label: "NGÀY SINH", type: "date" },
    {
      key: "trangThai",
      label: "TRẠNG THÁI",
      type: "select",
      options: ["DANG_LAM_VIEC", "NGHI_VIEC"],
      optionLabels: {
        DANG_LAM_VIEC: "Đang làm việc",
        NGHI_VIEC: "Đã nghỉ việc",
      },
    },
  ];

  // --- 2. LOGIC CRUD ---
  const fetchDrivers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await apiClient.get("/api/tai-xe");
      setData(response.data);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDrivers();
  }, [fetchDrivers]);

  // Hàm này dùng cho cả AddModal và EditModal
  const handleSave = async (itemData) => {
    const isEdit = itemData.maTaiXe;
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm tài xế mới này?";

    // 2. Thêm hộp thoại xác nhận
    if (!window.confirm(message)) {
      return false; // Báo cho modal biết là "thất bại" (để không tự đóng)
    }
    try {
      if (itemData.maTaiXe) {
        // Cập nhật (PUT)
        await apiClient.put(`/api/tai-xe/${itemData.maTaiXe}`, itemData);
        toast.success("Cập nhật tài xế thành công!");
      } else {
        // Thêm mới (POST)
        await apiClient.post("/api/tai-xe", itemData);
        toast.success("Thêm mới tài xế thành công!");
      }
      fetchDrivers(); // Tải lại dữ liệu
      return true; // Báo cho modal biết là đã thành công
    } catch (err) {
      const errMsg = err.response?.data?.message || err.message;
      toast.error(`Lưu thất bại: ${errMsg}`);
      return false; // Báo cho modal biết là thất bại
    }
  };

  // Hàm xóa mới
  const handleDelete = async (id) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa tài xế này?")) return;
    try {
      await apiClient.delete(`/api/tai-xe/${id}`);
      toast.success("Xóa tài xế thành công!");
      fetchDrivers(); // Tải lại dữ liệu
    } catch (err) {
      const errMsg = err.response?.data?.message || err.message;
      toast.error(`Xóa thất bại: ${errMsg}`);
    }
  };

  // --- 3. HÀM QUẢN LÝ MODAL ---
  const handleOpenEditModal = (item) => {
    setSelectedItem(item);
    setIsEditModalOpen(true);
  };

  // Lọc dữ liệu (logic cũ)
  const filteredData = (Array.isArray(data) ? data : []).filter((item) =>
    Object.keys(search).every((key) =>
      String(item[key] || "")
        .toLowerCase()
        .includes(search[key].toLowerCase())
    )
  );

  // --- 4. RENDER ---
  return (
    <>
      <PageLayout
        title="Quản lý TÀI XẾ"
        searchFields={searchFields}
        onAddClick={() => setIsAddModalOpen(true)} // Mở AddModal
        searchValues={search}
        onSearch={setSearch}
      >
        {/* Truyền DataTable làm children */}
        <DataTable
          data={filteredData}
          columns={columns}
          loading={loading}
          error={error}
          onRowClick={() => {}} // Bỏ onRowClick (hoặc giữ để highlight)
          primaryKeyField="maTaiXe"
        />
      </PageLayout>

      {/* Render các Modal (chúng tự ẩn/hiện) */}
      <AddModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSave={handleSave}
        fields={detailFields.filter(
          (f) => f.key !== "maTaiXe" && f.key !== "bien_so_xe"
        )} // Ẩn trường readOnly khi Thêm
        title="THÊM MỚI TÀI XẾ"
      />

      <EditModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        onSave={handleSave}
        item={selectedItem}
        fields={detailFields}
        title="CẬP NHẬT TÀI XẾ"
      />
    </>
  );
}

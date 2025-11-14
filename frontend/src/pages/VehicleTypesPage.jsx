// src/pages/LoaiXePage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
import EditModal from "../components/common/EditModal";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { Pencil, Trash2 } from "lucide-react";

export default function LoaiXePage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState({});
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  // --- 1. CẤU HÌNH ---
  const ENDPOINT = "/api/loai-xe";
  const PRIMARY_KEY = "maLoai"; // Giả định
  const PAGE_TITLE = "Quản lý Loại xe";

  const searchFields = [
    { key: "maLoai", placeholder: "MÃ LOẠI XE" },
    { key: "tenLoai", placeholder: "TÊN LOẠI XE" },
  ];

  const columns = [
    { key: "maLoai", header: "Mã Loại xe" },
    { key: "tenLoai", header: "Tên Loại xe" },
    { key: "soGhe", header: "Số ghế" },
    {
      key: "actions",
      header: "Hành động",
      render: (item) => (
        <div className="flex justify-center gap-3">
          <button
            onClick={() => handleOpenEditModal(item)}
            className="text-white px-4 py-1 rounded-md bg-blue-500 cursor-pointer hover:bg-blue-800"
            title="Sửa"
          >
            Sửa
          </button>
          <button
            onClick={() => handleDelete(item[PRIMARY_KEY])}
            className="text-white bg-red-500 px-4 py-1 rounded-md cursor-pointer hover:bg-red-800"
            title="Xóa"
          >
            Xóa
          </button>
        </div>
      ),
    },
  ];

  const detailFields = [
    { key: "maLoai", label: "MÃ LOẠI XE", readOnly: true },
    { key: "tenLoai", label: "TÊN LOẠI XE" },
    { key: "soGhe", label: "SỐ GHẾ", type: "number" },
  ];

  // --- 2. LOGIC CRUD (Giống hệt KhachHangPage) ---
  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await apiClient.get(ENDPOINT);
      setData(response.data);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleSave = async (itemData) => {
    const isEdit = itemData[PRIMARY_KEY];
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm loại xe mới này?";

    if (!window.confirm(message)) return false;

    try {
      if (isEdit) {
        await apiClient.put(`${ENDPOINT}/${itemData[PRIMARY_KEY]}`, itemData);
        toast.success("Cập nhật thành công!");
      } else {
        await apiClient.post(ENDPOINT, itemData);
        toast.success("Thêm mới thành công!");
      }
      fetchData();
      return true;
    } catch (err) {
      toast.error(
        `Lưu thất bại: ${err.response?.data?.message || err.message}`
      );
      return false;
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa mục này?")) return;
    try {
      await apiClient.delete(`${ENDPOINT}/${id}`);
      toast.success("Xóa thành công!");
      fetchData();
    } catch (err) {
      toast.error(
        `Xóa thất bại: ${err.response?.data?.message || err.message}`
      );
    }
  };

  // --- 3. LOGIC MODAL & LỌC (Giống hệt KhachHangPage) ---
  const handleOpenEditModal = (item) => {
    setSelectedItem(item);
    setIsEditModalOpen(true);
  };

  const filteredData = (Array.isArray(data) ? data : []).filter((item) =>
    Object.keys(search).every((key) =>
      String(item[key] || "")
        .toLowerCase()
        .includes(search[key].toLowerCase())
    )
  );

  // --- 4. RENDER (Giống hệt KhachHangPage) ---
  return (
    <>
      <PageLayout
        title={PAGE_TITLE}
        searchFields={searchFields}
        onAddClick={() => setIsAddModalOpen(true)}
        searchValues={search}
        onSearch={setSearch}
      >
        <DataTable
          data={filteredData}
          columns={columns}
          loading={loading}
          error={error}
          onRowClick={() => {}}
          primaryKeyField={PRIMARY_KEY}
        />
      </PageLayout>

      <AddModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSave={handleSave}
        fields={detailFields.filter((f) => !f.readOnly)}
        title={`Thêm mới ${PAGE_TITLE}`}
      />

      <EditModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        onSave={handleSave}
        item={selectedItem}
        fields={detailFields}
        title={`Cập nhật ${PAGE_TITLE}`}
      />
    </>
  );
}

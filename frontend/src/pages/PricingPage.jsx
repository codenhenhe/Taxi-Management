// src/pages/BangGiaPage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
import EditModal from "../components/common/EditModal";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { Pencil, Trash2 } from "lucide-react";

export default function BangGiaPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState({});
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  // --- 1. STATE MỚI CHO DROPDOWN ---
  const [loaiXeList, setLoaiXeList] = useState([]);

  // --- 2. CẤU HÌNH ---
  const ENDPOINT = "/api/bang-gia";
  const PRIMARY_KEY = "maBangGia"; // Giả định
  const PAGE_TITLE = "Quản lý Bảng giá";

  const searchFields = [
    { key: "maBangGia", placeholder: "MÃ BẢNG GIÁ" },
    { key: "tenLoaiXe", placeholder: "TÊN LOẠI XE" },
  ];

  const columns = [
    { key: "maBangGia", header: "Mã số" },
    {
      key: "loaiXe", // Sửa key
      header: "Mã loại xe",
      render: (item) => item.loaiXe?.maLoai || "N/A", // Dùng render
    }, // Giả định DTO trả về
    { key: "giaKhoiDiem", header: "Giá khởi điểm" },
    { key: "giaTheoKm", header: "Giá theo Km" },
    { key: "phuThu", header: "Phụ thu" },
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

  // --- 3. LOGIC CRUD (Giống XePage) ---
  const fetchData = useCallback(async () => {
    // ... (Giống hệt XePage)
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

  const fetchLoaiXe = async () => {
    // ... (Giống hệt XePage)
    try {
      const res = await apiClient.get("/api/loai-xe");
      setLoaiXeList(res.data);
    } catch (err) {
      toast.error("Lỗi khi tải danh sách loại xe", err);
    }
  };

  useEffect(() => {
    fetchData();
    fetchLoaiXe();
  }, [fetchData]);

  const handleSave = async (itemData) => {
    // ... (Giống hệt XePage)
    const isEdit = itemData[PRIMARY_KEY];
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm bảng giá mới này?";

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
    // ... (Giống hệt XePage)
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

  // --- 4. LOGIC MODAL & LỌC (Giống XePage) ---
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

  // --- 5. TẠO FIELDS ĐỘNG (Giống XePage) ---
  const getDetailFields = () => [
    { key: "maBangGia", label: "MÃ BẢNG GIÁ", readOnly: true },
    {
      key: "maLoaiXe",
      label: "LOẠI XE",
      type: "select",
      options: loaiXeList.map((lx) => lx.maLoai),
      optionLabels: loaiXeList.reduce((acc, lx) => {
        acc[lx.maLoaiXe] = lx.tenLoaiXe;
        return acc;
      }, {}),
    },
    { key: "giaKhoiDiem", label: "GIÁ KHỞI ĐIỂM", type: "number" },
    { key: "giaTheoKm", label: "GIÁ THEO KM", type: "number" },
    { key: "phuThu", label: "Phụ thu giờ cao điểm", type: "number" },
  ];

  // --- 6. RENDER (Giống XePage) ---
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
        fields={getDetailFields().filter((f) => !f.readOnly)}
        title={`Thêm mới ${PAGE_TITLE}`}
      />

      <EditModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        onSave={handleSave}
        item={selectedItem}
        fields={getDetailFields()}
        title={`Cập nhật ${PAGE_TITLE}`}
      />
    </>
  );
}

// src/pages/BaoTriXePage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
import EditModal from "../components/common/EditModal";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { Pencil, Trash2 } from "lucide-react";

export default function BaoTriXePage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState({});
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  // --- 1. STATE MỚI CHO DROPDOWN ---
  const [xeList, setXeList] = useState([]);

  // --- 2. CẤU HÌNH ---
  const ENDPOINT = "/api/bao-tri-xe";
  const PRIMARY_KEY = "maBaoTri"; // Giả định
  const PAGE_TITLE = "Quản lý Bảo trì xe";

  const searchFields = [
    { key: "maBaoTri", placeholder: "MÃ BẢO TRÌ" },
    { key: "maXe", placeholder: "MÃ XE" },
  ];

  const columns = [
    { key: "maBaoTri", header: "Mã BT" },
    { key: "ngayBaoTri", header: "Ngày bảo trì" },
    { key: "loaiBaoTri", header: "Loại bảo trì" },
    { key: "chiPhi", header: "Chi phí" },
    { key: "maXe", header: "Mã xe" },
    // { key: "bienSoXe", header: "Biển số xe" },
    { key: "moTa", header: "Mô tả" },
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

  // --- 3. LOGIC CRUD ---
  const fetchData = useCallback(async () => {
    // ... (Giống XePage)
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

  // --- 4. LOGIC MỚI: TẢI DROPDOWN ---
  const fetchXe = async () => {
    try {
      const res = await apiClient.get("/api/xe");
      setXeList(res.data);
    } catch (err) {
      toast.error("Lỗi khi tải danh sách xe", err);
    }
  };

  useEffect(() => {
    fetchData();
    fetchXe(); // Tải đồng thời
  }, [fetchData]);

  const handleSave = async (itemData) => {
    // ... (Giống hệt XePage)
    const isEdit = itemData[PRIMARY_KEY];
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm bảo trì mới này?";

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

  // --- 5. LOGIC MODAL & LỌC (Giống XePage) ---
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

  // --- 6. TẠO FIELDS ĐỘNG ---
  const getDetailFields = () => [
    { key: "maBaoTri", label: "MÃ BẢO TRÌ", readOnly: true },
    {
      key: "maXe",
      label: "BIỂN SỐ XE",
      type: "select",
      options: xeList.map((x) => x.maXe),
      optionLabels: xeList.reduce((acc, x) => {
        acc[x.maXe] = x.bienSoXe; // Hiển thị biển số
        return acc;
      }, {}),
    },
    { key: "ngayBaoTri", label: "NGÀY BẢO TRÌ", type: "date" },
    { key: "loaiBaoTri", label: "LOẠI BẢO TRÌ", type: "text" },
    { key: "chiPhi", label: "CHI PHÍ", type: "number" },
    { key: "moTa", label: "MÔ TẢ", type: "text" }, // Có thể đổi thành textarea
  ];

  // --- 7. RENDER (Giống XePage) ---
  return (
    <>
      <PageLayout
        title={PAGE_TITLE}
        searchFields={searchFields}
        // onAddClick={() => setIsAddModalOpen(true)}
        onAddClick={() => {}}
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

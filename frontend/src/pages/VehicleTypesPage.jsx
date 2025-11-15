// src/pages/VehicleTypesPage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import EditModal from "../components/common/EditModal";
import AddModal from "../components/common/AddModal";
import SearchBox from "../components/common/SearchBox";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import Pagination from "../components/common/Pagination"; // <-- Đã có

export default function VehicleTypesPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [queryParams, setQueryParams] = useState({
    filters: {},
    sort: { by: "maLoai", dir: "desc" },
  });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5;

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  const ENDPOINT = "/api/loai-xe";
  const PRIMARY_KEY = "maLoai";
  const PAGE_TITLE = "Quản lý Loại xe";

  const searchFields = [
    { key: "maLoai", placeholder: "Mã loại", type: "text" },
    { key: "tenLoai", placeholder: "Tên loại", type: "text" },
    { key: "soGhe", placeholder: "Số ghế", type: "text" }, // Lọc text vẫn ổn
  ];

  const sortFields = [
    { key: "maLoai", label: "Mã loại" },
    { key: "tenLoai", label: "Tên loại" },
    { key: "soGhe", label: "Số ghế" },
  ];

  const columns = [
    { key: "maLoai", header: "Mã loại", align: "left" },
    { key: "tenLoai", header: "Tên loại", align: "left" },
    { key: "soGhe", header: "Số ghế" },
    {
      key: "actions",
      header: "Hành động",
      render: (item) => (
        <div className="flex items-center justify-center gap-3">
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

  // --- 1. SỬA LỖI Ở ĐÂY ---
  const detailFields = [
    { key: "maLoai", label: "Mã loại", readOnly: true },
    { key: "tenLoai", label: "Tên loại" },
    { key: "soGhe", label: "Số ghế", type: "number" }, // <-- Thêm type: "number"
  ];
  // -------------------------

  const fetchVehicleTypes = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        ...queryParams.filters,
        sort: `${queryParams.sort.by},${queryParams.sort.dir}`,
        page: page,
        size: pageSize,
      };

      const response = await apiClient.get(ENDPOINT, { params });
      setData(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  }, [ENDPOINT, queryParams, page, pageSize]); // <-- Sửa: Thêm ENDPOINT

  useEffect(() => {
    fetchVehicleTypes();
  }, [fetchVehicleTypes]);

  // (handleSave, handleDelete, handleOpenEditModal giữ nguyên, đã đúng)
  const handleSave = async (itemData) => {
    const isEdit = itemData.maLoai;
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm loại xe mới này?";

    if (!window.confirm(message)) return false;

    try {
      if (itemData.maLoai) {
        await apiClient.put(`${ENDPOINT}/${itemData.maLoai}`, itemData);
        toast.success("Cập nhật loại xe thành công!");
      } else {
        await apiClient.post(ENDPOINT, itemData);
        toast.success("Thêm mới loại xe thành công!");
      }
      fetchVehicleTypes();
      return true;
    } catch (err) {
      const errMsg = err.response?.data?.message || err.message;
      toast.error(`Lưu thất bại: ${errMsg}`);
      return false;
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa loại xe này?")) return;
    try {
      await apiClient.delete(`${ENDPOINT}/${id}`);
      toast.success("Xóa loại xe thành công!");
      fetchVehicleTypes();
    } catch (err) {
      const errMsg = err.response?.data?.message || err.message;
      toast.error(`Xóa thất bại: ${errMsg}`);
    }
  };

  const handleOpenEditModal = (item) => {
    setSelectedItem(item);
    setIsEditModalOpen(true);
  };

  const handleFilterAndSort = (params) => {
    setQueryParams(params);
    setPage(0);
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  // --- 7. SỬA RENDER ---
  return (
    <>
      <PageLayout
        title="Quản lý LOẠI XE"
        onAddClick={() => setIsAddModalOpen(true)}
      >
        <SearchBox
          searchFields={searchFields}
          sortFields={sortFields}
          onFilterAndSort={handleFilterAndSort}
          initialParams={queryParams}
        />

        <DataTable
          data={data}
          columns={columns}
          loading={loading}
          error={error}
          onRowClick={() => {}}
          primaryKeyField={PRIMARY_KEY}
        />

        {/* --- 2. SỬA LỖI Ở ĐÂY --- */}
        <div className="flex justify-between items-center mt-4">
          <div className="text-sm text-gray-700">Hiển thị {pageSize} mục</div>
          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </div>
        {/* ------------------------- */}
      </PageLayout>

      {/* (Modals giữ nguyên) */}
      <AddModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSave={handleSave}
        fields={detailFields.filter((f) => f.key !== "maLoai")}
        title="THÊM MỚI LOẠI XE"
      />
      <EditModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        onSave={handleSave}
        item={selectedItem}
        fields={detailFields}
        title="CẬP NHẬT LOẠI XE"
      />
    </>
  );
}

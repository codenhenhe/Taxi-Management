// src/pages/CustomersPage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import EditModal from "../components/common/EditModal";
import AddModal from "../components/common/AddModal";
import SearchBox from "../components/common/SearchBox"; // <-- Import SearchBox mới
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import Pagination from "../components/common/Pagination"; // <-- 1. IMPORT

export default function CustomersPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // --- 1. SỬA STATE: Đổi 'search' thành 'queryParams' ---
  const [queryParams, setQueryParams] = useState({
    filters: {},
    sort: { by: "maKhachHang", dir: "desc" },
  });
  const [page, setPage] = useState(0); // Trang hiện tại (bắt đầu từ 0)
  const [totalPages, setTotalPages] = useState(0); // Tổng số trang
  const pageSize = 5;
  // State quản lý Modal (Giữ nguyên)
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  // --- 2. CẤU HÌNH MỚI CHO SEARCHBOX ---
  const searchFields = [
    { key: "maKhachHang", placeholder: "Mã khách hàng", type: "text" },
    { key: "tenKhachHang", placeholder: "Họ tên", type: "text" },
    { key: "sdt", placeholder: "Số điện thoại", type: "text" },
  ];

  const sortFields = [
    { key: "maKhachHang", label: "Mã khách hàng" },
    { key: "tenKhachHang", label: "Họ tên" },
  ];

  // --- 3. CẤU HÌNH COLUMNS (Thêm align) ---
  const columns = [
    { key: "maKhachHang", header: "Mã khách hàng", align: "left" },
    { key: "tenKhachHang", header: "Họ tên", align: "left" },
    { key: "sdt", header: "Số điện thoại" },
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
            onClick={() => handleDelete(item.maKhachHang)}
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
    { key: "maKhachHang", label: "MÃ khách hàng", readOnly: true },
    { key: "tenKhachHang", label: "HỌ TÊN" },
    { key: "sdt", label: "Số điện thoại" },
  ];

  // --- 4. SỬA LOGIC FETCH (Gửi params) ---
  const fetchCustomers = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      // Chuyển đổi state thành params API
      const params = {
        ...queryParams.filters,
        sort: `${queryParams.sort.by},${queryParams.sort.dir}`,
        page: page,
        size: pageSize,
      };

      const response = await apiClient.get("/api/khach-hang", { params });
      setData(response.data.content); // Mảng dữ liệu
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  }, [queryParams, page, pageSize]); // <-- CHẠY LẠI KHI queryParams THAY ĐỔI

  useEffect(() => {
    fetchCustomers();
  }, [fetchCustomers]);

  // Hàm này dùng cho cả AddModal và EditModal
  const handleSave = async (itemData) => {
    const isEdit = itemData.maKhachHang;
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm khách hàng mới này?";

    // 2. Thêm hộp thoại xác nhận
    if (!window.confirm(message)) {
      return false; // Báo cho modal biết là "thất bại" (để không tự đóng)
    }
    try {
      if (itemData.maKhachHang) {
        // Cập nhật (PUT)
        await apiClient.put(
          `/api/khach-hang/${itemData.maKhachHang}`,
          itemData
        );
        toast.success("Cập nhật khách hàng thành công!");
      } else {
        // Thêm mới (POST)
        await apiClient.post("/api/khach-hang", itemData);
        toast.success("Thêm mới khách hàng thành công!");
      }
      fetchCustomers(); // Tải lại dữ liệu
      return true; // Báo cho modal biết là đã thành công
    } catch (err) {
      const errMsg = err.response?.data?.message || err.message;
      toast.error(`Lưu thất bại: ${errMsg}`);
      return false; // Báo cho modal biết là thất bại
    }
  };

  // Hàm xóa mới
  const handleDelete = async (id) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa khách hàng này?")) return;
    try {
      await apiClient.delete(`/api/khach-hang/${id}`);
      toast.success("Xóa khách hàng thành công!");
      fetchCustomers(); // Tải lại dữ liệu
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

  // --- 5. XÓA 'filteredData' ---
  // (Toàn bộ khối `const filteredData = ...` đã bị xóa)

  // --- 6. HÀM MỚI (Nhận params từ SearchBox) ---
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
        title="Quản lý KHÁCH HÀNG"
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
          primaryKeyField="maKhachHang"
        />

        {/* 9. THÊM COMPONENT PHÂN TRANG */}
        <div className="flex justify-between items-center mt-4">
          <div className="text-sm text-gray-700">Hiển thị {pageSize} mục</div>
          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </div>
      </PageLayout>

      {/* (Modals giữ nguyên) */}
      <AddModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSave={handleSave}
        fields={detailFields.filter((f) => f.key !== "maKhachHang")}
        title="THÊM MỚI KHÁCH HÀNG"
      />
      <EditModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        onSave={handleSave}
        item={selectedItem}
        fields={detailFields}
        title="CẬP NHẬT KHÁCH HÀNG"
      />
    </>
  );
}

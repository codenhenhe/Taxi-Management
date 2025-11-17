// src/pages/CustomersPage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import EditModal from "../components/common/EditModal";
import AddModal from "../components/common/AddModal";
import SearchBox from "../components/common/SearchBox";
import Pagination from "../components/common/Pagination";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { exportToExcel } from "../utils/exportExcel"; // <-- 1. IMPORT HELPER

export default function CustomersPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // --- STATE QUẢN LÝ FILTER/SORT ---
  const [queryParams, setQueryParams] = useState({
    filters: {},
    sort: { by: "maKhachHang", dir: "desc" },
  });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5; // Đặt số mục/trang

  // --- STATE MODAL ---
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  // --- CẤU HÌNH ---
  const ENDPOINT = "/api/khach-hang";
  const PRIMARY_KEY = "maKhachHang";
  const PAGE_TITLE = "Quản lý KHÁCH HÀNG";

  const searchFields = [
    { key: "maKhachHang", placeholder: "Mã khách hàng", type: "text" },
    { key: "tenKhachHang", placeholder: "Họ tên", type: "text" },
    { key: "sdt", placeholder: "Số điện thoại", type: "text" },
  ];

  const sortFields = [
    { key: "maKhachHang", label: "Mã khách hàng" },
    { key: "tenKhachHang", label: "Họ tên" },
  ];

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
    { key: "maKhachHang", label: "MÃ KHÁCH HÀNG", readOnly: true },
    { key: "tenKhachHang", label: "HỌ TÊN" },
    { key: "sdt", label: "SỐ ĐIỆN THOẠI", type: "tel" },
  ];

  // --- 4. LOGIC FETCH ---
  const fetchCustomers = useCallback(async () => {
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
  }, [queryParams, page, pageSize]);

  useEffect(() => {
    fetchCustomers();
  }, [fetchCustomers]);

  // --- 5. LOGIC CRUD ---
  const handleSave = async (itemData) => {
    const isEdit = itemData.maKhachHang;
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm khách hàng mới này?";

    if (!window.confirm(message)) return false;

    try {
      if (isEdit) {
        await apiClient.put(`${ENDPOINT}/${itemData.maKhachHang}`, itemData);
        toast.success("Cập nhật khách hàng thành công!");
      } else {
        await apiClient.post(ENDPOINT, itemData);
        toast.success("Thêm mới khách hàng thành công!");
      }
      fetchCustomers();
      return true;
    } catch {
      // const errMsg = err.response?.data?.message || err.message;
      toast.error("Hành động bị từ chối bởi hệ thống.");
      return false;
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa khách hàng này?")) return;
    try {
      await apiClient.delete(`${ENDPOINT}/${id}`);
      toast.success("Xóa khách hàng thành công!");
      fetchCustomers();
    } catch (err) {
      const errMsg = err.response?.data?.message || err.message;
      toast.error(`Xóa thất bại: ${errMsg}`);
    }
  };

  const handleOpenEditModal = (item) => {
    setSelectedItem(item);
    setIsEditModalOpen(true);
  };

  // --- 6. HÀM XỬ LÝ SỰ KIỆN ---
  const handleFilterAndSort = (params) => {
    setQueryParams(params);
    setPage(0);
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  // --- 2. HÀM EXPORT (Mới) ---
  const handleExport = async () => {
    try {
      const params = {
        ...queryParams.filters,
        sort: `${queryParams.sort.by},${queryParams.sort.dir}`,
        page: 0,
        size: 10000, // Lấy tất cả
      };

      const toastId = toast.loading("Đang tải dữ liệu...");
      const response = await apiClient.get(ENDPOINT, { params });
      const allData = response.data.content;

      if (!allData || allData.length === 0) {
        toast.dismiss(toastId);
        toast.error("Không có dữ liệu để xuất!");
        return;
      }

      // Format dữ liệu
      const formattedData = allData.map((item) => ({
        "Mã Khách Hàng": item.maKhachHang,
        "Họ Tên": item.tenKhachHang,
        "Số Điện Thoại": item.sdt,
      }));

      exportToExcel(formattedData, "DanhSachKhachHang");

      toast.dismiss(toastId);
      toast.success("Xuất file thành công!");
    } catch {
      toast.error("Xuất file thất bại");
    }
  };

  // --- 7. RENDER (Truyền onExport) ---
  return (
    <>
      <PageLayout
        title={PAGE_TITLE}
        onAddClick={() => setIsAddModalOpen(true)}
        onExport={handleExport} // <-- TRUYỀN HÀM EXPORT
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

        <div className="flex justify-between items-center mt-4">
          <div className="text-sm text-gray-700">Hiển thị {pageSize} mục</div>
          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </div>
      </PageLayout>

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

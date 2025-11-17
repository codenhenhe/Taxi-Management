// src/pages/DriversPage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import EditModal from "../components/common/EditModal";
import AddModal from "../components/common/AddModal";
import SearchBox from "../components/common/SearchBox"; // <-- Import SearchBox mới
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import Pagination from "../components/common/Pagination"; // <-- 1. IMPORT
import { exportToExcel } from "../utils/exportExcel"; // <-- Import Helper

export default function DriversPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // --- 1. SỬA STATE: Đổi 'search' thành 'queryParams' ---
  const [queryParams, setQueryParams] = useState({
    filters: {},
    sort: { by: "maTaiXe", dir: "desc" },
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
    { key: "maTaiXe", placeholder: "Mã tài xế", type: "text" },
    { key: "tenTaiXe", placeholder: "Họ tên", type: "text" },
    { key: "soHieuGPLX", placeholder: "Giấy phép lái xe", type: "text" },
    {
      key: "trangThai",
      label: "Trạng thái",
      type: "select",
      options: [
        { value: "DANG_LAM_VIEC", label: "Làm việc" },
        { value: "NGHI_VIEC", label: "Đã nghỉ việc" },
      ],
    },
  ];

  const sortFields = [
    { key: "maTaiXe", label: "Mã tài xế" },
    { key: "tenTaiXe", label: "Họ tên" },
    { key: "ngaySinh", label: "Ngày sinh" },
  ];

  // --- 3. CẤU HÌNH COLUMNS (Thêm align) ---
  const columns = [
    { key: "maTaiXe", header: "Mã tài xế", align: "left" },
    { key: "tenTaiXe", header: "Họ tên", align: "left" },
    { key: "ngaySinh", header: "Ngày sinh" },
    { key: "soDienThoai", header: "Số điện thoại", align: "left" },
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
          {item.trangThai === "DANG_LAM_VIEC" ? "Làm việc" : "Đã nghỉ việc"}
        </span>
      ),
    },
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
            onClick={() => handleDelete(item.maTaiXe)}
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
    { key: "maTaiXe", label: "MÃ TÀI XẾ", readOnly: true },
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

  const handleExport = async () => {
    try {
      // 1. Chuẩn bị params để lấy TẤT CẢ dữ liệu (size lớn)
      // Giữ nguyên các bộ lọc hiện tại
      const params = {
        ...queryParams.filters,
        sort: `${queryParams.sort.by},${queryParams.sort.dir}`,
        page: 0,
        size: 10000, // <-- QUAN TRỌNG: Lấy số lượng lớn để xuất hết
      };

      // 2. Gọi API
      const toastId = toast.loading("Đang tải dữ liệu xuất file...");
      const response = await apiClient.get("/api/tai-xe", { params });
      const allData = response.data.content;

      if (!allData || allData.length === 0) {
        toast.dismiss(toastId);
        toast.error("Không có dữ liệu để xuất!");
        return;
      }

      // 3. Format dữ liệu cho đẹp (Tùy chọn)
      // Map lại dữ liệu để file Excel có tiêu đề cột tiếng Việt đẹp hơn
      const formattedData = allData.map((item) => ({
        "Mã Tài Xế": item.maTaiXe,
        "Họ Tên": item.tenTaiXe,
        "Ngày Sinh": item.ngaySinh,
        "Số Điện Thoại": item.soDienThoai,
        GPLX: item.soHieuGPLX,
        "Trạng Thái":
          item.trangThai === "DANG_LAM_VIEC" ? "Đang làm việc" : "Đã nghỉ",
      }));

      // 4. Gọi hàm xuất file
      exportToExcel(formattedData, "DanhSachTaiXe");

      toast.dismiss(toastId);
      toast.success("Xuất file thành công!");
    } catch (error) {
      toast.error("Xuất file thất bại");
      console.error(error);
    }
  };

  // --- 4. SỬA LOGIC FETCH (Gửi params) ---
  const fetchDrivers = useCallback(async () => {
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

      const response = await apiClient.get("/api/tai-xe", { params });
      setData(response.data.content); // Mảng dữ liệu
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  }, [queryParams, page, pageSize]); // <-- CHẠY LẠI KHI queryParams THAY ĐỔI

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
        title="Quản lý TÀI XẾ"
        onAddClick={() => setIsAddModalOpen(true)}
        onExport={handleExport}
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
          primaryKeyField="maTaiXe"
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
        fields={detailFields.filter((f) => f.key !== "maTaiXe")}
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

// src/pages/BangGiaPage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
import EditModal from "../components/common/EditModal";
import SearchBox from "../components/common/SearchBox"; // <-- 1. Import
import Pagination from "../components/common/Pagination"; // <-- 2. Import
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";

export default function BangGiaPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // --- 3. CẬP NHẬT STATE ---
  const [queryParams, setQueryParams] = useState({
    filters: {},
    sort: { by: "maBangGia", dir: "desc" },
  });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5;

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  const [loaiXeList, setLoaiXeList] = useState([]);

  // --- 4. CẤU HÌNH ---
  const ENDPOINT = "/api/bang-gia";
  const PRIMARY_KEY = "maBangGia";
  const PAGE_TITLE = "Quản lý Bảng giá";

  // --- 5. CẤU HÌNH FIELDS (Dùng hàm, khớp Controller) ---
  const getSearchFields = () => [
    { key: "maBangGia", placeholder: "Mã bảng giá", type: "text" },
    {
      key: "maLoai", // Khớp với @RequestParam("maLoai")
      label: "Loại xe",
      type: "select",
      options: loaiXeList.map((lx) => ({
        value: lx.maLoai,
        label: lx.tenLoai,
      })),
    },
    { key: "giaKhoiDiem", placeholder: "Giá khởi điểm (>=)", type: "number" },
    { key: "giaTheoKm", placeholder: "Giá theo km (>=)", type: "number" },
  ];

  const sortFields = [
    { key: "maBangGia", label: "Mã bảng giá" },
    { key: "giaKhoiDiem", label: "Giá khởi điểm" },
    { key: "giaTheoKm", label: "Giá theo Km" },
  ];

  // Sửa columns
  const columns = [
    { key: "maBangGia", header: "Mã số", align: "left" },
    {
      key: "loaiXe",
      header: "Loại xe", // Hiển thị tên loại xe
      render: (item) => item.loaiXe?.tenLoai || "N/A",
      align: "left",
    },
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

  // --- 6. SỬA LOGIC FETCH ---
  const fetchData = useCallback(async () => {
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
      setData(response.data.content); // Lấy content từ Page
      setTotalPages(response.data.totalPages); // Lấy totalPages
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  }, [queryParams, page, pageSize]);

  // Sửa fetchLoaiXe để lấy TẤT CẢ và đọc 'content'
  const fetchLoaiXe = async () => {
    try {
      const res = await apiClient.get("/api/loai-xe", {
        params: { size: 1000 },
      });
      setLoaiXeList(res.data.content); // Đọc mảng content
    } catch {
      toast.error("Lỗi khi tải danh sách loại xe");
    }
  };

  useEffect(() => {
    fetchData(); // Chạy khi params thay đổi
  }, [fetchData]);

  useEffect(() => {
    fetchLoaiXe(); // Chạy 1 lần
  }, []);

  // --- 7. LOGIC CRUD (Sửa handleSave) ---
  const handleSave = async (itemData) => {
    const isEdit = itemData[PRIMARY_KEY];
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm bảng giá mới này?";

    if (!window.confirm(message)) return false;

    // Dọn dẹp DTO trước khi gửi (Xóa object 'loaiXe' lồng nhau)
    const requestData = { ...itemData };
    delete requestData.loaiXe;

    try {
      if (isEdit) {
        await apiClient.put(
          `${ENDPOINT}/${requestData[PRIMARY_KEY]}`,
          requestData
        );
        toast.success("Cập nhật thành công!");
      } else {
        await apiClient.post(ENDPOINT, requestData);
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

  // Sửa: "Làm phẳng" item trước khi mở EditModal
  const handleOpenEditModal = (item) => {
    const flatItem = {
      ...item,
      maLoai: item.loaiXe?.maLoai, // Lấy ID từ object con
    };
    setSelectedItem(flatItem);
    setIsEditModalOpen(true);
  };

  // --- 8. THÊM HÀM MỚI ---
  const handleFilterAndSort = (params) => {
    setQueryParams(params);
    setPage(0); // Luôn quay về trang 1 khi lọc
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  // Sửa: Dùng hàm getDetailFields
  const getDetailFields = () => [
    { key: "maBangGia", label: "MÃ BẢNG GIÁ", readOnly: true },
    {
      key: "maLoai", // Khớp với RequestDTO
      label: "LOẠI XE",
      type: "select",
      options: loaiXeList.map((lx) => lx.maLoai),
      optionLabels: loaiXeList.reduce((acc, lx) => {
        acc[lx.maLoai] = lx.tenLoai; // Sửa: Dùng key 'maLoai'
        return acc;
      }, {}),
    },
    { key: "giaKhoiDiem", label: "GIÁ KHỞI ĐIỂM", type: "number" },
    { key: "giaTheoKm", label: "GIÁ THEO KM", type: "number" },
    { key: "phuThu", label: "Phụ thu", type: "number" },
  ];

  // --- 9. SỬA RENDER ---
  return (
    <>
      <PageLayout
        title={PAGE_TITLE}
        onAddClick={() => setIsAddModalOpen(true)}
        // Xóa các prop search cũ
      >
        <SearchBox
          searchFields={getSearchFields()} // Gọi hàm
          sortFields={sortFields}
          onFilterAndSort={handleFilterAndSort}
          initialParams={queryParams}
        />

        <DataTable
          data={data} // Dùng data (không phải filteredData)
          columns={columns}
          loading={loading}
          error={error}
          onRowClick={() => {}}
          primaryKeyField={PRIMARY_KEY}
        />

        {/* Thêm Pagination */}
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

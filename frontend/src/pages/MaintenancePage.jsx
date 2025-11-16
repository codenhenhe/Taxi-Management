// src/pages/BaoTriXePage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
import EditModal from "../components/common/EditModal";
import SearchBox from "../components/common/SearchBox";
import Pagination from "../components/common/Pagination";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";

/**
 * Hàm helper để định dạng chuỗi ISO (T) thành Ngày
 */
function formatDateTimeCell(dateTimeString) {
  if (!dateTimeString) {
    return <span className="text-gray-400">—</span>;
  }
  try {
    const date = new Date(dateTimeString);
    // Chỉ hiển thị Ngày/Tháng/Năm cho bảo trì
    return date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  } catch {
    return <span className="text-red-500">Lỗi ngày</span>;
  }
}

export default function BaoTriXePage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // State cho filter, sort
  const [queryParams, setQueryParams] = useState({
    filters: {},
    sort: { by: "ngayBaoTri", dir: "desc" }, // Sắp xếp theo ngày
  });

  // State cho phân trang
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5; // Đặt số mục/trang

  // State cho Modals
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  // State cho dropdown động
  const [xeList, setXeList] = useState([]);

  // --- CẤU HÌNH ---
  const ENDPOINT = "/api/bao-tri-xe";
  const PRIMARY_KEY = "maBaoTri";
  const PAGE_TITLE = "Bảo trì xe";

  // --- CẤU HÌNH FIELDS (Dùng hàm, khớp Controller) ---
  const getSearchFields = () => [
    { key: "maBaoTri", placeholder: "Mã bảo trì", type: "text" },
    { key: "loaiBaoTri", placeholder: "Loại bảo trì", type: "text" },
    { key: "maXe", placeholder: "Mã xe", type: "text" },
    // {
    //   key: "maXe",
    //   label: "Xe (Lọc theo Mã)",
    //   type: "select",
    //   options: xeList.map((x) => ({ value: x.maXe, label: x.bienSoXe })),
    // },
    { key: "bienSoXe", placeholder: "Biển số xe", type: "text" },
    { key: "chiPhi", placeholder: "Chi phí", type: "number" },
  ];

  const sortFields = [
    { key: "maBaoTri", label: "Mã bảo trì" },
    { key: "ngayBaoTri", label: "Ngày bảo trì" },
    { key: "chiPhi", label: "Chi phí" },
  ];

  // Cấu hình cột
  const columns = [
    { key: "maBaoTri", header: "Mã BT", align: "left" },
    {
      key: "ngayBaoTri",
      header: "Ngày bảo trì",
      render: (item) => formatDateTimeCell(item.ngayBaoTri),
    },
    { key: "loaiBaoTri", header: "Loại bảo trì", align: "left" },
    { key: "chiPhi", header: "Chi phí" },
    { key: "maXe", header: "Mã xe", align: "left" },
    { key: "bienSoXe", header: "Biển số xe", align: "left" },
    { key: "moTa", header: "Mô tả", align: "left" },
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

  // --- LOGIC FETCH ---
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

  // Sửa fetchXe để lấy TẤT CẢ xe và đọc 'content'
  const fetchXe = async () => {
    try {
      const res = await apiClient.get("/api/xe", { params: { size: 1000 } });
      setXeList(res.data.content); // Đọc mảng content
    } catch {
      toast.error("Lỗi khi tải danh sách xe");
    }
  };

  useEffect(() => {
    fetchData(); // Chạy khi params thay đổi
  }, [fetchData]);

  useEffect(() => {
    fetchXe(); // Chạy 1 lần
  }, []);

  // --- LOGIC CRUD ---
  const handleSave = async (itemData) => {
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

  const handleOpenEditModal = (item) => {
    setSelectedItem(item);
    setIsEditModalOpen(true);
  };

  // --- HÀM XỬ LÝ SỰ KIỆN TỪ CON ---
  const handleFilterAndSort = (params) => {
    setQueryParams(params);
    setPage(0); // Luôn quay về trang 1 khi lọc
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  // Cấu hình các trường trong Modal
  const getDetailFields = () => [
    { key: "maBaoTri", label: "MÃ BẢO TRÌ", readOnly: true },
    {
      key: "maXe",
      label: "BIỂN SỐ XE",
      type: "select",
      options: xeList.map((x) => x.maXe),
      optionLabels: xeList.reduce((acc, x) => {
        acc[x.maXe] = x.bienSoXe || x.maXe; // Hiển thị biển số
        return acc;
      }, {}),
    },
    { key: "ngayBaoTri", label: "NGÀY BẢO TRÌ", type: "date" },
    { key: "loaiBaoTri", label: "LOẠI BẢO TRÌ", type: "text" },
    { key: "chiPhi", label: "CHI PHÍ", type: "number" },
    { key: "moTa", label: "MÔ TẢ", type: "text" },
  ];

  // --- RENDER ---
  return (
    <>
      <PageLayout
        title={"Quản lý BẢO TRÌ XE"}
        onAddClick={() => setIsAddModalOpen(true)}
      >
        <SearchBox
          searchFields={getSearchFields()} // Gọi hàm
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

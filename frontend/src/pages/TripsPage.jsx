// src/pages/ChuyenDiPage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
import EditModal from "../components/common/EditModal";
import SearchBox from "../components/common/SearchBox";
import Pagination from "../components/common/Pagination";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { Pencil, Trash2 } from "lucide-react";

// Hàm helper format ngày (Giữ nguyên)
function formatDateTimeCell(dateTimeString) {
  if (!dateTimeString) {
    return <span className="text-gray-400">—</span>;
  }
  try {
    const date = new Date(dateTimeString);
    const time = date.toLocaleTimeString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
    });
    const day = date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
    return (
      <div className="flex flex-col items-center">
        <span className="font-medium">{time}</span>
        <span className="text-xs text-gray-600">{day}</span>
      </div>
    );
  } catch {
    return <span className="text-red-500">Lỗi ngày</span>;
  }
}

export default function ChuyenDiPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // State (Giữ nguyên)
  const [queryParams, setQueryParams] = useState({
    filters: {},
    sort: { by: "tgDon", dir: "desc" },
  });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5;

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  const [xeList, setXeList] = useState([]);
  const [khachHangList, setKhachHangList] = useState([]);

  // Cấu hình (Giữ nguyên)
  const ENDPOINT = "/api/chuyen-di";
  const PRIMARY_KEY = "maChuyen";
  const PAGE_TITLE = "Quản lý Chuyến đi";

  // --- SỬA HÀM NÀY ---
  const getSearchFields = () => [
    { key: "maChuyen", placeholder: "Mã chuyến", type: "text" },
    { key: "diemDon", placeholder: "Điểm đón", type: "text" },
    { key: "diemTra", placeholder: "Điểm trả", type: "text" },
    {
      key: "maKhachHang",
      label: "Mã khách hàng",
      type: "select",
      options: khachHangList.map((kh) => ({
        value: kh.maKhachHang,
        label: kh.maKhachHang,
      })),
    },
    {
      key: "maXe",
      label: "Mã xe",
      type: "select",
      options: xeList.map((x) => ({
        value: x.maXe,
        label: x.maXe,
      })),
    },
    // --- THAY ĐỔI 2 TRƯỜNG NÀY ---
    {
      key: "tuNgayDon",
      label: "Đón từ ngày", // Đổi placeholder thành label
      type: "date", // Đổi type
    },
    {
      key: "denNgayDon",
      label: "Đón đến ngày", // Đổi placeholder thành label
      type: "date", // Đổi type
    },
    // --- KẾT THÚC SỬA ---
  ];

  const sortFields = [
    { key: "tgDon", label: "Thời gian đón" },
    { key: "tgTra", label: "Thời gian trả" },
    { key: "cuocPhi", label: "Cước phí" },
    { key: "maChuyen", label: "Mã chuyến" },
  ];

  // (Columns giữ nguyên)
  const columns = [
    { key: "maChuyen", header: "Mã Chuyến", align: "left" },
    { key: "diemDon", header: "Điểm đón", align: "left" },
    { key: "diemTra", header: "Điểm trả", align: "left" },
    {
      key: "tgDon",
      header: "Thời gian đón",
      render: (item) => formatDateTimeCell(item.tgDon),
    },
    {
      key: "tgTra",
      header: "Thời gian trả",
      render: (item) => formatDateTimeCell(item.tgTra),
    },
    { key: "soKmDi", header: "Số km" },
    { key: "cuocPhi", header: "Cước phí" },
    { key: "maXe", header: "Mã xe", align: "left" },
    { key: "maKhachHang", header: "Mã khách hàng", align: "left" },
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

  // (Logic fetch... giữ nguyên)
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
      setData(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  }, [queryParams, page, pageSize]);

  const fetchDropdowns = async () => {
    try {
      const [xeRes, khRes] = await Promise.all([
        apiClient.get("/api/xe", {
          params: { size: 1000, trangThaiXe: "SAN_SANG" },
        }),
        apiClient.get("/api/khach-hang", { params: { size: 1000 } }),
      ]);
      setXeList(xeRes.data.content);
      setKhachHangList(khRes.data.content);
    } catch {
      toast.error("Lỗi khi tải danh sách xe hoặc khách hàng");
    }
  };

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  useEffect(() => {
    fetchDropdowns();
  }, []);

  // (Logic CRUD... giữ nguyên)
  const handleSave = async (itemData) => {
    const isEdit = itemData[PRIMARY_KEY];
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm chuyến đi mới này?";

    if (!window.confirm(message)) return false;

    const requestData = { ...itemData };

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

  const handleOpenEditModal = (item) => {
    setSelectedItem(item);
    setIsEditModalOpen(true);
  };

  // (Hàm xử lý sự kiện... giữ nguyên)
  const handleFilterAndSort = (params) => {
    setQueryParams(params);
    setPage(0);
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  // (getDetailFields... giữ nguyên)
  const getDetailFields = () => [
    { key: "maChuyen", label: "MÃ CHUYẾN", readOnly: true },
    {
      key: "maKhachHang",
      label: "KHÁCH HÀNG",
      type: "select",
      options: khachHangList.map((kh) => kh.maKhachHang),
      optionLabels: khachHangList.reduce((acc, kh) => {
        acc[kh.maKhachHang] = `${kh.tenKhachHang} (${kh.sdt})`;
        return acc;
      }, {}),
    },
    {
      key: "maXe",
      label: "XE (SẴN SÀNG)",
      type: "select",
      options: xeList.map((x) => x.maXe),
      optionLabels: xeList.reduce((acc, x) => {
        acc[x.maXe] = x.bienSoXe;
        return acc;
      }, {}),
    },
    { key: "diemDon", label: "ĐIỂM ĐÓN", type: "text" },
    { key: "diemTra", label: "ĐIỂM TRẢ", type: "text" },
    { key: "soKmDi", label: "SỐ KM (DỰ KIẾN)", type: "number" },
  ];

  return (
    <>
      <PageLayout title={PAGE_TITLE} onAddClick={() => setIsAddModalOpen(true)}>
        <SearchBox
          searchFields={getSearchFields()}
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

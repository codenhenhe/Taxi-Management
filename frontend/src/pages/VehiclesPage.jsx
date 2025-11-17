// src/pages/VehiclesPage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import EditModal from "../components/common/EditModal";
import AddModal from "../components/common/AddModal";
import SearchBox from "../components/common/SearchBox";
import Pagination from "../components/common/Pagination";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
// import { Pencil } from "lucide-react";
import { exportToExcel } from "../utils/exportExcel";

// --- 1. HÀM MAP TRẠNG THÁI ---
const TRANG_THAI_XE_LABELS = {
  SAN_SANG: "Sẵn sàng",
  BAO_TRI: "Đang bảo trì",
  DANG_CHAY: "Đang chạy",
  NGUNG_HOAT_DONG: "Ngưng hoạt động",
  CHO_PHAN_CONG: "Chờ phân công",
};

const TRANG_THAI_XE_STYLES = {
  SAN_SANG: "bg-green-100 text-green-800",
  BAO_TRI: "bg-yellow-100 text-yellow-800",
  DANG_CHAY: "bg-red-100 text-red-800",
  NGUNG_HOAT_DONG: "bg-gray-100 text-gray-800",
  CHO_PHAN_CONG: "bg-blue-100 text-blue-800",
};

// Tạo mảng options cho dropdown
const TRANG_THAI_OPTIONS = Object.keys(TRANG_THAI_XE_LABELS).map((key) => ({
  value: key,
  label: TRANG_THAI_XE_LABELS[key],
}));

export default function VehiclesPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [queryParams, setQueryParams] = useState({
    filters: {},
    sort: { by: "maXe", dir: "desc" },
  });

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5;

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  const [loaiXeList, setLoaiXeList] = useState([]);

  const ENDPOINT = "/api/xe";
  const PRIMARY_KEY = "maXe";
  const PAGE_TITLE = "Quản lý XE";

  const getSearchFields = () => [
    { key: "maXe", placeholder: "Mã xe", type: "text" },
    { key: "bienSoXe", placeholder: "Biển số xe", type: "text" },
    { key: "mauXe", placeholder: "Màu xe", type: "text" },
    { key: "namSanXuat", placeholder: "Năm sản xuất", type: "text" },
    {
      key: "trangThaiXe",
      label: "Trạng thái",
      type: "select",
      options: TRANG_THAI_OPTIONS,
    },
    {
      key: "maLoai",
      label: "Loại xe",
      type: "select",
      options: loaiXeList.map((lx) => ({
        value: lx.maLoai,
        label: lx.tenLoai,
      })),
    },
  ];

  const sortFields = [
    { key: "maXe", label: "Mã xe" },
    { key: "namSanXuat", label: "Năm sản xuất" },
    { key: "bienSoXe", label: "Biển số xe" },
  ];

  const columns = [
    { key: "maXe", header: "Mã xe", align: "left" },
    { key: "bienSoXe", header: "Biển số xe", align: "left" },
    { key: "mauXe", header: "Màu xe" },
    { key: "namSanXuat", header: "Năm sản xuất" },
    {
      key: "loaiXe",
      header: "Loại xe",
      render: (item) => item.loaiXe?.tenLoai || "N/A",
      align: "left",
    },
    {
      key: "trangThaiXe",
      header: "Trạng thái",
      render: (item) => {
        const label =
          TRANG_THAI_XE_LABELS[item.trangThaiXe] || item.trangThaiXe;
        const style =
          TRANG_THAI_XE_STYLES[item.trangThaiXe] || "bg-gray-100 text-gray-800";
        return (
          <span className={`px-2 py-1 rounded-full text-xs ${style}`}>
            {label}
          </span>
        );
      },
    },
    {
      key: "actions",
      header: "Hành động",
      render: (item) => (
        <div className="flex items-center justify-center gap-3">
          {item.trangThaiXe === "DANG_CHAY" ? (
            <span className="text-orange-500 text-sm italic font-medium">
              Không khả dụng
            </span>
          ) : (
            <button
              onClick={() => handleOpenEditModal(item)}
              className="text-white px-4 py-1 rounded-md bg-blue-500 cursor-pointer hover:bg-blue-800 flex items-center gap-1"
              title="Sửa"
            >
              {/* <Pencil size={16} /> */}
              Sửa
            </button>
          )}
        </div>
      ),
    },
  ];

  const fetchVehicles = useCallback(async () => {
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

  const fetchLoaiXe = async () => {
    try {
      const res = await apiClient.get("/api/loai-xe", {
        params: { size: 1000, page: 0 },
      });
      setLoaiXeList(res.data.content);
    } catch {
      toast.error("Lỗi khi tải danh sách loại xe");
    }
  };

  useEffect(() => {
    fetchVehicles();
  }, [fetchVehicles]);

  useEffect(() => {
    fetchLoaiXe();
  }, []);

  const handleExport = async () => {
    try {
      const params = {
        ...queryParams.filters,
        sort: `${queryParams.sort.by},${queryParams.sort.dir}`,
        page: 0,
        size: 10000,
      };

      const toastId = toast.loading("Đang tải dữ liệu...");
      const response = await apiClient.get(ENDPOINT, { params });
      const allData = response.data.content;

      if (!allData || allData.length === 0) {
        toast.dismiss(toastId);
        toast.error("Không có dữ liệu để xuất!");
        return;
      }

      const formattedData = allData.map((item) => ({
        "Mã Xe": item.maXe,
        "Biển Số": item.bienSoXe,
        "Màu Xe": item.mauXe,
        "Năm SX": item.namSanXuat,
        "Loại Xe": item.loaiXe?.tenLoai || "N/A",
        "Trạng Thái":
          TRANG_THAI_XE_LABELS[item.trangThaiXe] || item.trangThaiXe,
      }));

      exportToExcel(formattedData, "DanhSachXe");

      toast.dismiss(toastId);
      toast.success("Xuất file thành công!");
    } catch {
      toast.error("Xuất file thất bại");
    }
  };

  const handleSave = async (itemData) => {
    const isEdit = itemData.maXe;
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm xe mới này?";

    if (!window.confirm(message)) return false;

    const requestData = { ...itemData };
    delete requestData.loaiXe;

    try {
      if (isEdit) {
        await apiClient.put(`${ENDPOINT}/${requestData.maXe}`, requestData);
        toast.success("Cập nhật xe thành công!");
      } else {
        await apiClient.post(ENDPOINT, requestData);
        toast.success("Thêm mới xe thành công!");
      }
      fetchVehicles();
      return true;
    } catch (err) {
      const errMsg = err.response?.data?.message || err.message;
      toast.error(`Lưu thất bại: ${errMsg}`);
      return false;
    }
  };

  const handleOpenEditModal = (item) => {
    const flatItem = {
      ...item,
      maLoai: item.loaiXe?.maLoai,
    };
    setSelectedItem(flatItem);
    setIsEditModalOpen(true);
  };

  const handleFilterAndSort = (params) => {
    setQueryParams(params);
    setPage(0);
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  // --- CẤU HÌNH CHO MODAL THÊM MỚI ---
  const getAddFields = () => [
    // Không có maXe vì nó tự động tạo
    { key: "bienSoXe", label: "BIỂN SỐ" },
    { key: "mauXe", label: "MÀU XE" },
    { key: "namSanXuat", label: "NĂM SẢN XUẤT", type: "number" },
    {
      key: "maLoai",
      label: "LOẠI XE",
      type: "select",
      options: loaiXeList.map((lx) => lx.maLoai),
      optionLabels: loaiXeList.reduce((acc, lx) => {
        acc[lx.maLoai] = lx.tenLoai;
        return acc;
      }, {}),
    },
    {
      key: "trangThaiXe",
      label: "TRẠNG THÁI",
      type: "select",
      defaultValue: "CHO_PHAN_CONG",
      readOnly: true, // Không cho sửa khi thêm mới
      options: TRANG_THAI_OPTIONS.map((opt) => opt.value),
      optionLabels: TRANG_THAI_OPTIONS.reduce((acc, opt) => {
        acc[opt.value] = opt.label;
        return acc;
      }, {}),
    },
  ];

  // --- CẤU HÌNH CHO MODAL SỬA (CÓ THỂ SỬA TRẠNG THÁI) ---
  const getEditFields = () => [
    { key: "maXe", label: "MÃ XE", readOnly: true },
    { key: "bienSoXe", label: "BIỂN SỐ" },
    { key: "mauXe", label: "MÀU XE" },
    { key: "namSanXuat", label: "NĂM SẢN XUẤT", type: "number" },
    {
      key: "maLoai",
      label: "LOẠI XE",
      type: "select",
      options: loaiXeList.map((lx) => lx.maLoai),
      optionLabels: loaiXeList.reduce((acc, lx) => {
        acc[lx.maLoai] = lx.tenLoai;
        return acc;
      }, {}),
    },
    {
      key: "trangThaiXe",
      label: "TRẠNG THÁI",
      type: "select",
      // Khi sửa thì cho phép thay đổi trạng thái
      options: TRANG_THAI_OPTIONS.map((opt) => opt.value),
      optionLabels: TRANG_THAI_OPTIONS.reduce((acc, opt) => {
        acc[opt.value] = opt.label;
        return acc;
      }, {}),
    },
  ];

  return (
    <>
      <PageLayout
        title={PAGE_TITLE}
        onAddClick={() => setIsAddModalOpen(true)}
        onExport={handleExport}
      >
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

      {/* Modals */}
      <AddModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSave={handleSave}
        // Dùng getAddFields, không cần filter !readOnly nữa
        fields={getAddFields()}
        title="THÊM MỚI XE"
      />
      <EditModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        onSave={handleSave}
        item={selectedItem}
        fields={getEditFields()} // Dùng getEditFields
        title="CẬP NHẬT XE"
      />
    </>
  );
}

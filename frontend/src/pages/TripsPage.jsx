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
import { exportToExcel } from "../utils/exportExcel";

// Hàm helper format ngày
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

  const ENDPOINT = "/api/chuyen-di";
  const PRIMARY_KEY = "maChuyen";
  const PAGE_TITLE = "Quản lý CHUYẾN ĐI";

  const getSearchFields = () => [
    { key: "maChuyen", placeholder: "Mã chuyến", type: "text" },
    { key: "diemDon", placeholder: "Điểm đón", type: "text" },
    { key: "diemTra", placeholder: "Điểm trả", type: "text" },
    {
      key: "maKhachHang",
      placeholder: "Mã khách hàng",
      type: "text",
    },
    {
      key: "maXe",
      placeholder: "Mã xe",
      type: "text",
    },
    { key: "tuNgayDon", label: "Đón từ ngày", type: "date" },
    { key: "denNgayDon", label: "Đón đến ngày", type: "date" },
  ];

  const sortFields = [
    { key: "tgDon", label: "Thời gian đón" },
    { key: "tgTra", label: "Thời gian trả" },
    { key: "cuocPhi", label: "Cước phí" },
    { key: "maChuyen", label: "Mã chuyến" },
  ];

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
    { key: "soKmDi", header: "Số KM" },
    { key: "cuocPhi", header: "Cước phí" },
    { key: "maXe", header: "Mã xe", align: "left" },
    { key: "maKhachHang", header: "Mã khách hàng", align: "left" },
    {
      key: "actions",
      header: "Hành động",
      render: (item) => {
        // Logic kiểm tra hoàn tất
        const isCompleted = !!item.tgTra;

        return (
          <div className="flex justify-center items-center w-full">
            {!isCompleted ? (
              <button
                onClick={() => handleOpenEditModal(item)}
                className="text-white px-3 py-1 rounded-md bg-green-500 hover:bg-green-600 cursor-pointer text-sm transition-colors font-medium shadow-sm"
                title="Nhập số km để hoàn tất"
              >
                Hoàn tất
              </button>
            ) : (
              <span className="text-gray-500 font-semibold text-sm italic">
                Đã hoàn thành
              </span>
            )}
          </div>
        );
      },
    },
  ];

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const cleanedFilters = {};
      for (const key in queryParams.filters) {
        const value = queryParams.filters[key];
        if (value !== null && value !== "") {
          cleanedFilters[key] = value;
        }
      }

      const params = {
        ...cleanedFilters,
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

  const handleExport = async () => {
    try {
      const cleanedFilters = {};
      for (const key in queryParams.filters) {
        const value = queryParams.filters[key];
        if (value !== null && value !== "") {
          cleanedFilters[key] = value;
        }
      }

      const params = {
        ...cleanedFilters,
        sort: `${queryParams.sort.by},${queryParams.sort.dir}`,
        page: 0,
        size: 10000,
      };

      const toastId = toast.loading("Đang tải dữ liệu xuất file...");
      const response = await apiClient.get(ENDPOINT, { params });
      const allData = response.data.content;

      if (!allData || allData.length === 0) {
        toast.dismiss(toastId);
        toast.error("Không có dữ liệu để xuất!");
        return;
      }

      const formattedData = allData.map((item) => ({
        "Mã Chuyến": item.maChuyen,
        "Điểm Đón": item.diemDon,
        "Điểm Trả": item.diemTra,
        "TG Đón": item.tgDon
          ? new Date(item.tgDon).toLocaleString("vi-VN")
          : "",
        "TG Trả": item.tgTra
          ? new Date(item.tgTra).toLocaleString("vi-VN")
          : "",
        "Số KM": item.soKmDi,
        "Cước Phí": item.cuocPhi,
        "Biển Số Xe": item.bienSoXe || item.maXe,
        "Khách Hàng": item.tenKhachHang || item.maKhachHang,
      }));

      exportToExcel(formattedData, "DanhSachChuyenDi");
      toast.dismiss(toastId);
      toast.success("Xuất file thành công!");
    } catch (error) {
      toast.error("Xuất file thất bại");
      console.error(error);
    }
  };

  const handleSave = async (itemData) => {
    const isEdit = itemData[PRIMARY_KEY];
    const message = isEdit
      ? "Bạn có chắc chắn muốn hoàn tất chuyến đi này?"
      : "Bạn có chắc chắn muốn tạo chuyến đi mới?";

    if (!window.confirm(message)) return false;

    const requestData = { ...itemData };

    try {
      if (isEdit) {
        await apiClient.put(
          `${ENDPOINT}/hoan-tat/${requestData[PRIMARY_KEY]}`,
          {
            soKm: requestData.soKmDi,
          }
        );
        toast.success("Hoàn tất chuyến đi thành công!");
      } else {
        console.log("Dữ liệu gửi lên:", requestData);
        await apiClient.post(ENDPOINT, requestData);
        toast.success("Thêm mới chuyến đi thành công!");
      }
      fetchData();
      // Tải lại dropdown để cập nhật trạng thái xe (xe vừa chạy xong -> SAN_SANG, xe vừa tạo -> DANG_CHAY)
      fetchDropdowns();
      return true;
    } catch {
      toast.error("Hành động bị từ chối bởi hệ thống.");
      return false;
    }
  };

  // Đã xóa hàm handleDelete

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

  const getAddFields = () => [
    {
      key: "maKhachHang",
      label: "KHÁCH HÀNG",
      type: "select",
      options: khachHangList.map((kh) => kh.maKhachHang),
      optionLabels: khachHangList.reduce((acc, kh) => {
        acc[kh.maKhachHang] = kh.maKhachHang;
        return acc;
      }, {}),
    },
    {
      key: "maXe",
      label: "XE (SẴN SÀNG)",
      type: "select",
      options: xeList.map((x) => x.maXe),
      optionLabels: xeList.reduce((acc, x) => {
        acc[x.maXe] = x.maXe;
        return acc;
      }, {}),
    },
    { key: "diemDon", label: "ĐIỂM ĐÓN", type: "text" },
    { key: "diemTra", label: "ĐIỂM TRẢ", type: "text" },
  ];

  const getEditFields = () => [
    { key: "maChuyen", label: "MÃ CHUYẾN", readOnly: true },
    { key: "soKmDi", label: "SỐ KM THỰC TẾ", type: "number" },
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

      <AddModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSave={handleSave}
        fields={getAddFields()}
        title={`THÊM MỚI CHUYẾN ĐI`}
      />

      <EditModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        onSave={handleSave}
        item={selectedItem}
        fields={getEditFields()}
        title={`HOÀN TẤT CHUYẾN ĐI`}
      />
    </>
  );
}

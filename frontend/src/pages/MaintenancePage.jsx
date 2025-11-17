// src/pages/BaoTriXePage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
import SearchBox from "../components/common/SearchBox";
import Pagination from "../components/common/Pagination";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { Trash2 } from "lucide-react";
import { exportToExcel } from "../utils/exportExcel";

/**
 * Hàm helper để định dạng chuỗi ISO (T) thành Ngày
 */
function formatDateTimeCell(dateTimeString) {
  if (!dateTimeString) {
    return <span className="text-gray-400">—</span>;
  }
  try {
    const date = new Date(dateTimeString);
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

  const [queryParams, setQueryParams] = useState({
    filters: {},
    sort: { by: "ngayBaoTri", dir: "desc" },
  });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5;

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  const [xeList, setXeList] = useState([]);

  const ENDPOINT = "/api/bao-tri-xe";
  const PRIMARY_KEY = "maBaoTri";
  const PAGE_TITLE = "Quản lý BẢO TRÌ XE";

  // Cập nhật cấu hình các trường tìm kiếm
  const getSearchFields = () => [
    { key: "maBaoTri", placeholder: "Mã bảo trì", type: "text" },
    { key: "loaiBaoTri", placeholder: "Loại bảo trì", type: "text" },
    // Sửa maXe thành text input
    { key: "maXe", placeholder: "Mã xe", type: "text" },
    // Sửa chiPhi thành number input
    { key: "chiPhi", placeholder: "Chi phí", type: "number" },
  ];

  const sortFields = [
    { key: "maBaoTri", label: "Mã bảo trì" },
    { key: "ngayBaoTri", label: "Ngày bảo trì" },
    { key: "chiPhi", label: "Chi phí" },
  ];

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
    { key: "moTa", header: "Mô tả", align: "left" },
    {
      key: "actions",
      header: "Hành động",
      render: (item) => (
        <div className="flex justify-center gap-3">
          <button
            onClick={() => handleDelete(item[PRIMARY_KEY])}
            className="text-white bg-red-500 px-4 py-1 rounded-md cursor-pointer hover:bg-red-800"
            title="Xóa"
          >
            <Trash2 size={18} />
          </button>
        </div>
      ),
    },
  ];

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

  const fetchXe = async () => {
    try {
      const res = await apiClient.get("/api/xe", { params: { size: 1000 } });
      setXeList(res.data.content);
    } catch {
      toast.error("Lỗi khi tải danh sách xe");
    }
  };

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  useEffect(() => {
    fetchXe();
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
        "Mã Bảo Trì": item.maBaoTri,
        "Ngày Bảo Trì": item.ngayBaoTri
          ? new Date(item.ngayBaoTri).toLocaleDateString("vi-VN")
          : "",
        "Loại Bảo Trì": item.loaiBaoTri,
        "Chi Phí": item.chiPhi,
        "Biển Số Xe": item.bienSoXe || item.maXe,
        "Mô Tả": item.moTa,
      }));

      exportToExcel(formattedData, "DanhSachBaoTri");

      toast.dismiss(toastId);
      toast.success("Xuất file thành công!");
    } catch {
      toast.error(`Xuất file thất bại ${error}`);
    }
  };

  const handleSave = async (itemData) => {
    if (!window.confirm("Bạn có chắc chắn muốn thêm bảo trì mới này?"))
      return false;

    try {
      await apiClient.post(ENDPOINT, itemData);
      toast.success("Thêm mới thành công!");

      fetchData();
      return true;
    } catch {
      toast.error("Hành động bị từ chối bởi hệ thống.");
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

  const handleFilterAndSort = (params) => {
    setQueryParams(params);
    setPage(0);
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  const getDetailFields = () => [
    {
      key: "maBaoTri",
      label: "MÃ BẢO TRÌ",
      readOnly: true,
      defaultValue: "Tự động tạo",
    },
    {
      key: "maXe",
      label: "MÃ XE",
      type: "select",
      options: xeList.map((x) => x.maXe),
      optionLabels: xeList.reduce((acc, x) => {
        acc[x.maXe] = x.maXe;
        return acc;
      }, {}),
    },
    { key: "ngayBaoTri", label: "NGÀY BẢO TRÌ", type: "date" },
    { key: "loaiBaoTri", label: "LOẠI BẢO TRÌ", type: "text" },
    { key: "chiPhi", label: "CHI PHÍ", type: "number" },
    { key: "moTa", label: "MÔ TẢ", type: "text" },
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
        fields={getDetailFields().filter((f) => !f.readOnly)}
        title={`Thêm mới ${PAGE_TITLE}`}
      />
    </>
  );
}

// src/pages/BangGiaPage.jsx
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
import { exportToExcel } from "../utils/exportExcel"; // <-- 1. IMPORT HELPER

export default function BangGiaPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

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

  const ENDPOINT = "/api/bang-gia";
  const PRIMARY_KEY = "maBangGia";
  const PAGE_TITLE = "Quản lý BẢNG GIÁ";

  const getSearchFields = () => [
    { key: "maBangGia", placeholder: "Mã bảng giá", type: "text" },
    {
      key: "maLoai",
      placeholder: "Mã loại xe",
      type: "text",
    },
    { key: "giaKhoiDiem", placeholder: "Giá khởi điểm", type: "number" },
  ];

  const sortFields = [
    { key: "maBangGia", label: "Mã" },
    { key: "giaKhoiDiem", label: "Giá khởi điểm" },
    { key: "giaTheoKm", label: "Giá theo Km" },
  ];

  const columns = [
    { key: "maBangGia", header: "Mã số", align: "left" },
    {
      key: "loaiXe",
      header: "Mã loại xe",
      render: (item) => item.loaiXe?.maLoai || "N/A",
      align: "left",
    },
    {
      key: "loaiXe",
      header: "Loại xe",
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

  const fetchLoaiXe = async () => {
    try {
      const res = await apiClient.get("/api/loai-xe", {
        params: { size: 1000 },
      });
      setLoaiXeList(res.data.content);
    } catch {
      toast.error("Lỗi khi tải danh sách loại xe");
    }
  };

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  useEffect(() => {
    fetchLoaiXe();
  }, []);

  // --- 2. HÀM XỬ LÝ EXPORT ---
  const handleExport = async () => {
    try {
      const params = {
        ...queryParams.filters,
        sort: `${queryParams.sort.by},${queryParams.sort.dir}`,
        page: 0,
        size: 10000, // Lấy tất cả
      };

      const toastId = toast.loading("Đang tải dữ liệu xuất file...");
      const response = await apiClient.get(ENDPOINT, { params });
      const allData = response.data.content;

      if (!allData || allData.length === 0) {
        toast.dismiss(toastId);
        toast.error("Không có dữ liệu để xuất!");
        return;
      }

      // Format dữ liệu
      const formattedData = allData.map((item) => ({
        "Mã Bảng Giá": item.maBangGia,
        "Loại Xe": item.loaiXe?.tenLoai || "N/A",
        "Giá Khởi Điểm": item.giaKhoiDiem,
        "Giá Theo KM": item.giaTheoKm,
        "Phụ Thu": item.phuThu,
      }));

      exportToExcel(formattedData, "DanhSachBangGia");

      toast.dismiss(toastId);
      toast.success("Xuất file thành công!");
    } catch {
      toast.error(`Xuất file thất bại ${error}`);
    }
  };

  const handleSave = async (itemData) => {
    const isEdit = itemData[PRIMARY_KEY];
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm bảng giá mới này?";

    if (!window.confirm(message)) return false;

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
    } catch {
      // const errMsg = err.response?.data?.message || err.message;
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

  const getDetailFields = () => [
    { key: "maBangGia", label: "MÃ BẢNG GIÁ", readOnly: true },
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
    { key: "giaKhoiDiem", label: "GIÁ KHỞI ĐIỂM", type: "number" },
    { key: "giaTheoKm", label: "GIÁ THEO KM", type: "number" },
    { key: "phuThu", label: "PHỤ THU", type: "number" },
  ];

  // --- 3. TRUYỀN onExport VÀO PAGE LAYOUT ---
  return (
    <>
      <PageLayout
        title={PAGE_TITLE}
        onAddClick={() => setIsAddModalOpen(true)}
        onExport={handleExport} // <-- TRUYỀN HÀM VÀO ĐÂY
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

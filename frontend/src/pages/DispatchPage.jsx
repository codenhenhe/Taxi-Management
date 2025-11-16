// src/pages/PhanCongXePage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
import SearchBox from "../components/common/SearchBox";
import Pagination from "../components/common/Pagination";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { Trash2, CheckSquare } from "lucide-react";

/**
 * Hàm helper để định dạng chuỗi ISO (T) thành Giờ & Ngày
 */
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

export default function PhanCongXePage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // --- 1. SỬA LỖI SORT KEY (thêm 'id.') ---
  const [queryParams, setQueryParams] = useState({
    filters: {},
    sort: { by: "id.thoiGianBatDau", dir: "desc" }, // Sắp xếp theo khóa phức hợp
  });
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5;

  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [xeList, setXeList] = useState([]);
  const [taiXeList, setTaiXeList] = useState([]);

  // --- 2. CẤU HÌNH ---
  const ENDPOINT = "/api/phan-cong-xe";
  const PAGE_TITLE = "Quản lý Phân công";

  // --- 3. HÀM CẤU HÌNH FIELDS (Khớp Controller) ---
  const getSearchFields = () => [
    { key: "maTaiXe", placeholder: "Mã tài xế", type: "text" },
    { key: "maXe", placeholder: "Mã xe", type: "text" },
    { key: "tuTGBatDau", label: "Bắt đầu từ", type: "date" },
    { key: "denTGBatDau", label: "Bắt đầu đến", type: "date" },
    { key: "tuTGKetThuc", label: "Kết thúc từ", type: "date" },
    { key: "denTGKetThuc", label: "Kết thúc đến", type: "date" },
  ];

  // --- 4. SỬA LỖI SORT KEYS (thêm 'id.') ---
  const sortFields = [
    { key: "id.thoiGianBatDau", label: "Thời gian bắt đầu" },
    { key: "thoiGianKetThuc", label: "Thời gian kết thúc" }, // Giả sử đây là trường thường
    { key: "id.maTaiXe", label: "Mã tài xế" },
    { key: "id.maXe", label: "Mã xe" },
  ];

  // --- 5. SỬA COLUMNS (Lấy DTO đã phẳng) ---
  const columns = [
    { key: "maTaiXe", header: "Mã tài xế", align: "left" },
    { key: "tenTaiXe", header: "Tên tài xế", align: "left" },
    { key: "maXe", header: "Mã xe", align: "left" },
    { key: "bienSoXe", header: "Biển số xe", align: "left" },
    {
      key: "thoiGianBatDau",
      header: "Thời gian bắt đầu",
      render: (item) => formatDateTimeCell(item.thoiGianBatDau),
    },
    {
      key: "thoiGianKetThuc",
      header: "Thời gian kết thúc",
      render: (item) => formatDateTimeCell(item.thoiGianKetThuc),
    },
    {
      key: "actions",
      header: "Hành động",
      render: (item) => (
        <div className="flex justify-center gap-3">
          {item.thoiGianKetThuc === null && (
            <button
              onClick={() => handleKetThucCa(item)}
              className="text-white px-4 py-1 rounded-md bg-green-500 cursor-pointer hover:bg-green-800"
              title="Kết thúc ca"
            >
              <CheckSquare size={18} />
            </button>
          )}
          <button
            onClick={() => handleDelete(item)}
            className="text-white bg-red-500 px-4 py-1 rounded-md cursor-pointer hover:bg-red-800"
            title="Xóa"
          >
            <Trash2 size={18} />
          </button>
        </div>
      ),
    },
  ];

  // --- 6. LOGIC FETCH (Giữ nguyên, đã đúng) ---
  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      // Dọn dẹp filter (bỏ giá trị rỗng)
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

  // --- 7. SỬA FETCH DROPDOWNS (Sửa trạng thái) ---
  const fetchDropdowns = async () => {
    try {
      const [xeRes, taiXeRes] = await Promise.all([
        apiClient.get("/api/xe", {
          params: { size: 1000, trangThaiXe: "SAN_SANG" }, // Sửa: trangThaiXe
        }),
        apiClient.get("/api/tai-xe", {
          params: { size: 1000, trangThai: "DANG_LAM_VIEC" }, // Sửa: trangThai
        }),
      ]);
      setXeList(xeRes.data.content);
      setTaiXeList(taiXeRes.data.content);
    } catch {
      toast.error("Lỗi khi tải danh sách xe hoặc tài xế");
    }
  };

  useEffect(() => {
    fetchData(); // Chạy khi params thay đổi
  }, [fetchData]);

  useEffect(() => {
    fetchDropdowns(); // Chạy 1 lần
  }, []);

  // --- 8. LOGIC CRUD (Giữ nguyên, đã đúng) ---
  const handleSave = async (itemData) => {
    if (!window.confirm("Bạn có chắc chắn muốn thêm phân công này?"))
      return false;
    try {
      await apiClient.post(ENDPOINT, itemData);
      toast.success("Thêm mới thành công!");
      fetchData();
      fetchDropdowns();
      return true;
    } catch (err) {
      toast.error(
        `Lưu thất bại: ${err.response?.data?.message || err.message}`
      );
      return false;
    }
  };

  const handleKetThucCa = async (item) => {
    if (!window.confirm("Bạn có chắc chắn muốn kết thúc ca này?")) return;
    try {
      // DTO của Controller chỉ cần 3 trường
      const dto = {
        maTaiXe: item.maTaiXe,
        maXe: item.maXe,
        thoiGianBatDau: item.thoiGianBatDau,
      };
      await apiClient.put(`${ENDPOINT}/ket-thuc`, dto);
      toast.success("Kết thúc ca thành công!");
      fetchData();
      fetchDropdowns();
    } catch (err) {
      toast.error(`Thất bại: ${err.response?.data?.message || err.message}`);
    }
  };

  const handleDelete = async (item) => {
    if (!window.confirm("Bạn có chắc chắn muốn xóa phân công này?")) return;
    try {
      await apiClient.delete(ENDPOINT, {
        params: {
          maTaiXe: item.maTaiXe,
          maXe: item.maXe,
          thoiGianBatDau: item.thoiGianBatDau,
        },
      });
      toast.success("Xóa thành công!");
      fetchData();
    } catch (err) {
      toast.error(
        `Xóa thất bại: ${err.response?.data?.message || err.message}`
      );
    }
  };

  // --- 9. HÀM XỬ LÝ SỰ KIỆN (Giữ nguyên) ---
  const handleFilterAndSort = (params) => {
    setQueryParams(params);
    setPage(0);
  };

  const handlePageChange = (newPage) => {
    setPage(newPage);
  };

  // --- 10. SỬA FIELDS CHO MODAL (Hiển thị đúng tên) ---
  const getDetailFields = () => [
    {
      key: "maTaiXe",
      label: "TÀI XẾ (RẢNH)",
      type: "select",
      options: taiXeList.map((tx) => tx.maTaiXe),
      optionLabels: taiXeList.reduce((acc, tx) => {
        // Dùng tenTaiXe (từ TaiXeDTO)
        acc[tx.maTaiXe] = `${tx.tenTaiXe} (${tx.maTaiXe})`;
        return acc;
      }, {}),
    },
    {
      key: "maXe",
      label: "XE (SẴN SÀNG)",
      type: "select",
      options: xeList.map((x) => x.maXe),
      optionLabels: xeList.reduce((acc, x) => {
        // Dùng bienSoXe (từ XeDTO)
        acc[x.maXe] = `${x.bienSoXe} (${x.maXe})`;
        return acc;
      }, {}),
    },
  ];

  // --- 11. RENDER (Giữ nguyên) ---
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
          // Sửa: Dùng thoiGianBatDau làm key phụ (vẫn nên dùng key chính)
          // Tốt nhất là DTO nên trả về một 'id' duy nhất (ví dụ: maTaiXe + maXe + tg)
          primaryKeyField="thoiGianBatDau"
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

      {/* AddModal giữ nguyên */}
      <AddModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSave={handleSave}
        fields={getDetailFields()}
        title={`Thêm mới ${PAGE_TITLE}`}
      />
    </>
  );
}

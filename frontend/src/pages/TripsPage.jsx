// src/pages/ChuyenDiPage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
import EditModal from "../components/common/EditModal";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { Pencil, Trash2 } from "lucide-react";

function formatDateTimeCell(dateTimeString) {
  // Xử lý nếu thời gian kết thúc là null
  if (!dateTimeString) {
    return <span className="text-gray-400">—</span>;
  }

  try {
    const date = new Date(dateTimeString);

    // Lấy Giờ:Phút (ví dụ: 22:41)
    const time = date.toLocaleTimeString("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
    });

    // Lấy Ngày/Tháng/Năm (ví dụ: 14/11/2025)
    const day = date.toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });

    // Trả về JSX với 2 dòng
    return (
      <div className="flex flex-col items-center">
        <span className="font-medium">{time}</span>
        <span className="text-xs text-gray-600">{day}</span>
      </div>
    );
  } catch {
    // Xử lý nếu ngày giờ không hợp lệ
    return <span className="text-red-500">Lỗi ngày</span>;
  }
}

export default function ChuyenDiPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState({});
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  // --- 1. STATE MỚI CHO DROPDOWN ---
  const [xeList, setXeList] = useState([]);
  const [khachHangList, setKhachHangList] = useState([]);

  // --- 2. CẤU HÌNH ---
  const ENDPOINT = "/api/chuyen-di";
  const PRIMARY_KEY = "maChuyenDi"; // Giả định
  const PAGE_TITLE = "Quản lý Chuyến đi";

  const searchFields = [
    { key: "maChuyenDi", placeholder: "MÃ CHUYẾN" },
    { key: "tenKhachHang", placeholder: "TÊN KHÁCH HÀNG" },
  ];

  const columns = [
    { key: "maChuyen", header: "Mã Chuyến" },
    { key: "diemDon", header: "Điểm đón" },
    { key: "diemTra", header: "Điểm trả" },
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
    { key: "soKmDi", header: "Số km đã đi" },
    { key: "cuocPhi", header: "Cước phí" },
    { key: "maXe", header: "Mã xe" },
    { key: "maKhachHang", header: "Mã khách hàng" },
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
          {/* Bạn có thể thêm nút "Hoàn tất" ở đây sau */}
        </div>
      ),
    },
  ];

  // --- 3. LOGIC CRUD ---
  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await apiClient.get(ENDPOINT);
      setData(response.data);
    } catch (err) {
      setError(err.response?.data?.message || err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  // --- 4. LOGIC MỚI: TẢI DROPDOWN ---
  const fetchDropdowns = async () => {
    try {
      const [xeRes, khRes] = await Promise.all([
        apiClient.get("/api/xe?trangThai=SAN_SANG"), // Chỉ lấy xe sẵn sàng
        apiClient.get("/api/khach-hang"),
      ]);
      setXeList(xeRes.data);
      setKhachHangList(khRes.data);
    } catch (err) {
      toast.error("Lỗi khi tải danh sách xe hoặc khách hàng", err);
    }
  };

  useEffect(() => {
    fetchData();
    fetchDropdowns();
  }, [fetchData]);

  const handleSave = async (itemData) => {
    // ... (Giống hệt XePage)
    const isEdit = itemData[PRIMARY_KEY];
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm chuyến đi mới này?";

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
    // ... (Giống hệt XePage)
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

  // --- 5. LOGIC MODAL & LỌC (Giống XePage) ---
  const handleOpenEditModal = (item) => {
    setSelectedItem(item);
    setIsEditModalOpen(true);
  };

  const filteredData = (Array.isArray(data) ? data : []).filter((item) =>
    Object.keys(search).every((key) =>
      String(item[key] || "")
        .toLowerCase()
        .includes(search[key].toLowerCase())
    )
  );

  // --- 6. TẠO FIELDS ĐỘNG ---
  const getDetailFields = () => [
    { key: "maChuyenDi", label: "MÃ CHUYẾN", readOnly: true },
    {
      key: "maKhachHang",
      label: "KHÁCH HÀNG",
      type: "select",
      options: khachHangList.map((kh) => kh.maKhachHang),
      optionLabels: khachHangList.reduce((acc, kh) => {
        acc[kh.maKhachHang] = `${kh.tenKhachHang} (${kh.soDienThoai})`;
        return acc;
      }, {}),
    },
    {
      key: "maXe",
      label: "XE (SẴN SÀNG)",
      type: "select",
      options: xeList.map((x) => x.maXe),
      optionLabels: xeList.reduce((acc, x) => {
        acc[x.maXe] = x.bienSo;
        return acc;
      }, {}),
    },
    { key: "diemDon", label: "ĐIỂM ĐÓN", type: "text" },
    { key: "diemTra", label: "ĐIỂM TRẢ", type: "text" },
    { key: "soKm", label: "SỐ KM (DỰ KIẾN)", type: "number" },
  ];

  // --- 7. RENDER (Giống XePage) ---
  return (
    <>
      <PageLayout
        title={PAGE_TITLE}
        searchFields={searchFields}
        onAddClick={() => setIsAddModalOpen(true)}
        searchValues={search}
        onSearch={setSearch}
      >
        <DataTable
          data={filteredData}
          columns={columns}
          loading={loading}
          error={error}
          onRowClick={() => {}}
          primaryKeyField={PRIMARY_KEY}
        />
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
        fields={getDetailFields()} // Cho phép sửa cả chuyến đi
        title={`Cập nhật ${PAGE_TITLE}`}
      />
    </>
  );
}

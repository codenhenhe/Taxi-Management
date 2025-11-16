// src/pages/KhachHangPage.jsx
import { useState, useEffect, useCallback, useMemo } from "react"; // <-- Thêm useMemo
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
import EditModal from "../components/common/EditModal";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { ArrowUp, ArrowDown } from "lucide-react"; // <-- Thêm icon sắp xếp

export default function KhachHangPage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState({});
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  // --- 1. THÊM STATE SẮP XẾP ---
  const [sortConfig, setSortConfig] = useState({
    key: 'tenKhachHang',  // Sắp xếp theo Tên Khách Hàng
    direction: 'ascending', // Tăng dần
  });

  // --- 2. CẤU HÌNH (Giữ nguyên) ---
  const ENDPOINT = "/api/khach-hang";
  const PRIMARY_KEY = "maKhachHang";
  const PAGE_TITLE = "Quản lý Khách hàng";

  const searchFields = [
    { key: "maKhachHang", placeholder: "MÃ KHÁCH HÀNG" },
    { key: "tenKhachHang", placeholder: "HỌ TÊN" },
  ];

  // --- 3. HÀM XỬ LÝ SẮP XẾP MỚI ---
  const handleSort = (key) => {
    let direction = 'ascending';
    // Nếu bấm lại cột đang sắp xếp -> Đảo chiều
    if (sortConfig.key === key && sortConfig.direction === 'ascending') {
      direction = 'descending';
    }
    setSortConfig({ key, direction });
  };

  // Helper để hiển thị icon
  const getSortIcon = (key) => {
    if (sortConfig.key !== key) {
      return null; // Không hiển thị icon nếu không phải cột đang sort
    }
    if (sortConfig.direction === 'ascending') {
      return <ArrowUp size={16} className="inline ml-1 text-blue-500" />;
    }
    return <ArrowDown size={16} className="inline ml-1 text-blue-500" />;
  };

  // --- 4. SỬA LẠI CỘT (COLUMNS) ĐỂ CÓ THỂ BẤM SẮP XẾP ---
  const columns = [
    {
      key: "maKhachHang",
      // Thêm nút bấm vào header
      header: (
        <button
          onClick={() => handleSort('maKhachHang')}
          className="font-bold flex items-center group"
        >
          Mã KH
          {/* Hiển thị icon khi hover hoặc đang active */}
          <span className={sortConfig.key === 'maKhachHang' ? 'opacity-100' : 'opacity-0 group-hover:opacity-100 transition-opacity'}>
            {getSortIcon('maKhachHang') || <ArrowUp size={16} className="inline ml-1 opacity-30" />}
          </span>
        </button>
      ),
    },
    {
      key: "tenKhachHang",
      // Thêm nút bấm vào header
      header: (
        <button
          onClick={() => handleSort('tenKhachHang')}
          className="font-bold flex items-center group"
        >
          Họ tên
          {/* Hiển thị icon khi hover hoặc đang active */}
          <span className={sortConfig.key === 'tenKhachHang' ? 'opacity-100' : 'opacity-0 group-hover:opacity-100 transition-opacity'}>
            {getSortIcon('tenKhachHang') || <ArrowUp size={16} className="inline ml-1 opacity-30" />}
          </span>
        </button>
      ),
    },
    { key: "sdt", header: "Số điện thoại" }, // (Không cho sắp xếp cột này)
    {
      key: "actions",
      header: "Hành động",
      render: (item) => (
        <div className="flex justify-center gap-3">
          <button
            onClick={(e) => { e.stopPropagation(); handleOpenEditModal(item); }}
            className="text-white px-4 py-1 rounded-md bg-blue-500 cursor-pointer hover:bg-blue-800"
            title="Sửa"
          >
            Sửa
          </button>
          <button
            onClick={(e) => { e.stopPropagation(); handleDelete(item[PRIMARY_KEY]); }}
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
    { key: "maKhachHang", label: "MÃ KHÁCH HÀNG", readOnly: true },
    { key: "tenKhachHang", label: "HỌ TÊN" },
    { key: "sdt", label: "SỐ ĐIỆN THOẠI", type: "tel" },
  ];

  // --- 5. LOGIC CRUD (Giữ nguyên) ---
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
  }, []); // ENDPOINT được định nghĩa bên ngoài, không cần đưa vào dependencies

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleSave = async (itemData) => {
    // ... (Giữ nguyên logic handleSave)
    const isEdit = itemData[PRIMARY_KEY];
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm khách hàng mới này?";

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
    // ... (Giữ nguyên logic handleDelete)
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

  // --- 6. SỬA LẠI LOGIC LỌC VÀ SẮP XẾP ---
  const handleOpenEditModal = (item) => {
    setSelectedItem(item);
    setIsEditModalOpen(true);
  };

  // Đổi tên biến 'filteredData' -> 'processedData'
  // Dùng useMemo để tối ưu, chỉ tính lại khi data, search, hoặc sortConfig thay đổi
  const processedData = useMemo(() => {
    if (!Array.isArray(data)) return [];

    // 1. Lọc (Logic cũ của bạn)
    const filtered = data.filter((item) =>
      Object.keys(search).every((key) =>
        String(item[key] || "")
          .toLowerCase()
          .includes(search[key].toLowerCase())
      )
    );

    // 2. Sắp xếp (Logic mới)
    if (sortConfig.key) {
      // Dùng [...filtered] để tạo bản sao trước khi sort, tránh mutate state
      return [...filtered].sort((a, b) => {
        const aValue = String(a[sortConfig.key] || '');
        const bValue = String(b[sortConfig.key] || '');
        
        // So sánh kiểu số nếu là mã
        if (sortConfig.key === 'maKhachHang') {
           // Lấy số từ "KH001", "KH010",...
           const aNum = parseInt(aValue.replace( /[^0-9]/g, '' ), 10) || 0;
           const bNum = parseInt(bValue.replace( /[^0-9]/g, '' ), 10) || 0;
           return sortConfig.direction === 'ascending' ? aNum - bNum : bNum - aNum;
        }

        // So sánh kiểu chuỗi (cho tên)
        if (sortConfig.direction === 'ascending') {
          return aValue.localeCompare(bValue, 'vi'); // Sắp xếp tiếng Việt
        } else {
          return bValue.localeCompare(aValue, 'vi'); // Sắp xếp tiếng Việt
        }
      });
    }

    return filtered;
  }, [data, search, sortConfig]); // <-- Dependencies

  // --- 7. RENDER (Sửa data table) ---
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
          data={processedData} // <-- SỬA TÊN BIẾN
          columns={columns}
          loading={loading}
          error={error}
          onRowClick={handleOpenEditModal} // <-- Cho phép click vào hàng để sửa
          primaryKeyField={PRIMARY_KEY}
        />
      </PageLayout>

      <AddModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSave={handleSave}
        fields={detailFields.filter((f) => !f.readOnly)} // Ẩn trường readOnly khi Thêm
        title={`Thêm mới ${PAGE_TITLE}`}
      />

      <EditModal
        isOpen={isEditModalOpen}
        onClose={() => setIsEditModalOpen(false)}
        onSave={handleSave}
        item={selectedItem}
        fields={detailFields}
        title={`Cập nhật ${PAGE_TITLE}`}
      />
    </>
  );
}
// src/pages/XePage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
import EditModal from "../components/common/EditModal";
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { Pencil, Trash2 } from "lucide-react";

const TRANG_THAI_XE_LABELS = {
  SAN_SANG: "Sẵn sàng",
  BAO_TRI: "Đang bảo trì",
  DANG_CHAY: "Bận",
};

// 2. BẢN ĐỒ DỊCH SANG MÀU SẮC (Tailwind classes)
const TRANG_THAI_XE_STYLES = {
  SAN_SANG: "bg-green-200 text-black",
  BAO_TRI: "bg-yellow-200 text-black",
  DANG_CHAY: "bg-red-300 text-black",
};

export default function XePage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState({});
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);

  const [loaiXeList, setLoaiXeList] = useState([]);

  const ENDPOINT = "/api/xe";
  const PRIMARY_KEY = "maXe";
  const PAGE_TITLE = "Quản lý Xe";

  // --- 1. SỬA KEYS TRONG SEARCH FIELDS ---
  const searchFields = [
    { key: "maXe", placeholder: "MÃ XE" },
    { key: "bienSoXe", placeholder: "BIỂN SỐ" },
  ];

  // --- 2. SỬA KEYS & RENDER TRONG COLUMNS ---
  const columns = [
    { key: "maXe", header: "Mã xe" },
    { key: "bienSoXe", header: "Biển số" },
    { key: "mauXe", header: "Màu xe" },
    { key: "namSanXuat", header: "Năm sản xuất" },
    {
      key: "loaiXe", // Sửa key
      header: "Mã loại xe",
      render: (item) => item.loaiXe?.maLoai || "N/A", // Dùng render
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

  // --- 3. SỬA LOGIC FETCHER ---
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

  const fetchLoaiXe = async () => {
    try {
      const res = await apiClient.get("/api/loai-xe");
      setLoaiXeList(res.data);
    } catch (err) {
      toast.error("Lỗi khi tải danh sách loại xe", err);
    }
  };

  useEffect(() => {
    fetchData();
    fetchLoaiXe();
  }, [fetchData]);

  // --- 4. SỬA HANDLESAVE (Làm sạch DTO) ---
  const handleSave = async (itemData) => {
    const isEdit = itemData[PRIMARY_KEY];
    const message = isEdit
      ? "Bạn có chắc chắn muốn lưu các thay đổi này?"
      : "Bạn có chắc chắn muốn thêm xe mới này?";

    if (!window.confirm(message)) return false;

    // Tạo bản sao DTO để gửi đi
    const requestData = { ...itemData };

    // Xóa object 'loaiXe' lồng nhau, vì backend chỉ muốn 'maLoaiXe' (đã có sẵn)
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

  // --- 5. SỬA HANDLEOPENEDITMODAL (Làm phẳng) ---
  const handleOpenEditModal = (item) => {
    // "Làm phẳng" item trước khi gửi vào modal
    const flatItem = {
      ...item,
      maLoaiXe: item.loaiXe?.maLoai, // Lấy ID từ object con
    };
    setSelectedItem(flatItem);
    setIsEditModalOpen(true);
  };

  const filteredData = (Array.isArray(data) ? data : []).filter((item) =>
    Object.keys(search).every((key) =>
      String(item[key] || "")
        .toLowerCase()
        .includes(search[key].toLowerCase())
    )
  );

  // --- 6. SỬA DETAILFIELDS (Dùng key mới) ---
  const getDetailFields = () => [
    { key: "maXe", label: "MÃ XE", readOnly: true },
    { key: "bienSoXe", label: "BIỂN SỐ" },
    { key: "mauXe", label: "MÀU XE" },
    { key: "namSanXuat", label: "NĂM SẢN XUẤT", type: "number" },
    {
      key: "maLoaiXe", // Key này phải khớp với DTO Request
      label: "LOẠI XE",
      type: "select",
      options: loaiXeList.map((lx) => lx.maLoai), // Dùng maLoai
      optionLabels: loaiXeList.reduce((acc, lx) => {
        acc[lx.maLoai] = lx.tenLoai; // Dùng maLoai và tenLoai
        return acc;
      }, {}),
    },
    {
      key: "trangThaiXe",
      label: "TRẠNG THÁI",
      type: "select",
      options: ["SAN_SANG", "DANG_BAO_TRI", "NGUNG_HOAT_DONG"],
      optionLabels: {
        SAN_SANG: "Sẵn sàng",
        DANG_BAO_TRI: "Đang bảo trì",
        NGUNG_HOAT_DONG: "Ngưng hoạt động",
      },
    },
  ];

  // --- 7. RENDER ---
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
        item={selectedItem} // Gửi item đã được làm phẳng
        fields={getDetailFields()}
        title={`Cập nhật ${PAGE_TITLE}`}
      />
    </>
  );
}

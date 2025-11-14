// src/pages/PhanCongXePage.jsx
import { useState, useEffect, useCallback } from "react";
import PageLayout from "../components/common/PageLayout";
import DataTable from "../components/common/DataTable";
import AddModal from "../components/common/AddModal";
// import EditModal from "../components/common/EditModal"; // Không dùng Edit
import apiClient from "../api/apiClient";
import { toast } from "react-hot-toast";
import { Trash2, CheckSquare } from "lucide-react"; // Icon khác

export default function PhanCongXePage() {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [search, setSearch] = useState({});
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  // const [isEditModalOpen, setIsEditModalOpen] = useState(false); // Không dùng
  // const [selectedItem, setSelectedItem] = useState(null);

  // --- 1. STATE MỚI CHO DROPDOWN ---
  const [xeList, setXeList] = useState([]);
  const [taiXeList, setTaiXeList] = useState([]);

  // --- 2. CẤU HÌNH ---
  const ENDPOINT = "/api/phan-cong-xe";
  // KHÔNG CÓ PRIMARY KEY ĐƠN GIẢN
  const PAGE_TITLE = "Quản lý Phân công";

  const searchFields = [
    { key: "maTaiXe", placeholder: "MÃ TÀI XẾ" },
    { key: "bienSo", placeholder: "BIỂN SỐ XE" },
  ];

  const columns = [
    { key: "maTaiXe", header: "Mã tài xế" },
    { key: "maXe", header: "Mã xe" },
    { key: "thoiGianBatDau", header: "Thời gian bắt đầu" },
    { key: "thoiGianKetThuc", header: "Thời gian kết thúc" },
    {
      key: "actions",
      header: "Hành động",
      render: (item) => (
        <div className="flex justify-center gap-3">
          {item.trangThai === "DANG_LAM_VIEC" && ( // Chỉ hiển thị nếu đang làm
            <button
              onClick={() => handleKetThucCa(item)}
              className="text-white px-4 py-1 rounded-md bg-green-500 cursor-pointer hover:bg-green-800"
              title="Kết thúc ca"
            >
              <CheckSquare size={18} />
            </button>
          )}
          <button
            onClick={() => handleDelete(item)} // Gửi cả item
            className="text-white bg-red-500 px-4 py-1 rounded-md cursor-pointer hover:bg-red-800"
            title="Xóa"
          >
            <Trash2 size={18} />
          </button>
        </div>
      ),
    },
  ];

  // --- 3. LOGIC CRUD ---
  const fetchData = useCallback(async () => {
    // ... (Giống)
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
      const [xeRes, taiXeRes] = await Promise.all([
        apiClient.get("/api/xe?trangThai=SAN_SANG"), // Chỉ lấy xe sẵn sàng
        apiClient.get("/api/tai-xe?trangThai=active"), // Chỉ lấy tài xế rảnh
      ]);
      setXeList(xeRes.data);
      setTaiXeList(taiXeRes.data);
    } catch (err) {
      toast.error("Lỗi khi tải danh sách xe hoặc tài xế", err);
    }
  };

  useEffect(() => {
    fetchData();
    fetchDropdowns();
  }, [fetchData]);

  // CHỈ CÓ THÊM MỚI (POST)
  const handleSave = async (itemData) => {
    if (!window.confirm("Bạn có chắc chắn muốn thêm phân công này?"))
      return false;

    try {
      await apiClient.post(ENDPOINT, itemData);
      toast.success("Thêm mới thành công!");
      fetchData();
      fetchDropdowns(); // Tải lại danh sách xe/tài xế rảnh
      return true;
    } catch (err) {
      toast.error(
        `Lưu thất bại: ${err.response?.data?.message || err.message}`
      );
      return false;
    }
  };

  // HÀNH ĐỘNG ĐẶC BIỆT: KẾT THÚC CA
  const handleKetThucCa = async (item) => {
    if (!window.confirm("Bạn có chắc chắn muốn kết thúc ca này?")) return;
    try {
      // Body request theo DTO
      const dto = {
        maTaiXe: item.maTaiXe,
        maXe: item.maXe,
        thoiGianBatDau: item.thoiGianBatDau,
      };
      await apiClient.put(`${ENDPOINT}/ket-thuc`, dto);
      toast.success("Kết thúc ca thành công!");
      fetchData();
      fetchDropdowns(); // Tải lại danh sách
    } catch (err) {
      toast.error(`Thất bại: ${err.response?.data?.message || err.message}`);
    }
  };

  // XÓA DÙNG KHÓA PHỨC HỢP
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

  // --- 5. LOGIC MODAL & LỌC ---
  const filteredData = (Array.isArray(data) ? data : []).filter((item) =>
    Object.keys(search).every((key) =>
      String(item[key] || "")
        .toLowerCase()
        .includes(search[key].toLowerCase())
    )
  );

  // --- 6. TẠO FIELDS ĐỘNG ---
  const getDetailFields = () => [
    {
      key: "maTaiXe",
      label: "TÀI XẾ (RẢNH)",
      type: "select",
      options: taiXeList.map((tx) => tx.maTaiXe),
      optionLabels: taiXeList.reduce((acc, tx) => {
        acc[tx.maTaiXe] = tx.maTaiXe;
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
    // thoiGianBatDau sẽ được set tự động ở backend
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
          primaryKeyField={"thoiGianBatDau"} // Dùng tạm
        />
      </PageLayout>

      <AddModal
        isOpen={isAddModalOpen}
        onClose={() => setIsAddModalOpen(false)}
        onSave={handleSave}
        fields={getDetailFields()}
        title={`Thêm mới ${PAGE_TITLE}`}
      />
      {/* Không có EditModal */}
    </>
  );
}

// src/data/mockTaxiData.js

// Dữ liệu mẫu cho thực thể TAI_XE
export const mockDrivers = [
  { 
    ma_taxi: 'TX001', 
    ten_tai_xe: 'Nguyễn Văn A', 
    so_hieu_GPLX: 'GPLX001', 
    ngay_sinh: '1985-05-10', // Định dạng YYYY-MM-DD
    sdt: '0901234567', 
    trang_thai: 'Ranh' // Ranh, Bận, Nghỉ
  },
  { 
    ma_taxi: 'TX002', 
    ten_tai_xe: 'Lê Thị B', 
    so_hieu_GPLX: 'GPLX002', 
    ngay_sinh: '1990-11-20', 
    sdt: '0917654321', 
    trang_thai: 'Bận' 
  },
  { 
    ma_taxi: 'TX003', 
    ten_tai_xe: 'Trần Văn C', 
    so_hieu_GPLX: 'GPLX003', 
    ngay_sinh: '1978-01-01', 
    sdt: '0987123456', 
    trang_thai: 'Nghỉ' 
  },
  { 
    ma_taxi: 'TX004', 
    ten_tai_xe: 'Phạm Thị D', 
    so_hieu_GPLX: 'GPLX004', 
    ngay_sinh: '1995-03-15', 
    sdt: '0903333444', 
    trang_thai: 'Ranh' 
  },
];

// Dữ liệu mẫu cho thực thể KHACH_HANG
export const mockCustomers = [
  { 
    ma_khach_hang: 'KH001', 
    ten_khach_hang: 'Phạm Văn D', 
    KH_sdt: '0909888777' 
  },
  { 
    ma_khach_hang: 'KH002', 
    ten_khach_hang: 'Hoàng Thị E', 
    KH_sdt: '0911222333' 
  },
  { 
    ma_khach_hang: 'KH003', 
    ten_khach_hang: 'Võ Văn F', 
    KH_sdt: '0975432100' 
  },
];

// Dữ liệu mẫu cho thực thể CHUYEN_DI
export const mockTrips = [
  { 
    ma_chuyen: 'CD001', 
    ma_xe: 'XE005', // Giả định có liên kết đến bảng XE
    ma_khach_hang: 'KH001', 
    ma_tai_xe: 'TX001', // Thêm trường ma_tai_xe để dễ dàng liên kết
    diem_don: '35 Trần Hưng Đạo, Q.1', 
    diem_tra: '123 Cách Mạng T.8, Q.3', 
    TG_nhan: '2023-11-01T10:00:00Z', // Định dạng ISO 8601
    TG_tra: '2023-11-01T10:30:00Z', 
    TG_goc: '2023-11-01T10:00:00.000', // Dùng cho input datetime-local
    so_KM_di: 5.2, 
    cuoc_phi: 85000 
  },
  { 
    ma_chuyen: 'CD002', 
    ma_xe: 'XE010', 
    ma_khach_hang: 'KH002', 
    ma_tai_xe: 'TX002',
    diem_don: 'Sân bay Tân Sơn Nhất', 
    diem_tra: 'Khách sạn Rex', 
    TG_nhan: '2023-10-25T15:30:00Z', 
    TG_tra: '2023-10-25T16:15:00Z', 
    TG_goc: '2023-10-25T15:30:00.000', 
    so_KM_di: 12.8, 
    cuoc_phi: 320000 
  },
  { 
    ma_chuyen: 'CD003', 
    ma_xe: 'XE007', 
    ma_khach_hang: 'KH003', 
    ma_tai_xe: 'TX004',
    diem_don: 'Căn hộ Rivergate', 
    diem_tra: 'Landmark 81', 
    TG_nhan: '2023-11-05T08:00:00Z', 
    TG_tra: '2023-11-05T08:45:00Z', 
    TG_goc: '2023-11-05T08:00:00.000', 
    so_KM_di: 10.5, 
    cuoc_phi: 150000 
  },
];

// Dữ liệu mẫu cho thực thể XE (Nếu cần cho việc tra cứu trong CHUYEN_DI)
export const mockVehicles = [
  { ma_xe: 'XE005', bien_so_xe: '51F-12345', ma_tai_xe: 'TX001', mau_xe: 'Trắng', NSX: 'Toyota', trang_thai_xe: 'Hoạt động' },
  { ma_xe: 'XE010', bien_so_xe: '51G-67890', ma_tai_xe: 'TX002', mau_xe: 'Đen', NSX: 'Kia', trang_thai_xe: 'Hoạt động' },
  { ma_xe: 'XE007', bien_so_xe: '51H-11223', ma_tai_xe: 'TX004', mau_xe: 'Bạc', NSX: 'Mazda', trang_thai_xe: 'Hoạt động' },
];
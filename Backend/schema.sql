-- A. Tạo DATABASE
DROP DATABASE IF EXISTS taxi_management;

CREATE DATABASE taxi_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE taxi_management;

-- A1. Bảng Tài xế
CREATE TABLE TAI_XE (
    ma_tai_xe VARCHAR(50) PRIMARY KEY,
    ten_tai_xe VARCHAR(100) NOT NULL,
    so_hieu_GPLX VARCHAR(50) NOT NULL UNIQUE,
    ngay_sinh DATE,
    sdt VARCHAR(20),
    trang_thai ENUM(
        'Đang làm việc',
        'Nghỉ phép',
        'Nghỉ việc'
    ) DEFAULT 'Đang làm việc'
);

-- A2. Bảng loại xe
CREATE TABLE LOAI_XE (
    ma_loai VARCHAR(50) PRIMARY KEY,
    ten_loai VARCHAR(50) NOT NULL
);

-- A3. Bảng xe
CREATE TABLE XE (
    ma_xe VARCHAR(50) PRIMARY KEY,
    bien_so_xe VARCHAR(20) NOT NULL UNIQUE,
    mau_xe VARCHAR(30),
    nha_sx VARCHAR(50),
    trang_thai_xe ENUM(
        'Sẵn sàng',
        'Bảo trì',
        'Đang chạy'
    ) DEFAULT 'Sẵn sàng',
    ma_tai_xe VARCHAR(50),
    ma_loai VARCHAR(50),
    FOREIGN KEY (ma_tai_xe) REFERENCES TAI_XE (ma_tai_xe) ON DELETE SET NULL,
    FOREIGN KEY (ma_loai) REFERENCES LOAI_XE (ma_loai) ON DELETE SET NULL
);

-- A4. Bảng bảng giá
CREATE TABLE BANG_GIA (
    ma_bang_gia VARCHAR(50) PRIMARY KEY,
    gia_khoi_diem DOUBLE NOT NULL,
    gia_theo_km DOUBLE NOT NULL,
    phu_thu DOUBLE DEFAULT 0,
    ma_loai VARCHAR(50),
    FOREIGN KEY (ma_loai) REFERENCES LOAI_XE (ma_loai) ON DELETE CASCADE
);

-- A5. Bảng khách hàng
CREATE TABLE KHACH_HANG (
    ma_khach_hang VARCHAR(50) PRIMARY KEY,
    ten_khach_hang VARCHAR(100) NOT NULL,
    KH_sdt VARCHAR(20) NOT NULL UNIQUE
);

-- A6. Bảng chuyến đi
CREATE TABLE CHUYEN_DI (
    ma_chuyen VARCHAR(50) PRIMARY KEY,
    diem_don VARCHAR(255),
    diem_tra VARCHAR(255),
    tg_don DATETIME,
    tg_tra DATETIME,
    so_km_di DOUBLE,
    cuoc_phi DOUBLE,
    ma_xe VARCHAR(50),
    ma_khach_hang VARCHAR(50),
    FOREIGN KEY (ma_xe) REFERENCES XE (ma_xe) ON DELETE SET NULL,
    FOREIGN KEY (ma_khach_hang) REFERENCES KHACH_HANG (ma_khach_hang) ON DELETE SET NULL
);

-- A7. Bảng bảo trì
CREATE TABLE BAO_TRI_XE (
    ma_bao_tri VARCHAR(50) PRIMARY KEY,
    ngay_bao_tri DATE,
    loai_bao_tri VARCHAR(100),
    chi_phi DOUBLE,
    mo_ta VARCHAR(100),
    ma_xe VARCHAR(50),
    FOREIGN KEY (ma_xe) REFERENCES XE (ma_xe) ON DELETE CASCADE
);




-- B. FUNCTION / TRIGGER / SP

-- B1. FUNCTION 1: Tính cước phí
DELIMITER / /

CREATE FUNCTION calc_fare(p_km DECIMAL(6,2), p_ma_loai VARCHAR(50), p_is_peak BOOLEAN)
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE v_base DECIMAL(10,2);
    DECLARE v_perkm DECIMAL(10,2);
    DECLARE v_extra DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);

    SELECT gia_khoi_diem, gia_theo_km, phu_thu
    INTO v_base, v_perkm, v_extra
    FROM BANG_GIA
    WHERE ma_loai = p_ma_loai
    LIMIT 1;

    SET v_total = v_base + (p_km * v_perkm);
    IF p_is_peak THEN
        SET v_total = v_total + v_extra;
    END IF;

    RETURN v_total;
END //

DELIMITER;


-- B2. FUNCTION 2: ...

DELIMITER $$
-- Xóa hàm cũ nếu tồn tại
DROP FUNCTION IF EXISTS fn_XacDinhGioCaoDiem;

-- Tạo hàm mới
CREATE FUNCTION fn_XacDinhGioCaoDiem()
RETURNS BOOLEAN  -- 1. Thêm kiểu trả về là BOOLEAN (true/false)
READS SQL DATA   -- 2. Đổi thành 'NOT DETERMINISTIC' (vì dùng NOW())
NOT DETERMINISTIC
BEGIN
    -- Khai báo biến
    DECLARE current_hour INT;
    DECLARE current_day_of_week INT;
    
    -- Lấy giờ và ngày hiện tại
    SET current_hour = HOUR(NOW());
    SET current_day_of_week = DAYOFWEEK(NOW()); -- (1=Chủ Nhật, 2=Thứ 2, ..., 7=Thứ Bảy)
    
    -- 3. Logic kiểm tra
    
    -- Kiểm tra xem có phải ngày trong tuần không (Không phải T7 và CN)
    IF current_day_of_week > 1 AND current_day_of_week < 7 THEN
        -- Nếu đúng, kiểm tra khung giờ cao điểm hoặc tăng ca buổi tối
        IF (current_hour >= 7 AND current_hour < 8) -- Sáng: 7:00 - 8:59
           OR 
           (current_hour >= 17 AND current_hour < 18) -- Chiều: 17:00 - 18:59
           OR
           (current_hour >= 22 AND current_hour <= 24) -- Chiều: 17:00 - 18:59
           OR
           (current_hour >= 0 AND current_hour <= 5) -- Chiều: 17:00 - 18:59
        THEN
            RETURN TRUE;
        END IF;
    END IF;
    
    -- Nếu không rơi vào các trường hợp trên
    RETURN FALSE;
END $$
DELIMITER ;

-- B3. TRIGGER 1: Cập nhật trạng thái xe
DELIMITER / /

CREATE TRIGGER trg_update_vehicle_status
AFTER INSERT ON CHUYEN_DI
FOR EACH ROW
BEGIN
    UPDATE XE SET trang_thai_xe = 'Đang chạy'
    WHERE ma_xe = NEW.ma_xe;
END //

DELIMITER;


-- B4. TRIGGER 2:  KIỂM TRA HỢP LỆ TRƯỚC KHI TẠO CHUYẾN
DELIMITER $$
-- DROP TRIGGER trg_validate_trip;
CREATE TRIGGER trg_validate_trip
BEFORE INSERT ON CHUYEN_DI
FOR EACH ROW
BEGIN
	DECLARE driver_status VARCHAR(50);
    DECLARE verhicle_status VARCHAR(50);
    
    SELECT trang_thai_xe INTO verhicle_status
    FROM xe
    WHERE ma_xe = NEW.ma_xe; 
    
    IF verhicle_status!="Sẵn sàng"
    THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Xe  đang bận hoặc đang bảo trì. Không thể tạo chuyến đi mới.';
    END IF;
    
END $$

DELIMITER ;

-- B5. TRIGGER 3: TỰ ĐỘNG CẬP NHẬT TRẠNG THÁI XE SAU KHI BẢO TRÌ
DELIMITER $$
CREATE TRIGGER trg_update_verhicle_after_maintainment
AFTER INSERT ON bao_tri_xe
FOR EACH ROW
BEGIN
	UPDATE xe
    SET trang_thai_xe="Bảo trì"
    WHERE ma_xe=NEW.ma_xe;
END$$
DELIMITER  ;


-- B6. STORED PROCEDURE 1: Tính doanh thu theo tài xế
DELIMITER //

CREATE PROCEDURE sp_revenue_by_driver(IN p_date DATE)
BEGIN
    SELECT 
        tx.ma_tai_xe,
        tx.ten_tai_xe,
        SUM(c.cuoc_phi) AS tong_doanh_thu
    FROM CHUYEN_DI c
    JOIN XE x ON c.ma_xe = x.ma_xe
    JOIN TAI_XE tx ON x.ma_tai_xe = tx.ma_tai_xe
    WHERE DATE(c.tg_don) = p_date
    GROUP BY tx.ma_tai_xe, tx.ten_tai_xe;
END //

DELIMITER ;


-- B7. STORED PROCEDURE 2: ...
DELIMITER $$

CREATE PROCEDURE sp_ThongKeChuyenTheoGio()
BEGIN
    SELECT
        EXTRACT(HOUR FROM tg_don) AS gio_trong_ngay,
        COUNT(*) AS so_luong_chuyen
    FROM
        CHUYEN_DI
    GROUP BY
        EXTRACT(HOUR FROM tg_don)  
    ORDER BY
        gio_trong_ngay ASC;
END$$
DELIMITER ;
CALL sp_ThongKeChuyenTheoGio();

-- B8. STORED PROCEDURE 3: ...

DELIMITER $$
-- DROP PROCEDURE IF EXISTS sp_MONTHLY_VEHICLE_FEE;
CREATE PROCEDURE sp_MONTHLY_VEHICLE_FEE(
    IN p_nam YEAR 
)
BEGIN
    SELECT 
        MONTH(ngay_bao_tri) AS thang_bao_tri, 
        SUM(chi_phi) AS tong_chi_phi 
    FROM 
        bao_tri_xe
    WHERE 
        YEAR(ngay_bao_tri) = p_nam 
    GROUP BY 
        MONTH(ngay_bao_tri);
END $$
DELIMITER ;
-- 9
DELIMITER $$
DROP PROCEDURE IF EXISTS SP_HoanTatChuyenDi;

CREATE PROCEDURE SP_HoanTatChuyenDi(
    IN p_ma_chuyen VARCHAR(50), -- Mã chuyến đi cần hoàn tất
    IN p_so_km_di DOUBLE        -- Số KM thực tế tài xế nhập
)
BEGIN
    -- 1. Khai báo các biến để lưu thông tin lấy từ CSDL
    DECLARE v_ma_loai_xe VARCHAR(50);
    DECLARE v_tg_don DATETIME;
    DECLARE v_is_peak BOOLEAN;
    DECLARE v_final_fare DECIMAL(10, 2);

    -- 2. Lấy thông tin cần thiết của chuyến đi từ CSDL
    --    (Cần biết loại xe và giờ đón để tính tiền)
    SELECT 
        X.ma_loai, 
        CD.tg_don
    INTO 
        v_ma_loai_xe, 
        v_tg_don
    FROM 
        CHUYEN_DI CD
    JOIN 
        XE X ON CD.ma_xe = X.ma_xe
    WHERE 
        CD.ma_chuyen = p_ma_chuyen
    LIMIT 1;

    -- 3. Gọi FUNCTION 1: Kiểm tra giờ cao điểm
    SET v_is_peak = fn_KiemTraGioCaoDiem(v_tg_don);

    -- 4. Gọi FUNCTION 2: Tính cước phí (Hàm bạn vừa tạo)
    SET v_final_fare = calc_fare(p_so_km_di, v_ma_loai_xe, v_is_peak);

    -- 5. Cập nhật lại chuyến đi với thông tin cuối cùng
    UPDATE CHUYEN_DI
    SET
        tg_tra = NOW(),           -- Giờ trả khách là bây giờ
        so_km_di = p_so_km_di,  -- Số KM cuối
        cuoc_phi = v_final_fare -- Cước phí đã tính
    WHERE
        ma_chuyen = p_ma_chuyen;
        
    -- (LÚC NÀY: Trigger 'AFTER UPDATE ON CHUYEN_DI' sẽ tự động
    --  kích hoạt và set trạng thái xe/tài xế về "Rảnh")

END $$
DELIMITER ;



-- C. DỮ LIỆU MẪU
-- C1.TÀI XẾ
INSERT INTO TAI_XE (ma_tai_xe, ten_tai_xe, so_hieu_gplx, ngay_sinh, sdt, trang_thai) VALUES
('TX001',   'Nguyễn Văn An',    'GPLX001', '2002-05-15', '0983456789', 'Đang làm việc'),
('TX002',   'Trần Thị Bích',    'GPLX002', '2000-11-28', '0912345678', 'Đang làm việc'),
('TX003',   'Lê Quang Cường',   'GPLX003', '2004-01-03', '0907890123', 'Đang làm việc'),
('TX004',   'Phạm Minh Dũng',   'GPLX004', '2001-08-19', '0961234567', 'Nghỉ việc'),
('TX005',   'Hoàng Thu Giang',  'GPLX005', '2003-04-10', '0978901234', 'Đang làm việc'),
('TX006',   'Đỗ Trọng Hiếu',    'GPLX006', '2000-12-07', '0945678901', 'Đang làm việc'),
('TX007',   'Bùi Thanh Hương',  'GPLX007', '2002-06-22', '0923456789', 'Rảnh'),
('TX008',   'Vũ Tuấn Khanh',    'GPLX008', '2004-03-01', '0938901234', 'Nghỉ việc'),
('TX009',   'Đặng Việt Lâm',    'GPLX009', '2001-09-14', '0881122334', 'Rảnh'),
('TX010',   'Lương Hồng Sơn',   'GPLX010', '2003-04-23', '0963344556', 'Đang làm việc');


-- C2. LOẠI XE
INSERT INTO LOAI_XE(ma_loai, ten_loai) VALUES
('LX001', 'Xe 4 chỗ'),
('LX002', 'Xe 5 chỗ'),
('LX003', 'Xe 7 chỗ'),
('LX004', 'Xe Limousine');


-- C3. XE
INSERT INTO XE (ma_xe, bien_so_xe, mau_xe, nha_sx, trang_thai_xe, ma_tai_xe, ma_loai) VALUES
('XE001', '51A-00001', 'Trắng',     'Toyota',       'Sẵn sàng',  'TX001', 'LX004'),
('XE002', '51A-00002', 'Xám',       'Hyundai',      'Sẵn sàng',  'TX002', 'LX003'),
('XE003', '51A-00003', 'Đỏ',        'Toyota',       'Sẵn sàng',  'TX006', 'LX002'),
('XE004', '51A-00004', 'Xanh lam',  'Mitsubishi',   'Sẵn sàng',  'TX006', 'LX001'),
('XE005', '51A-00005', 'Đen',       'Toyota',       'Sẵn sàng',  'TX008', 'LX001'),
('XE006', '51A-00006', 'Hồng',      'Hyundai',      'Sẵn sàng',  'TX008', 'LX002'),
('XE007', '51A-00007', 'Vàng',      'KIA',          'Bảo trì',   'TX003', 'LX003'),
('XE008', '51A-00008', 'Vàng',      'Mitsubishi',   'Sẵn sàng',  'TX005', 'LX004'),
('XE009', '51A-00009', 'Trắng',     'Mitsubishi',   'Sẵn sàng',  'TX004', 'LX004'),
('XE010', '51A-00010', 'Đen',       'KIA',          'Sẵn sàng',  'TX007', 'LX003'),
('XE011', '51A-00011', 'Đen',       'Mitsubishi',   'Bảo trì',   'TX009', 'LX002'),
('XE012', '51A-00012', 'Đỏ',        'Hyundai',      'Bảo trì',   'TX010', 'LX001'),
('XE013', '51A-00013', 'Xanh lam',  'Toyota',       'Bảo trì',   'TX001', 'LX001'),
('XE014', '51A-00014', 'Xanh lục',  'KIA',          'Đang chạy', 'TX002', 'LX002'),
('XE015', '51A-00015', 'Xanh lục',  'Hyundai',      'Đang chạy', 'TX003', 'LX003');


-- C4. BẢNG GIÁ
INSERT INTO BANG_GIA (ma_bang_gia, ma_loai, gia_khoi_diem, gia_theo_km, phu_thu) VALUES
('G001', 'LX001', 40000,  4000,  2200),
('G002', 'LX002', 50000,  5000,  1600),
('G003', 'LX003', 70000,  7000,  3500),
('G004', 'LX004', 100000, 10000, 4000);


-- C5. KHÁCH HÀNG
INSERT INTO KHACH_HANG (ma_khach_hang, ten_khach_hang, KH_sdt) VALUES
('KH001', 'Thái Phú An',        '0377057709'),
('KH002', 'Trần Minh Duy',      '0334787760'),
('KH003', 'Mã Quốc Đạt',        '0943672808'),
('KH004', 'Mai Hà Ngọc Hải',    '0793982469'),
('KH005', 'Mạch Gia Hân',       '0911608752'),
('KH006', 'Châu Thế Khanh',     '0327533788'),
('KH007', 'Đào Thị Kim Khánh',  '0901086499'),
('KH008', 'Lý Trí Khải',        '0848041482'),
('KH009', 'Nguyễn Phước Khải',  '0939388541'),
('KH010', 'Nguyễn Nhựt Linh',   '0702860215'),
('KH011', 'Tôn Minh Lộc',       '0379870898'),
('KH012', 'Trầm Tri Min',       '0389282503'),
('KH013', 'Trần Thanh Tài',     '0776558070'),
('KH014', 'Võ Duy Tân',         '0842596099'),
('KH015', 'Đinh Hữu Thành',     '0898134535'),
('KH016', 'Nguyễn Lê Tấn Thành','0907961580'),
('KH017', 'Nguyễn Minh Truyền', '0764142269'),
('KH018', 'Thân Quốc Tuấn',     '0584020990'),
('KH019', 'Dương Đình Văn',     '0886250400'),
('KH020', 'Đoàn Thị Như Ý',     '0848077996');


-- C6. CHUYẾN ĐI
INSERT INTO CHUYEN_DI (ma_chuyen, diem_don, diem_tra, tg_don, tg_tra, so_km_di, cuoc_phi, ma_xe, ma_khach_hang) VALUES
('CD001', 'Bến Ninh Kiều, Cần Thơ', 'KDC Hưng Phú, Cần Thơ', 
    '2024-10-10 10:00:00', '2024-10-10 10:15:00', 5.20, 85000,  'XE001', 'KH001'),
('CD002', 'Đại học Cần Thơ', 'Sân bay Cần Thơ', 
    '2024-11-11 14:30:00', '2024-11-11 15:05:00', 12.8, 210000, 'XE003', 'KH003'),
('CD003', 'Bệnh viện Đa khoa Trung ương Cần Thơ', 'Chợ Nổi Cái Răng, Cần Thơ', 
    '2024-12-12 07:15:00', '2025-12-12 07:40:00', 8.50, 135000, 'XE002', 'KH003'),
('CD004', 'Trung tâm TP. Vĩnh Long', 'Cầu Mỹ Thuận', 
    '2025-01-01 09:00:00', '2025-01-01 09:35:00', 16.0, 280000, 'XE004', 'KH013'),
('CD005', 'Vincom Xuân Khánh, Cần Thơ', 'Bến xe khách Cần Thơ',
    '2025-02-02 11:20:00', '2025-02-02 11:45:00', 7.90, 125000, 'XE005', 'KH002'),
('CD006', 'Cầu Cần Thơ (QL1A)', 'Trung tâm TP. Vĩnh Long', 
    '2025-03-03 17:30:00', '2025-03-03 18:20:00', 38.5, 650000, 'XE008', 'KH002'),
('CD007', 'Phường Cái Khế, Cần Thơ', 'Khu Công nghiệp Trà Nóc',
    '2025-04-04 19:45:00', '2025-04-04 20:25:00', 14.1, 225000, 'XE001', 'KH010'),
('CD008', 'Siêu thịCần Thơ', 'Đường 30/4, Cần Thơ',
    '2025-05-05 08:00:00', '2025-05-05 08:18:00', 6.50, 105000, 'XE010', 'KH002'),
('CD009', 'Huyện Trà Ôn, Vĩnh Long', 'TP. Vĩnh Long', 
    '2025-06-06 13:00:00', '2025-06-06 13:40:00', 25.0, 400000, 'XE002', 'KH013'),
('CD010', 'Khu Đô thị Stella Mega City', 'Chợ An Lạc, Cần Thơ', 
    '2025-07-07 16:30:00', '2025-07-07 16:55:00', 9.10, 145000, 'XE004', 'KH012'),
('CD011', 'Phường 3, TP. Vĩnh Long', 'Phường 4, TP. Vĩnh Long',
    '2025-08-08 21:00:00', '2025-08-08 21:10:00', 3.50, 65000,  'XE005', 'KH018'),
('CD012', 'Cầu Rạch Súc, Ô Môn, Cần Thơ', 'QL91, Thốt Nốt, Cần Thơ', 
    '2025-09-09 06:45:00', '2025-09-09 07:30:00', 21.3, 340000, 'XE006', 'KH010'),
('CD013', 'Bến xe Vĩnh Long', 'Công viên Sông Hậu, Cần Thơ', 
    '2025-10-10 12:00:00', '2025-10-10 13:10:00', 45.0, 750000, 'XE006', 'KH016'),
('CD014', 'Huyện Tam Bình, Vĩnh Long', 'Huyện Long Hồ, Vĩnh Long', 
    '2025-11-11 15:30:00', '2025-11-11 16:15:00', 18.2, 290000, 'XE009', 'KH007'),
('CD015', 'Lotte Mart Cần Thơ', 'Khách sạn Mường Thanh, Cần Thơ',
    '2025-12-12 19:00:00', '2025-12-12 19:20:00', 6.80, 110000, 'XE008', 'KH019');


-- C7. BẢO TRÌ XE
INSERT INTO BAO_TRI_XE (ma_bao_tri, ma_xe, ngay_bao_tri, loai_bao_tri, chi_phi, mo_ta) VALUES
('BT001', 'XE001', '2024-01-01', 'Kiểm tra phanh',      110000,     'Phanh mòn'),
('BT002', 'XE004', '2024-04-01', 'Thay ác quy',         1500000,    'Ắc quy yếu'),
('BT003', 'XE004', '2024-06-09', 'Bảo dưỡng định kỳ',   500000,     'Kiểm tra tổng thể'),
('BT004', 'XE005', '2024-12-30', 'Thay nhớt',           350000,     'Bảo dưỡng định kỳ'),
('BT005', 'XE007', '2025-11-04', 'Thay bugi',           300000,     'Đề khó'),
('BT006', 'XE001', '2025-04-12', 'Vệ sinh nội thất',    400000,     'Làm sạch toàn bộ nội thất'),
('BT007', 'XE010', '2025-05-10', 'Thay nhớt',           350000,     'Bảo dưỡng định kỳ'),
('BT008', 'XE011', '2025-11-04', 'Vệ sinh nội thất',    400000,     'Làm sạch toàn bộ nội thất'),
('BT009', 'XE012', '2025-11-05', 'Thay nhớt',           350000,     'Bảo dưỡng định kỳ'),
('BT010', 'XE013', '2025-10-04', 'Sửa máy lạnh',        1200000,    'Không mát');

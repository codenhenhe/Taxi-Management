// src/component/DriverManagement.jsx (Đã sửa cho Bootstrap)
import React, { useState } from 'react';
import { Car, User, Trash2, Edit2, PlusCircle, RotateCcw } from 'lucide-react';
import { mockDrivers } from '../data/mockTaxiData'; 

const initialDriverForm = {
    ma_taxi: '', ten_tai_xe: '', so_hieu_GPLX: '', ngay_sinh: '', sdt: '', trang_thai: 'Rảnh',
};

const DriverManagement = () => {
    const [drivers, setDrivers] = useState(mockDrivers);
    const [formData, setFormData] = useState(initialDriverForm);
    const isEditing = !!formData.ma_taxi;
    const isEditing2 = !!formData.ma_taxi;
    // ... (Giữ nguyên các hàm handle... của bạn) ...
    const handleFormChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };
     // ... (Giữ nguyên các hàm handle... của bạn) ...
    const handleFormSearch = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };
    const handleSearch = () => {
        let filteredDrivers = mockDrivers;
        if (formData.ma_tai_xe) {
            filteredDrivers = filteredDrivers.filter(d => d.ma_taxi.includes(formData.ma_tai_xe));
        }
        if (formData.ten_tai_xe) {
            filteredDrivers = filteredDrivers.filter(d => d.ten_tai_xe.toLowerCase().includes(formData.ten_tai_xe.toLowerCase()));
        }
        if (formData.TX_sdt) {
            filteredDrivers = filteredDrivers.filter(d => d.sdt.includes(formData.TX_sdt));
        }
        if (formData.GPLX) {
            filteredDrivers = filteredDrivers.filter(d => d.so_hieu_GPLX.includes(formData.GPLX));
        }
        if (formData.trang_thai) {
            filteredDrivers = filteredDrivers.filter(d => d.trang_thai === formData.trang_thai);
        }
            setDrivers(filteredDrivers);

    }
    const handleSelectDriver = (driver) => {
        setFormData({ ...driver, ngay_sinh: driver.ngay_sinh.split('T')[0] });
    };
    const handleResetForm = () => setFormData(initialDriverForm);
    const handleAddOrUpdate = () => {
        if (!formData.ma_taxi || !formData.ten_tai_xe) {
            alert('Vui lòng nhập Mã và Tên Tài xế.'); return;
        }
        setDrivers(prev => {
            const existingIndex = prev.findIndex(d => d.ma_taxi === formData.ma_taxi);
            if (existingIndex >= 0) {
                const updated = [...prev];
                updated[existingIndex] = formData;
                return updated;
            }
            return [...prev, formData];
        });
        handleResetForm();
    };
    const handleDelete = () => {
        if (window.confirm(`Xác nhận xóa Tài xế ${formData.ten_tai_xe}?`)) {
            setDrivers(prev => prev.filter(d => d.ma_taxi !== formData.ma_taxi));
            handleResetForm();
            alert('Xóa Tài xế thành công!');
        }
    };

    return (
        // d-flex và gap-3 thay cho flex và gap-6
        <div className="d-flex gap-3">
            {/* Cột 1: Danh sách - Dùng Card và flex-grow-1 */}
            <div className="card shadow-sm flex-grow-1">

                {/* nửa trên */}
                 <div className="card shadow-sm flex-shrink-0 mh-50  mb-2" >
                        <div className="card-body p-4">
                            <h3 className="h5 fw-bold text-success mb-3 border-bottom pb-2 d-flex align-items-center gap-2">
                                <Edit2 className="w-5 h-5" /> TÌM KIẾM TÀI XẾ
                            </h3>
                            <form className="d-flex flex-wrap gap-3 ">
                                <div className="mb-2 ">
                                    <label className="form-label fw-medium small">MÃ TÀI XẾ</label>
                                    <input   type="text" name="ma_tai_xe" value={formData.ma_tai_xe} onChange={handleFormSearch} disabled={isEditing2} className="form-control" />
                                </div>
                                <div className="mb-2 ">
                                    <label className="form-label fw-medium small">TÊN TÀI XẾ</label>
                                    <input  type="text" name="ten_tai_xe" value={formData.ten_tai_xe} onChange={handleFormSearch} className="form-control" />
                                </div>
                                <div className="mb-2 ">
                                    <label className="form-label fw-medium small">SỐ ĐIỆN THOẠI</label>
                                    <input  type="tel" name="TX_sdt" value={formData.TX_sdt} onChange={handleFormSearch} className="form-control" />
                                </div>
                                <div className="mb-2 ">
                                    <label className="form-label fw-medium small">GPLX</label>
                                    <input  type="tel" name="GPLX" value={formData.GPLX} onChange={handleFormSearch} className="form-control" />
                                </div>
                                
                                <div className="mb-2 ">
                                    <label className="form-label fw-medium small">TRẠNG THÁI</label>
                                    <select name="trang_thai" value={formData.trang_thai} onChange={handleFormSearch} className="form-select">
                                        <option value="">Chọn trạng thái</option>
                                        <option value="Rảnh">Rảnh</option>
                                        <option value="Nghỉ">Nghỉ</option>
                                        <option value="Bận">Bận</option>
                                    </select>
                                </div>  
                                <div className="d-flex gap-2 pt-2">
                                        <button 
                                        type="button"
                                        onClick={handleSearch} 
                                        className={`btn ${isEditing2 ? 'btn-warning' : 'btn-success'} d-flex align-items-center justify-content-center gap-1`}
                                    >
                                        {isEditing2 ? <Edit2 size={16} /> : <PlusCircle size={16} />} {  'Tìm kiếm'}
                                    </button>
                                    <button 
                                        type="button"
                                        onClick={handleResetForm} 
                                        disabled={!isEditing2} 
                                        className="btn btn-danger d-flex align-items-center justify-content-center gap-1"
                                    >
                                        <Trash2 size={16} /> reset
                                    </button>
                               
                                </div>
                            </form>
                        </div>
                    </div>
                
                      {/*Nửa dưới  */}
                      <div>
                    <div className="card-header bg-white pb-0 border-bottom-0">
                    <h2 className="h5 fw-bold text-primary mb-3 d-flex align-items-center gap-2">
                        <User className="w-6 h-6" /> DANH SÁCH TÀI XẾ
                    </h2>
                </div>
                <div className="card-body pt-0">
                    <div className="overflow-auto border rounded-3" style={{ maxHeight: '600px' }}>
                        {/* Dùng table, table-hover, table-striped của Bootstrap */}
                        <table className="table table-hover table-striped mb-0">
                            <thead className="table-primary sticky-top">
                                <tr>
                                    {['Mã', 'Tên', 'SĐT', 'GPLX', 'Ngày Sinh', 'Trạng Thái'].map(header => (
                                        <th key={header} scope="col" className="py-2 px-3">{header}</th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody>
                                {drivers.map((d) => (
                                    <tr 
                                        key={d.ma_taxi} 
                                        onClick={() => handleSelectDriver(d)}
                                        style={{ cursor: 'pointer' }}
                                    >
                                        <td className="py-2 px-3 fw-medium">{d.ma_taxi}</td>
                                        <td className="py-2 px-3">{d.ten_tai_xe}</td>
                                        <td className="py-2 px-3">{d.sdt}</td>
                                        <td className="py-2 px-3">{d.so_hieu_GPLX}</td>
                                        <td className="py-2 px-3">{d.ngay_sinh}</td>
                                        <td className={`py-2 px-3 fw-semibold ${
                                            d.trang_thai === 'Rảnh' ? 'text-success' : 'text-danger'
                                        }`}>{d.trang_thai}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
                          
                    </div>    
            
                   
            </div>

            {/* Cột 2: Form - Dùng Card và style cho width */}
            <div className="card shadow-sm flex-shrink-0" style={{ width: '24rem' }}>
                <div className="card-body p-4">
                    <h3 className="h5 fw-bold text-primary mb-3 border-bottom pb-2 d-flex align-items-center gap-2">
                        <Edit2 className="w-5 h-5" /> THÔNG TIN TÀI XẾ
                    </h3>
                    
                    <form className="d-flex  gap-3 flex-column ">
                        {/* Dùng form-label, form-control, form-select */}
                        <div className="mb-2">
                            <label className="form-label fw-medium small">MÃ TÀI XẾ</label>
                            <input type="text" name="ma_taxi" value={formData.ma_taxi} onChange={handleFormChange}  className="form-control" />
                        </div>
                        <div className="mb-2">
                            <label className="form-label fw-medium small">TÊN TÀI XẾ</label>
                            <input type="text"  name="ten_tai_xe" value={formData.ten_tai_xe} onChange={handleFormChange} className="form-control" />
                        </div>
                        <div className="mb-2">
                            <label className="form-label fw-medium small">SỐ ĐIỆN THOẠI</label>
                            <input type="tel"  name="sdt" value={formData.sdt} onChange={handleFormChange} className="form-control" />
                        </div>
                         <div className="mb-2">
                            <label className="form-label fw-medium small">SỐ HIỆU GPLX</label>
                            <input type="text" name="so_hieu_GPLX" value={formData.so_hieu_GPLX} onChange={handleFormChange} className="form-control" />
                        </div>
                        <div className="mb-2">
                            <label className="form-label fw-medium small">NGÀY SINH</label>
                            <input type="date" name="ngay_sinh" value={formData.ngay_sinh} onChange={handleFormChange} className="form-control" />
                        </div>
                        <div className="mb-2">
                            <label className="form-label fw-medium small">TRẠNG THÁI</label>
                            <select name="trang_thai" value={formData.trang_thai} onChange={handleFormChange} className="form-select">
                                <option value="Ranh">Ranh</option>
                                <option value="Bận">Bận</option>
                                <option value="Nghỉ">Nghỉ</option>
                            </select>
                        </div>
                        
                        {/* Dùng btn, btn-*, d-grid, gap-2 */}
                        <div className="d-grid gap-2 pt-2">
                            <button 
                                type="button"
                                onClick={handleAddOrUpdate} 
                                className={`btn ${isEditing ? 'btn-warning' : 'btn-success'} d-flex align-items-center justify-content-center gap-1`}
                            >
                                {isEditing ? <Edit2 size={16} /> : <PlusCircle size={16} />} {isEditing ? 'SỬA' : 'THÊM'}
                            </button>
                            <button 
                                type="button"
                                onClick={handleDelete} 
                                disabled={!isEditing} 
                                className="btn btn-danger d-flex align-items-center justify-content-center gap-1"
                            >
                                <Trash2 size={16} /> XÓA
                            </button>
                            <button 
                                type="button"
                                onClick={handleResetForm} 
                                className="btn btn-secondary d-flex align-items-center justify-content-center gap-1"
                            >
                                <RotateCcw size={16} /> RESET
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default DriverManagement;
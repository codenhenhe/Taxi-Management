// src/component/TripManagement.jsx (Đã sửa cho Bootstrap)
import React, { useState } from 'react';
import { Route, MapPin, DollarSign, Trash2, Edit2, PlusCircle, RotateCcw } from 'lucide-react';
import { mockTrips, mockDrivers, mockCustomers } from '../data/mockTaxiData'; 

const initialTripForm = {
    ma_chuyen: '', ma_xe: '', ma_khach_hang: '', diem_don: '', diem_tra: '', TG_nhan: '', TG_tra: '', so_KM_di: 0, cuoc_phi: 0,
};

const formatCurrency = (amount) => new Intl.NumberFormat('vi-VN').format(amount);

const TripManagement = () => {
    const [trips, setTrips] = useState(mockTrips);
    const [formData, setFormData] = useState(initialTripForm);
    const isEditing = !!formData.ma_chuyen;
    const handleFormSearch = (e) => {
                const { name, value } = e.target;
                setFormData(prev => ({ ...prev, [name]: value }));
            };
    const handleSearch = () => {
        let filteredTrips = mockTrips;
        if (formData.ma_khach_hang) {
            filteredTrips = filteredTrips.filter(d => d.ma_khach_hang.includes(formData.ma_khach_hang));
        }
        if (formData.ten_khach_hang) {
            filteredTrips = filteredTrips.filter(d => d.ten_khach_hang.toLowerCase().includes(formData.ten_khach_hang.toLowerCase()));
        }
        if (formData.KH_sdt) {
            filteredTrips = filteredTrips.filter(d => d.KH_sdt.includes(formData.KH_sdt));
        }
        setTrips(filteredTrips);

        }
    // ... (Giữ nguyên các hàm handle... của bạn) ...
    const handleFormChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: ['so_KM_di', 'cuoc_phi'].includes(name) ? Number(value) : value }));
    };
    const handleSelectTrip = (trip) => {
        const formatDateTime = (isoDate) => isoDate ? new Date(isoDate).toISOString().slice(0, 16) : '';
        setFormData({ ...trip, TG_nhan: formatDateTime(trip.TG_nhan), TG_tra: formatDateTime(trip.TG_tra) });
    };
    const handleResetForm = () => setFormData(initialTripForm);
    const handleAddOrUpdate = () => {
        if (!formData.ma_chuyen || !formData.diem_don || !formData.diem_tra) {
            alert('Vui lòng nhập Mã chuyến, Điểm đón và Điểm trả.'); return;
        }
        const dataToSave = { ...formData, TG_nhan: formData.TG_nhan ? new Date(formData.TG_nhan).toISOString() : '', TG_tra: formData.TG_tra ? new Date(formData.TG_tra).toISOString() : '' };
        if (isEditing) {
            setTrips(prev => prev.map(t => t.ma_chuyen === dataToSave.ma_chuyen ? dataToSave : t));
            alert('Cập nhật Chuyến đi thành công!');
        } else {
            if (trips.find(t => t.ma_chuyen === dataToSave.ma_chuyen)) {
                alert('Mã Chuyến đi đã tồn tại!'); return;
            }
            setTrips(prev => [...prev, dataToSave]);
            alert('Thêm Chuyến đi thành công!');
        }
        handleResetForm();
    };
    const handleDelete = () => {
        if (window.confirm(`Xác nhận xóa Chuyến đi ${formData.ma_chuyen}?`)) {
            setTrips(prev => prev.filter(t => t.ma_chuyen !== formData.ma_chuyen));
            handleResetForm();
            alert('Xóa Chuyến đi thành công!');
        }
    };

    return (
        <div className="d-flex gap-3">
            {/* Cột 1: Danh sách */}
            <div className="card shadow-sm flex-grow-1">
                 {/* nửa trên */}
                <div className="card shadow-sm flex-shrink-0 mh-50  mb-2" >
                    <div className="card-body p-4">
                        <h3 className="h5 fw-bold text-success mb-3 border-bottom pb-2 d-flex align-items-center gap-2">
                            <Edit2 className="w-5 h-5" /> TÌM KIẾM TÀI XẾ
                        </h3>
                        <form className="d-flex flex-wrap gap-3 ">
                            <div className="mb-2 ">
                                <label className="form-label fw-medium small">MÃ CHUYẾN</label>
                                <input   type="text" name="ma_chuyen" value={formData.ma_chuyen} onChange={handleFormSearch}  className="form-control" />
                            </div>
                            <div className="mb-2 ">
                                <label className="form-label fw-medium small">MÃ KHÁCH HÀNG</label>
                                <input  type="text" name="ma_khach_hang" value={formData.ma_khach_hang} onChange={handleFormSearch} className="form-control" />
                            </div>
                            <div className="mb-2 ">
                                <label className="form-label fw-medium small">MÃ XE</label>
                                <input  type="tel" name="ma_xe" value={formData.ma_xe} onChange={handleFormSearch} className="form-control" />
                            </div>
                         
                            
                            <div className="d-flex gap-2 pt-2">
                                    <button 
                                    type="button"
                                    onClick={handleSearch} 
                                    className={`btn ${isEditing ? 'btn-warning' : 'btn-success'} d-flex align-items-center justify-content-center gap-1`}
                                >
                                    {isEditing ? <Edit2 size={16} /> : <PlusCircle size={16} />} {  'Tìm kiếm'}
                                </button>
                                <button 
                                    type="button"
                                    onClick={handleResetForm} 
                                    disabled={!isEditing} 
                                    className="btn btn-danger d-flex align-items-center justify-content-center gap-1"
                                >
                                    <Trash2 size={16} /> reset
                                </button>
                            
                            </div>
                        </form>
                    </div>
                </div>
                {/* Nửa dưới */}
                <div className="card-header bg-white pb-0 border-bottom-0">
                    <h2 className="h5 fw-bold text-info mb-3 d-flex align-items-center gap-2">
                        <Route className="w-6 h-6" /> DANH SÁCH CHUYẾN ĐI
                    </h2>
                </div>
                 <div className="card-body pt-0">
                    <div className="overflow-auto border rounded-3" style={{ maxHeight: '600px' }}>
                        <table className="table table-hover table-striped mb-0">
                            <thead className="table-info sticky-top">
                                <tr>
                                    {['Mã Chuyến', 'Mã KH', 'Mã Xe', 'Điểm Đón', 'Điểm Trả', 'KM', 'Cước Phí'].map(header => (
                                        <th key={header} scope="col" className="py-2 px-3">{header}</th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody>
                                {trips.map((t) => (
                                    <tr 
                                        key={t.ma_chuyen} 
                                        onClick={() => handleSelectTrip(t)}
                                        style={{ cursor: 'pointer' }}
                                    >
                                        <td className="py-2 px-3 fw-medium">{t.ma_chuyen}</td>
                                        <td className="py-2 px-3">{t.ma_khach_hang}</td>
                                        <td className="py-2 px-3">{t.ma_xe}</td>
                                        <td className="py-2 px-3 text-truncate" style={{maxWidth: '150px'}}>{t.diem_don}</td>
                                        <td className="py-2 px-3 text-truncate" style={{maxWidth: '150px'}}>{t.diem_tra}</td>
                                        <td className="py-2 px-3 text-end">{t.so_KM_di}</td>
                                        <td className="py-2 px-3 fw-semibold text-danger text-end">{formatCurrency(t.cuoc_phi)}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            {/* Cột 2: Form */}
            <div className="card shadow-sm flex-shrink-0" style={{ width: '24rem' }}>
                <div className="card-body p-4">
                    <h3 className="h5 fw-bold text-info mb-3 border-bottom pb-2 d-flex align-items-center gap-2">
                        <Edit2 className="w-5 h-5" /> THÔNG TIN CHUYẾN ĐI
                    </h3>
                    
                    <form className="d-flex flex-column gap-3">
                        <div className="mb-2">
                            <label className="form-label fw-medium small">MÃ CHUYẾN</label>
                            <input type="text" name="ma_chuyen" value={formData.ma_chuyen} onChange={handleFormChange} disabled={isEditing} className="form-control" />
                        </div>
                        
                        <div className="mb-2">
                            <label className="form-label fw-medium small">MÃ KHÁCH HÀNG</label>
                            <select name="ma_khach_hang" value={formData.ma_khach_hang} onChange={handleFormChange} className="form-select">
                                <option value="">--Chọn KH--</option>
                                {mockCustomers.map(c => <option key={c.ma_khach_hang} value={c.ma_khach_hang}>{c.ma_khach_hang} - {c.ten_khach_hang}</option>)}
                            </select>
                        </div>
                        
                        <div className="mb-2">
                            <label className="form-label fw-medium small">MÃ XE (Tài xế)</label>
                            <select name="ma_xe" value={formData.ma_xe} onChange={handleFormChange} className="form-select">
                                <option value="">--Chọn Xe--</option>
                                {mockDrivers.map(d => <option key={d.ma_taxi} value={d.ma_xe || d.ma_taxi}>{d.ma_taxi} - {d.ten_tai_xe}</option>)} 
                            </select>
                        </div>

                        <div className="row g-2">
                            <div className="col-md-6 mb-2">
                                <label className="form-label fw-medium small">ĐIỂM ĐÓN</label>
                                <input type="text" name="diem_don" value={formData.diem_don} onChange={handleFormChange} className="form-control" />
                            </div>
                            <div className="col-md-6 mb-2">
                                <label className="form-label fw-medium small">ĐIỂM TRẢ</label>
                                <input type="text" name="diem_tra" value={formData.diem_tra} onChange={handleFormChange} className="form-control" />
                            </div>
                        </div>

                        <div className="row g-2">
                            <div className="col-md-6 mb-2">
                                <label className="form-label fw-medium small">TG NHẬN</label>
                                <input type="datetime-local" name="TG_nhan" value={formData.TG_nhan} onChange={handleFormChange} className="form-control" />
                            </div>
                            <div className="col-md-6 mb-2">
                                <label className="form-label fw-medium small">TG TRẢ</label>
                                <input type="datetime-local" name="TG_tra" value={formData.TG_tra} onChange={handleFormChange} className="form-control" />
                            </div>
                        </div>

                        <div className="row g-2">
                            <div className="col-md-6 mb-2">
                                <label className="form-label fw-medium small">SỐ KM</label>
                                <input type="number" name="so_KM_di" value={formData.so_KM_di} onChange={handleFormChange} className="form-control" />
                            </div>
                             <div className="col-md-6 mb-2">
                                <label className="form-label fw-medium small">CƯỚC PHÍ</label>
                                <input type="number" name="cuoc_phi" value={formData.cuoc_phi} onChange={handleFormChange} className="form-control" />
                            </div>
                        </div>

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

export default TripManagement;
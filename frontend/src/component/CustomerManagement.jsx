// src/component/CustomerManagement.jsx (Đã sửa cho Bootstrap)
import React, { useState } from 'react';
import { User, Trash2, Edit2, PlusCircle, RotateCcw } from 'lucide-react';
import { mockCustomers } from '../data/mockTaxiData'; 

const initialCustomerForm = {
    ma_khach_hang: '', ten_khach_hang: '', KH_sdt: '',
};

const CustomerManagement = () => {
    const [customers, setCustomers] = useState(mockCustomers);
    const [formData, setFormData] = useState(initialCustomerForm);
    const isEditing = !!formData.ma_khach_hang;

    // ... (Giữ nguyên các hàm handle... của bạn) ...
    const handleFormChange = (e) => setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
    const handleSelectCustomer = (customer) => setFormData(customer);
    const handleFormSearch = (e) => {
            const { name, value } = e.target;
            setFormData(prev => ({ ...prev, [name]: value }));
        };
    const handleSearch = () => {
        let filteredDrivers = mockCustomers;
        if (formData.ma_khach_hang) {
            filteredDrivers = filteredDrivers.filter(d => d.ma_khach_hang.includes(formData.ma_khach_hang));
        }
        if (formData.ten_khach_hang) {
            filteredDrivers = filteredDrivers.filter(d => d.ten_khach_hang.toLowerCase().includes(formData.ten_khach_hang.toLowerCase()));
        }
        if (formData.KH_sdt) {
            filteredDrivers = filteredDrivers.filter(d => d.KH_sdt.includes(formData.KH_sdt));
        }
        setCustomers(filteredDrivers);

        }
    const handleResetForm = () => setFormData(initialCustomerForm);
    const handleAddOrUpdate = () => {
        if (!formData.ma_khach_hang || !formData.ten_khach_hang) {
            alert('Vui lòng nhập Mã và Tên Khách hàng.'); return;
        }
        if (isEditing) {
            setCustomers(prev => prev.map(c => c.ma_khach_hang === formData.ma_khach_hang ? formData : c));
            alert('Cập nhật Khách hàng thành công!');
        } else {
            if (customers.find(c => c.ma_khach_hang === formData.ma_khach_hang)) {
                alert('Mã Khách hàng đã tồn tại!'); return;
            }
            setCustomers(prev => [...prev, formData]);
            alert('Thêm Khách hàng thành công!');
        }
        handleResetForm();
    };
    const handleDelete = () => {
        if (window.confirm(`Xác nhận xóa Khách hàng ${formData.ten_khach_hang}?`)) {
            setCustomers(prev => prev.filter(c => c.ma_khach_hang !== formData.ma_khach_hang));
            handleResetForm();
            alert('Xóa Khách hàng thành công!');
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
                            <Edit2 className="w-5 h-5" /> TÌM KIẾM KHÁCH HÀNG
                        </h3>
                        <form className="d-flex flex-wrap gap-3 ">
                            <div className="mb-2 ">
                                <label className="form-label fw-medium small">MÃ KHÁCH HÀNG</label>
                                <input   type="text" name="ma_khach_hang" value={formData.ma_khach_hang} onChange={handleFormSearch}  className="form-control" />
                            </div>
                            <div className="mb-2 ">
                                <label className="form-label fw-medium small">TÊN KHÁCH HÀNG</label>
                                <input  type="text" name="ten_khach_hang" value={formData.ten_khach_hang} onChange={handleFormSearch} className="form-control" />
                            </div>
                            <div className="mb-2 ">
                                <label className="form-label fw-medium small">SỐ ĐIỆN THOẠI</label>
                                <input  type="tel" name="KH_sdt" value={formData.KH_sdt} onChange={handleFormSearch} className="form-control" />
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
                    <h2 className="h5 fw-bold text-success mb-3 d-flex align-items-center gap-2">
                        <User className="w-6 h-6" /> DANH SÁCH KHÁCH HÀNG
                    </h2>
                </div>
                <div className="card-body pt-0">
                    <div className="overflow-auto border rounded-3" style={{ maxHeight: '600px' }}>
                        <table className="table table-hover table-striped mb-0">
                            <thead className="table-success sticky-top">
                                <tr>
                                    {['Mã KH', 'Tên Khách Hàng', 'SĐT'].map(header => (
                                        <th key={header} scope="col" className="py-2 px-3">{header}</th>
                                    ))}
                                </tr>
                            </thead>
                            <tbody>
                                {customers.map((c) => (
                                    <tr 
                                        key={c.ma_khach_hang} 
                                        onClick={() => handleSelectCustomer(c)}
                                        style={{ cursor: 'pointer' }}
                                    >
                                        <td className="py-2 px-3 fw-medium">{c.ma_khach_hang}</td>
                                        <td className="py-2 px-3">{c.ten_khach_hang}</td>
                                        <td className="py-2 px-3">{c.KH_sdt}</td>
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
                    <h3 className="h5 fw-bold text-success mb-3 border-bottom pb-2 d-flex align-items-center gap-2">
                        <Edit2 className="w-5 h-5" /> THÔNG TIN KHÁCH HÀNG
                    </h3>
                    <form className="d-flex flex-column gap-3">
                        <div className="mb-2">
                            <label className="form-label fw-medium small">MÃ KHÁCH HÀNG</label>
                            <input type="text" name="ma_khach_hang" value={formData.ma_khach_hang} onChange={handleFormChange} disabled={isEditing} className="form-control" />
                        </div>
                        <div className="mb-2">
                            <label className="form-label fw-medium small">TÊN KHÁCH HÀNG</label>
                            <input type="text" name="ten_khach_hang" value={formData.ten_khach_hang} onChange={handleFormChange} className="form-control" />
                        </div>
                        <div className="mb-2">
                            <label className="form-label fw-medium small">SỐ ĐIỆN THOẠI</label>
                            <input type="tel" name="KH_sdt" value={formData.KH_sdt} onChange={handleFormChange} className="form-control" />
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

export default CustomerManagement;
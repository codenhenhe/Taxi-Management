package com.project.backend.controller;

import com.project.backend.dto.XeDTO; // <-- Import
import com.project.backend.dto.XeRequestDTO; // <-- Import
import com.project.backend.service.XeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/xe") // (Mình đoán URL, bạn tự sửa nếu cần)
public class XeController {

    @Autowired
    private XeService xeService;

    // GET ALL: Trả về List<XeDTO>
    @GetMapping
    public ResponseEntity<List<XeDTO>> getAllXe() {
        List<XeDTO> dsXe = xeService.getAllXe();
        return ResponseEntity.ok(dsXe);
    }

    // GET BY ID: Trả về XeDTO
    @GetMapping("/{id}")
    public ResponseEntity<XeDTO> getXeById(@PathVariable String id) {
        XeDTO xe = xeService.getXeById(id);
        return ResponseEntity.ok(xe);
    }

    // POST: Nhận XeRequestDTO, Trả về XeDTO
    @PostMapping
    public ResponseEntity<XeDTO> createXe(@RequestBody XeRequestDTO dto) {
        XeDTO xeMoi = xeService.createXe(dto);
        return ResponseEntity.ok(xeMoi);
    }

    // PUT: Nhận id + XeRequestDTO, Trả về XeDTO
    @PutMapping("/{id}")
    public ResponseEntity<XeDTO> updateXe(@PathVariable String id, @RequestBody XeRequestDTO dto) {
        XeDTO xeCapNhat = xeService.updateXe(id, dto);
        return ResponseEntity.ok(xeCapNhat);
    }

    // DELETE: Chỉ nhận id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteXe(@PathVariable String id) {
        xeService.deleteXe(id);
        return ResponseEntity.noContent().build();
    }
}
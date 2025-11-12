package com.project.backend.controller;

import com.project.backend.model.Xe;
import com.project.backend.service.XeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/xe")
// @CrossOrigin(origins = "http://localhost:5173")
public class XeController {

    @Autowired
    private XeService xeService;

    // URL: GET http://localhost:8080/api/xe
    @GetMapping
    public ResponseEntity<List<Xe>> layTatCaXe() {
        List<Xe> dsXe = xeService.getAllXe();
        return ResponseEntity.ok(dsXe);
    }

    // URL: GET http://localhost:8080/api/xe/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Xe> layXeTheoId(@PathVariable String id) {
        Xe xe = xeService.getXeById(id);
        return ResponseEntity.ok(xe);
    }

    // URL: POST http://localhost:8080/api/xe?maLoai=LX001
    @PostMapping
    public ResponseEntity<Xe> taoMoiXe(
            @RequestBody Xe xeMoi,
            @RequestParam String maLoai) {
        // Frontend chỉ cần gửi JSON: {"bienSoXe": "51A-12345", "mauXe": "Đen", ...}
        Xe xeDaTao = xeService.createXe(xeMoi, maLoai);
        return ResponseEntity.ok(xeDaTao);
    }

    // URL: PUT http://localhost:8080/api/xe/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Xe> capNhatXeTheoId(@PathVariable String id,
            @RequestBody Xe xeDetails) {

        Xe xeCapNhat = xeService.updateXe(id, xeDetails);
        return ResponseEntity.ok(xeCapNhat);
    }

    // URL: DELETE http://localhost:8080/api/xe/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> xoaXeTheoId(@PathVariable String id) {
        xeService.deleteXe(id);
        return ResponseEntity.noContent().build();
    }
}
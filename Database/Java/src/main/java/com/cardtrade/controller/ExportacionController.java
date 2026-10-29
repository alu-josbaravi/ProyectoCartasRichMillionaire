package com.cardtrade.controller;
import com.cardtrade.service.ExportacionService;import org.springframework.http.*;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/exportacion")
public class ExportacionController {private final ExportacionService s;public ExportacionController(ExportacionService s){this.s=s;}
 @GetMapping(value="/inserts",produces="application/sql") public ResponseEntity<String> exportar(){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=inserts.sql").body(s.exportar());}}

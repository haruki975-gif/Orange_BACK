package com.kh.dotogether.log.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.dotogether.log.model.dto.LogDTO;
import com.kh.dotogether.log.model.service.LogService;

import lombok.RequiredArgsConstructor;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/log")
@RequiredArgsConstructor
public class LogController {
	
	private final LogService logService;
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> getAllLogs(
			@RequestParam(defaultValue = "1", name="page") int page,
			@RequestParam(defaultValue = "5", name="size") int size) {
		List<LogDTO> logs = logService.findAll(page, size);
	    int total = logService.countAll();

	    Map<String, Object> result = new HashMap();
	    result.put("logs", logs);
	    result.put("total", total);

	    return ResponseEntity.ok(result);
	}
	
}

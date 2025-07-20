package com.kh.dotogether.log.model.service;

import java.util.List;

import com.kh.dotogether.log.model.dto.LogDTO;

public interface LogService {
	
	List<LogDTO> findAll(int page, int size);
    int countAll();
    void insertLog(String logUserId, String logUserName, String logValue);
	
}
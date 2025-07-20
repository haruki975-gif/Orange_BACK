package com.kh.dotogether.log.model.service;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import com.kh.dotogether.log.model.dao.LogMapper;
import com.kh.dotogether.log.model.dto.LogDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
	
	private final LogMapper logMapper;

	@Override
	public List<LogDTO> findAll(int page, int size) {
		RowBounds rowBounds = new RowBounds((page - 1) * size, size);
		return logMapper.findAll(rowBounds);
	}

	@Override
	public int countAll() {
		return logMapper.countAll();
	}
	
	@Override
	public void insertLog(String logUserId, String logUserName, String logValue) {
		try {
	    	LogDTO log = new LogDTO();
	    	log.setLogUserId(logUserId);
	    	log.setLogUserName(logUserName);
	    	log.setLogValue(logValue);
	    	
	    	logMapper.insertLog(log);
		} catch (Exception e) {
			System.out.println("(!ERROR!)로그 저장 실패: " + e.getMessage());
	        e.printStackTrace();
		}
	}

	
}

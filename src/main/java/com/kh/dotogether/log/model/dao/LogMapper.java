package com.kh.dotogether.log.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.dotogether.log.model.dto.LogDTO;

@Mapper
public interface LogMapper {
	
	List<LogDTO> findAll(RowBounds rowBounds);
	int countAll();
	void insertLog(LogDTO log);
}
package com.kh.dotogether.section.model.service;

import java.util.List;

import com.kh.dotogether.section.model.dto.SectionDTO;


public interface SectionService {

	SectionDTO setSection(SectionDTO sectionDTO);
	boolean existsByTitle(String sectionTitle, Long userNo);
	List<SectionDTO> findAllSectionsWithSchedules(Long userNo);
	SectionDTO findSectionWithSchedules(Long sectionNo, Long userNo);
	void updateSectionTitle(SectionDTO sectionDTO);
	boolean existsBaseSection(Long userNo);
	void deleteSection(Long sectionNo, Long userNo);
	SectionDTO findSectionByNo(Long newSectionNo, Long userNo);
}





	
	


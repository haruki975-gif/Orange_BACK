package com.kh.dotogether.section.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.dotogether.schedule.model.dto.ScheduleDTO;
import com.kh.dotogether.section.model.dto.SectionDTO;

@Mapper
public interface SectionMapper {

	int existsByTitle(@Param("sectionTitle") String sectionTitle, 
					  @Param("userNo") Long userNo);
	
	int countBaseSection(Long userNo);

	void insertSection(SectionDTO sectionDTO);

	void updateSectionTitle(@Param("sectionTitle") String sectionTitle, 
							@Param("userNo") Long userNo, 
							@Param("sectionNo") Long sectionNo);

	int findBySection(Long userNo);

    SectionDTO findLastestSection(@Param("sectionNo") Long sectionNo, @Param("userNo") Long userNo);

	void updateBaseSection(SectionDTO oldSection);

	void deleteSection(Long sectionNo);

	void moveSchedulesToSection(@Param("newSectionNo") Long newSectionNo, 
								@Param("sectionNo") Long sectionNo, 
								@Param("userNo") Long userNo);

	SectionDTO findSectionByNo(@Param("sectionNo") Long sectionNo, @Param("userNo") Long userNo);

	List<ScheduleDTO> findSchedulesBySectionNo(Long sectionNo);

	List<SectionDTO> findAllSectionWithSchedule(Long userNo);

	SectionDTO findSectionWithSchedules(@Param("sectionNo") Long sectionNo, @Param("userNo") Long userNo);

	Long selectBaseSectionNoByUserNo(Long userNo);


	
	

}

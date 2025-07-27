package com.kh.dotogether.work.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import com.kh.dotogether.work.model.dto.WorkDTO;
import com.kh.dotogether.work.model.vo.Work;

@Mapper
public interface WorkMapper {
	
	
	/**
	 * 특정 팀의 상태별 업무 목록 조회
	 * 
	 * @param work teamId, status가 포함된 Work 객체
	 * @return 해당 팀의 상태별 업무 목록 리스트
	 */
	List<WorkDTO> findWorkList(Work work);
	
	
	/**
	 * 새로운 업무 추가
	 * 
	 * 마감일은 SYSDATE + 7로 기본 설정되며,
	 * CURRENT_DATE는 SYSDATE 기준으로 자동 설정됨
	 * 
	 * @param work 업무 정보
	 * @return insert 성공 여부 (1:성공, 0:실패)
	 */
	int addWork(Work work);
	
	
	/**
	 * 업무 ID로 단일 업무 상세 조회
	 * 
	 * @param workId 조회할 업무의 ID
	 * @return 업무 상세 정보 DTO
	 */
	WorkDTO findWorkByWorkId(String workId);
	
	
	/**
	 * 업무 상태 변경 처리
	 * 
	 * 상태 변경 시 CURRENT_DATE를 SYSDATE로 갱신함
	 * 
	 * @param work 업무 ID와 변경할 상태 정보 포함
	 * @return update 성공 여부 (1:성공, 0:실패)
	 */
	int updateWorkStatus(Work work);
	
	
	/**
	 * 업무의 상세 정보(제목, 내용, 담당자, 마감일) 수정 처리
	 * 
	 * 마감일(endDate)은 문자열로 전달되며 TO_DATE로 변환됨
	 * 
	 * @param work 수정할 업무 정보
	 * @return update 성공 여부 (1:성공, 0:실패)
	 */
	int updateWorkDetail(Work work);
	
	
	/**
	 * 업무 ID를 기반으로 업무 삭제
	 * 
	 * @param workId 삭제할 업무 ID
	 * @return delete 성공 여부 (1:성공, 0:실패)
	 */
	int deleteWorkByWorkNo(String workId);
	
	
	boolean findWorkByStatus(WorkDTO work);
	
}

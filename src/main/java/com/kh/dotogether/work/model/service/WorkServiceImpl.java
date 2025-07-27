package com.kh.dotogether.work.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kh.dotogether.auth.service.AuthService;
import com.kh.dotogether.chat.model.dto.MessageDTO;
import com.kh.dotogether.team.model.dao.TeamMapper;
import com.kh.dotogether.team.model.vo.Team;
import com.kh.dotogether.team.util.TeamValidator;
import com.kh.dotogether.work.model.dao.WorkMapper;
import com.kh.dotogether.work.model.dto.WorkDTO;
import com.kh.dotogether.work.model.vo.Work;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {
	
	private final TeamValidator teamValidator;
	private final AuthService authService;
	private final WorkMapper workMapper;
	private final TeamMapper teamMapper;

	
	/**
	 * 팀의 업무 목록 조회
	 * 
	 * @param teamId 조회할 팀 ID
	 * @param status 업무 상태 필터 (예: todo, doing 등)
	 * @return 업무 DTO 리스트
	 */
	@Override
	public List<WorkDTO> findWorkList(String teamId, String status) {
		
		Long userNo = authService.getUserDetails().getUserNo();
		
		Team team = Team.builder()
						.userNo(userNo)
						.teamId(teamId)
						.build();
		
		teamValidator.isTeamMember(team);
				
		
		Work work = Work.builder().teamId(teamId).status(status).build();
		
		List<WorkDTO> workList = workMapper.findWorkList(work);
		
		return workList;
	}

	
	/**
	 * 업무 추가
	 * 
	 * @param request 추가할 업무 정보
	 * @return 추가된 업무 정보 또는 실패 메시지
	 */
	@Override
	public WorkDTO addWork(WorkDTO request) {
		
		String workId = UUID.randomUUID().toString();
		
		String isTeamMember = teamValidator.isTeamMember(request.getTeamId(), request.getRequestUserNo());
		
		if(isTeamMember != null) {
			return createResponse(request.getRequestUserNo(), isTeamMember);
		}
		
		Work work = Work.builder()
						.teamId(request.getTeamId())
						.workId(workId)
						.assigneeNo(request.getRequestUserNo())
						.status(request.getStatus())
						.build();
		
		int addWork = workMapper.addWork(work);
		
		if(addWork == 0) {
			return createResponse(request.getRequestUserNo(), "업무 추가에 실패했습니다.");
		}
		
		WorkDTO responseWork = workMapper.findWorkByWorkId(workId);
		
		responseWork.setType("add");
		responseWork.setRequestUserNo(request.getRequestUserNo());
		
		return responseWork;
	}

	
	/**
	 * 업무 상태 변경 (ex: TODO → DOING)
	 * 
	 * @param request 변경할 업무 정보 (workId, status 포함)
	 * @return 변경된 업무 정보 또는 실패 메시지
	 */
	@Override
	public WorkDTO updateWorkStatus(WorkDTO request) {
		
		String isTeamMember = teamValidator.isTeamMember(
				request.getTeamId(), request.getRequestUserNo());
				
		if(isTeamMember != null) {
			return createResponse(request.getRequestUserNo(), isTeamMember);
		}
		
		Work work = Work.builder()
						.workId(request.getWorkId())
						.status(request.getStatus())
						.version(request.getVersion())
						.build();
		
		
		int updateWorkStatus = workMapper.updateWorkStatus(work);
		
		if(updateWorkStatus == 0) {
			return createResponse(request.getRequestUserNo(), "업무 상태 수정에 실패했습니다.");
		}
		
		WorkDTO responseWork = workMapper.findWorkByWorkId(request.getWorkId());
		
		responseWork.setType("statusUpdate");
		responseWork.setRequestUserNo(request.getRequestUserNo());
		
		responseWork.setPrevStatus(request.getPrevStatus());
		
		return responseWork;
	}
	
	
	/**
	 * 업무 상태 변경 (ex: TODO → DOING)
	 * 
	 * @param request 변경할 업무 정보 (workId, status 포함)
	 * @return 변경된 업무 정보 또는 실패 메시지
	 */
//	@Override
//	public WorkDTO updateWorkStatus(WorkDTO request) {
//		
//		String isTeamMember = teamValidator.isTeamMember(
//				request.getTeamId(), request.getRequestUserNo());
//				
//		if(isTeamMember != null) {
//			return createResponse(request.getRequestUserNo(), isTeamMember);
//		}
//		
//		Work work = Work.builder()
//						.workId(request.getWorkId())
//						.status(request.getStatus())
//						.build();
//		
//		if(workMapper.findWorkByStatus(request)) {
//			return createResponse(request.getRequestUserNo(), "업무 상태가 이미 변경되었습니다.");
//		}
//		
//		int updateWorkStatus = workMapper.updateWorkStatus(work);
//		
//		if(updateWorkStatus == 0) {
//			return createResponse(request.getRequestUserNo(), "업무 상태 수정에 실패했습니다.");
//		}
//		
//		WorkDTO responseWork = workMapper.findWorkByWorkId(request.getWorkId());
//		
//		responseWork.setType("statusUpdate");
//		responseWork.setRequestUserNo(request.getRequestUserNo());
//		
//		responseWork.setPrevStatus(request.getPrevStatus());
//		
//		return responseWork;
//	}


	
	/**
	 * 업무 상세 내용 수정 (제목, 내용, 담당자, 마감일 등)
	 * 
	 * @param request 수정할 업무 정보
	 * @return 수정된 업무 정보 또는 실패 메시지
	 */
	@Override
	public WorkDTO updateWorkDetail(WorkDTO request) {
		
		String isTeamMember = teamValidator.isTeamMember(request.getTeamId(), request.getRequestUserNo());
				
		if(isTeamMember != null) {
			return createResponse(request.getRequestUserNo(), isTeamMember);
		}
		
		if(isNullOrEmpty(request.getTitle()) ||
		   request.getTitle() == null ||	
		   isNullOrEmpty(request.getAssigneeNo().toString()) ||
		   isNullOrEmpty(request.getEndDate()) 
		   ) {
			return createResponse(request.getRequestUserNo(), "빈 값이 있어 수정에 실패했습니다.");
		}
		
		if(isNullOrEmpty(request.getContent())) {
			request.setContent("내용 없음");
		}
		
		Work work = Work.builder()
				.workId(request.getWorkId())
				.title(request.getTitle())
				.content(request.getContent())
				.assigneeNo(request.getAssigneeNo())
				.endDate(request.getEndDate())
				.build();
		
		int updateWorkDetail = workMapper.updateWorkDetail(work);
		
		if(updateWorkDetail == 0) {
			return createResponse(request.getRequestUserNo(), "업무 정보 수정에 실패했습니다.");
		}
		
		WorkDTO responseWork = workMapper.findWorkByWorkId(request.getWorkId());
		
		responseWork.setType("update");
		responseWork.setRequestUserNo(request.getRequestUserNo());
				
		return responseWork;
		
	}

	
	/**
	 * 업무 삭제
	 * 
	 * @param request 삭제할 업무 정보 (workId, teamId 포함)
	 * @return 삭제 성공 여부를 포함한 요청 정보
	 */
	@Override
	public WorkDTO deleteWorkByWorkNo(WorkDTO request) {
		
		String isTeamMember = teamValidator.isTeamMember(request.getTeamId(), request.getRequestUserNo());
				
		if(isTeamMember != null) {
			return createResponse(request.getRequestUserNo(), isTeamMember);
		}
		
		int deleteWorkByWorkNo = workMapper.deleteWorkByWorkNo(request.getWorkId());
		
		if(deleteWorkByWorkNo == 0) {
			return createResponse(request.getRequestUserNo(), "업무 삭제에 실패했습니다.");
		}
				
		return request;
	}
	
	
	/**
	 * 문자열이 null이거나 공백인지 확인
	 * 
	 * @param value 검사할 문자열
	 * @return true = null 또는 빈 문자열, false = 값 존재
	 */
	private boolean isNullOrEmpty(String value) {
		return Optional.ofNullable(value)		// null값이면 orElse에 걸려 true 반환
					   .map(String::isBlank)	// null이 아닐 경우 isBlank 메서드를 이용해 빈 값인지 체크
					   .orElse(true);			
	}
	
	
	/**
	 * 문자열이 null이거나 공백인지 확인
	 * 
	 * @param value 검사할 문자열
	 * @return true = null 또는 빈 문자열, false = 값 존재
	 */
	private WorkDTO createResponse(Long requestUserNo, String type) {
		WorkDTO dto = new WorkDTO();
	    dto.setRequestUserNo(requestUserNo);
	    dto.setType(type);
	    return dto;
	}
	

}

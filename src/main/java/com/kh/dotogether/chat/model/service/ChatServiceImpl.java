package com.kh.dotogether.chat.model.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kh.dotogether.auth.service.AuthService;
import com.kh.dotogether.chat.model.dao.ChatMapper;
import com.kh.dotogether.chat.model.dto.MessageDTO;
import com.kh.dotogether.chat.model.vo.GetMessagesRequest;
import com.kh.dotogether.chat.model.vo.Message;
import com.kh.dotogether.team.model.dao.TeamMapper;
import com.kh.dotogether.team.model.vo.Team;
import com.kh.dotogether.team.util.TeamValidator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
	
	private final TeamValidator teamValidator;
	private final AuthService authService;
	private final ChatMapper chatMapper;
	private final TeamMapper teamMapper;
	
	
	/**
	 * 팀 채팅방의 메시지 목록 조회
	 * 
	 * <p>
	 * 특정 팀(채팅방)의 메시지들을
	 * 커서 기반 페이징(lastTimeStamp 기준)으로 조회함.
	 * 조회 전, 해당 팀에 사용자가 소속된 팀원인지 검증 수행.
	 * </p>
	 * 
	 * @param teamId 조회할 팀 ID(채팅방)
	 * @param lastTimeStamp 마지막으로 조회된 메시지의 타임스탬프 (null일 경우 최신부터 조회)
	 * @return 메시지 목록 리스트
	 */
	@Override
	public List<MessageDTO> findMessagesByRoomId(String teamId, String lastTimeStamp){
		
		GetMessagesRequest request = GetMessagesRequest.builder()
															 .teamId(teamId)
															 .lastTimeStamp(lastTimeStamp)
															 .build();
		
		Long userNo = authService.getUserDetails().getUserNo();
		
		Team team = Team.builder()
						.userNo(userNo)
						.teamId(request.getTeamId())
						.build();
		
		
		teamValidator.isTeamMember(team);
		
		List<MessageDTO> messages = chatMapper.findMessagesByRoomId(request);
		
		return messages;
	}


	/**
	 * 새로운 채팅 메시지 전송 처리
	 * 
	 * <p>
	 * 메시지 내용이 빈 문자열인지 확인하고,
	 * 메시지를 보내려는 사용자가 팀에 소속된 팀원인지 검증 후,
	 * 메시지 전송을 수행.
	 * </p>
	 * 
	 * @param message 전송할 메시지 DTO (팀ID, 발신자, 내용 포함)
	 * @return 실제 저장된 메시지 및 상태를 포함한 MessageDTO
	 */
	@Override
	public MessageDTO sendChatMessage(MessageDTO message) {

		String messageId = UUID.randomUUID().toString(); 
		String content = message.getContent();
		
		String checkContent = message.getContent().replaceAll("\\n", "").trim();
		
		if("".equals(checkContent)){
			return createResponse(message.getSenderNo(), "빈 문자 메시지는 전송이 불가능합니다.");
		}
		
		String validateTeamAndMember = 
				teamValidator.isTeamMember(message.getTeamId(), message.getSenderNo());
		
		if(validateTeamAndMember != null) {
			return createResponse(message.getSenderNo(), validateTeamAndMember);
		}
		
		Message messageVO = Message.builder()
								   .messageId(messageId)
								   .teamId(message.getTeamId())
								   .senderNo(message.getSenderNo())
								   .content(message.getContent())
								   .build();
		
		int sendChatMessage = chatMapper.sendChatMessage(messageVO);
		
		if(sendChatMessage == 0) {
			return createResponse(message.getSenderNo(), "메시지 전송에 실패했습니다.");
		}
		
		MessageDTO responseMessage = chatMapper.findMessageByMessageId(messageId);
		
		responseMessage.setType("send");
		responseMessage.setSenderNo(message.getSenderNo());
		
		return responseMessage;
	}


	/**
	 * 기존 채팅 메시지 수정 처리
	 * 
	 * <p>
	 * 메시지 내용이 빈 문자열인지 확인하고,
	 * 메시지를 수정하려는 사용자가 팀원인지,
	 * 발신자 본인이 맞는지, 메시지가 존재하는지 체크 후
	 * 메시지 수정을 수행함.
	 * </p>
	 * 
	 * @param message 수정할 메시지 DTO (메시지 ID, 팀ID, 발신자, 수정 내용 포함)
	 * @return 수정된 메시지와 상태를 담은 MessageDTO
	 */
	@Override
	public MessageDTO updateChatMessage(MessageDTO message) {
	
		String content = message.getContent();
		String checkContent = message.getContent().replaceAll("\\n", "").trim();
		
		if(checkContent == null || "".equals(checkContent)){
			return createResponse(message.getSenderNo(), "빈 문자 메시지는 전송이 불가능합니다.");
		}
		
		String validateTeamAndMember = 
				teamValidator.isTeamMember(message.getTeamId(), message.getSenderNo());
		
		if(validateTeamAndMember != null) {
			return createResponse(message.getSenderNo(), validateTeamAndMember);
		}
		
		if(chatMapper.checkIsSender(message)) {
			return createResponse(message.getSenderNo(), "발신자가 아니면 수정이 불가 합니다.");
		}
		
		if(chatMapper.checkMessage(message.getMessageId())) {
			return createResponse(message.getSenderNo(), "요청 보낸 메시지가 존재하지 않습니다.");
		}
		
		int updateChatMessage = chatMapper.updateChatMessage(message);
		
		if(updateChatMessage == 0) {
			return createResponse(message.getSenderNo(), "메시지 수정에 실패했습니다.");
		}
		
		MessageDTO responseMessage = createResponse(message.getSenderNo(), "update");
		
		responseMessage.setMessageId(message.getMessageId());
		responseMessage.setContent(message.getContent());
		
		return responseMessage;
	}


	/**
	 * 채팅 메시지 삭제 처리
	 * 
	 * <p>
	 * 메시지 삭제 요청 사용자가 팀원인지 확인하고,
	 * 발신자인지 검증 후 메시지 삭제를 수행함.
	 * </p>
	 * 
	 * @param message 삭제할 메시지 정보 DTO (메시지 ID, 팀ID, 발신자 정보 포함)
	 * @return 삭제 결과 상태를 포함한 MessageDTO
	 */
	@Override
	public MessageDTO deleteChatMessage(MessageDTO message) {
		String content = message.getContent();
		
		
		String validateTeamAndMember = 
				teamValidator.isTeamMember(message.getTeamId(), message.getSenderNo());
		
		if(validateTeamAndMember != null) {
			return createResponse(message.getSenderNo(), validateTeamAndMember);
		}
		
		if(chatMapper.checkIsSender(message)) {
			return createResponse(message.getSenderNo(), "발신자가 아니면 삭제가 불가 합니다.");
		}
		
		int deleteChatMessage = chatMapper.deleteChatMessage(message.getMessageId());
		
		if(deleteChatMessage == 0) {
			return createResponse(message.getSenderNo(), "메시지 삭제에 실패했습니다.");
		}
		
		MessageDTO responseMessage = createResponse(message.getSenderNo(), "delete");
		responseMessage.setMessageId(message.getMessageId());
		
		return responseMessage;
	}
	
	
	/**
	 * 요청 사용자 번호와 응답 타입 문자열을 기반으로 응답 객체 생성
	 * 
	 * @param senderNo 요청한 사용자 번호
	 * @param type 응답 타입 (예: 오류 메시지, 작업 유형)
	 * @return 응답 DTO 객체
	 */
	private MessageDTO createResponse(Long senderNo, String type) {
	    MessageDTO dto = new MessageDTO();
	    dto.setSenderNo(senderNo);
	    dto.setType(type);
	    return dto;
	}
	

}

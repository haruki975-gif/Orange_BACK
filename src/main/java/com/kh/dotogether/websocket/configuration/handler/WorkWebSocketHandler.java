package com.kh.dotogether.websocket.configuration.handler;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.dotogether.auth.service.AuthService;
import com.kh.dotogether.work.model.dto.WorkDTO;
import com.kh.dotogether.work.model.service.WorkService;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class WorkWebSocketHandler extends TextWebSocketHandler{

	private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
	private final WorkService workService;
	private final AuthService authService;
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		String roomId = getRoomId(session);
		
		if(roomId == null || "".equals(roomId)) {
			session.close(CloseStatus.BAD_DATA);
			return;
		}
		
		rooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);			
		
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		
		ObjectMapper objectMapper = new ObjectMapper();
		WorkDTO work = objectMapper.readValue(message.getPayload(), WorkDTO.class);
		
		String roomId = getRoomId(session);
		
		switch(work.getType()) {
		case "add": 
			work = workService.addWork(work);
			break;
		case "statusUpdate":
			work = workService.updateWorkStatus(work);
			break;
		case "update":
			work = workService.updateWorkDetail(work);
			break;
		case "delete":
			work = workService.deleteWorkByWorkNo(work);
			break;
		default : break;
		}
		
		TextMessage textMessage = 
				new TextMessage(objectMapper.writeValueAsString(work));
				
		for(WebSocketSession user: rooms.getOrDefault(roomId, Collections.emptySet())) {
			if(user.isOpen()) {
				user.sendMessage(textMessage);
			}
		}
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
	}
	
	
	
	private String getRoomId(WebSocketSession session) {
		
		String path = session.getUri().getPath();
		
		String[] endPiont = path.split("/");
		
		return endPiont[endPiont.length - 1];
	}
}


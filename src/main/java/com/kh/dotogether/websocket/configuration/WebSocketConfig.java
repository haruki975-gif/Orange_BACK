package com.kh.dotogether.websocket.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.kh.dotogether.websocket.configuration.handler.ChatWebSocketHandler;
import com.kh.dotogether.websocket.configuration.handler.WorkWebSocketHandler;
import com.kh.dotogether.websocket.configuration.handshake.WebSocketAuthInterceptor;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer{
	
	private final ChatWebSocketHandler chatHandler;
	private final WorkWebSocketHandler workHandler;
	private final WebSocketAuthInterceptor webSocketAuthInterceptor;
	
	@Value("${deploy.publicIp}")
	private String publicIp;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		
		// 배포 시 수정 부분
		registry.addHandler(chatHandler, "/ws/chat/{roomId}")
				.addInterceptors(webSocketAuthInterceptor)
//				.setAllowedOrigins(publicIp);
				.setAllowedOrigins("http://localhost:5173");
		
		// 배포 시 수정 부분
		registry.addHandler(workHandler, "/ws/work/{roomId}")
				.addInterceptors(webSocketAuthInterceptor)
//				.setAllowedOrigins(publicIp);
				.setAllowedOrigins("http://localhost:5173");
	}


}

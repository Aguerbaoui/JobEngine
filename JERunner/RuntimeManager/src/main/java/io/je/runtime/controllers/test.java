package io.je.runtime.controllers;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class test {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		 String URL = "ws://localhost:8081/ws";

			WebSocketClient client = new StandardWebSocketClient();
			WebSocketStompClient stompClient = new WebSocketStompClient(client);

			stompClient.setMessageConverter(new MappingJackson2MessageConverter());

			StompSessionHandler sessionHandler = new MyStompSessionHandler();
			stompClient.connect(URL, sessionHandler);

			new Scanner(System.in).nextLine(); // Don't close immediately.

	}
	public static class MyStompSessionHandler extends StompSessionHandlerAdapter {


		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
			System.out.println("New session established : " + session.getSessionId());
			//session.subscribe("/topic/testPayload", this);
			//System.out.println("Subscribed to /topic/testPayload");
			HashMap<String, String> m = new HashMap<>();
			m.put("test", "hola");
			session.send("/app/testPayload", m);
			System.out.println("Message sent to websocket server");
		}

		@Override
		public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
			System.out.println("Got an exception");
		}

		@Override
		public Type getPayloadType(StompHeaders headers) {
			return Map.class;
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			HashMap<String, String> msg = (HashMap<String, String>) payload;
			System.out.println("Received : " + msg.get("test"));
		}

		/**
		 * A sample message instance.
		 * @return instance of <code>Message</code>
		 */
		private Message getSampleMessage() {
			Message msg = new Message();
			msg.setFrom("Nicky");
			msg.setText("Howdy!!");
			return msg;
		}
	}

	public static class Message {

		private String from;
		private String text;

		public String getText() {
			return text;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public void setText(String text) {
			this.text = text;
		}

	}
}

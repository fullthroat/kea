package com.lollibond.chat.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.corundumstudio.socketio.SocketIOClient;

public class ClientDocker {

	@SuppressWarnings("rawtypes")
	private static final Map<ConsumerWorker, Map> mapStore = new ConcurrentHashMap<ConsumerWorker, Map>();
	@SuppressWarnings("rawtypes")
	private static final Map<Map, ConsumerWorker> threadStore = new ConcurrentHashMap<Map, ConsumerWorker>();
	private static final Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<String, SocketIOClient>();
	private static final Map<SocketIOClient, String> userNameMap=new ConcurrentHashMap<SocketIOClient, String>();

	public static Map<String, SocketIOClient> getClientmap() {
		return clientMap;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, SocketIOClient> initializeMapForThread(ConsumerWorker thread) {
		if (!mapStore.containsKey(thread)) {
			Map<String, SocketIOClient> threadMap = new HashMap<String, SocketIOClient>();
			mapStore.put(thread, threadMap);
			return threadMap;
		} else {
			return mapStore.get(thread);

		}
	}
	
	public static Map<SocketIOClient, String> getUsernamemap() {
		return userNameMap;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, SocketIOClient> getMapForThread(ConsumerWorker thread) {
		return mapStore.get(thread);
	}

	@SuppressWarnings("rawtypes")
	public static ConsumerWorker getThreadforMap(Map map) {
		return threadStore.get(map);
	}

	@SuppressWarnings("rawtypes")
	public static Map<ConsumerWorker, Map> getMapstore() {
		return mapStore;
	}

	@SuppressWarnings("rawtypes")
	public static Map<Map, ConsumerWorker> getThreadstore() {
		return threadStore;
	}

}

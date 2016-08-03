package com.lollibond.chat.server;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.google.gson.Gson;
import com.lollibond.chat.data.NotificationsPayload;
import com.lollibond.chat.data.RegisterPayload;
import com.lollibond.chat.data.ThreadParam;
import com.lollibond.chat.domain.MessageThread121;
import com.lollibond.chat.service.ThreadService;

public class DynamicChatServer {

	private static KafkaProducer<String, String> kafkaProducer;
	private static Map<String, SocketIOClient> clientMap = ClientDocker.getClientmap();
	private static Map<SocketIOClient, String> userNameMap = ClientDocker.getUsernamemap();

	@Autowired
	private static ThreadService threadService;

	/*
	 * @Kaunain - Used during benchmark - Please do not remove private static
	 * int count = 0;
	 */

	@Value("${com.lollibond.pigeon.host}")
	private static String hostName;

	@Value("${com.lollibond.pigeon.port}")
	private static int portNumber;

	public static void startServer(String args[]) throws InterruptedException {

		try {
			final int numberOfThreads = 4;

			Properties producerProperties = new Properties();
			producerProperties.put("bootstrap.servers", "localhost:9092");
			producerProperties.put("acks", "all");
			producerProperties.put("retries", "0");
			producerProperties.put("batch.size", 16384);
			producerProperties.put("linger.ms", 0);
			producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
			producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

			kafkaProducer = new KafkaProducer<String, String>(producerProperties);

			//
			Configuration config = new Configuration();
			config.setHostname(hostName);
			config.setPort(portNumber);

			final SocketIOServer server = new SocketIOServer(config);

			final ConsumerWorker consumerWorker[] = new ConsumerWorker[4];
			for (int i = 0; i < numberOfThreads; i++) {
				consumerWorker[i] = new ConsumerWorker();
				consumerWorker[i].start();
			}

			/*
			 * @Kaunain - Please do not remove this code
			 * server.addEventListener("chatevent", ChatPayload.class, new
			 * DataListener<ChatPayload>() {
			 * 
			 * @Override public void onData(SocketIOClient client, ChatPayload
			 * data, AckRequest ackRequest) { // broadcast messages to all
			 * clients server.getBroadcastOperations().sendEvent("chatevent",
			 * data); registerMap.put(data.getUserName(), client); } });
			 */

			server.addEventListener("register", RegisterPayload.class, new DataListener<RegisterPayload>() {
				@Override
				public void onData(SocketIOClient client, RegisterPayload data, AckRequest ackRequest) {
					System.out.println("Connection Request Revieved");
					/* countConnections(); */
					long userId = Long.parseLong(data.getUserId());
					int threadId = (int) (userId % consumerWorker.length);
					clientMap.put(data.getUserId(), client);
					userNameMap.put(client, data.getUserId());
					consumerWorker[threadId].getAssignedMap().put(data.getUserId(), client);
					consumerWorker[threadId].getShouldSubscribe().set(true);
				}
			});

			/*
			 * server.addEventListener("authenticate", RegisterPayload.class,
			 * new DataListener<RegisterPayload>() {
			 * 
			 * @Override public void onData(SocketIOClient client,
			 * RegisterPayload data, AckRequest ackRequest) { if
			 * (data.getUserId().contains("notification")) {
			 * clientMap.put(data.getUserId(), client); return; }
			 * clientMap.put(data.getUserId(), client);
			 * 
			 * }
			 * 
			 * });
			 */

			server.addConnectListener(new ConnectListener() {
				@Override
				public void onConnect(SocketIOClient client) {
					System.out.println("New Client Connected");
				}
			});

			server.addEventListener("loadthreadhistory", ThreadParam.class, new DataListener<ThreadParam>() {
				@Override
				public void onData(SocketIOClient client, ThreadParam data, AckRequest ackRequest) {
					if (data != null) {
						client.sendEvent("loadthreadhistory",
								new Gson().toJson(pullThread(data.getFromUser(), data.getToUser(), data.getPageNumber(),data.getPageSize())));
					}
				}
			});

			server.addDisconnectListener(new DisconnectListener() {
				@Override
				public void onDisconnect(SocketIOClient client) {
					clientMap.remove(userNameMap.get(client));
					userNameMap.remove(client);
				}
			});

			server.addEventListener("notification", NotificationsPayload.class,
					new DataListener<NotificationsPayload>() {
						@SuppressWarnings("unchecked")
						@Override
						public void onData(SocketIOClient client, NotificationsPayload data, AckRequest ackRequest) {
							JSONObject json = new JSONObject();
							json.put("notification_type", data.getType());
							json.put("notification_message", data.getMessage());
							saveMessage(data.getUid(), data.getTouid(), data.getMessage());
							ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(
									data.getTouid(), json.toJSONString());
							kafkaProducer.send(producerRecord);
							kafkaProducer.close();
						}
					});

			server.start();

			while (true) {
				// This loop should not be removed
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static boolean saveMessage(String fromUser, String toUser, String message) {
		return threadService.saveMessageToThread(message, fromUser, toUser);
	}

	private static List<MessageThread121> pullThread(String fromUser, String toUser, int pageNumber, int pageSize) {
		return threadService.pullThread(fromUser, toUser, new PageRequest(pageNumber, pageSize));
	}

	/*
	 * private static void countConnections() { DynamicChatServer.count++;
	 * 
	 * if (count % 100000 == 0) { System.out.println(
	 * "Connection Count is now ....." + count); } }
	 */

	/*
	 * @Kaunain - Please leave this code during review. Its a reminder set
	 * public static Map<String, SocketIOClient> getLeastLoadMap() {
	 * 
	 * Map<ConsumerWorker, Map> map = ClientDocker.getMapstore(); Map<Integer,
	 * Map> idMap = new HashMap<Integer, Map>(); int lengthArray[];
	 * Iterator<Map> iter = map.values().iterator(); while (iter.hasNext()) {
	 * Map mp = iter.next(); idMap.put(mp.size(), mp); } idMap.keySet();
	 * 
	 * 
	 * return null; }
	 */

}

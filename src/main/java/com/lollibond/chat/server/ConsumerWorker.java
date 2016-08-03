package com.lollibond.chat.server;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.corundumstudio.socketio.SocketIOClient;
import com.lollibond.chat.data.Message;

public class ConsumerWorker extends Thread {

	Properties consumerProperties;
	private KafkaConsumer<String, String> consumer;
	private final AtomicBoolean shouldSubscribe = new AtomicBoolean(true);
	private final Map<String, SocketIOClient> assignedMap = ClientDocker.initializeMapForThread(this);

	public AtomicBoolean getShouldSubscribe() {
		return shouldSubscribe;
	}

	public ConsumerWorker() {

		consumerProperties = new Properties();
		consumerProperties.put("bootstrap.servers", "localhost:9092");
		consumerProperties.put("group.id", "wunabalu");
		consumerProperties.put("enable.auto.commit", true);
		consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumerProperties.put("auto.commit.interval.ms", 1000);
		consumerProperties.put("session.timeout.ms", 30000);
		consumer = new KafkaConsumer<>(consumerProperties);
	}

	@Override
	public void run() {
		System.out.println("Kafka Consumer Started .......... Listening to topics");
		Set<String> topicIds = assignedMap.keySet();
		/*
		 * Map<String, SocketIOClient> clientMap = ClientDocker.getClientmap();
		 */

		while (true) {
			if (shouldSubscribe.get()) {
				shouldSubscribe.set(false);
				consumer.subscribe(topicIds);
			}
			ConsumerRecords<String, String> records = consumer.poll(1000);
			if (!records.isEmpty()) {
				if (assignedMap != null && !assignedMap.isEmpty()) {
					for (ConsumerRecord<String, String> record : records) {
						System.out.println(record.topic());
						System.out.println(record.value());
						JSONParser parser = new JSONParser();
						try {
							JSONObject jobj = (JSONObject) parser.parse(record.value());

							if (jobj != null) {

								if (jobj.get("notification_type").equals("Chat")) {
									Message msg = new Message();
									msg.setTopic(record.topic());
									msg.setMessage(jobj.get("notification_message").toString());
									assignedMap.get(record.topic()).sendEvent("chat_message",
											jobj.get("notification_message"));
								} else {
									Message msg = new Message();
									msg.setTopic(record.topic());
									msg.setMessage(jobj.get("notification_message").toString());
									assignedMap.get(record.topic()).sendEvent("notification",
											msg);
								}

							}

						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

		}

	}

	public Map<String, SocketIOClient> getAssignedMap() {
		return assignedMap;
	}

}

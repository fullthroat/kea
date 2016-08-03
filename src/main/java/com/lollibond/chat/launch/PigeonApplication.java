package com.lollibond.chat.launch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.lollibond.chat.server.DynamicChatServer;

@SpringBootApplication
@EnableCassandraRepositories(basePackages = { "com.lollibond.chat.repo" })
public class PigeonApplication implements CommandLineRunner{

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(PigeonApplication.class, args);
		
	}

	@Override
	public void run(String... arg0) throws Exception {
		DynamicChatServer.startServer(arg0);
		
	}
}

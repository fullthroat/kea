package com.lollibond.chat.domain;

import java.io.Serializable;

import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class ThreadId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@PrimaryKeyColumn(name = "fromUser", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String fromUser;
	@PrimaryKeyColumn(name = "fromUser", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
	private String toUser;
	@PrimaryKeyColumn(name = "fromUser", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
	private String time;
}

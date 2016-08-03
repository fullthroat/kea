package com.lollibond.chat.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.lollibond.chat.domain.MessageThread121;

public interface ThreadService{

	boolean saveMessageToThread(String message, String fromUser, String toUser);
	List<MessageThread121> pullThread(String fromUser, String toUser, Pageable pageable);
}

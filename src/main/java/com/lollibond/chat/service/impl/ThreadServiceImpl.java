package com.lollibond.chat.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.lollibond.chat.domain.MessageThread121;
import com.lollibond.chat.repo.MessageThreadRepository;
import com.lollibond.chat.service.ThreadService;

@Service
public class ThreadServiceImpl implements ThreadService {

	@Autowired
	private MessageThreadRepository messageThreadRepository;

	public boolean saveMessageToThread(String message, String fromUser, String toUser) {

		MessageThread121 chatMessage = new MessageThread121();
		chatMessage.setBody(message);
		if (fromUser != null && toUser != null) {
			long user = Long.parseLong(fromUser);
			long user2 = Long.parseLong(toUser);
			if (user > user2) {
				chatMessage.setU1(toUser);
				chatMessage.setU2(fromUser);

			} else {
				chatMessage.setU1(fromUser);
				chatMessage.setU2(toUser);
			}
		}
		chatMessage.setFromUser(fromUser);

		chatMessage.setTime(new Date());
		try {
			messageThreadRepository.save(chatMessage);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<MessageThread121> pullThread(String fromUser, String toUser, Pageable pageable) {
		if (fromUser != null && toUser != null) {
			long user = Long.parseLong(fromUser);
			long user2 = Long.parseLong(toUser);
			if (user > user2) {
				return messageThreadRepository.findByU1AndU2(toUser, fromUser, pageable);

			} else {
				return messageThreadRepository.findByU1AndU2(fromUser, toUser, pageable);
			}
		}
		return null;

	}

}

package com.lollibond.chat.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.lollibond.chat.domain.MessageThread121;

public interface MessageThreadRepository extends CrudRepository<MessageThread121, String> {

/*	List<MessageThread121> findByFromUserAndToUser(String fromuser, String touser);

	List<MessageThread121> findByFromUserAndToUser(String fromuser, String touser, Pageable pageAble);*/
	 
	List<MessageThread121> findByU1AndU2(String u1, String u2, Pageable pageable);

}

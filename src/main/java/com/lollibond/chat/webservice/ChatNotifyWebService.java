package com.lollibond.chat.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.lollibond.chat.data.ThreadParam;
import com.lollibond.chat.service.ThreadService;

@RestController
public class ChatNotifyWebService {

	@Autowired
	private ThreadService threadService;
	
	
	public String getChatThread(@RequestBody ThreadParam param){
		if(param!=null)
		return threadService.pullThread(param.getFromUser(), param.getToUser(), new PageRequest(param.getPageNumber(), param.getPageSize())).toString();
		return null;
	}
 
	
}

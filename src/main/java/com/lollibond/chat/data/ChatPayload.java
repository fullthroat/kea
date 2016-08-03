package com.lollibond.chat.data;

public class ChatPayload {

	private String userName;
	private String message;
	private String toUser;
	private String fromUser;

	public ChatPayload() {

	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	
	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	@Override
	public String toString() {
		return "ChatPayload [userName=" + userName + ", message=" + message + ", toUser=" + toUser + ", fromUser="
				+ fromUser + "]";
	}

}

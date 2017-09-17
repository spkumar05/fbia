package com.hearst.fbia.app.model;

public class Meta {

	private int code;
	private String message;
	private String detailedMessage;

	public Meta() {
		this.code = 200;
		this.message = "SUCCESS";
		this.detailedMessage = "SUCCESS";
	}

	public Meta(int code, String message, String detailedMessage) {
		this.code = code;
		this.message = message;
		this.detailedMessage = detailedMessage;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetailedMessage() {
		return detailedMessage;
	}

	public void setDetailedMessage(String detailedMessage) {
		this.detailedMessage = detailedMessage;
	}

}
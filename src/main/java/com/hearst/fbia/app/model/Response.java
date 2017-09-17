package com.hearst.fbia.app.model;

public class Response {

	private Object data;
	private Meta status;

	public Response() {
		super();
	}

	public Response(Object data, Meta status) {
		super();
		this.data = data;
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Meta getStatus() {
		return status;
	}

	public void setStatus(Meta status) {
		this.status = status;
	}

}
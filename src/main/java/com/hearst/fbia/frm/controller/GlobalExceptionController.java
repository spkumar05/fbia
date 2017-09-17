package com.hearst.fbia.frm.controller;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hearst.fbia.app.model.Meta;
import com.hearst.fbia.app.model.Response;

@ControllerAdvice
public class GlobalExceptionController {

	@ResponseBody
	@ExceptionHandler(Exception.class)
	public Response handleAllException(Exception ex) {
		return new Response(null,
				new Meta(500, ExceptionUtils.getMessage(ex).split(":")[0], ExceptionUtils.getMessage(ex)));
	}
}
package com.gms.web.admin.controller.common;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorPageHandler implements ErrorController {
	
	@RequestMapping("/error")
	public String handleError(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);		
		
		if(status != null){ 
			int statusCode = Integer.valueOf(status.toString());
			
			if(statusCode == HttpStatus.NOT_FOUND.value()){ 
				return "gms/error"; 
			} 
			
			if(statusCode == HttpStatus.FORBIDDEN.value()){ 
				return "gms/error"; 
			}			
		}
		return "gms/error";
	}
		
	@Override
	public String getErrorPath() {
		// TODO Auto-generated method stub
		return "gms//error";
	}

}

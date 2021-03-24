package com.gms.web.admin.common.web.utils;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gms.web.admin.common.domain.AbstractVO;
import com.gms.web.admin.domain.common.LoginUserVO;



public class RequestUtils {
	public static void initUserPrgmInfo(HttpServletRequest request, AbstractVO abstractVO) {
		
		String userId = null;
		LoginUserVO adminLoginUserVO = (LoginUserVO)request.getSession().getAttribute(LoginUserVO.ATTRIBUTE_NAME);
		
		userId = adminLoginUserVO.getUserId();
		
		// TODO 계정 및 메뉴 관리 정책 정해지기 전 임시코드
		//userId = StringUtils.defaultIfEmpty(userId, "SYSTEM");
		
		abstractVO.setCreateId(userId);
		abstractVO.setUpdateId(userId);
	}
	
	public static String getParameterInfo(HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		
		Enumeration enumParamNames = request.getParameterNames();
		while(enumParamNames.hasMoreElements()) {
			String paramName = (String) enumParamNames.nextElement();
			
			sb.append("\n").append(paramName).append(" = " ).append(request.getParameter(paramName));
		}
		
		return sb.toString();
	}
	
	public static String forwardUrl(HttpServletRequest request, String contentUrl) {
		String contentPath = request.getContextPath();
		if(contentPath != null && contentPath.length() > 0) {
			contentUrl = contentUrl.substring(contentPath.length());
		}
		
		return contentUrl;
	}
	
	public static String removeParam(String contentUrl, String paramName) {
		if(contentUrl == null || paramName == null || paramName.length() < 1)
			return contentUrl;
		
		int beginIndex = contentUrl.indexOf(paramName + "=");
		if(beginIndex < 0) 
			return contentUrl;
		
		int endIndex = contentUrl.indexOf('&', beginIndex + paramName.length() + 1);
		if(endIndex < 0) {
			endIndex = contentUrl.length();
			if(beginIndex > 0) {
				beginIndex--;
			}
		} else {
			endIndex++;
		}
		
		return contentUrl.substring(0, beginIndex) + contentUrl.substring(endIndex);
	}
	
	public static String removePageNoParam(String contentUrl) {
		return removeParam(contentUrl, "pageNo");
	}
	
	/**
     * JSP 에 메시지 출력
     * 
     * @param response
     * @param message
     */
	public static void responseWriteException(HttpServletResponse response, String message, String returnUrl) {
        
        response.reset();
        
        try {
            StringBuffer html = new StringBuffer();
            html.append("<html><head>");
            html.append("<title>GMS Admin</title>");
            html.append("<script type='text/javascript'>");
            html.append(" alert(\"");
            html.append(message);
            html.append(" \"); ");
            html.append("location.href='" + returnUrl + "';");
            html.append("</script></head>");
            html.append("<body></body></html>");
            
            response.setContentType("text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getOutputStream().write(html.toString().getBytes("UTF-8"));

        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
}

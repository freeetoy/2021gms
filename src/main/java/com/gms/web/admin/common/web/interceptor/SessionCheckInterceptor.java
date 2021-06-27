package com.gms.web.admin.common.web.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.SessionUtil;
import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.service.common.LoginService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SessionCheckInterceptor implements HandlerInterceptor {
	
	private Long startTime = 0L;
	
	@Autowired
	private LoginService loginService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		startTime = System.currentTimeMillis();

		String requestURI = request.getRequestURI().trim();
        
		HttpSession session = request.getSession(false);
        
        String userId = "";
        String systemRole = "";
       
        String forwardUrl = "gms/login";
        
        LoginUserVO sessionInfo = SessionUtil.getSessionInfo(request);       
        
        if(sessionInfo != null) {  
            
            userId 		= StringUtils.defaultString(sessionInfo.getUserId());
            systemRole 	= StringUtils.defaultString(sessionInfo.getUserAuthority());
         
            session.setAttribute("compNm", PropertyFactory.getProperty("common.Member.Comp.Daehan.name"));		
            
            // Session에 있는 ID가 존재하는지 확인하여 없으면, 강제 로그아웃 처리
            if ("".equals(userId) || "".equals(systemRole)) {
                request.getSession().invalidate();
				response.sendRedirect(request.getContextPath() + "/" + forwardUrl);
				return false;   				
            }
        }
        else {   
        	
        	final Cookie[] cookies = request.getCookies();

            Map<String, String> result = new HashMap<>();
            if(cookies != null ) {
	            for (Cookie cookie : cookies) {
	            	
	                if(cookie.getName().equals(LoginUserVO.ATTRIBUTE_NAME)) {
	                	userId = cookie.getValue();
	                	break;
	                }
	            }
            }
            if(userId != null && userId.length() > 0) {
            	LoginUserVO param = new LoginUserVO();
            	param.setUserId(userId); 
            	
            	LoginUserVO loginUser = loginService.getLoginUser(param);
            	
            	if(loginUser != null) {
            		            		
            		session = request.getSession(false);
            		session.setAttribute("LoginUser", loginUser);		
    				
    				session.setAttribute("userId", loginUser.getUserId());		
    				session.setAttribute("compNm", PropertyFactory.getProperty("common.Member.Comp.Daehan.name"));	
    				return true;
            	}else {
            		response.sendRedirect(request.getContextPath() + "/login");
    				return false;
            	}
            }else {
	        	response.sendRedirect(request.getContextPath() + "/login");
				return false;   				
	        }        
        }
        return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		//log.debug("postHandle!!");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		Long endTime = System.currentTimeMillis();
		//log.debug("afterCompletion!! =" +(endTime-startTime)+" millis");
	}

}

package com.gms.web.admin.common.web.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.gms.web.admin.domain.common.LoginUserVO;

public class SessionUtil {
	public static LoginUserVO getSessionInfo(HttpServletRequest request){ 

		LoginUserVO adminLoginUserVO = (LoginUserVO)request.getSession().getAttribute(LoginUserVO.ATTRIBUTE_NAME); 
		return adminLoginUserVO; 
	}
	
	public boolean saveSessionInfo(HttpServletRequest request, LoginUserVO userInfo){ 
		
		boolean isSuccess = false;
		
		HttpSession session = request.getSession();
		
		if(null != session){
			LoginUserVO adminLoginUserVO = new LoginUserVO();
        	
        	adminLoginUserVO.setUserId(userInfo.getUserId());
        	adminLoginUserVO.setUserNm(userInfo.getUserNm());
        	adminLoginUserVO.setUserPartCd(userInfo.getUserPartCd());
        	adminLoginUserVO.setUserAuthority(userInfo.getUserAuthority());
        	adminLoginUserVO.setCurrentMenu(userInfo.getCurrentMenu());
        	
        	//adminLoginUserVO.setPwdErrCnt(userInfo.getPwdErrCnt());
        	//adminLoginUserVO.setPwdUptDt(userInfo.getPwdUptDt());
        	//adminLoginUserVO.setSvcStatus(userInfo.getSvcStatus());
        	
        	session.setAttribute(LoginUserVO.ATTRIBUTE_NAME, adminLoginUserVO);
        	
        	isSuccess = true;
		}
		return isSuccess;
	}
}

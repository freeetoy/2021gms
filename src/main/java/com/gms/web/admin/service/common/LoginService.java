package com.gms.web.admin.service.common;

import com.gms.web.admin.domain.common.LoginUserVO;

public interface LoginService {
	public LoginUserVO modifyUserInfo(LoginUserVO param);
	
	public LoginUserVO getUserInfo(LoginUserVO param);
	
	public LoginUserVO getLoginUser(LoginUserVO param);
	
	public int modifyLastConnect(LoginUserVO param);
}

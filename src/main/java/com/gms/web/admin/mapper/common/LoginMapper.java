package com.gms.web.admin.mapper.common;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.common.LoginUserVO;

@Mapper
public interface LoginMapper {
	public LoginUserVO selectUserInfo(LoginUserVO param);
	public int updateUserLastConnect(LoginUserVO param);
	public LoginUserVO selectLoginUser(LoginUserVO param);
}

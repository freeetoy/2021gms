package com.gms.web.admin.mapper.common;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.common.AppVersionVO;
import com.gms.web.admin.domain.common.CodeVO;


@Mapper
public interface CodeMapper {
	public List<CodeVO> selectCodeList(String param);
	
	public AppVersionVO selectAppVersion();
}

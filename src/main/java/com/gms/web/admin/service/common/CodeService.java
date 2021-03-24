package com.gms.web.admin.service.common;

import java.util.List;

import com.gms.web.admin.domain.common.AppVersionVO;
import com.gms.web.admin.domain.common.CodeVO;

public interface CodeService {

	public List<CodeVO> getCodeList(String param);
	
	public AppVersionVO getAppVersion();
}

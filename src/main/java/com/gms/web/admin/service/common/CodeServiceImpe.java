package com.gms.web.admin.service.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.common.AppVersionVO;
import com.gms.web.admin.domain.common.CodeVO;
import com.gms.web.admin.mapper.common.CodeMapper;

@Service
public class CodeServiceImpe implements CodeService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CodeMapper codeMapper;

	@Override
	public List<CodeVO> getCodeList(String param) {
		logger.debug("****** getCodeList *****===*" + param);
		return codeMapper.selectCodeList(param);
	}

	@Override
	public AppVersionVO getAppVersion() {
		return codeMapper.selectAppVersion();
	}

}

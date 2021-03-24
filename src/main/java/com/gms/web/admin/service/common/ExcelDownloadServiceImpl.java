package com.gms.web.admin.service.common;

import org.apache.ibatis.session.ResultContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.common.utils.ResultRowDataHandler;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.mapper.manage.BottleMapper;

@Service
public class ExcelDownloadServiceImpl implements ExcelDownloadService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private BottleMapper bottleMapper;
	
	@Override
	public void download(BottleVO bottleVo, ResultRowDataHandler resultRowDataHandler) {
				
		BottleVO result = bottleMapper.selectBottleDetail(bottleVo.getBottleId());
		resultRowDataHandler.open(result);
		resultRowDataHandler.handleResult((ResultContext<? extends BottleVO>) result);
		
		//MyObjDto dto = resultContext.getResultObject();

	}

}

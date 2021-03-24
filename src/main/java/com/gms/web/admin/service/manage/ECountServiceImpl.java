package com.gms.web.admin.service.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.manage.ECountVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.mapper.manage.ECountMapper;


@Service
public class ECountServiceImpl implements ECountService {

	@Autowired
	private ECountMapper eMapper;

	@Override
	public List<ECountVO> getECountList(WorkReportVO workReport) {
		return eMapper.selectECount(workReport);
	}

}

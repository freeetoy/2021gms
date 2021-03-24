package com.gms.web.admin.service.manage;

import java.util.List;

import com.gms.web.admin.domain.manage.ECountVO;
import com.gms.web.admin.domain.manage.WorkReportVO;

public interface ECountService {

	public List<ECountVO> getECountList(WorkReportVO workReport);
}

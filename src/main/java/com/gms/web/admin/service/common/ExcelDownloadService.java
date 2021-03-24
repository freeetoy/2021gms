package com.gms.web.admin.service.common;

import com.gms.web.admin.common.utils.ResultRowDataHandler;
import com.gms.web.admin.domain.manage.BottleVO;

public interface ExcelDownloadService {
	public void download(BottleVO commonVo, ResultRowDataHandler resultRowDataHandler);

}

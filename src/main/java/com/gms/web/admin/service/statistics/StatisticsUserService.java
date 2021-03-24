package com.gms.web.admin.service.statistics;

import java.util.List;

import com.gms.web.admin.domain.statistics.StatisticsUserVO;

public interface StatisticsUserService {
	public List<StatisticsUserVO> getDailylStatisticsUserList(StatisticsUserVO param);	
	
	public List<StatisticsUserVO> getMontlylStatisticsUserList(StatisticsUserVO param);	
	
	public int registerDailyStatisticsUser();

	public int registerMonthlyStatisticsUser();
}

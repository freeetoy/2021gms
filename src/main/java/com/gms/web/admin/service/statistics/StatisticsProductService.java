package com.gms.web.admin.service.statistics;

import java.util.List;

import com.gms.web.admin.domain.statistics.StatisticsProductVO;

public interface StatisticsProductService {
	
	public List<StatisticsProductVO> getDailylStatisticsProductList(StatisticsProductVO param);	
	
	public List<StatisticsProductVO> getMontlylStatisticsProductList(StatisticsProductVO param);	
	
	public int registerDailyStatisticsProduct();

	public int registerMonthlyStatisticsProduct();
}

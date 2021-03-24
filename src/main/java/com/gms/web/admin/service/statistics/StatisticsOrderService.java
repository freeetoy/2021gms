package com.gms.web.admin.service.statistics;

import java.util.List;

import com.gms.web.admin.domain.statistics.StatisticsOrderVO;
import com.gms.web.admin.domain.statistics.StatisticsSalesVO;

public interface StatisticsOrderService {

	public List<StatisticsOrderVO> getDailylStatisticsOrderList(StatisticsOrderVO param);	
	
	public List<StatisticsOrderVO> getMontlylStatisticsOrderList(StatisticsOrderVO param);	
	
	public int registerDailyStatisticsOrder();

	public int registerMonthlyStatisticsOrder();
	
	
	public List<StatisticsSalesVO> getDailylStatisticsSalesList(StatisticsSalesVO param);	
	
	public List<StatisticsSalesVO> getMontlylStatisticsSalesList(StatisticsSalesVO param);	
	
	public int registerDailyStatisticsSales();

	public int registerMonthlyStatisticsSales();
}

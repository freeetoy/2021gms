package com.gms.web.admin.service.statistics;

import java.util.List;

import com.gms.web.admin.domain.statistics.StatisticsCustomerBottleVO;
import com.gms.web.admin.domain.statistics.StatisticsCustomerVO;

public interface StatisticsCustomerService {
	
	public List<StatisticsCustomerVO> getDailylStatisticsCustomerList(StatisticsCustomerVO param);	
	
	public List<StatisticsCustomerVO> getMontlylStatisticsCustomerList(StatisticsCustomerVO param);	
	
	public int registerDailyStatisticsCustomer();

	public int registerMonthlyStatisticsCustomer();
	
	public List<StatisticsCustomerBottleVO> getStatisticsCustomerBottleList(StatisticsCustomerBottleVO param);	
	
	public List<StatisticsCustomerBottleVO> getStatSalesCustomerBottleList(StatisticsCustomerBottleVO param);	
	
	public List<StatisticsCustomerVO> getStatSalesCustomerCount(StatisticsCustomerBottleVO param);
}

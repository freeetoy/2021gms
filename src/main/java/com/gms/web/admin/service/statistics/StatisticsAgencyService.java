package com.gms.web.admin.service.statistics;

import java.util.List;
import java.util.Map;

import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.statistics.StatisticsAgencyResultVO;
import com.gms.web.admin.domain.statistics.StatisticsAgencyResultVO2;
import com.gms.web.admin.domain.statistics.StatisticsAgencyVO;

public interface StatisticsAgencyService {

	public List<StatisticsAgencyResultVO> getDailylStatisticsAgencyList(StatisticsAgencyVO param);	
	
	public List<StatisticsAgencyResultVO> getMontlylStatisticsAgencyList(StatisticsAgencyVO param);	
	
	public List<CustomerVO> getStatisticsAgencyCustomerList(StatisticsAgencyVO param);	
	
	public int registerDailyStatisticsAgency();

	public int registerMonthlyStatisticsAgency();
	
	public Map<String, Object> getDailylStatisticsAgencyList1(StatisticsAgencyVO param);	

}

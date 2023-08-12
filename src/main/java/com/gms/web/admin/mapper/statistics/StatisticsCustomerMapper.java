package com.gms.web.admin.mapper.statistics;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.statistics.StatisticsCustomerBottleVO;
import com.gms.web.admin.domain.statistics.StatisticsCustomerVO;

@Mapper
public interface StatisticsCustomerMapper {
	
	public List<StatisticsCustomerVO> selectDailylStatisticsCustomerList(Map<String, Object> map);	
	
	public List<StatisticsCustomerVO> selectMontlylStatisticsCustomerList(Map<String, Object> map);	
	
	public int inserDailyStatisticsCustomer();
	
	public int inserDailyStatisticsCustomerOrigin(List<StatisticsCustomerVO> param);
	
	public List<StatisticsCustomerVO> selectDailyCustomerForInsertNew();
	
	public int deleteDailyStatisticsCustomer();

	public int inserMonthlyStatisticsCustomer();
	
	public List<StatisticsCustomerBottleVO> selectStatCustomerBottleList(StatisticsCustomerBottleVO param);	
	
	public List<StatisticsCustomerBottleVO> selectStatCustomerBottleHistList(StatisticsCustomerBottleVO param);
	
	public List<StatisticsCustomerBottleVO> selectStatSalesCustomerBottleList(StatisticsCustomerBottleVO param);
	
	public List<StatisticsCustomerVO> selectCountSalesCustomer(StatisticsCustomerBottleVO param);

}

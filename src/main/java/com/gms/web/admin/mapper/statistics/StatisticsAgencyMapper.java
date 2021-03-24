package com.gms.web.admin.mapper.statistics;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.statistics.StatisticsAgencyVO;

@Mapper
public interface StatisticsAgencyMapper {
	
	public List<CustomerVO> selectStatisticsAgencyCustomerList(Map<String, Object> map);	
	
	public List<ProductPriceSimpleVO> selectStatisticsAgencyProductList(Map<String, Object> map);	
	
	public List<StatisticsAgencyVO> selectDailylStatisticsAgencyList(Map<String, Object> map);	
	
	public List<StatisticsAgencyVO> selectMontlylStatisticsAgencyList(Map<String, Object> map);	
		
	public int insertDailyStatisticsAgency();

	public int insertMonthlyStatisticsAgency();
	
	public List<StatisticsAgencyVO> selectTodayStatisticsAgencyList();	
	
	public List<CustomerVO> selectTodayStatisticsAgencyCustomerList();	
	
	public List<ProductPriceSimpleVO> selectTodayStatisticsAgencyProductList();

}

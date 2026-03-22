package com.gms.web.admin.mapper.statistics;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.statistics.StatisticsProductVO;
import com.gms.web.admin.domain.statistics.StatisticsProuctCustomerCountVO;

@Mapper
public interface StatisticsProductMapper {
	
	public List<StatisticsProductVO> selectDailylStatisticsProductList(Map<String, Object> map);	
	
	public List<StatisticsProductVO> selectMontlylStatisticsProductList(Map<String, Object> map);	
	
	public int inserDailyStatisticsProduct();

	public int inserMonthlyStatisticsProduct();
	
	public int deleteDailyStatProduct();

	public List<StatisticsProuctCustomerCountVO> selectWorkBottleListOfProductStat(StatisticsProuctCustomerCountVO param);
}

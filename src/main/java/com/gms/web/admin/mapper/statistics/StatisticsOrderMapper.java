package com.gms.web.admin.mapper.statistics;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.statistics.StatisticsOrderVO;
import com.gms.web.admin.domain.statistics.StatisticsSalesVO;

@Mapper
public interface StatisticsOrderMapper {

	public List<StatisticsOrderVO> selectDailylStatisticsOrderList(Map<String, Object> map);	
	
	public List<StatisticsOrderVO> selectMontlylStatisticsOrderList(Map<String, Object> map);	
	
	public int inserDailyStatisticsOrder();
	
	public int inserDailyStatisticsOrderInfo(StatisticsOrderVO param);
	
	public StatisticsOrderVO selectDailylOrderInfo();

	public int inserMonthlyStatisticsOrder();
	
	public List<StatisticsSalesVO> selectDailylStatisticsSalesList(Map<String, Object> map);	
	
	public List<StatisticsSalesVO> selectMontlylStatisticsSalesList(Map<String, Object> map);	
	
	public int insertDailyStatisticsSales();
	
	public int insertMonthlyStatisticsSales();
}

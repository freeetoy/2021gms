package com.gms.web.admin.mapper.statistics;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.statistics.StatisticsBottleVO;

@Mapper
public interface StatisticsBottleMapper {

	public List<StatisticsBottleVO> selectDailylStatisticsBottleList(Map<String, Object> map);	
	
	public List<StatisticsBottleVO> selectMontlylStatisticsBottleList(Map<String, Object> map);	
	
	public List<StatisticsBottleVO> selectDailylStatisticsBottleListNew(Map<String, Object> map);	
	
	public StatisticsBottleVO selectTodayStatisticsBottleListNew(Map<String, Object> map);	
	
	public int inserDailyStatisticsBottle();

	public int inserMonthlyStatisticsBottle();
	
	public int inserDailyStatisticsBottleNew();
}

package com.gms.web.admin.service.statistics;

import java.util.List;

import com.gms.web.admin.domain.statistics.StatisticsBottleVO;

public interface StatisticsBottleService {

	public List<StatisticsBottleVO> getDailylStatisticsBottleList(StatisticsBottleVO param);	
	
	public List<StatisticsBottleVO> getMontlylStatisticsBottleList(StatisticsBottleVO param);	
	
	public List<StatisticsBottleVO> getDailylStatisticsBottleListNew(StatisticsBottleVO param);
	
	public StatisticsBottleVO getTodayStatisticsBottleListNew(StatisticsBottleVO param);
	
	public int registerDailyStatisticsBottle();

	public int registerMonthlyStatisticsBottle();
	
	public int registerDailyStatisticsBottleNew();

}

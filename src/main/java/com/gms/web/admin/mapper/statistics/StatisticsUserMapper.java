package com.gms.web.admin.mapper.statistics;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.statistics.StatisticsUserVO;

@Mapper
public interface StatisticsUserMapper {

	public List<StatisticsUserVO> selectDailylStatisticsUserList(Map<String, Object> map);	
	
	public List<StatisticsUserVO> selectMontlylStatisticsUserList(Map<String, Object> map);	
	
	public int inserDailyStatisticsUser();

	public int inserMonthlyStatisticsUser();
}

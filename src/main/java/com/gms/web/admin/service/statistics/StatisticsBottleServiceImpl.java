package com.gms.web.admin.service.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.statistics.StatisticsBottleVO;
import com.gms.web.admin.mapper.statistics.StatisticsBottleMapper;

@Service
public class StatisticsBottleServiceImpl implements StatisticsBottleService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatisticsBottleMapper statMapper;
	
	@Override
	public List<StatisticsBottleVO> getDailylStatisticsBottleList(StatisticsBottleVO param) {
		logger.info("****** getDailylStatisticsBottleList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
//			logger.debug("****** getDailylStatisticsBottleList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
//			logger.debug("****** getDailylStatisticsBottleList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
//			logger.debug("****** getDailylStatisticsBottleList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsBottleVO> statList = statMapper.selectDailylStatisticsBottleList(map);	
		
		return statList;
	}

	@Override
	public List<StatisticsBottleVO> getMontlylStatisticsBottleList(StatisticsBottleVO param) {
		logger.info("****** getMonthlyStatisticsBottleList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
//			logger.debug("****** getMonthlylStatisticsBottleList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
//			logger.debug("****** getMonthlylStatisticsBottleList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
//			logger.debug("****** getMonthlylStatisticsBottleList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsBottleVO> statList = statMapper.selectMontlylStatisticsBottleList(map);		
				
		return statList;
	}

	@Override
	public int registerDailyStatisticsBottle() {
		// TODO Auto-generated method stub
		return statMapper.inserDailyStatisticsBottle();
	}

	@Override
	public int registerMonthlyStatisticsBottle() {
		// TODO Auto-generated method stub
		return statMapper.inserMonthlyStatisticsBottle();
	}

}

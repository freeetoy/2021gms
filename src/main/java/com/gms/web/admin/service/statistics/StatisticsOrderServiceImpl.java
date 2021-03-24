package com.gms.web.admin.service.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.statistics.StatisticsOrderVO;
import com.gms.web.admin.domain.statistics.StatisticsSalesVO;
import com.gms.web.admin.mapper.statistics.StatisticsOrderMapper;

@Service
public class StatisticsOrderServiceImpl implements StatisticsOrderService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatisticsOrderMapper statMapper;
	
	@Override
	public List<StatisticsOrderVO> getDailylStatisticsOrderList(StatisticsOrderVO param) {
		logger.info("****** getDailylStatisticsOrderList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
			logger.debug("****** getDailylStatisticsOrderList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
			logger.debug("****** getDailylStatisticsOrderList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
			logger.debug("****** getDailylStatisticsOrderList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsOrderVO> statList = statMapper.selectDailylStatisticsOrderList(map);			
				
		return statList;
	}

	@Override
	public List<StatisticsOrderVO> getMontlylStatisticsOrderList(StatisticsOrderVO param) {
		logger.info("****** getMonthlyStatisticsOrderList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
			logger.debug("****** getMonthlylStatisticsOrderList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
			logger.debug("****** getMonthlylStatisticsOrderList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
			logger.debug("****** getMonthlylStatisticsOrderList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsOrderVO> statList = statMapper.selectMontlylStatisticsOrderList(map);	
						
		return statList;
	}

	@Override
	public int registerDailyStatisticsOrder() {		
		return statMapper.inserDailyStatisticsOrder();
	}

	@Override
	public int registerMonthlyStatisticsOrder() {
		return statMapper.inserMonthlyStatisticsOrder();
	}

	@Override
	public List<StatisticsSalesVO> getDailylStatisticsSalesList(StatisticsSalesVO param) {
		logger.info("****** getDailylStatisticsSalesList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
			logger.debug("****** getDailylStatisticsSalesList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
			logger.debug("****** getDailylStatisticsSalesList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
			logger.debug("****** getDailylStatisticsSalesList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsSalesVO> statList = statMapper.selectDailylStatisticsSalesList(map);			
				
		return statList;
	}

	@Override
	public List<StatisticsSalesVO> getMontlylStatisticsSalesList(StatisticsSalesVO param) {
		logger.info("****** getMonthlyStatisticsSalesList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
			logger.debug("****** getMonthlyStatisticsSalesList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
			logger.debug("****** getMonthlyStatisticsSalesList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
			logger.debug("****** getMonthlyStatisticsSalesList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsSalesVO> statList = statMapper.selectMontlylStatisticsSalesList(map);	
						
		return statList;
	}

	@Override
	public int registerDailyStatisticsSales() {
		return statMapper.insertDailyStatisticsSales();
	}

	@Override
	public int registerMonthlyStatisticsSales() {
		return statMapper.insertMonthlyStatisticsSales();
	}

}

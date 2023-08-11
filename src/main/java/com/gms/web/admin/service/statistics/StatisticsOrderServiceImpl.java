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
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
		}	
				
		List<StatisticsOrderVO> statList = statMapper.selectDailylStatisticsOrderList(map);			
				
		return statList;
	}

	@Override
	public List<StatisticsOrderVO> getMontlylStatisticsOrderList(StatisticsOrderVO param) {
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
		}	
				
		List<StatisticsOrderVO> statList = statMapper.selectMontlylStatisticsOrderList(map);	
						
		return statList;
	}

	@Override
	public int registerDailyStatisticsOrder() {		
		
		int result = 0;
		try {
			StatisticsOrderVO statOrder = statMapper.selectDailylOrderInfo();
			result = statMapper.inserDailyStatisticsOrderInfo(statOrder);	
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		
		return result;
		//return statMapper.inserDailyStatisticsOrder();
	}

	@Override
	public int registerMonthlyStatisticsOrder() {
		return statMapper.inserMonthlyStatisticsOrder();
	}

	@Override
	public List<StatisticsSalesVO> getDailylStatisticsSalesList(StatisticsSalesVO param) {
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
		}	
				
		List<StatisticsSalesVO> statList = statMapper.selectDailylStatisticsSalesList(map);			
				
		return statList;
	}

	@Override
	public List<StatisticsSalesVO> getMontlylStatisticsSalesList(StatisticsSalesVO param) {
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
		}	
				
		List<StatisticsSalesVO> statList = statMapper.selectMontlylStatisticsSalesList(map);	
						
		return statList;
	}

	@Override
	public int registerDailyStatisticsSales() {
		int result = 0;
		try {
			StatisticsSalesVO sales = statMapper.selectDailySalesForInsert();
			result = statMapper.insertDailyStatisticsSalesOrigin(sales);
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return result;
	}

	@Override
	public int registerMonthlyStatisticsSales() {
		return statMapper.insertMonthlyStatisticsSales();
	}

}

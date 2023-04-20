package com.gms.web.admin.service.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.statistics.StatisticsCustomerBottleVO;
import com.gms.web.admin.domain.statistics.StatisticsCustomerVO;
import com.gms.web.admin.mapper.statistics.StatisticsCustomerMapper;

@Service
public class StatisticsCustomerServiceImpl implements StatisticsCustomerService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatisticsCustomerMapper statMapper;
	
	@Override
	public List<StatisticsCustomerVO> getDailylStatisticsCustomerList(StatisticsCustomerVO param) {
		
		Map<String, Object> map = new HashMap<String, Object>();		

		map.put("searchCustomerId", param.getSearchCustomerId());	
		
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
		}	
				
		List<StatisticsCustomerVO> statList = statMapper.selectDailylStatisticsCustomerList(map);	
		
				
		return statList;
	}

	@Override
	public List<StatisticsCustomerVO> getMontlylStatisticsCustomerList(StatisticsCustomerVO param) {

		
		Map<String, Object> map = new HashMap<String, Object>();		

		map.put("searchCustomerId", param.getSearchCustomerId());			
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
		}	
				
		List<StatisticsCustomerVO> statList = statMapper.selectMontlylStatisticsCustomerList(map);	
		
				
		return statList;
	}

	@Override
	public int registerDailyStatisticsCustomer() {
		int result=0;
		try {
			result = statMapper.inserDailyStatisticsCustomer();
			if(result > 0) result = statMapper.deleteDailyStatisticsCustomer();	
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		
		return result;
	}

	@Override
	public int registerMonthlyStatisticsCustomer() {
		// TODO Auto-generated method stub
		return statMapper.inserMonthlyStatisticsCustomer();
	}

	@Override
	public List<StatisticsCustomerBottleVO> getStatisticsCustomerBottleList(StatisticsCustomerBottleVO param) {
		return statMapper.selectStatCustomerBottleList(param);
	}
	
	@Override
	public List<StatisticsCustomerBottleVO> getStatSalesCustomerBottleList(StatisticsCustomerBottleVO param) {
		return statMapper.selectStatSalesCustomerBottleList(param);
	}

	@Override
	public List<StatisticsCustomerVO> getStatSalesCustomerCount(StatisticsCustomerBottleVO param) {
		return statMapper.selectCountSalesCustomer(param);
	}
}

package com.gms.web.admin.service.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.statistics.StatisticsProductVO;
import com.gms.web.admin.mapper.statistics.StatisticsProductMapper;

@Service
public class StatisticsProductServiceImpl implements StatisticsProductService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatisticsProductMapper statMapper;

	@Override
	public List<StatisticsProductVO>  getDailylStatisticsProductList(StatisticsProductVO param) {
		logger.info("****** getDailylStatisticsProductList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		
		map.put("searchProductId", param.getSearchProductId());		
		map.put("productPriceSeq", param.getProductPriceSeq());	
//		logger.debug("****** getDailylStatisticsProductList *****productPriceSeq===*"+param.getProductPriceSeq());
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
//			logger.debug("****** getDailylStatisticsProductList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
//			logger.debug("****** getDailylStatisticsProductList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
//			logger.debug("****** getDailylStatisticsProductList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsProductVO> statList = statMapper.selectDailylStatisticsProductList(map);			
				
		return statList;
	}

	@Override
	public List<StatisticsProductVO>  getMontlylStatisticsProductList(StatisticsProductVO param) {
		logger.info("****** getMonthlyStatisticsProductList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		

		map.put("searchProductId", param.getSearchProductId());	
		map.put("productPriceSeq", param.getProductPriceSeq());	
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
//			logger.debug("****** getMonthlylStatisticsProductList *****getSearchStatDt===*"+param.getSearchStatDt());
		}		
		
		if(param.getSearchStatDtFrom() != null) {
			map.put("searchStatDtFrom", param.getSearchStatDtFrom());
//			logger.debug("****** getMonthlylStatisticsProductList *****getSearchStatDtFrom===*"+param.getSearchStatDtFrom());
		}
		
		if(param.getSearchStatDtEnd() != null) {
			map.put("searchStatDtEnd", param.getSearchStatDtEnd());
//			logger.debug("****** getMonthlylStatisticsProductList *****getSearchStatDtEnd===*"+param.getSearchStatDtEnd());
		}	
				
		List<StatisticsProductVO> statList = statMapper.selectMontlylStatisticsProductList(map);			
				
		return statList;
	}

	@Override
	public int registerDailyStatisticsProduct() {
		int result = 0;
		result = statMapper.inserDailyStatisticsProduct();
		return statMapper.deleteDailyStatProduct();
	}

	@Override
	public int registerMonthlyStatisticsProduct() {
		return statMapper.inserMonthlyStatisticsProduct();
	}
	
}

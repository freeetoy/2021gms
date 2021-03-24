package com.gms.web.admin.service.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;
import com.gms.web.admin.domain.statistics.StatisticsAgencyResultVO;
import com.gms.web.admin.domain.statistics.StatisticsAgencyVO;
import com.gms.web.admin.mapper.statistics.StatisticsAgencyMapper;

@Service
public class StatisticsAgencyServiceImpl implements StatisticsAgencyService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatisticsAgencyMapper statMapper;
	
	@Override
	public List<StatisticsAgencyResultVO> getDailylStatisticsAgencyList(StatisticsAgencyVO param) {
		logger.info("****** getDailylStatisticsAgencyList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();		

		List<StatisticsAgencyVO> agencyList = null;
		List<CustomerVO> customerList = null;
		List<ProductPriceSimpleVO> productList = null;
		
		if(param.getSearchStatDt() != null && param.getSearchStatDt().length() > 0) {
			map.put("searchStatDt", param.getSearchStatDt());
			logger.debug("****** getDailylStatisticsAgencyList *****getSearchStatDt===*"+param.getSearchStatDt());
			agencyList = statMapper.selectDailylStatisticsAgencyList(map);	
			customerList = statMapper.selectStatisticsAgencyCustomerList(map);	
			productList = statMapper.selectStatisticsAgencyProductList(map);	
		}else {
			agencyList = statMapper.selectTodayStatisticsAgencyList();	
			customerList = statMapper.selectTodayStatisticsAgencyCustomerList();	
			productList = statMapper.selectTodayStatisticsAgencyProductList();	
		}
			
		//List<CustomerSimpleVO> customerList = statMapper.selectStatisticsAgencyCustomerList(map);			
		//List<ProductPriceSimpleVO> productList = statMapper.selectStatisticsAgencyProductList(map);	
		//List<StatisticsAgencyVO> agencyList = statMapper.selectDailylStatisticsAgencyList(map);	
		
		StatisticsAgencyResultVO statResult = new StatisticsAgencyResultVO();
		List<StatisticsAgencyResultVO> statList = new ArrayList<StatisticsAgencyResultVO>();	
		List<Integer> bottleOwnCountList1 = new ArrayList<Integer>();
		//Integer[] customerIdList = new Integer[customerList.size()];
		for(int i =0 ; i < productList.size() ; i++) {
			statResult = new StatisticsAgencyResultVO();
			statResult.setProductId(productList.get(i).getProductId());
			statResult.setProductNm(productList.get(i).getProductNm());
			statResult.setProductPriceSeq(productList.get(i).getProductPriceSeq());
			statResult.setProductCapa(productList.get(i).getProductCapa());
			
			Integer[] countOwnList = new Integer[customerList.size()];
			Integer[] countRentList = new Integer[customerList.size()];
			
			for(int k =0 ; k < customerList.size() ; k++) {	
				
				int countOwn = 0 ;
				int countRent = 0;
				for(int j =0 ; j < agencyList.size() ; j++) {	
					
					if(agencyList.get(j).getCustomerId()- customerList.get(k).getCustomerId() == 0 	) {
						//logger.debug("&&&agencyList.customerId ="+agencyList.get(j).getCustomerId());
						//logger.debug("&&&customerList.customerId ="+customerList.get(k).getCustomerId());
						//logger.debug("==customerList.get(k).getParentCustomerId() =*"+customerList.get(k).getParentCustomerId());
				
						if(statResult.getProductId() == agencyList.get(j).getProductId() 
								&& statResult.getProductPriceSeq() == agencyList.get(j).getProductPriceSeq()) {
								/*
							logger.debug("==statResult.getProductId =*"+statResult.getProductId());	
							//logger.debug(" statResult.getProductNm =*"+statResult.getProductNm());	
							logger.debug(" statResult.getProductPriceSeq =*"+statResult.getProductPriceSeq());	
							//logger.debug("== statResult.getProductCapa =*"+statResult.getProductCapa());	
							
							logger.debug(" agencyList.getProductId =*"+agencyList.get(j).getProductId());				
							logger.debug(" agencyList.getProductPriceSeq =*"+agencyList.get(j).getProductPriceSeq());
							*/
							
							countOwnList[k] = agencyList.get(j).getBottleOwnCount();
							countRentList[k] = agencyList.get(j).getBottleRentCount();
							
						}			
					}
				}				
			}
			
			for(int  j=0; j< countOwnList.length ;  j++) {
				if(countOwnList[j] == null) {
					countOwnList[j] =0;
					bottleOwnCountList1.add(0);
				}else {
					bottleOwnCountList1.add(countOwnList[j]);
				}
				if(countRentList[j] == null) countRentList[j] =0;
			}
			statResult.setBottleOwnCountList(countOwnList);
			statResult.setBottleRentCountList(countRentList);
			statResult.setBottleOwnCountList1(bottleOwnCountList1);
			statList.add(statResult);
		}		
		/*
		for(int i =0 ; i < statList.size() ; i++ ) {
			//StatisticsAgencyVO statAgency = statList.get(i).getStatAgency();
			Integer[] countList  = statList.get(i).getBottleOwnCountList();
			Integer[] countList1  = statList.get(i).getBottleRentCountList();			
			
			logger.debug(" statList.get(i).getProductNm =*"+statList.get(i).getProductNm());					
			logger.debug(" statList.get(i).getProductCapa =*"+statList.get(i).getProductCapa());				
			logger.debug(" statList.get(i).get =*"+statList.get(i).getProductNm());	
			for(int j =0 ; j < countList.length ; j++) {
				logger.debug(" statAgency.countList =="+countList[j]);		
				logger.debug(" statAgency.countList1 =="+countList1[j]);
			}
					
		}
		*/
		//statList.setCustomerList(customerList);
		//statList.setProductList(productList);
		//statList.setStatAgencyList(agencyList);
		
		return statList;
	}

	@Override
	public List<StatisticsAgencyResultVO> getMontlylStatisticsAgencyList(StatisticsAgencyVO param) {

		logger.info("****** getMontlylStatisticsAgencyList *****start===*");	
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
		}				
			
		List<CustomerVO> customerList = statMapper.selectStatisticsAgencyCustomerList(map);			
		List<ProductPriceSimpleVO> productList = statMapper.selectStatisticsAgencyProductList(map);	
		List<StatisticsAgencyVO> agencyList = statMapper.selectMontlylStatisticsAgencyList(map);	
		
		StatisticsAgencyResultVO statResult = new StatisticsAgencyResultVO();
		List<StatisticsAgencyResultVO> statList = new ArrayList<StatisticsAgencyResultVO>();	
		List<Integer> bottleOwnCountList1 = new ArrayList<Integer>();
		
		for(int i =0 ; i < productList.size() ; i++) {
			statResult = new StatisticsAgencyResultVO();
			statResult.setProductId(productList.get(i).getProductId());
			statResult.setProductNm(productList.get(i).getProductNm());
			statResult.setProductPriceSeq(productList.get(i).getProductPriceSeq());
			statResult.setProductCapa(productList.get(i).getProductCapa());
			
			Integer[] countOwnList = new Integer[customerList.size()];
			Integer[] countRentList = new Integer[customerList.size()];
			
			for(int k =0 ; k < customerList.size() ; k++) {					
				
				for(int j =0 ; j < agencyList.size() ; j++) {	
					
					if(agencyList.get(j).getCustomerId()- customerList.get(k).getCustomerId() == 0) {
						
						if(statResult.getProductId() == agencyList.get(j).getProductId() 
								&& statResult.getProductPriceSeq() == agencyList.get(j).getProductPriceSeq()) {
							
							countOwnList[k] = agencyList.get(j).getBottleOwnCount();
							countRentList[k] = agencyList.get(j).getBottleRentCount();							
						}			
					}
				}				
			}
			
			for(int  j=0; j<countOwnList.length ;  j++) {
				if(countOwnList[j] == null) {
					countOwnList[j] =0;
					bottleOwnCountList1.add(0);
				}else {
					bottleOwnCountList1.add(countOwnList[j]);
				}
				if(countRentList[j] == null) countRentList[j] =0;
			}
			statResult.setBottleOwnCountList(countOwnList);
			statResult.setBottleRentCountList(countRentList);
			statResult.setBottleOwnCountList1(bottleOwnCountList1);
			statList.add(statResult);
		}		
		/*
		for(int i =0 ; i < statList.size() ; i++ ) {
			//StatisticsAgencyVO statAgency = statList.get(i).getStatAgency();
			Integer[] countList  = statList.get(i).getBottleOwnCountList();
			Integer[] countList1  = statList.get(i).getBottleRentCountList();
			
			logger.debug(" statList.get(i).getProductId =*"+statList.get(i).getProductId());	
			logger.debug(" statList.get(i).getProductNm =*"+statList.get(i).getProductNm());	
			logger.debug(" statList.get(i).getProductPriceSeq =*"+statList.get(i).getProductPriceSeq());	
			logger.debug(" statList.get(i).getProductCapa =*"+statList.get(i).getProductCapa());	
			
			for(int j =0 ; j < countList.length ; j++) {
				logger.debug(" statAgency.countList =="+countList[j]);		
				logger.debug(" statAgency.countList1 =="+countList1[j]);
			}			
		}
		*/
		
		
		return statList;
	}

	@Override
	public int registerDailyStatisticsAgency() {
		
		return statMapper.insertDailyStatisticsAgency();
	}

	@Override
	public int registerMonthlyStatisticsAgency() {
		
		return statMapper.insertMonthlyStatisticsAgency();
	}

	@Override
	public List<CustomerVO> getStatisticsAgencyCustomerList(StatisticsAgencyVO param) {
		Map<String, Object> map = new HashMap<String, Object>();		

		//map.put("searchCustomerId", param.getSearchCustomerId());	
		
		if(param.getSearchStatDt() != null) {
			map.put("searchStatDt", param.getSearchStatDt());
			//logger.debug("****** getDailylStatisticsAgencyList *****getSearchStatDt===*"+param.getSearchStatDt());
		}				
			
		List<CustomerVO> customerList = null;
		if(param.getSearchStatDt() != null && param.getSearchStatDt().length() > 0) 
			customerList = statMapper.selectStatisticsAgencyCustomerList(map);
		else
			customerList = statMapper.selectTodayStatisticsAgencyCustomerList();
		return customerList;
		
	}

	

}

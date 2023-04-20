package com.gms.web.admin.service.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.statistics.StatisticsAgencyResultVO;
import com.gms.web.admin.domain.statistics.StatisticsAgencyResultVO2;
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
		//List<StatisticsAgencyVO> agencyChildList = statMapper.selectTodayStatisticsAgencyChildeList();	
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
		
		List<StatisticsAgencyVO> childAgencyList = statMapper.selectTodayStatisticsAgencyChildeList();	
		logger.debug(" statList.gcustomerList.size() =*"+customerList.size());
		//Integer[] customerIdList = new Integer[customerList.size()];
		for(int i =0 ; i < productList.size() ; i++) {
			statResult = new StatisticsAgencyResultVO();
			statResult.setProductId(productList.get(i).getProductId());
			statResult.setProductNm(productList.get(i).getProductNm());
			statResult.setProductPriceSeq(productList.get(i).getProductPriceSeq());
			statResult.setProductCapa(productList.get(i).getProductCapa());
			ProductPriceSimpleVO simpleProduct = productList.get(i);
			Integer[] countOwnList = new Integer[customerList.size()];
			Integer[] countRentList = new Integer[customerList.size()];
			
			for(int k =0 ; k < customerList.size() ; k++) {					
				CustomerVO customer = customerList.get(k);
				countOwnList[k]  = 0;
				for(int j =0 ; j < agencyList.size() ; j++) {	
					
					if(agencyList.get(j).getCustomerId()- customerList.get(k).getCustomerId() == 0 	) {
						
						if(statResult.getProductId() == agencyList.get(j).getProductId() 
								&& statResult.getProductPriceSeq() == agencyList.get(j).getProductPriceSeq()) {						
							
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
		
//		for(int i =0 ; i < statList.size() ; i++ ) {
//			//StatisticsAgencyVO statAgency = statList.get(i).getStatAgency();
//			Integer[] countList  = statList.get(i).getBottleOwnCountList();
//			Integer[] countList1  = statList.get(i).getBottleRentCountList();			
//			
//			logger.debug(" statList.get(i).getProductNm =*"+statList.get(i).getProductNm());					
//			logger.debug(" statList.get(i).getProductCapa =*"+statList.get(i).getProductCapa());				
//			logger.debug(" statList.get(i).get =*"+statList.get(i).getProductNm());	
//			
//			for(int j =0 ; j < countList.length ; j++) {
//				logger.debug(" statAgency.countList =="+countList[j]);		
//				logger.debug(" statAgency.countList1 =="+countList1[j]);
//			}
//					
//		}
		
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
				if(countRentList[j] == null) countRentList[j] = 0;
			}
			//statResult.setBottleOwnCountList(countOwnList);
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
		int result =0;
		try {
			result = statMapper.insertDailyStatisticsAgency();	
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		 
		 return result;
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

	@Override
	public Map<String, Object> getDailylStatisticsAgencyList1(StatisticsAgencyVO param) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		StatisticsAgencyResultVO2 statResult = null;
		
		List<StatisticsAgencyVO> agencyList = null;
		List<StatisticsAgencyVO> agencyChildList = null;
		List<CustomerVO> customerList = null;
		List<ProductPriceSimpleVO> productList = null;				
		
		customerList = statMapper.selectTodayStatisticsAgencyCustomerList1();	
		productList = statMapper.selectTodayStatisticsAgencyProductList();	
		agencyList = statMapper.selectTodayStatisticsAgencyList1();	
		
		agencyChildList = statMapper.selectTodayStatisticsAgencyChildeList();	
		
		List<StatisticsAgencyResultVO2> statList = new ArrayList<StatisticsAgencyResultVO2>();	
		
		for(int i =0 ; i < productList.size() ; i++) {
			ProductPriceSimpleVO simpleProduct = productList.get(i);
			int[] countOwnList = new int[customerList.size()];
			int[] countRentList = new int[customerList.size()];
			statResult = new StatisticsAgencyResultVO2();
			statResult.setProductId(productList.get(i).getProductId());
			statResult.setProductNm(productList.get(i).getProductNm());
			statResult.setProductPriceSeq(productList.get(i).getProductPriceSeq());
			statResult.setProductCapa(productList.get(i).getProductCapa());
			
			for(int k =0 ; k < customerList.size() ; k++) {					
				CustomerVO customer = customerList.get(k);
				countOwnList[k] = 0;
				for(int j =0 ; j < agencyList.size() ; j++) {	
					StatisticsAgencyVO agencyInfo = agencyList.get(j);
					if(agencyInfo.getCustomerId()- customer.getCustomerId() == 0 	) {						
						if(simpleProduct.getProductId() == agencyInfo.getProductId() 
								&& simpleProduct.getProductPriceSeq() == agencyInfo.getProductPriceSeq()) {						
							
							countOwnList[k] = agencyInfo.getBottleOwnCount();	
							countRentList[k] = agencyInfo.getBottleRentCount();	
						}			
					}					
				}		
			
				for(int m =0 ; m < agencyChildList.size() ; m++) {	
					StatisticsAgencyVO agencyChild = agencyChildList.get(m);
					//logger.debug(" countOwn agencyChild.getBottleOwnCount()1 ="+agencyChild.getBottleOwnCount());
					if(agencyChild.getParentCustomerId()- customer.getCustomerId() == 0  && simpleProduct.getProductId() == agencyChild.getProductId() 
							&& simpleProduct.getProductPriceSeq() == agencyChild.getProductPriceSeq()	) {				
						
						countOwnList[k] += agencyChild.getBottleOwnCount();	
						countRentList[k] += agencyChild.getBottleRentCount();	
					}
				}
				
			}
			statResult.setBottleOwnCountList(countOwnList);
			statResult.setBottleRentCountList(countRentList);
			statList.add(statResult);
		}
		
//		for(int k =0 ; k < customerList.size() ; k++) {
//			CustomerVO customer = customerList.get(k);
//			for(int j =0 ; j < productList.size() ; j++){
//				ProductPriceSimpleVO simpleProduct = productList.get(j);
//				for(int m =0 ; m < agencyChildList.size() ; m++) {	
//					StatisticsAgencyVO agencyChild = agencyChildList.get(m);
//					//logger.debug(" countOwn agencyChild.getBottleOwnCount()1 ="+agencyChild.getBottleOwnCount());
//					if(agencyChild.getParentCustomerId()- customer.getCustomerId() == 0  && simpleProduct.getProductId() == agencyChild.getProductId() 
//							&& simpleProduct.getProductPriceSeq() == agencyChild.getProductPriceSeq()	) {						
//									
//						//logger.debug(" countOwn agencyChild.getBottleOwnCount() ="+agencyChild.getBottleOwnCount());
//						countOwnList[j] += agencyChild.getBottleOwnCount();							
//							
//					}
//				}
//				logger.debug(" countOwn="+customerList.get(k).getCustomerNm());
//				logger.debug(" countOwn="+countOwnList[j]);
//			}
//		}
		map.put("statAgency",statList);
		map.put("customerList",customerList);
		return map;
	}


}

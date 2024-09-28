package com.gms.web.admin.controller.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.BottleHistoryVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CustomerLn2AlarmVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.SimpleBottleVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.service.common.ApiService;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.ProductService;

@Controller
public class ApiController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
//	private final Logger logger1 = LoggerFactory.getLogger("ROLLING_FILE1");
	
	private final int CUSOTMER_NOT_EXIST = -3;
	private final int USER_NOT_EXIST = -4;
	/*
	 * Service 빈(Bean) 선언
	 */
	@Autowired
	private ApiService apiService;
	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private ProductService productService;
	
	@RequestMapping(value = "/api/controlAction.do")
	@ResponseBody
	public String controlAction(String userId, String bottles, String customerNm, String bottleType, String bottleWorkCd,String workEtc)	{	
		Long startTime = System.currentTimeMillis();		
		logger.info("=======================================================================================");
		logger.info("<<<< controlAction  userId="+userId+" : bottles ="+bottles +": bottleType ="+ bottleType + ": bottleWorCd ="+bottleWorkCd+" : customerNm ="+customerNm+" : workEtc ="+workEtc);
		
		boolean phoneCall = true;
		int result = 0;		
		
		try {
			WorkReportVO workReport = new WorkReportVO();	
			
			workReport.setBottleType(bottleType);
			workReport.setBottlesIds(bottles);		//BottleBarCd 모음
			workReport.setCustomerNm(customerNm);		
			workReport.setPhoneFlag(phoneCall);
			
			workReport.setCreateId(userId);		
			workReport.setUserId(userId);
			workReport.setUpdateId(userId);	
			workReport.setReportEtc(workEtc);
			
			if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.come")) ) {			//입고
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.come"));
				result = apiService.registerWorkReportForSale(workReport);
				
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.out"))) {		//출고
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.out"));
				result = apiService.registerWorkReportForSale(workReport);
				
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.incar"))) {		// 상차
				
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.incar"));
				result = apiService.registerWorkReportForChangeCd(workReport);
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.charge"))) {		//충전
				
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.charge"));
				result = apiService.registerWorkReportForChangeCd(workReport);
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.sales"))) {		//판매
				
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));			
				result = apiService.registerWorkReportForSale(workReport);
				
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.rental"))) {		//대여
				
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.rent"));			
				result = apiService.registerWorkReportForSale(workReport);
				
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.back"))) {			//회수
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.back"));
				result = apiService.registerWorkReportForChangeCd(workReport);
																		
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.freeback"))) {			//무료회수
//				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.freeback"));
				//20221101 처리
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.back"));
				result = apiService.registerWorkReportForChangeCd(workReport);
				
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.buyback"))) {			//매입
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.buyback"));
				result = apiService.registerWorkReportForChangeCd(workReport);
				
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.chargeDt"))) {			//충전기한확인
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.chargeDt"));
				result = apiService.registerWorkReportForChangeCd(workReport);
				
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.vacuum"))) {			//진공배기
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.vacuum"));
				result = apiService.registerWorkReportForChangeCd(workReport);
				
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.hole"))) {			//누공확인
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.hole"));
				result = apiService.registerWorkReportForChangeCd(workReport);
				
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.freechange"))) {			//무상교체
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.freechange"));
				result = apiService.registerWorkReportForChangeCd(workReport);
				
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.salesgas"))) {			//가스판매
				
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesgas"));			
				result = apiService.registerWorkReportForSale(workReport);			
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.salesBack"))) {			//판매회수
				
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesBack"));			
				result = apiService.registerWorkReportForChangeCd(workReport);
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.agencyRent"))) {			//공장대여
				
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.agencyRent"));			
				result = apiService.registerWorkReportForSale(workReport);			
			}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.agencyBack"))) {			//대여회수
				
				workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.agencyBack"));			
				result = apiService.registerWorkReportForChangeCd(workReport);				
			}
		} catch (Exception e) {		
			logger.error("controlAction ", e.toString());
			return "fail";
		}
		
		Long endTime = System.currentTimeMillis();
		logger.info("afterCompletion!! =userId="+userId+" : bottles ="+bottles +" "+(endTime-startTime)+" millis");
		if(result > 0)
			return "success";
		if(result == USER_NOT_EXIST)
			return "noUser";
		else
			return "fail";
		//return null;
	}

	
	@RequestMapping(value = "/api/controlActionNoGas.do")
	@ResponseBody
	public String controlActionNoGas(WorkBottleVO param )	{	
		Long startTime = System.currentTimeMillis();			
//		logger1.info("<<<< controlActionNoGas userId="+param.getUserId()+" : productId ="+param.getProductId() +": productPriceSeq ="+ param.getProductPriceSeq() + " : customerNm ="+param.getCustomerNm() + " : productCount ="+param.getProductCount());
		logger.info("=======================================================================================");
		logger.info("<<<<  controlActionNoGas userId="+param.getUserId()+" : productId ="+param.getProductId() +": productPriceSeq ="+ param.getProductPriceSeq() + " : customerNm ="+param.getCustomerNm() + " : productCount ="+param.getProductCount());
		
		boolean phoneCall = true;
		int result = 1;		
		
		param.setCreateId(param.getUserId());	
		param.setUpdateId(param.getUserId());
		param.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));		
		
		//workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.come"));
		result = apiService.registerWorkReportNoGas(param);			
		Long endTime = System.currentTimeMillis();
		logger.info("afterCompletion!! =userId="+param.getUserId()+" : productId ="+param.getProductId() +" : productPriceSeq ="+param.getProductPriceSeq() +" "+(endTime-startTime)+" millis");
		
		if(result > 0)
			return "success";
		if(result == USER_NOT_EXIST)
			return "noUser";
		else
			return "fail";
		//return null;
	}
	
	@RequestMapping(value = "/api/controlCashFlow.do")
	@ResponseBody
	public String manageCashFlow(CashFlowVO param )	{	
		Long startTime = System.currentTimeMillis();	
		
		logger.info("=======================================================================================");
		logger.info("<<<<manageCashFlow userId="+param.getCreateId()+" : incomeAmount ="+param.getIncomeAmount() +": receivableAmount ="+ param.getReceivableAmount() + " : customerNm ="+param.getCustomerNm() + " : incomeWay ="+param.getIncomeWay());
	
		int result = 0;
		
		result = apiService.registerCashFlow(param);
		
		Long endTime = System.currentTimeMillis();
		logger.info("afterCompletion!! =" +(endTime-startTime)+" millis");
		if(result > 0)
			return "success";
		if(result == USER_NOT_EXIST)
			return "noUser";
		else
			return "fail";
		
		//return null;
	}
	
	
	@RequestMapping(value = "/api/controlTank.do")
	@ResponseBody
	public String controlTank(WorkBottleVO param )	{
		Long startTime = System.currentTimeMillis();	
		logger.info("=======================================================================================");
		logger.info("<<<<  controlTank userId="+param.getUserId()+" : productId ="+param.getProductId() +" : productPriceSeq ="+param.getProductPriceSeq() + " : customerNm ="+param.getCustomerNm() + " : BottleWorkCd ="+param.getBottleWorkCd()+ " : productCount ="+param.getProductCount());
		
		int result = 0;
		
		param.setCreateId(param.getUserId());	
		param.setUpdateId(param.getUserId());
		/*
		if(PropertyFactory.getProperty("common.bottle.status.title.sales").equals(param.getBottleWorkCdNm())) 
			param.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));
		else
			param.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.tcharge"));
		*/
		
		result = apiService.registerWorkReportTank(param);	
		
		Long endTime = System.currentTimeMillis();
		logger.info("afterCompletion!! =userId="+param.getUserId()+" : productId ="+param.getProductId() +" : productPriceSeq ="+param.getProductPriceSeq() +" "+(endTime-startTime)+" millis");
		if(result > 0)
			return "success";
		if(result == USER_NOT_EXIST)
			return "noUser";
		else
			return "fail";
		
		//return null;
	}
	
	
	@RequestMapping(value = "/api/customerBottle.do")
	@ResponseBody
	public List<SimpleBottleVO> getCustomerSimpleBottleList(String customerNm)	{			
		
		return apiService.getCustomerSimpleBottleList(customerNm);
	}
	
	
	@RequestMapping(value = "/api/controlGasAndBottle.do")
	@ResponseBody
	public String manageGasAndBottle(String userId, String bottles, String customerNm, String bottleType, String bottleWorkCd )	{	
		Long startTime = System.currentTimeMillis();	
		logger.info("=======================================================================================");
		logger.info("<<<<  manageGasAndBottle userId="+userId+" : bottles ="+bottles +": bottleType ="+ bottleType + ": bottleWorCd ="+bottleWorkCd+" : customerNm ="+customerNm);
//		logger1.info("<<<< manageGasAndBottle","userId="+userId+" : bottles ="+bottles +": bottleType ="+ bottleType + ": bottleWorCd ="+bottleWorkCd+" : customerNm ="+customerNm);
				
		int result = 0;
		boolean phoneCall = true;
		
		WorkReportVO workReport = new WorkReportVO();	
		
		workReport.setBottleType(bottleType);
		workReport.setBottlesIds(bottles);		//BottleBarCd 모음
		workReport.setCustomerNm(customerNm);		
		workReport.setPhoneFlag(phoneCall);
		workReport.setCreateId(userId);				
		workReport.setUpdateId(userId);				
		
		Long endTime = System.currentTimeMillis();
		logger.info("afterCompletion!! =userId="+userId+" : bottles ="+bottles +"  " +(endTime-startTime)+" millis");
		if(result > 0)
			return "success";
		if(result == USER_NOT_EXIST)
			return "noUser";
		else
			return "fail";
		//return null;
	}
	
	@RequestMapping(value = "/api/bottleHistoryList.do")
	@ResponseBody
	public List<BottleHistoryVO> getBottleHistoryList(String bottleBarCd)	{			
		
		return bottleService.selectBottleHistoryList(bottleBarCd);
	}
	
	@RequestMapping(value = "/api/workReportList.do")
	@ResponseBody
	public List<WorkBottleVO> getWorkBottleList(String userId)	{			
		logger.debug("getWorkBottleList","userId="+userId);
		WorkReportVO workReport = new WorkReportVO();
		
		workReport.setSearchUserId(userId);
		workReport.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		
		return apiService.getWorkReportList(workReport);
	}
	
	@RequestMapping(value = "/api/controlMassAction.do")
	@ResponseBody
	public String controlMassAction(String userId, String bottles, String customerNm, String bottleType, String bottleWorkCd)	{	
		Long startTime = System.currentTimeMillis();		
		logger.info("=======================================================================================");
		logger.info("<<<< controlMassAction  userId="+userId+" : bottles ="+bottles +" : bottleType ="+ bottleType + ": bottleWorCd ="+bottleWorkCd+" : customerNm ="+customerNm +"/n*******");
		
		int result = 1;		
		boolean phoneCall = true;
		
		WorkReportVO workReport = new WorkReportVO();	
		
		workReport.setBottleType(bottleType);
		workReport.setBottlesIds(bottles);		//BottleBarCd 모음
		workReport.setCustomerNm(customerNm);		
		workReport.setPhoneFlag(phoneCall);
		workReport.setMultiYn("Y");
		workReport.setCreateId(userId);		
		workReport.setUserId(userId);
		workReport.setUpdateId(userId);		
		
		if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.masssales"))) {		//판매
			
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));		
			workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));	
			result = apiService.registerWorkReportMassForSale(workReport);
			
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.massrental"))) {		//대여
			
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.rent"));	
			workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.rent"));	
			result = apiService.registerWorkReportMassForSale(workReport);
			
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.massback"))) {			//판매회수
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesBack"));
			workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.salesBack"));	
			result = apiService.registerWorkReportMassForChangeCd(workReport);
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.agencyBack"))) {			//대여회수
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.back"));
			workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.back"));	
			result = apiService.registerWorkReportMassForChangeCd(workReport);		
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.massCome"))) {			//대량입고
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.come"));
			workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.come"));	
			result = apiService.registerWorkReportMassForSale(workReport);		
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.massOut"))) {			//대량출고
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.out"));
			workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.out"));	
			result = apiService.registerWorkReportMassForSale(workReport);		
		}
		
		Long endTime = System.currentTimeMillis();
		logger.info("afterCompletion!! = userId="+userId+" : bottles ="+bottles +" " +(endTime-startTime)+" millis");
		if(result > 0)
			return "success";
		if(result == USER_NOT_EXIST)
			return "noUser";
		else
			return "fail";
	}
	
	
	@RequestMapping(value = "/api/dummyList.do")
	@ResponseBody
	public List<BottleVO> getDummyBottleList()	{			
		logger.debug("getDummyBottleList===");
		
		return apiService.getDummyBottleList();
	}
	
	
	@RequestMapping(value = "/api/bottleDetail.do")
	@ResponseBody
	public BottleVO getBottleDetail(String bottleBarCd)	{				
		logger.info("getBottleDetail bottleBarCd="+bottleBarCd);
		try {
			BottleVO bottle =  bottleService.getBottleDetailForBarCd(bottleBarCd);			
			
			if(bottle!=null) {
				if(bottle.getBottleCapa() == null || (bottle.getBottleCapa()!=null && bottle.getBottleCapa().length() == 0)) {
					bottle.setBottleCapa("-");
					bottle.setChargeCapa("-");
				}
				
				bottle.setSuccess(true);
			}
			else {
				bottle = new BottleVO();
				bottle.setSuccess(false);
			}
			return bottle;
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("====* getBottleDetail bottleBarCd== "+bottleBarCd);
			return null;
		}
	}
	
	@RequestMapping(value = "/api/appVersion.do")
	@ResponseBody
	public String getAppVersion()	{			
		
		return apiService.getAppVersion();
	}
	
	@RequestMapping(value = "/api/customerLn2List.do")
	@ResponseBody
	public List<ProductPriceSimpleVO> getProductPriceListOfNoGas()	{	
		
		
		List<ProductPriceSimpleVO> productList = apiService.getCustomerLn2List();
		//model.addAttribute("productList", productList);
		
		return productList;
	}
	
		
	@RequestMapping(value = "/api/customerBottleList.do")
	@ResponseBody
	public List<CustomerProductVO> getCustomerBottleList(String customerNm)	{	
		
		
		List<CustomerProductVO> bottleList = apiService.getCustomerBottleList(customerNm);
		//model.addAttribute("productList", productList);
		
		return bottleList;
	}
	
	@RequestMapping(value = "/api/deleteWorkBottle.do")
	@ResponseBody
	public String deleteWorkBottle(String param)	{
		logger.info("###################################################");
		logger.info("deleteWorkBottle==="+param);
		logger.info("###################################################");
		List<String> list = null;
		int result = 0;		
		WorkBottleVO workBottle = new WorkBottleVO();		
		
		if(param !=null && param.length() > 0) {
			//bottleIds= request.getParameter("bottleIds");
			list = StringUtils.makeForeach(param, ";"); 		
			
			int idx=0;
			if(list.size() == 4) {
				workBottle.setWorkReportSeq(Integer.parseInt(list.get(idx++)));
				workBottle.setProductId(Integer.parseInt(list.get(idx++)));
				workBottle.setProductPriceSeq(Integer.parseInt(list.get(idx++)));
				workBottle.setBottleWorkCd(list.get(idx++));
				
				result = apiService.deleteWorkBottle(workBottle);
			}
		}		
		
		if(result > 0)
			return "success";
		else
			return "fail";
	}
	
	@RequestMapping(value = "/api/deleteWorkBottleNew.do")
	@ResponseBody
	public String deleteWorkBottleNew(String userId,String param)	{
		logger.info("###################################################");
		logger.info("deleteWorkBottle==="+param+"-==userId="+userId);
		logger.info("###################################################");
		List<String> list = null;
		int result = 0;		
		WorkBottleVO workBottle = new WorkBottleVO();		
		workBottle.setUserId(userId);
		workBottle.setUpdateId(userId);
		
		if(param !=null && param.length() > 0) {
			//bottleIds= request.getParameter("bottleIds");
			list = StringUtils.makeForeach(param, ";"); 		
			
			int idx=0;
			if(list.size() == 4) {
				workBottle.setWorkReportSeq(Integer.parseInt(list.get(idx++)));
				workBottle.setProductId(Integer.parseInt(list.get(idx++)));
				workBottle.setProductPriceSeq(Integer.parseInt(list.get(idx++)));
				workBottle.setBottleWorkCd(list.get(idx++));
				
				result = apiService.deleteWorkBottle(workBottle);
			}
		}		
		
		if(result > 0)
			return "success";
		else
			return "fail";
	}
	
	@RequestMapping(value = "/api/allProductSimpleList.do")
	@ResponseBody
	public List<ProductPriceSimpleVO> getAllProductSimpleList()	{	
						
		return productService.getAllProductSimpleList() ;
	}
	
	@RequestMapping(value = "/api/registerOrder.do")
	@ResponseBody
	public String registerOrderApi(OrderVO param)	{	
		int result = 0;		
				
		result = apiService.registerOrder(param);
		
		if(result > 0)
			return "success";
		else
			return "fail";
	}
	
	
	@RequestMapping(value = "/api/orderList.do")
	@ResponseBody
	public List<OrderVO> getOrderList(OrderVO param)	{	
						
		return apiService.getOrderList(param) ;
	}
	
	@RequestMapping(value = "/api/orderProductList.do")
	@ResponseBody
	public List<OrderProductVO> getOrderProductList(OrderVO param)	{	
						
		return apiService.getOrderProductList(param) ;
	}
	
	@RequestMapping(value = "/api/deleteOrder.do")
	@ResponseBody
	public String deleteOrderApi(OrderVO param)	{	
		int result = 0;		
		logger.info("<<<< deleteOrderApi  userId="+param.getUpdateId());
		result = apiService.deleteOrder(param);
		
		if(result > 0)
			return "success";
		else
			return "fail";
	}
	
	@RequestMapping(value = "/api/ln2CustomerList.do")
	@ResponseBody
	public List<WorkBottleVO> getDeliveredLn2CustomerList(String createId)	{	
		
		List<WorkBottleVO> customerList = null;
		if(PropertyFactory.getProperty("ln2.alarm.UseYN").equals("Y"))
				customerList = apiService.deliveredLn2CustomerList(createId);
		
//		logger.info("getDeliveredLn2CustomerList createId="+createId+"===customerList ="+customerList.size());
		//model.addAttribute("productList", productList);
		
		return customerList;
	}
	
	@RequestMapping(value = "/api/ln2CustomerListNew.do")
	@ResponseBody
	public List<CustomerLn2AlarmVO> getDeliveredLn2CustomerListNew(String salesId)	{	
		
		List<CustomerLn2AlarmVO> customerList = null;
		if(PropertyFactory.getProperty("ln2.alarm.UseYN").equals("Y"))
				customerList = apiService.deliveredLn2CustomerListNew(salesId);
		
//		for(int i =0 ; i < customerList.size() ; i++) {
//			CustomerLn2AlarmVO customerAlarm = customerList.get(i);
//			logger.debug("###customerAlarm.getPeriodCd=######"+customerAlarm.getPeriodCd());
//			logger.debug("###customerAlarm.getWorkDt=######"+customerAlarm.getWorkDt());
//			logger.debug("###customerAlarm.getDayOfWeek=######"+customerAlarm.getDayOfWeek());
//			Calendar cal = Calendar.getInstance();
//
//			cal.setTime(customerAlarm.getWorkDt());	
//			
//			int dayOfWeek = customerAlarm.getDayOfWeek();
//			
//			if(customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.1week"))) {
//				
//				cal.add(Calendar.DATE, 7);
//				customerAlarm.setWorkDt(cal.getTime());
//
////				logger.debug("customerAlarm.getWorkDt=1week)"+customerAlarm.getWorkDt());
//
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.2week"))) {
//				cal.add(Calendar.DATE, 14);
//				customerAlarm.setWorkDt(cal.getTime());
//
////				logger.debug("customerAlarm.getWorkDt=2week)"+customerAlarm.getWorkDt());
//				
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.3week"))) {
//				cal.add(Calendar.DATE, 21);
//				customerAlarm.setWorkDt(cal.getTime());
//
////				logger.debug("customerAlarm.getWorkDt=3week)"+customerAlarm.getWorkDt());
//				
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.1month"))) {
//				LocalDate localDate = new java.sql.Date(customerAlarm.getWorkDt().getTime()).toLocalDate();
//				
//				localDate= localDate.plusMonths(1);
//				customerAlarm.setWorkDt(java.sql.Date.valueOf(localDate));
//				
////				logger.debug("customerAlarm.getWorkDt=1month)"+customerAlarm.getWorkDt());
//				
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.1m1w"))) {
//				cal.add(Calendar.MONTH,1);
//				cal.set(Calendar.DAY_OF_MONTH, 1);
//								
//				int year = cal.get(Calendar.YEAR);
//				int month = cal.get(Calendar.MONTH)+1;
//
//				int firstMonday = Integer.parseInt(DateUtils.getWeekInMonths(Integer.toString(year),Integer.toString(month)));
//				int day = 0;
//				if(dayOfWeek == 2) day = firstMonday;	//월요일
//				else if(dayOfWeek == 3) day = firstMonday+1;		//화요일
//				else if(dayOfWeek == 4) day = firstMonday+2;		//수요일
//				else if(dayOfWeek == 5) day = firstMonday+3;		//목요일
//				else if(dayOfWeek == 6) day = firstMonday+4;		//금요일
//				else if(dayOfWeek == 7) day = firstMonday+5;		//토요일
//				
//				cal.set(Calendar.DAY_OF_MONTH, day);
//				
//				customerAlarm.setWorkDt(cal.getTime());
////				logger.debug("after ###customerAlarm.getWorkDt=######"+customerAlarm.getWorkDt());
//				
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.1m2w"))) {
//				
//				cal.add(Calendar.MONTH,1);
//				cal.set(Calendar.DAY_OF_MONTH, 1);
//				
//				int year = cal.get(Calendar.YEAR);
//				int month = cal.get(Calendar.MONTH)+1;
//			
//				int firstMonday = Integer.parseInt(DateUtils.getWeekInMonths(Integer.toString(year),Integer.toString(month)))+7;
//				int day = 0;
//				if(dayOfWeek == 2) day = firstMonday;	//월요일
//				else if(dayOfWeek == 3) day = firstMonday+1;		//화요일
//				else if(dayOfWeek == 4) day = firstMonday+2;		//수요일
//				else if(dayOfWeek == 5) day = firstMonday+3;		//목요일
//				else if(dayOfWeek == 6) day = firstMonday+4;		//금요일
//				else if(dayOfWeek == 7) day = firstMonday+5;		//토요일
//				
//				cal.set(Calendar.DAY_OF_MONTH, day);
//				
//				customerAlarm.setWorkDt(cal.getTime());
////				logger.debug("after ###customerAlarm.getWorkDt=######"+customerAlarm.getWorkDt());
//				
////				logger.debug("##DateUtils.getWeekInMonths"+com.gms.web.admin.common.utils.DateUtils.getWeekInMonths(Integer.toString(year),Integer.toString(month)));
//								
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.1m3w"))) {
//				cal.add(Calendar.MONTH,1);
//				cal.set(Calendar.DAY_OF_MONTH, 1);
//				
//				int year = cal.get(Calendar.YEAR);
//				int month = cal.get(Calendar.MONTH)+1;
//			
//				int firstMonday = Integer.parseInt(DateUtils.getWeekInMonths(Integer.toString(year),Integer.toString(month)))+14;
//				int day = 0;
//				if(dayOfWeek == 2) day = firstMonday;	//월요일
//				else if(dayOfWeek == 3) day = firstMonday+1;		//화요일
//				else if(dayOfWeek == 4) day = firstMonday+2;		//수요일
//				else if(dayOfWeek == 5) day = firstMonday+3;		//목요일
//				else if(dayOfWeek == 6) day = firstMonday+4;		//금요일
//				else if(dayOfWeek == 7) day = firstMonday+5;		//토요일
//				
//				cal.set(Calendar.DAY_OF_MONTH, day);
//				
//				customerAlarm.setWorkDt(cal.getTime());
////				logger.debug("after ###customerAlarm.getWorkDt=######"+customerAlarm.getWorkDt());
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.2month"))) {
//				LocalDate localDate = new java.sql.Date(customerAlarm.getWorkDt().getTime()).toLocalDate();
//				
//				localDate= localDate.plusMonths(2);
//				
//				customerAlarm.setWorkDt(java.sql.Date.valueOf(localDate));
//				
////				logger.debug("customerAlarm.getWorkDt=2month)"+customerAlarm.getWorkDt());
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.5week"))) {
//				cal.add(Calendar.DATE, 35);
//				customerAlarm.setWorkDt(cal.getTime());
//
////				logger.debug("customerAlarm.getWorkDt=5week)"+customerAlarm.getWorkDt());
//
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.6week"))) {
//				cal.add(Calendar.DATE, 42);
//				customerAlarm.setWorkDt(cal.getTime());
//
////				logger.debug("customerAlarm.getWorkDt=6week)"+customerAlarm.getWorkDt());
//
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.7week"))) {
//				cal.add(Calendar.DATE, 49);
//				customerAlarm.setWorkDt(cal.getTime());
//
////				logger.debug("customerAlarm.getWorkDt=7week)"+customerAlarm.getWorkDt());
//
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.10day"))) {
//				int day = cal.get(Calendar.DAY_OF_MONTH);
//				if(day==5 || day==10) cal.add(Calendar.DATE, 5);
//				else if(day==15) {
//					cal.add(Calendar.MONTH,1);
//					cal.set(Calendar.DAY_OF_MONTH,5);
//				}
//				customerAlarm.setWorkDt(cal.getTime());
//
//				logger.debug("######customerAlarm.getWorkDt=10day)"+customerAlarm.getWorkDt());
//				
//			}else if (customerAlarm.getPeriodCd().equals(PropertyFactory.getProperty("common.code.period.40day"))) {
//				cal.add(Calendar.DATE, 40);
//				customerAlarm.setWorkDt(cal.getTime());
//				
//			}
//		}
		
		return customerList;
	}
	
	
	@RequestMapping(value = "/api/customer/recentOrderList.do")
	@ResponseBody
	public List<CustomerProductVO> recentOrderList(CustomerVO param) {
		logger.debug("recentOrderList " + param.getCustomerId());
		List<CustomerProductVO> productList = null;
		productList = apiService.recentOrderList(param);
		
		return productList;
	}
}

package com.gms.web.admin.controller.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.BottleHistoryVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.SimpleBottleVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.service.common.ApiService;
import com.gms.web.admin.service.manage.BottleService;

@Controller
public class ApiController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Logger logger1 = LoggerFactory.getLogger("ROLLING_FILE1");
	
	private final int CUSOTMER_NOT_EXIST = -3;
	private final int USER_NOT_EXIST = -4;
	private final String TAG="ApiController";
	/*
	 * Service 빈(Bean) 선언
	 */
	@Autowired
	private ApiService apiService;
	
	@Autowired
	private BottleService bottleService;
	
	@RequestMapping(value = "/api/controlAction.do")
	@ResponseBody
	public String controlAction(String userId, String bottles, String customerNm, String bottleType, String bottleWorkCd)	{	
				
		logger1.info("<<<< controlAction userId="+userId+" : bottles ="+bottles +": bottleType ="+ bottleType + ": bottleWorCd ="+bottleWorkCd+" : customerNm ="+customerNm);
		logger.info("*******************************************************************************************************************************************");
		logger.info("<<<< controlAction", "userId="+userId+" : bottles ="+bottles +": bottleType ="+ bottleType + ": bottleWorCd ="+bottleWorkCd+" : customerNm ="+customerNm);
		
		boolean phoneCall = true;
		int result = 0;		
				
		WorkReportVO workReport = new WorkReportVO();	
		
		workReport.setBottleType(bottleType);
		workReport.setBottlesIds(bottles);		//BottleBarCd 모음
		workReport.setCustomerNm(customerNm);		
		workReport.setPhoneFlag(phoneCall);
		workReport.setCreateId(userId);		
		workReport.setUserId(userId);
		workReport.setUpdateId(userId);		
		
		if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.come"))) {			
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.come"));
			result = apiService.registerWorkReportForChangeCd(workReport);
			
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.out"))) {		//출고
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.out"));
			result = apiService.registerWorkReportForChangeCd(workReport);
			
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
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.freeback"));
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
				
		logger1.info("<<<< controlActionNoGas userId="+param.getUserId()+" : productId ="+param.getProductId() +": productPriceSeq ="+ param.getProductPriceSeq() + " : customerNm ="+param.getCustomerNm() + " : productCount ="+param.getProductCount());
		logger.info("*******************************************************************************************************************************************");
		logger.info("<<<<  controlActionNoGas userId="+param.getUserId()+" : productId ="+param.getProductId() +": productPriceSeq ="+ param.getProductPriceSeq() + " : customerNm ="+param.getCustomerNm() + " : productCount ="+param.getProductCount());
		
		boolean phoneCall = true;
		int result = 1;
		/*
		WorkBottleVO workBottle = new WorkBottleVO();
		
		workBottle.setProductId(param.get);
		workBottle.setProductPriceSeq(productPriceSeq);
		
		workBottle.setProductCount(productCount);
		workBottle.setCustomerNm(customerNm);			
		*/	
		
		param.setCreateId(param.getUserId());	
		param.setUpdateId(param.getUserId());
		param.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));
		
		
		//workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.come"));
		result = apiService.registerWorkReportNoGas(param);			
		
		if(result > 0)
			return "success";
		if(result == USER_NOT_EXIST)
			return "noUser";
		else
			return "fail";
		//return null;
	}
	/*
	@RequestMapping(value = "/api/controlActionNoGas.do")
	@ResponseBody
	public String controlActionNoGas(String userId, String customerNm, Integer productId, Integer productPriceSeq, int productCount )	{	
				
		logger1.info("controlActionNoGas userId="+userId+" : productId ="+productId +": productPriceSeq ="+ productPriceSeq + " : customerNm ="+customerNm + " : productCount ="+productCount);
		logger.info("controlActionNoGas", "userId="+userId+" : productId ="+productId +": productPriceSeq ="+ productPriceSeq + " : customerNm ="+customerNm + " : productCount ="+productCount);
		
		boolean phoneCall = true;
		int result = 0;
		
		WorkBottleVO workBottle = new WorkBottleVO();
		
		workBottle.setProductId(productId);
		workBottle.setProductPriceSeq(productPriceSeq);
		
		workBottle.setProductCount(productCount);
		workBottle.setCustomerNm(customerNm);				
		workBottle.setCreateId(userId);	
		workBottle.setUpdateId(userId);
		workBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));
		
		logger.debug("단품판매 start");
		//workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.come"));
		result = apiService.registerWorkReportNoGas(workBottle);			
		
		if(result > 0)
			return "success";
		if(result == USER_NOT_EXIST)
			return "noUser";
		else
			return "fail";
		//return null;
	}
	*/
	/*
	@RequestMapping(value = "/api/controlCashFlow.do")
	@ResponseBody
	public String manageCashFlow(String userId, String customerNm, String incomeAmount, String receivableAmount, String incomeWay )	{	
				
		logger.info("userId="+userId+" : incomeAmount ="+incomeAmount +": receivableAmount ="+ receivableAmount + " : customerNm ="+customerNm + " : incomeWay ="+incomeWay);
		logger1.info("userId="+userId+" : incomeAmount ="+incomeAmount +": receivableAmount ="+ receivableAmount + " : customerNm ="+customerNm + " : incomeWay ="+incomeWay);
				
		int result = 0;
		
		CashFlowVO cashFlow = new CashFlowVO();
		
		cashFlow.setCustomerNm(customerNm);
		cashFlow.setIncomeAmount(Float.parseFloat(incomeAmount));
		cashFlow.setReceivableAmount(Float.parseFloat(receivableAmount));
		cashFlow.setIncomeWay(incomeWay);
		cashFlow.setCreateId(userId);
		
		result = apiService.registerCashFlow(cashFlow);
		
		if(result > 0)
			return "success";
		if(result == USER_NOT_EXIST)
			return "noUser";
		else
			return "fail";
		//return null;
	}
*/
	@RequestMapping(value = "/api/controlCashFlow.do")
	@ResponseBody
	public String manageCashFlow(CashFlowVO param )	{	
		logger.info("*******************************************************************************************************************************************");		
		logger.info("<<<< userId="+param.getCreateId()+" : incomeAmount ="+param.getIncomeAmount() +": receivableAmount ="+ param.getReceivableAmount() + " : customerNm ="+param.getCustomerNm() + " : incomeWay ="+param.getIncomeWay());
	
		int result = 0;
		/*
		CashFlowVO cashFlow = new CashFlowVO();
		
		cashFlow.setCustomerNm(customerNm);
		cashFlow.setIncomeAmount(Float.parseFloat(incomeAmount));
		cashFlow.setReceivableAmount(Float.parseFloat(receivableAmount));
		cashFlow.setIncomeWay(incomeWay);
		cashFlow.setCreateId(userId);
		*/
		result = apiService.registerCashFlow(param);
		
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
		logger.debug("Tank start logger.info(\"*******************************************************************************************************************************************\");");
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
				
		logger.info("<<<<  manageGasAndBottle userId="+userId+" : bottles ="+bottles +": bottleType ="+ bottleType + ": bottleWorCd ="+bottleWorkCd+" : customerNm ="+customerNm);
		logger.info("*******************************************************************************************************************************************");
		logger1.info("<<<< manageGasAndBottle","userId="+userId+" : bottles ="+bottles +": bottleType ="+ bottleType + ": bottleWorCd ="+bottleWorkCd+" : customerNm ="+customerNm);
				
		int result = 0;
		boolean phoneCall = true;
		
		WorkReportVO workReport = new WorkReportVO();	
		
		workReport.setBottleType(bottleType);
		workReport.setBottlesIds(bottles);		//BottleBarCd 모음
		workReport.setCustomerNm(customerNm);		
		workReport.setPhoneFlag(phoneCall);
		workReport.setCreateId(userId);				
		workReport.setUpdateId(userId);				
		
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
		logger1.debug("getWorkBottleList","userId="+userId);
		WorkReportVO workReport = new WorkReportVO();
		
		workReport.setSearchUserId(userId);
		workReport.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		
		return apiService.getWorkReportList(workReport);
	}
	
	@RequestMapping(value = "/api/controlMassAction.do")
	@ResponseBody
	public String controlMassAction(String userId, String bottles, String customerNm, String bottleType, String bottleWorkCd)	{	
				
		logger.info("<<<< controlMassAction  userId="+userId+" : bottles ="+bottles +" : bottleType ="+ bottleType + ": bottleWorCd ="+bottleWorkCd+" : customerNm ="+customerNm +"/n*******");
		logger.info("*******************************************************************************************************************************************");
		logger1.info("<<<< controlMassAction"," userId="+userId+" : bottles ="+bottles +" : bottleType ="+ bottleType + ": bottleWorCd ="+bottleWorkCd+" : customerNm ="+customerNm +"/n*******");
		
		int result = 1;		
		boolean phoneCall = true;
		
		WorkReportVO workReport = new WorkReportVO();	
		
		workReport.setBottleType(bottleType);
		workReport.setBottlesIds(bottles);		//BottleBarCd 모음
		workReport.setCustomerNm(customerNm);		
		workReport.setPhoneFlag(phoneCall);
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
			
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.title.massback"))) {			//회수
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesBack"));
			workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.salesBack"));	
			result = apiService.registerWorkReportMassForChangeCd(workReport);		
		}
		
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
		logger1.info("getBottleDetail bottleBarCd="+bottleBarCd);
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
		logger.debug("deleteWorkBottle==="+param);
		
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
}

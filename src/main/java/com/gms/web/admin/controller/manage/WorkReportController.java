package com.gms.web.admin.controller.manage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.OrderBottlesVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CashFlowService;
import com.gms.web.admin.service.manage.UserService;
import com.gms.web.admin.service.manage.WorkReportService;

import groovyjarjarpicocli.CommandLine.Model;

@Controller
public class WorkReportController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/*
	 * Service 빈(Bean) 선언
	 */
	@Autowired
	private WorkReportService workService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private CashFlowService cashService;
	
	@RequestMapping(value = "/gms/report/list.do")
	public ModelAndView getWorkReportList(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {

		
		RequestUtils.initUserPrgmInfo(request, params);				
		
		ModelAndView mav = new ModelAndView();		
		
		params.setUserId(params.getCreateId());		
		params.setSearchUserId(params.getCreateId());
		
		
		List<WorkReportViewVO> workList = workService.getWorkReportListAll(params);
		
		mav.addObject("workList", workList);	
		mav.addObject("searchDt", params.getSearchDt());	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.diary"));	 	
		
		mav.setViewName("gms/report/list");
		
		return mav;
	}
	
	
	
	@RequestMapping(value = "/gms/report/listAll.do")
	public ModelAndView getWorkReportListAll(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {

		RequestUtils.initUserPrgmInfo(request, params);				
//		logger.debug("WorkReportController getWorkReportListAll=");
		ModelAndView mav = new ModelAndView();		
		
		params.setUserId(params.getCreateId());	
		params.setSearchUserId(request.getParameter("searchUserId"));
		UserVO tempUser = new UserVO();		
		List<UserVO> userList = userService.getUserListPartNot(tempUser);
		
		mav.addObject("userList",userList);
		
		if(params.getSearchUserId() == null && userList.size() > 0) params.setSearchUserId(userList.get(0).getUserId());
		
		List<WorkReportViewVO> workList = workService.getWorkReportListAll(params);		
		
		mav.addObject("workList", workList);	
		mav.addObject("searchDt", params.getSearchDt());	
		mav.addObject("searchUserId", params.getSearchUserId());			
				
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.diary"));	 	
		
		mav.setViewName("gms/report/listAll");
		
		return mav;
	}
	
	@RequestMapping(value = "/gms/report/register.do", method = RequestMethod.POST)
	public ModelAndView registerWorkReportForOrder(HttpServletRequest request
			, HttpServletResponse response
			, OrderBottlesVO params) {
		
		
		RequestUtils.initUserPrgmInfo(request, params);
		ModelAndView mav = new ModelAndView();	

		int result =0;
		try {	
			
			WorkReportVO work = new WorkReportVO();
			
			work.setOrderId(params.getOrderId());
			work.setBottlesIds(params.getBottleIds());
			work.setBottleWorkCd(params.getBottleWorkCd());
			work.setUserId(params.getCreateId());
			work.setCreateId(params.getCreateId());
			
			result = workService.registerWorkReportForOrder(work);					

			//mav.setViewName("/gms/mypage/assign");			
		
		} catch (Exception e) {
			logger.error(" registerWorkReportForOrder Exception==="+e.toString());
			e.printStackTrace();
		}
		if(result > 0){
			String alertMessage = "처리되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/mypage/assign.do");
		}
		return null;
		//return "redirect:/gms/mypage/assign.do";		
	}
	
	@RequestMapping(value = "/gms/report/registerAll.do", method = RequestMethod.POST)
	public ModelAndView registerWorkReportAll(HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {
		
		
		RequestUtils.initUserPrgmInfo(request, params);
		//logger.debug("WorkReportController userId="+params.getUserId());
		ModelAndView mav = new ModelAndView();		//검색조건 셋팅		
		int result =0;
		try {	
			
			if(params.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back")) || params.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) 
					|| params.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out")) ){
				List<String> list = null;				
				BottleVO bottle = new BottleVO();
				params.setBottleType("E");
				if(params.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))) params.setBottleType("F");
				if(params.getBottlesIds()!=null && params.getBottlesIds().length() > 0) {
					//bottleIds= request.getParameter("bottleIds");
					list = StringUtils.makeForeach(params.getBottlesIds(), ","); 		
					bottle.setBottList(list);
					bottle.setBottleWorkCd(params.getBottleWorkCd());
					bottle.setBottleWorkId(params.getCreateId());
					bottle.setCustomerId(params.getCustomerId());
					bottle.setUpdateId(params.getCreateId());
					bottle.setBottleType(params.getBottleType());
				}			
				
				List<BottleVO> bottleList = bottleService.getBottleDetails(bottle);
				
				if(params.getUserId()==null)
					params.setUserId(params.getCreateId());
				result = workService.registerWorkReportByBottle(params,bottleList);
				
				result =  bottleService.changeWorkCdsAndHistory(bottle, bottleList);
				
			}else {
				params.setBottleType("F");
				result = workService.registerWorkReportNoOrder(params);					
			}
			//mav.setViewName("/gms/mypage/assign");			
		
		} catch (Exception e) {
			logger.error(" registerWorkReportAll Exception==="+e.toString());
			e.printStackTrace();
		}
		//return "redirect:/gms/mypage/assign.do";
		mav.addObject("searchUserId", params.getUserId());	
		if(result > 0){
			String alertMessage = "처리되었습니다.";
			LoginUserVO adminLoginUserVO = (LoginUserVO)request.getSession().getAttribute(LoginUserVO.ATTRIBUTE_NAME); 
			if(adminLoginUserVO.getUserAuthority().equals(PropertyFactory.getProperty("common.user.Authority.manager")))
				RequestUtils.responseWriteException(response, alertMessage, "/gms/report/listAll.do?searchUserId="+params.getUserId());
			else 
				RequestUtils.responseWriteException(response, alertMessage, "/gms/report/list.do");			
		}
		return null;
		
	}
	
	@RequestMapping(value = "/gms/report/register0310.do", method = RequestMethod.POST)
	public ModelAndView registerWorkReport0310(HttpServletRequest request
			, HttpServletResponse response
			, OrderBottlesVO params) {
		
		
		RequestUtils.initUserPrgmInfo(request, params);
		ModelAndView mav = new ModelAndView();	

		//검색조건 셋팅
		int result =0;
		try {	
			
			WorkReportVO work = new WorkReportVO();
			
			work.setOrderId(params.getOrderId());
			work.setBottlesIds(params.getBottleIds());
			work.setBottleWorkCd(params.getBottleWorkCd());
			work.setUserId(params.getCreateId());
			work.setCreateId(params.getCreateId());
			
			result = workService.registerWorkReport0310(work);					
		
		} catch (Exception e) {
			logger.error(" registerWorkReport0310 Exception==="+e.toString());
			e.printStackTrace();
		}
		if(result > 0){
			String alertMessage = "처리되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/mypage/assign.do");
		}
		return null;
		//return "redirect:/gms/mypage/assign.do";		
	}
		
	@RequestMapping(value = "/gms/report/print.do")
	public ModelAndView getWorkReportListPrint(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO params) {

		
		RequestUtils.initUserPrgmInfo(request, params);		
		
		ModelAndView mav = new ModelAndView();		
		
		if(params.getSearchUserId()!=null && params.getSearchUserId().length() > 1) {
			params.setUserId(params.getSearchUserId());
		}else {
			params.setUserId(params.getCreateId());
			params.setSearchUserId(params.getCreateId());
		}
		
		//logger.debug("WorkReportController getWorkReportList User_id= "+ params.getUserId());		
		
		//List<WorkReportViewVO> workList = workService.getWorkReportList1(params);
		List<WorkReportViewVO> workList = workService.getWorkReportListAll(params);
		
		mav.addObject("workList", workList);	
		mav.addObject("searchDt", params.getSearchDt());			 
		
		if(workList.size() > 0 ) {
			mav.addObject("orderAmountToday", new Double(workList.get(0).getOrderAmountToday()));
			mav.addObject("receivedAmountToday", new Double(workList.get(0).getReceivedAmountToday()));
		}
		
		mav.setViewName("gms/report/print");
		
		return mav;
	}
	
	@RequestMapping(value = "/gms/report/excelDownload.do")
	public void excelDownloadReport(HttpServletResponse response
			, WorkReportVO params) throws Exception {
		
		UserVO user = userService.getUserDetails( params.getSearchUserId());
		String fileName = "업무일지";
			if(user != null) fileName = fileName + "_"+user.getUserNm()+"_"+params.getSearchDt();
			
		if(params.getSearchUserId()!=null && params.getSearchUserId().length() > 1) {
			params.setUserId(params.getSearchUserId());
		}else {
			params.setUserId(params.getCreateId());
			params.setSearchUserId(params.getCreateId());
		}
		
		List<WorkReportViewVO> workList = workService.getWorkReportListAll(params);
		double totalReceivedAmount = 0;
		for(int i=0; i < workList.size() ; i++) {
			WorkReportViewVO workReport = workList.get(i);
			workReport.setSeqNumber(Integer.toString(i+1));
			StringBuffer sf = new StringBuffer();
			totalReceivedAmount += workReport.getReceivedAmount();
			for(int j=0; j < workReport.getSalesBottles().size() ; j++) {
				WorkBottleVO workBottle = workReport.getSalesBottles().get(j);
				if(j > 0) sf.append("\n");
				sf.append(workBottle.getProductNm()).append(" ")
					.append(workBottle.getProductCapa()).append(" ")
					.append(workBottle.getProductCount()).append(" (")
					.append(workBottle.getBottleWorkCdNm()).append(") ");
			}
			workReport.setStrSalesBottles(sf.toString());
			
			StringBuffer sf1 = new StringBuffer();
			for(int j=0; j < workReport.getBackBottles().size() ; j++) {
				WorkBottleVO workBottle = workReport.getBackBottles().get(j);
				if(j > 0) sf1.append("\n");
				sf1.append(workBottle.getProductNm()).append(" ")
					.append(workBottle.getProductCapa()).append(" ")
					.append(workBottle.getProductCount()).append(" ");
				if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back")) )
					sf1.append("(").append(workBottle.getBottleWorkCdNm()).append(") ");
			}
			workReport.setStrBackBottles(sf1.toString());
		}
		
		int iSeq = 25-workList.size();
		for(int i=0; i < iSeq ; i++) {
			WorkReportViewVO workReport = new WorkReportViewVO();
			workReport.setCustomerNm("");
			workList.add(workReport);
		}
	
//		logger.debug("WorkReportController getWorkReportList start ");	
		try(
	    		InputStream is = WorkReportController.class.getResourceAsStream("report_template.xls")){
		        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
		        response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"ISO8859_1") + ".xls");
		       
        		Context context = new Context();
        		context.putVar("workList", workList);
        		context.putVar("searchDt", params.getSearchDt());
        		context.putVar("totalAmount", totalReceivedAmount);
	            JxlsHelper.getInstance().processTemplate(is, response.getOutputStream(), context);
	       
	    }
	}


	@RequestMapping(value = "/gms/report/noGasSales.do", method = RequestMethod.POST)
	public ModelAndView registerWorkReportNoGasProduct(HttpServletRequest request
			, HttpServletResponse response
			, WorkBottleVO param) {
		
		
		RequestUtils.initUserPrgmInfo(request, param);
		
		ModelAndView mav = new ModelAndView();	
		
		int result =0;
		try {	
			
			if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
					&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") )
					&& param.getProductCount() > 1000 ) {
				param.setProductPrice(param.getProductCount());
				param.setProductCount(1);
			}
			
			result = workService.registerWorkNoBottle(param);					
		
		} catch (Exception e) {
			logger.error(" registerWorkReportNoGasProduct Exception==="+e.toString());
			e.printStackTrace();
		}
		//return "redirect:/gms/mypage/assign.do";
		
		if(result > 0){
			String alertMessage = "처리되었습니다.";
			LoginUserVO adminLoginUserVO = (LoginUserVO)request.getSession().getAttribute(LoginUserVO.ATTRIBUTE_NAME); 
			if(adminLoginUserVO.getUserAuthority().equals(PropertyFactory.getProperty("common.user.Authority.manager")))
				RequestUtils.responseWriteException(response, alertMessage, "/gms/report/listAll.do?searchUserId="+param.getUserId());
			else 
				RequestUtils.responseWriteException(response, alertMessage, "/gms/report/list.do");
			
		}
		return null;		
	}
	
	@RequestMapping(value = "/gms/report/workBottleList.do")
	@ResponseBody
	public List<WorkBottleVO> getWorkBottleList(@RequestParam(value = "workReportSeq", required = false) Integer workReportSeq, Model model)	{	
				
		
		List<WorkBottleVO> workBottleList = workService.getWorkBottleList(workReportSeq);
		//model.addAttribute("orderProductList", orderProductList);
		
		return workBottleList;
	}
	
	@RequestMapping(value = "/gms/report/update.do")
	public ModelAndView getWorkReportUpdate(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO param) {

		
		RequestUtils.initUserPrgmInfo(request, param);				
		
		ModelAndView mav = new ModelAndView();		
			
		WorkReportVO workReport = workService.getWorkReport(param.getWorkReportSeq());
		
		mav.addObject("workReport", workReport);	 	
		mav.addObject("searchUserId", workReport.getUserId());
		if(request.getParameter("action") != null) mav.addObject("action", request.getParameter("action"));
		else mav.addObject("action", "update");	 
		//mav.addObject("searchDt", DateUtils.convertDateFormat(workReport.getSearchDt(), "yyyy/MM/dd"));
		mav.setViewName("gms/report/update");
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.diary"));	 	
		
		return mav;
	}
	
	@RequestMapping(value = "/gms/report/modify.do")
	public ModelAndView getWorkReportModify(HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO param) {

		int result = 1;
		try {			
			//RequestUtils.initUserPrgmInfo(request, param);	
			if(param.getUserId() !=null ) {			
				param.setUserId(param.getUserId());
				param.setCreateId(param.getUserId());
				param.setUpdateId(param.getUserId());
			}else {				
				RequestUtils.initUserPrgmInfo(request, param);	
				param.setUserId(param.getCreateId());
			}
			ModelAndView mav = new ModelAndView();		
			logger.info("getWorkReportModify== workReportSeq="+param.getWorkReportSeq());
			result = workService.modifyWorkBottleManual(request,param);
			// WorkReport 정보 변경			
			
			mav.addObject("workReportSeq", param.getWorkReportSeq());	 	
			mav.addObject("action", "modify");	 	
					
			mav.setViewName("gms/report/update");
			if(result > 0){
				String alertMessage = "수정되었습니다.";
				RequestUtils.responseWriteException(response, alertMessage,
						"/gms/report/update.do?workReportSeq="+param.getWorkReportSeq()+"&action=modify");
			}
		} catch (DataAccessException e) {		
			logger.error(" getWorkReportModify Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {			
			logger.error(" getWorkReportModify Exception==="+e.toString());
			e.printStackTrace();
		}
		return null;		
	}
	
	@RequestMapping(value = "/gms/report/delete.do", method = RequestMethod.POST)
	public ModelAndView deleteWorkReport(HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO param) {
		
		//logger.debug("WorkReportController deleteWorkReport");
		
		RequestUtils.initUserPrgmInfo(request, param);
		param.setUserId(param.getCreateId());
		ModelAndView mav = new ModelAndView();	

		int result =0;
		try {	
			result = workService.deleteWorkReport(param);					

			mav.addObject("searchDt", param.getSearchDt());
			mav.addObject("searchUserId", param.getSearchUserId());
		
		} catch (Exception e) {
			logger.error(" deleteWorkReport Exception==="+e.toString());
			e.printStackTrace();
		}
		if(result > 0){
			String alertMessage = "처리되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/report/listAll.do?searchUserId="+param.getSearchUserId());
		}
		return null;
		//return "redirect:/gms/mypage/assign.do";		
	}
	
	
	@RequestMapping(value = "/gms/report/registerCash.do", method = RequestMethod.POST)
	public ModelAndView registerCashFlow(
			HttpServletRequest request
			, HttpServletResponse response
			, CashFlowVO param) {
		
		int result = 0;
		ModelAndView mav = new ModelAndView();		
				
		param.setUpdateId(param.getCreateId());
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.diary"));	 	
		
		// 수금액 정보 업데이트			
		WorkReportVO workReport = new WorkReportVO();
		workReport.setCustomerId(param.getCustomerId());
		workReport.setUserId(param.getCreateId());
		
		int workReportSeq = workService.getWorkReportSeqForCustomerToday(workReport);
		
		if(workReportSeq <= 0) {
			workReportSeq = workService.getWorkReportSeq();		
			workReport.setWorkReportSeq(workReportSeq);
			workReport.setCreateId(param.getCreateId());
			workReport.setUserId(param.getCreateId());
			workReport.setReceivedAmount(param.getIncomeAmount());
			workReport.setIncomeWay(param.getIncomeWay());
			workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.0312"));
			
			workService.registerWorkReportOnly(workReport);
			
		}else {
			workReport.setUpdateId(param.getCreateId());
			workReport.setReceivedAmount(param.getIncomeAmount());
			workReport.setIncomeWay(param.getIncomeWay());
			workReport.setWorkReportSeq(workReportSeq);
			
			result = workService.modifyWorkReportReceivedAmount(workReport);
		}	
		
		if(param.getIncomeAmount() <=0)
			param.setIncomeWay(null);

		result = cashService.registerCashFlow(param);
		if(result > 0){
			String alertMessage = "등록되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/report/listAll.do?searchUserId="+param.getCreateId() );
		}
		return null;
	}
	
	
	@RequestMapping(value = "/gms/report/saveEtc.do")
	@ResponseBody
	public int saveWorkReportEtc(HttpServletRequest request
			, HttpServletResponse response
			,WorkBottleVO param)	{			
		RequestUtils.initUserPrgmInfo(request, param);
		logger.info("saveWorkReportEtc==="+param.getWorkEtc()+ " workReportSeq="+param.getWorkReportSeq()+"=="+param.getUpdateId());
		
		int result = 0;		
		result = workService.modifyWorkReportEtc(param);
		
		return result;
	}
	
	@RequestMapping(value = "/gms/report/saveBottleEtc.do")
	@ResponseBody
	public int saveWorkBottleEtc(HttpServletRequest request
			, HttpServletResponse response
			,WorkBottleVO param)	{			
		RequestUtils.initUserPrgmInfo(request, param);
		logger.info("saveWorkReportEtc==="+param.getWorkEtc()+ " workReportSeq="+param.getWorkReportSeq()+"=="+param.getUpdateId());
		
		int result = 0;		
		result = workService.modifyWorkBottleEtc(param);
		
		return result;
	}
}

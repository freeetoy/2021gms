package com.gms.web.admin.controller.manage;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CashSumVO;
import com.gms.web.admin.service.manage.CashFlowService;
import com.gms.web.admin.service.manage.CustomerService;

@Controller
public class CashFlowController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private CashFlowService cashService;
	
	@Autowired
	private CustomerService customerService;
	
	@RequestMapping(value = "/gms/cash/register.do", method = RequestMethod.POST)
	public ModelAndView registerCashFlow(
			HttpServletRequest request
			, HttpServletResponse response
			, CashFlowVO param) {
		
		int result = 0;
		ModelAndView mav = new ModelAndView();		
		
		RequestUtils.initUserPrgmInfo(request, param);		
		param.setUpdateId(param.getCreateId());
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.cash"));
		
		
		if(request.getParameter("customerId1") != null)
			param.setCustomerId(Integer.parseInt(request.getParameter("customerId1")) );
		logger.debug("customerId = "+param.getCustomerId());
		
		if(param.getIncomeAmount() <=0)
			param.setIncomeWay(null);

		result = cashService.registerCashFlow(param);
		if(result > 0){
			String alertMessage = "등록되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/cash/list.do?customerId="+param.getCustomerId() );
		}
		return null;
	}
	
	@RequestMapping(value = "/gms/cash/modify.do", method = RequestMethod.POST)
	public ModelAndView modifyCashFlow(
			HttpServletRequest request
			, HttpServletResponse response
			, CashFlowVO param) {

		ModelAndView mav = new ModelAndView();		
		
		RequestUtils.initUserPrgmInfo(request, param);		
		param.setUpdateId(param.getCreateId());
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.cash"));
		
		int result = 0;
		//TODO 판매완료된 상품 주문 수정 안되게 처리
		logger.debug(" modifyCashFlow customerId="+param.getCustomerId());
		logger.debug(" modifyCashFlow getSearchCreateDt="+param.getSearchCreateDt());
		
		result = cashService.modifyCashFlow(param);
		if(result > 0){
			String alertMessage = "수정되었습니다.";
			String strUrl = "/gms/cash/list.do?currentPage="+param.getCurrentPage();
			if(param.getCustomerId() !=null && param.getCustomerId() > 0) strUrl +="&customerId="+param.getCustomerId();
			if(param.getSearchCreateDt() !=null && param.getSearchCreateDt().length() > 0) strUrl +="&searchCreateDt="+param.getSearchCreateDt();
			RequestUtils.responseWriteException(response, alertMessage, strUrl );
		}
		return null;
	}
	
	@RequestMapping(value = "/gms/cash/delete.do", method = RequestMethod.POST)
	public ModelAndView deleteCashFlow(
			HttpServletRequest request
			, HttpServletResponse response
			, CashFlowVO param) {

		ModelAndView mav = new ModelAndView();		

		RequestUtils.initUserPrgmInfo(request, param);		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.cash"));
		
		Integer customerId=0;
		if(param.getCustomerId()!=null && param.getCustomerId() > 0)
			customerId=param.getCustomerId();
		
		int result = 0;
		//TODO 판매완료된 상품 주문 수정 안되게 처리
		
		result = cashService.deleteCashFlow(param);
		if(result > 0){
			String alertMessage = "삭제되었습니다.";
			String strUrl = "/gms/cash/list.do?currentPage="+param.getCurrentPage();
			if(param.getCustomerId() !=null && param.getCustomerId() > 0) strUrl +="&customerId="+param.getCustomerId();
			if(param.getSearchCreateDt() !=null && param.getSearchCreateDt().length() > 0) strUrl +="&searchCreateDt="+param.getSearchCreateDt();
			RequestUtils.responseWriteException(response, alertMessage, strUrl );
		}
		return null;
	}
	
	@RequestMapping(value = "/gms/cash/list.do")
	public ModelAndView getCashFlowList(CashFlowVO param) {

		logger.debug("CashFlowController getCashFlowList");
		
		ModelAndView mav = new ModelAndView();		
				
		Map<String, Object> map = cashService.getCashFlowList(param);
		
		mav.addObject("list", map.get("list"));		
		if(param.getCustomerId()!=null && param.getCustomerId() >0)
			mav.addObject("customerId", param.getCustomerId());	
	
		String searchCreateDt = param.getSearchCreateDt();	
		mav.addObject("searchCreateDt", param.getSearchCreateDt());		
		
		/*
		CashSumVO cashSum = cashService.getCashFlowSum(param);
		if(cashSum == null) {
			cashSum = new CashSumVO();
			cashSum.setIncomeAmountSum(0);
			cashSum.setReceivableAmountNet(0);
			cashSum.setReceivableAmountSum(0);
		}
		mav.addObject("cashSum", cashSum);
		*/
		// 주문 타입 불러오기
		Map<String, Object> map1 = customerService.searchCustomerList("");
		mav.addObject("customerList", map1.get("list"));		
		
		mav.addObject("currentPage", map.get("currentPage"));
		mav.addObject("lastPage", map.get("lastPage"));
		mav.addObject("startPageNum", map.get("startPageNum"));
		mav.addObject("lastPageNum", map.get("lastPageNum"));
		mav.addObject("totalCount", map.get("totalCount"));
		mav.addObject("rowPerPage", param.getRowPerPage());
		
		if(param.getCustomerId() !=null && param.getCustomerId() > 0) {
			
			mav.addObject("customerYN", "Y");
		} else {
			mav.addObject("customerYN", "N");
		}
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.cash"));	 		
		
		return mav;
	}
	
	@RequestMapping(value = "/gms/cash/detail.do")
	@ResponseBody
	public CashFlowVO getCashFlow(CashFlowVO param) {

		logger.debug(" getCashFlow");
		
		return cashService.getCashFlowDetail(param);
	}

}

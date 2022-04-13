package com.gms.web.admin.controller.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.common.CodeVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CashSumVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderExtCustomerVO;
import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderPrintVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.service.common.CodeService;
import com.gms.web.admin.service.manage.CashFlowService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.OrderService;
import com.gms.web.admin.service.manage.WorkReportService;


@Controller
public class OrderController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private WorkReportService workService;
	
	@Autowired
	private CashFlowService cashService;
	
	@RequestMapping(value = "/gms/order/list.do")
	public ModelAndView getOrderList(OrderVO params) {

		logger.debug(" getOrderList");

		ModelAndView mav = new ModelAndView();
		
		String searchOrderDt = params.getSearchOrderDt();	
		
		String searchOrderDtFrom = null;
		String searchOrderDtEnd = null;
				
		if(searchOrderDt != null && searchOrderDt.length() > 20) {						
			searchOrderDtFrom = searchOrderDt.substring(0, 10) ;			
			searchOrderDtEnd = searchOrderDt.substring(13, searchOrderDt.length()) ;
			
			params.setSearchOrderDtFrom(searchOrderDtFrom);
			params.setSearchOrderDtEnd(searchOrderDtEnd);			
		}
				
		Map<String, Object> map = orderService.getOrderList(params);
		
		mav.addObject("orderList", map.get("list"));			
		
		//Order_Process_CD 코드정보 불러오기 
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.code.order.process"));
		mav.addObject("codeList", codeList);
		//검색어 셋팅
		mav.addObject("searchOrderDt", params.getSearchOrderDt());	
		mav.addObject("searchCustomerNm", params.getSearchCustomerNm());	
		mav.addObject("searchOrderProcessCd", params.getSearchOrderProcessCd());	
		
		mav.addObject("currentPage", map.get("currentPage"));
		mav.addObject("lastPage", map.get("lastPage"));
		mav.addObject("startPageNum", map.get("startPageNum"));
		mav.addObject("lastPageNum", map.get("lastPageNum"));
		mav.addObject("totalCount", map.get("totalCount"));
		mav.addObject("rowPerPage", params.getRowPerPage());
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));	 		
		
		mav.setViewName("gms/order/list");
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/order/monitor.do")
	public ModelAndView getWorkBottleListToday(BottleVO params) {

		logger.debug(" getWorkBottleListToday");

		ModelAndView mav = new ModelAndView();		
		
		Map<String, Object> map =  workService.getWorkBottleListTotal(params);
		
		mav.addObject("bottleList", map.get("list"));			
				
		//검색어 셋팅
		mav.addObject("searchChargeDt", params.getSearchChargeDt());	
		mav.addObject("searchCustomerNm", params.getSearchCustomerNm());	
		
		mav.addObject("currentPage", map.get("currentPage"));
		mav.addObject("lastPage", map.get("lastPage"));
		mav.addObject("startPageNum", map.get("startPageNum"));
		mav.addObject("lastPageNum", map.get("lastPageNum"));
		mav.addObject("totalCount", map.get("totalCount"));
		mav.addObject("rowPerPage", params.getRowPerPage());		
				
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.trasnaction"));		
		
		mav.setViewName("gms/order/monitor");
		return mav;
	}
	
	@RequestMapping(value = "/gms/order/write.do")
	public ModelAndView openOrderWrite(Model model) {
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
	
		// 주문 타입 불러오기
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.code.order.type"));
		mav.addObject("codeList", codeList);	
		
		// 주문 타입 불러오기
		Map<String, Object> map = customerService.searchCustomerList("");
		mav.addObject("customerList", map.get("list"));
		
		mav.setViewName("gms/order/write");
		
		return mav;
					
	}

	
	@RequestMapping(value = "/gms/order/register.do", method = RequestMethod.POST)
	public ModelAndView registerOrder(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {

		ModelAndView mav = new ModelAndView();		

		RequestUtils.initUserPrgmInfo(request, params);		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		int result = 0;
		
		result = orderService.registerOrder(request,params);
		if(result > 0){
			String alertMessage = "등록되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/order/list.do");
		}
		return null;
	}
	
	
	@RequestMapping(value = "/gms/order/update.do")
	public ModelAndView openOrderUpdate(HttpServletRequest request
			, HttpServletResponse response,
			OrderVO params) {
		
		logger.debug("openOrderUpate "+ params.getOrderId());
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));			
			
		if (params.getOrderId() == null) {
			return null;
		} else {
			
			OrderVO order = orderService.getOrderDetail(params.getOrderId());		
			
			if (order == null) {
				return null;
			}
			
			// 주문 타입 불러오기
			List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.code.order.type"));
			mav.addObject("codeList", codeList);	
			
			// 주문 타입 불러오기
			Map<String, Object> map = customerService.searchCustomerList("");
			mav.addObject("customerList", map.get("list"));
			
			mav.addObject("order", order);
			//mav.addObject("orderProductList", orderProductList);
			mav.addObject("currentPage", params.getCurrentPage());
			mav.addObject("searchCustomerNm", params.getSearchCustomerNm());
			mav.addObject("searchOrderDt", params.getSearchOrderDt());
			mav.addObject("searchOrderProcessCd", params.getSearchOrderProcessCd());
			
		}
		mav.setViewName("gms/order/update");
		
		return mav;
					
	}
	
	@RequestMapping(value = "/gms/order/modify.do", method = RequestMethod.POST)
	public ModelAndView modifyOrder(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {

		ModelAndView mav = new ModelAndView();		

		RequestUtils.initUserPrgmInfo(request, params);		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		int result = 0;
		//TODO 판매완료된 상품 주문 수정 안되게 처리
		
		result = orderService.modifyOrder(request,params);
		if(result > 0){
			String alertMessage = "수정되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/order/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+params.getSearchCustomerNm()+"&searchOrderDt="+params.getSearchOrderDt()+"&searchOrderProcessCd="+params.getSearchOrderProcessCd());
		}
		return null;
	}
	
	@RequestMapping(value = "/gms/order/modifyBottleId.do", method = RequestMethod.POST)
	public ModelAndView modifyOrderBottlId(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderProductVO params) {

		ModelAndView mav = new ModelAndView();		

		RequestUtils.initUserPrgmInfo(request, params);		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		int result = 0;
		
		result = orderService.modifyOrderBottleId(params);
		if(result > 0){
			String alertMessage = "수정되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/order/list.do");
		}
		return null;
	}
	
	
	@RequestMapping(value = "/gms/order/orderProductList.do")
	@ResponseBody
	public List<OrderProductVO> getOrderProductList(@RequestParam(value = "orderId", required = false) Integer orderId, Model model)	{	
				
		logger.debug("getOrderProductList "+ orderId);
		
		List<OrderProductVO> orderProductList = orderService.getOrderProductList(orderId);
		//model.addAttribute("orderProductList", orderProductList);
		
		return orderProductList;
	}
	
	
	@RequestMapping(value = "/gms/order/delete.do", method = RequestMethod.POST)
	public ModelAndView deleteOrder(HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {
		
		logger.debug(" deleteOrder");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		//검색조건 셋팅		
		int  result = orderService.deleteOrder(params);
		
		if(result > 0){
			String alertMessage = "삭제하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/order/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+params.getSearchCustomerNm()+"&searchOrderDt="+params.getSearchOrderDt()+"&searchOrderProcessCd="+params.getSearchOrderProcessCd());
		}else if(result < 0) {
			String alertMessage = "이미 납품 진행된 주문입니다..";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/order/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+params.getSearchCustomerNm()+"&searchOrderDt="+params.getSearchOrderDt()+"&searchOrderProcessCd="+params.getSearchOrderProcessCd());
		}
		return null;		
	}
	
	
	@RequestMapping(value = "/gms/order/detail.do")
	@ResponseBody
	public OrderExtVO getOrderDetail(@RequestParam(value = "orderId", required = false) Integer orderId, Model model)	{
		
		OrderExtVO result = orderService.getOrder(orderId);			
		
		return result;
	}
		
	@RequestMapping(value = "/gms/order/changeProcess.do", method = RequestMethod.POST)
	public ModelAndView modifyOrderWorkCd(HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {
		
		logger.debug(" modifyOrderWorkCd");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		int result = orderService.changeOrderProcessCd(params);
		//logger.debug("OrderContoller after service searchCustomerNm=="+params.getSearchCustomerNm());
		if(result > 0){
			String alertMessage = "진행상태를 변경하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/order/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+params.getSearchCustomerNm()+"&searchOrderDt="+params.getSearchOrderDt()+"&searchOrderProcessCd="+params.getSearchOrderProcessCd());
		}
		return null;
		
	}
	
	@RequestMapping(value = "/gms/order/changeOrdersProcess.do", method = RequestMethod.POST)
	public ModelAndView modifyOrdersProcessCd(HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {
		
		logger.debug(" modifyOrdersProcessCd");
		
		RequestUtils.initUserPrgmInfo(request, params);
	
		int result = orderService.changeOrdersProcessCd(params);
	//	logger.debug("OrderContoller after service searchCustomerNm=="+params.getSearchCustomerNm());
		if(result > 0){
			String alertMessage = "진행상태를 변경하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/order/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+params.getSearchCustomerNm()+"&searchOrderDt="+params.getSearchOrderDt()+"&searchOrderProcessCd="+params.getSearchOrderProcessCd());
		}
		return null;
		
	}
	
	
	@RequestMapping(value = "/gms/order/popupOrder.do")
	public String getPopupOrderDetail(@RequestParam(value = "orderId", required = false) Integer orderId, Model model)	{
		
		logger.debug(" getPopupOrderDetail");
		
		OrderExtCustomerVO result = orderService.getOrderPopup(orderId);			
		
		model.addAttribute("orderExt", result.getOrderExt());		
//		if(result.getOrderExt().getOrder().getOrderTotalAmount() < 0) {
//			double orderTotalAmount = result.getOrderExt().getOrder().getOrderTotalAmount() * -1;
//			model.addAttribute("orderTotalAmountHan","-"+StringUtils.numberToHan(String.valueOf(Math.round(orderTotalAmount))));
//		}else
//			model.addAttribute("orderTotalAmountHan",StringUtils.numberToHan(String.valueOf(Math.round(result.getOrderExt().getOrder().getOrderTotalAmount()))));
		model.addAttribute("customer", result.getCustomer());
		
		//Customer_Product 목록
		List<CustomerProductVO> productList = customerService.getCustomerProductList(result.getCustomer().getCustomerId());		
		
		List<CustomerProductVO> rentBottleList = new ArrayList<CustomerProductVO>();
		StringBuffer rentList = new StringBuffer();
		
		for(int i =0 ; i < productList.size() ; i++) {
			CustomerProductVO customerProduct = productList.get(i);
			
			if(customerProduct.getBottleRentCount() != 0) {
				rentBottleList.add(customerProduct);
				rentList.append(customerProduct.getProductNm()).append(" ").append(customerProduct.getBottleRentCount()).append("ea");
				if(i < productList.size()-1) rentList.append(" / ");
			}
			
		}
		model.addAttribute("rentBottleList", rentBottleList);
		if(rentBottleList.size() > 3 && rentBottleList.size()%3 > 0)
			model.addAttribute("rentBottleListCount", rentBottleList.size()+1);
		
		model.addAttribute("rentBottle", rentList.toString());
		
		CashFlowVO cashFlow = new CashFlowVO();
		cashFlow.setCustomerId(result.getCustomer().getCustomerId());
		CashSumVO cashSum = cashService.getCashFlowSum(cashFlow);
		if(cashSum == null) {
			cashSum = new CashSumVO();
			cashSum.setIncomeAmountSum(0);
			cashSum.setReceivableAmountNet(0);
			cashSum.setReceivableAmountSum(0);
		}
		model.addAttribute("cashSum", cashSum);
		
		return "gms/order/popupOrder";
	}
	
	@RequestMapping(value = "/gms/order/popupOrderAll.do")
	public String getPopupOrderDetailAll(OrderVO param, Model model)	{
		
		logger.debug(" getPopupOrderDetailAll");
		
		ArrayList<OrderPrintVO> orderPrintList = new ArrayList<OrderPrintVO>();
		//OrderVO tOrder = new OrderVO();
		//tOrder.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.receive"));
		String searchOrderDt = param.getSearchOrderDt();	
		String searchOrderDtFrom = null;
		String searchOrderDtEnd = null;
				
		if(searchOrderDt != null && searchOrderDt.length() > 20) {						
			searchOrderDtFrom = searchOrderDt.substring(0, 10) ;			
			searchOrderDtEnd = searchOrderDt.substring(13, searchOrderDt.length()) ;
			
			param.setSearchOrderDtFrom(searchOrderDtFrom);
			param.setSearchOrderDtEnd(searchOrderDtEnd);			
		}
		
		List<OrderVO> orderList = orderService.getOrderReqDtTomorrow(param);
		Integer orderId= 0;
		for(int i=0 ; i< orderList.size() ; i++) {
			OrderPrintVO orderPrint = new OrderPrintVO();
			orderId = orderList.get(i).getOrderId();
			
			OrderExtVO result = orderService.getOrder(orderId);				
			
			CustomerVO customer = customerService.getCustomerDetails(result.getOrder().getCustomerId());	
						
			//Customer_Product 목록
			List<CustomerProductVO> productList = customerService.getCustomerProductList(result.getOrder().getCustomerId());		
			
			List<CustomerProductVO> rentBottleList = new ArrayList<CustomerProductVO>();
			StringBuffer rentList = new StringBuffer();
			for(int j =0 ; j < productList.size() ; j++) {
				CustomerProductVO customerProduct = productList.get(j);

				if(customerProduct.getBottleRentCount() != 0) {
					rentBottleList.add(customerProduct);
					rentList.append(customerProduct.getProductNm()).append(" ").append(customerProduct.getBottleRentCount()).append("ea");
					if(i < productList.size()-1) rentList.append(" / ");
				}
			}
			/*
			//model.addAttribute("rentBottleList", rentBottleList);
			if(rentBottleList.size() > 3 && rentBottleList.size()%3 > 0)
				model.addAttribute("rentBottleListCount", rentBottleList.size()+1);
			*/
			//model.addAttribute("rentBottle", rentList.toString());
			
			CashFlowVO cashFlow = new CashFlowVO();
			cashFlow.setCustomerId(result.getOrder().getCustomerId());
			CashSumVO cashSum = cashService.getCashFlowSum(cashFlow);
			if(cashSum == null) {
				cashSum = new CashSumVO();
				cashSum.setIncomeAmountSum(0);
				cashSum.setReceivableAmountNet(0);
				cashSum.setReceivableAmountSum(0);				
			}
			//model.addAttribute("cashSum", cashSum);			
//			logger.debug("****************OrderContoller after service result.getOrder().getOrderTotalAmount()=="+result.getOrder().getOrderTotalAmount());
			orderPrint.setOrderExt(result);
//			if(result.getOrder().getOrderTotalAmount() < 0) {
//				double orderTotalAmount = result.getOrder().getOrderTotalAmount() * -1;
//				orderPrint.setOrderTotalAmountHan("-"+StringUtils.numberToHan(String.valueOf(Math.round(orderTotalAmount))));
//			}else 
//				orderPrint.setOrderTotalAmountHan(StringUtils.numberToHan(String.valueOf(Math.round(result.getOrder().getOrderTotalAmount()))));
			orderPrint.setCustomer(customer);
			orderPrint.setRentBottle(rentList.toString());
			//orderPrint.setCustomerProduct(rentBottleList);
			orderPrint.setCashSum(cashSum);

			orderPrintList.add(orderPrint);
		}
		model.addAttribute("orderPrintList",orderPrintList);		
		
		return "gms/order/popupOrderAll";
	}
	
}

package com.gms.web.admin.controller.manage;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.OrderService;
import com.gms.web.admin.service.manage.UserService;

@Controller
public class MypageController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * Service 빈(Bean) 선언
	 */
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CustomerService customerService;
	
	@RequestMapping(value = "/gms/mypage/assign.do")
	public ModelAndView getAssignList(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {

		//logger.info(" getAssignList");
		
		RequestUtils.initUserPrgmInfo(request, params);				
		
		ModelAndView mav = new ModelAndView();		
		
		List<OrderVO> orderList = orderService.getSalseOrderList(params.getCreateId());
		
		List<CustomerVO> carList = customerService.searchCustomerListCar();
		mav.addObject("carList", carList);	
		
		mav.addObject("orderList", orderList);	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.assign"));	 	
		
		mav.setViewName("gms/mypage/assign");
		
		return mav;
	}
	
	@RequestMapping(value = "/gms/mypage/print.do")
	public ModelAndView getAssignListPrint(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {

		//logger.info(" getAssignListPrint");
		
		RequestUtils.initUserPrgmInfo(request, params);			
		
		ModelAndView mav = new ModelAndView();		
		
		List<OrderVO> orderList = null;
		
		if(params.getSalesId() != null) {
			orderList = orderService.getSalseOrderList(params.getSalesId());
			mav.addObject("salesId", params.getSalesId());	
		}else {
			orderList = orderService.getSalseOrderList(params.getCreateId());
		}
		
		mav.addObject("orderList", orderList);	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.assign"));	 	
		
		mav.setViewName("gms/mypage/print");
		
		return mav;
	}
	
	
	
	@RequestMapping(value = "/gms/mypage/update.do")
	public ModelAndView getMyInfoUpdate(
			HttpServletRequest request
			, HttpServletResponse response) {

		//logger.info(" getMyInfoUpdate");
		
		LoginUserVO loginUser = (LoginUserVO)request.getSession().getAttribute(LoginUserVO.ATTRIBUTE_NAME);
		
		//RequestUtils.initUserPrgmInfo(request, params);	
		
		ModelAndView mav = new ModelAndView();				
		
		UserVO user = userService.getUserDetails(loginUser.getUserId());
			
		mav.addObject("user", user);		 	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.self"));	 
		mav.setViewName("gms/mypage/update");
		
		return mav;
	}
	
	@RequestMapping(value = "/gms/mypage/modify.do", method = RequestMethod.POST)
	public ModelAndView modifyUser(HttpServletRequest request
			, HttpServletResponse response
			, UserVO params) {
		//logger.info(" modifyUser");
		
		ModelAndView mav = new ModelAndView();		
		
		RequestUtils.initUserPrgmInfo(request, params);		
		params.setUpdateId(params.getCreateId());
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.self"));	
		
		boolean result = userService.modifyUser(params);
		
		if(result){
			String alertMessage = "수정되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/mypage/update.do");
		}else {
			String alertMessage = "오류가 발생하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/mypage/update.do");
		}
		return null;
		
	}
	
	
	@RequestMapping(value = "/gms/mypage/assignAll.do")
	public ModelAndView getAssignListAll(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {

		//logger.info(" getAssignList");
		
		RequestUtils.initUserPrgmInfo(request, params);				
		
		ModelAndView mav = new ModelAndView();		
		
		UserVO tempUser = new UserVO();		
		List<UserVO> userList = userService.getUserListPart(tempUser);
		mav.addObject("userList",userList);
				
		List<OrderVO> orderList = null;
		
		if(params.getSalesId() == null && userList.size() > 0) params.setSalesId(userList.get(0).getUserId());
		if(params.getSalesId() != null) {
			orderList = orderService.getSalseOrderList(params.getSalesId());
			mav.addObject("salesId", params.getSalesId());	
		}else {
			orderList = orderService.getSalseOrderList(params.getCreateId());
		}		
		
		List<CustomerVO> carList = customerService.searchCustomerListCar();
		mav.addObject("carList", carList);			
		
		mav.addObject("orderList", orderList);	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.assign"));	 	
		
		mav.setViewName("gms/mypage/assignAll");
		
		return mav;
	}
}

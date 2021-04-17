package com.gms.web.admin.controller.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerBottleVO;
import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.OrderService;
import com.gms.web.admin.service.manage.ProductService;
import com.gms.web.admin.service.manage.UserService;
import com.gms.web.admin.service.manage.WorkReportService;

@Controller
public class CustomerController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * CustomerService 빈(Bean) 선언
	 */
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private WorkReportService workService;
	
	
	@RequestMapping(value = "/gms/customer/list.do")
	public String openCustomerList(CustomerVO params, Model model) {
		
		Map<String, Object> map = customerService.getCustomerList(params);

		model.addAttribute("customerList", map.get("list"));
		model.addAttribute("searchCustomerNm", params.getSearchCustomerNm());
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));	
		model.addAttribute("rowPerPage", params.getRowPerPage());
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));
		
		return "gms/customer/list";
	}

	
	@RequestMapping(value = "/gms/customer/write.do")
	public String openCustomerWrite(@RequestParam(value = "customerId", required = false) String customerId, Model model) {
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));
		
		UserVO param = new UserVO();
		param.setUserPartCd(PropertyFactory.getProperty("common.user.SALES"));
		
		List<CustomerSimpleVO> agencyList = customerService.getAgencyCustomerList();
		model.addAttribute("agencyList", agencyList);
		
		List<UserVO> userList = userService.getUserListPart(param);
		model.addAttribute("userList", userList);
		
		//Map<String, Object> map1 = productService.getProductList();
		model.addAttribute("productList", productService.getProductList());
		
					
		return "gms/customer/write";
	}
	
	@RequestMapping(value = "/gms/customer/register.do", method = RequestMethod.POST)
	public String registerCustomer(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, CustomerVO params) {
		logger.info(" registerCustomer");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));
		
		try {
			//임시
			params.setMemberCompSeq(1);
			
			//ID 중복체크			
			boolean result = customerService.registerCustomer(params);
			if (result == false) {
				logger.error(" registerCustomer Exception===");
			}
		} catch (DataAccessException e) {
			logger.error(" registerCustomer Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" registerCustomer Exception==="+e.toString());
			e.printStackTrace();
		}
	
		return "redirect:/gms/customer/list.do";
	}

	@RequestMapping(value = "/gms/customer/update.do")
	public String openCustomerUpdate(@RequestParam(value = "customerId", required = false) Integer customerId, 
			HttpServletRequest request , HttpServletResponse response,Model model) {

		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));		
		model.addAttribute("customerId", customerId);
		model.addAttribute("searchCustomerNm", request.getParameter("searchCustomerNm"));
		if(request.getParameter("currentPage")!=null)
			model.addAttribute("currentPage",request.getParameter("currentPage"));
		else
			model.addAttribute("currentPage","1");
		
		String old_url = request.getHeader("referer");
		
		if (customerId == null) {
			return "redirect:/gms/customer/list.do";
		} else {
			
			CustomerVO customer = customerService.getCustomerDetails(customerId);
						
			if (customer == null) {
				return "redirect:/gms/customer/list.do";
			}
			
			model.addAttribute("customer", customer);
			
			UserVO param = new UserVO();
			param.setUserPartCd(PropertyFactory.getProperty("common.user.SALES"));
			
			List<UserVO> userList = userService.getUserListPart(param);
			model.addAttribute("userList", userList);					

			List<CustomerSimpleVO> agencyList = customerService.getAgencyCustomerList();
			model.addAttribute("agencyList", agencyList);			
			
			// Customer_Price
			List<CustomerPriceExtVO> customerPriceList = customerService.getCustomerPriceList(customerId) ;
			model.addAttribute("customerPriceList", customerPriceList);	
			
			//Customer_Product 목록
			List<CustomerProductVO> productList = customerService.getCustomerProductList(customerId);
			
			List<CustomerProductVO> ownBottleList = new ArrayList<CustomerProductVO>();
			List<CustomerProductVO> rentBottleList = new ArrayList<CustomerProductVO>();
			
			for(int i =0 ; i < productList.size() ; i++) {
				CustomerProductVO customerProduct = productList.get(i);
				
				if(customerProduct.getBottleOwnCount() != 0) {
					ownBottleList.add(customerProduct);
				}
				if(customerProduct.getBottleRentCount() != 0) {
					rentBottleList.add(customerProduct);
				}
			}
			model.addAttribute("ownBottleList", ownBottleList);	
			model.addAttribute("rentBottleList", rentBottleList);				
			
			BottleVO bottle = new BottleVO();
			bottle.setCustomerId(customerId);
			//bottle.
			String searchChargeDt = request.getParameter("searchChargeDt");					
			
			String searchChargeDtFrom = null;
			String searchChargeDtEnd = null;
			boolean firstFlag = false;		
			
			if(searchChargeDt != null && searchChargeDt.length() > 20) {
				bottle.setSearchChargeDt(searchChargeDt);		
				
				searchChargeDtFrom = searchChargeDt.substring(0, 10) ;				
				searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;				
				
			}else {
				firstFlag = true;
				searchChargeDtFrom = DateUtils.getDate("yyyy/MM/dd");				
				searchChargeDtEnd = DateUtils.getDate("yyyy/MM/dd");
				
				searchChargeDt = searchChargeDtFrom+" - "+searchChargeDtEnd;
				
				bottle.setSearchChargeDt(searchChargeDt);				
			}
			bottle.setSearchChargeDtFrom(searchChargeDtFrom);
			bottle.setSearchChargeDtEnd(searchChargeDtEnd);
			
			model.addAttribute("searchChargeDt",searchChargeDt);
			
			List<BottleVO> bottleList = bottleService.getCustomerBottleListDate(bottle);
			if(bottleList.size()==0 && firstFlag) {
				BottleVO recentBottle = bottleService.getCustomerBottleRecent(customerId);
				if(recentBottle !=null) {
					bottle.setSearchChargeDtFrom(DateUtils.convertDateFormat(recentBottle.getUpdateDt(),"yyyy/MM/dd"));
					bottle.setSearchChargeDtEnd(DateUtils.convertDateFormat(recentBottle.getUpdateDt(),"yyyy/MM/dd"));		
					
					searchChargeDt = DateUtils.convertDateFormat(recentBottle.getUpdateDt(),"yyyy/MM/dd")+" - "+DateUtils.convertDateFormat(recentBottle.getUpdateDt(),"yyyy/MM/dd");
					bottle.setSearchChargeDt(searchChargeDt); 
					
					bottleList = bottleService.getCustomerBottleListDate(bottle);
					model.addAttribute("searchChargeDt",searchChargeDt);
					
				}
			}
			
			model.addAttribute("bottleList", bottleList);		
		}
		
		return "gms/customer/update";
	}
	
	@RequestMapping(value = "/gms/customer/modify.do", method = RequestMethod.POST)
	public ModelAndView modifyCustomer(HttpServletRequest request
			, HttpServletResponse response
			, CustomerVO params) {
		
		ModelAndView mav = new ModelAndView();	
		
		logger.info(" modifyCustomer");
		
		RequestUtils.initUserPrgmInfo(request, params);
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.customer"));
		
		String searchCustomerNm = params.getSearchCustomerNm();
		boolean result = false;
		try {			
			//logger.debug("******params.getCustomerId()()) *****===*"+params.getCustomerId());
			mav.addObject("currentPage", params.getCurrentPage());
			mav.addObject("searchCustomerNm", params.getSearchCustomerNm());
			result = customerService.modifyCustomer(params);
			
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			}
		} catch (DataAccessException e) {
			logger.error(" modifyCustomer Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" modifyCustomer Exception==="+e.toString());
			e.printStackTrace();
		}
		
		if(result){
			String alertMessage = "수정되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/customer/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+searchCustomerNm);
		}
		return null;
		//return "/gms/customer/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+searchCustomerNm;
	}
	
	
	@RequestMapping(value = "/gms/customer/delete.do", method = RequestMethod.POST)
	public ModelAndView deleteCustomer(HttpServletRequest request
			, HttpServletResponse response
			, CustomerVO param) {		
		
		logger.info(" deleteCustomer");
		
		ModelAndView mav = new ModelAndView();	
		RequestUtils.initUserPrgmInfo(request, param);		
		
		String searchCustomerNm = param.getSearchCustomerNm();
		
		int result = 0;
		try {			
			//logger.debug("******params.getCustomerId()()) *****===*"+param.getCustomerId());
			mav.addObject("menuId", PropertyFactory.getProperty("common.menu.customer"));
			mav.addObject("currentPage", param.getCurrentPage());
			mav.addObject("searchCustomerNm", param.getSearchCustomerNm());
			/*
			if(bottleService.getCustomerBottleList(param.getCustomerId()).size() > 0 ) 
				result = -1;
			else */
			result = customerService.deleteCustomer(param);			
			
		} catch (DataAccessException e) {
			logger.error(" deleteCustomer Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" deleteCustomer Exception==="+e.toString());
			e.printStackTrace();
		}
		
		if(result > 0){
			String alertMessage = "삭제되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/customer/list.do?currentPage="+param.getCurrentPage()+"&searchCustomerNm="+searchCustomerNm);
		/*
		}else if(result == -1) {
			String alertMessage = "삭제되었습니다.";
			http://27.96.134.138/gms/customer/update.do?customerId=5393
			RequestUtils.responseWriteException(response, alertMessage, "/gms/customer/list.do?currentPage="+param.getCurrentPage()+"&searchCustomerNm="+searchCustomerNm);
		*/
		}
		return null;
		//return "/gms/customer/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+searchCustomerNm;
	}
	
	@RequestMapping(value = "/gms/customer/detail.do")
	@ResponseBody
	public CustomerVO getCustomerDetails(@RequestParam(value = "customerId", required = false) Integer customerId, Model model)	{
		
		CustomerVO result = customerService.getCustomerDetails(customerId);
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));
		/*
		if(result != null) logger.info("******result *****===*"+result.getCustomerId());
		else logger.info("******result is null  *****===*"); 
		*/
		return result;
	}
	
	
	@RequestMapping(value = "/gms/customer/CheckBusiId.do")
	@ResponseBody
	public Object checkBusinessRegId(CustomerVO param){
		Map<String, Object> result = customerService.checkBusinessRegIdDuplicate(param);
		
		return result;
	}
	
	
	@RequestMapping(value = "/gms/price/write.do")
	public ModelAndView openCustomerPriceWrite(@RequestParam(value = "searchCustomerNm", required = false) String searchCustomerNm, Model model) {
		
		ModelAndView mav = new ModelAndView();
		//model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer.price"));
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.customer.price"));	
		
		Map<String, Object> map = customerService.searchCustomerList(searchCustomerNm);
		//model.addAttribute("customerList", map.get("list"));
		mav.addObject("customerList", map.get("list"));	

		//return "/gms/price/write";
		mav.setViewName("gms/price/write");
		return mav;
	}
	
	@RequestMapping(value = "/gms/price/register.do", method = RequestMethod.POST)
	public String registerCustomerPrice(HttpServletRequest request
			, HttpServletResponse response
			, Model model) {
		
		logger.info(" registerCustomerPrice");	
		
		try {
			
			boolean result = false;
			
			model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));				
			
			String[] productIdArray = request.getParameterValues("productId");
			String[] productPriceSeqArray = request.getParameterValues("productPriceSeq");
			String[] productPriceArray = request.getParameterValues("productPrice");
			String[] productBottlePriceArray = request.getParameterValues("productBottlePrice");

			
			int priceCount  = productIdArray.length;
			
			
			//result = customerService.deleteCustomerPrice(Integer.parseInt(request.getParameter("customerId1")));
			CustomerPriceVO[] customerPrice = new CustomerPriceVO[priceCount];
			String bottlePrice = "";
			for(int i =0 ; i < priceCount ; i++ ) {
				
				CustomerPriceVO priceVo = new CustomerPriceVO();
				bottlePrice = "";
				
				RequestUtils.initUserPrgmInfo(request, priceVo);
				result = false;			
				priceVo.setCustomerId(Integer.parseInt(request.getParameter("customerId1")));
				priceVo.setProductId(Integer.parseInt(productIdArray[i]));
				priceVo.setProductPriceSeq(Integer.parseInt(productPriceSeqArray[i]));
				priceVo.setProductPrice(Float.parseFloat(productPriceArray[i]));
				priceVo.setProductBottlePrice(Float.parseFloat(productBottlePriceArray[i]));
				
//				priceVo.setCustomerId(Integer.parseInt(request.getParameter("customerId1")));
//				priceVo.setProductId(Integer.parseInt(request.getParameter("productId_"+i)));
//				priceVo.setProductPriceSeq(Integer.parseInt(request.getParameter("productPriceSeq_"+i)));
//				priceVo.setProductPrice(Float.parseFloat(request.getParameter("productPrice_"+i)));
//				if(request.getParameter("productBottlePrice_"+i) != null && request.getParameter("productBottlePrice_"+i).length() > 0 )	bottlePrice = request.getParameter("productBottlePrice_"+i);
//				else bottlePrice = "0";
//				priceVo.setProductBottlePrice(Float.parseFloat(bottlePrice));
//					
				customerPrice[i] = priceVo;				
			}
			int result1 = 1;
			if(customerPrice.length == 0 && request.getParameter("customerId1")!=null && request.getParameter("customerId1").length() > 0) 
				result = customerService.deleteCustomerPrice(Integer.parseInt(request.getParameter("customerId1")));
			else {
				result = customerService.registerCustomerPrice(customerPrice);
			
//				List<WorkBottleVO> workBottleList = workService.getWorkBottleListOfCustomerToday(Integer.parseInt(request.getParameter("customerId1")));
//				for(int i =0 ; i < workBottleList.size() ; i++) {
//					for(int j=0 ; j < customerPrice.length ; j++) {
//						if(workBottleList.get(i).getProductId() == customerPrice[j].getProductId() && workBottleList.get(i).getProductPriceSeq() == customerPrice[j].getProductPriceSeq()) {
//													
//							if(workBottleList.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))	) {
//								workBottleList.get(i).setProductPrice(customerPrice[j].getProductBottlePrice());
//								
//							}else if(workBottleList.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))
//									|| workBottleList.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.title.salesgas")) 
//									|| workBottleList.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent"))  ) {
//								
//								workBottleList.get(i).setProductPrice(customerPrice[j].getProductPrice());
//							}
//							
//							result1 = workService.modifyWorkBottlePrice(workBottleList.get(i));
//						}
//					}
//				}			
			}
			// 당일 거래 내역 업데이트
			//if(res)
			
		} catch (DataAccessException de) {
			logger.error(" registerCustomerPrice Exception==="+de.toString());
			de.printStackTrace();
		} catch (Exception e) {
			logger.error(" registerCustomerPrice Exception==="+e.toString());
			e.printStackTrace();
		}
	
		return "redirect:/gms/price/write.do";
		//return null;
	}
	
	
	@RequestMapping(value = "/gms/cbottle/write.do")
	public ModelAndView openCustomerBottleWrite(@RequestParam(value = "searchCustomerNm", required = false) String searchCustomerNm, Model model) {
		
		ModelAndView mav = new ModelAndView();
		//model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer.price"));
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.customer.price"));	
		
		Map<String, Object> map = customerService.searchCustomerList(searchCustomerNm);
		//model.addAttribute("customerList", map.get("list"));
		mav.addObject("customerList", map.get("list"));	
		//model.addAttribute("productList", productService.getProductList());		
					
		UserVO user = new UserVO();
		user.setUserPartCd(PropertyFactory.getProperty("common.user.part.sales"));
		List<UserVO> userList = userService.getUserListPart(user) ;
		//Map<String, Object> map1 = userService.getUserList(user);
		mav.addObject("userList", userList);			
		
		mav.setViewName("gms/cbottle/write");
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/cbottle/register.do", method = RequestMethod.POST)
	public String registerCustomerBottle(HttpServletRequest request
			, HttpServletResponse response
			, Model model) {
		
		logger.info(" registerCustomerBottle");	
		
		try {
			model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer"));		
			int priceCount  = Integer.parseInt(request.getParameter("priceCount"));
			
			int result=0;

			List<CustomerBottleVO> cBottleList = new ArrayList<CustomerBottleVO>();	
			
			for(int i =0 ; i < priceCount ; i++ ) {
				
				CustomerBottleVO cBottle = new CustomerBottleVO();
				
				RequestUtils.initUserPrgmInfo(request, cBottle);					
				
				cBottle.setCustomerId(Integer.parseInt(request.getParameter("customerId1")));
				cBottle.setProductId(Integer.parseInt(request.getParameter("productId_"+i)));
				cBottle.setProductPriceSeq(Integer.parseInt(request.getParameter("productPriceSeq_"+i)));
				cBottle.setRentCount(Integer.parseInt(request.getParameter("rentCount_"+i)));
				cBottle.setBackCount(Integer.parseInt(request.getParameter("backCount_"+i)));
				cBottle.setSalesId(request.getParameter("salesId_"+i));
				
				cBottleList.add(cBottle);				
			}
			
			result = customerService.registerCustomerBottles(cBottleList);
			//ID 중복체크			
			
		} catch (DataAccessException e) {
			logger.error(" registerCustomerBottle Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" registerCustomerBottle Exception==="+e.toString());
			e.printStackTrace();
		}
	
		return "redirect:/gms/cbottle/write.do";
		//return null;
	}
	
	@RequestMapping(value = "/gms/price/customerList.do")
	@ResponseBody
	public List<CustomerPriceExtVO> getCustomerProductPriceList(@RequestParam(value = "customerId", required = false) Integer customerId, Model model)	{	
		
		List<CustomerPriceExtVO> customerPriceList = customerService.getCustomerPriceList(customerId);
		model.addAttribute("customerPriceList", customerPriceList);
		
		return customerPriceList;
		//return null;
	}
	
	@RequestMapping(value = "/gms/common/customerList.do")
	@ResponseBody
	public List<CustomerVO> getCustomerList(@RequestParam(value = "searchCustomerNm", required = false) String searchCustomerNm, Model model)	{	
		
		Map<String, Object> map = customerService.searchCustomerList(searchCustomerNm);
		//model.addAttribute("customerList", map.get("list"));
		List<CustomerVO> customerPriceList = (List<CustomerVO>) map.get("list");
		return customerPriceList;
		//return null;
	}
	
	@RequestMapping(value = "/gms/cbottle/cBottleList.do")
	@ResponseBody
	public List<CustomerBottleVO> getCustomerBottleList(@RequestParam(value = "customerId", required = false) Integer customerId)	{			
						
		return customerService.getCustomerBottleList(customerId);

	}
	
	@RequestMapping(value = "/gms/customer/cProductBottleList.do")
	@ResponseBody
	public List<CustomerProductVO> getCustomerProductBottleList(@RequestParam(value = "customerId", required = false) Integer customerId)	{			
						
		return customerService.getCustomerProductList(customerId);

	}
	
	@RequestMapping(value = "/gms/customer/bottle.do")
	public String getCustomerRentBottle(@RequestParam(value = "searchCustomerNm", required = false) String searchCustomerNm, Model model) {
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.customer.cproduct"));
		
		Map<String, Object> map = customerService.searchCustomerList(searchCustomerNm);
		
		model.addAttribute("customerList", map.get("list"));	
		model.addAttribute("searchCustomerNm",searchCustomerNm);		
					
		return "gms/customer/bottle";
	}
	
	@RequestMapping(value = "/gms/customer/bottleRegister.do", method = RequestMethod.POST)
	public ModelAndView registerCustomerProductBottle(HttpServletRequest request
			, HttpServletResponse response ) {
		
		logger.info(" registerCustomerBottle");	
		ModelAndView mav = new ModelAndView();	
		int result=0;
		try {
			mav.addObject("menuId", PropertyFactory.getProperty("common.menu.customer.cproduct"));
			mav.addObject("searchCustomerNm", request.getParameter("searchCustomerNm1"));
			int priceCount  = Integer.parseInt(request.getParameter("priceCount"));

			List<CustomerProductVO> cBottleList = new ArrayList<CustomerProductVO>();	
			
			for(int i =0 ; i < priceCount ; i++ ) {
				
				CustomerProductVO cBottle = new CustomerProductVO();
				
				RequestUtils.initUserPrgmInfo(request, cBottle);					
				
				cBottle.setCustomerId(Integer.parseInt(request.getParameter("customerId1")));
				cBottle.setProductId(Integer.parseInt(request.getParameter("productId_"+i)));
				cBottle.setProductPriceSeq(Integer.parseInt(request.getParameter("productPriceSeq_"+i)));
				cBottle.setBottleRentCount(Integer.parseInt(request.getParameter("bottleRentCount_"+i)));
				cBottle.setBottleOwnCount(Integer.parseInt(request.getParameter("bottleOwnCount_"+i)));
				
				if(cBottle.getBottleRentCount() > 0 || cBottle.getBottleOwnCount() > 0)
					cBottleList.add(cBottle);				
			}
			
			result = customerService.deleteCustomerProducts(Integer.parseInt(request.getParameter("customerId1")));
			
			if(cBottleList.size() > 0) {	
				result = customerService.registerCustomerProducts(cBottleList);
			}else {
				result = -3;
			}
			//ID 중복체크			
			
		} catch (DataAccessException e) {
			logger.error(" registerCustomerProductBottle Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" registerCustomerProductBottle Exception==="+e.toString());
			e.printStackTrace();
		}
		if(result > 0){
			String alertMessage = "저장되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/customer/bottle.do?searchCustomerNm="+request.getParameter("searchCustomerNm1"));
		}else if(result ==-3) {
			String alertMessage = "등록할 정보가 없습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/customer/bottle.do?searchCustomerNm="+request.getParameter("searchCustomerNm1"));
		}
		return null;
		
	}
	
	@RequestMapping(value = "/api/customerList.do")
	@ResponseBody
	public List<CustomerSimpleVO> getCustomerList(@RequestParam(value = "searchCustomerNm", required = false) String searchCustomerNm)	{	
				
		List<CustomerSimpleVO> customerList = customerService.searchCustomerSimpleList(searchCustomerNm);
		//String customerList = customerService.searchCustomerSimpleListString(searchCustomerNm);
		
		return customerList;
		//return null;
	}	
	
	@RequestMapping(value = "/api/carList.do")
	@ResponseBody
	public List<CustomerSimpleVO> getCarList()	{
		logger.info("CustomerContoller getCarList");
		List<CustomerSimpleVO> customerList = null;
		try {	
			//customerList = customerService.getCarSimpleList("Y");
			customerList = customerService.searchCustomerSimpleList("");
		}catch(Exception e) {
			return null;
		}
		
		return customerList;
		//return null;
	}
	
	@RequestMapping(value = "/api/gasCustomerList.do")
	@ResponseBody
	public List<CustomerSimpleVO> getGasCustoerList()	{
		logger.info(" getCarList");

		List<CustomerSimpleVO> customerList = customerService.searchCustomerSimpleList("");
		return customerList;
	}
	
	@RequestMapping(value = "/api/customerAllList.do")
	@ResponseBody
	public List<CustomerSimpleVO> getCustomerAllList()	{	
				
		logger.info(" getCustomerAllList");
		
		List<CustomerSimpleVO> customerList = customerService.searchCustomerSimpleList("");
		return customerList;
	}
	
	@RequestMapping(value = "/api/customerAllList1.do")
	@ResponseBody
	public String getCustomerListString(@RequestParam(value = "searchCustomerNm", required = false)  String searchCustomerNm)	{	
		
		logger.info(" getCustomerListString");
						
		String customerList = customerService.searchCustomerSimpleListString(searchCustomerNm);
		
		return customerList;
	}
	
	@RequestMapping(value = "/api/customerRentBottle.do")
	@ResponseBody
	public List<CustomerProductVO> getCustomerRentBottleList(@RequestParam(value = "customerId", required = false)  Integer customerId)	{	
		
		logger.info(" getCustomerRentBottleList");
						
		List<CustomerProductVO> productList = customerService.getCustomerProductList(customerId);	
		List<CustomerProductVO> rentBottleList = new ArrayList<CustomerProductVO>();
		
		for(int i =0 ; i < productList.size() ; i++) {
			CustomerProductVO customerProduct = productList.get(i);
			
			if(customerProduct.getBottleRentCount() != 0) {
				rentBottleList.add(customerProduct);
			}
		}
		return rentBottleList;
		
	}
	
}

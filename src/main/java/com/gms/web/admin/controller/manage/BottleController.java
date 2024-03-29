package com.gms.web.admin.controller.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.common.CodeVO;
import com.gms.web.admin.domain.manage.BottleHistoryVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.domain.manage.ProductNewVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.ProductVO;
import com.gms.web.admin.service.common.CodeService;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.GasService;
import com.gms.web.admin.service.manage.ProductService;

@Controller
public class BottleController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private GasService gasService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private CustomerService customerService;
	
	
	@RequestMapping(value = "/gms/bottle/list.do")
	public String getBottleList(BottleVO params, Model model) {

//		logger.debug("getBottleList");
		
		String searchChargeDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
				
		if(searchChargeDt != null && searchChargeDt.length() > 20) {
			searchChargeDtFrom = searchChargeDt.substring(0, 10) ;			
			searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;
			
			params.setSearchChargeDtFrom(searchChargeDtFrom);
			params.setSearchChargeDtEnd(searchChargeDtEnd);			
		}
		
		String searchDt = params.getSearchDt();	
		
		String searchDtFrom = null;
		String searchDtEnd = null;
				
		if(searchDt != null && searchDt.length() > 20) {
			searchDtFrom = searchDt.substring(0, 10) ;			
			searchDtEnd = searchDt.substring(13, searchDt.length()) ;
			
			params.setSearchDtFrom(searchDtFrom);
			params.setSearchDtEnd(searchDtEnd);			
		}
		
		//params.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.come"));
		
		Map<String, Object> map = bottleService.getBottleList(params);
		
		model.addAttribute("bottleList", map.get("list"));
		
		String searchProductId = "";
		if(params.getSearchProductId() != null && params.getSearchProductId().length() > 0 ) {
			searchProductId = params.getSearchProductId();
			model.addAttribute("searchProductId", Integer.parseInt(searchProductId));
			model.addAttribute("searchProductId1", Integer.parseInt(searchProductId));			
		}
		
		// 가스 정보 불러오기
		//List<GasVO> gasList = gasService.getGasList();
		//model.addAttribute("gasList", gasList);
//		logger.debug(" getBottleList ownCustomerId="+params.getOwnCustomerId());
//		logger.debug(" getBottleList searchCustomerNm1="+params.getSearchCustomerNm1());
		String ownCustomerId = "";
		if(params.getOwnCustomerId() !=null && params.getOwnCustomerId().length() > 0 ) ownCustomerId = params.getOwnCustomerId();
		
		List<CustomerSimpleVO> customerList = null;
		if(params.getSearchCustomerNm1()!=null && params.getSearchCustomerNm1().length() > 0)
			customerList = customerService.searchCustomerSimpleList(params.getSearchCustomerNm1());
		
		model.addAttribute("customerList", customerList);	
		model.addAttribute("searchCustomerNm1", params.getSearchCustomerNm1());
		if(params.getOwnCustomerId() !=null && params.getOwnCustomerId().length() > 0 ) 
			model.addAttribute("ownCustomerId", Integer.parseInt(params.getOwnCustomerId()) );
		
		// 상품 정보 불러오기
		List<ProductVO> productList = productService.getGasProductList();
		model.addAttribute("productList", productList);		
		
		//BOTTLE_WORK_CD 코드정보 불러오기 
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.bottle.status"));
		model.addAttribute("codeList", codeList);
		
		List<CustomerVO> carList = customerService.searchCustomerListCar();
		model.addAttribute("carList", carList);		
		
		//검색어 셋팅
		model.addAttribute("searchBottleId", params.getSearchBottleId());
		model.addAttribute("searchBottleBarCd", params.getSearchBottleBarCd());
		model.addAttribute("searchChargeDt", params.getSearchChargeDt());	
		model.addAttribute("searchDt", params.getSearchDt());	
		model.addAttribute("searchWorkCd", params.getSearchWorkCd() );		
		
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.bottle"));	 
		
		return "gms/bottle/list";
	}
	
	
	@RequestMapping(value = "/gms/bottle/charge.do")
	public String getBottleTestList(BottleVO params, Model model) {

//		logger.debug(" getBottleList");
		
		String searchChargeDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
				
		if(searchChargeDt != null && searchChargeDt.length() > 20) {
			
			//logger.debug("BottleContoller searchChargeDt "+ searchChargeDt.length());
			searchChargeDtFrom = searchChargeDt.substring(0, 10) ;
			
			searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;
			
			params.setSearchChargeDtFrom(searchChargeDtFrom);
			params.setSearchChargeDtEnd(searchChargeDtEnd);
			
		}else {		
			
			// Date 로 구하기
		    SimpleDateFormat fm1 = new SimpleDateFormat("yyyy/MM/dd");
		    String fromDate = fm1.format(new Date());
		    //logger.debug("현재시간 년월일 = " + fromDate);

		    Calendar cal = Calendar.getInstance();;
		    cal.setTime(new Date());
		    cal.add(Calendar.DAY_OF_YEAR, 7); // 하루를 더한다.
		    	    
		    String endDate = fm1.format(cal.getTime());
		    //logger.debug("현재시간 년월일 = " + endDate);
		    
		    searchChargeDt = fromDate+" - "+endDate;
		    
		    params.setSearchChargeDt(searchChargeDt);
			
			params.setSearchChargeDtFrom(fromDate);
			params.setSearchChargeDtEnd(endDate); 
			
		}		
		
		Map<String, Object> map = bottleService.getBottleList(params);
		
		model.addAttribute("bottleList", map.get("list"));
		
		//String searchProductId = "";
		if(params.getSearchProductId() != null && params.getSearchProductId().length() > 0 ) {
			//searchProductId = params.getSearchGasId();
			model.addAttribute("searchProductId", Integer.parseInt(params.getSearchProductId()));
		}
		
		// 가스 정보 불러오기
		//List<GasVO> gasList = gasService.getGasList();
		//model.addAttribute("gasList", gasList);
		
		// 상품 정보 불러오기
		List<ProductVO> productList = productService.getGasProductList();
		model.addAttribute("productList", productList);		
		
		//BOTTLE_WORK_CD 코드정보 불러오기 
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.bottle.status"));
		model.addAttribute("codeList", codeList);
		
		//검색어 셋팅
		model.addAttribute("searchBottleId", params.getSearchBottleId());	
		model.addAttribute("searchChargeDt", searchChargeDt);	
		
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));		
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.boottle.test"));	 
		
		return "gms/bottle/charge";
	}
	
	@RequestMapping(value = "/gms/bottle/sales.do")
	public String getBottleSalesList(BottleVO params, Model model) {

//		logger.debug(" getBottleList");
		
		String searchChargeDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
				
		if(searchChargeDt != null && searchChargeDt.length() > 20) {
			
			//logger.debug("BottleContoller searchChargeDt "+ searchChargeDt.length());
			searchChargeDtFrom = searchChargeDt.substring(0, 10) ;			
			searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;
			
			params.setSearchChargeDtFrom(searchChargeDtFrom);
			params.setSearchChargeDtEnd(searchChargeDtEnd);
			
		}		
		
		params.setSearchWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));		
		
		Map<String, Object> map = bottleService.getBottleList(params);
		
		model.addAttribute("bottleList", map.get("list"));
		
		if(params.getSearchProductId() != null && params.getSearchProductId().length() > 0 ) {
			//searchProductId = params.getSearchGasId();
			model.addAttribute("searchProductId", Integer.parseInt(params.getSearchProductId()));
		}
		
		// 가스 정보 불러오기
		//List<GasVO> gasList = gasService.getGasList();
		//model.addAttribute("gasList", gasList);
		
		// 상품 정보 불러오기
		List<ProductVO> productList = productService.getGasProductList();
		model.addAttribute("productList", productList);		
		
		//BOTTLE_WORK_CD 코드정보 불러오기 
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.bottle.status"));
		model.addAttribute("codeList", codeList);
		
		//검색어 셋팅
		model.addAttribute("searchBottleId", params.getSearchBottleId());	
		model.addAttribute("searchChargeDt", searchChargeDt);	
		
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.boottle.sales"));	 
		
		return "gms/bottle/sales";
	}
	
	@RequestMapping(value = "/gms/bottle/rental.do")
	public String getBottleRentalList(BottleVO params, Model model) {

		String searchChargeDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
				
		if(searchChargeDt != null && searchChargeDt.length() > 20) {
			
			//logger.debug("BottleContoller searchChargeDt "+ searchChargeDt.length());
			searchChargeDtFrom = searchChargeDt.substring(0, 10) ;			
			searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;
			
			params.setSearchChargeDtFrom(searchChargeDtFrom);
			params.setSearchChargeDtEnd(searchChargeDtEnd);
			
		}
		//params.setSearchSalesYn("Y");
		//params.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.rent"));
		params.setSearchWorkCd(PropertyFactory.getProperty("common.bottle.status.rent"));
		//logger.debug("BottleContoller searchSalesYn "+ params.getSearchSalesYn());
		
		Map<String, Object> map = bottleService.getBottleList(params);
		
		model.addAttribute("bottleList", map.get("list"));
		
		if(params.getSearchProductId() != null && params.getSearchProductId().length() > 0 ) {
			//searchProductId = params.getSearchGasId();
			model.addAttribute("searchProductId", Integer.parseInt(params.getSearchProductId()));
		}		
		
		// 상품 정보 불러오기
		List<ProductVO> productList = productService.getGasProductList();
		model.addAttribute("productList", productList);		
		
		//BOTTLE_WORK_CD 코드정보 불러오기 
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.bottle.status"));
		model.addAttribute("codeList", codeList);		
		//검색어 셋팅
		model.addAttribute("searchBottleId", params.getSearchBottleId());	
		model.addAttribute("searchChargeDt", searchChargeDt);	
		
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.boottle.rental"));	 
		
		return "gms/bottle/rental";
	}
	
	@RequestMapping(value = "/gms/bottle/write.do")
	public String openBottleWrite(Model model) {
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.bottle"));
	
		// 가스 정보 불러오기
		List<GasVO> gasList = gasService.getGasList();
		model.addAttribute("gasList", gasList);		
		
		// 상품 정보 불러오기
		List<ProductVO> productList = productService.getGasProductList();
		model.addAttribute("productList", productList);		
		
		// 대리점 정보 불러오기
		List<CustomerSimpleVO> agencyList = customerService.getAgencyCustomerList();
		model.addAttribute("agencyList", agencyList);
					
		return "gms/bottle/write";
	}
	
	@RequestMapping(value = "/gms/bottle/CheckBottleId.do")
	@ResponseBody
	public Object checkBottleId(BottleVO param){
		
		Map<String, Object> result = bottleService.checkBottleIdDuplicate(param);
		  
		return result;
	}
	
	
	@RequestMapping(value = "/gms/bottle/register.do", method = RequestMethod.POST)
	public ModelAndView registerBottle(HttpServletRequest request
			, HttpServletResponse response
			, BottleVO params) {
//		logger.info(" registerBottle");
		
		ModelAndView mav = new ModelAndView();
		RequestUtils.initUserPrgmInfo(request, params);
		int result = 0;
		
		try {
			//임시
			params.setMemberCompSeq(1);
			mav.addObject("menuId", PropertyFactory.getProperty("common.menu.bottle"));
			//ID 중복체크
			
			result = bottleService.registerBottle(params);
			if (result < 0) {
				
				logger.debug(" registerBottle error");
				
				String alertMessage = "에러가 되었습니다.";
				RequestUtils.responseWriteException(response, alertMessage, "/gms/bottle/list.do");
			}
		} catch (DataAccessException e) {
			logger.error(" registerBottle Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" registerBottle Exception==="+e.toString());
			e.printStackTrace();
		}
		if(result > 0){
			String alertMessage = "용기가 등록되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/bottle/list.do");
		}
		//return "redirect:/gms/bottle/list.do";
		return null;
	}
	
	@RequestMapping(value = "/gms/bottle/update.do")
	public String openBottleUpdate(BottleVO params, Model model) {
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.bottle"));
//		logger.debug(" openBottleUpdate bottleBarCd= "+ params.getSearchDt());
		
		if (params.getBottleBarCd() == null) {
			return "redirect:/gms/bottle/list.do";
		} else {
			
			BottleVO bottle = bottleService.getBottleDetail(params.getBottleBarCd());
			
			if (bottle == null) {
				return "redirect:/gms/bottle/list.do";
			}			
			
			// 상품 정보 불러오기
			List<ProductVO> productList = productService.getGasProductList();
			model.addAttribute("productList", productList);		
			
			List<ProductPriceVO> productPriceList = productService.getProductPriceList(bottle.getProductId());
			model.addAttribute("productPriceList", productPriceList);		
			
			// 용기 이력 정보 불러오기
			List<BottleHistoryVO> historyList = bottleService.selectBottleHistoryList(params.getBottleBarCd());
			model.addAttribute("historyList", historyList);	
			
			// 대리점 정보 불러오기
			List<CustomerSimpleVO> agencyList = customerService.getAgencyCustomerList();
			model.addAttribute("agencyList", agencyList);
			
			model.addAttribute("bottle", bottle);
			model.addAttribute("currentPage", params.getCurrentPage());
			model.addAttribute("searchProductId", params.getSearchProductId());
			model.addAttribute("searchBottleId", params.getSearchBottleId());
			model.addAttribute("searchChargeDt", params.getSearchChargeDt());
			model.addAttribute("searchDt", params.getSearchDt());
		}
		
		return "gms/bottle/update";
	}
	
	@RequestMapping(value = "/gms/bottle/modify.do", method = RequestMethod.POST)
	public String modifyBottle(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, BottleVO params) {
//		logger.info(" modifyBottle ");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		String searchChargetDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
				
		if(searchChargetDt != null && searchChargetDt.length() > 20) {			
			//logger.debug("BottleContoller searchChargeDt "+ searchChargetDt.length());
			searchChargeDtFrom = searchChargetDt.substring(0, 10) ;			
			searchChargeDtEnd = searchChargetDt.substring(13, searchChargetDt.length()) ;
			
			params.setSearchChargeDtFrom(searchChargeDtFrom);
			params.setSearchChargeDtEnd(searchChargeDtEnd);			
		}
		
		String searchDt = params.getSearchDt();	
		
		String searchDtFrom = null;
		String searchDtEnd = null;
				
		if(searchDt != null && searchDt.length() > 20) {			
			//logger.debug("BottleContoller searchChargeDt "+ searchChargetDt.length());
			searchDtFrom = searchDt.substring(0, 10) ;			
			searchDtEnd = searchDt.substring(13, searchDt.length()) ;
			
			params.setSearchDtFrom(searchDtFrom);
			params.setSearchDtEnd(searchDtEnd);			
		}else {
			params.setSearchDt("");
		}
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.bottle"));
		
		String searchProductId = "";
		if(params.getSearchProductId() != null ) searchProductId = params.getSearchProductId();		
		
		try {

			int  result = bottleService.modifyBottle(params);
			
			model.addAttribute("searchBottleId", params.getSearchBottleId());
			model.addAttribute("searchProductId", searchProductId);
			model.addAttribute("searchDt", params.getSearchDt());	
			model.addAttribute("searchChargeDt", params.getSearchChargeDt());			
			model.addAttribute("currentPage", params.getCurrentPage());
			
			if (result < 0) {				
				logger.error("BottleContoller modifyBottle error");
			}
		} catch (DataAccessException e) {
			logger.error(" modifyBottle Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" modifyBottle Exception==="+e.toString());
			e.printStackTrace();
		}
	
		return "redirect:/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchDt="+params.getSearchDt()+"&searchProductId="+params.getSearchProductId();
	}
	
	
	@RequestMapping(value = "/gms/bottle/detail.do")
	@ResponseBody
	public BottleVO getBottleDetail(@RequestParam(value = "bottleId", required = false) String bottleBarCd, Model model)	{
		
		BottleVO result = bottleService.getBottleDetail(bottleBarCd);		
		
		if(result != null) logger.debug("******result *****===*"+result.getGasId());
		else logger.debug("******result is null  *****===*"); 
		
		return result;
	}
	
	
	@RequestMapping(value = "/gms/bottle/changeWorkCd.do", method = RequestMethod.POST)
	public ModelAndView modifyBottleWorkCd(HttpServletRequest request
			, HttpServletResponse response
			, BottleVO params) {
//		logger.debug(" modifyBottleWorkCd");
		
		ModelAndView mav = new ModelAndView();
		RequestUtils.initUserPrgmInfo(request, params);
		
		//검색조건 셋팅
		if(params.getCarCustomerId() != null && params.getCarCustomerId().length()>0)
			params.setCustomerId(Integer.parseInt(request.getParameter("carCustomerId"))) ; 
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
		String searchProductId = "";
		
		int  result = 0;
		
		try {		
			//logger.debug("******params.getBottleId()()) *****===*"+params.getChBottleId());
			
			String searchChargetDt = params.getSearchChargeDt();	
					
			if(searchChargetDt != null && searchChargetDt.length() > 20) {				
				//logger.debug("BottleContoller searchChargeDt "+ searchChargetDt.length());
				searchChargeDtFrom = searchChargetDt.substring(0, 10) ;				
				searchChargeDtEnd = searchChargetDt.substring(13, searchChargetDt.length()) ;
				
				params.setSearchChargeDtFrom(searchChargeDtFrom);
				params.setSearchChargeDtEnd(searchChargeDtEnd);				
			}			
			
			String searchDt = params.getSearchDt();	
			
			String searchDtFrom = null;
			String searchDtEnd = null;
					
			if(searchDt != null && searchDt.length() > 20) {			
				//logger.debug("BottleContoller searchChargeDt "+ searchChargetDt.length());
				searchDtFrom = searchDt.substring(0, 10) ;			
				searchDtEnd = searchDt.substring(13, searchDt.length()) ;
				
				params.setSearchDtFrom(searchDtFrom);
				params.setSearchDtEnd(searchDtEnd);			
			}else {
				params.setSearchDt("");
			}
			
			if(params.getSearchProductId() != null && params.getSearchProductId().length() > 0 ) {
				searchProductId = params.getSearchProductId();
				//model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
				mav.addObject("searchProductId", searchProductId);	
			}
			
			params.setBottleWorkId(params.getUpdateId());
			
			result = bottleService.changeBottleWorkCd(params); 
			
			mav.addObject("searchBottleId", params.getSearchBottleId());	
			mav.addObject("searchChargeDt", params.getSearchChargeDt());	
			mav.addObject("currentPage", params.getCurrentPage());	
			
			if (result < 0) {
				
				logger.debug(" modifyBottleWorkCd error");
				
				String alertMessage = "처리중 에러가 발생하였습니다.";
				RequestUtils.responseWriteException(response, alertMessage, "/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchDt="+params.getSearchDt()+"&searchProductId="+params.getSearchProductId());
			}
		} catch (DataAccessException e) {
			logger.error(" modifyBottleWorkCd Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" modifyBottleWorkCd Exception==="+e.toString());
			e.printStackTrace();
		}
		
		if(result > 0){
			String alertMessage = "용기를 작업하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchDt="+params.getSearchDt()+"&searchProductId="+params.getSearchProductId());
		}
		return null;
		
		//return "redirect:/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchChargeDt="+params.getSearchChargeDt()+"&searchGasId="+params.getSearchGasId();
	}
		
	@RequestMapping(value = "/gms/bottle/changeBottlesWorkCd.do", method = RequestMethod.POST)
	public ModelAndView modifyBottlesWorkCd(HttpServletRequest request
			, HttpServletResponse response
			, BottleVO params) {
//		logger.info(" modifyBottleWorkCd");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		//검색조건 셋팅
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
		String searchProductId = "";
		
		int  result = 0;
		try {		
			//logger.debug("******params.getBottleId()()) *****===*"+params.getChBottleId());
			
			String searchChargetDt = params.getSearchChargeDt();	
					
			if(searchChargetDt != null && searchChargetDt.length() > 20) {				
				//logger.debug("BottleContoller searchChargeDt "+ searchChargetDt.length());
				searchChargeDtFrom = searchChargetDt.substring(0, 10) ;				
				searchChargeDtEnd = searchChargetDt.substring(13, searchChargetDt.length()) ;
				
				params.setSearchChargeDtFrom(searchChargeDtFrom);
				params.setSearchChargeDtEnd(searchChargeDtEnd);				
			}			
			
			String searchDt = params.getSearchDt();	
			
			String searchDtFrom = null;
			String searchDtEnd = null;
					
			if(searchDt != null && searchDt.length() > 20) {			
				//logger.debug("BottleContoller searchChargeDt "+ searchChargetDt.length());
				searchDtFrom = searchDt.substring(0, 10) ;			
				searchDtEnd = searchDt.substring(13, searchDt.length()) ;
				
				params.setSearchDtFrom(searchDtFrom);
				params.setSearchDtEnd(searchDtEnd);			
			}else {
				params.setSearchDt("");
			}
			
			if(params.getSearchProductId() != null && params.getSearchProductId().length() > 0 ) {
				searchProductId = params.getSearchProductId();
				//model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
			}
			
			params.setBottleWorkId(params.getUpdateId());
			
			result = bottleService.changeBottlesWorkCd(params);
			/*
			model.addAttribute("searchBottleId", params.getSearchBottleId());
			//model.addAttribute("searchGasId", Integer.parseInt(searchGasId));
			model.addAttribute("searchChargeDt", params.getSearchChargeDt());			
			model.addAttribute("currentPage", params.getCurrentPage());
			*/
			if (result < 0) {				
				logger.error(" modifyBottlesWorkCd error");
			}
		} catch (DataAccessException e) {
			logger.error(" modifyBottlesWorkCd Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" modifyBottlesWorkCd Exception==="+e.toString());
			e.printStackTrace();
		}
		if(result > 0){
			String alertMessage = "용기를 작업하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchDt="+params.getSearchDt()+"&searchPrdouctId="+params.getSearchProductId());
		}
		return null;
		//return "redirect:/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchChargeDt="+params.getSearchChargeDt()+"&searchGasId="+params.getSearchGasId();
	}
	
	@RequestMapping(value = "/gms/bottle/delete.do", method = RequestMethod.POST)
	public ModelAndView deleteBottle(HttpServletRequest request
			, HttpServletResponse response
			, BottleVO params) {
//		logger.info(" deleteBottle");		
		
		ModelAndView mav = new ModelAndView();
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		//검색조건 셋팅
		String searchChargetDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;			
		String searchProductId = "";	
		int  result = 0;
		try {
			
			if(searchChargetDt != null && searchChargetDt.length() > 20) {				
				//logger.debug("BottleContoller searchChargeDt "+ searchChargetDt.length());
				searchChargeDtFrom = searchChargetDt.substring(0, 10) ;				
				searchChargeDtEnd = searchChargetDt.substring(13, searchChargetDt.length()) ;
				
				params.setSearchChargeDtFrom(searchChargeDtFrom);
				params.setSearchChargeDtEnd(searchChargeDtEnd);				
			}			
			
			if(params.getSearchProductId() != null && params.getSearchProductId().length() > 0 ) {
				searchProductId = params.getSearchProductId();
				//model.addAttribute("searchProductId", Integer.parseInt(searchProductId));				
				mav.addObject("searchProductId", searchProductId);
			}									
			//logger.debug("******params.getBottleId()()) *****===*"+params.getBottleId());
			
			result = bottleService.deleteBottle(params);
			
			mav.addObject("searchBottleId", params.getSearchBottleId());
			mav.addObject("searchChargeDt", params.getSearchChargeDt());
			mav.addObject("currentPage", params.getCurrentPage());
			
			if (result < 0) {
				logger.debug(" deleteBottle error");
				String alertMessage = "용기중 오류가 발생하였습니다.";
				RequestUtils.responseWriteException(response, alertMessage, "/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchChargeDt="+params.getSearchChargeDt()+"&searchProductId="+params.getSearchProductId());
			}
		} catch (DataAccessException e) {
			logger.error(" deleteBottle Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" deleteBottle Exception==="+e.toString());
			e.printStackTrace();
		}
	
		if(result > 0){
			String alertMessage = "용기가 삭제 되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchChargeDt="+params.getSearchChargeDt()+"&searchProductId="+params.getSearchProductId());
		}
		return null;
		//return "redirect:/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchChargeDt="+params.getSearchChargeDt()+"&searchProductId="+params.getSearchProductId();
	}
	
	
	@RequestMapping(value = "/gms/bottle/deleteChecked.do", method = RequestMethod.POST)
	public ModelAndView deleteBottles(HttpServletRequest request
			, HttpServletResponse response
			, BottleVO params) {
		
//		logger.info(" deleteBottles");
		
		RequestUtils.initUserPrgmInfo(request, params);
		ModelAndView mav = new ModelAndView();
		
		//검색조건 셋팅
		String bottleIds = null;	
		
		String searchChargetDt = params.getSearchChargeDt();	
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;			
		String searchProductId = "";	
		int  result = 0;
		try {
			
			if(searchChargetDt != null && searchChargetDt.length() > 20) {
				searchChargeDtFrom = searchChargetDt.substring(0, 10) ;				
				searchChargeDtEnd = searchChargetDt.substring(13, searchChargetDt.length()) ;
				
				params.setSearchChargeDtFrom(searchChargeDtFrom);
				params.setSearchChargeDtEnd(searchChargeDtEnd);				
			}			
			
			if(params.getSearchProductId() != null && params.getSearchProductId().length() > 0 ) {
				searchProductId = params.getSearchGasId();
				//model.addAttribute("searchProductId", Integer.parseInt(searchProductId));
				mav.addObject("searchProductId", searchProductId);
			}			
						
			//logger.debug("******params.getBottleId()()) *****===*"+params.getBottleId());			
			if(request.getParameter("bottleIds")!=null && request.getParameter("bottleIds").length() > 0) {
				bottleIds= request.getParameter("bottleIds");
				List<String> list = StringUtils.makeForeach(bottleIds, ","); 		
				params.setBottList(list);
			}					
			
			result = bottleService.deleteBottles(params);
			
			mav.addObject("searchBottleId", params.getSearchBottleId());
			mav.addObject("searchChargeDt", params.getSearchChargeDt());
			mav.addObject("currentPage", params.getCurrentPage());
			
			if (result < 0) {
				logger.debug(" deleteBottles error");
			}
		} catch (DataAccessException e) {
			logger.error(" deleteBottles Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" deleteBottles Exception==="+e.toString());
			e.printStackTrace();
		}
		if(result > 0){
			String alertMessage = "용기가 삭제 되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchChargeDt="+params.getSearchChargeDt()+"&searchProductId="+params.getSearchProductId());
		}
		return null;
		//return "redirect:/gms/bottle/list.do?currentPage="+params.getCurrentPage()+"&searchBottleId="+params.getSearchBottleId()+"&searchChargeDt="+params.getSearchChargeDt()+"&searchProductId="+params.getSearchProductId();
	}
	
	
	@RequestMapping(value = "/gms/bottle/customerBottles.do")
	@ResponseBody
	public List<BottleVO> getCustomerBottleList(@RequestParam(value = "customerId", required = false) Integer customerId, Model model)	{
				
		List<BottleVO> bottleList =  bottleService.getCustomerBottleList(customerId);
		
		model.addAttribute("bottleList", bottleList);		
		return bottleList;
		//return null;
	}
	
	@RequestMapping(value = "/gms/bottle/swrite.do")
	public String openBottleSimpleWrite(Model model) {
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.bottle"));
	
		// 상품 정보 불러오기
		List<ProductVO> productList = productService.getGasProductList();
		model.addAttribute("productList", productList);		
		

		return "gms/bottle/swrite";
	}
	
	@RequestMapping(value = "/gms/bottle/sregister.do", method = RequestMethod.POST)
	public String registerSimpleBottle(HttpServletRequest req) {		
		
		try {
	
			int bottleCount  = Integer.parseInt(req.getParameter("bottleCount"));
			List<ProductNewVO> productList = productService.getGasProductListNew();
			
			int result = 0;
			
			List<BottleVO> list = new ArrayList<BottleVO>();

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			
			int maxBarcode = bottleService.getMaxBarcode();
//		    logger.debug("maxBarcode -="+maxBarcode);
		    
			for(int i =0 ; i < bottleCount ; i++ ) {
				BottleVO bottle = new BottleVO();			
				
				RequestUtils.initUserPrgmInfo(req, bottle);
				
				bottle.setMemberCompSeq(Integer.parseInt(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
				bottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.new"));
				bottle.setBottleOwnYn("Y");
				bottle.setBottleWorkId(bottle.getCreateId());
				
				bottle.setBottleBarCd("DH"+maxBarcode++);
				
				if(req.getParameter("bottleId_"+i)!=null && req.getParameter("bottleId_"+i).length() > 0)
					bottle.setBottleId(req.getParameter("bottleId_"+i));	

				bottle.setProductId(Integer.parseInt(req.getParameter("productId_"+i)));
				bottle.setProductPriceSeq(Integer.parseInt(req.getParameter("productPriceSeq_"+i)));

				if(req.getParameter("bottleVolumn_"+i)!=null && req.getParameter("bottleVolumn_"+i).length() > 0)
					bottle.setBottleVolumn(req.getParameter("bottleVolumn_"+i));	
				
				if(req.getParameter("chargeCapa_"+i)!=null && req.getParameter("chargeCapa_"+i).length() > 0) {
					bottle.setChargeCapa(req.getParameter("chargeCapa_"+i));
					bottle.setBottleCapa(bottle.getChargeCapa());
				}
				String nowString = req.getParameter("bottleChargeDt_"+i);

			    Date nowDate = format.parse(nowString);
			    bottle.setBottleChargeDt(nowDate);
				
			    for(int j = 0; j < productList.size() ; j++) {
			    	if(bottle.getProductId().equals(productList.get(j).getProductId()) ){
			    		bottle.setGasId(productList.get(j).getGasId());
			    		bottle.setGasCd(productList.get(j).getGasCd());
			    		break;
			    	}
			    }
			    
//			    logger.debug("bottle.getBottleId -="+bottle.getBottleId());
//			    logger.debug("bottle.getBottleChargeDt -="+bottle.getBottleChargeDt());
//			    logger.debug("bottle.getProductId -="+bottle.getProductId());
//			    logger.debug("bottle.getProductPriceSeq -="+bottle.getProductPriceSeq());
//			    logger.debug("bottle.getBottleVolumn -="+bottle.getBottleVolumn());
//			    logger.debug("bottle.getGasCd -="+bottle.getGasCd());
//			    logger.debug("bottle.getGasId -="+bottle.getGasId());
				list.add(bottle);
			}
			
			result = bottleService.registerBottles(list);

			result = bottleService.updateMaxBarcode(maxBarcode-1);
			
		} catch (DataAccessException e) {
			logger.error(" registerSimpleBottle Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" registerSimpleBottle Exception==="+e.toString());
			e.printStackTrace();
		}
			
		return "redirect:/gms/bottle/list.do";
	}
	
}

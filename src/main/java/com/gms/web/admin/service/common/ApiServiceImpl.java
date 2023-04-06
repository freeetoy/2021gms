package com.gms.web.admin.service.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CustomerLn2AlarmVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.SimpleBottleVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CashFlowService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.OrderService;
import com.gms.web.admin.service.manage.ProductService;
import com.gms.web.admin.service.manage.UserService;
import com.gms.web.admin.service.manage.WorkReportService;

@Service
public class ApiServiceImpl implements ApiService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final int CUSOTMER_NOT_EXIST = -3;
	private final int USER_NOT_EXIST = -4;


	@Autowired
	private WorkReportService workService;

	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CashFlowService cashService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private OrderService orderService;
	
	@Override
	public int registerWorkReportForSale(WorkReportVO param) { 
		
		int result = 0;	
		
		UserVO user = userService.getUserDetails(param.getUserId());
		
		if(user != null) {
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
		
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			if(customer!=null) {
				param.setCustomerId(customer.getCustomerId());
				param.setAgencyYn(customer.getAgencyYn());				//20201220		
				param.setWorkCd(param.getBottleWorkCd());
	
				result = workService.registerWorkReportNoOrder(param);			
				
			}else {
				return CUSOTMER_NOT_EXIST;
			}		
		}else {
			return USER_NOT_EXIST;
		}
		return result;
	}
	
	@Override
	public int registerWorkReportForChangeCd(WorkReportVO param) {
		
		int result = 0;
		UserVO user = userService.getUserDetails(param.getUserId());
		
		if(user != null) {
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			List<String> list = null;				
			BottleVO bottle = new BottleVO();
			
			bottle.setBottleWorkCd(param.getBottleWorkCd());
			bottle.setBottleType(param.getBottleType());		
			bottle.setBottleWorkId(param.getCreateId());
			bottle.setCreateId(param.getCreateId());
			bottle.setUpdateId(param.getCreateId());
					
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
				bottle.setBottList(list);
			}			
			
			List<BottleVO> bottleList = bottleService.getBottleDetails(bottle);
			
			CustomerVO customer = getCustomer(param.getCustomerNm());		
			if(customer !=null) {
				bottle.setCustomerId(customer.getCustomerId());
				param.setCustomerId(customer.getCustomerId());
				//20201220
				param.setAgencyYn(customer.getAgencyYn());
			}else {
				return CUSOTMER_NOT_EXIST;
			}
			
			param.setUserId(param.getCreateId());
			param.setWorkCd(param.getBottleWorkCd());
			result = workService.registerWorkReportByBottle(param, bottleList);
			if(result <= 0) return result;
			
			result =  bottleService.changeWorkCdsAndHistory(bottle, bottleList);
		}else {
			return USER_NOT_EXIST;
		}
		return result;
	}

	
	private CustomerVO getCustomer(String customerNm) {				
		return customerService.getCustomerDetailsByNm(customerNm);
	}

	@Override
	public int registerWorkReportNoGas(WorkBottleVO param) {
		int result = 0;	
		
		UserVO user = userService.getUserDetails(param.getCreateId());
		
		if(user != null) {
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			
			if(customer!=null) {
				param.setCustomerId(customer.getCustomerId());
				param.setAgencyYn(customer.getAgencyYn());	
				if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
						&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") )
						&& param.getProductCount() > 1000 ) {
					param.setProductPrice(param.getProductCount());
					param.setProductCount(1);
				}
				if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId")) ){
					ProductPriceVO productPrice = new ProductPriceVO();
					productPrice.setProductId(param.getProductId());
					productPrice.setProductCapa(param.getProductPriceSeq().toString());
					param.setProductCapa(param.getProductPriceSeq().toString());
					//TODO 해당 용량별 거래처별 단가 여부 확인 20210331		*** 확인 필요			
					CustomerPriceVO customerPrice = customerService.getCustomerLn2Capa(param);
					ProductPriceVO productPrice1 = null;
					if(customerPrice !=null)  {
						//productPrice1 = productService.getProductPriceDetailsByCapa(productPrice);
						param.setProductPriceSeq(customerPrice.getProductPriceSeq());
					}
					else {
						productPrice1 = productService.getProductPriceDetailsByCapa(productPrice);
						param.setProductPriceSeq(productPrice1.getProductPriceSeq());
					}
					
//					ProductPriceVO productPrice1 = productService.getProductPriceDetailsByCapa(productPrice);
//					param.setProductPriceSeq(productPrice1.getProductPriceSeq());
				}
//				logger.debug("****** registerWorkReportNoGas *****="+param.getProductCount());
				result = workService.registerWorkNoBottle(param);		
			}else {
				return CUSOTMER_NOT_EXIST;
			}
		}else {
			return USER_NOT_EXIST;
		}			
		return result;
	}

	@Override
	public int registerCashFlow(CashFlowVO param) {
		int result = 0;	
		UserVO user = userService.getUserDetails(param.getCreateId());
		
		if(user != null) {
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			
			if(customer!=null) {
				
				param.setCustomerId(customer.getCustomerId());
				// 수금액 정보 업데이트
				
				WorkReportVO workReport = new WorkReportVO();
				workReport.setCustomerId(customer.getCustomerId());
				workReport.setUserId(param.getCreateId());
				workReport.setCreateId(param.getCreateId());
				int workReportSeq = workService.getWorkReportSeqForCustomerToday(workReport);
				
				if(workReportSeq <= 0) {
					workReportSeq = workService.getWorkReportSeq();		
					workReport.setWorkReportSeq(workReportSeq);					
					workReport.setUserId(param.getCreateId());
					workReport.setReceivedAmount(param.getIncomeAmount());
					workReport.setIncomeWay(param.getIncomeWay());
					workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.0312"));
					
					workService.registerWorkReportOnly(workReport);
					
				}else {
					workReport.setUpdateId(user.getUserId());					
					workReport.setReceivedAmount(param.getIncomeAmount());
					workReport.setIncomeWay(param.getIncomeWay());
					workReport.setWorkReportSeq(workReportSeq);
			
					result = workService.modifyWorkReportReceivedAmount(workReport);
				}			
				
				result = cashService.registerCashFlow(param);		
			}else {
				return CUSOTMER_NOT_EXIST;
			}
		}else {
			return USER_NOT_EXIST;
		}	
		return result;
	}

	@Override
	public List<SimpleBottleVO> getCustomerSimpleBottleList(String customerNm) {
		//Customer 정보가져
		CustomerVO customer = getCustomer(customerNm);
		
		if(customer!=null) {			
			return bottleService.getCustomerSimpleBottleList(customer.getCustomerId());
		}else {
			return null;
		}
	}

	@Override
	public int registerWorkReportGasAndBottle(WorkReportVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<WorkBottleVO> getWorkReportList(WorkReportVO param) {
		
		return workService.getWorkBottleListOfUser(param);
	}

	@Override
	public int registerWorkReportMassForSale(WorkReportVO param) {
		int result = 0;	
		
		UserVO user = userService.getUserDetails(param.getUserId());
		
		if(user != null) {	
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			if(customer!=null) {
				param.setCustomerId(customer.getCustomerId());
				param.setAgencyYn(customer.getAgencyYn());				//20201220	
				result = workService.registerWorkReportMassNoOrder(param);
				
			}else {
				return CUSOTMER_NOT_EXIST;
			}		
		}else {
			return USER_NOT_EXIST;
		}
		return result;
				
	}

	@Override
	public int registerWorkReportMassForChangeCd(WorkReportVO param) {
		int result = 0;	
		
		UserVO user = userService.getUserDetails(param.getUserId());
		
		if(user != null) {		
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			if(customer!=null) {
				param.setCustomerId(customer.getCustomerId());
				param.setAgencyYn(customer.getAgencyYn());				//20201220	
				param.setWorkCd(param.getBottleWorkCd());
				result = workService.registerWorkReportMassByBottle(param);		
				
			}else {
				return CUSOTMER_NOT_EXIST;
			}		
		}else {
			return USER_NOT_EXIST;
		}
		return result;						   
	}

	@Override
	public List<BottleVO> getDummyBottleList() {
		
		return bottleService.getSimpleDummyBottleList();
	}

	@Override
	public String getAppVersion() {
		
		return codeService.getAppVersion().getAppVer();
	}

	@Override
	public List<ProductPriceSimpleVO> getCustomerLn2List() {
		//CustomerVO customer = getCustomer(customerNm);
		List<ProductPriceSimpleVO> productList= null;
		productList = productService.getCustomerLn2List();				
		
		return productList;
	}

	@Override
	public List<CustomerProductVO> getCustomerBottleList(String customerNm) {
		
		List<CustomerProductVO> bottleList= null;
		CustomerVO customer = getCustomer(customerNm);
		
		if(customer!=null)
			bottleList = customerService.getCustomerProductList(customer.getCustomerId());		
		
		return bottleList;
	}

	@Override
	public int deleteWorkBottle(WorkBottleVO param) {
		// TODO Auto-generated method stub
		WorkReportVO report = workService.getWorkReport(param.getWorkReportSeq());
		int result = 0;
		report.setCreateId(report.getUserId());
		report.setUpdateId(report.getUserId());
		
		param.setCreateId(report.getUserId());
		param.setUpdateId(report.getUserId());
		result = workService.deleteWorkReportAndBottle(report, param);
		
		return result;
	}

	@Override
	public int registerWorkReportTank(WorkBottleVO param) {
		int result = 0;	
		
		UserVO user = userService.getUserDetails(param.getCreateId());
		
		if(user != null) {
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			
			if(customer!=null) {
				param.setCustomerId(customer.getCustomerId());
				
//				logger.debug("****** registerWorkReportNoGas *****="+param.getProductPriceSeq());
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.tcharge"))) {
					result = workService.registerWorkBottleChargeTank(param);	
				}else
					result = workService.registerWorkNoBottle(param);		
			}else {
				return CUSOTMER_NOT_EXIST;
			}
		}else {
			return USER_NOT_EXIST;
		}			
		return result;
	}

	@Override
	public int registerOrder(OrderVO param) {
		int result = 0;	
		
		UserVO user = userService.getUserDetails(param.getCreateId());
		
		if(user != null) {
			//사용자 최종접속일 정보 업데이트
			LoginUserVO loginUser = new LoginUserVO();
			loginUser.setUserId(user.getUserId());
			
			if(!DateUtils.convertDateFormat(user.getLastConnectDt(),"yyyy-MM-dd").equals(DateUtils.getDate("yyyy-MM-dd")) )
				result = loginService.modifyLastConnect(loginUser);
			
			//Customer 정보가져
			CustomerVO customer = getCustomer(param.getCustomerNm());
			
			if(customer!=null) {
				param.setCustomerId(customer.getCustomerId());
				
				List<String> list = null;
				if(param.getOrderIds()!=null && param.getOrderIds().length() > 0) {
					//bottleIds= request.getParameter("bottleIds");
					list = StringUtils.makeForeach(param.getOrderIds(), ";"); 			
				}		
				List<OrderProductVO> orderProductList = new ArrayList<OrderProductVO>();
				for(int i =0 ; i < list.size() ; i++) {

					List<String> detail = StringUtils.makeForeach(list.get(i), "#");
					
					OrderProductVO productVo = new OrderProductVO();
					productVo.setOrderProductSeq(i+1);
					productVo.setProductId(Integer.parseInt(detail.get(0)));
					productVo.setProductPriceSeq(Integer.parseInt(detail.get(1)));
					productVo.setOrderProductEtc(detail.get(2));
					productVo.setOrderCount(Integer.parseInt(detail.get(3)) );
					productVo.setBottleChangeYn(detail.get(4));	
					productVo.setBottleSaleYn(detail.get(5));
					productVo.setRetrievedYn(detail.get(6));
					productVo.setAsYn(detail.get(7));
					
					productVo.setIncomeYn(detail.get(8));
					productVo.setOutYn(detail.get(9));
					
					orderProductList.add(productVo);
				}
				
				result = orderService.registerOrderFromApi(param, orderProductList, customer);
			}else {
				return CUSOTMER_NOT_EXIST;
			}
		}else {
			return USER_NOT_EXIST;
		}			
		return result;
	}

	@Override
	public List<OrderVO> getOrderList(OrderVO param) {
		param.setSearchOrderDtFrom(param.getSearchOrderDt());
		param.setSearchOrderDtEnd(param.getSearchOrderDt());
		
		Map<String, Object> map = orderService.getOrderList(param);
		List<OrderVO> orderList = (List<OrderVO>) map.get("list");
		
		return orderList;
	}

	@Override
	public int deleteOrder(OrderVO param) {

		return orderService.deleteOrder(param);
	}

	@Override
	public List<OrderProductVO> getOrderProductList(OrderVO param) {
		
		return orderService.getOrderProductList(param.getOrderId());
	}

	@Override
	public List<WorkBottleVO> deliveredLn2CustomerList(String createId) {
		WorkReportVO report = new WorkReportVO();
		report.setCreateId(createId);
		
		return workService.deliveredLn2CustomerList(report);
	}

	@Override
	public List<CustomerLn2AlarmVO> deliveredLn2CustomerListNew(String salesId) {
			
		return customerService.getLn2CustomerList(salesId);
	}


}

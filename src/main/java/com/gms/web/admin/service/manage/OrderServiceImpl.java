package com.gms.web.admin.service.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderBottleVO;
import com.gms.web.admin.domain.manage.OrderExtCustomerVO;
import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.mapper.manage.OrderMapper;

@Service
public class OrderServiceImpl implements OrderService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private OrderMapper orderMapper;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private WorkReportService workService;
	

	@Override
	public Map<String, Object> getOrderList(OrderVO param) {
//		logger.debug("****** getOrderList *****start===*");		
		
		int currentPage = param.getCurrentPage();
		int ROW_PER_PAGE = param.getRowPerPage();
		
		int startPageNum =1;
		
		int lastPageNum = ROW_PER_PAGE;
		
		if(currentPage > (ROW_PER_PAGE/2)) {
			lastPageNum += (startPageNum-1);
		}
		
		int startRow = (currentPage-1) * ROW_PER_PAGE;
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		map.put("startRow", startRow);
		map.put("rowPerPage", ROW_PER_PAGE);	
		map.put("searchCustomerNm", param.getSearchCustomerNm());			
		
		if(param.getSearchOrderDt() != null) {
			map.put("searchOrderDt", param.getSearchOrderDt());
		}		
		
		if(param.getSearchOrderDtFrom() != null) {
			map.put("searchOrderDtFrom", param.getSearchOrderDtFrom());
		}
		
		if(param.getSearchOrderDtEnd() != null) {
			map.put("searchOrderDtEnd", param.getSearchOrderDtEnd());
		}	
		if(param.getSearchOrderProcessCd() != null) {
			map.put("searchOrderProcessCd", param.getSearchOrderProcessCd());
		}	
		
		int orderCount = orderMapper.selectOrderCount(map);		
		
		//int lastPage = (int)(Math.ceil(orderCount/ROW_PER_PAGE));
		int lastPage = (int)((double)orderCount/ROW_PER_PAGE+0.95);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		//수정 Start
		int pages = (orderCount == 0) ? 1 : (int) ((orderCount - 1) / ROW_PER_PAGE) + 1; // * 정수형이기때문에 소숫점은 표시안됨		
       
        int block;       
        block = (int) Math.ceil(1.0 * currentPage / ROW_PER_PAGE); // *소숫점 반올림
        startPageNum = (block - 1) * ROW_PER_PAGE + 1;
        lastPageNum = block * ROW_PER_PAGE;        
        
        if (lastPageNum > pages){
        	lastPageNum = pages;
        }
		//수정 end
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<OrderVO> orderList = orderMapper.selectOrderList(map);
		
		resutlMap.put("list",  orderList);
		
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", startPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", orderCount);
		
		resutlMap.put("rowPerPage", ROW_PER_PAGE);
		
		return resutlMap;
	}

	@Override
	public List<OrderVO> getOrderListToExcel(OrderVO param) {
					
		Map<String, Object> map = new HashMap<String, Object>();		
		
		map.put("searchCustomerNm", param.getSearchCustomerNm());	
		if(param.getSearchOrderDt() != null) {
			map.put("searchOrderDt", param.getSearchOrderDt());
		}		
		
		if(param.getSearchOrderDtFrom() != null) {
			map.put("searchOrderDtFrom", param.getSearchOrderDtFrom());			
		}
		
		if(param.getSearchOrderDtEnd() != null) {
			map.put("searchOrderDtEnd", param.getSearchOrderDtEnd());			
		}	
				
		map.put("searchCustomerNm", param.getSearchCustomerNm());	
		map.put("searchOrderProcessCd", param.getSearchOrderProcessCd());	
		
		List<OrderVO> orderList = orderMapper.selectOrderListToExcel(map);		
			
		return orderList;
	}


	
	@Override
	public List<OrderVO> getCustomerOrderList(Integer customerId) {
		return orderMapper.selectCustomerOrderList(customerId);
	}
	
	@Override
	public List<OrderVO> getSalseOrderList(String salesId) {
		return orderMapper.selectSalesOrderList(salesId);
	}


	@Override
	public OrderVO getOrderDetail(Integer orderId) {
		return orderMapper.selectOrderDetail(orderId);	
	}


	@Override
	public OrderVO getLastOrderForCustomer(Integer customerId) {
		OrderVO orderInfo = orderMapper.selectLastOrderForCustomer(customerId);	
		//return null;
		String today = DateUtils.getDate("YYYY/MM/dd");
			
		if( orderInfo != null) {
			if(orderInfo.getOrderProcessCd().equals(PropertyFactory.getProperty("common.code.order.process.delivery"))) {
				if(DateUtils.convertDateFormat(orderInfo.getUpdateDt(),"YYYY/MM/dd").equals(today) ) return orderInfo;
				else orderInfo = null;
			}
		}				
		else orderInfo = null;
		
		return orderInfo;
		//return orderMapper.selectLastOrderForCustomer(customerId);	
	}
	
	
	@Override
	public List<OrderProductVO> getOrderProductList(Integer orderId) {
		return orderMapper.selectOrderProductList(orderId);	
	}

	
	@Override
	public int getOrderCount(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional
	public int registerOrder(HttpServletRequest request,OrderVO params) {
		// 정보 등록
		
		RequestUtils.initUserPrgmInfo(request, params);		
		int result = 0;
		int result1 =0;
		//mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		try {
			//임시
			params.setMemberCompSeq(1);
			
			int productCount  = params.getProductCount();						
			
			List<OrderProductVO> orderProduct = new ArrayList<OrderProductVO>();
			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();
					
			// Sales ID 설정
			CustomerVO customer = customerService.getCustomerDetails(params.getCustomerId());
			params.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.receive"));
			params.setSalesId(customer.getSalesId());
			result =  orderMapper.insertOrder(params);	
						
			int orderId = getNewOrderId(params);
			params.setOrderId(Integer.valueOf(orderId));
			
			String orderTypeCd = params.getOrderTypeCd();
						
			double orderAmount = 0;
			//int deleteAmount = 0;
			double orderTotalAmount = 0;
			
			Integer productId =0;
			Integer productPriceSeq = 0;
			Integer orderCount = 0;
			
			String orderProductNm = "";
			String orderProductCapa = "";
			
			String bottleChangeYn = null;
			String bottleSaleYn = "N";
			String retrievedYn = "N";
			String asYn = "N";
			
			ProductTotalVO tempProduct = null;			
			
			if(orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.order")) ||  orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.cancel"))
					|| orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.auto"))) {
			
				// 거래처 상품별 단가 정보 가져오기
				List<CustomerPriceExtVO> customerPriceList = customerService.getCustomerPriceList(params.getCustomerId());			
				
				// 상품별 단가 정보 가져오기
				List<ProductTotalVO> productPriceList = productService.getProductTotalList();
				//List<ProductTotalVO> productPriceList = productService.getCustomerProductTotalList(params.getCustomerId());						
				
				for(int i =0 ; i < productCount ; i++ ) {
					
					OrderProductVO productVo = new OrderProductVO();
					
					RequestUtils.initUserPrgmInfo(request, productVo);
					
					productId =0;
					productPriceSeq = 0;
					orderCount = 0;
					bottleChangeYn = "N";
					bottleSaleYn = "N";
					retrievedYn = "N";
					asYn = "N";
					boolean bottleFlag = false;
					
					productId = Integer.parseInt(request.getParameter("productId_"+i));
					productPriceSeq = Integer.parseInt(request.getParameter("productPriceSeq_"+i));
					if(request.getParameter("orderCount_"+i) != null)
						orderCount = Integer.parseInt(request.getParameter("orderCount_"+i));
					else 
						orderCount = 1;
					
					if(request.getParameter("bottleChangeYn_"+i) !=null)  bottleChangeYn = "Y";
					if(request.getParameter("bottleSaleYn_"+i) !=null )  bottleSaleYn = "Y";									
					
					if(request.getParameter("retrievedYn_"+i) !=null )  retrievedYn = "Y";	
					if(request.getParameter("asYn_"+i) !=null )  asYn = "Y";	
					
					for(int k=0;k<productPriceList.size();k++) {
						orderAmount = 0;
						
						tempProduct = productPriceList.get(k);
						
						if(productId == tempProduct.getProductId() && productPriceSeq == tempProduct.getProductPriceSeq()) {
							
							if(tempProduct.getGasId()!=null && tempProduct.getGasId() > 0) bottleFlag = true;
							
							if(i==0) {
								orderProductNm = tempProduct.getProductNm();
								orderProductCapa = tempProduct.getProductCapa();
							}
							if(bottleFlag) {
								if(bottleSaleYn.equals("Y")) {								
									if(bottleFlag && customer.getAgencyYn().equals("N")){	// 가스 및 용기 판매
										orderAmount = tempProduct.getProductBottlePrice() + tempProduct.getProductPrice()* orderCount;
									}else 
										orderAmount = tempProduct.getProductBottlePrice() * orderCount;		
								}else
									orderAmount = tempProduct.getProductPrice() *orderCount;
							}else {
								orderAmount = tempProduct.getProductBottlePrice() * orderCount;		
							}
							
							
							productVo.setOrderAmount(orderAmount);												
						}
					}			

					for(int k=0; k<customerPriceList.size(); k++) {
						orderAmount = 0;
						CustomerPriceExtVO customerPrice = customerPriceList.get(k);
						
//						if(customerPrice.getGasId()!=null && customerPrice.getGasId() > 0) bottleFlag = true;
						if(productId.equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId")) ) 
								&& productPriceSeq.equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.no.producSeq")) )){
							
							if(productId.equals(customerPrice.getProductId() ) ){
								int units = Integer.parseInt(customerPrice.getProductCapa().replace("병_",""));
								double salePrice = customerPrice.getProductBottlePrice()/units;
								
								orderAmount = salePrice *orderCount;
								
								productVo.setOrderAmount(orderAmount);
								break;		// 2021-07-19 첫번째 것으로 확인
							}
						}else {
							if(productId.equals(customerPrice.getProductId() ) && productPriceSeq.equals(customerPrice.getProductPriceSeq() ) ) {
								if(customerPrice.getGasId()!=null && customerPrice.getGasId() > 0) bottleFlag = true;
								if(bottleSaleYn.equals("Y"))  {								
									if(bottleFlag && customer.getAgencyYn().equals("N")){	// 가스 및 용기 판매
										orderAmount = customerPrice.getProductBottlePrice() * orderCount + customerPrice.getProductPrice()* orderCount;
									}else
										orderAmount = customerPrice.getProductBottlePrice() *orderCount;								
								}else
									orderAmount = customerPrice.getProductPrice() *orderCount;		
								
								productVo.setOrderAmount(orderAmount);
							}
						}						
					}
					
					productVo.setOrderId(params.getOrderId());
					productVo.setOrderProductSeq(i+1);
					productVo.setProductId(productId);
					productVo.setProductPriceSeq(productPriceSeq);				
					productVo.setOrderCount(orderCount);
					productVo.setOrderProductEtc(request.getParameter("orderProductEtc_"+i));
					productVo.setBottleChangeYn(bottleChangeYn);	
					productVo.setBottleSaleYn(bottleSaleYn);
					productVo.setRetrievedYn(retrievedYn);
					productVo.setAsYn(asYn);
					//productVo.setProductDeliveryDt(null);
					
					orderProduct.add(productVo);
					
					if(bottleFlag) {
					
						for(int k=0; k< orderCount ; k++) {
						
							OrderBottleVO orderBottle = new OrderBottleVO();
							
							orderBottle.setOrderId(orderId);
							orderBottle.setOrderProductSeq(i+1);
							orderBottle.setProductId(productId);
							orderBottle.setProductPriceSeq(productPriceSeq);
							orderBottle.setCreateId(params.getCreateId());
							orderBottle.setUpdateId(params.getCreateId());
							
							orderBottleList.add(orderBottle);			
						}
					}
				}
				
				if(productCount > 1) {
					orderProductNm = orderProductNm +"외 "+ (productCount-1);
					orderProductCapa = orderProductCapa +"외 "+ (productCount-1);
				}
							
				result1 = orderMapper.insertOrderProducts(orderProduct);				
				
				if(orderBottleList.size() > 0)
					result1 = orderMapper.insertOrderBottles(orderBottleList);
				
				params.setOrderProductNm(orderProductNm);
				params.setOrderProductCapa(orderProductCapa);
				orderTotalAmount = 0;
				for(int j=0;j<orderProduct.size();j++) {
					orderTotalAmount  += orderProduct.get(j).getOrderAmount();		
				}
				params.setOrderTotalAmount(orderTotalAmount);
			}
			result =  orderMapper.updateOrder(params);	
		
		} catch (DataAccessException de) {
			logger.error("registerOrder DataAccessException==="+de.toString());
			de.printStackTrace();
		} catch (Exception e) {
			logger.error("registerOrder Exception==="+e.toString());
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public int registerOrder(OrderVO param) {
		return orderMapper.insertOrder(param);	
	}
	
	@Override
	@Transactional
	public int registerOrderProduct(OrderProductVO param) {
		// 정보 등록
		int result = 0;
		result =  orderMapper.insertOrderProduct(param);		
		//if(result > 0 ) result = bottleMapper.insertBottleHistory(param.getBottleId());
		
		return result;

	}

	@Override
	@Transactional
	public int registerOrderAndProduct(OrderVO order, List<OrderProductVO> orderProduct) {
		int result = 0;
		try {
			result =  orderMapper.insertOrder(order);	
			
			result = orderMapper.insertOrderProducts(orderProduct);
			
		} catch (DataAccessException e) {
			logger.error("registerOrderAndProduct DataAccessException==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("registerOrderAndProduct Exception==="+e.toString());
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	@Transactional
	public int modifyOrderRegiProduct(OrderVO order, List<OrderProductVO> orderProduct) {
		int result = 0;
		try {			
			result = orderMapper.insertOrderProducts(orderProduct);
			
			result =  orderMapper.updateOrder(order);	
			
		} catch (DataAccessException e) {
			logger.error("modifyOrderRegiProduct DataAccessException==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("modifyOrderRegiProduct Exception==="+e.toString());
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@Override
	@Transactional
	public int modifyOrder(HttpServletRequest request, OrderVO params) {
		// 정보 등록
		
		RequestUtils.initUserPrgmInfo(request, params);		
		int result = 0;
		int result1 =0;
		//mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		try {
			//임시
			params.setMemberCompSeq(1);
			
			int productCount  = params.getProductCount();
						
			List<OrderProductVO> orderProduct = new ArrayList<OrderProductVO>();
			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();
			
			Integer orderId = params.getOrderId();
			//params.setOrderId(Integer.valueOf(orderId));					
			List<OrderProductVO> oldProductList = getOrderProductList(orderId);
			List<OrderBottleVO> oldOrderBottleList = getOrderBottleList(orderId);
			
			String orderTypeCd = params.getOrderTypeCd();
						
			double orderAmount = 0;
			double orderTotalAmount = 0;
			
			Integer productId =0;
			Integer productPriceSeq = 0;
			Integer orderCount = 0;
			
			String orderProductNm = "";
			String orderProductCapa = "";			
			String bottleChangeYn = "N";
			String bottleSaleYn = "N";
			String retrievedYn = "N";
			String asYn = "N";
			
			CustomerPriceExtVO tempCustomerPrice = null;
			ProductTotalVO tempProduct = null;		
			
			// Sales ID 설정
			CustomerVO customer = customerService.getCustomerDetails(params.getCustomerId());
			
			params.setSalesId(customer.getSalesId());
			params.setUpdateId(params.getCreateId());
			
			if(orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.order")) ||  orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.cancel"))
					|| orderTypeCd.equals(PropertyFactory.getProperty("common.code.order.type.auto"))) {
				
				// 거래처별 상품 단가 정보 가져오기
				List<CustomerPriceExtVO> customerPriceList = customerService.getCustomerPriceList(params.getCustomerId());			
				
				// 상품별 단가 정보 가져오기
				List<ProductTotalVO> productPriceList = productService.getProductTotalList();
								
				for(int i =0 ; i < productCount ; i++ ) {
					
					OrderProductVO productVo = new OrderProductVO();
					
					RequestUtils.initUserPrgmInfo(request, productVo);
					
					productId =0;
					productPriceSeq = 0;
					orderCount = 0;
					bottleChangeYn = "N";
					bottleSaleYn = "N";
					retrievedYn = "N";
					asYn = "N";
					boolean bottleFlag = false;
					int requestIndex = i;					
					
					if(request.getParameter("productId_"+i) != null) {
						requestIndex = i;
						productId = Integer.parseInt(request.getParameter("productId_"+i));
						productPriceSeq = Integer.parseInt(request.getParameter("productPriceSeq_"+i));
						orderCount = Integer.parseInt(request.getParameter("orderCount_"+i));
					}else {
						for(int j=1;j<11-productCount ; j++) {
							if(request.getParameter("productId_"+(i+j)) != null) {
								requestIndex = i+j;
								productId = Integer.parseInt(request.getParameter("productId_"+(i+j)));
								productPriceSeq = Integer.parseInt(request.getParameter("productPriceSeq_"+(i+j)));
								orderCount = Integer.parseInt(request.getParameter("orderCount_"+(i+j)));
								break;
							}
						}						
					}				
					if(request.getParameter("bottleChangeYn_"+requestIndex) !=null)  {
						bottleChangeYn = "Y";
					}
					if(request.getParameter("bottleSaleYn_"+requestIndex) !=null )  {
						bottleSaleYn = "Y";
					}
					
					if(request.getParameter("retrievedYn_"+i) !=null )  retrievedYn = "Y";	
					if(request.getParameter("asYn_"+i) !=null )  asYn = "Y";	
					
					for(int k=0;k<productPriceList.size();k++) {
						orderAmount = 0;
						tempProduct = productPriceList.get(k);
						
						if(productId == tempProduct.getProductId() && productPriceSeq == tempProduct.getProductPriceSeq()) {
							
							if(tempProduct.getGasId() != null && tempProduct.getGasId() > 0) bottleFlag = true;
							
							if(i==0) {
								orderProductNm = tempProduct.getProductNm();
								orderProductCapa = tempProduct.getProductCapa();
							}
							if(bottleFlag) {
								if(bottleSaleYn.equals("Y")) {								
									if(bottleFlag && customer.getAgencyYn().equals("N")){	// 가스 및 용기 판매
										orderAmount = tempProduct.getProductBottlePrice()* orderCount + tempProduct.getProductPrice()* orderCount;
									}else 
										orderAmount = tempProduct.getProductBottlePrice() * orderCount;		
								}else
									orderAmount = tempProduct.getProductPrice() *orderCount;
							}else {
								orderAmount = tempProduct.getProductBottlePrice() * orderCount;	
							}
							
							productVo.setOrderAmount(orderAmount);
						}
					}			
					
					for(int k=0;k<customerPriceList.size();k++) {
						orderAmount = 0;
						CustomerPriceExtVO customerPrice = customerPriceList.get(k);
						if(productId.equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId")) ) 
								&& productPriceSeq.equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.no.producSeq")) )){
							if(productId.equals(customerPrice.getProductId() ) ){
								int units = Integer.parseInt(customerPrice.getProductCapa().replace("병_",""));
								double salePrice = customerPrice.getProductBottlePrice()/units;
								
								orderAmount = salePrice *orderCount;
								
								productVo.setOrderAmount(orderAmount);
							}
						}else {
							if(productId.equals(customerPrice.getProductId() ) && productPriceSeq.equals(customerPrice.getProductPriceSeq() ) ) {
								if(customerPrice.getGasId()!=null && customerPrice.getGasId() > 0) bottleFlag = true;	
								if(bottleSaleYn.equals("Y"))  {								
									if(bottleFlag && customer.getAgencyYn().equals("N")){	// 가스 및 용기 판매
										orderAmount = customerPrice.getProductBottlePrice()* orderCount + customerPrice.getProductPrice()* orderCount;
									}else
										orderAmount = customerPrice.getProductBottlePrice() *orderCount;								
								}else
									orderAmount = customerPrice.getProductPrice() *orderCount;		
								
								productVo.setOrderAmount(orderAmount);
							}
						}						
					}
					
//					for(int k=0;k<productPriceList.size();k++) {
//						orderAmount = 0;
//						tempProduct = productPriceList.get(k);
//						
//						if(productId.equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId")) ) 
//								&& productPriceSeq.equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.no.producSeq")) )){
//							if(productId.equals(tempProduct.getProductId()) ){
//								int units = Integer.parseInt(tempProduct.getProductCapa().replace("병_",""));
//								double salePrice = tempProduct.getProductBottlePrice()/units;
//								
//								logger.debug("modifyrOrder salePrice="+salePrice);
//								orderAmount = salePrice *orderCount;
//								productVo.setOrderAmount(orderAmount);
//								orderTotalAmount += orderAmount;	
//							}
//						}else {
//							if(productId == tempProduct.getProductId() && productPriceSeq == tempProduct.getProductPriceSeq()) {
//								if(tempProduct.getGasId() !=null && tempProduct.getGasId() > 0 && orderCount > 0) bottleFlag = true;
//								if(i==0) {
//									orderProductNm = tempProduct.getProductNm();
//									orderProductCapa = tempProduct.getProductCapa();
//								}
//								
//								if(bottleSaleYn.equals("Y"))
//									orderAmount = tempProduct.getProductBottlePrice() *orderCount;
//								else
//									orderAmount = tempProduct.getProductPrice() *orderCount;									
//								
//								//orderAmount = tempProduct.getProductPrice() *orderCount;	
//								productVo.setOrderAmount(orderAmount);
//								orderTotalAmount += orderAmount;								
//							}
//						}
//					}	
//					
					//기존 주문상품과 빅교
					for(int j=0; j< oldProductList.size() ; j++) {
						OrderProductVO oldProductVo = oldProductList.get(j);						
					}
					
					productVo.setOrderId(params.getOrderId());
					productVo.setOrderProductSeq(i+1);
					productVo.setProductId(productId);
					productVo.setProductPriceSeq(productPriceSeq);				
					productVo.setOrderCount(orderCount);
					productVo.setOrderProductEtc(request.getParameter("orderProductEtc_"+requestIndex));
					productVo.setBottleChangeYn(bottleChangeYn);								
					productVo.setBottleSaleYn(bottleSaleYn);
					productVo.setRetrievedYn(retrievedYn);
					productVo.setAsYn(asYn);
					//productVo.setProductDeliveryDt(null);
					
					orderProduct.add(productVo);
					
					if(bottleFlag) {
						for(int k=0; k< orderCount ; k++) {
							
							OrderBottleVO orderBottle = new OrderBottleVO();
							String orderBottleBarCd ="";
							
							for(int l=oldOrderBottleList.size()-1 ; l >= 0 ;l--) {
								OrderBottleVO oldOrderBottle = oldOrderBottleList.get(l);
								
								if(oldOrderBottle.getProductId() == productId && oldOrderBottle.getProductPriceSeq()==productPriceSeq) {
									orderBottleBarCd = oldOrderBottle.getBottleBarCd();
									oldOrderBottleList.remove(l);
								}
								
							}
							orderBottle.setOrderId(orderId);
							orderBottle.setOrderProductSeq(i+1);
							orderBottle.setProductId(productId);							
							orderBottle.setProductPriceSeq(productPriceSeq);
							orderBottle.setBottleBarCd(orderBottleBarCd);
							orderBottle.setCreateId(params.getCreateId());
							orderBottle.setUpdateId(params.getCreateId());
							
							orderBottleList.add(orderBottle);						
						}
					}
				}
					
				if(productCount > 1) {
					orderProductNm = orderProductNm +"외 "+ (productCount-1);
					orderProductCapa = orderProductCapa +"외 "+ (productCount-1);
				}
				
				orderTotalAmount = 0;
				for(int j=0;j<orderProduct.size();j++) {
					orderTotalAmount  += orderProduct.get(j).getOrderAmount();		
				}
				/*
				// 기존 상품과 비교
				 * 
				List<OrderProductVO> oldOrderProductList = getOrderProductList(orderId);
				for(int i = 0 ; i<oldOrderProductList.size();i++) {
					
				}
				*/
				if(orderMapper.deleteOrderProducts(orderId) > 0 ) {
					
					result1 = orderMapper.insertOrderProducts(orderProduct);
					
					result1 = orderMapper.deleteOrderBottles(orderId);
					
					result1 = orderMapper.insertOrderBottles(orderBottleList);
				}
				params.setOrderProductNm(orderProductNm);
				params.setOrderProductCapa(orderProductCapa);
				params.setOrderTotalAmount(orderTotalAmount);
			}
			//			
			params.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.receive"));
		
			result =  orderMapper.updateOrder(params);	
		
		} catch (DataAccessException e) {
			logger.error("modifyOrder Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("modifyOrder Exception==="+e.toString());
			e.printStackTrace();
		}
			
		return result;
	}

	@Override
	@Transactional
	public int modifyOrderAmount(OrderVO param) {
		return orderMapper.updateOrderTotalAmount(param);
	}
	
	@Override
	@Transactional
	public int modifyOrderProductCount(OrderProductVO param) {
		
		return orderMapper.updateOrderProductCount(param);
	}
	
	@Override
	@Transactional
	public int modifyOrderBottleId(OrderProductVO param) {		
		
		BottleVO bottle = new BottleVO();
		bottle.setBottleId(param.getBottleId());
		bottle.setOrderId(param.getOrderId());
		bottle.setOrderProductSeq(param.getOrderProductSeq());
		bottle.setUpdateId(param.getUpdateId());
		
		int result = bottleService.modifyBottleOrder(bottle);
		
		if(result >0 )
			return orderMapper.updateOrderBottleId(param);
		else
			return 0;
	}

	@Override
	public int modifyOrderComplete(OrderProductVO param) {
		int result = 0;
		try {			
			BottleVO bottle = new BottleVO();
			
			bottle.setBottleId(param.getBottleId());
			bottle.setOrderId(param.getOrderId());
			bottle.setOrderProductSeq(param.getOrderProductSeq());
			bottle.setBottleWorkCd(param.getBottleWorkCd());
			bottle.setBottleWorkId(param.getUpdateId());		
			bottle.setBottleType(param.getBottleType());
			bottle.setUpdateId(param.getUpdateId());
			
			result = bottleService.modifyBottleOrder(bottle);
			
			if(result >0 ) {
				result =  orderMapper.updateOrderBottleId(param);
				/*
				OrderVOreturn  order = new OrderVO();
				
				order.setOrderId(param.getOrderId());
				order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));
				order.setUpdateId(param.getUpdateId());
				
				return orderMapper.updateOrderProcessCd(order);
				*/
			}else {
				return 0;
			}
		} catch (DataAccessException e) {
			logger.error("modifyOrderComplete Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("modifyOrderComplete Exception==="+e.toString());
			e.printStackTrace();
		}
		return result;
	}

	
	@Override
	public int modifyOrderProductBottle(OrderProductVO param) {
		return orderMapper.updateOrderBottleId(param);
	}

	
	@Override
	@Transactional
	public int changeOrderProcessCd(OrderVO params) {
		
		String searchOrderDtFrom = null;
		String searchOrderDtEnd = null;
		int  result = 0;
		
		try {		
			
			String searchOrderDt = params.getSearchOrderDt();	
					
			if(searchOrderDt != null && searchOrderDt.length() > 20) {

				searchOrderDtFrom = searchOrderDt.substring(0, 10) ;				
				searchOrderDtEnd = searchOrderDt.substring(13, searchOrderDt.length()) ;
				
				params.setSearchOrderDtFrom(searchOrderDtFrom);
				params.setSearchOrderDtEnd(searchOrderDtEnd);				
			}			
			
			result = orderMapper.updateOrderProcessCd(params);			
					
		} catch (DataAccessException e) {
			logger.error("changeOrderProcessCd Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("changeOrderProcessCd Exception==="+e.toString());
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	@Transactional
	public int deleteOrder(OrderVO param) {
		
		List<OrderProductVO> orderProductList = getOrderProductList(param.getOrderId());
		
		boolean alreadySales = false;
		for(int i=0; i<orderProductList.size() ; i++) {
			if(orderProductList.get(i).getSalesCount() <= 0) alreadySales = true;
		}
		if(alreadySales) return -1;
		
		return orderMapper.deleteOrder(param);
	}

	@Override
	@Transactional
	public int deleteOrderProduct(OrderProductVO param) {
		
		return orderMapper.deleteOrderProduct(param);
	}
	
	@Override
	@Transactional
	public int deleteOrderProducts(OrderProductVO param) {
		
		return orderMapper.deleteOrderProducts(param.getOrderId());
	}

	@Override
	@Transactional
	public int registerOrderProducts(List<OrderProductVO> orderProduct) {
		int result = 0;
		result =  orderMapper.insertOrderProducts(orderProduct);		
		//if(result > 0 ) result = bottleMapper.insertBottleHistory(param.getBottleId());
		
		return result;
	}

	@Override
	public int getOrderId() {
		
		int result = 0;
		try {
			result =  orderMapper.selectOrderId();	
		} catch (DataAccessException e) {
			logger.error("getOrderId Exception==="+e.toString());
			result = 1;
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("getOrderId Exception==="+e.toString());
			e.printStackTrace();
		}
		if(result ==0) result = 1;
		return result;
	}
	
	@Override
	public int getNewOrderId(OrderVO param) {
		
		int result = 0;
		try {
			result =  orderMapper.selectCustomerOrderId(param);	
		} catch (DataAccessException e) {
			logger.error("getOrderId Exception==="+e.toString());
			result = 1;
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("getOrderId Exception==="+e.toString());
			e.printStackTrace();
		}
		if(result ==0) result = 1;
		return result;
	}

	@Override
	public OrderExtVO getOrder(Integer orderId) {
		
		OrderVO order = orderMapper.selectOrderDetail(orderId);	
		
		List<OrderProductVO> orderProductList = null;
		
		if(order.getOrderTypeCd().equals(PropertyFactory.getProperty("common.code.order.type.order")) )
			orderProductList = orderMapper.selectOrderProductList(orderId);
		else
			orderProductList = orderMapper.selectOrderInfoOfNotProduct(orderId);
		
		OrderExtVO result = new OrderExtVO();
		int productCount = 0;
		for(int i=0 ; i < orderProductList.size() ; i++) {
			productCount += orderProductList.get(i).getOrderCount();
			orderProductList.get(i).setOrderVat(Math.round(orderProductList.get(i).getOrderAmount()*0.1 *100)/100.0 );
		}
		order.setProductCount(productCount);
		
		result.setOrderProduct(orderProductList);
		result.setOrder(order);
		
		return result;
	}
	
	@Override
	public OrderExtCustomerVO getOrderPopup(Integer orderId) {
		
		OrderVO order = orderMapper.selectOrderDetail(orderId);	
		
		CustomerVO customer = customerService.getCustomerDetails(order.getCustomerId());	
		
		List<OrderProductVO> orderProductListNew = new ArrayList<OrderProductVO>();		
		List<OrderProductVO> orderProductList = orderMapper.selectOrderProductList(orderId);
		
		List<BottleVO> bottleList =  new ArrayList<BottleVO>();		
		
		for(int i=0 ; i < orderProductList.size() ; i++) {
			BottleVO bottle = new BottleVO();
			bottle.setProductId(orderProductList.get(i).getProductId());
			bottle.setProductPriceSeq(orderProductList.get(i).getProductPriceSeq());
			bottleList.add(bottle);
		}
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("bottleList", bottleList);
		map.put("customerId", order.getCustomerId());
		
		List<ProductTotalVO> productTotalList =  productService.getPriceList(map);	
		int productCount = 0;
		for(int i=0 ; i < orderProductList.size() ; i++) {
			productCount += orderProductList.get(i).getOrderCount();
			OrderProductVO OrderProduct = orderProductList.get(i) ;
			
			if(customer.getAgencyYn().equals("N") &&  orderProductList.get(i).getBottleSaleYn().equals("Y") && orderProductList.get(i).getGasId() > 0) {
				BottleVO soldBottle = new BottleVO();	
				soldBottle.setProductId(orderProductList.get(i).getProductId());
				soldBottle.setProductPriceSeq(orderProductList.get(i).getProductPriceSeq());

				ProductTotalVO productTotal = getProductTotal(productTotalList,soldBottle);

				double orderAmount = 0;
				
				if(productTotal.getCustomerBottlePrice() > 0) {
					orderAmount = productTotal.getCustomerBottlePrice() * OrderProduct.getOrderCount() ;
				}else {
					orderAmount =  productTotal.getProductBottlePrice() * OrderProduct.getOrderCount() ;
				}
				OrderProduct.setOrderAmount(orderAmount);
				OrderProduct.setOrderVat(Math.round(orderAmount * 0.1 * 100)/100.0 );
				
				OrderProductVO newOrderProduct = new OrderProductVO();
				newOrderProduct.setOrderId(order.getOrderId());
				newOrderProduct.setProductId(productTotal.getProductId());
				newOrderProduct.setProductNm(productTotal.getProductNm());
				newOrderProduct.setProductPriceSeq(productTotal.getProductPriceSeq());
				newOrderProduct.setProductCapa(productTotal.getProductCapa());
				newOrderProduct.setOrderCount(OrderProduct.getOrderCount());
				
				orderAmount = 0;
				if(customer.getAgencyYn().equals("N")) {
					if(productTotal.getCustomerProductPrice() > 0) {						
						orderAmount = productTotal.getCustomerProductPrice();
					}else {
						orderAmount = productTotal.getProductPrice();
					}
				}
				newOrderProduct.setOrderAmount(orderAmount);
				newOrderProduct.setOrderVat(Math.round(orderAmount * 0.1 * 100)/100.0 );
				orderProductListNew.add(newOrderProduct);
			}
			orderProductListNew.add(OrderProduct);
		}
		OrderExtCustomerVO result = new OrderExtCustomerVO();
		OrderExtVO orderExt = new OrderExtVO();
//		int productCount = 0;
//		for(int i=0 ; i < orderProductList.size() ; i++) {
//			productCount += orderProductList.get(i).getOrderCount();
//			orderProductList.get(i).setOrderVat(Math.round(orderProductList.get(i).getOrderAmount()*0.1 *100)/100.0 );
//		}
		
		order.setProductCount(productCount);
		
		orderExt.setOrderProduct(orderProductListNew);
		orderExt.setOrder(order);
		
		result.setOrderExt(orderExt);
		result.setCustomer(customer);
		
		return result;
	}
	
	public ProductTotalVO getProductTotal(List<ProductTotalVO> params, BottleVO bottle) {
		
		ProductTotalVO result = null;
		for(int i = 0 ; i < params.size() ; i++) {

			if(params.get(i).getProductId().equals(bottle.getProductId()) && params.get(i).getProductPriceSeq().equals(bottle.getProductPriceSeq())) {
				result = params.get(i);
				return result ;
			}
		}
		return result;
	}
	
	@Override
	public OrderExtVO getOrderNotDelivery(Integer orderId) {
		
		OrderVO order = orderMapper.selectOrderDetail(orderId);	
		
		List<OrderProductVO> orderProduct = orderMapper.selectOrderProductListNotDelivery(orderId);
		
		OrderExtVO result = new OrderExtVO();
		result.setOrder(order);
		result.setOrderProduct(orderProduct);
		
		return result;
	}

	@Override
	public int modifyOrderAdditionBottles(OrderVO param) {
		return orderMapper.updateOrderAdditionBottles(param);
	}
/*
	@Override
	public int modifyOrderProductDeliveryDt(OrderProductVO param) {
		return orderMapper.updateOrderProductDeliveryDt(param);
	}
*/

	@Override
	public int registerOrderBottle(OrderBottleVO param) {
		return orderMapper.insertOrderBottle(param);	
	}

	@Override
	public int registerOrderBottles(List<OrderBottleVO> param) {
		int result = 0;
		result =  orderMapper.insertOrderBottles(param);		
		
		return result;
	}

	@Override
	public int modifyOrderBottles(List<OrderBottleVO> param) {
		return orderMapper.updateOrderBottles(param);	
	}

	@Override
	public int modifyOrderBottle(OrderBottleVO param) {
		return orderMapper.updateOrderBottle(param);	
	}

	@Override
	public int getNextOrderProductSeq(Integer orderId) {
	
		if(orderId == null)
			return 1;
		else
			return orderMapper.selectNextOrderProductSeq(orderId);	
	}

	@Override
	public int changeOrdersProcessCd(OrderVO param) {
		
		List<String> list = null;
		if(param.getOrderIds()!=null && param.getOrderIds().length() > 0) {
			list = StringUtils.makeForeach(param.getOrderIds(), ","); 	
		}	
		
		List<Integer> orderList = new ArrayList<Integer>();
		for(int i = 0 ; i < list.size() ; i++) {
			if(list.get(i) != null) {
				int orderId = Integer.parseInt(list.get(i));
				
				Integer order = new Integer(orderId);
				orderList.add(order);	
			}
		}
		param.setOrderList(orderList);
		
		return orderMapper.updateOrdersProcessCd(param);	
	}

	private  List<OrderBottleVO> getOrderBottleList(Integer orderId){
		return orderMapper.selectOrderBottleList(orderId);	
	}

	@Override
	public OrderVO getTodayOrderForCustomer(Integer customerId) {
		return orderMapper.selectOrderTodayOfCustomer(customerId);
	}

	@Override
	public int modifyOrderAmount(Integer customerId) {
		int result = 0 ;

		List<CustomerPriceExtVO> productList = customerService.getCustomerPriceList(customerId);
		
		OrderVO order = getTodayOrderForCustomer(customerId);
		List<OrderProductVO> orderProductList = null;
		List<WorkBottleVO> workBottleList = null;
		if(order !=null) {
			orderProductList = getOrderProductList(order.getOrderId());
		
			workBottleList = workService.getWorkBottleListOfOrder(order.getOrderId());
		}
		
		double orderTotalAmount = 0;
		
		for(int j=0;j<productList.size();j++) {
			CustomerPriceExtVO customerProduct = productList.get(j);
			
			if(orderProductList != null) {
				for(int i=0;i<orderProductList.size() ; i++) {
					
					if(orderProductList.get(i).getProductId() == customerProduct.getProductId() 
							&& orderProductList.get(i).getProductPriceSeq() == customerProduct.getProductPriceSeq()) {
						if(orderProductList.get(i).getBottleSaleYn() !=null && orderProductList.get(i).getBottleSaleYn().equals("N"))
							orderProductList.get(i).setOrderAmount(orderProductList.get(i).getOrderCount()*customerProduct.getProductPrice());
						else
							orderProductList.get(i).setOrderAmount(orderProductList.get(i).getOrderCount()*customerProduct.getProductBottlePrice());
					
					result = orderMapper.updateOrderProductAmount(orderProductList.get(i));
					orderTotalAmount += orderProductList.get(i).getOrderAmount();
					}
				}			
			}
			if(workBottleList !=null) {
				for(int i=0;i<workBottleList.size();i++) {
					
					if(workBottleList.get(i).getProductId() == customerProduct.getProductId() 
							&& workBottleList.get(i).getProductPriceSeq() == customerProduct.getProductPriceSeq()) {
						if(workBottleList.get(i).getBottleSaleYn()!=null && workBottleList.get(i).getBottleSaleYn().equals("N"))
							workBottleList.get(i).setProductPrice(customerProduct.getProductPrice());
						else
							workBottleList.get(i).setProductPrice(customerProduct.getProductBottlePrice());
						
						result = workService.modifyWorkBottlePrice(workBottleList.get(i));
					}
				}
			}
		}
		
		if(order !=null) {
			order.setOrderTotalAmount(orderTotalAmount);
			
			result = orderMapper.updateOrderTotalAmount(order);
		}else {
			result = 1;
		}
		return result;
	}
	
	@Override
	public int modifyOrderAmountAll() {
		int result = 1 ;
		try {
			List<CustomerPriceVO>  customerPriceList = customerService.getCustomerPriceListAllNow();
			Integer customerId = 0;
			
			if(customerPriceList.size() == 0) result = 1;
			for(int k=0 ; k < customerPriceList.size() ; k++) {
				customerId = customerPriceList.get(k).getCustomerId();
				
				OrderVO order = getTodayOrderForCustomer(customerId);
				if(order !=null) {
					List<CustomerPriceExtVO> productList = customerService.getCustomerPriceList(customerId);
					
					List<OrderProductVO> orderProductList = getOrderProductList(order.getOrderId());
					
					List<WorkBottleVO> workBottleList = workService.getWorkBottleListOfOrder(order.getOrderId());
					
					double orderTotalAmount = 0;
					for(int j=0;j<productList.size();j++) {
						CustomerPriceExtVO customerProduct = productList.get(j);
						
						for(int i=0;i<orderProductList.size() ; i++) {
							
							if(orderProductList.get(i).getProductId() == customerProduct.getProductId() 
									&& orderProductList.get(i).getProductPriceSeq() == customerProduct.getProductPriceSeq()) {
								if(orderProductList.get(i).getBottleSaleYn().equals("N"))
									orderProductList.get(i).setOrderAmount(orderProductList.get(i).getOrderCount()*customerProduct.getProductPrice());
								else
									orderProductList.get(i).setOrderAmount(orderProductList.get(i).getOrderCount()*customerProduct.getProductBottlePrice());
							
							result = orderMapper.updateOrderProductAmount(orderProductList.get(i));
							orderTotalAmount += orderProductList.get(i).getOrderAmount();
							}
						}			
					
						for(int i=0;i<workBottleList.size();i++) {
							
							if(workBottleList.get(i).getProductId() == customerProduct.getProductId() 
									&& workBottleList.get(i).getProductPriceSeq() == customerProduct.getProductPriceSeq()) {
								if(workBottleList.get(i).getBottleSaleYn().equals("N"))
									workBottleList.get(i).setProductPrice(customerProduct.getProductPrice());
								else
									workBottleList.get(i).setProductPrice(customerProduct.getProductBottlePrice());
								
								result = workService.modifyWorkBottlePrice(workBottleList.get(i));
							}
						}
						
					}
					
					order.setOrderTotalAmount(orderTotalAmount);
					
					result = orderMapper.updateOrderTotalAmount(order);
				}
			}
		}catch (Exception e) {
            e.printStackTrace();
            
            return -1;
        }
		return result;
	}

	@Override
	public List<OrderVO> getOrderReqDtTomorrow(OrderVO param) {
		return orderMapper.selectOrderReqDtTomorrow(param);
	}

	@Override
	public List<OrderBottleVO> getOrderBottleListOfProduct(OrderProductVO param) {
		
		return orderMapper.selectOrderBottleListOfProduct(param);
	}

	@Override
	public int deleteOrderBottle(OrderBottleVO param) {
		
		return orderMapper.deleteOrderBottle(param);
	}

	@Override
	public List<OrderProductVO> getOrderProductListNotDelivery(Integer orderId) {
		
		return orderMapper.selectOrderProductListNotDelivery(orderId);
	}

	@Override
	public List<OrderBottleVO> getOrderBottleListNotDelivery(Integer orderId) {
		
		return orderMapper.selectOrderBottleListNotDelivery(orderId);
	}

	@Override
	public int deleteOrderBottles(OrderProductVO param) {
		
		return orderMapper.deleteOrderBottles(param.getOrderId());
	}

	@Override
	public int deleteOrderProductByProduct(OrderProductVO param) {
	
		return orderMapper.deleteOrderProductByProduct(param);
	}

	@Override
	public int deleteOrderBottlesByProduct(OrderProductVO param) {
		return orderMapper.deleteOrderBottlesByProduct(param);
	}

	@Override
	public List<OrderVO> getOrderListExcel(OrderVO param) {		
		return orderMapper.selectOrderListExcel(param);
	}

	@Override
	public List<OrderProductVO> getAllOrderListExcel(OrderVO param) {
		return orderMapper.selectAllOrderProductListExcel(param);
	}

	@Override
	public List<OrderProductVO> getOrderProducSimpletList(Integer orderId) {
		
		return orderMapper.selectOrderProductSimpleList(orderId);
	}

	@Override
	public int modifyOrderProcessCd0250() {
		return orderMapper.updateOrdersProcessCd0250();
	}

	@Override
	public int modifyOrderProduct(OrderProductVO param) {
		
		return orderMapper.updateOrderProduct(param);
	}

	@Override
	public int modifyOrderInfo(OrderVO param) {
		int result = 0;
		try {	
			result =  orderMapper.updateOrder(param);	
			
		} catch (DataAccessException e) {
			logger.error("modifyOrderInfo Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("modifyOrderInfo Exception==="+e.toString());
			e.printStackTrace();
		}
		return result;
	}

	
	
	
}

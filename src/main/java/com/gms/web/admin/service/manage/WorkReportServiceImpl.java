package com.gms.web.admin.service.manage;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
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
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CustomerLn2AlarmVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.CustomerSalesVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderBottleVO;
import com.gms.web.admin.domain.manage.OrderExtVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.WorkBottleRegisterVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkBottleViewVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;
import com.gms.web.admin.mapper.manage.WorkReportMapper;

@Service
public class WorkReportServiceImpl implements WorkReportService {

	private final Logger logger = LoggerFactory.getLogger(getClass());	 
	
	@Autowired
	private WorkReportMapper workMapper;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CashFlowService cashService;
	
	@Override
	public WorkReportVO getWorkReport(Integer workReportSeq) {
		
		return workMapper.selectWorkReport(workReportSeq);
	}
	
	@Override
	public List<WorkReportVO> getWorkReportList(WorkReportVO param) {
			
		if(param.getSearchDt() == null || param.getSearchDt().length() == 0) {						
			
			param.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		}
		
		return workMapper.selectWorkReportList(param);
	}
	

	@Override
	public List<WorkReportViewVO> getWorkReportListAll(WorkReportVO param) {		
		
//		logger.debug("WorkReportServiceImpl getWorkReportListAll getSearchUserId "+param.getSearchUserId());
		List<WorkReportViewVO> viewList = new ArrayList<WorkReportViewVO>();
						
		if(param.getSearchDt() == null || param.getSearchDt().length() == 0) {			
			param.setSearchDt(DateUtils.getDate("yyyy/MM/dd"));
		}		
		//날짜 비교
		List<WorkBottleVO> workBottleList = null;
		
		workBottleList = workMapper.selectWorkReportListAll(param);
		
		List<WorkReportVO> reportList = workMapper.selectWorkReportOnlyListAll(param);
		double orderAmountToday = 0.0;
		double receivedAmountToday = 0.0;
		
		for(int i = 0 ; i < reportList.size() ; i++ ) {
			
			WorkReportViewVO temp = new WorkReportViewVO();
			temp.setWorkReportSeq(reportList.get(i).getWorkReportSeq());
			temp.setReceivedAmount(reportList.get(i).getReceivedAmount());
			
			temp.setUpdateDt(reportList.get(i).getUpdateDt());	//20241010 수정일 추가
			
			if(reportList.get(i).getIncomeWay()!=null && reportList.get(i).getIncomeWay().equals("CASH")) 
				temp.setIncomeWay("(현금)");
			else if(reportList.get(i).getIncomeWay()!=null && reportList.get(i).getIncomeWay().equals("CARD")) 
				temp.setIncomeWay("(카드)");
			else
				temp.setIncomeWay("");
			
			temp.setCustomerNm(reportList.get(i).getCustomerNm());
			temp.setTaxinvoiceType(reportList.get(i).getTaxinvoiceType());
			
			List<WorkBottleVO> tempBottle = new ArrayList<WorkBottleVO>();
			List<WorkBottleVO> tempBottle1 = new ArrayList<WorkBottleVO>();
			
			temp.setBackBottles(tempBottle);
			temp.setSalesBottles(tempBottle1);
			
			if(reportList.get(i).getReceivedAmount() > 0) {
				DecimalFormat df = new DecimalFormat("###,###");
				String formatMoney = df.format(reportList.get(i).getReceivedAmount());
				temp.setReceivedAmountSt(formatMoney);
			}
			
			viewList.add(temp);			
			
			receivedAmountToday += reportList.get(i).getReceivedAmount();
		}		
		
		for(int i = 0 ; i < workBottleList.size() ; i++ ) {
			
			WorkBottleVO workBottle = workBottleList.get(i);
			//logger.debug("WorkReportServiceImpl getWorkReportListAll workBottle.getProductCapa().indexOf(Kg) "+ workBottle.getProductCapa().indexOf("Kg"));
			//if(workBottle.getProductCapa().indexOf("Kg") < 0) workBottle.setProductCapa(workBottle.getProductCapa() +"L");
			if(workBottle.getGasId() > 0) {
				if(workBottle.getProductCapa().indexOf("Kg") < 0) workBottle.setProductCapa(workBottle.getProductCapa() +"L");
			}
			//20211123 Charge_Volumn 컬럼 추가
//			if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.tcharge")) ){
//				workBottle.setProductCount(workBottle.getChargeVolumn());
//			}
			if(StringUtils.isTankProduct(workBottle.getProductId()) ){
				workBottle.setProductCount(workBottle.getChargeVolumn());
			}
			for(int j=0; j < viewList.size() ; j++) {
				WorkReportViewVO temp = viewList.get(j);
				
				// Work_Report_Seq 동일
				if((temp.getWorkReportSeq() - workBottle.getWorkReportSeq())==0) {
					
					temp.setCustomerId(workBottle.getCustomerId());
					temp.setCustomerNm(workBottle.getCustomerNm());					
					
					//비고 추가
					if(workBottle.getWorkEtc() != null && workBottle.getWorkEtc().length() > 0 ) {
						temp.setReportEtc(workBottle.getWorkEtc());
					}
					// 회수
					if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback")) 
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freechange"))
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack"))
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyBack"))
							|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come"))
							) {
						
						temp.getBackBottles().add(workBottle);
						
					}else {
						//logger.debug("WorkReportServiceImpl getWorkReportList sales workBottle.getProductId= "+ workBottle.getProductId());
						temp.getSalesBottles().add(workBottle);
					}
					
				}else {					
					
				}
			}
		}
		//logger.debug("WorkReportServiceImpl getWorkReportListAll viewList.size= "+ viewList.size());
		double totalAmount=0;
		for(int i=0;i<viewList.size() ; i++) {
			totalAmount = 0;
			if(viewList.get(i).getReportEtc() != null) {
				viewList.get(i).setReportEtc("["+viewList.get(i).getReportEtc()+"]");
			}else {
				viewList.get(i).setReportEtc("");
			}
			
			for(int j=0;j<viewList.get(i).getSalesBottles().size();j++) {
				totalAmount +=viewList.get(i).getSalesBottles().get(j).getProductPrice()*viewList.get(i).getSalesBottles().get(j).getProductCount();
				
				//logger.debug("WorkReportServiceImpl getWorkReportListAll viewList.get(i).getSalesBottles().size() .getProductId= "+ viewList.get(i).getSalesBottles().get(j).getProductNm());
			}
			viewList.get(i).setOrderAmount(totalAmount);
			orderAmountToday += totalAmount;
			for(int j=0;j<viewList.get(i).getBackBottles().size();j++) {
				//logger.debug("WorkReportServiceImpl getWorkReportList viewList.get(i).getBackBottles().size() .getProductId= "+ viewList.get(i).getBackBottles().get(j).getProductNm());
			}

		}

		if(viewList.size() > 0 ) {
			viewList.get(0).setOrderAmountToday(orderAmountToday);
//			logger.debug("WorkReportServiceImpl getWorkReportListAll orderAmountToday = "+ orderAmountToday);
			
			viewList.get(0).setReceivedAmountToday(receivedAmountToday);
		}
		
		return viewList;
	}

	
	@Override
	@Transactional
	public int registerWorkReportForOrder(WorkReportVO param) {
//		logger.debug("****registerWorkReportForOrder start ="+param.getBottleWorkCd() );
		int result = 1;
		try {
			//Order 정보가져오기
			OrderVO order = orderService.getOrderDetail(param.getOrderId());
			//Order_Product 비교
			List<OrderProductVO> orderProductList = orderService.getOrderProducSimpletList(param.getOrderId());
			int orderProductSeq = orderProductList.size() +1;
			if(orderProductList.size() > 0) orderProductSeq = orderProductList.get(orderProductList.size()-1).getOrderProductSeq()+1;
			
			for(int i=orderProductList.size()-1 ; i >= 0  ; i-- ) {
				OrderProductVO removeP = orderProductList.get(i);
				if(removeP.getBottleChangeYn().equals("N") && removeP.getBottleSaleYn().equals("N") && removeP.getIncomeYn().equals("N") && removeP.getOutYn().equals("N")) {
					orderProductList.remove(i);
				}
			}

			List<OrderBottleVO> orderBottleListNot = orderService.getOrderBottleListNotDelivery(param.getOrderId());
			//Bottle 정보 가져오기
			List<BottleVO> bottleList = getBottleList(param);

			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();	
			List<OrderProductVO> newOrderProductList = new ArrayList<OrderProductVO>();
			
			//Work_Report_Seq 가져오기
			boolean registerFlag = false;
			param.setCustomerId(order.getCustomerId());
			if(param.getUserId() == null) {
				param.setUserId(param.getCreateId());
				param.setUpdateId(param.getCreateId());
			}
			int workReportSeq = getWorkReportSeqForCustomerToday(param);
			int workSeq=1;
			if(workReportSeq <= 0) {
				//workReportSeq = getWorkReportSeq();
				registerFlag = true;
				
				result = workMapper.insertWorkReport(param);
				workReportSeq = getWorkReportSeqForCustomerToday(param);
				param.setWorkReportSeq(workReportSeq);	
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}
			param.setWorkReportSeq(workReportSeq);
			param.setWorkCd(param.getBottleWorkCd());
			
			String strBottleSaleYn1 = "Y";
			
			if( param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))   
				|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas"))
				|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent")) ) {
				strBottleSaleYn1 = "N";
			}
			
			boolean updateOrderAddFlag = false;
			double receivableAmount = 0 ;
			Integer insertOrderProductSeq = 0;
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("bottleList", bottleList);
			map.put("customerId", param.getCustomerId());
			
			List<ProductTotalVO> productTotalList =  productService.getPriceList(map);	
			
			
			for(int i = bottleList.size()-1 ; i >= 0 ; i--) {
				BottleVO soldBottle = bottleList.get(i);
				ProductTotalVO productTotal = getProductTotal(productTotalList,soldBottle);//2021-06-19
				
				soldBottle.setCustomerId(param.getCustomerId());
				soldBottle.setUpdateId(param.getCreateId());
				soldBottle.setBottleWorkCd(param.getBottleWorkCd());
				soldBottle.setDeleteYn("N");
				// 기등록된 주문 상품 중 납품안된 주문 처리
				for(int j = orderBottleListNot.size()-1 ; j >= 0  ; j--) {
					OrderBottleVO orderBottle = orderBottleListNot.get(j);
					String strBottleSaleYn ="";
					String strIncomeYn = "N";
					String strOutYn = "N";
					
					for(int k=0 ; k < orderProductList.size() ; k++) {
						OrderProductVO orderProduct = orderProductList.get(k);
						
						if(orderBottle.getOrderProductSeq().equals( orderProduct.getOrderProductSeq())) {
							strBottleSaleYn = orderProduct.getBottleSaleYn();
							strIncomeYn =orderProduct.getIncomeYn();
							strOutYn =orderProduct.getOutYn();
							break;
						}						
					}
					if(soldBottle.getProductId().equals(orderBottle.getProductId()) 
							&& soldBottle.getProductPriceSeq().equals(orderBottle.getProductPriceSeq() )
							&& ( (strBottleSaleYn.equals("Y") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) )
									|| (strBottleSaleYn.equals("N") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) ) )   
									|| (param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas")) )
									|| (param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent")) )
									|| (strIncomeYn.equals("Y") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) )
									|| (strOutYn.equals("Y") && param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out")) )   
							) {
						soldBottle.setDeleteYn("Y");
						orderBottle.setBottleBarCd(soldBottle.getBottleBarCd());
						
						WorkBottleVO workBottle = makeWorkBottle(soldBottle);
						workBottle.setWorkReportSeq(param.getWorkReportSeq());
						workBottle.setAgencyYn(param.getAgencyYn());
						workBottle.setWorkSeq(workSeq++);	
						workBottle.setBottleType(param.getBottleType());
						
						workBottle.setWorkEtc(param.getReportEtc());
						logger.debug("########### WorkReportServiceImpl registerWorkReportNoOrder workBottle.setWorkEtc =" + workBottle.getWorkEtc());
						if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {	
							workBottle.setBottleSaleYn("Y");
							
							if(productTotal.getCustomerBottlePrice() > 0) {
								workBottle.setProductPrice(productTotal.getCustomerBottlePrice());
							}else {
								workBottle.setProductPrice( productTotal.getProductBottlePrice());	
							}
							if(param.getAgencyYn().equals("N")) {
								if(productTotal.getCustomerProductPrice() > 0) {
									workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getCustomerProductPrice());
									workBottle.setGasPrice(productTotal.getCustomerProductPrice());
								}else {
									workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getProductPrice());
									workBottle.setGasPrice(productTotal.getProductPrice());
								}
							}
						}else	if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come"))) {	
							workBottle.setBottleSaleYn("N");
							workBottle.setProductPrice( 0);	
							workBottle.setGasPrice( 0);
						}else	if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))) {	
							workBottle.setBottleSaleYn("N");
							workBottle.setProductPrice( 0);	
							workBottle.setGasPrice( 0);	
						}else {
							workBottle.setBottleSaleYn("N");
							if(productTotal.getCustomerProductPrice() > 0)
								workBottle.setProductPrice(productTotal.getCustomerProductPrice());
							else
								workBottle.setProductPrice(productTotal.getProductPrice());														
						}
						
						workBottleList.add(workBottle); 						
						
						result = orderService.modifyOrderBottle(orderBottle);
						orderBottleListNot.remove(j);
						
						receivableAmount += workBottle.getProductPrice();
						
						break;
					}
				}//for(int j = orderBottleListNot.size()-1 ; j >= 0  ; j--) {
				
				// 신규 주문 등록
				if(soldBottle.getDeleteYn().equals("N")) {
					updateOrderAddFlag = true;
					OrderProductVO newOrderProduct = new OrderProductVO();					
					
					newOrderProduct.setOrderId(param.getOrderId());		
					newOrderProduct.setOrderProductSeq(orderProductSeq++);
					newOrderProduct.setProductId(soldBottle.getProductId());
					newOrderProduct.setProductPriceSeq(soldBottle.getProductPriceSeq());
					newOrderProduct.setBottleBarCd(soldBottle.getBottleBarCd());
					newOrderProduct.setGasId(soldBottle.getGasId());
					newOrderProduct.setCreateId(param.getUserId());
					newOrderProduct.setOrderCount(1);
					newOrderProduct.setOrderProductEtc("현장추가");
					newOrderProduct.setBottleWorkCd(param.getBottleWorkCd());
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {									
						newOrderProduct.setBottleChangeYn("N");
						newOrderProduct.setBottleSaleYn("Y");
						newOrderProduct.setIncomeYn("N");
						newOrderProduct.setOutYn("N");
						
						if(productTotal.getCustomerBottlePrice() > 0) 
							newOrderProduct.setOrderAmount(productTotal.getCustomerBottlePrice());
						else 
							newOrderProduct.setOrderAmount( productTotal.getProductBottlePrice());
						
						if(productTotal.getCustomerProductPrice() > 0 )
							newOrderProduct.setOrderAmount(newOrderProduct.getOrderAmount() + productTotal.getCustomerProductPrice());
						else
							newOrderProduct.setOrderAmount(newOrderProduct.getOrderAmount() + productTotal.getProductPrice());
					}else	if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come"))) {	
						newOrderProduct.setBottleChangeYn("N");
						newOrderProduct.setBottleSaleYn("N");
						newOrderProduct.setIncomeYn("Y");
						newOrderProduct.setOutYn("N");
						newOrderProduct.setOrderAmount(0);
					}else	if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))) {	
						newOrderProduct.setBottleChangeYn("N");
						newOrderProduct.setBottleSaleYn("N");
						newOrderProduct.setIncomeYn("N");
						newOrderProduct.setOutYn("Y");
						newOrderProduct.setOrderAmount(0);
					}else {
						newOrderProduct.setBottleChangeYn("Y");
						newOrderProduct.setBottleSaleYn("N");
						newOrderProduct.setIncomeYn("N");
						newOrderProduct.setOutYn("N");
						
						if(productTotal.getCustomerProductPrice() > 0)
							newOrderProduct.setOrderAmount(productTotal.getCustomerProductPrice());
						else
							newOrderProduct.setOrderAmount(productTotal.getProductPrice());						
					}
					newOrderProduct.setRetrievedYn("N");
					newOrderProduct.setAsYn("N");
					
					boolean isNew  = true;
					for(int k = 0 ; k < newOrderProductList.size(); k++) {
						// 동일  상품 주문 존재
						if(newOrderProductList.get(k).getProductId().equals(newOrderProduct.getProductId()) &&
								newOrderProductList.get(k).getProductPriceSeq().equals(newOrderProduct.getProductPriceSeq()) ) {
							if(newOrderProductList.get(k).getBottleSaleYn().equals(newOrderProduct.getBottleSaleYn()) ){
						
								isNew  = false;
								newOrderProductList.get(k).setOrderCount(newOrderProductList.get(k).getOrderCount()+1);
								newOrderProductList.get(k).setOrderAmount(newOrderProductList.get(k).getOrderAmount() + newOrderProduct.getOrderAmount());

							}
						}
					}
					if(isNew)
						newOrderProductList.add(newOrderProduct);
						
					WorkBottleVO workBottle = makeWorkBottle(soldBottle);
					workBottle.setWorkReportSeq(param.getWorkReportSeq());
					workBottle.setWorkSeq(workSeq++);
					workBottle.setProductPrice(newOrderProduct.getOrderAmount());
					workBottle.setBottleSaleYn(newOrderProduct.getBottleSaleYn());
					workBottle.setBottleWorkCd(param.getBottleWorkCd());
					workBottle.setBottleType(param.getBottleType());
					workBottle.setWorkEtc(param.getReportEtc());
					//20201220
					workBottle.setAgencyYn(param.getAgencyYn());
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {								

						if(productTotal.getCustomerBottlePrice() > 0) {
							workBottle.setProductPrice(productTotal.getCustomerBottlePrice());
						}else {
							workBottle.setProductPrice( productTotal.getProductBottlePrice());	
						}
						if(param.getAgencyYn().equals("N")) {
							if(productTotal.getCustomerProductPrice() > 0) {
								workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getCustomerProductPrice());
								workBottle.setGasPrice(productTotal.getCustomerProductPrice());
							}else {
								workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getProductPrice());
								workBottle.setGasPrice(productTotal.getProductPrice());
							}
						}
					}
					
					workBottleList.add(workBottle); 
					receivableAmount += workBottle.getProductPrice();
					
					OrderBottleVO orderBottle = makeOrderBottle(newOrderProduct, soldBottle);
					orderBottleList.add(orderBottle);
				}
			}
			
			if(newOrderProductList.size() > 0)
				result = orderService.registerOrderProducts(newOrderProductList);
			if(orderBottleList.size() > 0 )
				result = orderService.registerOrderBottles(orderBottleList);

			if(workBottleList.size() > 0)
				result = workMapper.insertWorkBottles(workBottleList);			
							
			// 처리안된 주문 상품 삭제 OrderCount  와  SaleCount 비교
			List<OrderProductVO> allOrderProductList = null;						
			allOrderProductList = orderService.getOrderProductListNew(order.getOrderId());
			
			List<OrderProductVO> updateOrderProducts = new ArrayList<OrderProductVO>();
			List<OrderProductVO> deleteOrderProducts = new ArrayList<OrderProductVO>();
			
			for(int j=0; j < allOrderProductList.size() ; j++) {
				OrderProductVO paramOrderProduct = allOrderProductList.get(j);
//				logger.debug("****paramOrderProduct.getSalesCount ="+paramOrderProduct.getSalesCount());
//				logger.debug("****paramOrderProduct.getOrderCount ="+paramOrderProduct.getOrderCount());
				
				if(paramOrderProduct.getSalesCount() == paramOrderProduct.getOrderCount()) deleteOrderProducts.add(paramOrderProduct);
				else if(paramOrderProduct.getSalesCount() > 0 && paramOrderProduct.getOrderCount() > paramOrderProduct.getSalesCount()) {
					paramOrderProduct.setOrderAmount(paramOrderProduct.getOrderAmount() / paramOrderProduct.getOrderCount());
					paramOrderProduct.setOrderCount(paramOrderProduct.getOrderCount() - paramOrderProduct.getSalesCount());
					paramOrderProduct.setUpdateId(param.getCreateId());
					updateOrderProducts.add(paramOrderProduct);
				}
			}
//			logger.debug("****updateOrderProducts.size ="+updateOrderProducts.size());
//			logger.debug("****deleteOrderProducts.size ="+deleteOrderProducts.size());
			if(updateOrderProducts.size() > 0 || deleteOrderProducts.size() > 0) updateOrderAddFlag = true;
			if(updateOrderProducts.size() > 0 ) result = orderService.modifyOrderProducts(updateOrderProducts);
			if(deleteOrderProducts.size() > 0 ) result = orderService.deleteOrderProducts(deleteOrderProducts);
			
			// Order 상태 정보 변경
			allOrderProductList = null;	
			allOrderProductList = orderService.getOrderProductList(param.getOrderId());
			int remainCount = 0;
			double orderAmount = 0;

			boolean orderCompleted = false;
			
			for(int j=0; j < allOrderProductList.size() ; j++) {
				OrderProductVO orderProduct = allOrderProductList.get(j);
				remainCount += allOrderProductList.get(j).getSalesCount();
				orderAmount += allOrderProductList.get(j).getOrderAmount();
			}
			
			if(remainCount <= 0) orderCompleted = true;			
//			logger.debug("****allOrderProductList.size ="+allOrderProductList.size());
			if(allOrderProductList.size() > 1) {
				order.setOrderProductNm(allOrderProductList.get(0).getProductNm()+" 외 "+(allOrderProductList.size()-1));
				order.setOrderProductCapa(allOrderProductList.get(0).getProductCapa()+" 외 "+(allOrderProductList.size()-1));
			}
			if(updateOrderAddFlag && orderAmount > 0) {
				order.setOrderTotalAmount(orderAmount);	
				order.setUpdateId(param.getUserId());
//				result = orderService.modifyOrderAdditionBottles(order);
			}
			
			if(order.getOrderProcessCd().equals(PropertyFactory.getProperty("common.code.order.process.receive")) && orderCompleted) {
				order.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));	
				order.setSalesId(param.getCreateId());
				order.setChOrderId(order.getOrderId());
				order.setUpdateId(param.getUserId());
				result = orderService.modifyOrderInfo(order);
			}else if(updateOrderAddFlag && orderAmount > 0) {
				
				result = orderService.modifyOrderAdditionBottles(order);
			}
			
			//WorkReprot
			param.setOrderId(order.getOrderId());			
			param.setWorkProductNm(order.getOrderProductNm());
			param.setWorkProductCapa(order.getOrderProductCapa());
			
			result = workMapper.modifyWorkReportProduct(param);			

			//CashFlow
			double cashTotal = 0;
			for(int i=0; i < workBottleList.size() ; i++) {
				cashTotal+=workBottleList.get(i).getProductPrice();
			}
			
			if(cashTotal > 0)
				result = registerCashFlow(param,cashTotal);
						
			//Customer Bottle_Own_Count 증가
			// common.bottle.status.salesgas / common.bottle.status.agencyRent 처리
			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas")) ) {
				
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleOwnCount(bottleList.size());
				//20201220
				customer.setAgencyYn(param.getAgencyYn());
				
				result = changeCustomerProduct(workBottleList);	//Customer_Product 등록				
//				result = customerService.modifyCustomerBottleCount(customer);
			}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent")) ) {
				
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleRentCount(bottleList.size());
				//20201220
				customer.setAgencyYn(param.getAgencyYn());
				
				result = changeCustomerProduct(workBottleList);	//Customer_Product 등록				
//				result = customerService.modifyCustomerBottleRentCount(customer);
			}
			
			List<BottleVO> bottleUList =  new ArrayList<BottleVO>();
			for(int i = 0 ; i < bottleList.size() ; i++) {
				BottleVO bottle  = bottleList.get(i);
				// Bottle 정보 업데이트
				bottle.setOrderId(order.getOrderId());				
				bottle.setCustomerId(param.getCustomerId());
				bottle.setBottleWorkId(param.getUserId());
				bottle.setUpdateId(param.getUserId());
				bottle.setBottleType(param.getBottleType());
				bottle.setBottleWorkCd(param.getBottleWorkCd());
				
				// 20210612 history 분리
//				result = bottleService.modifyBottleOrder(bottle);
//				result = bottleService.modifyBottleOrderV2(bottle);
				bottleUList.add(bottle);		//20210619
			}
			
			result = bottleService.modifyBottlesOrder(bottleUList);		
			//20210612 history 분리
			result = bottleService.registerBottlesHistory(bottleList);	
			
		} catch (DataAccessException e) {
			result = 0;
			e.printStackTrace();
			logger.error("WorkReportSeviceImpl", e.toString());
		} catch (Exception e) {			
			result = 0;
			e.printStackTrace();
			logger.error("WorkReportSeviceImpl", e.toString());
		}
		return result;		
	}
	
	

	@Override
	@Transactional
	public int registerWorkReportNoOrder(WorkReportVO param) {

		int result = 0;
		
		try {		
			//기존 주문이 있는지 여부 확인
			OrderVO orderTemp =  null;
					
			//if(param.getAgencyYn().equals("N") || !param.getUserId().equals("factory") ) orderTemp = orderService.getLastOrderForCustomer(param.getCustomerId());
			orderTemp = orderService.getLastOrderForCustomer(param.getCustomerId());

//			if(orderTemp != null && (orderTemp.getSalesId() ==null || (orderTemp.getSalesId() != null && orderTemp.getSalesId().equals(param.getCreateId()) )  ) ){
			if(orderTemp != null ) {
				param.setOrderId(orderTemp.getOrderId());
				//result = registerWorkReportForOrder(param);
				result = registerWorkReportForOrder(param);
//				result = registerWorkReportForOrderNew(param);
			}else {
				//Bottle 정보 가져오기
				List<BottleVO> bottleList = getBottleList(param);
				
				if(param.getUserId() == null)
					param.setUserId(param.getCreateId());
				//Work_Report_Seq 가져오기
				boolean registerFlag = false;
				int workSeq=1;
				int workReportSeq = 0;
				
				//if(param.getAgencyYn().equals("N") || !param.getUserId().equals("factory") ) workReportSeq = getWorkReportSeqForCustomerToday(param);
				workReportSeq = getWorkReportSeqForCustomerToday(param); //20210619 재수정
				//workReportSeq = 0;
				if(workReportSeq <= 0) {
					//workReportSeq = getWorkReportSeq();
					registerFlag = true;
					
					result = workMapper.insertWorkReport(param);
					workReportSeq = getWorkReportSeqForCustomerToday(param);
					param.setWorkReportSeq(workReportSeq);	
				}else {
					workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
				}
				
				//TB_Work_Reprot 등록
				param.setWorkReportSeq(workReportSeq);							
				
				//TB_Work_Bottle 등록					
				List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
				List<OrderProductVO> orderProductList = new ArrayList<OrderProductVO>();
				List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();	
				
				OrderVO order = new OrderVO();				
				
				if(param.getUserId() != null && param.getUserId().length() > 0)
					order.setCreateId(param.getUserId());
				else
					order.setCreateId(param.getCreateId());
				order.setUpdateId(param.getCreateId());
				order.setCustomerId(param.getCustomerId());
				order.setMemberCompSeq(1);
				order.setOrderTypeCd(PropertyFactory.getProperty("common.code.order.type.order"));				
				
				Calendar cal = Calendar.getInstance();
				int amPm = cal.get(Calendar.AM_PM);
				order.setDeliveryReqDt(cal.getTime());
				
				if (amPm == 1 ) order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.PM"));
				else  order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.AM"));
				
				order.setOrderEtc("현장주문");
				order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));
				if(param.getUserId() != null && param.getUserId().length() > 0)
					order.setSalesId(param.getUserId());
				else
					order.setSalesId(param.getCreateId());
				order.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				
				// Order 먼저 등록  2020-08-28
				result = orderService.registerOrder(order);
				
				int orderId = orderService.getNewOrderId(order);				
				order.setOrderId(orderId);
				
				OrderProductVO tempOrderProduct = null;
				
				WorkBottleVO workBottle = null;
				BottleVO tempBottle = null;
				ProductTotalVO tempProductTotal = null;
				
				//용기 정보를 이횽해 상품 정보 가져옴				
				int orderProductSeq = 1;
				int orderBottleSeq= 1;
				int orderCount = 1;
				double orderTotalAmount = 0;		
				double receivableAmount = 0;
				String orderProductNm = "";
				String orderProductCapa = "";	
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("bottleList", bottleList);
				map.put("customerId", param.getCustomerId());
				
				List<ProductTotalVO> productTotalList =  productService.getPriceList(map);	

				List<BottleVO> bottleUList =  new ArrayList<BottleVO>();
				for(int i = 0; i < bottleList.size() ; i++) {		
					
					tempBottle = bottleList.get(i);
					tempBottle.setCustomerId(param.getCustomerId());
					tempBottle.setUpdateId(param.getUserId());
					tempBottle.setBottleWorkCd(param.getBottleWorkCd());
			
					tempOrderProduct = new OrderProductVO();
					
					//tempProductTotal = productService.getBottleGasCapa(tempBottle);		
					tempBottle.setCustomerId(param.getCustomerId());
					
					tempProductTotal = getProductTotal(productTotalList,tempBottle);//2021-06-19
					//tempProductTotal = productService.getPrice(tempBottle);		//2020-06-15
	
					tempOrderProduct.setProductId(tempProductTotal.getProductId());
					tempOrderProduct.setProductPriceSeq(tempProductTotal.getProductPriceSeq());
					tempOrderProduct.setOrderId(orderId);
					tempOrderProduct.setBottleWorkCd(param.getBottleWorkCd());
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
						tempOrderProduct.setBottleChangeYn("N");
						tempOrderProduct.setBottleSaleYn("Y");	
						tempOrderProduct.setIncomeYn("N");
						tempOrderProduct.setOutYn("N");
					}else	if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come"))) {	
						
						tempOrderProduct.setBottleChangeYn("N");
						tempOrderProduct.setBottleSaleYn("N");
						tempOrderProduct.setIncomeYn("Y");
						tempOrderProduct.setOutYn("N");
						
					}else	if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))) {
						tempOrderProduct.setBottleChangeYn("N");
						tempOrderProduct.setBottleSaleYn("N");
						tempOrderProduct.setIncomeYn("N");
						tempOrderProduct.setOutYn("Y");
						
					}else {
						tempOrderProduct.setBottleChangeYn("Y");
						tempOrderProduct.setBottleSaleYn("N");
						tempOrderProduct.setIncomeYn("N");
						tempOrderProduct.setOutYn("N");
					}
					tempOrderProduct.setRetrievedYn("N");
					tempOrderProduct.setAsYn("N");
					//tempOrderProduct.setProductDeliveryDt(cal.getTime());
					tempOrderProduct.setOrderProductEtc("현장추가");					
					
					// TB_Order Product_Nm, Product_Capa 초기값
					if(i==0) {
						orderProductNm = tempProductTotal.getProductNm();
						orderProductCapa = tempProductTotal.getProductCapa();
					}
					
					OrderBottleVO tempOrderBottle = new OrderBottleVO();
					tempOrderBottle.setOrderId(orderId);
					tempOrderBottle.setOrderProductSeq(orderBottleSeq++);
					tempOrderBottle.setCreateId(param.getCreateId());
					tempOrderBottle.setUpdateId(param.getCreateId());	
					
					//orderProduct 					
					if(i > 0 ) {
						BottleVO tempBottle1 = bottleList.get(i-1);
						
						// 이전 용기의 상품 비교
						if(tempBottle.getProductId() == tempBottle1.getProductId() 
								&& tempBottle.getProductPriceSeq() == tempBottle1.getProductPriceSeq()) {
							orderCount++;							
							
							orderProductList.get(orderProductSeq-1).setOrderCount(orderCount);
							if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {								
								if(tempProductTotal.getCustomerBottlePrice() > 0)
									orderProductList.get(orderProductSeq-1).setOrderAmount(orderCount*tempProductTotal.getCustomerBottlePrice());	
								else
									orderProductList.get(orderProductSeq-1).setOrderAmount(orderCount*tempProductTotal.getProductBottlePrice());									
								
								if(param.getAgencyYn().equals("N")) {
									if(tempProductTotal.getCustomerProductPrice() > 0) {
										orderProductList.get(orderProductSeq-1).setOrderAmount(orderProductList.get(orderProductSeq-1).getOrderAmount() + orderCount*tempProductTotal.getCustomerProductPrice());	
									}else {
										orderProductList.get(orderProductSeq-1).setOrderAmount(orderProductList.get(orderProductSeq-1).getOrderAmount() + orderCount*tempProductTotal.getProductPrice());
									}
								}
							}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) 
									|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out")) ) {	
								orderProductList.get(orderProductSeq-1).setOrderAmount(0);
							} else {
								if(tempProductTotal.getCustomerProductPrice() > 0)
									orderProductList.get(orderProductSeq-1).setOrderAmount(orderCount*tempProductTotal.getCustomerProductPrice());	
								else
									orderProductList.get(orderProductSeq-1).setOrderAmount(orderCount*tempProductTotal.getProductPrice());								
							}
//							receivableAmount += orderProductList.get(orderProductSeq-1).getOrderAmount()/orderCount;
						}else {
							orderProductSeq++;
							orderCount  = 1;
							tempOrderProduct.setOrderCount(orderCount);
							if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {
								if(tempProductTotal.getCustomerBottlePrice() > 0)
									tempOrderProduct.setOrderAmount(tempProductTotal.getCustomerBottlePrice());	
								else
									tempOrderProduct.setOrderAmount(tempProductTotal.getProductBottlePrice());
								
								if(param.getAgencyYn().equals("N")) {
									if(tempProductTotal.getCustomerProductPrice() > 0) {
										tempOrderProduct.setOrderAmount(tempOrderProduct.getOrderAmount() + orderCount*tempProductTotal.getCustomerProductPrice());	
									}else {
										tempOrderProduct.setOrderAmount(tempOrderProduct.getOrderAmount() + orderCount*tempProductTotal.getProductPrice());
									}
								}
							}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) 
									|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out")) ) {	
								tempOrderProduct.setOrderAmount(0);
							}else {
								if(tempProductTotal.getCustomerProductPrice() > 0)
									tempOrderProduct.setOrderAmount(tempProductTotal.getCustomerProductPrice());
								else
									tempOrderProduct.setOrderAmount(tempProductTotal.getProductPrice());									
							}
							tempOrderProduct.setOrderProductSeq(orderProductSeq);
							
							orderProductList.add(tempOrderProduct);			
//							receivableAmount += tempOrderProduct.getOrderAmount();
						}							
					}else {
						
						tempOrderProduct.setOrderCount(orderCount);
						if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {
							if(tempProductTotal.getCustomerBottlePrice() > 0)
								tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getCustomerBottlePrice());	
							else
								tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getProductBottlePrice());

							if(param.getAgencyYn().equals("N")) {
								if(tempProductTotal.getCustomerProductPrice() > 0) {
									tempOrderProduct.setOrderAmount(tempOrderProduct.getOrderAmount() + orderCount*tempProductTotal.getCustomerProductPrice());	
								}else {
									tempOrderProduct.setOrderAmount(tempOrderProduct.getOrderAmount() + orderCount*tempProductTotal.getProductPrice());
								}
							}
						}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) 
								|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out")) ) {	
							tempOrderProduct.setOrderAmount(0);
						}else {
							if(tempProductTotal.getCustomerProductPrice() > 0)
								tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getCustomerProductPrice());		
							else
								tempOrderProduct.setOrderAmount(orderCount*tempProductTotal.getProductPrice());
						}
						tempOrderProduct.setOrderProductSeq(orderProductSeq);
						tempOrderProduct.setCreateId(param.getCreateId());
						orderProductList.add(tempOrderProduct);				
						receivableAmount += tempOrderProduct.getOrderAmount()/orderCount;
					}
					workBottle = makeWorkBottle(tempBottle);
					workBottle.setSearchDt(param.getSearchDt());
					workBottle.setWorkReportSeq(workReportSeq);
					workBottle.setBottleType(param.getBottleType());
					workBottle.setWorkSeq(workSeq++);
					//20240927 대여/판매 등 비고 추가
					workBottle.setWorkEtc(param.getReportEtc());
					//20201220
					
					workBottle.setAgencyYn(param.getAgencyYn());
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {
						workBottle.setBottleSaleYn("Y");
						/*
						 * // 가스판매 추가 20210710
						WorkBottleVO workBottleAdditional = makeWorkBottle(tempBottle);
						workBottleAdditional.setWorkReportSeq(param.getWorkReportSeq());
						workBottleAdditional.setAgencyYn(param.getAgencyYn());
						workBottleAdditional.setWorkSeq(workSeq++);	
						workBottleAdditional.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesgas"));
						workBottleAdditional.setProductPrice(tempProductTotal.getProductPrice());
						
						if(tempProductTotal.getCustomerBottlePrice() > 0) {
							workBottle.setProductPrice(tempProductTotal.getCustomerBottlePrice());
						}else {
							workBottle.setProductPrice( tempProductTotal.getProductBottlePrice());	
						}
						
						if(tempProductTotal.getCustomerProductPrice() > 0) {
							workBottleAdditional.setProductPrice(tempProductTotal.getCustomerProductPrice());
						}else {
							workBottleAdditional.setProductPrice(tempProductTotal.getProductPrice());
						}
						receivableAmount += workBottleAdditional.getProductPrice();
						workBottleList.add(workBottleAdditional); 	
						*/
						
						if(tempProductTotal.getCustomerBottlePrice() > 0) {
							workBottle.setProductPrice(tempProductTotal.getCustomerBottlePrice());
						}else {
							workBottle.setProductPrice( tempProductTotal.getProductBottlePrice());	
						}
						if(param.getAgencyYn().equals("N")) {
							if(tempProductTotal.getCustomerProductPrice() > 0) {
								workBottle.setProductPrice(workBottle.getProductPrice() + tempProductTotal.getCustomerProductPrice());
								workBottle.setGasPrice(tempProductTotal.getCustomerProductPrice());
								receivableAmount += tempProductTotal.getCustomerProductPrice();
							}else {
								workBottle.setProductPrice(workBottle.getProductPrice() + tempProductTotal.getProductPrice());
								workBottle.setGasPrice(tempProductTotal.getProductPrice());
								receivableAmount += tempProductTotal.getProductPrice();
							}
						}
					}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) 
							|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out")) ) {	
						workBottle.setBottleSaleYn("N");
						workBottle.setProductPrice(0);
					} else {
						workBottle.setBottleSaleYn("N");
						if(tempProductTotal.getCustomerProductPrice() > 0)
							workBottle.setProductPrice(tempProductTotal.getCustomerProductPrice());
						else
							workBottle.setProductPrice(tempProductTotal.getProductPrice());				
					}		
					receivableAmount += workBottle.getProductPrice();
					
					tempBottle.setBottleWorkCd(param.getBottleWorkCd());
					tempBottle.setBottleWorkId(param.getCreateId());
					tempBottle.setCustomerId(param.getCustomerId());
					tempBottle.setBottleType(workBottle.getBottleType());
					tempBottle.setUpdateId(param.getCreateId());				
					tempBottle.setOrderId(orderId);
					tempBottle.setOrderProductSeq(i+1);
					
					//20210612 history 분리
//					result = bottleService.modifyBottleOrder(tempBottle);					
					//result = bottleService.modifyBottleOrderV2(tempBottle);			
					bottleUList.add(tempBottle);
					
					// TB_Order_Bottle
					tempOrderBottle.setProductId(tempBottle.getProductId());
					tempOrderBottle.setProductPriceSeq(tempBottle.getProductPriceSeq());
					tempOrderBottle.setBottleBarCd(tempBottle.getBottleBarCd()); 		
					tempOrderBottle.setUpdateId(param.getCreateId());
					
					orderBottleList.add(tempOrderBottle);		
					workBottleList.add(workBottle);							
				}
				
				//20210612 history 분리
				result = bottleService.modifyBottlesOrder(bottleUList);		
				result = bottleService.registerBottlesHistory(bottleList);				
				
				if( orderProductList.size() > 1) {
					orderProductNm = orderProductNm + " 외 " + (orderProductList.size()-1);
					orderProductCapa = orderProductCapa + " 외 " + (orderProductList.size()-1);
				}
				order.setOrderProductNm(orderProductNm);
				order.setOrderProductCapa(orderProductCapa);
				
				for(int k=0; k < workBottleList.size() ; k++) { 
					orderTotalAmount += workBottleList.get(k).getProductPrice();
					
				}
				order.setOrderTotalAmount(orderTotalAmount);				
				
				//TB_Order 등록TB_Order_Product			등록
				//result  = orderService.registerOrderAndProduct(order, orderProductList);		//2020-080-28 
				result  = orderService.modifyOrderRegiProduct(order, orderProductList);
				
				// Order_Bottle 등록
				if(orderBottleList.size() > 0)
					result = orderService.registerOrderBottles(orderBottleList);
				
				param.setWorkCd(param.getBottleWorkCd());
				param.setWorkProductNm(orderProductNm);
				param.setWorkProductCapa(orderProductCapa);
				param.setOrderId(orderId);
				param.setOrderAmount(orderTotalAmount);
				param.setWorkDt(cal.getTime());
				//param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
								
				// common.bottle.status.salesgas / common.bottle.status.agencyRent 처리 필요
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas"))) {
				
					CustomerVO customer = new CustomerVO();
					customer.setCustomerId(param.getCustomerId());
					customer.setUpdateId(param.getCreateId());					
					customer.setBottleOwnCount(bottleList.size());
					customer.setAgencyYn(param.getAgencyYn());
					
					result = changeCustomerProduct(workBottleList);	//Customer_Product 등록
//					result = customerService.modifyCustomerBottleCount(customer);
				}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))  || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent")) ) {
					
					CustomerVO customer = new CustomerVO();
					customer.setCustomerId(param.getCustomerId());
					customer.setUpdateId(param.getCreateId());
					customer.setBottleRentCount(bottleList.size());
					customer.setAgencyYn(param.getAgencyYn());
					
					result = changeCustomerProduct(workBottleList);	//Customer_Product 등록
//					result = customerService.modifyCustomerBottleRentCount(customer);
				}
				
				if(registerFlag) {
					result = workMapper.updateWorkReportOrder(param);
				}else {
					
					param.setUpdateId(param.getCreateId());
					result = workMapper.modifyWorkReportOrderId(param);
				}
				if(workBottleList.size() > 0)
					result = workMapper.insertWorkBottles(workBottleList);	
			
				//CashFlow
				double cashTotal = 0;
				for(int i=0; i < workBottleList.size() ; i++) {
					cashTotal+=workBottleList.get(i).getProductPrice();
				}
				if(cashTotal > 0)
					result = registerCashFlow(param,cashTotal);
			
			}
		} catch (DataAccessException e) {			
			e.printStackTrace();
//			logger.error("registerWorkReportNoOrder DataAccessException= param=="+param.getUserId());
			logger.error("registerWorkReportNoOrder DataAccessException==="+e.toString());
		} catch (Exception e) {			
			e.printStackTrace();
			
			logger.error("registerWorkReportNoOrder Exception==="+e.toString());
		}
		return result;
	}	
	
	
	@Override
	@Transactional
	public int registerWorkReportByBottle(WorkReportVO param, List<BottleVO> bottleList) {
//		logger.debug(" registerWorkReport start ");
		int result = 0;
		boolean insertFlag = false;
		try {			
			
			logger.debug(" registerWorkReportByBottle getCreateId =" + param.getCreateId());			
			
			int workReportSeq = 0;
			int workSeq = 1;
			if(param.getUserId() != null && param.getUserId().length() > 0)
				param.setUserId(param.getUserId());
			else
				param.setUserId(param.getCreateId());
			
//			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back")) 
//					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
//					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback"))
//					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freechange")) 
//					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack")) 
//					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyBack")) 
//					) {
				workReportSeq = getWorkReportSeqForCustomerToday(param);
//			}
			//Work_Report_Seq 가져오기
			//int workReportSeq = getWorkReportSeqForCustomerToday(param);
			
			if(workReportSeq <= 0) {
				//workReportSeq = getWorkReportSeq();
				insertFlag = true;
				
				result = workMapper.insertWorkReport(param);
				workReportSeq = getWorkReportSeqForCustomerToday(param);
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}

			//TB_Work_Reprot 등록
			param.setWorkReportSeq(workReportSeq);
						
			//TB_Work_Bottle 등록				
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
						
			WorkBottleVO workBottle = null;
			BottleVO tempBottle = null;				
			
			String orderProductNm = "";
			String orderProductCapa = "";			
			
			for(int i = 0; i < bottleList.size() ; i++) {		
				
				tempBottle = bottleList.get(i);
				tempBottle.setCustomerId(param.getCustomerId());
				tempBottle.setUpdateId(param.getUserId());
				tempBottle.setBottleWorkCd(param.getBottleWorkCd());
				//workBottle = new WorkBottleVO();
				
				workBottle = makeWorkBottle(tempBottle);
				
				workBottle.setWorkReportSeq(param.getWorkReportSeq());
				workBottle.setWorkSeq(workSeq++);			
				workBottle.setBottleType(param.getBottleType());
				workBottle.setBottleWorkCd(param.getBottleWorkCd());
				workBottle.setAgencyYn(param.getAgencyYn());
				workBottle.setSearchDt(param.getSearchDt());
				
				//20240927 대여/판매 등 비고 추가
				workBottle.setWorkEtc(param.getReportEtc());
				
				if(i==0) {
					orderProductNm = tempBottle.getProductNm();
					orderProductCapa = tempBottle.getBottleCapa();
				}
				
				workBottleList.add(workBottle);							
			}			
			
			if( bottleList.size() > 1) {
				orderProductNm = orderProductNm + " 외 " + (bottleList.size()-1);
				orderProductCapa = orderProductCapa + " 외" + (bottleList.size()-1);
			}
			
			//TB_Order 등록
			//result = orderService.registerOrder(request, param)(orderInfo.getOrder());
			Calendar cal = Calendar.getInstance();
			param.setWorkDt(cal.getTime());
			param.setWorkCd(param.getBottleWorkCd());
			param.setWorkProductNm(orderProductNm);
			param.setWorkProductCapa(orderProductCapa);
//			logger.debug("WorkReportServiceImpl registerWorkReportByBottle bottleList.size() =" + bottleList.size() +"==workBottleList.size="+workBottleList.size());
			
			//Customer Bottle_Own_Count 감소
			//TODO
			// common.bottle.status.agencyBack / common.bottle.status.salesBack 처리 필요
			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
					|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback"))) {
				
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleOwnCount(bottleList.size()*-1);
				customer.setAgencyYn(param.getAgencyYn());
				
				result = changeCustomerProduct(workBottleList);	//Customer_Product 등록
				
//				result = customerService.modifyCustomerBottleCount(customer);
			}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyBack"))  ){
				
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleRentCount(bottleList.size()*-1);
				customer.setAgencyYn(param.getAgencyYn());
				
				result = changeCustomerProduct(workBottleList);	//Customer_Product 등록
				
//				result = customerService.modifyCustomerBottleRentCount(customer);
			}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack")) ){
				CustomerVO customer = new CustomerVO();
				customer.setCustomerId(param.getCustomerId());
				customer.setUpdateId(param.getCreateId());
				customer.setBottleOwnCount(bottleList.size()*1);
				customer.setAgencyYn(param.getAgencyYn());
				
				result = changeCustomerProduct(workBottleList);	//Customer_Product 등록				
//				result = customerService.modifyCustomerBottleCount(customer);
			}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freechange")) ){
				
				
			}
			
			if(insertFlag) {
//				result = workMapper.insertWorkReport(param);
				result = workMapper.updateWorkReportNoOrder(param);
			}
			result = workMapper.insertWorkBottles(workBottleList);				
			
		} catch (DataAccessException e) {			
			e.printStackTrace();
			logger.error("registerWorkReportByBottle Exception==="+e.toString());
			return result;
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("registerWorkReportByBottle Exception==="+e.toString());
			return result;
		}
		return result;
	}
	
	@Override
	public List<WorkBottleVO> getWorkBottleList(Integer workReportSeq) {
		
		return workMapper.selectWorkBottleList(workReportSeq);
	}
	

	@Override
	public List<WorkBottleVO> getWorkBottleListOfOrder(Integer orderId) {
		return workMapper.selectWorkBottleListOfOrder(orderId);	
	}
	
	
	@Override
	public List<WorkBottleRegisterVO> getWorkBottleListAndCountOfOrder(Integer orderId) {		
		return workMapper.selectWorkBottleListAndCountOfOrder(orderId);	
	}

	
	@Override
	public int registerWorkBottle(WorkBottleVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int registerWorkBottleList(List<WorkBottleVO> param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getWorkReportSeq() {
		return  workMapper.selectWorkReportSeq();
	}

	@Override
	public Map<String,Object> getWorkBottleListTotal(BottleVO param) {
							  
//		logger.debug("****** getWorkBottleListTotal *****start===*");	
				
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
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
		String searchChargeDt = param.getSearchChargeDt();	
		
		if(searchChargeDt != null && searchChargeDt.length() > 20) {
			map.put("searchChargeDt", searchChargeDt);
			
			searchChargeDtFrom = searchChargeDt.substring(0, 10) ;			
			map.put("searchChargeDtFrom", searchChargeDtFrom);
			
			searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;			
			map.put("searchChargeDtEnd", searchChargeDtEnd);
		}						
		
		int bottleCount = workMapper.selectWorBottleCountTotal(map);		
		
		//int lastPage = (int)(Math.ceil(bottleCount/ROW_PER_PAGE));
		int lastPage = (int)((double)bottleCount/ROW_PER_PAGE+0.95);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		if(lastPageNum ==0) lastPageNum=1;
		
		//수정 Start
		int pages = (bottleCount == 0) ? 1 : (int) ((bottleCount - 1) / ROW_PER_PAGE) + 1; // * 정수형이기때문에 소숫점은 표시안됨		
        //int blocks;
        int block;
        //blocks = (int) Math.ceil(1.0 * pages / ROW_PER_PAGE); // *소숫점 반올림
        block = (int) Math.ceil(1.0 * currentPage / ROW_PER_PAGE); // *소숫점 반올림
        startPageNum = (block - 1) * ROW_PER_PAGE + 1;
        lastPageNum = block * ROW_PER_PAGE;        
        
        if (lastPageNum > pages){
        	lastPageNum = pages;
        }
		//수정 end
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<BottleVO> bottleList = workMapper.selectWorBottleListTotal(map);
		
		resutlMap.put("list",  bottleList);				
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", startPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", bottleCount);		
		
		return  resutlMap;
	}

	@Override
	public List<BottleVO> getWorkBottleListToday(BottleVO param) {		
		List<BottleVO>  bottleList = workMapper.selectWorBottleListToday(param);
		
		return bottleList;
	}

	@Override
	public int registerWorkReport0310(WorkReportVO param) {
		logger.debug(" registerWorkReport0310 start ");
		int result = 0;		
		
		try {						
			
			//Bottle 정보 가져오기
			BottleVO bottle = new BottleVO();
			//TB_Work_Bottle 등록				
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
			
			List<String> list = null;
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
				bottle.setBottList(list);
				param.setBottList(list);
			}				
			
			List<BottleVO> bottleList = bottleService.getBottleDetails(bottle);			
			
			//Work_Report_Seq 가져오기
			if(bottleList.size() > 0) {
				param.setCustomerId(bottleList.get(0).getCustomerId());
			}
			boolean registerFlag = false;
			param.setUserId(param.getCreateId());
			int workReportSeq = getWorkReportSeqForCustomerToday(param);
			
			if(workReportSeq <= 0) {
				workReportSeq = getWorkReportSeq();
				registerFlag = true;
			}
			WorkBottleVO workBottle = null;
			BottleVO tempBottle = null;				
			
			String orderProductNm = "";
			String orderProductCapa = "";
			int workSeq = 1;
			for(int i = 0; i < bottleList.size() ; i++) {		
				
				tempBottle = bottleList.get(i);
				
				workBottle = new WorkBottleVO();
				
				if(i==0) {
					orderProductNm = tempBottle.getProductNm();
					orderProductCapa = tempBottle.getBottleCapa();
				}
				workBottle.setWorkReportSeq(workReportSeq);
				workBottle.setBottleId(tempBottle.getBottleId());
				workBottle.setBottleBarCd(tempBottle.getBottleBarCd());				
				workBottle.setCreateId(param.getCreateId());
				workBottle.setCustomerId(param.getCustomerId());
				workBottle.setBottleWorkCd(param.getBottleWorkCd());
				workBottle.setGasId(tempBottle.getGasId());
				workBottle.setProductId(tempBottle.getProductId());
				workBottle.setProductPriceSeq(tempBottle.getProductPriceSeq());	
				workBottle.setBottleType(param.getBottleType());
				workBottle.setWorkSeq(workSeq++);
				
				workBottleList.add(workBottle);							
			}			
			
			if( bottleList.size() > 1) {
				orderProductNm = orderProductNm + " 외 " + (bottleList.size()-1);
				orderProductCapa = orderProductCapa + " 외" + (bottleList.size()-1);
			}
			
			// bottle Bottle_Work_CD 업데이트
			bottle.setBottleIds(param.getBottlesIds());
			bottle.setBottleWorkCd(param.getBottleWorkCd());
			bottle.setUpdateId(param.getCreateId());
			bottle.setBottleType(param.getBottleType());
			bottle.setCustomerId(param.getCustomerId());
			
			result = bottleService.changeBottlesWorkCdOnly(bottle);
			
			//TB_Order 등록
			//result = orderService.registerOrder(request, param)(orderInfo.getOrder());
			param.setWorkReportSeq(workReportSeq);
			Calendar cal = Calendar.getInstance();
			param.setWorkDt(cal.getTime());
			param.setWorkCd(param.getBottleWorkCd());
			param.setWorkProductNm(orderProductNm);
			param.setWorkProductCapa(orderProductCapa);
			
			if(registerFlag)
				result = workMapper.insertWorkReport(param);			
			
			result = workMapper.insertWorkBottles(workBottleList);	
			
		} catch (DataAccessException e) {
			e.printStackTrace();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		
		return result;		
	}

	@Override
	public int getWorkReportSeqForCustomerToday(WorkReportVO param) {		
		return workMapper.selectWorkReportSeqForCustomerToday(param);
	}

	
	private List<BottleVO> getBottleList(WorkReportVO param){
		BottleVO bottle = new BottleVO();
		
		if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
			//bottleIds= request.getParameter("bottleIds");
			List<String> list = StringUtils.makeForeach(param.getBottlesIds(), ","); 		
			bottle.setBottList(list);
		}		
		
		return  bottleService.getBottleDetails(bottle);		
	}
	
//TODO	단품상품 판매 후 가스 판매시 order 상태변경 안됨

	@Override
	@Transactional
	public int registerWorkNoBottle(WorkBottleVO param) {
		
		boolean registerFlag = false;
		int result = 0;
		int workSeq=1;
		double receivableAmount =0;
		//WorkReportBottle 등록
		List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();
		
		if(param.getProductId() != null && param.getProductPriceSeq() != null) {
			
			BottleVO bottle = new BottleVO();
			bottle.setProductId(param.getProductId());
			bottle.setProductPriceSeq(param.getProductPriceSeq());
			bottle.setCustomerId(param.getCustomerId());
			
			ProductTotalVO productTotal = productService.getPrice(bottle);
			// 금일 작업 이력 확인 및 WorkReporSeq 가져오기
			WorkReportVO workReport = new WorkReportVO();
			workReport.setCustomerId(param.getCustomerId());
			workReport.setWorkCd(param.getBottleWorkCd());
			workReport.setCreateId(param.getCreateId());
			workReport.setSearchDt(param.getSearchDt());		
			workReport.setUpdateId(param.getCreateId());		
			if(param.getUserId() !=null && param.getUserId().length() > 0)
				workReport.setUserId(param.getUserId());
			else
				workReport.setUserId(param.getCreateId());
						
			//WorkReportSeq 가져오기
			int workReportSeq = 0;
			
			workReportSeq = getWorkReportSeqForCustomerToday(workReport);
			if(workReportSeq <= 0) {
				//workReportSeq = getWorkReportSeq();
				registerFlag = true;
				
				result = workMapper.insertWorkReport(workReport);
				workReportSeq = getWorkReportSeqForCustomerToday(workReport);
				workReport.setWorkReportSeq(workReportSeq);
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}
			//logger.debug("WorkReportServiceImpl registerWorkNoBottle  workReportSeq=" + workReportSeq +" workSeq=" + workSeq );
			
			//주문정보 확인
			//기존 주문이 있는지 여부 확인
			OrderVO orderTemp =  null;			
			//if(param.getAgencyYn().equals("N") || !param.getCreateId().equals("factory") ) orderTemp =  orderService.getLastOrderForCustomer(param.getCustomerId());
			orderTemp =  orderService.getLastOrderForCustomer(param.getCustomerId());
			
			List<OrderProductVO> orderProductList= null;		
			List<OrderProductVO> tempOrderProductList= null;	
			
			if(orderTemp !=null) { // 기존 주문 여부 확인
				param.setOrderId(orderTemp.getOrderId());
				
				orderTemp.setChOrderId(orderTemp.getOrderId());
				orderTemp.setUpdateId(param.getCreateId());
				//order 정보 가져와서 
				orderProductList = orderService.getOrderProductList(orderTemp.getOrderId());
				tempOrderProductList = orderProductList;
				
				//해당 주문 상품이 이미 처리되어 있는지 확인 WorkReport & WorkBottle 조회
				List<WorkBottleRegisterVO> registeredWorkBottleList = getWorkBottleListAndCountOfOrder(orderTemp.getOrderId());
				
				// 주문 상품수 새로 설정			
				Integer insertOrderProductSeq = 0;
				for(int i = 0 ; i < orderProductList.size();i++) {
					OrderProductVO tempOrderProduct = orderProductList.get(i);
					// Ln2 소분 미지정 주문처리
					if(param.getProductId().equals(tempOrderProduct.getProductId()) && tempOrderProduct.getProductId().equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId")) ) ){
						if(tempOrderProduct.getProductPriceSeq().equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.no.producSeq")) ) ) {
							
							tempOrderProduct.setProductPriceSeq(param.getProductPriceSeq());
							tempOrderProduct.setOrderCount(param.getProductCount());
							if(productTotal.getCustomerBottlePrice() > 0) tempOrderProduct.setOrderAmount(productTotal.getCustomerBottlePrice()*param.getProductCount());
							else tempOrderProduct.setOrderAmount(productTotal.getProductBottlePrice()*param.getProductCount());
							
							result = orderService.modifyOrderProduct(tempOrderProduct);
							
							//주문정보 변경 필요
							OrderVO updateOrder = new OrderVO();
							updateOrder.setOrderId(orderTemp.getOrderId());
							updateOrder.setUpdateId(param.getCreateId());
							updateOrder.setOrderTotalAmount(orderTemp.getOrderTotalAmount()+tempOrderProduct.getOrderAmount());
							result = orderService.modifyOrderAmount(updateOrder);
						}
					}
					
					if(tempOrderProduct.getOrderProductSeq() > insertOrderProductSeq) insertOrderProductSeq = tempOrderProduct.getOrderProductSeq();
							
					for(int j = 0 ; j < registeredWorkBottleList.size() ; j++) {
						WorkBottleRegisterVO tempRegisteredBottle = registeredWorkBottleList.get(j);
						
						if(tempOrderProduct.getProductId() == tempRegisteredBottle.getProductId() && tempOrderProduct.getProductPriceSeq() == tempRegisteredBottle.getProductPriceSeq()) {
							int leftCount  = tempOrderProduct.getOrderCount()-tempRegisteredBottle.getRegisteredCount();
							
							if(leftCount <= 0) leftCount = 0;
							
							// 주문 상품의 카운트 셋팅(0이면 이미 주문 처리된 것임)
							orderProductList.get(i).setOrderCount(leftCount);							
						}
					}					
				}
				
				// 해당 상품이 기존 주문에 있는지 확인
				boolean isNewProduct = true;
				
				for(int i = 0 ; i < orderProductList.size();i++) {
					OrderProductVO tempOrderProduct = orderProductList.get(i);											
					//
					// 기존 주문 상품과 비교
					if(tempOrderProduct.getProductId() == param.getProductId() && tempOrderProduct.getProductPriceSeq() == param.getProductPriceSeq()) {
						//기존 상품과 수량 비교(다르면 TB_order 업데이트, TB_order_product 업데이트), 같으면 주문상태 확인 후 업데이트 
						int addCount = param.getProductCount() - tempOrderProduct.getOrderCount();
						
						if(addCount > 0) {	// 기존 주문과 동일 상품의 추가가 있는 경우(TB_order 업데이트, TB_order_product 업데이트)
							orderProductList.get(i).setOrderCount(tempOrderProduct.getOrderCount()+addCount);
							
							if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
									&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") )
									&& param.getProductPrice()> 100) 
							{								
								orderProductList.get(i).setOrderAmount(tempOrderProduct.getOrderAmount()+param.getProductPrice() );								
							}else {
								if(productTotal.getCustomerProductPrice() > 0)
									orderProductList.get(i).setOrderAmount(param.getProductCount()* productTotal.getCustomerBottlePrice()  );
								else 
									orderProductList.get(i).setOrderAmount(param.getProductCount()* productTotal.getProductBottlePrice() );
							}
							orderProductList.get(i).setUpdateId(param.getCreateId());
							
							// 주문상품 업데이트
							result = orderService.modifyOrderProductCount(orderProductList.get(i));
							
							//주문정보 업데이트
							if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
									&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") )
									&& param.getProductPrice()> 100 ) 
							{								
								orderTemp.setOrderTotalAmount(orderTemp.getOrderTotalAmount()+(addCount*param.getProductPrice()) );								
							}else {
								if(productTotal.getCustomerBottlePrice() > 0)
									orderTemp.setOrderTotalAmount(orderTemp.getOrderTotalAmount()+(addCount*productTotal.getCustomerBottlePrice() ));
								else
									orderTemp.setOrderTotalAmount(orderTemp.getOrderTotalAmount()+(addCount*productTotal.getProductBottlePrice()));
							}
							result = orderService.modifyOrderAdditionBottles(orderTemp);
							orderProductList.get(i).setOrderCount(0);	
							
							if(orderTemp.getProductCount() > 0)
								receivableAmount += orderTemp.getOrderTotalAmount()/orderTemp.getProductCount();
							else
								receivableAmount += orderTemp.getOrderTotalAmount();
						}else if(addCount < 0) { // 기존 주문 상품을 전체 납품을 못할 경우
							
						}else if(addCount == 0) {	//해당 상품 납품 완료							
							orderProductList.get(i).setOrderCount(0);
						}
						orderProductList.get(i).setOrderCount(addCount);
						isNewProduct = false;
					}else {
						// 다를경우 판매상품 주문 등록(TB_Order 업데이트, TB_order_product 추가)										
					}
				}

				boolean orderCompleted = true;
				for(int i = 0 ; i < orderProductList.size();i++) {
					OrderProductVO tempOrderProduct = orderProductList.get(i);	
					
					if(tempOrderProduct.getOrderCount() > 0 ) orderCompleted = false;
				}
				
				if(isNewProduct) {	// 신규 상품 주문 추가
					
					OrderProductVO newOrderProduct = new OrderProductVO();
					newOrderProduct.setOrderId(orderTemp.getOrderId());
					newOrderProduct.setOrderProductSeq(++insertOrderProductSeq);
					newOrderProduct.setProductId(param.getProductId());
					newOrderProduct.setProductPriceSeq(param.getProductPriceSeq());
					newOrderProduct.setOrderCount(param.getProductCount());
					
					if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
							&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") ) 
							&& param.getProductPrice()> 100 ) 
					{
						newOrderProduct.setOrderAmount(param.getProductCount() * param.getProductPrice() );
					}else {
						if( productTotal.getCustomerBottlePrice() > 0)
							newOrderProduct.setOrderAmount(productTotal.getCustomerBottlePrice()*param.getProductCount());
						else 
							newOrderProduct.setOrderAmount(productTotal.getProductBottlePrice()*param.getProductCount());
					}
					newOrderProduct.setOrderProductEtc("추가상품");
					newOrderProduct.setCreateId(param.getCreateId());
					newOrderProduct.setUpdateId(param.getUpdateId());					
					newOrderProduct.setBottleSaleYn("N");
					newOrderProduct.setBottleChangeYn("N");
					newOrderProduct.setRetrievedYn("N");
					newOrderProduct.setAsYn("N");
					newOrderProduct.setBottleWorkCd(param.getBottleWorkCd());
					
					// 주문상품 추가
					result = orderService.registerOrderProduct(newOrderProduct);
					
					// 주문 정보 업데이트					
					orderTemp.setOrderTotalAmount(orderTemp.getOrderTotalAmount()+newOrderProduct.getOrderAmount());					
					orderTemp.setOrderProductNm(orderProductList.get(0).getProductNm()+" 외 "+orderProductList.size());
					orderTemp.setOrderProductCapa(orderProductList.get(0).getProductCapa()+" 외 "+orderProductList.size());					
					orderTemp.setUpdateId(param.getUpdateId());
					
					result = orderService.modifyOrderAdditionBottles(orderTemp);
					
					receivableAmount += newOrderProduct.getOrderAmount();
				}				

				if(!registerFlag) {	//WorkReport 업데이트
					workReport.setWorkProductNm(orderProductList.get(0).getProductNm()+" 외 "+(orderProductList.size()-1));
					workReport.setWorkProductCapa(orderProductList.get(0).getProductCapa()+" 외 "+(orderProductList.size()-1));
					workReport.setWorkReportSeq(workReportSeq);
					workReport.setOrderId(orderTemp.getOrderId());
					result = workMapper.modifyWorkReportProduct(workReport);
					
				}else {	// WorkReport 신규 등록
					workReport.setWorkReportSeq(workReportSeq);
					workReport.setOrderId(orderTemp.getOrderId());
					workReport.setWorkProductNm(productTotal.getProductNm());
					workReport.setWorkProductCapa(productTotal.getProductCapa());
					workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));			
					
					result = workMapper.updateWorkReportOrder(workReport);
					
				}
//				logger.debug("WorkReportServiceImpl registerWorkNoBottle  param.getProductCount()=" + param.getProductCount()  );
				if(StringUtils.isTankProduct(param.getProductId()) ) {
					WorkBottleVO addWorkBottle = new WorkBottleVO();
					
					addWorkBottle.setWorkReportSeq(workReportSeq);
					addWorkBottle.setCustomerId(param.getCustomerId());
					addWorkBottle.setWorkSeq(workSeq++);
					addWorkBottle.setBottleWorkCd(param.getBottleWorkCd());
					addWorkBottle.setProductId(param.getProductId());
					addWorkBottle.setProductPriceSeq(param.getProductPriceSeq());
					
					if(productTotal.getCustomerBottlePrice() > 0)
						addWorkBottle.setProductPrice(productTotal.getCustomerBottlePrice()); 
					else
						addWorkBottle.setProductPrice(productTotal.getProductBottlePrice());
					
					addWorkBottle.setProductCapa(productTotal.getProductCapa());
					addWorkBottle.setBottleSaleYn("N");
					addWorkBottle.setChargeVolumn(param.getProductCount());
					
					if(param.getUserId()!=null && param.getUserId().length() > 0) 
						addWorkBottle.setCreateId(param.getUserId());
					else
						addWorkBottle.setCreateId(param.getCreateId());
					addWorkBottle.setUpdateId(param.getCreateId());
					addWorkBottle.setSearchDt(param.getSearchDt());

					addWorkBottle.setWorkEtc(param.getWorkEtc());
					addWorkBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));
					addWorkBottle.setCreateId(param.getCreateId());
					
					result = workMapper.insertWorkBottle(addWorkBottle);
				}else {
					for(int i = 0 ; i < param.getProductCount() ; i++) {					
					
						WorkBottleVO addWorkBottle = new WorkBottleVO();
						
						addWorkBottle.setWorkReportSeq(workReportSeq);
						addWorkBottle.setCustomerId(param.getCustomerId());
						/*
						if(registeredWorkBottleList.size()>=1)
							addWorkBottle.setWorkSeq(registeredWorkBottleList.size()+1);					
						else 
						*/
						addWorkBottle.setWorkSeq(workSeq++);
						addWorkBottle.setBottleWorkCd(param.getBottleWorkCd());
						addWorkBottle.setProductId(param.getProductId());
						addWorkBottle.setProductPriceSeq(param.getProductPriceSeq());
						if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
								&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") )
								&& param.getProductPrice()> 100 ) {
						
							addWorkBottle.setProductPrice(param.getProductPrice()); 
						}else {
							if(productTotal.getCustomerProductPrice() > 0)
								addWorkBottle.setProductPrice(productTotal.getCustomerBottlePrice()); 
							else
								addWorkBottle.setProductPrice(productTotal.getProductBottlePrice());
							
						}
						
						addWorkBottle.setProductCapa(productTotal.getProductCapa());
						addWorkBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));
						addWorkBottle.setBottleSaleYn("N");
						addWorkBottle.setWorkEtc(param.getWorkEtc());
						
						addWorkBottle.setCreateId(param.getCreateId());
						addWorkBottle.setUpdateId(param.getCreateId());			
						addWorkBottle.setSearchDt(param.getSearchDt());
						
	//					logger.debug(" registerWorkNoBottle  getSearchDt=" + param.getSearchDt() );
	//					logger.debug(" registerWorkNoBottle addWorkBottle getSearchDt=" + addWorkBottle.getSearchDt() );
						workBottleList.add(addWorkBottle);
					}
					result = workMapper.insertWorkBottles(workBottleList);
				}
				//Order 정보 확인 후  업데이트 필요
/*			//20220519 수정
//				orderProductList = orderService.getOrderProductList(orderTemp.getOrderId());
//				registeredWorkBottleList = getWorkBottleListAndCountOfOrder(orderTemp.getOrderId());
//								
//				for(int i = 0 ; i < orderProductList.size();i++) {
//					OrderProductVO tempOrderProduct = orderProductList.get(i);	
//					
//					for(int j = 0 ; j < registeredWorkBottleList.size() ; j++) {
//						
//						WorkBottleRegisterVO tempRegisteredBottle = registeredWorkBottleList.get(j);
//						
//						if(tempOrderProduct.getProductId() == tempRegisteredBottle.getProductId() && tempOrderProduct.getProductPriceSeq() == tempRegisteredBottle.getProductPriceSeq()) {
//							int leftCount  = tempOrderProduct.getOrderCount()-tempRegisteredBottle.getRegisteredCount();
//							//leftOrderProduct +=leftCount;
//							if(leftCount < 0) leftCount = 0;
//							// 주문 상품의 카운트 셋팅(0이면 이미 주문 처리된 것임)
//							orderProductList.get(i).setOrderCount(leftCount);							
//						}
//					}					
//				}				
	*/
				double orderAmount = 0;
				boolean updateOrderAddFlag = false;
				// 처리안된 주문 상품 삭제 OrderCount  와  SaleCount 비교
				List<OrderProductVO> allOrderProductList = null;						
				allOrderProductList = orderService.getOrderProductListNew(orderTemp.getOrderId());
				
				List<OrderProductVO> updateOrderProducts = new ArrayList<OrderProductVO>();
				List<OrderProductVO> deleteOrderProducts = new ArrayList<OrderProductVO>();
				
				for(int j=0; j < allOrderProductList.size() ; j++) {
					OrderProductVO paramOrderProduct = allOrderProductList.get(j);

					if(paramOrderProduct.getSalesCount() == paramOrderProduct.getOrderCount()) deleteOrderProducts.add(paramOrderProduct);
					else if(paramOrderProduct.getSalesCount() > 0 && paramOrderProduct.getOrderCount() > paramOrderProduct.getSalesCount()) {
						paramOrderProduct.setOrderAmount(paramOrderProduct.getOrderAmount() / paramOrderProduct.getOrderCount());
						paramOrderProduct.setOrderCount(paramOrderProduct.getOrderCount() - paramOrderProduct.getSalesCount());
						paramOrderProduct.setUpdateId(param.getCreateId());
						updateOrderProducts.add(paramOrderProduct);
					}
				}

				if(updateOrderProducts.size() > 0 || deleteOrderProducts.size() > 0) updateOrderAddFlag = true;
				if(updateOrderProducts.size() > 0 ) result = orderService.modifyOrderProducts(updateOrderProducts);
				if(deleteOrderProducts.size() > 0 ) result = orderService.deleteOrderProducts(deleteOrderProducts);
				
				// Order 상태 정보 변경
				List<OrderProductVO> orderProduct = orderService.getOrderProductList(orderTemp.getOrderId());
//				logger.debug("!!!!!!!!!!***orderProduct.size ="+orderProduct.size());
				int remainCount = 0;
				for(int j=0; j < orderProduct.size() ; j++) {
					remainCount += orderProduct.get(j).getSalesCount();
					orderAmount += orderProduct.get(j).getOrderAmount();
					orderTemp.setOrderProductNm(orderProduct.get(j).getProductNm());
					orderTemp.setOrderProductCapa(orderProduct.get(0).getProductCapa());
				}
			
				if(orderProduct.size() > 1) {
					orderTemp.setOrderProductNm(orderTemp.getOrderProductNm()+" 외 "+(allOrderProductList.size()-1));
					orderTemp.setOrderProductCapa(orderTemp.getOrderProductCapa()+" 외 "+(allOrderProductList.size()-1));
				}
				if(updateOrderAddFlag && orderAmount > 0) {
					orderTemp.setOrderTotalAmount(orderAmount);	
					orderTemp.setUpdateId(param.getUserId());
					
//					result = orderService.modifyOrderAdditionBottles(orderTemp);		
				}
				
				if(remainCount == 0) orderCompleted = true;			
				
				if(orderTemp.getOrderProcessCd().equals(PropertyFactory.getProperty("common.code.order.process.receive")) && orderCompleted) {
					orderTemp.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
					orderTemp.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));	
					orderTemp.setSalesId(param.getCreateId());
					orderTemp.setChOrderId(orderTemp.getOrderId());
					orderTemp.setUpdateId(param.getUserId());
					result = orderService.modifyOrderInfo(orderTemp);
				}else if(updateOrderAddFlag && orderAmount > 0) {
					
					result = orderService.modifyOrderAdditionBottles(orderTemp);
				}

			}else {
				// order 정보 등록 (Tb_Order, Tb_Order_Product)
				orderTemp = new OrderVO();
								
				orderTemp.setMemberCompSeq(Integer.parseInt(PropertyFactory.getProperty("common.Member.Comp.Daehan")) );
				orderTemp.setCustomerId(param.getCustomerId());
				orderTemp.setOrderTypeCd(PropertyFactory.getProperty("common.code.order.type.order"));	//상품주문
				orderTemp.setProductCount(param.getProductCount());
				
				Calendar cal = Calendar.getInstance();
				int amPm = cal.get(Calendar.AM_PM);
				orderTemp.setDeliveryReqDt(cal.getTime());
				
				if (amPm == 1 ) orderTemp.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.PM"));
				else  orderTemp.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.AM"));		
				
				orderTemp.setOrderEtc("현장주문");
				orderTemp.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));
				if(param.getUserId()!=null && param.getUserId().length() > 0) {
					orderTemp.setSalesId(param.getUserId());
					orderTemp.setCreateId(param.getUserId());
					orderTemp.setUpdateId(param.getUserId());
				}else {
					orderTemp.setSalesId(param.getCreateId());
					orderTemp.setCreateId(param.getCreateId());
					orderTemp.setUpdateId(param.getUserId());
				}
				orderTemp.setOrderProductNm(productTotal.getProductNm());
				orderTemp.setOrderProductCapa(productTotal.getProductCapa());
				orderTemp.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				
				if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
						&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") ) 
						&& param.getProductPrice()> 100 ) 
				{
					orderTemp.setOrderTotalAmount(param.getProductPrice());					
				}else {
					if(productTotal.getCustomerBottlePrice()> 0 )
						orderTemp.setOrderTotalAmount(param.getProductCount()*productTotal.getCustomerBottlePrice());
					else
						orderTemp.setOrderTotalAmount(param.getProductCount()*productTotal.getProductBottlePrice());
				}
				
				//order정보 등록
				result = orderService.registerOrder(orderTemp);
				
				int orderId = orderService.getNewOrderId(orderTemp);
				orderTemp.setOrderId(Integer.valueOf(orderId));
				
				receivableAmount += orderTemp.getOrderTotalAmount();
				
				OrderProductVO orderProduct = new OrderProductVO();
				
				orderProduct.setOrderId(orderTemp.getOrderId());
				orderProduct.setOrderProductSeq(1);
				orderProduct.setProductId(param.getProductId());
				orderProduct.setProductPriceSeq(param.getProductPriceSeq());
				orderProduct.setProductCapa(productTotal.getProductCapa());
				orderProduct.setOrderCount(param.getProductCount());
				if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
						&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") ) 
						&& param.getProductPrice()> 100 ) 
				{
					orderProduct.setOrderAmount(param.getProductPrice() );
				}else {
					if(productTotal.getCustomerBottlePrice() > 0)
						orderProduct.setOrderAmount(param.getProductCount()* productTotal.getCustomerBottlePrice() );
					else 
						orderProduct.setOrderAmount(param.getProductCount()* productTotal.getProductBottlePrice() );
				}
				//orderProduct.setOrderAmount(param.getProductCount()*productTotal.getProductPrice());
				orderProduct.setOrderProductEtc("현장추가");
				orderProduct.setCreateId(param.getCreateId());
				orderProduct.setUpdateId(param.getCreateId());
				orderProduct.setBottleChangeYn("N");
				orderProduct.setBottleSaleYn("N");
				orderProduct.setRetrievedYn("N");
				orderProduct.setAsYn("N");
				orderProduct.setBottleWorkCd(param.getBottleWorkCd());
				
				//order product 등록
				result = orderService.registerOrderProduct(orderProduct);
				result = orderService.modifyOrderInfo(orderTemp);
				
				//WorkReport 등록 및 업데이트				
				workReport.setWorkReportSeq(workReportSeq);
				workReport.setOrderId(orderTemp.getOrderId());
				workReport.setOrderProductNm(productTotal.getProductNm());
				workReport.setOrderProductCapa(productTotal.getProductCapa());
				workReport.setWorkCd(PropertyFactory.getProperty("common.bottle.status.sale"));				
				
				//WorkReport 등록
				if(registerFlag)
					result = workMapper.updateWorkReportOrder(workReport);
				
				if(StringUtils.isTankProduct(param.getProductId()) ) {
					WorkBottleVO addWorkBottle = new WorkBottleVO();
					
					addWorkBottle.setWorkReportSeq(workReportSeq);
					addWorkBottle.setCustomerId(param.getCustomerId());
					addWorkBottle.setWorkSeq(workSeq++);
					addWorkBottle.setBottleWorkCd(param.getBottleWorkCd());
					addWorkBottle.setProductId(param.getProductId());
					addWorkBottle.setProductPriceSeq(param.getProductPriceSeq());
					
					if(productTotal.getCustomerBottlePrice() > 0)
						addWorkBottle.setProductPrice(productTotal.getCustomerBottlePrice()); 
					else
						addWorkBottle.setProductPrice(productTotal.getProductBottlePrice());
					
					addWorkBottle.setProductCapa(productTotal.getProductCapa());
					addWorkBottle.setBottleSaleYn("N");
					addWorkBottle.setChargeVolumn(param.getProductCount());
					addWorkBottle.setWorkEtc(param.getWorkEtc());
					
					if(param.getUserId()!=null && param.getUserId().length() > 0) 
						addWorkBottle.setCreateId(param.getUserId());
					else
						addWorkBottle.setCreateId(param.getCreateId());
					addWorkBottle.setUpdateId(param.getCreateId());
					addWorkBottle.setSearchDt(param.getSearchDt());
					
					result = workMapper.insertWorkBottle(addWorkBottle);
				}else {
				
					for(int i = 0 ; i < param.getProductCount() ; i++) {		
						
						WorkBottleVO addWorkBottle = new WorkBottleVO();
						
						addWorkBottle.setWorkReportSeq(workReportSeq);
						addWorkBottle.setCustomerId(param.getCustomerId());
						addWorkBottle.setWorkSeq(workSeq++);
						addWorkBottle.setBottleWorkCd(param.getBottleWorkCd());
						addWorkBottle.setProductId(param.getProductId());
						addWorkBottle.setProductPriceSeq(param.getProductPriceSeq());
						
						if(param.getProductId()==Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.productId"))
								&& param.getProductPriceSeq() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.bottle.productPriceSeq") ) 
								&& param.getProductPrice()> 100 ) 
						{
							addWorkBottle.setProductPrice(param.getProductPrice()); 
	//						logger.debug("WorkReportServiceImpl registerWorkNoBottle  param.getProductPrice()=" + param.getProductPrice() );
						}else {
							
							if(productTotal.getCustomerBottlePrice() > 0)
								addWorkBottle.setProductPrice(productTotal.getCustomerBottlePrice()); 
							else
								addWorkBottle.setProductPrice(productTotal.getProductBottlePrice());
	//						logger.debug("WorkReportServiceImpl registerWorkNoBottle  addWorkBottle.getProductPrice=" + addWorkBottle.getProductPrice() );
						}
						addWorkBottle.setProductCapa(productTotal.getProductCapa());
						addWorkBottle.setBottleSaleYn("N");
						addWorkBottle.setWorkEtc(param.getWorkEtc());
						
						if(param.getUserId()!=null && param.getUserId().length() > 0) 
							addWorkBottle.setCreateId(param.getUserId());
						else
							addWorkBottle.setCreateId(param.getCreateId());
						addWorkBottle.setUpdateId(param.getCreateId());
						addWorkBottle.setSearchDt(param.getSearchDt());
						workBottleList.add(addWorkBottle);
					}
	//				logger.debug("--registerWorkNoBottle  workBottleList.size=" + workBottleList.size() );
					result = workMapper.insertWorkBottles(workBottleList);			
				}
			}			
		}
		
		//Customer_Ln2 변경
		if(param.getProductId().equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId")) )) {
			//Customer_Ln2 변경
//			logger.debug("--registerWorkNoBottle  workBottleList.size=product.LN2.divide.new.productId" );
			CustomerLn2AlarmVO ln2Alarm = new CustomerLn2AlarmVO();
			ln2Alarm.setCustomerId(param.getCustomerId());
			
			result = customerService.modifyCustomerLn2WorkDt(ln2Alarm);	
		}		
				
		
		double cashTotal = 0;
		for(int i=0; i < workBottleList.size() ; i++) {
			cashTotal+=workBottleList.get(i).getProductPrice();
		}
		CashFlowVO cashFlow = new CashFlowVO();
		cashFlow.setCustomerId(param.getCustomerId());
		cashFlow.setReceivableAmount(cashTotal);
		if(param.getUserId()!=null && param.getUserId().length() > 0) 
			cashFlow.setCreateId(param.getUserId());
		else
			cashFlow.setCreateId(param.getCreateId());
		cashFlow.setSearchCreateDt(param.getSearchDt());
		result = cashService.registerCashFlow(cashFlow);
		
		return result;
	}


	@Override
	public int modifyWorkReportReceivedAmount(WorkReportVO param) {		
		int workSeq=1;
		int result = 0 ;

		workSeq = workMapper.selectWorkBottleSeq(param.getWorkReportSeq());
		
		WorkBottleVO workBottle = new WorkBottleVO();
		workBottle.setWorkReportSeq(param.getWorkReportSeq());
		workBottle.setWorkSeq(workSeq);
		workBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0312"));		
		workBottle.setCustomerId(param.getCustomerId());				
		workBottle.setCreateId(param.getUserId());			
		
		result =  workMapper.updateWorkReportReceivedAmount(param);
		if(result > 0)
			result = workMapper.insertWorkBottle(workBottle);
		
		
		return result;
	}

	// 입금용 WorkReport 등록
	@Override
	public int registerWorkReportOnly(WorkReportVO param) {		
		
		int workSeq=1;
		int result = 0 ;

		WorkBottleVO workBottle = new WorkBottleVO();
		workBottle.setWorkReportSeq(param.getWorkReportSeq());
		workBottle.setWorkSeq(workSeq);
		workBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0312"));		
		workBottle.setCustomerId(param.getCustomerId());				
		workBottle.setCreateId(param.getCreateId());
		if(param.getSearchDt() != null && param.getSearchDt().length() >0) {
			workBottle.setSearchDt(param.getSearchDt());
		}
		
		result =  workMapper.insertWorkReport(param);
		if(result > 0)
			result = workMapper.insertWorkBottle(workBottle);
		
		return result ; 
	}


	@Override
	public int getWorkReportSeqForCustomer(WorkReportVO param) {
		return workMapper.selectWorkReportSeqForCustomer(param);
	}
	

	@Override
	public int modifyWorkBottlePrice(WorkBottleVO param) {		
		return workMapper.updateWorkBottlePrice(param);
	}

	@Override
	public int modifyWorkBottleManual(HttpServletRequest request, WorkReportVO param) {
		
		int result = 0;
		try {
			int productCount = 0;
			CustomerVO customer = customerService.getCustomerDetails(param.getCustomerId());
					
			if(request.getParameter("productCount") !=null) productCount = Integer.parseInt(request.getParameter("productCount"));
			
			List<WorkBottleVO> beforeWorkBottleList = workMapper.selectWorkBottleList(param.getWorkReportSeq());
			
			List<OrderProductVO> orderProductList = orderService.getOrderProductList(param.getOrderId());
			
			List<WorkBottleVO> afterWorkBottleList = new ArrayList<WorkBottleVO>();			
			//주문상품/주문상품용기 업데이트
			List<OrderProductVO> newOrderProductList = new ArrayList<OrderProductVO>();
			List<OrderBottleVO> newOrderBottletList = new ArrayList<OrderBottleVO>();			
			
			int newOrderProrderSeq = orderProductList.size()+1;
			
			if(param.getOrderId() == null)
				param.setOrderId(orderService.getOrderId());
			
			for(int i=0 ; i < productCount ; i++ ) {
				WorkBottleVO workBottle = new WorkBottleVO();
				workBottle.setCreateId(param.getUserId());
				workBottle.setUpdateId(param.getUpdateId());
				workBottle.setCustomerId(param.getCustomerId());
				workBottle.setOrderId(param.getOrderId());
				workBottle.setSearchDt(param.getSearchDt());
				workBottle.setAgencyYn(customer.getAgencyYn());
				workBottle.setManualYn("Y");
				boolean rightYn = true;
//				logger.debug("modifyWorkBottleManual workBottle.setSearchDt==" + workBottle.getSearchDt() ); 
				if(request.getParameter("bottleWorkCd_"+i) !=null) {
					String strBottleWorkCd = request.getParameter("bottleWorkCd_"+i);
					
					workBottle.setBottleWorkCd(strBottleWorkCd); 
					if(PropertyFactory.getProperty("common.bottle.status.rent").equals(strBottleWorkCd) || PropertyFactory.getProperty("common.bottle.status.agencyRent").equals(strBottleWorkCd)
							 || PropertyFactory.getProperty("common.bottle.status.salesgas").equals(strBottleWorkCd) ) workBottle.setBottleSaleYn("N");
					else if(PropertyFactory.getProperty("common.bottle.status.sale").equals(strBottleWorkCd)) workBottle.setBottleSaleYn("Y");
					
					//20211125 Charge_Volumn 추가
				}
				else rightYn = false;
				
				if(request.getParameter("productId_"+i) !=null) workBottle.setProductId(Integer.parseInt(request.getParameter("productId_"+i))); 
				else rightYn = false;
				
				if(request.getParameter("productPriceSeq_"+i) !=null) workBottle.setProductPriceSeq(Integer.parseInt(request.getParameter("productPriceSeq_"+i)));
				else rightYn = false;
				
				if(request.getParameter("bottleType_"+i) !=null && request.getParameter("bottleType_"+i).length() > 0) workBottle.setBottleType(request.getParameter("bottleType_"+i));
				//else rightYn = false;
				
				if(request.getParameter("productCount_"+i) !=null) {
					workBottle.setProductCount(Integer.parseInt(request.getParameter("productCount_"+i)));
					
//					if(PropertyFactory.getProperty("common.bottle.status.tcharge").equals(request.getParameter("bottleWorkCd_"+i))) {
//						workBottle.setChargeVolumn(Integer.parseInt(request.getParameter("productCount_"+i)));
//					}
					if(StringUtils.isTankProduct(workBottle.getProductId() ) ) {
						//workBottle.setProductCount(1);
						workBottle.setChargeVolumn(Integer.parseInt(request.getParameter("productCount_"+i)));
					}
				}
				else rightYn = false;
				
				if(request.getParameter("workEtc_"+i) !=null) workBottle.setWorkEtc(request.getParameter("workEtc_"+i)); 
//				else rightYn = false;
				
				workBottle.setNewYn("Y");
				workBottle.setNewProductYn("Y");
				logger.debug("modifyWorkBottleManual workBottle.getWorkEtc==" + workBottle.getWorkEtc() );
				if(rightYn) afterWorkBottleList.add(workBottle);
			}
//			logger.debug("modifyWorkBottleManual afterWorkBottleList.size=" + afterWorkBottleList.size() ); 
			//이전 WorkBottle 과 비교
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("bottleList", afterWorkBottleList);
			map.put("customerId", param.getCustomerId());
			
			List<ProductTotalVO> productTotalList =  productService.getPriceList(map);	
			
			Integer customerId = 0;
			int orderProductSeq=1;
			for(int j=0; j< afterWorkBottleList.size() ; j++) {
				
				WorkBottleVO afterWorkBottle = afterWorkBottleList.get(j);		
				afterWorkBottleList.get(j).setWorkReportSeq(param.getWorkReportSeq());				
				afterWorkBottle.setAgencyYn(customer.getAgencyYn());
				for(int i =0 ; i< beforeWorkBottleList.size() ; i++) {
					WorkBottleVO beforeWorkBottle = beforeWorkBottleList.get(i);		
					customerId = beforeWorkBottle.getCustomerId();
					beforeWorkBottle.setCreateId(param.getCreateId());;
					beforeWorkBottle.setWorkReportSeq(param.getWorkReportSeq());
					beforeWorkBottle.setSearchDt(param.getSearchDt());
					beforeWorkBottle.setAgencyYn(customer.getAgencyYn());

					if(beforeWorkBottle.getBottleWorkCd().equals(afterWorkBottle.getBottleWorkCd() )
							&& beforeWorkBottle.getProductId() == afterWorkBottle.getProductId() 
							&& beforeWorkBottle.getProductPriceSeq() == afterWorkBottle.getProductPriceSeq()){		
					
						beforeWorkBottle.setWorkEtc(afterWorkBottle.getWorkEtc());
						double fPrice = 0;
						if(beforeWorkBottle.getProductCount() > 0) fPrice = beforeWorkBottle.getProductPrice()/beforeWorkBottle.getProductCount();
						else fPrice = beforeWorkBottle.getProductPrice();
//						logger.debug("modifyWorkBottleManual fPrice==" + fPrice ); 
						beforeWorkBottle.setProductPrice(fPrice);
						int remainCount = afterWorkBottle.getProductCount() - beforeWorkBottle.getProductCount();
						
						if(StringUtils.isTankProduct(beforeWorkBottle.getProductId() ) ) {
							remainCount = afterWorkBottle.getChargeVolumn()- beforeWorkBottle.getChargeVolumn();
							beforeWorkBottle.setChargeVolumn( afterWorkBottle.getChargeVolumn());
							beforeWorkBottle.setUpdateId(param.getUpdateId());
						}
						
						afterWorkBottle.setGasId(beforeWorkBottle.getGasId());
						//afterWorkBottle.setProductCount(remainCount);
						afterWorkBottle.setNewYn("N");
						if(StringUtils.isTankProduct(beforeWorkBottle.getProductId() ) ) {
							afterWorkBottle.setProductPrice(beforeWorkBottle.getProductPrice());
						}else {
							if(beforeWorkBottle.getProductPrice() > 0 && beforeWorkBottle.getProductCount() > 0)
								afterWorkBottle.setProductPrice(beforeWorkBottle.getProductPrice());
							else
								afterWorkBottle.setProductPrice(0.0);
						}
//						logger.debug("modifyWorkBottleManual beforeWorkBottle.getBottleType()=" + beforeWorkBottle.getBottleType() ); 
						afterWorkBottle.setBottleType(beforeWorkBottle.getBottleType());
						afterWorkBottle.setProductPrice(beforeWorkBottle.getProductPrice());
						afterWorkBottle.setBottleSaleYn(beforeWorkBottle.getBottleSaleYn());
						afterWorkBottle.setCustomerId(customerId);
						afterWorkBottle.setWorkEtc(beforeWorkBottle.getWorkEtc());
						afterWorkBottle.setCreateDt(beforeWorkBottle.getCreateDt());
						afterWorkBottle.setSearchDt(param.getSearchDt());
						
						beforeWorkBottle.setProductCount(remainCount);	
						beforeWorkBottle.setWorkEtc(afterWorkBottle.getWorkEtc());
//						logger.debug("modifyWorkBottleManual remainCount==" + remainCount ); 
//						logger.debug("WorkReportServiceImpl --modifyWorkBottleManual  beforeWorkBottle.getMultiYn()=" + beforeWorkBottle.getMultiYn() );
						if(beforeWorkBottle.getMultiYn().equals("Y")) afterWorkBottle.setMultiYn("Y");
						// 남은처리 workBottle 처리 
						if(remainCount > 0) { // 추가			
							beforeWorkBottle.setManualYn("Y");
							if(beforeWorkBottle.getGasId() > 0 ) {								
								
								//beforeWorkBottle.setProductPrice(beforeWorkBottle.getProductPrice()/beforeWorkBottle.getProductCount());								
								result = addWorkBottle(beforeWorkBottle);	
								
								if(!afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) && !afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out")) )
									result = modifyCustomerProduct(beforeWorkBottle);
							}else {
								if(PropertyFactory.getProperty("common.bottle.status.tcharge").equals(request.getParameter("bottleWorkCd_"+i))) {
									beforeWorkBottle.setChargeVolumn(afterWorkBottle.getChargeVolumn());
								}
								result = addWorkBottleNoGas(beforeWorkBottle);	
							}
						}else if(remainCount < 0 ){
							if(!afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) && !afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out")) )
								result = modifyCustomerProduct(beforeWorkBottle);
							
							result = minusWorkBottle(beforeWorkBottle);		
						}
						// orderProduct처리 -orderBottle도 처리
					}					
				}
				//if(afterWorkBottle.getProductCount() > 0 && !afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) && !afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out")) ) {
				if(afterWorkBottle.getProductCount() > 0 ) {

					ProductTotalVO productTotal = null;
					OrderProductVO orderProduct = new OrderProductVO();
					orderProduct.setOrderId(param.getOrderId());
					orderProduct.setOrderProductSeq(orderProductSeq++);
					orderProduct.setCreateId(param.getCreateId());
					orderProduct.setProductId(afterWorkBottle.getProductId() );
					orderProduct.setProductPriceSeq(afterWorkBottle.getProductPriceSeq());
					orderProduct.setOrderCount(afterWorkBottle.getProductCount());
					if(StringUtils.isTankProduct(afterWorkBottle.getProductId() ) ) {
						orderProduct.setOrderCount(afterWorkBottle.getChargeVolumn()  );
					}
					if(afterWorkBottle.getProductPrice() > 0)
						orderProduct.setOrderAmount(afterWorkBottle.getProductCount()*afterWorkBottle.getProductPrice());
					orderProduct.setRetrievedYn("N");
					orderProduct.setAsYn("N");	
					orderProduct.setBottleWorkCd(afterWorkBottle.getBottleWorkCd());
					
					BottleVO bottle = new BottleVO();
					bottle.setCustomerId(customerId);
					bottle.setProductId(afterWorkBottle.getProductId());
					bottle.setProductPriceSeq(afterWorkBottle.getProductPriceSeq());	
					
					productTotal = getProductTotal(productTotalList,bottle);//2021-06-19
//					productTotal = productService.getPrice(bottle);
					
					if(afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))
							|| afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent"))
							|| afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas")) ) {
						orderProduct.setBottleChangeYn("Y");
						orderProduct.setBottleSaleYn("N");	
						if(productTotal !=null && productTotal.getCustomerProductPrice() > 0) orderProduct.setOrderAmount(afterWorkBottle.getProductCount()*productTotal.getCustomerProductPrice());
						else orderProduct.setOrderAmount(afterWorkBottle.getProductCount()*productTotal.getProductPrice());
						newOrderProductList.add(orderProduct);
					}else if(afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {
						orderProduct.setBottleChangeYn("N");
						orderProduct.setBottleSaleYn("Y");	
						if(productTotal !=null && productTotal.getCustomerBottlePrice() > 0) orderProduct.setOrderAmount(afterWorkBottle.getProductCount()*productTotal.getCustomerBottlePrice());
						else orderProduct.setOrderAmount(afterWorkBottle.getProductCount()*productTotal.getProductBottlePrice());
						newOrderProductList.add(orderProduct);

					}else if(afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) || afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))) {
						orderProduct.setBottleChangeYn("N");
						orderProduct.setBottleSaleYn("N");
						orderProduct.setIncomeYn("N");
						orderProduct.setOutYn("N");
						if(afterWorkBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come"))) orderProduct.setIncomeYn("Y");
						else orderProduct.setOutYn("Y");
						orderProduct.setOrderAmount(0);
						newOrderProductList.add(orderProduct);
					}					
				}
			}			
			result = addNewWorkBottle(getNewWorkBottle(afterWorkBottleList,1));			
			
			//20210220 수정
			//result = addNewOrderProduct(getNewWorkBottle(afterWorkBottleList,-1));		
			if(newOrderProductList.size() >0) {
				result = orderService.deleteOrderProducts(newOrderProductList.get(0));
				result = orderService.registerOrderProducts(newOrderProductList);	
				//주문상품용기는 변경하지 않음
			}
			//order 처리
			if(orderProductList.size() > 0)
				result = modifyOrderInfo(param);
			
		} catch (DataAccessException de) {		
			de.printStackTrace();
			logger.error("WorkReportServiceImpl modifyWorkBottleManual DataAccessException ", de.toString());
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("WorkReportServiceImpl modifyWorkBottleManual Exception ", e.toString());
		}
		return result;
	}
	
	private int addWorkBottle(WorkBottleVO param) {
		int result= 1;
//		logger.debug("WorkReportServiceImpl --addWorkBottle  param.getSearchDt=" + param.getSearchDt() );
		//logger.debug("WorkReportServiceImpl --addWorkBottle  param.getProductCount=" + param.getProductCount() );
		BottleVO bottle = new BottleVO();
		bottle.setProductId(param.getProductId());
		bottle.setProductPriceSeq(param.getProductPriceSeq());
		bottle.setGasId(param.getGasId());
		
		bottle = bottleService.getDummyBottle(bottle);
		
		List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();		
		int workSeq = workMapper.selectWorkBottleSeq(param.getWorkReportSeq());
		
		for(int i=0; i < param.getProductCount() ; i++) {
			WorkBottleVO workBottle = new WorkBottleVO();
			//logger.debug("workSeq="+workSeq);
//			logger.debug("WorkReportServiceImpl --addWorkBottle param.getWorkReportSeq()=" + param.getWorkReportSeq() );
//			logger.debug("WorkReportServiceImpl --addWorkBottle workSeq=" + workSeq );
//			logger.debug("WorkReportServiceImpl --addWorkBottle bottle.getBottleId()=" + bottle.getBottleId());
//			logger.debug("WorkReportServiceImpl --addWorkBottle bottle.getBottleBarCd()=" + bottle.getBottleBarCd());
//			logger.debug("WorkReportServiceImpl --addWorkBottle param.getCustomerId()=" + param.getCustomerId());
//			logger.debug("WorkReportServiceImpl --addWorkBottle param.getBottleWorkCd()=" + param.getBottleWorkCd());
//			logger.debug("WorkReportServiceImpl --addWorkBottle param.getGasId()=" + param.getGasId());
//			logger.debug("WorkReportServiceImpl --addWorkBottle param.getGasCd()=" + param.getGasCd());
//			logger.debug("WorkReportServiceImpl --addWorkBottle param.getProductId()=" + param.getProductId());
//			logger.debug("WorkReportServiceImpl --addWorkBottle param.getProductPriceSeq()=" + param.getProductPriceSeq());
			logger.debug("WorkReportServiceImpl --addWorkBottle param.getWorkEtc()=" + param.getWorkEtc());

			
			workBottle.setWorkReportSeq(param.getWorkReportSeq());
			workBottle.setWorkSeq(workSeq++);
			workBottle.setBottleId(bottle.getBottleId());
			workBottle.setBottleBarCd(bottle.getBottleBarCd());
			workBottle.setCustomerId(param.getCustomerId());
			workBottle.setBottleWorkCd(param.getBottleWorkCd());
			workBottle.setGasId(param.getGasId());
			workBottle.setGasCd(param.getGasCd());
			workBottle.setProductId(param.getProductId());
			workBottle.setProductPriceSeq(param.getProductPriceSeq());
			workBottle.setProductPrice(param.getProductPrice());
			workBottle.setBottleType(param.getBottleType());
			workBottle.setCreateId(param.getCreateId());
			workBottle.setBottleSaleYn(param.getBottleSaleYn());
			workBottle.setManualYn(param.getManualYn());
			workBottle.setSearchDt(param.getSearchDt());
			workBottle.setWorkEtc(param.getWorkEtc());
			workBottle.setMultiYn(param.getMultiYn());
			workBottleList.add(workBottle);
		}
		if(workBottleList.size() > 0)
			result = workMapper.insertWorkBottles(workBottleList);
		
		return result;
	}
	
	private int addWorkBottleNoGas(WorkBottleVO param) {
		int result= 1;
		logger.debug("WorkReportServiceImpl addWorkBottleNoGas --param.getSearchDt="+param.getSearchDt() );
		List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();		
		int workSeq = 0;
		
		if(StringUtils.isTankProduct(param.getProductId() ) ) {
//		if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.tcharge"))) {
//			workSeq = workMapper.selectWorkBottleSeqTcharge(param.getWorkReportSeq());
//			param.setWorkSeq(workSeq);

			result = workMapper.modifyWorkBottleCharge(param);
		}else {
			workSeq = workMapper.selectWorkBottleSeq(param.getWorkReportSeq());
		
			for(int i=0; i < param.getProductCount() ; i++) {
//				WorkBottleVO workBottle = param;
				
				WorkBottleVO workBottle = new WorkBottleVO();
				
				workBottle.setWorkReportSeq(param.getWorkReportSeq());
				workBottle.setWorkSeq(workSeq++);
//				workBottle.setBottleId(bottle.getBottleId());
//				workBottle.setBottleBarCd(bottle.getBottleBarCd());
				workBottle.setCustomerId(param.getCustomerId());
				workBottle.setBottleWorkCd(param.getBottleWorkCd());
				workBottle.setGasId(param.getGasId());
				workBottle.setGasCd(param.getGasCd());
				workBottle.setProductId(param.getProductId());
				workBottle.setProductPriceSeq(param.getProductPriceSeq());
//				workBottle.setProductPrice(param.getProductPrice());
				workBottle.setBottleType(param.getBottleType());
				workBottle.setCreateId(param.getCreateId());
				workBottle.setBottleSaleYn(param.getBottleSaleYn());
				workBottle.setManualYn(param.getManualYn());
				workBottle.setSearchDt(param.getSearchDt());
				workBottle.setMultiYn(param.getMultiYn());
							
//				workBottle.setWorkSeq(workSeq++);		
				
				if(param.getProductPrice() > 0)
					workBottle.setProductPrice(param.getProductPrice()/param.getProductCount());
				else
					workBottle.setProductPrice(param.getProductPrice());
				
				workBottleList.add(workBottle);
			}
			if(workBottleList.size() > 0)
				result = workMapper.insertWorkBottles(workBottleList);
		}
		
		return result;
	}
	
	private int minusWorkBottle(WorkBottleVO param) {
		int result= 1;
//		logger.debug("WorkReportServiceImpl minusWorkBottle -"+param.getWorkSeq() );
		param.setProductCount(param.getProductCount()*-1);
		List<WorkBottleVO> workBottleList = workMapper.selectWorkBottleListOfProduct(param);
	
		List<Integer> workSeqList = new ArrayList<Integer>();		
		//param.getProductCount()
//		logger.debug("WorkReportServiceImpl minusWorkBottle -"+workBottleList.size() );
//		logger.debug("cmdWorkReportServiceImpl minusWorkBottle param.getProductCount() -"+param.getProductCount() );
		int getI = workBottleList.size()-1;
		
		int productCount = param.getProductCount();
		
		// T 경우 수량 조정
		if(param.getProductId() >=65 &&  param.getProductId() <= 71 ) productCount = 1;
		
		for(int i=0; i < productCount; i++) {
			workSeqList.add(workBottleList.get(getI--).getWorkSeq());
		}		
	
		param.setWorkSeqList(workSeqList);
		result = workMapper.deleteWorkBottleOfProduct(param);
		return result;
	}
	
	@Transactional
	private int addOrderProduct(OrderProductVO param) {
		int result= 1;
		
		BottleVO bottle = new BottleVO();
		bottle.setProductId(param.getProductId());
		bottle.setProductPriceSeq(param.getProductPriceSeq());
		bottle.setGasId(param.getGasId());
		
		bottle = bottleService.getDummyBottle(bottle);
		
		List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();				
		
		for(int i=0; i < param.getSalesCount() ; i++) {
			OrderBottleVO orderBottle = new OrderBottleVO();			
			
			orderBottle.setOrderId(param.getOrderId());
			orderBottle.setOrderProductSeq(param.getOrderProductSeq());
			
			orderBottle.setProductId(param.getProductId());
			orderBottle.setProductPriceSeq(param.getProductPriceSeq());
			orderBottle.setCreateId(param.getCreateId());
			orderBottle.setBottleBarCd(bottle.getBottleBarCd());
			
			orderBottleList.add(orderBottle);
		}
		if(orderBottleList.size() > 0)
			result = orderService.registerOrderBottles(orderBottleList);
		
		double orderPrice = param.getOrderAmount() / param.getOrderCount();
		
		param.setOrderCount(param.getOrderCount()+param.getSalesCount());
		param.setOrderAmount(param.getOrderAmount()+orderPrice*param.getSalesCount());		
		
		result = orderService.modifyOrderProductCount(param);		
		
		return result;
	}
	
	private int addOrderProductNoGas(OrderProductVO param) {
		int result= 1;

		double orderPrice = param.getOrderAmount() / param.getOrderCount();
		
		param.setOrderCount(param.getOrderCount()+param.getSalesCount());
		param.setOrderAmount(param.getOrderAmount()+orderPrice*param.getSalesCount());		
		
		result = orderService.modifyOrderProductCount(param);
		
		return result;
	}

	@Transactional
	private int minusOrderProduct(OrderProductVO param) {
		int result= 1;

		int orderProductCount = 1;
		orderProductCount = param.getOrderCount()+ param.getSalesCount() ;
//		logger.debug("WorkReportServiceImpl --minusOrderProduct  param.getOrderCount=" + param.getOrderCount() );
//		logger.debug("WorkReportServiceImpl --minusOrderProduct  param.getSalesCount=" + param.getSalesCount() );		
//		logger.debug("WorkReportServiceImpl --minusOrderProduct  orderProductCount=" + orderProductCount );
		if(param.getGasId() > 0){
			// OrderBottlte 삭제
			//param.setSalesCount(param.getSalesCount()*-1);
			List<OrderBottleVO> orderBottleList = orderService.getOrderBottleListOfProduct(param);
			if(orderBottleList.size() == param.getSalesCount()) orderProductCount = 0;
			
			OrderBottleVO orderBottle = new OrderBottleVO();
			List<Integer> orderList = new ArrayList<Integer>();
			
			int getI=orderBottleList.size()-1;
			
			for(int i = 0 ; i < param.getSalesCount() ; i++) {
				orderList.add(orderBottleList.get(getI).getOrderBottleSeq());
				
				if(orderBottleList.get(getI).getBottleBarCd()!=null && orderBottleList.get(getI).getBottleBarCd().length() > 0) {
					BottleVO bottle = new BottleVO();
					bottle.setBottleBarCd(orderBottleList.get(getI).getBottleBarCd());
					bottle.setCustomerId(param.getCustomerId());
					bottle.setUpdateId(param.getCreateId());
					
					result = bottleService.deleteCustomerIdOfBottle(bottle);
				}
			}
			
			orderBottle.setOrderBottleList(orderList);
			
			if(orderList.size() > 0) {
				orderService.deleteOrderBottle(orderBottle);
			}
		}

		if(orderProductCount > 0) {
		// orderProduct 수정s
			double orderPrice = param.getOrderAmount() / param.getOrderCount();
			
			param.setOrderCount(param.getOrderCount()+param.getSalesCount());
			param.setOrderAmount(param.getOrderAmount()-orderPrice*param.getSalesCount());		
			
			result = orderService.modifyOrderProductCount(param);
		}else {
			result = orderService.deleteOrderProduct(param);
		}
		return result;
	}
	
	private List<WorkBottleVO> getNewWorkBottle(List<WorkBottleVO> params, int flag) {		
		
		List<WorkBottleVO> addWorkBottleList = new ArrayList<WorkBottleVO>();
		
		for(int i = 0 ; i < params.size() ; i++) {
//			logger.debug("************* bottleType =="+params.get(i).getBottleType());
			if(flag > 0) {
				if(params.get(i).getNewYn().equals("Y")) {
					addWorkBottleList.add(params.get(i));				
				}
			}else {
				if(params.get(i).getNewProductYn().equals("Y")) {
					addWorkBottleList.add(params.get(i));				
				}
			}
		}		
		return addWorkBottleList;		
	}
	
	@Transactional
	private int addNewWorkBottle(List<WorkBottleVO> params) {		
//		logger.debug(" --addNewWorkBottle =#################") ;
		int result = 0;
		if(params.size() <= 0 ) return 1;
		List<WorkBottleVO> addWorkBottleList = new ArrayList<WorkBottleVO>();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("bottleList", params);
		map.put("customerId", params.get(0).getCustomerId());
		
		List<ProductTotalVO> productTotalList =  productService.getPriceList(map);	
		
		if(params.size() > 0) {
			int workSeq = workMapper.selectWorkBottleSeq(params.get(0).getWorkReportSeq());
			
			for(int i = 0 ; i < params.size() ; i++) {
				
				WorkBottleVO workBottle = params.get(i);
//				logger.debug("WorkReportServiceImpl addNewWorkBottle --workBottle.getAgencyYn()" + workBottle.getAgencyYn());
				BottleVO bottle = new BottleVO();
				bottle.setProductId(workBottle.getProductId());
				bottle.setProductPriceSeq(workBottle.getProductPriceSeq());
				bottle.setCustomerId(workBottle.getCustomerId());
							
//				ProductTotalVO productTotal = productService.getPrice(bottle);
				ProductTotalVO productTotal = getProductTotal(productTotalList,bottle);//2021-06-19
				
				BottleVO dummy = bottleService.getDummyBottle(bottle);
				
				if(StringUtils.isTankProduct(workBottle.getProductId())) {
					WorkBottleVO addWorkBottle = new WorkBottleVO();
					
					addWorkBottle.setWorkReportSeq(workBottle.getWorkReportSeq());
					addWorkBottle.setCustomerId(workBottle.getCustomerId());
					addWorkBottle.setProductId(workBottle.getProductId());
					addWorkBottle.setProductPriceSeq(workBottle.getProductPriceSeq());
					addWorkBottle.setBottleWorkCd(workBottle.getBottleWorkCd());
					addWorkBottle.setCreateId(workBottle.getCreateId());
					addWorkBottle.setSearchDt(workBottle.getSearchDt());
					addWorkBottle.setBottleWorkCd(workBottle.getBottleWorkCd());
					addWorkBottle.setBottleSaleYn(workBottle.getBottleSaleYn());					
					addWorkBottle.setBottleType(workBottle.getBottleType());
					addWorkBottle.setWorkSeq(workSeq);
					addWorkBottle.setChargeVolumn(params.get(i).getProductCount() );
					addWorkBottle.setWorkEtc(params.get(i).getWorkEtc() );
					addWorkBottle.setAgencyYn(workBottle.getAgencyYn());
					workSeq++;
					if(dummy != null) {
						addWorkBottle.setGasId(dummy.getGasId());
						addWorkBottle.setGasCd(dummy.getGasCd());
						addWorkBottle.setBottleId(dummy.getBottleId());
						addWorkBottle.setBottleBarCd(dummy.getBottleBarCd());
					}
					
					if(productTotal !=null && productTotal.getCustomerBottlePrice() > 0) addWorkBottle.setProductPrice(productTotal.getCustomerBottlePrice());	
					else addWorkBottle.setProductPrice(productTotal.getProductBottlePrice());		
					addWorkBottle.setBottleSaleYn("Y");
					
					addWorkBottleList.add(addWorkBottle);
				}else {
					for(int j=0 ; j < params.get(i).getProductCount() ; j++) {
						WorkBottleVO addWorkBottle = new WorkBottleVO();
						
						addWorkBottle.setWorkReportSeq(workBottle.getWorkReportSeq());
						addWorkBottle.setCustomerId(workBottle.getCustomerId());
						addWorkBottle.setProductId(workBottle.getProductId());
						addWorkBottle.setProductPriceSeq(workBottle.getProductPriceSeq());
						addWorkBottle.setBottleWorkCd(workBottle.getBottleWorkCd());
						addWorkBottle.setCreateId(workBottle.getCreateId());
						addWorkBottle.setSearchDt(workBottle.getSearchDt());
						addWorkBottle.setBottleWorkCd(workBottle.getBottleWorkCd());
						addWorkBottle.setBottleSaleYn(workBottle.getBottleSaleYn());					
						addWorkBottle.setBottleType(workBottle.getBottleType());
						addWorkBottle.setWorkSeq(workSeq);
						addWorkBottle.setAgencyYn(workBottle.getAgencyYn());
						workSeq++;
						if(dummy != null) {
							addWorkBottle.setGasId(dummy.getGasId());
							addWorkBottle.setGasCd(dummy.getGasCd());
							addWorkBottle.setBottleId(dummy.getBottleId());
							addWorkBottle.setBottleBarCd(dummy.getBottleBarCd());
						}
	//					if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))) {
	//						addWorkBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
	//					}else {
	//						addWorkBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.FULL"));
	//					}
						
						if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))) {
							if(productTotal !=null && productTotal.getCustomerProductPrice() > 0) addWorkBottle.setProductPrice(productTotal.getCustomerProductPrice());
							else addWorkBottle.setProductPrice(productTotal.getProductPrice());
						}else if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
							if(productTotal !=null && productTotal.getCustomerBottlePrice() > 0) addWorkBottle.setProductPrice(productTotal.getCustomerBottlePrice());	
							else addWorkBottle.setProductPrice(productTotal.getProductBottlePrice());		
							addWorkBottle.setBottleSaleYn("Y");
						}
	//					logger.debug("WorkReportServiceImpl --addWorkBottle getGasCd" + addWorkBottle.getGasCd());
						addWorkBottleList.add(addWorkBottle);
					}
				}
			}		
			
			if(addWorkBottleList.size() > 0)
				result = workMapper.insertWorkBottles(addWorkBottleList);
			
			result = changeCustomerProduct(addWorkBottleList);
		}else
			result = 1;
		
		return result;		
	}
	
	@Transactional
	private int addNewOrderProduct(List<WorkBottleVO> params) {		
		
		int result = 0;
		
		if(params.size() > 0) {
			List<OrderProductVO> addOrderProductList = new ArrayList<OrderProductVO>();
			List<OrderBottleVO> addOrderBottletList = new ArrayList<OrderBottleVO>();
			
			int orderProductSeq = orderService.getNextOrderProductSeq(params.get(0).getOrderId());
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("bottleList", params);
			map.put("customerId", params.get(0).getCustomerId());
			
			List<ProductTotalVO> productTotalList =  productService.getPriceList(map);
			
			for(int i = 0 ; i < params.size() ; i++) {
				
				OrderProductVO orderProduct = new OrderProductVO();
				WorkBottleVO workBottle = params.get(i);
				if(!workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))) {
					BottleVO bottle = new BottleVO();
					bottle.setProductId(workBottle.getProductId());
					bottle.setProductPriceSeq(workBottle.getProductPriceSeq());
					//bottle.setCurrentPage(workBottle.getCustomerId());
								
//					ProductTotalVO productTotal = productService.getPrice(bottle);
					ProductTotalVO productTotal = getProductTotal(productTotalList,bottle);//2021-06-19
					
					BottleVO dummy = bottleService.getDummyBottle(bottle);
					
					orderProduct.setOrderId(workBottle.getOrderId());
					orderProduct.setOrderProductSeq(orderProductSeq++);
					orderProduct.setProductId(workBottle.getProductId());
					orderProduct.setProductPriceSeq(workBottle.getProductPriceSeq());
					orderProduct.setOrderCount(workBottle.getProductCount());
					
					orderProduct.setOrderProductEtc(PropertyFactory.getProperty("order.etc.message.work"));
					
//					logger.debug("for in --workBottle.getBottleWorkCd()=" + workBottle.getBottleWorkCd());
					if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))) {
						orderProduct.setBottleChangeYn("Y");
						orderProduct.setBottleSaleYn("N");
						
						if(productTotal !=null && productTotal.getCustomerProductPrice() > 0) orderProduct.setOrderAmount(workBottle.getProductCount()*productTotal.getCustomerProductPrice());
						else orderProduct.setOrderAmount(workBottle.getProductCount()*productTotal.getProductPrice());
					}else if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
						orderProduct.setBottleChangeYn("N");
						orderProduct.setBottleSaleYn("Y");
						
						if(productTotal !=null && productTotal.getCustomerBottlePrice() > 0) orderProduct.setOrderAmount(workBottle.getProductCount()*productTotal.getCustomerBottlePrice());
						else orderProduct.setOrderAmount(workBottle.getProductCount()*productTotal.getProductBottlePrice());
					}
					if(dummy !=null) {
						for(int j=0; j < orderProduct.getOrderCount() ; j++) {
							OrderBottleVO orderBottle = new OrderBottleVO();
							
							orderBottle.setOrderId(orderProduct.getOrderId());
							orderBottle.setOrderProductSeq(orderProduct.getOrderProductSeq());
							orderBottle.setProductId(orderProduct.getProductId());
							orderBottle.setProductPriceSeq(orderProduct.getProductPriceSeq());
							orderBottle.setBottleBarCd(dummy.getBottleBarCd());
							orderBottle.setCreateId(workBottle.getCreateId());
							
							addOrderBottletList.add(orderBottle);					
						}
					}
					orderProduct.setCreateId(workBottle.getCreateId());		
					
					addOrderProductList.add(orderProduct);		
				}
			}		
		
			if(addOrderProductList.size() > 0) {
				result = orderService.registerOrderProducts(addOrderProductList);
			
				if(addOrderBottletList.size() > 0) 
					result = orderService.registerOrderBottles(addOrderBottletList);
			}else {
				result = 1;
			}
		}
		return result;		
	}

	private int modifyOrderInfo(WorkReportVO param) {
		int result = 0;
		
		List<OrderProductVO> orderProductList = orderService.getOrderProductList( param.getOrderId());
//		logger.debug(" --modifyOrderInfo  orderProductList.size=" + orderProductList.size() );
		String orderProductNm = "";
		String orderProductCapa = "";
		double orderTotalAmount = 0;		
		
		for(int i=0 ;i < orderProductList.size() ; i++) {
			orderTotalAmount +=orderProductList.get(i).getOrderAmount();
			if(i==0) {
				orderProductNm = orderProductList.get(i).getProductNm();
				orderProductCapa = orderProductList.get(i).getProductCapa();
			}
		}
		
		if(orderProductList.size()  > 1) {
			orderProductNm +="외 "+ (orderProductList.size()-1); 
			orderProductCapa +="외 "+ (orderProductList.size()-1); 
		}
		OrderVO order = new OrderVO();
		
		order.setOrderId(param.getOrderId());
		order.setUpdateId(param.getUpdateId());
		
		if(orderProductList.size() > 0) {
			order.setOrderProductNm(orderProductNm);
			order.setOrderProductCapa(orderProductCapa);
			order.setOrderTotalAmount(orderTotalAmount);
			
			result = orderService.modifyOrderAdditionBottles(order);
		}else {
			result = orderService.deleteOrder(order);
		}
		return result ;
	}
	
	private int modifyCustomerProduct(WorkBottleVO param) {
		
		int result = 0;
		CustomerProductVO customerProduct = new CustomerProductVO();
		
		customerProduct.setCustomerId(param.getCustomerId());
		customerProduct.setUpdateId(param.getCreateId());
		customerProduct.setProductId(param.getProductId());
		customerProduct.setProductPriceSeq(param.getProductPriceSeq());
//		logger.debug("modifyCustomerProduct param.getProductCount()==" + param.getProductCount() ); 
//		logger.debug("modifyCustomerProduct (param.getAgencyYn()==" + param.getAgencyYn() ); 
		if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {			
			customerProduct.setBottleOwnCount(param.getProductCount());		
			result = customerService.modifyCustomerProductOwnCount(customerProduct);
		}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) ) {			
			customerProduct.setBottleRentCount(param.getProductCount());			
			result = customerService.modifyCustomerProductRentCount(customerProduct);			
		}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back")) ) {	
			if(param.getAgencyYn().equals("Y"))
				customerProduct.setBottleRentCount(param.getProductCount());			
			else
				customerProduct.setBottleRentCount(param.getProductCount()*-1);			
			result = customerService.modifyCustomerProductRentCount(customerProduct);
		}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack")) ) {	
			if(param.getAgencyYn().equals("Y"))
				customerProduct.setBottleOwnCount(param.getProductCount());		
			else
				customerProduct.setBottleOwnCount(param.getProductCount()*-1);
			result = customerService.modifyCustomerProductOwnCount(customerProduct);
		}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyBack")) ) {		
			customerProduct.setBottleRentCount(param.getProductCount()*-1);			
			result = customerService.modifyCustomerProductRentCount(customerProduct);
		}else {
			result = 1;
		}
		return result;
	}
	

	@Override
	public List<WorkBottleVO> getWorkBottleListOfUser(WorkReportVO param) {
		
		return workMapper.selectWorkReportListAll(param);
	}

	@Override
	@Transactional
	public int registerWorkReportMassByBottle(WorkReportVO param) {
		
		int result = 0;
		try {
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();		
			List<OrderProductVO> orderProductList = new ArrayList<OrderProductVO>();	
			List<String> bottles = new ArrayList<String>();	
			List<String> productCounts = new ArrayList<String>();	
			
			List<String> strBottleList = null;
			
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				strBottleList = StringUtils.makeForeach(param.getBottlesIds(), ","); 							
			}		
			
			List<String> list = null;
			String strBottle = "";
			for(int i = 0 ; i < strBottleList.size() ; i++) {
				list = StringUtils.makeForeach(strBottleList.get(i), "-");
				
				if(list.size()==2) {
					bottles.add(list.get(0));
					strBottle += list.get(0)+",";
					productCounts.add(list.get(1));					
				}				
			}
			param.setBottlesIds(strBottle);
			
			List<BottleVO> bottleList = getBottleList(param);
			
			if(param.getUserId() == null)
				param.setUserId(param.getCreateId());
			
			boolean registerFlag = false;
			int workSeq=1;
			int workReportSeq = getWorkReportSeqForCustomerToday(param);
			
			if(workReportSeq <= 0) {
				//workReportSeq = getWorkReportSeq();
				registerFlag = true;
				result = workMapper.insertWorkReport(param);
				
				workReportSeq = getWorkReportSeqForCustomerToday(param);
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}
						
			param.setWorkReportSeq(workReportSeq);
//			if(registerFlag)
//				result = workMapper.insertWorkReport(param);	
			
			for(int i = 0 ; i < bottleList.size() ; i++) {
				BottleVO bottle = bottleList.get(i);
				
				bottle.setCustomerId(param.getCustomerId());
				bottle.setUpdateId(param.getUserId());
				bottle.setBottleWorkCd(param.getBottleWorkCd());
				
				OrderProductVO orderProduct = new OrderProductVO();
				orderProduct.setProductId(bottle.getProductId());
				orderProduct.setProductPriceSeq(bottle.getProductPriceSeq());
				orderProduct.setBottleWorkCd(param.getBottleWorkCd());
				orderProduct.setCustomerId(param.getCustomerId());
				orderProduct.setRetrievedYn("N");
				orderProduct.setAsYn("N");
				
				for(int j=0;j<bottles.size() ; j++) {

					if(bottle.getBottleBarCd().equals(bottles.get(j)) ) {
						int productCount = Integer.parseInt(productCounts.get(j));
						orderProduct.setOrderCount(Integer.parseInt(productCounts.get(j))) ;
						
						for(int k=0 ; k < productCount ; k++) {							
							
							WorkBottleVO workBottle = new WorkBottleVO();
							workBottle.setWorkReportSeq(param.getWorkReportSeq());
							workBottle.setWorkSeq(workSeq++);
							workBottle.setBottleWorkCd(param.getBottleWorkCd());
							workBottle.setBottleId(bottle.getBottleId());
							workBottle.setBottleBarCd(bottle.getBottleBarCd());
							workBottle.setCustomerId(param.getCustomerId());
							workBottle.setGasId(bottle.getGasId());
							workBottle.setProductId(bottle.getProductId());
							workBottle.setProductPriceSeq(bottle.getProductPriceSeq());
							workBottle.setBottleType(param.getBottleType());										
							workBottle.setCreateId(param.getUserId());
							workBottle.setAgencyYn(param.getAgencyYn());
							workBottle.setMultiYn("Y");
							workBottleList.add(workBottle);						
						}
						break;
					}					
				} //for(int j=0;j<bottles.size() ; j++) {
				orderProductList.add(orderProduct);
			}
			
			result = workMapper.insertWorkBottles(workBottleList);			
			
			BottleVO bottle = new BottleVO();
			bottle.setCustomerId(param.getCustomerId());
			bottle.setBottleWorkCd(param.getBottleWorkCd());
			bottle.setBottleWorkId(param.getCreateId());
			bottle.setBottleType(param.getBottleType());
			bottle.setUpdateId(param.getCreateId());
			List<String> list1 = null;
			String tempBottleIds = "";
			
			for(int i = 0; i< bottleList.size() ; i++) {
				tempBottleIds += bottleList.get(i).getBottleId()+",";
			}
			list1 = StringUtils.makeForeach(tempBottleIds, ","); 		
			bottle.setBottList(list1);
//			logger.debug("WorkReportServiceImpl registerWorkReportMassByBottle bottleList =" + list1.size());	
			result =  bottleService.changeWorkCdsAndHistory(bottle,bottleList);
			
			result = modifyCustomerProductMass(param, orderProductList);
			
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("registerWorkReportMassByBottle Exception==="+e.toString());
			return -1;
		}		
		
		return result;
	}
	
	@Override
	@Transactional
	public int registerWorkReportMassNoOrder(WorkReportVO param) {
		
		int result = 0;
		try {		
			//기존 주문이 있는지 여부 확인
			OrderVO order =  null;
			//if(param.getAgencyYn().equals("N") || !param.getUserId().equals("factory") ) 
			order = orderService.getLastOrderForCustomer(param.getCustomerId());
			
			//Bottle 정보 가져오기
			
			List<String> bottles = new ArrayList<String>();	
			List<String> productCounts = new ArrayList<String>();	
			
			List<String> strBottleList = null;
			
			if(param.getBottlesIds()!=null && param.getBottlesIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				strBottleList = StringUtils.makeForeach(param.getBottlesIds(), ","); 							
			}		
			
			List<String> list = null;
			String strBottle = "";
			for(int i = 0 ; i < strBottleList.size() ; i++) {
				list = StringUtils.makeForeach(strBottleList.get(i), "-");
				
				if(list.size()==2) {
					bottles.add(list.get(0));
					strBottle += list.get(0)+",";
					productCounts.add(list.get(1));					
				}				
			}
			
			if(param.getUserId() == null)
				param.setUserId(param.getCreateId());
			param.setBottlesIds(strBottle);
//			logger.debug(" registerWorkReportMassNoOrder workReportSeq =" + p);
			List<BottleVO> bottleList = getBottleList(param);
						
//			for(int i=0; i < bottleList.size() ; i++) {
//				bottleList.get(i).setBottleWorkCd(param.getBottleWorkCd());
//				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {	
//					if(param.getAgencyYn().equals("Y")) {
//						bottleList.get(i).setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesgas"));
//					}
//				}
//			}
			//Work_Report_Seq 가져오기
			boolean registerFlag = false;
			int workSeq=1;
			int workReportSeq = 0;
//			if(param.getAgencyYn().equals("N") || !param.getUserId().equals("factory") )  workReportSeq = getWorkReportSeqForCustomerToday(param);
			workReportSeq = getWorkReportSeqForCustomerToday(param); //20210619 추가
			
			if(workReportSeq <= 0) {
				//workReportSeq = getWorkReportSeq();
				registerFlag = true;
				
				result = workMapper.insertWorkReport(param);	
				workReportSeq = getWorkReportSeqForCustomerToday(param);
			}else {
				workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
			}
			
//			logger.debug(" registerWorkReportMassNoOrder workReportSeq =" + workReportSeq);
			//TB_Work_Reprot 등록
			param.setWorkReportSeq(workReportSeq);
			
			if(order != null ) {
				param.setOrderId(order.getOrderId());
				//logger.debug("WorkReportServiceImpl registerWorkReportMassNoOrder getOrderId =" + param.getOrderId());	
				WorkBottleVO workBottle = new WorkBottleVO();
				workBottle.setWorkReportSeq(workReportSeq);
				workBottle.setWorkSeq(workSeq);
//				logger.debug(" registerWorkReportMassNoOrder workSeq =" + workSeq);
				result = registerWorkReportMassForOrder(param, workBottle, order, bottleList, bottles,productCounts);
			}else {
											
				//Order & Order_Product & order_bottle CustomerProduct
				OrderVO orderResult = registerOrderInfo(param,workSeq,bottleList,bottles,productCounts);			
				
				//WorkReprot
				param.setOrderId(orderResult.getOrderId());
				//param.setOrderAmount(orderResult.getOrderTotalAmount());
				param.setWorkProductNm(orderResult.getOrderProductNm());
				param.setWorkProductCapa(orderResult.getOrderProductCapa());
				param.setUpdateId(param.getUserId());
				
				result = workMapper.modifyWorkReportProduct(param);
				
				//Cash_Flow
				if(orderResult.getOrderTotalAmount() > 0)
					result = registerCashFlow(param,orderResult.getOrderTotalAmount());
			}
//			result = bottleService.modifyBottlesOrder(bottleList);		
//			result = bottleService.registerBottlesHistory(bottleList);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("registerWorkReportMassNoOrder Exception==="+e.toString());
		}
		return result;
	}

	@Transactional
	private OrderVO registerOrderInfo(WorkReportVO param,int workSeq, List<BottleVO> params, List<String> bottles, List<String> productCounts) {
//		logger.debug(" registerOrderInfo" );
		try {
			int result = 0;
			//Order
			OrderVO order = new OrderVO();
			
			order.setCreateId(param.getUpdateId());
			order.setCustomerId(param.getCustomerId());
			
			order.setMemberCompSeq(1);
			order.setOrderTypeCd(PropertyFactory.getProperty("common.code.order.type.order"));				
			
			Calendar cal = Calendar.getInstance();
			int amPm = cal.get(Calendar.AM_PM);
			order.setDeliveryReqDt(cal.getTime());
			
			if (amPm == 1 ) order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.PM"));
			else  order.setDeliveryReqAmpm(PropertyFactory.getProperty("Order.Request.AM"));
			
			order.setOrderEtc("대량판매(현)");
			if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come"))) {
				order.setOrderEtc("대량입고(현)");
			}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out")) ) {	
				order.setOrderEtc("대량출고(현)");
			}
			order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));
			if(param.getUserId() != null && param.getUserId().length() > 0)
				order.setSalesId(param.getUserId());
			else
				order.setSalesId(param.getCreateId());
			order.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
			order.setUpdateId(param.getUserId());
			
			result = orderService.registerOrder(order);			
			
			int orderId = orderService.getNewOrderId(order);
			order.setOrderId(orderId);
			
			List<OrderProductVO> orderProductList = new ArrayList<OrderProductVO>();	
			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();			
			
			double orderTotalAmount = 0 ;
			String orderProductNm = "";
			String orderProductCapa = "";
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("bottleList", params);
			map.put("customerId", param.getCustomerId());
			
			List<ProductTotalVO> productTotalList =  productService.getPriceList(map);
			
			for(int i = 0 ; i < params.size() ; i++) {
				BottleVO bottle = params.get(i);
				
				bottle.setCustomerId(param.getCustomerId());
				bottle.setBottleType(param.getBottleType());
				bottle.setUpdateId(param.getUserId());
				bottle.setBottleWorkId(param.getCreateId());
				
				//대리점의 경우 판매->가스판매로 변경
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) && param.getAgencyYn().equals("Y"))  bottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesgas"));
				else bottle.setBottleWorkCd(param.getBottleWorkCd());
				
//				ProductTotalVO productTotal = productService.getPrice(bottle);		
				ProductTotalVO productTotal = getProductTotal(productTotalList,bottle);//2021-06-19
				if(i==0) {
					orderProductNm =productTotal.getProductNm();
					orderProductCapa = productTotal.getProductCapa();
				}
				
				OrderProductVO orderProduct = new OrderProductVO();
				
				orderProduct.setOrderId(orderId);
				orderProduct.setOrderProductSeq(i+1);
				orderProduct.setProductId(bottle.getProductId());
				orderProduct.setProductPriceSeq(bottle.getProductPriceSeq());
				orderProduct.setCreateId(param.getUserId());
				orderProduct.setOrderProductEtc(PropertyFactory.getProperty("order.etc.message.customer"));
				orderProduct.setOrderProductEtc("현장추가");
				orderProduct.setBottleWorkCd(param.getBottleWorkCd());
				params.get(i).setOrderId(orderId);
				params.get(i).setOrderProductSeq(orderProduct.getOrderProductSeq());
				
				for(int j=0;j<bottles.size() ; j++) {
//					logger.debug(" registerOrderInfo bottle.getBottleId()="+bottle.getBottleId() );
//					logger.debug(" registerOrderInfo bottles.get(j)="+bottles.get(j) );
					if(bottle.getBottleId().equals(bottles.get(j)) ) {
						orderProduct.setOrderCount(Integer.parseInt(productCounts.get(j))) ;
						break;
					}
					else orderProduct.setOrderCount(1);
				}
				
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
					double tempOrderAmount = 0;
					orderProduct.setBottleChangeYn("N");
					orderProduct.setBottleSaleYn("Y");
					
					if(param.getAgencyYn().equals("N")) {
						
						if(productTotal.getCustomerProductPrice() > 0) 
							tempOrderAmount+= (productTotal.getCustomerProductPrice() + productTotal.getCustomerBottlePrice() ) * orderProduct.getOrderCount();							
						else 
							tempOrderAmount+= (productTotal.getProductPrice() + productTotal.getProductBottlePrice() ) * orderProduct.getOrderCount();			
					}else {
//						orderProduct.setBottleChangeYn("Y");
//						orderProduct.setBottleSaleYn("N");
						
						if(productTotal.getCustomerProductPrice() > 0) {
							tempOrderAmount+= productTotal.getCustomerProductPrice() * orderProduct.getOrderCount();
						}else {
							tempOrderAmount+=  productTotal.getProductPrice() * orderProduct.getOrderCount();	
						}			
					}
					orderProduct.setOrderAmount(tempOrderAmount);
//					if(productTotal.getCustomerBottlePrice() > 0) 
//						orderProduct.setOrderAmount(orderProduct.getOrderCount() * productTotal.getCustomerBottlePrice());
//					else 
//						orderProduct.setOrderAmount(orderProduct.getOrderCount() * productTotal.getProductBottlePrice());					
					
					orderProduct.setIncomeYn("N");
					orderProduct.setOutYn("N");
				}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come"))) {
					orderProduct.setOrderAmount(0);
					orderProduct.setBottleChangeYn("N");
					orderProduct.setBottleSaleYn("N");
					orderProduct.setIncomeYn("Y");
					orderProduct.setOutYn("N");
				}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))) {
					orderProduct.setOrderAmount(0);
					orderProduct.setBottleChangeYn("N");
					orderProduct.setBottleSaleYn("N");
					orderProduct.setIncomeYn("N");
					orderProduct.setOutYn("Y");
				}else {
					if(productTotal.getCustomerProductPrice() > 0)
						orderProduct.setOrderAmount(orderProduct.getOrderCount() * productTotal.getCustomerProductPrice());
					else
						orderProduct.setOrderAmount(orderProduct.getOrderCount() * productTotal.getProductPrice());
					
					orderProduct.setBottleChangeYn("Y");
					orderProduct.setBottleSaleYn("N");
					orderProduct.setIncomeYn("N");
					orderProduct.setOutYn("N");
				}
				orderProduct.setRetrievedYn("N");
				orderProduct.setAsYn("N");
				
				orderTotalAmount += orderProduct.getOrderAmount();
				orderProductList.add(orderProduct);
//				logger.debug(" registerWorkReportMassNoOrder orderTotalAmount0 =" + orderTotalAmount);
				for(int k =0 ; k < orderProduct.getOrderCount() ; k++) {
					OrderBottleVO orderBottle = new OrderBottleVO();
					orderBottle.setCreateId(param.getUserId());
					orderBottle.setOrderId(orderId);					
					orderBottle.setOrderProductSeq(orderProduct.getOrderProductSeq());
					orderBottle.setBottleBarCd(bottle.getBottleBarCd());
					orderBottle.setProductId(bottle.getProductId());
					orderBottle.setProductPriceSeq(bottle.getProductPriceSeq());
					
					orderBottleList.add(orderBottle);
				
					WorkBottleVO workBottle = makeWorkBottle(bottle);
					workBottle.setWorkReportSeq(param.getWorkReportSeq());
					workBottle.setWorkSeq(workSeq++);
					workBottle.setBottleSaleYn(orderProduct.getBottleSaleYn());
					workBottle.setProductPrice(orderProduct.getOrderAmount()/orderProduct.getOrderCount());	
					workBottle.setAgencyYn(param.getAgencyYn());
					workBottle.setBottleType(param.getBottleType());
					if(param.getMultiYn().equals("Y")) workBottle.setMultiYn("Y");
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {	
						workBottle.setBottleSaleYn("Y");
						/*
						// 가스판매 추가 20210710
						WorkBottleVO workBottleAdditional = makeWorkBottle(bottle);
						workBottleAdditional.setWorkReportSeq(param.getWorkReportSeq());
						workBottleAdditional.setWorkSeq(workSeq++);
//						workBottleAdditional.setBottleSaleYn(orderProduct.getBottleSaleYn());
						workBottleAdditional.setAgencyYn(param.getAgencyYn());
					
						workBottleAdditional.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesgas"));
						
						if(productTotal.getCustomerBottlePrice() > 0) {
							workBottle.setProductPrice(productTotal.getCustomerBottlePrice());
						}else {
							workBottle.setProductPrice( productTotal.getProductBottlePrice());	
						}
						
						if(productTotal.getCustomerProductPrice() > 0) {
							workBottleAdditional.setProductPrice(productTotal.getCustomerProductPrice());
						}else {
							workBottleAdditional.setProductPrice(productTotal.getProductPrice());
						}
						
						workBottleList.add(workBottleAdditional); 
						orderTotalAmount += workBottleAdditional.getProductPrice();
						*/
						if(param.getAgencyYn().equals("N")) {
							
							if(productTotal.getCustomerProductPrice() > 0) {
								workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getCustomerProductPrice());
								workBottle.setGasPrice(productTotal.getCustomerProductPrice());
								orderTotalAmount += productTotal.getCustomerProductPrice();
							}else {
								workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getProductPrice());
								workBottle.setGasPrice(productTotal.getProductPrice());
								orderTotalAmount += productTotal.getProductPrice();
							}
						}else {
			
							if(productTotal.getCustomerProductPrice() > 0) {
								workBottle.setProductPrice(productTotal.getCustomerProductPrice());
							}else {
								workBottle.setProductPrice( productTotal.getProductPrice());	
							}
							workBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesgas"));
						}
					}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out")) ) {	
						workBottle.setProductPrice(0);	
					}
//					logger.debug(" registerWorkReportMassNoOrder orderTotalAmount =" + orderTotalAmount);
					workBottleList.add(workBottle);
				}
				
			} // bottleList for
			
			if(params.size() > 1) {
				orderProductNm +="외"+ (params.size()-1);
				orderProductCapa +="외"+ (params.size()-1);
			}
			order.setOrderProductNm(orderProductNm);
			order.setOrderProductCapa(orderProductCapa);			
			
			orderTotalAmount = 0;
			for(int i=0 ; i < orderProductList.size() ; i++ ) {
				orderTotalAmount += orderProductList.get(i).getOrderAmount();
			}
			order.setOrderTotalAmount(orderTotalAmount);
			
			//result = orderService.registerOrderAndProduct(order,orderProductList); 2020-08-28 수저
			List<OrderProductVO> newOrderProductList = new ArrayList<OrderProductVO>();	
			OrderProductVO beforeOrderProduct = null;
			int j =0;
			for(int i =0 ; i < orderProductList.size() ; i++) {
				OrderProductVO tempOrderProduct = orderProductList.get(i);
				if(i==0) {
					j++;
					newOrderProductList.add(tempOrderProduct);
					beforeOrderProduct = tempOrderProduct;
				}else {
					if(tempOrderProduct.getProductId().equals(beforeOrderProduct.getProductId()) 
							&& tempOrderProduct.getProductPriceSeq().equals(beforeOrderProduct.getProductPriceSeq()) ){
						newOrderProductList.get(j-1).setOrderCount(newOrderProductList.get(j-1).getOrderCount()+tempOrderProduct.getOrderCount());
						newOrderProductList.get(j-1).setOrderAmount(newOrderProductList.get(j-1).getOrderAmount()+tempOrderProduct.getOrderAmount());
					}else {
						j++;
						newOrderProductList.add(tempOrderProduct);
						beforeOrderProduct = tempOrderProduct;
					}
				}
			}
			result = orderService.modifyOrderRegiProduct(order,newOrderProductList);
			
			if(orderBottleList.size() > 0)
				result = orderService.registerOrderBottles(orderBottleList);
			
			if(workBottleList.size() > 0)
				result = workMapper.insertWorkBottles(workBottleList);
			
			result = modifyCustomerProductMass(param, orderProductList);
			
			for(int i=0; i < params.size() ; i++) {
				params.get(i).setBottleWorkCd(param.getBottleWorkCd());
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {	
					if(param.getAgencyYn().equals("Y")) {
						params.get(i).setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesgas"));
					}
				}
			}
			
			result = bottleService.modifyBottlesOrder(params);		
			result = bottleService.registerBottlesHistory(params);
			
			if(result > 0) 			
				return order;
			else
				return null;
		} catch (DataAccessException e) {
			e.printStackTrace();
			logger.error("registerWorkReportForOrder Exception==="+e.toString());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("registerWorkReportForOrder Exception==="+e.toString());
			return null;
		}
	}
	
		
	private int modifyCustomerProductMass(WorkReportVO param, List<OrderProductVO> params) {
//		logger.debug(" modifyCustomerProductMass");

		int result = 0 ;
		try {
			List<CustomerProductVO> customerProductList = customerService.getCustomerProductList(param.getCustomerId());
			List<CustomerProductVO> registerCustomerProductList = new ArrayList<CustomerProductVO>();
			
			List<OrderProductVO> productList = new ArrayList<OrderProductVO>();
			boolean isBeen = false;
			// 동일한 상품 정리
			for(int i = 0 ; i < params.size() ; i++ ) {
				OrderProductVO orderProduct = params.get(i);
				OrderProductVO productNew = new OrderProductVO();
				
				for(int j =0 ; j < productList.size() ; j++) {
					if(orderProduct.getProductId()== productList.get(j).getProductId() && orderProduct.getProductPriceSeq() == productList.get(j).getProductPriceSeq()) {
						productList.get(j).setOrderCount(productList.get(j).getOrderCount()+orderProduct.getOrderCount());
						isBeen = true;
					}
				}
				if(!isBeen) {
					productNew.setProductId(orderProduct.getProductId());
					productNew.setProductPriceSeq(orderProduct.getProductPriceSeq());
					productNew.setOrderCount(orderProduct.getOrderCount());
					productList.add(productNew);
				}
			}
			
			
			int checkAgency = 1;
			if(param.getAgencyYn()!=null && param.getAgencyYn().equals("Y") && param.getWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) checkAgency = -1;
			
			for(int i = 0 ; i < productList.size() ; i++ ) {
				OrderProductVO orderProduct = productList.get(i);
				boolean isReg = true;

				if(customerProductList !=null) {
					for(int j=0 ; j < customerProductList.size() ; j++) {
						CustomerProductVO  customerProduct = customerProductList.get(j);
						customerProduct.setCustomerId(param.getCustomerId());
						customerProduct.setUpdateId(param.getUserId());
						
						if(orderProduct.getProductId()== customerProduct.getProductId() && orderProduct.getProductPriceSeq() == customerProduct.getProductPriceSeq()) {
							if(param.getWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
								customerProduct.setBottleOwnCount(checkAgency * orderProduct.getOrderCount());
								result = customerService.modifyCustomerProductOwnCount(customerProduct);
							}else if(param.getWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))) {
								customerProduct.setBottleRentCount(checkAgency * orderProduct.getOrderCount());
								result = customerService.modifyCustomerProductRentCount(customerProduct);
							}else if(param.getWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))) {
								customerProduct.setBottleRentCount(checkAgency * orderProduct.getOrderCount()*-1);
								result = customerService.modifyCustomerProductRentCount(customerProduct);
							}else if(param.getWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack")) || param.getWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.massback")) ) {
								customerProduct.setBottleOwnCount(checkAgency * orderProduct.getOrderCount()*-1);
								result = customerService.modifyCustomerProductOwnCount(customerProduct);
							}
							isReg = false;						
						}
						
					}
				}
				if(isReg) {
					CustomerProductVO customerP = new CustomerProductVO();
					customerP.setCreateId(param.getUserId());
					customerP.setCustomerId(param.getCustomerId());
					customerP.setProductId(orderProduct.getProductId());
					customerP.setProductPriceSeq(orderProduct.getProductPriceSeq());

					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
						customerP.setBottleOwnCount(checkAgency * orderProduct.getOrderCount());
					}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))) {
						customerP.setBottleRentCount(checkAgency * orderProduct.getOrderCount());
					}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))) {
						customerP.setBottleRentCount(checkAgency * orderProduct.getOrderCount()*-1);
					}					
					registerCustomerProductList.add(customerP);
				}
			}
			
			if(registerCustomerProductList.size() > 0) {
				result = customerService.registerCustomerProducts(registerCustomerProductList);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("registerWorkReportForOrder Exception==="+e.toString());
			return -1;
		}
		return result ;
		
	}
	
	
	@Transactional
	private int registerOrderProductDummy(WorkBottleVO param, OrderProductVO orderProduct) {
		int result = 0;
		
		List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();	
		
		orderProduct.setProductId(param.getProductId());
		orderProduct.setOrderCount(param.getProductCount());
				
		orderProduct.setOrderProductEtc(PropertyFactory.getProperty("order.etc.message.noOrder"));					
		
		orderProduct.setRetrievedYn("N");
		orderProduct.setAsYn("N");
		
		if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
			orderProduct.setBottleChangeYn("N");
			orderProduct.setBottleSaleYn("Y");
		}else {
			orderProduct.setBottleChangeYn("Y");
			orderProduct.setBottleSaleYn("N");
		}
		orderProduct.setCreateId(param.getUserId());
		
		result = orderService.registerOrderProduct(orderProduct);
		
		//Order_Bottle
		for(int i =0 ; i < param.getProductCount() ; i++) {
			OrderBottleVO orderBottle = new OrderBottleVO();
			orderBottle.setOrderId(orderProduct.getOrderId());
			orderBottle.setOrderProductSeq(orderProduct.getOrderProductSeq());
			orderBottle.setProductId(param.getProductId());
			orderBottle.setProductPriceSeq(param.getProductPriceSeq());
			orderBottle.setBottleBarCd(param.getBottleBarCd());
			orderBottle.setCreateId(param.getUserId());
			
			orderBottleList.add(orderBottle);					
		}		
		// Order_Bottle 등록
		if(orderBottleList.size() > 0)
			result = orderService.registerOrderBottles(orderBottleList);
		
		return result;		
	}	

	@Transactional
	private int registerWorkReportMassForOrder(WorkReportVO param, WorkBottleVO workB, OrderVO order, List<BottleVO> bottleList, List<String> bottles, List<String> productCounts) {
		int result = 0;
		logger.debug("WorkReportServiceImp"," registerWorkReportMassForOrder start");
		logger.debug("WorkReportServiceImp"," registerWorkReportMassForOrder param.getWorkReportSeq()=="+param.getWorkReportSeq());
		try {
			int workSeq = workB.getWorkSeq();
			List<OrderProductVO> soldOrderProductList = new ArrayList<OrderProductVO>();	
			List<OrderProductVO> remainOrderProductList = new ArrayList<OrderProductVO>();	
			List<OrderProductVO> addOrderProductList = new ArrayList<OrderProductVO>();	
			
			List<OrderBottleVO> orderBottleList = new ArrayList<OrderBottleVO>();
			List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();	
			
			//Order_Product 비교
			List<OrderProductVO> orderProductList = orderService.getOrderProductListNew(order.getOrderId());
//			logger.debug("WorkReportServiceImpl registerWorkReportMassForOrder 1 ");
			List<OrderBottleVO> orderBottleListNot = orderService.getOrderBottleListNotDelivery(order.getOrderId());
			double addOrderTotalAmount = 0;
			double orderTotalAmount = 0;
			
			int addOrderProductSeq = 0;
			
			for(int i = 0 ; i < bottleList.size() ; i++) {
				BottleVO bottle = bottleList.get(i);
				OrderProductVO orderProduct = new OrderProductVO();
				
				orderProduct.setOrderId(order.getOrderId());			
				orderProduct.setProductId(bottle.getProductId());
				orderProduct.setProductPriceSeq(bottle.getProductPriceSeq());
				orderProduct.setBottleId(bottle.getBottleId());
				orderProduct.setBottleBarCd(bottle.getBottleBarCd());
				orderProduct.setGasId(bottle.getGasId());
				orderProduct.setGasCd(bottle.getGasCd());
				orderProduct.setRetrievedYn("N");
				orderProduct.setAsYn("N");
				orderProduct.setBottleWorkCd(param.getBottleWorkCd());
				bottleList.get(i).setOrderId(order.getOrderId());
				
				for(int j=0;j<bottles.size() ; j++) {
					if(bottle.getBottleId().equals(bottles.get(j)) ) {						
						orderProduct.setOrderCount(Integer.parseInt(productCounts.get(j))) ;
						break;
					}
					else orderProduct.setOrderCount(1);
				}
				soldOrderProductList.add(orderProduct);
			}
//			logger.debug("WorkReportServiceImpl registerWorkReportMassForOrder 2 ");
			int orderProductCount = 0;
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("bottleList", bottleList);
			map.put("customerId", param.getCustomerId());
			
			List<ProductTotalVO> productTotalList =  productService.getPriceList(map);
			
			for(int j=0; j < soldOrderProductList.size() ; j++) {
				OrderProductVO orderProduct = soldOrderProductList.get(j);
				orderProductCount = orderProduct.getOrderCount();				
				
				BottleVO bottle = new BottleVO();
				bottle.setCustomerId(param.getCustomerId());
				bottle.setProductId(orderProduct.getProductId());
				bottle.setProductPriceSeq(orderProduct.getProductPriceSeq());
				
				ProductTotalVO productTotal = getProductTotal(productTotalList,bottle);//2021-06-19
				
				for(int k =0 ; k < orderProduct.getOrderCount() ; k++) {					
					
					for(int i = 0 ; i < orderBottleListNot.size() ; i++) {
						OrderBottleVO orderBottle = orderBottleListNot.get(i);
						
						if(orderProductCount > 0 && orderBottle.getBottleBarCd() ==null
								&& orderBottle.getProductId() == orderProduct.getProductId() && orderBottle.getProductPriceSeq() == orderProduct.getProductPriceSeq()
								&& orderBottle.getBottleWorkCd().equals(orderProduct.getBottleWorkCd()) ) {
							orderBottle.setBottleBarCd(orderProduct.getBottleBarCd());
							
							WorkBottleVO workBottle = new WorkBottleVO();
							
							workBottle.setWorkReportSeq(param.getWorkReportSeq());
							workBottle.setWorkSeq(workSeq++);
							workBottle.setCustomerId(param.getCustomerId());
							workBottle.setBottleId(orderProduct.getBottleId());
							workBottle.setBottleBarCd(orderProduct.getBottleBarCd());
							workBottle.setGasId(orderProduct.getGasId());		
							workBottle.setGasCd(orderProduct.getGasCd());	
							workBottle.setCreateId(param.getUserId());
							workBottle.setProductId(orderProduct.getProductId());
							workBottle.setProductPriceSeq(orderProduct.getProductPriceSeq());		
							workBottle.setBottleWorkCd(param.getBottleWorkCd());
//							workBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.FULL"));
							workBottle.setBottleType(param.getBottleType());
							workBottle.setAgencyYn(param.getAgencyYn());
							workBottle.setMultiYn("Y");
							
							if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {	
								
							
								if(productTotal.getCustomerBottlePrice() > 0) {
									workBottle.setProductPrice(productTotal.getCustomerBottlePrice());
								}else {
									workBottle.setProductPrice( productTotal.getProductBottlePrice());	
								}
								if(param.getAgencyYn().equals("N")) {
									workBottle.setBottleSaleYn("Y");
									if(productTotal.getCustomerProductPrice() > 0) {
										workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getCustomerProductPrice());
										workBottle.setGasPrice(productTotal.getCustomerProductPrice());
										orderTotalAmount += productTotal.getCustomerProductPrice();
									}else {
										workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getProductPrice());
										workBottle.setGasPrice(productTotal.getProductPrice());
										orderTotalAmount += productTotal.getProductPrice();
									}

								}else {
									workBottle.setBottleSaleYn("N");
									workBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesgas"));
								}
							}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come"))) {
								workBottle.setBottleSaleYn("N");
								workBottle.setProductPrice( 0);
							}else {
								workBottle.setBottleSaleYn("N");
								if(productTotal.getCustomerProductPrice() > 0)
									workBottle.setProductPrice(productTotal.getCustomerProductPrice());
								else
									workBottle.setProductPrice(productTotal.getProductPrice());					
							}
							orderTotalAmount += workBottle.getProductPrice();
							workBottleList.add(workBottle); 
							orderProductCount--;
							
						}
					}
				} //for(int k =0 ; k < orderProduct.getOrderCount() ; k++) {
//				logger.debug(" registerWorkReportMassForOrder orderProductCount2 ="+orderProductCount);
				if(orderProductCount > 0 ) {
					orderProduct.setOrderCount(orderProductCount);		
					
					remainOrderProductList.add(orderProduct);
				}
			}
//			logger.debug("WorkReportServiceImpl registerWorkReportMassForOrder 3 ");
			for(int i = 0 ; i < orderBottleListNot.size() ; i++) {
				OrderBottleVO orderBottle = orderBottleListNot.get(i);
				orderBottle.setUpdateId(param.getUserId());
				if(orderBottle.getBottleBarCd() != null) {
					result = orderService.modifyOrderBottle(orderBottle);
					//orderBottleList.add(orderBottle);
				}
			}			
			
			if(orderProductList!=null) {
				for(int i = 0 ; i < orderProductList.size() ; i++) {
					if( i==0) addOrderProductSeq = orderProductList.get(i).getOrderProductSeq();
					
					if(addOrderProductSeq < orderProductList.get(i).getOrderProductSeq())
						addOrderProductSeq = orderProductList.get(i).getOrderProductSeq();
					
					logger.debug(" registerWorkReportMassForOrder addOrderProductSeq11 ="+addOrderProductSeq);
				}
				addOrderProductSeq++;
			}
//			logger.debug(" registerWorkReportMassForOrder addOrderProductSeq ="+addOrderProductSeq);
//			addOrderProductSeq = orderProductList.size()+1;
			for(int i = 0 ; i < remainOrderProductList.size() ; i++) {
				boolean regiFlag = true;
				OrderProductVO remainOrderProduct = remainOrderProductList.get(i);
				//logger.debug("WorkReportServiceImpl registerWorkReportMassForOrder addOrderProductList getProductId "+remainOrderProduct.getProductId());
				
				BottleVO bottle = new BottleVO();
				bottle.setCustomerId(param.getCustomerId());
				bottle.setProductId(remainOrderProduct.getProductId());
				bottle.setProductPriceSeq(remainOrderProduct.getProductPriceSeq());
				
				ProductTotalVO productTotal = getProductTotal(productTotalList,bottle);//2021-06-19
				
				for (int j =0 ; j < orderProductList.size() ; j++) {
					if(remainOrderProductList.get(i).getProductId() == orderProductList.get(j).getProductId()
							&& remainOrderProductList.get(i).getProductPriceSeq() == orderProductList.get(j).getProductPriceSeq()
							&& remainOrderProductList.get(i).getBottleWorkCd().equals(orderProductList.get(j).getBottleWorkCd())) {
						
						double orginPrice = orderProductList.get(j).getOrderAmount() / orderProductList.get(j).getOrderCount();
						
						orderProductList.get(j).setOrderCount(orderProductList.get(j).getOrderCount()+remainOrderProduct.getOrderCount());
						orderProductList.get(j).setOrderAmount( orderProductList.get(j).getOrderCount()*orginPrice);
						if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))) {	
							orderProductList.get(j).setOrderAmount( 0);
						}
						result = orderService.modifyOrderProductCount(orderProductList.get(j));
						regiFlag = false;
						
						//WorkBottle orderBottle 추가
						for(int k=0 ; k < remainOrderProduct.getOrderCount() ; k++) {
							OrderBottleVO orderBottle = new OrderBottleVO();
							
							orderBottle.setOrderId(order.getOrderId());
							orderBottle.setOrderProductSeq(orderProductList.get(j).getOrderProductSeq());
							orderBottle.setProductId(remainOrderProduct.getProductId());
							orderBottle.setProductPriceSeq(remainOrderProduct.getProductPriceSeq());
							orderBottle.setBottleBarCd(remainOrderProduct.getBottleBarCd());
							orderBottle.setCreateId(param.getUserId());
							
							orderBottleList.add(orderBottle);
							
							WorkBottleVO workBottle = new WorkBottleVO();
							
							workBottle.setWorkReportSeq(param.getWorkReportSeq());
							workBottle.setWorkSeq(workSeq++);
							workBottle.setCustomerId(param.getCustomerId());
							workBottle.setBottleId(remainOrderProduct.getBottleId());
							workBottle.setBottleBarCd(remainOrderProduct.getBottleBarCd());
							workBottle.setGasId(remainOrderProduct.getGasId());
							workBottle.setGasCd(remainOrderProduct.getGasCd());
							workBottle.setCreateId(param.getUserId());
							workBottle.setProductId(remainOrderProduct.getProductId());
							workBottle.setProductPriceSeq(remainOrderProduct.getProductPriceSeq());		
							workBottle.setBottleWorkCd(param.getBottleWorkCd());
//							workBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.FULL"));
							workBottle.setBottleType(param.getBottleType());
							workBottle.setProductPrice(orginPrice);
							workBottle.setBottleSaleYn("Y");
							workBottle.setAgencyYn(param.getAgencyYn());
							workBottle.setMultiYn("Y");
							
							if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {	
								workBottle.setBottleSaleYn("Y");
							
								if(param.getAgencyYn().equals("N")) {
									if(productTotal.getCustomerProductPrice() > 0) {
										workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getCustomerProductPrice());
										workBottle.setGasPrice(productTotal.getCustomerProductPrice());
										orderTotalAmount += productTotal.getCustomerProductPrice();
									}else {
										workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getProductPrice());
										workBottle.setGasPrice(productTotal.getProductPrice());
										orderTotalAmount += productTotal.getProductPrice();
									}
								}else {
									if(productTotal.getCustomerProductPrice() > 0) {
										workBottle.setProductPrice(productTotal.getCustomerProductPrice());
									}else {
										workBottle.setProductPrice( productTotal.getProductPrice());	
									}
									workBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesgas"));
								}
							}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))) {
								workBottle.setProductPrice( 0);	
								workBottle.setBottleSaleYn("N");
							}
							
							workBottleList.add(workBottle);
						}
					}	
				}
//				logger.debug("WorkReportServiceImpl registerWorkReportMassForOrder 4 ");
				if(regiFlag) {
					
					remainOrderProduct.setOrderProductSeq(addOrderProductSeq++);
					remainOrderProduct.setOrderProductEtc("현장추가");
					remainOrderProduct.setBottleChangeYn("N");
					remainOrderProduct.setBottleSaleYn("Y");
					remainOrderProduct.setIncomeYn("N");
					remainOrderProduct.setOutYn("N");
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
						if(productTotal.getCustomerBottlePrice() > 0) {
							remainOrderProduct.setOrderAmount(productTotal.getCustomerBottlePrice()* remainOrderProduct.getOrderCount());
						}else {
							remainOrderProduct.setOrderAmount( productTotal.getProductBottlePrice()* remainOrderProduct.getOrderCount());	
						}
						if(param.getAgencyYn().equals("N")) {
							if(productTotal.getCustomerProductPrice() > 0) {
								remainOrderProduct.setOrderAmount(remainOrderProduct.getOrderAmount()+productTotal.getCustomerProductPrice() * remainOrderProduct.getOrderCount());
							}else {
								remainOrderProduct.setOrderAmount(remainOrderProduct.getOrderAmount()+productTotal.getProductPrice() * remainOrderProduct.getOrderCount());
							}
						}
						
					}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) ) {
						remainOrderProduct.setBottleChangeYn("N");
						remainOrderProduct.setBottleSaleYn("N");
						remainOrderProduct.setIncomeYn("Y");
						remainOrderProduct.setOutYn("N");
					}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))) {
						remainOrderProduct.setBottleChangeYn("N");
						remainOrderProduct.setBottleSaleYn("N");
						remainOrderProduct.setIncomeYn("N");
						remainOrderProduct.setOutYn("Y");
					}else {
						if(productTotal.getCustomerProductPrice() > 0)
							remainOrderProduct.setOrderAmount(remainOrderProduct.getOrderCount() * productTotal.getCustomerProductPrice());
						else
							remainOrderProduct.setOrderAmount(remainOrderProduct.getOrderCount() * productTotal.getProductPrice());
						
						remainOrderProduct.setBottleChangeYn("Y");
						remainOrderProduct.setBottleSaleYn("N");
						remainOrderProduct.setIncomeYn("N");
						remainOrderProduct.setOutYn("N");
					}
					remainOrderProduct.setCreateId(param.getUserId());
					
					addOrderProductList.add(remainOrderProduct);
					
				}
			}
//			logger.debug("WorkReportServiceImpl registerWorkReportMassForOrder 5 ");
			//Order_Bottle, Work_Bottle
			for(int i = 0 ; i < addOrderProductList.size() ; i++) {
				OrderProductVO orderProduct = addOrderProductList.get(i);
				
				BottleVO bottle = new BottleVO();
				bottle.setCustomerId(param.getCustomerId());
				bottle.setProductId(orderProduct.getProductId());
				bottle.setProductPriceSeq(orderProduct.getProductPriceSeq());
				
				ProductTotalVO productTotal = getProductTotal(productTotalList,bottle);//2021-06-19
				
				for(int j = 0 ; j < orderProduct.getOrderCount() ;j++) {
					OrderBottleVO orderBottle = new OrderBottleVO();
					
					orderBottle.setOrderId(order.getOrderId());
					orderBottle.setOrderProductSeq(orderProduct.getOrderProductSeq());
					orderBottle.setProductId(orderProduct.getProductId());
					orderBottle.setProductPriceSeq(orderProduct.getProductPriceSeq());
					orderBottle.setBottleBarCd(orderProduct.getBottleBarCd());
					orderBottle.setCreateId(param.getUserId());
					
					orderBottleList.add(orderBottle);
					
					WorkBottleVO workBottle = new WorkBottleVO();
					
					workBottle.setWorkReportSeq(param.getWorkReportSeq());
					workBottle.setWorkSeq(workSeq++);
					workBottle.setCustomerId(param.getCustomerId());
					workBottle.setBottleId(orderProduct.getBottleId());
					workBottle.setBottleBarCd(orderProduct.getBottleBarCd());
					workBottle.setGasId(orderProduct.getGasId());						
					workBottle.setCreateId(param.getUserId());
					workBottle.setProductId(orderProduct.getProductId());
					workBottle.setProductPriceSeq(orderProduct.getProductPriceSeq());		
					workBottle.setBottleWorkCd(param.getBottleWorkCd());
//					workBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.FULL"));
					workBottle.setBottleType(param.getBottleType());
					workBottle.setProductPrice(orderProduct.getOrderAmount() / orderProduct.getOrderCount() );
					workBottle.setBottleSaleYn("Y");
					workBottle.setAgencyYn(param.getAgencyYn());
					workBottle.setMultiYn("Y");
					
					if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {	
						workBottle.setBottleSaleYn("Y");

						if(productTotal.getCustomerBottlePrice() > 0) {
							workBottle.setProductPrice(productTotal.getCustomerBottlePrice());
						}else {
							workBottle.setProductPrice( productTotal.getProductBottlePrice());	
						}

						if(param.getAgencyYn().equals("N")) {
							if(productTotal.getCustomerProductPrice() > 0) {
								workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getCustomerProductPrice());
								workBottle.setGasPrice(productTotal.getCustomerProductPrice());
								orderTotalAmount += productTotal.getCustomerProductPrice();
							}else {
								workBottle.setProductPrice(workBottle.getProductPrice() + productTotal.getProductPrice());
								workBottle.setGasPrice(productTotal.getProductPrice());
								orderTotalAmount += productTotal.getProductPrice();
							}
						}else {
							workBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesgas"));
						}
					}else if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.come")) || param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))) {
						workBottle.setProductPrice( 0);	
						workBottle.setGasPrice(0);
						workBottle.setBottleSaleYn("N");
						orderTotalAmount += 0;
					}
					workBottleList.add(workBottle);
					
					addOrderTotalAmount += workBottle.getProductPrice();
				}							
			}
			boolean updateOrderAddFlag = false;
			if(addOrderProductList.size() > 0) {
				result = orderService.registerOrderProducts(addOrderProductList);
				updateOrderAddFlag = true;
			}
			if(orderBottleList.size() > 0)
				result = orderService.registerOrderBottles(orderBottleList);
			if(workBottleList.size() > 0)
				result = workMapper.insertWorkBottles(workBottleList);			
						
			//WorkReprot
			param.setOrderId(order.getOrderId());
			//param.setOrderAmount(orderResult.getOrderTotalAmount());
			param.setWorkProductNm(order.getOrderProductNm());
			param.setWorkProductCapa(order.getOrderProductCapa());
			param.setUpdateId(param.getUserId());
			logger.debug("WorkReportServiceImp"," registerWorkReportMassForOrder befor modifyWorkReportProduct param.getWorkReportSeq()=="+param.getWorkReportSeq());
			result = workMapper.modifyWorkReportProduct(param);
			
			//Order Update
			boolean orderCompleted = false;
			double orderAmount = 0;
			
			// 처리안된 주문 상품 삭제 OrderCount  와  SaleCount 비교
			List<OrderProductVO> allOrderProductList = null;						
			allOrderProductList = orderService.getOrderProductListNew(order.getOrderId());
			
			List<OrderProductVO> updateOrderProducts = new ArrayList<OrderProductVO>();
			List<OrderProductVO> deleteOrderProducts = new ArrayList<OrderProductVO>();
			for(int j=0; j < allOrderProductList.size() ; j++) {
				OrderProductVO paramOrderProduct = allOrderProductList.get(j);
				if(paramOrderProduct.getSalesCount() == paramOrderProduct.getOrderCount()) deleteOrderProducts.add(paramOrderProduct);
				else if(paramOrderProduct.getSalesCount() > 0 && paramOrderProduct.getOrderCount() > paramOrderProduct.getSalesCount()) {
					paramOrderProduct.setOrderAmount(paramOrderProduct.getOrderAmount() / paramOrderProduct.getOrderCount());
					paramOrderProduct.setOrderCount(paramOrderProduct.getOrderCount() - paramOrderProduct.getSalesCount());
					paramOrderProduct.setUpdateId(param.getCreateId());
					updateOrderProducts.add(paramOrderProduct);
				}
			}
			
			if(updateOrderProducts.size() > 0 || deleteOrderProducts.size() > 0) updateOrderAddFlag = true;
			if(updateOrderProducts.size() > 0 ) result = orderService.modifyOrderProducts(updateOrderProducts);
//			if(deleteOrderProducts.size() > 0 ) result = orderService.deleteOrderProducts(deleteOrderProducts);
			
			// Order 상태 정보 변경
			List<OrderProductVO> orderProduct = orderService.getOrderProductList(order.getOrderId());
			//logger.debug("!!!!!!!!!!***orderProduct.size ="+orderProduct.size());
			int remainCount = 0;
			for(int j=0; j < orderProduct.size() ; j++) {
				remainCount += orderProduct.get(j).getSalesCount();
				orderAmount += orderProduct.get(j).getOrderAmount();
				
				order.setOrderProductNm(orderProduct.get(j).getProductNm());
				order.setOrderProductCapa(orderProduct.get(0).getProductCapa());
			}
			
			if(orderProduct.size() > 1) {
				order.setOrderProductNm(order.getOrderProductNm()+" 외 "+(allOrderProductList.size()-1));
				order.setOrderProductCapa(order.getOrderProductCapa()+" 외 "+(allOrderProductList.size()-1));
			}
			if(updateOrderAddFlag && orderAmount > 0) {
				order.setOrderTotalAmount(orderAmount);	
				order.setUpdateId(param.getUserId());
				
//				result = orderService.modifyOrderAdditionBottles(order);		
			}
			
			if(remainCount == 0) orderCompleted = true;			

			if(order.getOrderProcessCd().equals(PropertyFactory.getProperty("common.code.order.process.receive")) && orderCompleted) {
				order.setOrderDeliveryDt(DateUtils.getDate("yyyy/MM/dd HH:mm"));
				order.setOrderProcessCd(PropertyFactory.getProperty("common.code.order.process.delivery"));	
				order.setSalesId(param.getCreateId());
				order.setChOrderId(order.getOrderId());
				order.setUpdateId(param.getUserId());
				result = orderService.modifyOrderInfo(order);
			}else if(updateOrderAddFlag && orderAmount > 0) {
				
				result = orderService.modifyOrderAdditionBottles(order);
			}
			
			//Customer_Product
			result = modifyCustomerProductMass(param, addOrderProductList);
			
			for(int i=0; i < bottleList.size() ; i++) {
				bottleList.get(i).setBottleWorkCd(param.getBottleWorkCd());
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {	
					if(param.getAgencyYn().equals("Y")) {
						bottleList.get(i).setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.salesgas"));
					}
				}
			}
			result = bottleService.modifyBottlesOrder(bottleList);		
			result = bottleService.registerBottlesHistory(bottleList);
			
			if(orderTotalAmount+addOrderTotalAmount >0)
				result = registerCashFlow(param,order.getOrderTotalAmount());
						
		} catch (DataAccessException e) {		
			e.printStackTrace();
			logger.error(" registerWorkReportMassForOrder", e.toString());
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error(" registerWorkReportMassForOrder", e.toString());
		}
		return result;
	}
	
	
	@Override
	@Transactional
	public int deleteWorkReport(WorkReportVO param) {
		int result = 0;

		try {
			//work_report 업데이트
			WorkReportVO workReport = workMapper.selectWorkReportOnly(param.getWorkReportSeq());
			
			CustomerVO customer = customerService.getCustomerDetails(workReport.getCustomerId());
			
			List<WorkBottleVO> beforeWorkBottleList = workMapper.selectWorkBottleList(param.getWorkReportSeq());			
			
			List<OrderProductVO> orderProductList = orderService.getOrderProductList(workReport.getOrderId());					
			
			double totalAmount = 0;
			String bottleIds = "";
			//Order_product Order_Bottle
			for(int j=0 ; j < beforeWorkBottleList.size() ; j++) {
				WorkBottleVO workBottle = beforeWorkBottleList.get(j);		
				int workBottleProductCount = workBottle.getProductCount();
				workBottle.setProductCount(0);			
				workBottle.setAgencyYn(customer.getAgencyYn());
				bottleIds += workBottle.getBottleId()+",";
				totalAmount += workBottle.getProductPrice();
				//Work_bottle
				if(!workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back"))
						|| !workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack"))
						|| !workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyBack")) ) {
						
					for(int i =0 ; i <orderProductList.size() ; i++) {
						OrderProductVO orderProduct = orderProductList.get(i);
						
						if(orderProduct.getProductId() == workBottle.getProductId() 
								&& orderProduct.getProductPriceSeq() == workBottle.getProductPriceSeq()){
							
							// 같은 경우
							if( (orderProduct.getBottleChangeYn().equals("Y") && workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) )
									|| (orderProduct.getBottleChangeYn().equals("N") && workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) )  ) {
																
								int remainCount = workBottle.getProductCount() - orderProduct.getOrderCount();
								
								orderProduct.setSalesCount(remainCount);
								orderProduct.setCustomerId(workReport.getCustomerId());
								orderProduct.setCreateId(param.getCreateId());
								orderProduct.setUpdateId(param.getCreateId());
								orderProduct.setGasId(workBottle.getGasId());
								
								result = minusOrderProduct(orderProduct);
								if(result <=0 ) return -1;
							}							
						}
					} // for(int i =0 ; i <orderProductList.size() ; i++) {
				}		

				workBottle.setProductCount(workBottleProductCount*-1);
				workBottle.setUpdateId(param.getCreateId());

				if(workBottle.getGasId() > 0) {
					result = modifyCustomerProduct(workBottle);
					if(result <=0 ) return -1;
				}
				//Bottle정보 업데이트
			}			   

			if(beforeWorkBottleList.size() > 0) result = workMapper.deleteWorkBottle(param.getWorkReportSeq());
//			if(result <=0 ) return -1;
					
			result = workMapper.deleteWorkReport(param);		
			if(result <=0 ) return -1;			
			
			//Order
			if(workReport.getOrderId() !=null && workReport.getOrderId() > 0) {
				workReport.setUpdateId(param.getCreateId());
				result = modifyOrderInfo(workReport);			
				if(result <=0 ) return -1;
			}
			//Cash_flow
			CashFlowVO cashFlow = new CashFlowVO();
			cashFlow.setCustomerId(workReport.getCustomerId());
			cashFlow.setReceivableAmount(totalAmount*-1);
			if(workReport.getReceivedAmount() > 0)
				cashFlow.setIncomeAmount(workReport.getReceivedAmount()*-1);
			cashFlow.setIncomeWay("");
			if(param.getUserId() != null && param.getUserId().length() > 0)
				cashFlow.setCreateId(param.getUserId());
			else
				cashFlow.setCreateId(param.getCreateId());
			
			result = cashService.registerCashFlow(cashFlow);
			
		} catch (DataAccessException e) {		
			e.printStackTrace();
			logger.error("WorkReportServiceImpl", e.toString());
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("WorkReportServiceImpl", e.toString());
		}
		
		return result;
	}
	
	private WorkBottleVO makeWorkBottle(BottleVO bottle) {
		
		WorkBottleVO workBottle = new WorkBottleVO();
				
		workBottle.setCustomerId(bottle.getCustomerId());
		workBottle.setBottleId(bottle.getBottleId());
		workBottle.setBottleBarCd(bottle.getBottleBarCd());
		workBottle.setGasId(bottle.getGasId());						
		workBottle.setCreateId(bottle.getUpdateId());
		workBottle.setProductId(bottle.getProductId());
		workBottle.setProductPriceSeq(bottle.getProductPriceSeq());		
		workBottle.setBottleWorkCd(bottle.getBottleWorkCd());
		workBottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.FULL"));
		workBottle.setGasCd(bottle.getGasCd());
		
		return workBottle;		
	}
	
	private OrderBottleVO makeOrderBottle(OrderProductVO orderProduct, BottleVO bottle) {
		
		OrderBottleVO orderBottle = new OrderBottleVO();				
		
		orderBottle.setBottleBarCd(bottle.getBottleBarCd());
		orderBottle.setOrderId(orderProduct.getOrderId());
		orderBottle.setOrderProductSeq(orderProduct.getOrderProductSeq());
		orderBottle.setCreateId(bottle.getUpdateId());
		orderBottle.setProductId(bottle.getProductId());
		orderBottle.setProductPriceSeq(bottle.getProductPriceSeq());	
		
		
		return orderBottle;		
	}
	
	
	private int changeCustomerProduct(List<WorkBottleVO> param) {
		int result = 0;
		
		List<CustomerProductVO> cProductList = new ArrayList<CustomerProductVO>();
		String bottleWorkCd = "";
		Integer customerId = 0;
		String updateId = "";

		for(int i=0 ; i < param.size() ; i++) {
			customerId = param.get(i).getCustomerId();
			CustomerProductVO ctemp = new CustomerProductVO();
			ctemp.setCustomerId(param.get(i).getCustomerId());
			ctemp.setProductId(param.get(i).getProductId());
			ctemp.setProductPriceSeq(param.get(i).getProductPriceSeq());
			ctemp.setCreateId(param.get(i).getCreateId());
			bottleWorkCd = param.get(i).getBottleWorkCd();
			updateId = param.get(i).getCreateId();
			
			int bOwnCount = 0;
			int bRentCount = 0;
			
			if(param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) ) {
				if(param.get(i).getAgencyYn().equals("Y"))
					bOwnCount =- 1;
				else
					bOwnCount =+ 1;
			}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesBack")) ){ //20201220
				if(param.get(i).getAgencyYn().equals("Y"))
					bOwnCount =+ 1;
				else
					bOwnCount =- 1;
			}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent")) ) {
				if(param.get(i).getAgencyYn().equals("Y"))
					bRentCount =- 1;
				else
					bRentCount =+ 1;
			}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent")) ){ //20201220
				bRentCount =- 1;
			}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.back")) ) {
				if(param.get(i).getAgencyYn().equals("Y"))
					bRentCount =+ 1;
				else
					bRentCount =- 1;				
			}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyBack")) ){ //20201220
				bRentCount =+ 1;
			}else if (param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
					|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.buyback")) 
					|| param.get(i).getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas"))	) { //20201220
//				logger.debug(" --changeCustomerProduct  param.get(i).getAgencyYn().=" +param.get(i).getAgencyYn());
				if(param.get(i).getAgencyYn().equals("Y")) bOwnCount =+ 1;
				else bOwnCount =- 1;
			}		
			ctemp.setBottleOwnCount(bOwnCount);
			ctemp.setBottleRentCount(bRentCount);
			boolean checkYn = false;
			for(int j=0 ; j < cProductList.size() ; j++) {
				CustomerProductVO cP =cProductList.get(j);
				if(cP.getCustomerId() == ctemp.getCustomerId() && cP.getProductId() == ctemp.getProductId() 
						&& cP.getProductPriceSeq() == ctemp.getProductPriceSeq()) {
					checkYn =true;
					cP.setBottleOwnCount(ctemp.getBottleOwnCount()+bOwnCount);
					cP.setBottleRentCount(ctemp.getBottleRentCount()+bRentCount);
				}
			}
			if(!checkYn) cProductList.add(ctemp);
		}
		List<CustomerProductVO>  cPList = customerService.getCustomerProductList(customerId);
		for(int j=0 ; j < cProductList.size() ; j++) {
			CustomerProductVO cP =cProductList.get(j);
			cP.setUpdateId(updateId);
			boolean checkYn1 = false;
			for(int k=0 ; k < cPList.size() ; k++) {
				CustomerProductVO cProduct =cPList.get(k);
				if(cP.getProductId() == cProduct.getProductId() 
						&& cP.getProductPriceSeq() == cProduct.getProductPriceSeq()) {
					
					checkYn1 =true;
				}
			}
			
			if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.tcharge")))
					return 1;
					
			if(checkYn1) {
				if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.sale"))
						|| bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.freeback"))
						|| bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.buyback")) 
						|| bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.salesgas")) 
						|| bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.salesBack")) 
						) {
					result = customerService.modifyCustomerProductOwnCount(cP);
					
				}else if (bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.rent"))
						|| bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.back"))
						|| bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.agencyRent"))
						|| bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.agencyBack"))
						) {
//					logger.debug("customerProduct.getRentCOunt="+customerProduct.getBottleRentCount());
					result = customerService.modifyCustomerProductRentCount(cP);
				}
			}else{
				result = customerService.registerCustomerProduct(cP);
			}
		}
		
		return result;
	}

	@Override
	public int deleteWorkReportAndBottle(WorkReportVO report, WorkBottleVO workBottle) {
		int result = 0 ;
		// api/deleteWorkBottle 
		//List<WorkBottleVO> workBottleList = workMapper.selectWorkBottleListOfWorkBottleCd(workBottle);	
		List<WorkBottleVO> workBottleList = getWorkBottleList(report.getWorkReportSeq());	
		
		//workBottle Count / orderProduct Count
		int workBottleListTotalCount = workBottleList.size();		
		int workBottleListCount = 0;		
		Double workBottleAmount = 0.0;		
		
		for(int i = 0; i < workBottleList.size() ; i++) {
			if(workBottle.getProductId() == workBottleList.get(i).getProductId() &&
					workBottle.getProductPriceSeq() == workBottleList.get(i).getProductPriceSeq()
					&& workBottle.getBottleWorkCd().equals(workBottleList.get(i).getBottleWorkCd())) {
				workBottleListCount++;
				
				workBottleAmount = workBottleList.get(i).getProductPrice();
				workBottle.setGasId(workBottleList.get(i).getGasId());
				workBottle.setBottleWorkCd(workBottleList.get(i).getBottleWorkCd());
			}
		}
		
//		logger.debug(" --deleteWorkReportAndBottle  workBottleListTotalCount=" + workBottleListTotalCount );
		
		// workBottleListTotalCount workBottleListCount 비교
		if(workBottleListTotalCount == workBottleListCount) {	// report 삭제 및 workBottle 삭제
			result = workMapper.deleteWorkBottle(report.getWorkReportSeq());
			if(result <=0 ) return -1;
					
			result = workMapper.deleteWorkReport(report);		
			if(result <=0 ) return -1;		
		}else if(workBottleListTotalCount > workBottleListCount) {
			
			result = workMapper.deleteWorkBottleProductWorkCd(workBottle);			
		}
		
		if(workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))
				|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.rent"))
				|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.salesgas"))
				|| workBottle.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.agencyRent"))
				) 
		{
			result = deleteOrderAndProduct(report,workBottle);
			
			result = registerCashFlow(report, workBottleAmount*workBottleListCount*-1);
		}
		return result;
	}
	
	private int deleteOrderAndProduct(WorkReportVO param, WorkBottleVO workBottle) {
		
		int result = 0;
		
		if(workBottle.getProductId() > 0 && workBottle.getProductPriceSeq() > 0) {
			OrderExtVO orderExt = orderService.getOrder(param.getOrderId());
			orderExt.getOrder().setUpdateId(param.getUserId());
			Double orderProductAmount = 0.0;
			
			if(orderExt!=null && orderExt.getOrderProduct().size() > 0) {
				//orderProduct Count		
				int orderProductListTotalCount = orderExt.getOrderProduct().size();		
				int orderProductListCount = 0;
				int orderProductCount = 0;
				
				List<Integer> orderList = new ArrayList<Integer>();
				int idxSel = 0;
				for(int i = 0; i < orderExt.getOrderProduct().size() ; i++) {
					if(workBottle.getProductId() == orderExt.getOrderProduct().get(i).getProductId() &&
							workBottle.getProductPriceSeq() == orderExt.getOrderProduct().get(i).getProductPriceSeq()) {
						orderProductListCount++;			
						orderProductAmount = orderExt.getOrderProduct().get(i).getOrderAmount();
						orderProductCount = orderExt.getOrderProduct().get(i).getOrderCount();
						idxSel = i;
					}
				}
								
//				logger.debug(" --deleteOrderAndProduct  orderProductListTotalCount=" + orderProductListTotalCount );
//				logger.debug(" --deleteOrderAndProduct  orderProductListCount=" + orderProductListCount );
//				logger.debug(" --deleteOrderAndProduct  orderProductAmount=" + orderProductAmount );
//				logger.debug(" --deleteOrderAndProduct  orderProductCount=" + orderProductCount );
//				logger.debug(" --deleteOrderAndProduct  workBottle.getGasId()=" + workBottle.getGasId() );
				
				if(orderProductListTotalCount == orderProductListCount) {
					//OrderProduct / OrderBottle / Order
					result = orderService.deleteOrderProducts(orderExt.getOrderProduct().get(0));
					
					if(workBottle.getGasId() > 0) {
//						logger.debug(" --deleteOrderAndProduct  orderProductCount1=" + orderProductCount );
						
						List<OrderBottleVO> orderBottleList = orderService.getOrderBottleListOfProduct(orderExt.getOrderProduct().get(0));
						
						for ( int i = 0 ; i < orderBottleList.size() ; i++) {
							orderBottleList.get(i).setUpdateId(param.getCreateId());
							result = bottleService.modifyBottleAfterDelete(orderBottleList.get(i));	
						}
						
						result = orderService.deleteOrderBottles(orderExt.getOrderProduct().get(0));
						
						CustomerProductVO customerProduct = new CustomerProductVO();
						customerProduct.setUpdateId(param.getUserId());
						customerProduct.setProductId(workBottle.getProductId());
						customerProduct.setProductPriceSeq(workBottle.getProductPriceSeq());
						customerProduct.setCustomerId(param.getCustomerId());
						//TB_Customer_Product 
						if(workBottle.getBottleWorkCd() != null 
								&& PropertyFactory.getProperty("common.bottle.status.rent").equals(workBottle.getBottleWorkCd())
								&& PropertyFactory.getProperty("common.bottle.status.salesgas").equals(workBottle.getBottleWorkCd()) ){
							// updateCustomerProductRentCount
//							logger.debug(" --deleteOrderAndProduct  orderProductCount2=" + orderProductCount );
							customerProduct.setBottleRentCount(orderProductCount*-1);
							
							result = customerService.modifyCustomerProductRentCount(customerProduct);
						}else {//updateCustomerProductOwnCount
							customerProduct.setBottleOwnCount(orderProductCount*-1);
							result = customerService.modifyCustomerProductOwnCount(customerProduct);
						}
					}
					result = orderService.deleteOrder(orderExt.getOrder());
					
				}else if(orderProductListTotalCount > orderProductListCount) {
					result = orderService.deleteOrderProductByProduct(orderExt.getOrderProduct().get(idxSel));
					if(workBottle.getGasId() > 0) {
						List<OrderBottleVO> orderBottleList = orderService.getOrderBottleListOfProduct(orderExt.getOrderProduct().get(idxSel));
						
						for ( int i = 0 ; i < orderBottleList.size() ; i++) {
							orderBottleList.get(i).setUpdateId(param.getCreateId());
							result = bottleService.modifyBottleAfterDelete(orderBottleList.get(i));	
						}
						
						result = orderService.deleteOrderBottlesByProduct(orderExt.getOrderProduct().get(idxSel));
					}
					
					List<OrderProductVO> orderProductList = orderService.getOrderProductList( param.getOrderId());
//					logger.debug(" --deleteOrderAndProduct  orderProductList.size=" + orderProductList.size() );
					String orderProductNm = "";
					String orderProductCapa = "";
					double orderTotalAmount = 0;		
					
					for(int i=0 ;i < orderProductList.size() ; i++) {
						orderTotalAmount +=orderProductList.get(i).getOrderAmount();
						if(i==0) {
							orderProductNm = orderProductList.get(i).getProductNm();
							orderProductCapa = orderProductList.get(i).getProductCapa();
						}
					}
					
					if(orderProductList.size()  > 1) {
						orderProductNm +="외 "+ (orderProductList.size()-1); 
						orderProductCapa +="외 "+ (orderProductList.size()-1); 
					}
					OrderVO order = new OrderVO();
					
					order.setOrderId(param.getOrderId());
					order.setUpdateId(param.getUpdateId());
					
					if(orderProductList.size() > 0) {
						order.setOrderProductNm(orderProductNm);
						order.setOrderProductCapa(orderProductCapa);
						order.setOrderTotalAmount(orderTotalAmount);
						
						result = orderService.modifyOrderAdditionBottles(order);
					}
					CustomerProductVO customerProduct = new CustomerProductVO();
					customerProduct.setUpdateId(param.getUserId());
					customerProduct.setProductId(workBottle.getProductId());
					customerProduct.setProductPriceSeq(workBottle.getProductPriceSeq());
					customerProduct.setCustomerId(param.getCustomerId());
					if(workBottle.getBottleWorkCd() != null 
							&& PropertyFactory.getProperty("common.bottle.status.rent").equals(workBottle.getBottleWorkCd())
							&& PropertyFactory.getProperty("common.bottle.status.salesgas").equals(workBottle.getBottleWorkCd()) ){
						// updateCustomerProductRentCount
//						logger.debug(" --deleteOrderAndProduct  orderProductCount2=" + orderProductCount );
						customerProduct.setBottleRentCount(orderProductCount*-1);
						
						result = customerService.modifyCustomerProductRentCount(customerProduct);
					}else {//updateCustomerProductOwnCount
						customerProduct.setBottleOwnCount(orderProductCount*-1);
						result = customerService.modifyCustomerProductOwnCount(customerProduct);
					}
				}
			}//if(orderExt!=null && orderExt.getOrderProduct().size() > 0) {
		}
		return result;
	}

	@Override
	public int registerWorkBottleChargeTank(WorkBottleVO param) {
//		logger.debug(" -registerWorkBottleChargeTank" );
		int result = 0;
		int workSeq=1;
		boolean registerFlag = false;
		
		// 금일 작업 이력 확인 및 WorkReporSeq 가져오기
		WorkReportVO workReport = new WorkReportVO();
		workReport.setCustomerId(param.getCustomerId());
		workReport.setWorkCd(param.getBottleWorkCd());
		workReport.setCreateId(param.getCreateId());
		workReport.setUpdateId(param.getCreateId());		
		if(param.getUserId() !=null && param.getUserId().length() > 0)
			workReport.setUserId(param.getUserId());
		else
			workReport.setUserId(param.getCreateId());
					
		//WorkReportSeq 가져오기
		int workReportSeq = getWorkReportSeqForCustomerToday(workReport);
		
		if(workReportSeq <= 0) {
//			workReportSeq = getWorkReportSeq();
			registerFlag = true;
			workReport.setWorkReportSeq(workReportSeq);
			
			result = workMapper.insertWorkReport(workReport);
			workReportSeq = getWorkReportSeqForCustomerToday(workReport);
		}else {
			workSeq = workMapper.selectWorkBottleSeq(workReportSeq);
		}	
		
		BottleVO bottle = new BottleVO();
		bottle.setProductId(param.getProductId());
		bottle.setProductPriceSeq(param.getProductPriceSeq());
		bottle.setCustomerId(param.getCustomerId());
		
		ProductTotalVO productTotal = productService.getPrice(bottle);
		
		List<WorkBottleVO> workBottleList = new ArrayList<WorkBottleVO>();
		// 20211123 충전 용량 컬럼 추가
		if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.tcharge"))) {
			//20211123 충전용량 컬럼 추가 수정
			WorkBottleVO addWorkBottle = new WorkBottleVO();
			
			addWorkBottle.setWorkReportSeq(workReportSeq);
			addWorkBottle.setCustomerId(param.getCustomerId());
			
			addWorkBottle.setWorkSeq(workSeq++);
			addWorkBottle.setBottleWorkCd(param.getBottleWorkCd());
			addWorkBottle.setProductId(param.getProductId());
			addWorkBottle.setProductPriceSeq(param.getProductPriceSeq());
			addWorkBottle.setProductCapa(productTotal.getProductCapa());
			addWorkBottle.setBottleWorkCd(param.getBottleWorkCd());
			addWorkBottle.setBottleSaleYn("N");
			addWorkBottle.setChargeVolumn(param.getProductCount());
			addWorkBottle.setCreateId(param.getCreateId());
			addWorkBottle.setUpdateId(param.getCreateId());	
			
			result = workMapper.insertWorkBottle(addWorkBottle);
		}else {
			for(int i = 0 ; i < param.getProductCount() ; i++) {					
			
				WorkBottleVO addWorkBottle = new WorkBottleVO();
				
				addWorkBottle.setWorkReportSeq(workReportSeq);
				addWorkBottle.setCustomerId(param.getCustomerId());
				
				addWorkBottle.setWorkSeq(workSeq++);
				addWorkBottle.setBottleWorkCd(param.getBottleWorkCd());
				addWorkBottle.setProductId(param.getProductId());
				addWorkBottle.setProductPriceSeq(param.getProductPriceSeq());
				addWorkBottle.setProductCapa(productTotal.getProductCapa());
				addWorkBottle.setBottleWorkCd(param.getBottleWorkCd());
				addWorkBottle.setBottleSaleYn("N");
				addWorkBottle.setCreateId(param.getCreateId());
				addWorkBottle.setUpdateId(param.getCreateId());			
				
				workBottleList.add(addWorkBottle);
			}
			result = workMapper.insertWorkBottles(workBottleList);
		}
		
		return result;
	}

	@Override
	public List<WorkBottleVO> getWorkBottleListOfCustomerToday(Integer customerId) {
		
		return workMapper.selectWorkBottleListOfCustomerToday(customerId);
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
	public int modifyWorkReportEtc(WorkBottleVO param) {
		return workMapper.modifyWorkReportEtc(param);
	}
	
	@Override
	public int modifyWorkBottleEtc(WorkBottleVO param) {
		return workMapper.modifyWorkBottleEtc(param);
	}
	
	public int checkOrder(WorkReportVO param, List<BottleVO> bottleList) {
		
		int result = 0;
		
		//기존 주문이 있는지 여부 확인
		OrderVO order =  null;
				
		//if(param.getAgencyYn().equals("N") || !param.getUserId().equals("factory") ) orderTemp = orderService.getLastOrderForCustomer(param.getCustomerId());
		order = orderService.getLastOrderForCustomer(param.getCustomerId());
		
		if(order != null) {
			
		}
		else result = 1;
		
		return result;
	}
	
	private int registerCashFlow(WorkReportVO param, double receivableAmount) {
	
		int result = 0;
		
		CashFlowVO cashFlow = new CashFlowVO();
		cashFlow.setCustomerId(param.getCustomerId());
		cashFlow.setReceivableAmount(receivableAmount);
		cashFlow.setIncomeWay("");
		if(param.getUserId() != null && param.getUserId().length() > 0)
			cashFlow.setCreateId(param.getUserId());
		else
			cashFlow.setCreateId(param.getCreateId());
		cashFlow.setSearchCreateDt(param.getSearchDt());
		
		result = cashService.registerCashFlow(cashFlow);
				
		return result;
	}

	@Override
	public Map<String,Object> getCustomerWorkReportList(CustomerSalesVO param) {
		
		Map<String, Object> map = new HashMap<String, Object>();	
		List<WorkReportViewVO> viewList = new ArrayList<WorkReportViewVO>();
		List<WorkBottleViewVO> bottleViewList = new ArrayList<WorkBottleViewVO>();
		
		List<WorkBottleVO> workBottleList = workMapper.selectCustomerWorkBottleListAll(param);
		
		List<WorkReportVO> reportList = workMapper.selectCustomerWorkReportOnlyListAll(param);

		for(int i = 0 ; i < reportList.size() ; i++ ) {
			WorkReportViewVO temp = new WorkReportViewVO();
			temp.setWorkReportSeq(reportList.get(i).getWorkReportSeq());
			temp.setReceivedAmount(reportList.get(i).getReceivedAmount());
			temp.setCreateDt(reportList.get(i).getCreateDt());
			
			if(reportList.get(i).getIncomeWay()!=null && reportList.get(i).getIncomeWay().equals("CASH")) 
				temp.setIncomeWay("(현금)");
			else if(reportList.get(i).getIncomeWay()!=null && reportList.get(i).getIncomeWay().equals("CARD")) 
				temp.setIncomeWay("(카드)");
			else
				temp.setIncomeWay("");
			
			temp.setCustomerNm(reportList.get(i).getCustomerNm());
			temp.setTaxinvoiceType(reportList.get(i).getTaxinvoiceType());
			
			List<WorkBottleVO> tempBottle1 = new ArrayList<WorkBottleVO>();
			
			temp.setSalesBottles(tempBottle1);
			viewList.add(temp);			
			
		}		
		
		for(int i = 0 ; i < workBottleList.size() ; i++ ) {
			
			WorkBottleVO workBottle = workBottleList.get(i);
			if(workBottle.getGasId() > 0) {
				if(workBottle.getProductCapa().indexOf("Kg") < 0) workBottle.setProductCapa(workBottle.getProductCapa() +"L");
			}
			if(StringUtils.isTankProduct(workBottle.getProductId()) ){
				workBottle.setProductCount(workBottle.getChargeVolumn());
			}
			for(int j=0; j < viewList.size() ; j++) {
				WorkReportViewVO temp = viewList.get(j);
				
				// Work_Report_Seq 동일
				if((temp.getWorkReportSeq() - workBottle.getWorkReportSeq())==0) {
					
					temp.setCustomerId(workBottle.getCustomerId());
					temp.setCustomerNm(workBottle.getCustomerNm());					
					
					temp.getSalesBottles().add(workBottle);
				}
			}
		}
		
		double totalAmount=0;
		int idx = 0;
		int idxj = 0 ;
		double aggregateAmount=0;
		double aggregateReceviedAmount=0;
		
		for(int i=0;i<viewList.size() ; i++) {
			totalAmount = 0;
			WorkBottleViewVO bottleView = new WorkBottleViewVO();
			
			bottleView.setCreateDt(viewList.get(i).getCreateDt());
			bottleView.setReceivedAmount(viewList.get(i).getReceivedAmount());
			bottleView.setIncomeWay(viewList.get(i).getIncomeWay());

			bottleViewList.add(bottleView);
			idxj = idx;
			idx++;
			
			for(int j=0;j<viewList.get(i).getSalesBottles().size();j++) {
				totalAmount += Math.round(viewList.get(i).getSalesBottles().get(j).getProductPrice()*viewList.get(i).getSalesBottles().get(j).getProductCount()*100)/100.0;
				
				WorkBottleViewVO bottleView1 = new WorkBottleViewVO();
				bottleView1.setProductNm(viewList.get(i).getSalesBottles().get(j).getProductNm());
				bottleView1.setProductCapa(viewList.get(i).getSalesBottles().get(j).getProductCapa());
				bottleView1.setProductCount(viewList.get(i).getSalesBottles().get(j).getProductCount());
				bottleView1.setOrderTotalAmount(viewList.get(i).getSalesBottles().get(j).getProductPrice()*viewList.get(i).getSalesBottles().get(j).getProductCount());
				bottleView1.setBottleWorkCdNm(viewList.get(i).getSalesBottles().get(j).getBottleWorkCdNm());
				bottleView1.setProductPrice(viewList.get(i).getSalesBottles().get(j).getProductPrice());
				
				bottleViewList.add(bottleView1);
				idx++;
			}
		
			bottleViewList.get(idxj).setOrderTotalAmount(totalAmount);
			
			bottleViewList.get(idxj).setReceivableAmount(totalAmount-viewList.get(i).getReceivedAmount());

			aggregateAmount +=totalAmount;
			aggregateReceviedAmount += viewList.get(i).getReceivedAmount();
//			logger.debug(" -totalAmount-viewList.get(i).getReceivableAmount() ==" +(totalAmount-viewList.get(idxj).getReceivedAmount()));
		}
		map.put("bottleViewList", bottleViewList);
		WorkBottleViewVO bottleView = new WorkBottleViewVO();
		bottleView.setProductNm("누계");
		bottleView.setOrderTotalAmount(aggregateAmount);
		bottleView.setReceivedAmount(aggregateReceviedAmount);
		bottleView.setReceivableAmount(aggregateAmount-aggregateReceviedAmount);
	
		map.put("aggredateView", bottleView);
		return map;
	}

	@Override
	public List<WorkBottleVO> deliveredLn2CustomerList(WorkReportVO param) {
		return workMapper.selectDelieveredLn2CustomerList(param);
	}

	@Override
	@Transactional
	public int registerWorkReportHist(WorkReportVO param) {
		int result = 0;
		
		result = workMapper.insertWorkBottleHist(param);
		
//		result = workMapper.deleteWorkBottleOld(param);
				
		return result;
	}
	
	@Override
	public Map<String,Object> getWorkBottleHistListTotal(BottleVO param) {
							  
		logger.debug("****** getWorkBottleHistListTotal *****start===*");	
				
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
		
		String searchChargeDtFrom = null;
		String searchChargeDtEnd = null;
		String searchChargeDt = param.getSearchChargeDt();	
		
		if(searchChargeDt != null && searchChargeDt.length() > 20) {
			map.put("searchChargeDt", searchChargeDt);
			
			searchChargeDtFrom = searchChargeDt.substring(0, 10) ;			
			map.put("searchChargeDtFrom", searchChargeDtFrom);
			
			searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;			
			map.put("searchChargeDtEnd", searchChargeDtEnd);
		}						
		
		int bottleCount = workMapper.selectWorBottleHistCountTotal(map);		
		
		//int lastPage = (int)(Math.ceil(bottleCount/ROW_PER_PAGE));
		int lastPage = (int)((double)bottleCount/ROW_PER_PAGE+0.95);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		if(lastPageNum ==0) lastPageNum=1;
		
		//수정 Start
		int pages = (bottleCount == 0) ? 1 : (int) ((bottleCount - 1) / ROW_PER_PAGE) + 1; // * 정수형이기때문에 소숫점은 표시안됨		
        //int blocks;
        int block;
        //blocks = (int) Math.ceil(1.0 * pages / ROW_PER_PAGE); // *소숫점 반올림
        block = (int) Math.ceil(1.0 * currentPage / ROW_PER_PAGE); // *소숫점 반올림
        startPageNum = (block - 1) * ROW_PER_PAGE + 1;
        lastPageNum = block * ROW_PER_PAGE;        
        
        if (lastPageNum > pages){
        	lastPageNum = pages;
        }
		//수정 end
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<BottleVO> bottleList = workMapper.selectWorBottleHistListTotal(map);
		
		resutlMap.put("list",  bottleList);				
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", startPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", bottleCount);		
		
		return  resutlMap;
	}

}

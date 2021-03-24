package com.gms.web.admin.mapper.manage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.OrderBottleVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;

@Mapper
public interface OrderMapper {

	public List<OrderVO> selectOrderList(Map<String, Object> map);	
	
	public List<OrderVO> selectOrderListToExcel(Map<String, Object> map);	

	public List<OrderProductVO> selectOrderProductList(Integer orderId);	

	public List<OrderProductVO> selectOrderInfoOfNotProduct(Integer orderId);	
	
	public List<OrderProductVO> selectOrderProductListNotDelivery(Integer orderId);	

	public int selectOrderCount(Map<String, Object> map);	

	public List<OrderVO> selectCustomerOrderList(Integer customerId);	
	
	public List<OrderVO> selectSalesOrderList(String customerId);	
	
	public OrderVO selectOrderDetail(Integer orderId) ;		
	
	public OrderVO selectLastOrderForCustomer(Integer customerId) ;		
	
	public int selectOrderId() ;
	
	public int selectNextOrderProductSeq(Integer orderId) ;

	public int insertOrder(OrderVO param);

	public int insertOrderProduct(OrderProductVO param);
	
	public int insertOrderBottle(OrderBottleVO param);
	
	public int insertOrderBottles(List<OrderBottleVO> param);
	
	public int insertOrderProducts(List<OrderProductVO>  param);
	
	public int updateOrder(OrderVO param);
	
	public int updateOrderTotalAmount(OrderVO param);
	
	public int updateOrderProductAmount(OrderProductVO param);
	
	public int updateOrderAdditionBottles(OrderVO param);
	
	public int updateOrderProductCount(OrderProductVO param);
	
	//public int updateOrderProductDeliveryDt(OrderProductVO param);
	
	public int updateOrderBottleId(OrderProductVO param);
	
	public int updateOrderBottle(OrderBottleVO param);
	
	public int updateOrderBottles(List<OrderBottleVO> param);
	
	public int updateOrderProcessCd(OrderVO param);
	
	public int updateOrdersProcessCd(OrderVO param);

	public int deleteOrder(OrderVO param);

	public int deleteOrderProduct(OrderProductVO param);
	
	public int deleteOrderProducts(Integer orderId);
	
	public int deleteOrderBottles(Integer orderId);

	public List<OrderBottleVO> selectOrderBottleList(Integer orderId);	
	
	public List<OrderBottleVO> selectOrderBottleListNotDelivery(Integer orderId);	
	
	public List<OrderBottleVO> selectOrderBottleListOfProduct(OrderProductVO param);	
	
	public int deleteOrderBottle(OrderBottleVO param);
	
	public OrderVO selectOrderTodayOfCustomer(Integer custoerId);	
	
	public List<OrderVO> selectOrderReqDtTomorrow(OrderVO param);	
	
	public int deleteOrderProductByProduct(OrderProductVO param);
	
	public int deleteOrderBottlesByProduct(OrderProductVO param);
}

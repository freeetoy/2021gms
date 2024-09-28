package com.gms.web.admin.mapper.manage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.CustomerBottleVO;
import com.gms.web.admin.domain.manage.CustomerLn2AlarmVO;
import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.CustomerSalesVO;
import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;

@Mapper
public interface CustomerMapper {
	
	public List<CustomerVO> selectCustomerList(Map<String, Object> map);	
	
	public List<CustomerVO> searchCustomerList(String searchCustomerNm);
	
	public List<CustomerVO> searchCustomerListExcel(String searchCustomerNm);
	
	public List<CustomerVO> selectCustomerListCar();
	
	public int selectCustomerCount(Map<String, Object> map);	
	
	public int selectBusinessRegId(CustomerVO param);	
	
	public CustomerVO selectCustomerDetail(Integer customerId) ;
	
	public CustomerVO selectCustomerDetailByNm(String customerNm) ;
	
	public CustomerVO selectCustomerDetailByNmBusi(CustomerVO param ) ;

	public int insertCustomer(CustomerVO param);
	
	public int insertCustomers(List<CustomerVO> param);
	
	public int updateCustomer(CustomerVO param);
	
	public int updateCustomerExcel(CustomerVO param);
	
	public int updateCustomerStatus(CustomerVO param);

	public int deleteCustomer(CustomerVO param);	
	
	public int updateCustomerBottleCount(CustomerVO param);
	
	public int updateCustomerBottleRentCount(CustomerVO param);
		
	public int insertCustomerPrice(CustomerPriceVO param);
	
	public int insertCustomerPrices(List<CustomerPriceVO> param);
	
	public int deleteCustomerPrice(Integer customerId);	
	
	public int deleteCustomerPrices(List<CustomerPriceVO> param);	
	
	public List<CustomerPriceExtVO> selectCustomerPriceList(Integer customerId);
	
	public List<CustomerPriceVO> selectCustomerPriceListAll();
	
	public List<CustomerPriceVO> selectCustomerProductPriceList(Integer productId);
	
	public List<CustomerPriceVO> selectCustomerPriceListAllNow();
	
	public List<CustomerSimpleVO> searchCustomerSimpleList(String searchCustomerNm);
	
	public List<CustomerSimpleVO> selectAgencyCustomerList();
	
	public String searchCustomerSimpleListString(String searchCustomerNm);
	
	public List<CustomerSimpleVO> selectCarSimpleList(String carYn);	

	public int updateCustomerPrice(CustomerPriceVO param);
	
	public CustomerVO selectCustomerCar(String userId) ;
	
	public CustomerProductVO selectCustomerProduct(CustomerProductVO param) ;
	
	public List<CustomerProductVO> selectCustomerProductList(Integer customerId);
	
	public int insertCustomerProduct(CustomerProductVO param);
	
	public int insertCustomerProducts(List<CustomerProductVO> params);
	
	public int deleteCustomerProduct(Integer customerId);
	
	public int updateCustomerProductOwnCount(CustomerProductVO param);
	
	public int updateCustomerProductRentCount(CustomerProductVO param);	
	
	public int insertCustomerBottle(CustomerBottleVO param);
	
	public int insertCustomerBottles(List<CustomerBottleVO> param);
	
	public List<CustomerBottleVO> selectCustomerBottleList(Integer customerId);
	
	public CustomerPriceVO selectCustomerLn2Capa(WorkBottleVO param);
	
	public List<CustomerSalesVO> selectCustomerSalesList(Map<String, Object> map);	
	
	public List<CustomerLn2AlarmVO> selectLn2CustomerList(String salesId);
	
	public int updateCustomerLn2WorkDt(CustomerLn2AlarmVO param);
	
	public int updateCustomerLn2(CustomerLn2AlarmVO param);
	
	public CustomerLn2AlarmVO selectCustomerLn2(Integer customerId);
	
	public int updateCustomerLnsWorkDts(List<CustomerLn2AlarmVO> param);
	
	public List<CustomerLn2AlarmVO> selectCustomerLn2ListToday();
	
	public List<CustomerProductVO> selectRecentOrderList(Map<String, Object> map);
	
}

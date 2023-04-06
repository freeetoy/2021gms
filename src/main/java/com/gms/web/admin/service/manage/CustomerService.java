package com.gms.web.admin.service.manage;

import java.util.List;
import java.util.Map;

import com.gms.web.admin.domain.manage.CustomerBottleVO;
import com.gms.web.admin.domain.manage.CustomerLn2AlarmVO;
import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.CustomerSalesVO;
import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.statistics.StatisticsCustomerVO;

public interface CustomerService {

	public Map<String,Object> getCustomerList(CustomerVO param);
	
	public Map<String,Object> searchCustomerList(String param);
	
	public List<CustomerVO> searchCustomerListExcel(String param);
	
	public List<CustomerVO> searchCustomerListCar();
	
	public List<CustomerSimpleVO> searchCustomerSimpleList(String customerNm);
	
	public List<CustomerSimpleVO> getAgencyCustomerList();
	
	public String searchCustomerSimpleListString(String customerNm);

	public List<CustomerSimpleVO> getCarSimpleList(String carYn);
	
	public CustomerVO getCustomerDetails(Integer customerId);
	
	public CustomerVO getCustomerDetailsByNm(String customerNm);
	
	public CustomerVO getCustomerDetailsByNmBusi(CustomerVO param);

	public boolean registerCustomer(CustomerVO param);	
	
	public int registerCustomers(List<CustomerVO> param);	
	
	public boolean modifyCustomer(CustomerVO param);
	
	public boolean modifyCustomerExcel(CustomerVO param);
	
	public boolean modifyCustomerStatus(CustomerVO param);
	
	public int deleteCustomer(CustomerVO param);
	
	public int modifyCustomerBottleCount(CustomerVO param);
	
	public int modifyCustomerBottleRentCount(CustomerVO param);

	public Map<String, Object> checkBusinessRegIdDuplicate(CustomerVO param);
	
	public List<CustomerPriceExtVO>  getCustomerPriceList(Integer customerId);
	
	public boolean registerCustomerPrice(CustomerPriceVO[] param);
	
	public int registerCustomerPrices(List<CustomerPriceVO> param);	
	
	public boolean deleteCustomerPrice(Integer customerId);
	
	public int deleteCustomerPrices(List<CustomerPriceVO> param);
	
	public List<CustomerPriceVO>  getCustomerPriceListAll();
	
	public List<CustomerPriceVO>  getCustomerProductPriceList(Integer productId);
	
	public int modifyCustomerPrice(CustomerPriceVO param);
	
	public CustomerProductVO getCustomerProduct(CustomerProductVO param);
	
	public List<CustomerProductVO> getCustomerProductList(Integer customerId);
	
	public int registerCustomerProduct(CustomerProductVO param);	
	
	public int registerCustomerProducts(List<CustomerProductVO> params);	
	
	public int deleteCustomerProducts(Integer customerId);
	
	public int modifyCustomerProductOwnCount(CustomerProductVO param);
	
	public int modifyCustomerProductRentCount(CustomerProductVO param);
	
	public int registerCustomerBottle(CustomerBottleVO param);	
	
	public int registerCustomerBottles(List<CustomerBottleVO> param);	
	
	public List<CustomerBottleVO> getCustomerBottleList(Integer customerId);
	
	public List<CustomerPriceVO>  getCustomerPriceListAllNow();
	
	public CustomerPriceVO  getCustomerLn2Capa(WorkBottleVO param);
	
	public List<CustomerSalesVO> getCustomerSalesList(CustomerSalesVO param);	
	
	public List<CustomerLn2AlarmVO> getLn2CustomerList(String salesId);
	
	public int modifyCustomerLn2WorkDt(CustomerLn2AlarmVO param);
	
	public int modifyCustomerLn2(CustomerLn2AlarmVO param);
	
	public CustomerLn2AlarmVO getCustomerLn2(Integer customerId);
}

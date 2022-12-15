package com.gms.web.admin.service.common;

import java.util.List;

import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.SimpleBottleVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;

public interface ApiService {
	
	public int registerWorkReportForSale(WorkReportVO param);
		
	public int registerWorkReportForChangeCd(WorkReportVO param);

	public int registerWorkReportNoGas(WorkBottleVO param);
	
	public int registerCashFlow(CashFlowVO param);
	
	public List<SimpleBottleVO> getCustomerSimpleBottleList(String customerNm);	
	
	public int registerWorkReportGasAndBottle(WorkReportVO param);
	
	public List<WorkBottleVO> getWorkReportList(WorkReportVO param);
	
	public int registerWorkReportMassForSale(WorkReportVO param);
	
	public int registerWorkReportMassForChangeCd(WorkReportVO param);
	
	public List<BottleVO> getDummyBottleList();
	
	public String getAppVersion();
	
	public List<ProductPriceSimpleVO> getCustomerLn2List();
	
	public List<CustomerProductVO> getCustomerBottleList(String customerNm);
	
	public int deleteWorkBottle(WorkBottleVO param);
	
	public int registerWorkReportTank(WorkBottleVO param);
	
	public int registerOrder(OrderVO param);
	
	public List<OrderVO> getOrderList(OrderVO param);
	
	public List<OrderProductVO> getOrderProductList(OrderVO param);
	
	public int deleteOrder(OrderVO param);
}

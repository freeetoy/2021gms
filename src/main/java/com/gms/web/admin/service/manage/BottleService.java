package com.gms.web.admin.service.manage;

import java.util.List;
import java.util.Map;

import com.gms.web.admin.domain.manage.BottleHistoryVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.OrderBottleVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.ProductVO;
import com.gms.web.admin.domain.manage.SimpleBottleVO;

public interface BottleService {

	public Map<String,Object> getBottleList(BottleVO params);	
	
	public List<BottleVO> getBottleListAll();
	
	public List<BottleVO> getBottleListToExcel(BottleVO params);
	
	public List<BottleVO> getCustomerBottleList(Integer customerId);
	
	public List<BottleVO> getCustomerBottleListDate(BottleVO param);
	
	public BottleVO getCustomerBottleRecent(Integer customerId);
	
	public List<SimpleBottleVO> getCustomerSimpleBottleList(Integer customerId) ;
	
	public BottleVO getBottleDetail(String bottleId) ;
	
	public BottleVO getBottleDetailForBarCd(String bottleBarCd) ;
	
	public List<BottleVO> getBottleDetails(BottleVO param) ;
	
	public List<BottleVO> getBottleListByBarCds(BottleVO param) ;
	
	public int getBottleCount(Map<String, Object> map);	
	
	public int getBottleIdCheck(BottleVO param);	

	public int getBottleBarCdCheck(BottleVO param);	
	
	public int registerBottle(BottleVO param);	
	
	public int registerBottles(List<BottleVO> param);	

	public int modifyBottle(BottleVO param);
	
	public int modifyBottleList(List<BottleVO> param);
	
	public int modifyBottleOrder(BottleVO param);
	
	public int modifyBottleOrderV2(BottleVO param);
	
	public int modifyBottlesOrder(List<BottleVO> params);
	
	public int changeBottleWorkCd(BottleVO param);
	
	public int changeBottlesWorkCd(BottleVO param);
	
	public int changeWorkCdsAndHistory(BottleVO param,List<BottleVO> params);
	
	public int changeBottlesWorkCdOnly(BottleVO param);

	public int deleteBottle(BottleVO param);
	
	public int deleteBottles(BottleVO param);
	
	public int deleteCustomerIdOfBottle(BottleVO param);
	
	public Map<String, Object> checkBottleIdDuplicate(BottleVO param);
	
	public List<BottleHistoryVO> selectBottleHistoryList(String bottleId);
	
	public BottleVO getLastBottleHist(String bottleId);
	
	public List<BottleVO> getDummyBottleList();
	
	public List<BottleVO> getSimpleDummyBottleList();
	
	public BottleVO getDummyBottle(BottleVO param);
	
	public int modifyBottleAfterDelete(OrderBottleVO param);
	
	public int registerBottlesHistory(List<BottleVO> param);	
	
	public int deleteProductDummyBottle(ProductVO param);
}

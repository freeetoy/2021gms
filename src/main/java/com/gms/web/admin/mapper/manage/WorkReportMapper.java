package com.gms.web.admin.mapper.manage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CarInventoryVO;
import com.gms.web.admin.domain.manage.CustomerSalesVO;
import com.gms.web.admin.domain.manage.WorkBottleRegisterVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;

@Mapper	
public interface WorkReportMapper {
	
	public WorkReportVO selectWorkReport(Integer workReportSeq);	
	
	public WorkReportVO selectWorkReportOnly(Integer workReportSeq);	
	
	public List<WorkReportVO> selectWorkReportList(WorkReportVO param);	
	
	public List<WorkReportVO> selectWorkReportOnlyList(WorkReportVO param);	
	
	public List<WorkReportVO> selectWorkReportOnlyListAll(WorkReportVO param);	
	
	public List<WorkBottleVO> selectWorkReportList1(WorkReportVO param);	
	
	public List<WorkBottleVO> selectWorkReportListAll(WorkReportVO param);	
	
	public List<WorkBottleVO> selectWorkBottleList(Integer workReportSeq);	
	
	public List<WorkBottleVO> selectWorkBottleListOfOrder(Integer orderId);	
	
	public List<WorkBottleRegisterVO> selectWorkBottleListAndCountOfOrder(Integer orderId);	
	
	public List<BottleVO> selectWorBottleListTotal(Map<String, Object> map);	
	
	public List<BottleVO> selectWorBottleListToday(BottleVO params);	
	
	public int selectWorBottleCountTotal(Map<String, Object> map);	
	
	public int selectWorkReportSeq() ;
	
	public int selectWorkBottleSeq(int workReportSeq) ;
	
	public int selectWorkReportSeqForCustomerToday(WorkReportVO param) ;
	
	public int selectWorkReportSeqForCustomer(WorkReportVO param) ;
	
	public int insertWorkReport(WorkReportVO param);
	
	public int insertWorkBottle(WorkBottleVO param);
	
	public int insertWorkBottles(List<WorkBottleVO> param);	
	
	public int modifyWorkReportProduct(WorkReportVO param);
	
	public int modifyWorkReportOrderId(WorkReportVO param);
	
	public int deleteWorkReport(WorkReportVO param);
	
	public int updateWorkReportReceivedAmount(WorkReportVO param);
	
	public int updateWorkBottlePrice(WorkBottleVO param);
	
	public List<WorkBottleVO> selectWorkBottleListOfProduct(WorkBottleVO params);	
	
	public int deleteWorkBottleProduct(WorkBottleVO params);
	
	public int deleteWorkBottleOfProduct(WorkBottleVO params);
	
	public int deleteWorkBottle(Integer workReportSeq);
	
	public int deleteWorkBottleProductWorkCd(WorkBottleVO param);
	
	public List<WorkBottleVO> selectWorkBottleListOfWorkBottleCd(WorkBottleVO params);	
	
	public List<WorkBottleVO> selectWorkBottleListOfCustomerToday(Integer customerId);	
	
	public int updateWorkReportOrder(WorkReportVO param);
	
	public int updateWorkReportNoOrder(WorkReportVO param);
	
	public int modifyWorkReportEtc(WorkBottleVO param);
	
	public int selectWorkBottleSeqTcharge(int workReportSeq) ;
	
	public int modifyWorkBottleCharge(WorkBottleVO param);
	
	public List<WorkBottleVO> selectCustomerWorkBottleListAll(CustomerSalesVO param);	
	
	public List<WorkReportVO> selectCustomerWorkReportOnlyListAll(CustomerSalesVO param);
	
//	public List<CarInventoryVO> selectCarInventoryList(WorkReportVO param);	
}

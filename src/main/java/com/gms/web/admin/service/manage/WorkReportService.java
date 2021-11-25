package com.gms.web.admin.service.manage;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.WorkBottleRegisterVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;

public interface WorkReportService {
	
	public WorkReportVO getWorkReport(Integer workReportSeq);	

	public List<WorkReportVO> getWorkReportList(WorkReportVO param);	
	
	//public List<WorkReportViewVO> getWorkReportList1(WorkReportVO param);	
	
	public List<WorkReportViewVO> getWorkReportListAll(WorkReportVO param);	
	
	public List<WorkBottleVO> getWorkBottleList(Integer workReportSeq);	
	
	public List<WorkBottleVO> getWorkBottleListOfOrder(Integer orderId);	
	
	public List<WorkBottleRegisterVO> getWorkBottleListAndCountOfOrder(Integer orderId);	
	
	public Map<String,Object> getWorkBottleListTotal(BottleVO param);	
	
	public List<BottleVO> getWorkBottleListToday(BottleVO param);	
	
	public int getWorkReportSeq() ;
	
	public int getWorkReportSeqForCustomerToday(WorkReportVO param) ;
	
	public int getWorkReportSeqForCustomer(WorkReportVO param) ;
	
	public int registerWorkReportByBottle(WorkReportVO param, List<BottleVO> bottleList);
	
	public int registerWorkReportForOrder(WorkReportVO param);
	
	public int registerWorkReportNoOrder(WorkReportVO param);
	
	public int registerWorkReport0310(WorkReportVO param);
	
	public int registerWorkBottle(WorkBottleVO param);
	
	public int registerWorkBottleList(List<WorkBottleVO> param);
	
	public int registerWorkNoBottle(WorkBottleVO param);
	
	public int modifyWorkReportReceivedAmount(WorkReportVO param);
	
	public int registerWorkReportOnly(WorkReportVO param);
	
	public int modifyWorkBottlePrice(WorkBottleVO param);
	
	//public int registerWorkGasAndBottle(WorkBottleVO param);
	public int modifyWorkBottleManual(HttpServletRequest request, WorkReportVO param);	
	
	public List<WorkBottleVO> getWorkBottleListOfUser(WorkReportVO param);		
	
	public int registerWorkReportMassNoOrder(WorkReportVO param);
	
	public int registerWorkReportMassByBottle(WorkReportVO param);
	
	public int deleteWorkReport(WorkReportVO param);
	
	public int deleteWorkReportAndBottle(WorkReportVO report,WorkBottleVO workBottle);
	
	public int registerWorkBottleChargeTank(WorkBottleVO param);
	
	public List<WorkBottleVO> getWorkBottleListOfCustomerToday(Integer customerId);	
	
	public int modifyWorkReportEtc(WorkBottleVO param);
}

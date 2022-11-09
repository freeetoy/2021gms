package com.gms.web.admin.service.manage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.gms.web.admin.domain.manage.CarInventoryVO;
import com.gms.web.admin.domain.manage.WorkReportVO;

public interface CarInventoryService {
	
	public int registerCarInventory(CarInventoryVO param);
	
	public int registerCarInventories(CarInventoryVO param);
	
	public int registerCarInventories(List<CarInventoryVO> param);	
	
	public int registerCarInventoriesScheduler();	
	
	public int modifyCarInventory(CarInventoryVO param);
	
	public int modifyCarInventories(List<CarInventoryVO> param);
	
	public int modifyCarInventoriesInCar(List<CarInventoryVO> param);
	
	public int modifyCarInventoriesYesterDay(List<CarInventoryVO> param);
	
	public List<CarInventoryVO> getCarInventoryList(WorkReportVO param);
	
	public List<CarInventoryVO> getDayCarInventoryList(CarInventoryVO param);
	
	public List<CarInventoryVO> getCarInventoryDayList(WorkReportVO param);
	
	public int modifyCarInventoriesManual(HttpServletRequest request, WorkReportVO param);
}

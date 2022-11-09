package com.gms.web.admin.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.CarInventoryVO;
import com.gms.web.admin.domain.manage.WorkReportVO;

@Mapper	
public interface CarInventoryMapper {
	
	public List<CarInventoryVO> selectCarInventoryList(WorkReportVO param);
	
	public int insertCarInventory(CarInventoryVO param);
	
	public int insertCarInventories(List<CarInventoryVO> param);	
	
	public int insertCarInventoriesDay(CarInventoryVO param);
	
	public int insertCarInventoriesDayScheduler();
	
	public int updateCarInventory(CarInventoryVO param);
	
	public int updateCarInventories(List<CarInventoryVO> param);
	
	public int addCarInventories(List<CarInventoryVO> param);
	
	public int updateCarInventoriesYesterDay(List<CarInventoryVO> param);
	
	public List<CarInventoryVO> selectDayCarInventoryList(CarInventoryVO param);
	
	public List<CarInventoryVO> selectCarInventoryDayList(WorkReportVO param);
	
	public int updateCarInventoryAfterInCar(CarInventoryVO param);
	

}

package com.gms.web.admin.service.manage;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.domain.manage.CarInventoryVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.mapper.manage.CarInventoryMapper;

@Service
public class CarInventoryServiceImpl implements CarInventoryService {

private final Logger logger = LoggerFactory.getLogger(getClass());	 
	
	@Autowired
	private CarInventoryMapper inventoryMapper;
	
	@Override
	public int registerCarInventory(CarInventoryVO param) {
		return inventoryMapper.insertCarInventory(param);
	}

	@Override
	public int registerCarInventories(List<CarInventoryVO> param) {
		return inventoryMapper.insertCarInventories(param);
	}

	@Override
	public int registerCarInventories(CarInventoryVO param) {
		return inventoryMapper.insertCarInventoriesDay(param);
	}
	
	@Override
	public int modifyCarInventory(CarInventoryVO param) {
		return inventoryMapper.updateCarInventory(param);
	}

	@Override
	public int modifyCarInventories(List<CarInventoryVO> param) {
		return inventoryMapper.updateCarInventories(param);
	}

	@Override
	public int modifyCarInventoriesInCar(List<CarInventoryVO> param) {
		logger.debug("*********** CarInventoryServiceImpl modifyCarInventoriesInCar start =" );
		
		List<CarInventoryVO> insertList = new ArrayList<CarInventoryVO>();
		List<CarInventoryVO> updateList = new ArrayList<CarInventoryVO>();
		
		CarInventoryVO paramInventory = param.get(0);
		if(paramInventory.getInventoryDt() == null) paramInventory.setInventoryDt(DateUtils.getDate("yyyy/MM/dd"));
		List<CarInventoryVO> regedList = getDayCarInventoryList(paramInventory) ;
		
		for(int i = 0 ; i < param.size() ; i++) {
			boolean checkYn = false;
			for(int j = 0 ; j < regedList.size() ; j++) {
				if(param.get(i).getProductId().equals(regedList.get(j).getProductId()) 
						&& param.get(i).getProductPriceSeq().equals(regedList.get(j).getProductPriceSeq()) ) checkYn = true;
			}
			if(checkYn) updateList.add(param.get(i));
			else insertList.add(param.get(i));
		}
		int result = 0;
		if(insertList.size() > 0)
			result =  inventoryMapper.insertCarInventories(insertList);
		if(updateList.size() > 0)
			result =  inventoryMapper.updateCarInventories(updateList);
		
		return result;
	}
	
	@Override
	public int modifyCarInventoriesYesterDay(List<CarInventoryVO> param) {
		logger.debug("*********** CarInventoryServiceImpl modifyCarInventoriesYesterDay start =" );
		
		List<CarInventoryVO> insertList = new ArrayList<CarInventoryVO>();
		List<CarInventoryVO> updateList = new ArrayList<CarInventoryVO>();
		
		CarInventoryVO paramInventory = param.get(0);
		if(paramInventory.getInventoryDt() == null) paramInventory.setInventoryDt(DateUtils.getNextDate(-1, "yyyy/MM/dd"));
		List<CarInventoryVO> regedList = getDayCarInventoryList(paramInventory) ;
		
		for(int i = 0 ; i < param.size() ; i++) {
			boolean checkYn = false;
			for(int j = 0 ; j < regedList.size() ; j++) {
				if(param.get(i).getProductId().equals(regedList.get(j).getProductId()) 
						&& param.get(i).getProductPriceSeq().equals(regedList.get(j).getProductPriceSeq()) ) checkYn = true;
			}
			if(checkYn) updateList.add(param.get(i));
			else insertList.add(param.get(i));
		}
		int result = 0;
		if(insertList.size() > 0)
			result =  inventoryMapper.insertCarInventories(insertList);
		if(updateList.size() > 0)
			result =  inventoryMapper.updateCarInventoriesYesterDay(updateList);
		
		return result;
	}

	
	@Override
	public List<CarInventoryVO> getCarInventoryList(WorkReportVO param) {
		return inventoryMapper.selectCarInventoryList(param);
	}

	@Override
	public List<CarInventoryVO> getDayCarInventoryList(CarInventoryVO param) {
		return inventoryMapper.selectDayCarInventoryList(param);
	}


	@Override
	public int registerCarInventoriesScheduler() {
		return inventoryMapper.insertCarInventoriesDayScheduler();
	}

	

}

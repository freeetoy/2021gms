package com.gms.web.admin.service.manage;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.CarInventoryVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
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
		int result = 0;
		List<CarInventoryVO> insertList = new ArrayList<CarInventoryVO>();
		List<CarInventoryVO> updateList = new ArrayList<CarInventoryVO>();
		
		CarInventoryVO paramInventory = param.get(0);
		if(paramInventory.getInventoryDt() == null) paramInventory.setInventoryDt(DateUtils.getDate("yyyy/MM/dd"));
		List<CarInventoryVO> regedList = getDayCarInventoryList(paramInventory) ;
		String inventoryDate = paramInventory.getInventoryDt() ;
		if(regedList.size() <= 0 ) {
			paramInventory.setInventoryDt(DateUtils.getNextDate(-1,"yyyy/MM/dd"));
			result = inventoryMapper.insertCarInventoriesDay(paramInventory);
			paramInventory.setInventoryDt(inventoryDate);
			result =  inventoryMapper.addCarInventories(param);
		}else {
		
			for(int i = 0 ; i < param.size() ; i++) {
				boolean checkYn = false;
				for(int j = 0 ; j < regedList.size() ; j++) {
					if(param.get(i).getProductId().equals(regedList.get(j).getProductId()) 
							&& param.get(i).getProductPriceSeq().equals(regedList.get(j).getProductPriceSeq()) ) checkYn = true;
				}
				if(checkYn) updateList.add(param.get(i));
				else insertList.add(param.get(i));
			}
			
			if(insertList.size() > 0)
				result =  inventoryMapper.insertCarInventories(insertList);
			if(updateList.size() > 0)
				result =  inventoryMapper.addCarInventories(updateList);
		}
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

	@Override
	public List<CarInventoryVO> getCarInventoryDayList(WorkReportVO param) {
		return inventoryMapper.selectCarInventoryDayList(param);
	}

	@Override
	public int modifyCarInventoriesManual(HttpServletRequest request, WorkReportVO param) {
		logger.debug("*********** CarInventoryServiceImpl modifyCarInventoriesManual start =" );
		int result = 0;
		
		try {
			int productCount = 0 ;
			if(request.getParameter("productCount") !=null) productCount = Integer.parseInt(request.getParameter("productCount"));
			
			List<CarInventoryVO> insertList = new ArrayList<CarInventoryVO>();
			List<CarInventoryVO> updateList = new ArrayList<CarInventoryVO>();
			
			List<CarInventoryVO> paramList = new ArrayList<CarInventoryVO>();
			
			for(int i = 0 ; i < productCount ; i++) {
				CarInventoryVO carInventory = new CarInventoryVO();
				RequestUtils.initUserPrgmInfo(request, carInventory);
				carInventory.setCustomerId(param.getCustomerId());
				carInventory.setInventoryDt(param.getSearchStatDt());
				
				if(request.getParameter("productId_"+i) != null) {
					carInventory.setProductId(Integer.parseInt(request.getParameter("productId_"+i)) );
					carInventory.setProductPriceSeq(Integer.parseInt(request.getParameter("productPriceSeq_"+i)) );
					carInventory.setFullCnt(Integer.parseInt(request.getParameter("fullCnt_"+i)) );
					carInventory.setEmptyCnt(Integer.parseInt(request.getParameter("emptyCnt_"+i)) );
				}
				paramList.add(carInventory); 
			}
			logger.debug("*********** CarInventoryServiceImpl modifyCarInventoriesManual paramList.size ="+paramList.size() );
			List<CarInventoryVO> oldInventoryList =  getCarInventoryDayList(param);
			logger.debug("*********** CarInventoryServiceImpl modifyCarInventoriesManual oldInventoryList.size ="+oldInventoryList.size() );
			
			for(int i = 0 ; i < paramList.size() ; i++) {
				boolean isExist = false;
				for(int j = 0 ; j < oldInventoryList.size() ; j++) {
					if(paramList.get(i).getProductId().equals(oldInventoryList.get(j).getProductId()) 
							&& paramList.get(i).getProductPriceSeq().equals(oldInventoryList.get(j).getProductPriceSeq()) )
						isExist = true;
				}
				if(isExist) updateList.add(paramList.get(i));
				else insertList.add(paramList.get(i));
			}
			logger.debug("*********** CarInventoryServiceImpl modifyCarInventoriesManual updateList.size ="+updateList.size() );
			logger.debug("*********** CarInventoryServiceImpl modifyCarInventoriesManual insertList.size ="+insertList.size() );
			
			if(insertList.size() > 0)
				result =  inventoryMapper.insertCarInventories(insertList);
			if(updateList.size() > 0)
				result =  inventoryMapper.updateCarInventories(updateList);
//			result = 1;
			
		} catch (DataAccessException de) {		
			de.printStackTrace();
			logger.error("CarInventoryServiceImpl modifyCarInventoriesManual DataAccessException ", de.toString());
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("CarInventoryServiceImpl modifyCarInventoriesManual Exception ", e.toString());
		}
		return result;
	}

	

}

package com.gms.web.admin.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.domain.manage.CarInventoryVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.service.manage.CarInventoryService;
import com.gms.web.admin.service.statistics.StatisticsAgencyService;
import com.gms.web.admin.service.statistics.StatisticsBottleService;
import com.gms.web.admin.service.statistics.StatisticsCustomerService;
import com.gms.web.admin.service.statistics.StatisticsOrderService;
import com.gms.web.admin.service.statistics.StatisticsProductService;
import com.gms.web.admin.service.statistics.StatisticsUserService;

@Service
public class SchedulerServiceImpl implements SchedulerService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatisticsOrderService statOrderService;
	
	@Autowired
	private StatisticsProductService statProductService;
	
	@Autowired
	private StatisticsCustomerService statCustomerService;
	
	@Autowired
	private StatisticsBottleService statBottleService;
	
	@Autowired
	private StatisticsUserService statUserService;

	@Autowired
	private StatisticsAgencyService statAgencyService;
	
	@Autowired
	private CarInventoryService inventoryService;
	
	@Override
	@Transactional
	public int registerDailyStatistics() {

		int result = 0;
		try {
			
			//TB_Daily_Statistics_Sales
			result = statOrderService.registerDailyStatisticsSales();
			
			// TB_Daily_Statistics_Product
			result = statProductService.registerDailyStatisticsProduct();
					
			// TB_Daily_Statistics_Customer
			result = statCustomerService.registerDailyStatisticsCustomer();
			
			result  = statBottleService.registerDailyStatisticsBottle();
			
			result  = statUserService.registerDailyStatisticsUser();
			
			result  = statAgencyService.registerDailyStatisticsAgency();
			
			//TB_Daily_Statistics_Order
			result = statOrderService.registerDailyStatisticsOrder();
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return result ;
	}

	@Override
	@Transactional
	public int registerMonthlyStatistics() {
		int result = 0;
		try {
			//TB_Daily_Statistics_Order
			result = statOrderService.registerMonthlyStatisticsOrder();
			
			//TB_Daily_Statistics_Sales
			result = statOrderService.registerMonthlyStatisticsSales();
			
			// TB_Daily_Statistics_Product
			result = statProductService.registerMonthlyStatisticsProduct();
					
			// TB_Daily_Statistics_Customer
			result = statCustomerService.registerMonthlyStatisticsCustomer();
			
			result  = statBottleService.registerMonthlyStatisticsBottle();
			
			result  = statAgencyService.registerMonthlyStatisticsAgency();
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return result ;
	}

	@Override
	public int registerCarInventoryEwha() {
		
		return inventoryService.registerCarInventoriesScheduler();
	}

}

package com.gms.web.admin.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.service.common.SchedulerService;
import com.gms.web.admin.service.manage.OrderService;

@Controller
public class SchedulerController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SchedulerService scheduleService;
	
	@Autowired
	private OrderService orderService;
	
	@Scheduled(cron="0 00 01 * * *")
	private void scheduleDaily() { 
		
		//Daiyl 통계 데이타 등록@Scheduled(cron="0 00 21 * * *")
		//statOrderService.
		logger.info("************* ScheduleController scheduleDaily Start *************");
		
		int result = scheduleService.registerDailyStatistics();				
		logger.info("******************* ScheduleController scheduleDaily End*************** ");		
	}
	
	 
	@Scheduled(cron="0 30 01 1 * *")
	private void scheduleMonthly() { 
		logger.info("************* ScheduleController scheduleMonthly Start *************");
		
		int result = scheduleService.registerMonthlyStatistics();	
		logger.info("************* ScheduleController scheduleMonthly end *************");
		
	}
	
	@Scheduled(cron="0 50 23 * * *")
	private void modifyOrderProcessCd0250() { 
		logger.info("************* ScheduleController scheduleMonthly Start *************");
		
		int result = orderService.modifyOrderProcessCd0250();	
		logger.info("************* ScheduleController scheduleMonthly end *************");
		
	}
	
	@Scheduled(cron="0 50 01 1 * *")
	private void registerWorkBottleHist() { 
		logger.info("************* ScheduleController registerWorkBottleHist Start *************");
		
		int result = scheduleService.registerWorkBottleHist();	
		logger.info("************* ScheduleController registerWorkBottleHist end *************");
		
	}
	
	@RequestMapping(value = "/gms/statistics/scheduleDailyManual.do")
	private ModelAndView scheduleDailyManual(HttpServletRequest request, HttpServletResponse response) { 
		
		logger.info("************* ScheduleController scheduleDailyManual Start *************");

		int result = scheduleService.registerDailyStatistics();				
		logger.info("******************* ScheduleController scheduleDailyManual End*************** ");		
		if(result > 0){
			String alertMessage = "통계를 등록하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,"/gms/start");
		}
		return null;
	}
	
}

package com.gms.web.admin.controller.common;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.service.common.SchedulerService;

@Controller
public class SchedulerController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SchedulerService scheduleService;
	
	@Scheduled(cron="0 00 03 * * *")
	private void scheduleDaily() { 
		
		//Daiyl 통계 데이타 등록@Scheduled(cron="0 00 21 * * *")
		//statOrderService.
		logger.info("************* ScheduleController scheduleDaily Start *************");
		String now = DateUtils.getDate();
		logger.info("************* ScheduleController scheduleDaily now ="+now);
		Calendar cal = Calendar.getInstance();
		logger.info("************* ScheduleController scheduleDaily cal.getTime() ="+cal.getTime());
		
		int result = scheduleService.registerDailyStatistics();		
		
		logger.info("******************* ScheduleController scheduleDaily End*************** ");		
	}
	
	 
	@Scheduled(cron="0 30 03 1 * *")
	private void scheduleMonthly() { 
		logger.info("************* ScheduleController scheduleMonthly Start *************");
		int result = scheduleService.registerMonthlyStatistics();	
		logger.info("************* ScheduleController scheduleMonthly end *************");
		
	}
	
}

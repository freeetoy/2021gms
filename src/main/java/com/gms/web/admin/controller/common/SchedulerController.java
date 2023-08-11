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

import com.gms.web.admin.GmsApplication;
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
		
		//Daiyl 통계 데이타 등록@Scheduled(cron="0 00 01 * * *")	매일 1시 정각
		//statOrderService. 초 (0-59) 분 (0-59) 시 (0-23) 일 (1-31) 월 (1-12 또는 JAN-DEC) 요일 (0-7 또는 SUN-SAT, 0과 7은 SUN과 같음)

		logger.info("************* ScheduleController scheduleDaily Start *************");
		
		if(GmsApplication.schedulerParam != null & GmsApplication.schedulerParam.equals("yes")) {
			int result = scheduleService.registerDailyStatistics();				
		}
		logger.info("******************* ScheduleController scheduleDaily End*************** ");		
	}
	
	@Scheduled(cron="0 10 01 * * *")
	private void scheduleDaily1() { 
		
		//Daiyl 통계 데이타 등록@Scheduled(cron="0 00 01 * * *")	매일 1시 정각
		//statOrderService. 초 (0-59) 분 (0-59) 시 (0-23) 일 (1-31) 월 (1-12 또는 JAN-DEC) 요일 (0-7 또는 SUN-SAT, 0과 7은 SUN과 같음)

		logger.info("************* ScheduleController scheduleDaily1 Start *************");
		
		if(GmsApplication.schedulerParam != null & GmsApplication.schedulerParam.equals("yes")) {
			int result = scheduleService.registerDailyStatistics1();	
		}
		logger.info("******************* ScheduleController scheduleDaily1 End*************** ");		
	}
	 
	@Scheduled(cron="0 30 01 1 * *")
	private void scheduleMonthly() { 
		logger.info("************* ScheduleController scheduleMonthly Start *************"+GmsApplication.schedulerParam);
		if(GmsApplication.schedulerParam != null & GmsApplication.schedulerParam.equals("yes")) {
			int result = scheduleService.registerMonthlyStatistics();	
		}
		logger.info("************* ScheduleController scheduleMonthly end *************");
		
	}
	
	@Scheduled(cron="0 50 23 * * *")
	private void modifyOrderProcessCd0250() { 
		logger.info("************* ScheduleController scheduleMonthly Start *************");
		
		if(GmsApplication.schedulerParam != null & GmsApplication.schedulerParam.equals("yes")) {
			int result = orderService.modifyOrderProcessCd0250();	
		}
		logger.info("************* ScheduleController scheduleMonthly end *************");
		
	}
	// 23시 30분
	@Scheduled(cron="0 30 23 * * *")
	private void modifyCustomerLn2AlramWorkDt() { 
		logger.info("************* ScheduleController modifyCustomerLn2AlramWorkDt Start *************");
		if(GmsApplication.schedulerParam != null & GmsApplication.schedulerParam.equals("yes")) {
			int result = scheduleService.modifyCustomerLn2AlramWorkDt();	
		}
		logger.info("************* ScheduleController modifyCustomerLn2AlramWorkDt end *************");
		
	}
	
	@Scheduled(cron="0 50 01 1 * *")
	private void registerWorkBottleHist() { 
		logger.info("************* ScheduleController registerWorkBottleHist Start *************");
		if(GmsApplication.schedulerParam != null & GmsApplication.schedulerParam.equals("yes")) {
			int result = scheduleService.registerWorkBottleHist();	
		}
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

package com.gms.web.admin.controller.manage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.CarInventoryVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.service.manage.CarInventoryService;

import groovyjarjarpicocli.CommandLine.Model;

@Controller
public class CarInventoryController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CarInventoryService inventoryService;

	@RequestMapping(value = "/gms/report/update1.do")
	public ModelAndView getCarInventoryUpdate(
			HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO param) {

		logger.debug("CarInventoryController getCarInventoryUpdate start ");	
		RequestUtils.initUserPrgmInfo(request, param);				
		
		ModelAndView mav = new ModelAndView();		
		String dayFlag = "Y";
		if("N".equals(request.getParameter("dayFlag"))) {
			LocalDate localDate = LocalDate.parse(param.getSearchDt(),DateTimeFormatter.ofPattern("yyyy/MM/dd"));
			param.setSearchStatDt(localDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
			dayFlag = "N";
		}
			
//		WorkReportVO workReport = workService.getWorkReport(param.getWorkReportSeq());
		logger.debug("CarInventoryController getCarInventoryUpdate param getSearchStatDt= "+param.getSearchStatDt());	
		
		mav.addObject("workReport", param);	 	
		mav.addObject("searchUserId", param.getUserId());
		mav.addObject("dayFlag", dayFlag);	 	
		if(request.getParameter("action") != null) mav.addObject("action", request.getParameter("action"));
		else mav.addObject("action", "update");	 
		
		mav.setViewName("gms/report/update1");
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.diary"));	 	
		
		return mav;
	}	
	
	@RequestMapping(value = "/gms/report/carInventoryList.do")
	@ResponseBody
	public List<CarInventoryVO> getCarInventoryList(WorkReportVO param, Model model)	{	
				
		List<CarInventoryVO> carInventoryList =  inventoryService.getCarInventoryDayList(param);
		
		return carInventoryList;
	}
	
	@RequestMapping(value = "/gms/report/inventoryModify.do")
	public ModelAndView getCarInventoryModify(HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO param) {

		int result = 1;
		String dayFlag = "Y";
		try {			
			//RequestUtils.initUserPrgmInfo(request, param);	
			if(param.getUserId() !=null ) {			
				param.setUserId(param.getUserId());
				param.setCreateId(param.getUserId());
				param.setUpdateId(param.getUserId());
			}else {				
				RequestUtils.initUserPrgmInfo(request, param);	
				param.setUserId(param.getCreateId());
			}
			ModelAndView mav = new ModelAndView();		
			
			if("N".equals(request.getParameter("dayFlag"))) dayFlag = "N";
//			logger.info("getWorkReportModify== workReportSeq="+param.getWorkReportSeq());
			result = inventoryService.modifyCarInventoriesManual(request,param);
			// WorkReport 정보 변경			
			
			mav.addObject("workReport", param);	 	
			mav.addObject("action", "modify");	 	
					
			mav.setViewName("gms/report/update1");
			if(result > 0){
				String alertMessage = "수정되었습니다.";
				RequestUtils.responseWriteException(response, alertMessage,
						"/gms/report/update1.do?searchUserId="+param.getSearchUserId()+"&searchDt="+param.getSearchDt()+"&dayFlag="+dayFlag+"&action=modify");
			}
		} catch (DataAccessException e) {		
			logger.error(" getCarInventoryModify Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {			
			logger.error(" getCarInventoryModify Exception==="+e.toString());
			e.printStackTrace();
		}
		return null;		
	}
}

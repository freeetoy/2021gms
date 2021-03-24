package com.gms.web.admin.controller.manage;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.common.web.utils.SessionUtil;
import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.domain.manage.ScheduleVO;
import com.gms.web.admin.service.manage.ScheduleService;

@Controller
public class ScheduleController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	ScheduleService scheduleService;
	
	
	@RequestMapping(value = "/gms/schedule/list.do")
	public ModelAndView getScheduleList(
			HttpServletRequest request
			, HttpServletResponse response
			, ScheduleVO param) {

		logger.info("ScheduleController getScheduleList");
		
		ModelAndView mav = new ModelAndView();	
		RequestUtils.initUserPrgmInfo(request, param);		
		
		param.setUserId(param.getCreateId());
		
		int startYear = 2020;
		int endYear = Integer.parseInt(DateUtils.getDate("yyyy"));
		List<String> searchYears = new ArrayList<String>();
		for(int i = startYear ; i <= endYear ; i++) {
			searchYears.add(Integer.toString(i));
		}
		mav.addObject("searchYears", searchYears);
		
		if(param.getSearchYear() == null ) {
			param.setSearchYear(DateUtils.getDate("yyyy"));
		}
		
		List<ScheduleVO> scheduleList = scheduleService.getScheduleListUser(param);
		mav.addObject("scheduleList", scheduleList);			
		/*
		for(int i=0;i<scheduleList.size();i++) {
			logger.info("ScheduleController getScheduleList createDt "+scheduleList.get(i).getCreateDt());
		}
		*/
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.vacation"));	 
		
		
		mav.setViewName("gms/schedule/list");
		return mav;
	}
	
	@RequestMapping(value = "/gms/schedule/write.do")
	public ModelAndView openScheduleWrite() {

		logger.info("ScheduleController getScheduleList");
		
		ModelAndView mav = new ModelAndView();			
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.vacation"));		
		
		mav.setViewName("gms/schedule/write");
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/schedule/register.do", method = RequestMethod.POST)
	public ModelAndView registerSchedule(
			HttpServletRequest request
			, HttpServletResponse response
			, ScheduleVO params) {

		ModelAndView mav = new ModelAndView();		

		RequestUtils.initUserPrgmInfo(request, params);		
		params.setUserId(params.getCreateId());
		HttpSession session = request.getSession();
		
		if(null != session){
			LoginUserVO user = new LoginUserVO();
			user = SessionUtil.getSessionInfo(request);
			params.setUserNm(user.getUserNm());
		}else {
			logger.info("ScheduleController registerSchedule session is null ");			
		}
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.vacation"));		
		
		logger.debug("ScheduleController registerSchedule params.getScheduleStartDt() " + params.getScheduleStartDt());		
		logger.debug("ScheduleController registerSchedule params.getScheduleEndDt() " + params.getScheduleEndDt());
		
		if(params.getScheduleEndDt()==null) params.setScheduleEndDt(params.getScheduleStartDt());
		int result = 0;
		
		result = scheduleService.registerSchedule(params);
		String alertMessage = "등록되었습니다.";
		
		if(result > 0){			
			RequestUtils.responseWriteException(response, alertMessage, "/gms/schedule/list.do");
		}else if(result <= 0) {
			alertMessage = "해당 일자에 휴가가 이미 등록되어있습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/schedule/list.do");
		}
		return null;
	}

	
	@RequestMapping(value = "/gms/schedule/delete.do", method = RequestMethod.POST)
	public ModelAndView deleteSchedule(HttpServletRequest request
			, HttpServletResponse response
			, ScheduleVO params) {
		
		logger.info("ScheduleController deleteSchedule");
		
		params.setUpdateId(params.getCreateId());
		RequestUtils.initUserPrgmInfo(request, params);
		
			
		int  result = scheduleService.deletelSchedule(params);
		
		if(result > 0){
			String alertMessage = "삭제하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/schedule/list.do");
		}
		return null;
		
	}
}

package com.gms.web.admin.controller.common;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.domain.common.CodeVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.OrderBottleVO;
import com.gms.web.admin.service.common.CodeService;
import com.gms.web.admin.service.manage.WorkReportService;

@Controller
public class startController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WorkReportService workService;
	@Autowired
	private CodeService codeService;
	
	@RequestMapping(value = "/gms/start")
	public ModelAndView getStartWorkBottleList(BottleVO params) {

		logger.debug("startController getStartWorkBottleList");

		ModelAndView mav = new ModelAndView();		
		
		//logger.debug("startController parmas.searchWordCD "+params.getSearchWorkCd());
		if(params.getSearchWorkCd()== null) params.setSearchWorkCd(PropertyFactory.getProperty("common.bottle.status.rent"));
		List<BottleVO> bottleList =  workService.getWorkBottleListToday(params);
		
		mav.addObject("bottleList", bottleList);		
		mav.addObject("totalCount", bottleList.size());	
		
		//BOTTLE_WORK_CD 코드정보 불러오기 
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.bottle.status"));
		mav.addObject("codeList", codeList);
		if(params.getSearchWorkCd()!=null)
			mav.addObject("searchWorkCd",params.getSearchWorkCd());
		mav.setViewName("gms/start");
		return mav;
	}
	
	@RequestMapping(value="/policy")
	public String getPolicy(Model model,HttpServletRequest req){
		return"gms/policy";
	}
}

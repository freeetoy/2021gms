package com.gms.web.admin.controller.manage;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.configurationprocessor.json.JSONArray;
//import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.domain.manage.ScheduleSimpleVO;
import com.gms.web.admin.domain.manage.ScheduleVO;
import com.gms.web.admin.service.manage.ScheduleService;


@Controller
public class CalendarController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@Autowired
	private ScheduleService scheduleService;

	@RequestMapping(value = "/gms/calendar/calendar.do")
	public String getCalendarList(Model model, HttpServletRequest req, String year, String month) {

		logger.info("CalendarContoller getCalendarList");
		
		List<ScheduleVO> scheduleList = scheduleService.getScheduleList();
		JSONObject obj =  new JSONObject();
        JSONArray jArray = new JSONArray();
        
        List<ScheduleSimpleVO> simpleList = new ArrayList<ScheduleSimpleVO>();
		try {
			
			ScheduleVO temp = new ScheduleVO();					
			ScheduleSimpleVO tempSimple = new ScheduleSimpleVO();		
			
			for(int i=0;i<scheduleList.size();i++) {
				temp = scheduleList.get(i);				
				
				JSONObject sObject = new JSONObject();
				
                sObject.put("title", temp.getScheduleTitle());
                tempSimple.setTitle(temp.getScheduleTitle());
                if(temp.getVacationGubun().equals(PropertyFactory.getProperty("Schedule.vacation.All"))) {
                	
                	sObject.put("start", temp.getScheduleStartDt());
                	tempSimple.setStart(temp.getScheduleStartDt());
                	
                }else {
                	String startH = "";
                	if(temp.getVacationGubun().equals(PropertyFactory.getProperty("Schedule.vacation.AM"))) {
                		startH = "09:00:00";
                	}else {
                		startH = "13:00:00";
                	}    	                	                	
                	//sObject.put("start", DateUtils.convertDateFormat(temp.getScheduleStartDt(),"yyyy-MM-dd")+"T"+startH);
                	sObject.put("start", temp.getScheduleStartDt()+"T"+startH);                	
                }
                
                if(!temp.getScheduleStartDt().equals(temp.getScheduleEndDt())) {
                	sObject.put("end", temp.getScheduleEndDt2());
                	tempSimple.setEnd(temp.getScheduleEndDt2());
                }
                sObject.put("groupId", temp.getScheduleSeq());
                tempSimple.setGroupId(temp.getScheduleSeq());
                //sObject.put("url", "/test/schedule?field="+temp.getScheduleSeq());
                jArray.put(sObject);
                simpleList.add(tempSimple);
				
			}
		
			obj.put("item", jArray);			
			model.addAttribute("list", obj);
			model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.calendar"));			
			 
		}catch(Exception e) {
			
		}
        //model.addAttribute("scheduleList", scheduleList);
		
		
		return "gms/calendar/calendar";
	}
	
	@RequestMapping("/gms/calendar/getEvent.do")
	@ResponseBody
	public List<ScheduleVO> getEvents() {
		List<ScheduleVO> scheduleList = scheduleService.getScheduleList();		
		
        return scheduleList;
    }	
	
	
}

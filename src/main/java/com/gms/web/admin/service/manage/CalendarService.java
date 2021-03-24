package com.gms.web.admin.service.manage;

import java.util.List;

import com.gms.web.admin.domain.manage.CalendarParam;
import com.gms.web.admin.domain.manage.CalendarVO;

public interface CalendarService {

	
	public List<CalendarVO> getCalendarList(CalendarParam  param);
	
	public List<CalendarVO> getCalendarListDay(CalendarParam param);
	
	public CalendarVO getCalendar(String seq);
	
	public boolean registerCalender(CalendarVO param);
	
	public boolean modifyCalendar(CalendarVO param);
	
	public boolean deletelCalendar(String seq);	
	
}

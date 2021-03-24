package com.gms.web.admin.service.manage;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.manage.CalendarParam;
import com.gms.web.admin.domain.manage.CalendarVO;

@Service
public class CalendarServiceImpl implements CalendarService {

	@Override
	public List<CalendarVO> getCalendarList(CalendarParam  param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CalendarVO> getCalendarListDay(CalendarParam param) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public CalendarVO getCalendar(String seq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean registerCalender(CalendarVO param) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modifyCalendar(CalendarVO param) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deletelCalendar(String seq) {
		// TODO Auto-generated method stub
		return false;
	}

	
	

}

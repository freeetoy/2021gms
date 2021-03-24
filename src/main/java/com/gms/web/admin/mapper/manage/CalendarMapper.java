package com.gms.web.admin.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.CalendarParam;
import com.gms.web.admin.domain.manage.CalendarVO;

@Mapper
public interface CalendarMapper {
	
	public List<CalendarVO> selectCalendarList(CalendarParam param);
	
	public List<CalendarVO> selectCalendarListDay(CalendarParam param);
	
	public int inserCalendar(CalendarVO param) ;
	
	public CalendarVO selectCalendar(String calSeq);
	
	public int updateCalendar(CalendarVO param) ;
	
	public int deleteCalendar(String calSeq) ;

}

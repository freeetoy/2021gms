package com.gms.web.admin.service.manage;

import java.util.List;

import com.gms.web.admin.domain.manage.ScheduleVO;

public interface ScheduleService {

	public List<ScheduleVO> getScheduleList();
	
	public List<ScheduleVO> getScheduleListUser(ScheduleVO param);
	
	public ScheduleVO getSchedule(Integer scheduleSeq);
	
	public int registerSchedule(ScheduleVO param);
	
	public int modifySchedule(ScheduleVO param);
	
	public int deletelSchedule(ScheduleVO param);	
	
	public int checkScheduleDuplicate(ScheduleVO param);	
}

package com.gms.web.admin.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.ScheduleVO;

@Mapper
public interface ScheduleMapper {

	public List<ScheduleVO> selectScheduleList();
	
	public List<ScheduleVO> selectScheduleListUser(ScheduleVO param);
	
	public int insertSchedule(ScheduleVO param) ;
	
	public ScheduleVO selectSchedule(Integer scheduleSeq);
	
	public int updateSchedule(ScheduleVO param) ;
	
	public int deleteSchedule(ScheduleVO param) ;
	
	public int selectScheduleCheck(ScheduleVO param);
}

package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleSimpleVO implements Serializable {
	
	private static final long serialVersionUID = 8133009842300234104L;
	
	String title;
	
	String start;
	
	String end;
	
	Integer groupId;

}

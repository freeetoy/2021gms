package com.gms.web.admin.domain.manage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarParam {

	private String id;
	private String yyyyMM;
	private String year;
	private String month;
	private String day;
	private String hour;
	private String minute;

	public CalendarParam(String id, String yyyyMM) {
		super();
		this.id = id;
		this.yyyyMM = yyyyMM;
	}

	public CalendarParam(String id, String yyyyMM, String year, String month, String day, String hour, String minute) {
		super();
		this.id = id;
		this.yyyyMM = yyyyMM;
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
	}


}

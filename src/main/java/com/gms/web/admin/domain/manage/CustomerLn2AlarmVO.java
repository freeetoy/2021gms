package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerLn2AlarmVO implements Serializable {
	
	private static final long serialVersionUID = 8224009842300238122L;

	/** Customer_ID       */
	private Integer customerId;
	
	/** Customer_Nm       */
	private String customerNm;
	
	/** Day_Period       */
	private int dayPeriod;
	
	/** Period_CD       */
	private String periodCd;
		
	/** Work_Dt       */
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date workDt;
	
	/** Ln2_Sales_Id       */
	private String ln2SalesId;
}

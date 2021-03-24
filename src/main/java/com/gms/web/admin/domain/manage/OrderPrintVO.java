package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPrintVO implements Serializable {

	private static final long serialVersionUID = 3233229822300528104L;
	
	private OrderExtVO orderExt;
	
	private CustomerVO customer;
	
	private String orderTotalAmountHan;
	
	private String rentBottle;
	
	private CashSumVO cashSum;
}

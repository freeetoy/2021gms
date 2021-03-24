package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashSumVO implements Serializable {
	
	private static final long serialVersionUID = 3212009822300528104L;

	double incomeAmountSum;
	
	double receivableAmountSum;
	
	double receivableAmountNet;
}

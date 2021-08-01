package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderExtCustomerVO implements Serializable {
	
	private static final long serialVersionUID = 3233229822300522104L;
	
	private OrderExtVO orderExt;
	
	private CustomerVO customer;
}

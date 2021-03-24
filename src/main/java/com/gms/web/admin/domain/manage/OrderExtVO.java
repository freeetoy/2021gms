package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderExtVO implements Serializable {
	
	private static final long serialVersionUID = 3233229822300528104L;
	
	private OrderVO order;
	
	private List<OrderProductVO> orderProduct;

}

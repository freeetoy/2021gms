package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkBottleRegisterVO implements Serializable {
	
	private static final long serialVersionUID = 3224009822300528102L;
	
	/** Product_ID */
	private Integer productId;
		
	/** Product_Price_Seq */
	private Integer productPriceSeq;
	
	/** Registered_Count      	*/
	private int registeredCount;	
	
	/** Bottle_Sale_YN 	*/
	private String bottleSaleYn;	
}

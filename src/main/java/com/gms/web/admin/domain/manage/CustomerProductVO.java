package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerProductVO extends AbstractVO implements Serializable {

private static final long serialVersionUID = 8224009842300238122L;
	
	/** Customer_ID       */
	private Integer customerId;
	
	/** Product_ID */
	private Integer productId;
	
	/**Product_Nm */
	private String productNm;	
		
	/** Product_Price_Seq */
	private Integer productPriceSeq;	
	
	/** Product_Capa     */
	private String productCapa;
	
	/**Bottle_Own_Count */
	private int bottleOwnCount;
	
	/**Bottle_Rent_Count */
	private int bottleRentCount;
}

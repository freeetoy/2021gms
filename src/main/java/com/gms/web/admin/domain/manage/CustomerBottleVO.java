package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerBottleVO extends AbstractVO implements Serializable {

	private static final long serialVersionUID = 8224009842310238104L;
	
	/** Bottle_Seq       */
	private Integer bottleSeq;
	
	/** Work_Dt       */
	private String workDt;
	
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
	
	/**Rent_Count   */
	private int rentCount;
	
	/**Back_Count    */
	private int backCount;
	
	/**Inventory_Count    */
	private int inventoryCount;
	
	/**Sales_ID    */
	private String salesId;	

}

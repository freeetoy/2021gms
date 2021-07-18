package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerPriceExtVO implements Serializable {
	
	private static final long serialVersionUID = 8229009842300238104L;
	
	/** Customer_ID       */
	private Integer customerId;
	
	/** Product_ID */
	private Integer productId;
		
	/** Product_Price_Seq */
	private Integer productPriceSeq;	
	
	/** Product_Price 	*/    
	private double productPrice;
	
	/** Product_Bottle_Price 	*/    
	private double productBottlePrice;
	
	/**Product_Nm */
	private String productNm;	
	
	/** Product_Capa     */
	private String productCapa;
	
	/**Gas_ID */
	private Integer gasId;

}

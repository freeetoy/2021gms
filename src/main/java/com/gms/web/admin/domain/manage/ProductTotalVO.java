package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductTotalVO extends AbstractVO  implements Serializable {
	
	private static final long serialVersionUID = 8484009842300528104L;
	
	/**Product_ID */
	private Integer productId;
		
	/**Product_Nm */
	private String productNm;	
	
	/**Gas_ID */
	private Integer gasId;
	
	/**Prodcut_Status */
	private String productStatus;
	
	/**Member_Comp_Seq */ 
	private Integer memberCompSeq;
	
	/** Product_Price_Seq */
	private Integer productPriceSeq;	
	
	/** Product_Price 	*/    
	private double productPrice;
	
	/** Product_Bottle_Price 	*/    
	private double productBottlePrice;
	
	/** Customer_Product_Price 	*/    
	private double customerProductPrice;
	
	/** Customer_Bottle_Price 	*/    
	private double customerBottlePrice;
	
	/** Product_Capa     */
	private String productCapa;
	
	/** Product_Price_count     */
	private int productPriceCount;
	
	/** ORDER_COUNT     */
	private int productOrderCount = 0;
	
	/** ECount_CD     */
	private String eCountCd;
	
	/** ECount_CDS     */
	private String eCountCdS;
	
}

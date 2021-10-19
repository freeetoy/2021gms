package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerSalesVO  implements Serializable {

	private static final long serialVersionUID = 8224009842300238133L;
	
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
	
	/**Sales_Count */
	private int salesCount;
	
	/**Product_Price */
	private int productPrice;
	
	/**CD_NM */
	private String cdNm;
	
	/** searchStatDt	*/
	private String  searchStatDt;
	
	/** searchStatDtFrom	*/
	private String  searchStatDtFrom;
	
	/** searchStatDtEnd	*/
	private String  searchStatDtEnd;
	
	/** searchCustomerId	*/
	private Integer searchCustomerId;
}

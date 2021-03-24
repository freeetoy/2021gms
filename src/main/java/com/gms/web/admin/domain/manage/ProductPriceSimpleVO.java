package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductPriceSimpleVO implements Serializable {
	
	private static final long serialVersionUID = 8284009822300528104L;

	/** Product_ID */
	private Integer productId;
		
	/**Product_Nm */
	private String productNm;	
	
	/** Product_Price_Seq */
	private Integer productPriceSeq;	
	
	/** Product_Capa     */
	private String productCapa;
}

package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductPriceVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 8484009822300528104L;

	
	/** Product_ID */
	private Integer productId;
		
	/** Product_Price_Seq */
	private Integer productPriceSeq;	
	
	/** Product_Price 	*/    
	private double productPrice;
	
	/** Product_Bottle_Price 	*/    
	private double productBottlePrice;
	
	/** Product_Capa     */
	private String productCapa;
	
	/**Prodcut_Status */
	private String productStatus;
	
	/**ECount_CD */
	private String eCountCd;
	
	/** ECount_CDS     */
	private String eCountCdS;
}

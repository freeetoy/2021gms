package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerPriceVO extends AbstractVO implements Serializable {

	private static final long serialVersionUID = 8224009842300238104L;
	
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
	
	/** 거래처명 검색	 */
	private String searchCustomerNm;
	
}

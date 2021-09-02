package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 8422009842300528104L;
	

	/** Order_ID			*/
	private Integer orderId;
	
	/** Order_Product_Seq	*/
	private Integer orderProductSeq;
	
	/** Product_ID 			*/
	private Integer productId;	
		
	/** Product_Price_Seq 	*/
	private Integer productPriceSeq;
	
	/** Order_Count      	*/
	private int orderCount;	
	
	/** Order_Product_ETC     	*/
	private String orderProductEtc;
	
	/** Bottle_Change_YN 	*/
	private String bottleChangeYn;	
	
	/** Bottle_Sale_YN 	*/
	private String bottleSaleYn;
	
	/** Retrieved_YN 	*/
	private String retrievedYn;	
	
	/** AS_YN 	*/
	private String asYn;	
	
	/** Order_Amount     	*/
	private double orderAmount;
	
	/** Order_VAT     	*/
	private double orderVat;
	
	/** Bottle_ID			*/
	private String bottleId;     
	
	/** Product_Nm 			*/
	private String productNm;
	
	/** Product_Capa 			*/
	private String productCapa;
	
	/** GAS_ID 			*/
	private Integer gasId;
	
	/** GAS_CD 			*/
	private String gasCd;
	
	/** Bottle_Work_CD 			*/
	private String bottleWorkCd;
	
	/** 삭제여부  Bottle_Type */
	/** E 공병, F 실병 */
	private String bottleType;
	
	/* Order_Bottle_Seq*/
	private Integer orderBottleSeq;
	
	/* Sales_Count*/
	private int salesCount;
	
	private Integer customerId;

	/** Bottle_BarCd		--20200807 추가	*/
	private String bottleBarCd;     
	
}

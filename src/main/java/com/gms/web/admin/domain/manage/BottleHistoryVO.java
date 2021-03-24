package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.Date;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BottleHistoryVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 3234009822300528104L;
		
	/** Bottle_Hist_Seq */
	private Integer bottleHistSeq ;
	
	/** Bottle_ID		*/
	private String bottleId;
	
	/** Bottle_ID		*/
	private String chBottleId;

	/** Bottle_BarCd	*/
	private String bottleBarCd;

	/** Product_ID */
	private Integer productId;
		
	/** Product_Price_Seq */
	private Integer productPriceSeq;
	
	/** Gas ID */
	private Integer gasId ;	
	
	/** Bottle_Capa	*/
	private String bottleCapa;
	
	/** Charge_Capa	*/
	private String chargeCapa;

	/** Bottle_Create_Dt	*/
	private Date bottleCreateDt;

	/** Bottle_Charge_Dt	*/
	private Date bottleChargeDt;
	
	/** Bottle_Volumn	*/
	private String bottleVolumn;

	/** Bottle_Charge_Prss	*/
	private String bottleChargePrss;

	/** Bottle_Sales_YN	*/
	private String bottleOwnYn;

	/** Bottle_Work_CD	*/
	/*
	0301	입고
	0302	진공배기
	0303	누출확인
	0304	충전기한확인
	0305	출고
	0306	상차
	0307	판매
	0308	회수
	0309	폐기
	*/
	private String bottleWorkCd;
	
	private String bottleWorkCdNm;
	
	/** Bottle_Work_ID	*/
	private String bottleWorkId;

	/** Bottle_Work_Dt	*/
	private Date bottleWorkDt;

	/** Customer_ID		*/
	private Integer customerId;

	/** Customer_Nm		*/
	private String customerNm;
	
	/** Member_Comp_Seq	*/
	private Integer memberCompSeq;

	/** Order_ID		*/
	private Integer orderId;

	/** Order_Product_Seq	*/
	private Integer orderProductSeq;
	
	/** 삭제여부  Bottle_Type */
	private String bottleType;
	
	/** 삭제여부  Delete_YN*/
	private String deleteYn;
	
	
}

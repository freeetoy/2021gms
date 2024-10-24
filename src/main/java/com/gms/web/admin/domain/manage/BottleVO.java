package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.gms.web.admin.common.domain.AbstractSearchVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BottleVO extends AbstractSearchVO implements Serializable {

	private static final long serialVersionUID = 3224009822300528104L;
	
	/** Bottle_ID		*/
	private String bottleId;
	
	/** Bottle_ID		*/
	private String chBottleId;

	/** Bottle_BarCd	*/
	private String bottleBarCd;
	
	/** Bottle_BarCd	*/
	private String chBottleBarCd;

	/** Product_ID */
	private Integer productId;
	
	/** Product Nm */
	private String productNm ;
		
	/** Product NIckNm */
	private String productNickNm ;
	
	/** Product_Price_Seq */
	private Integer productPriceSeq;
	
	/** Gas ID */
	private Integer gasId ;
	
	/** Gas Nm */
	private String gasNm ;
	
	/** Gas_CD */
	private String gasCd ;
	
	/** Bottle_Capa	*/
	private String bottleCapa;
	
	/** Charge_Capa	*/
	private String chargeCapa;

	/** Bottle_Create_Dt	*/
	private Date bottleCreateDt;

	/** Bottle_Charge_Dt	*/
	private Date bottleChargeDt;
	
	/** Bottle_Charge_Dt_Str	*/
	private String bottleChargeDtStr;
	
	/** Bottle_Volumn	*/
	private String bottleVolumn;

	/** Bottle_Charge_Prss	*/
	private String bottleChargePrss;

	/** Bottle_Sales_YN	*/
	private String bottleOwnYn;
	
	/** Own_Customer_ID	*/
	private String ownCustomerId;

	/** Bottle_Work_CD	*/
	/*
	 * 0300	용기작업코드
	 * 0301	입고
	 * 0302	진공배기
	 * 0303	누출확인
	 * 0304	충전기한확인
	 * 0305	충전
	 * 0306	출고
	 * 0307	상차
	 * 0308	판매
	 * 0309	대여
	 * 0310	회수
	 * 0311	기타
	 * 0388	신규
	 * 0399	폐기
	*/
	private String bottleWorkCd;
	
	private String bottleWorkCdNm;

	/** Bottle_Work_ID	*/
	private String bottleWorkId;
	
	/** Bottle_Work_Nm	*/
	private String bottleWorkNm;
	
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
	/** E 공병, F 실병 */
	private String bottleType;
	
	/** 삭제여부  Delete_YN*/
	private String deleteYn;
	
	/** Dummy_Yn	*/
	private String dummyYn = "N";
	
	/** Bottle_Etc	*/
	private String bottleEtc ;
	
	/** Owner_Customer_ID	*/
	private Integer ownerCustomerId ;
	
	/** Owner_Customer_Nm	*/
	private String ownerCustomerNm ;
	
	/** 복수 삭제용 Ids*/
	private String bottleIds;
	
	private List<String> bottList;
	
	/** 검색용			*/
	/** SearchBottleiId	*/
	private String  searchBottleId;
	
	/** SearchBottle_BarCd	*/
	private String searchBottleBarCd;
	
	/** SearchGasId		*/
	private String  searchGasId;
	
	/** SearchProductId		*/
	private String  searchProductId;
	
	/** searchChargeDt	*/
	private String  searchChargeDt;
	
	/** searchCustomerNm	*/
	private String  searchCustomerNm;
	
	/** searchCustomerNm	*/
	private String  searchCustomerNm1;
	
	/** searchOwnerCustomerNm	*/
	private String  searchOwnerCustomerNm;
	
	/** searchChargeDtFrom	*/
	private String  searchChargeDtFrom;
	
	/** searchChargeDtEnd	*/
	private String  searchChargeDtEnd;
	
	/** searchSalesYn	*/
	private String  searchSalesYn;
	
	/** menuType
	 * 1:list
	 * 2:charge
	 * 3:sales
	 * 4.:rental
	 * 	*/
	private int  menuType;
	
	
	/** Car Customer_ID		*/
	private String carCustomerId;
	
	/** Search BottleWorkCd		*/
	private String searchWorkCd;
	
	private boolean success;
	
	/** setCustomerIdNullYN	*/
	private String  customerIdNullYn;
	
	private int startRow;
	
	/** searchDt	*/
	private String  searchDt;
	
	/** searchDtFrom	*/
	private String  searchDtFrom;
	
	/** searchDtEnd	*/
	private String  searchDtEnd;
	
	int printCount =1;
	
	/** searchBottleType	*/
	private String  searchBottleType;
}

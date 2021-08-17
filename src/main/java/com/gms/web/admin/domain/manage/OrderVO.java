package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.gms.web.admin.common.domain.AbstractSearchVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderVO extends AbstractSearchVO implements Serializable {

	private static final long serialVersionUID = 3233009822300528104L;
	
	/** Order_ID		*/
	private Integer orderId;

	/** Member_Comp_Seq    	*/
	private Integer memberCompSeq;

	/** Customer_ID        	*/
	private Integer customerId;

	/** Customer_ID        	*/
	private String customerNm;
	
	/** Order_Type_CD   
	 * 	0101 상품주문
	 *	0102 취소주문
	 *	0103 자동주문
	 *	0104 회수요청
	 *	0105 점검요청
	 *	0106 기타요청				*/
	private String orderTypeCd;

	/** Delivery_Req_Dt    	*/
	private Date deliveryReqDt;

	/** Delivery_Req_AmPm	
	 * 	A:오전
		P:오후					*/
	private String deliveryReqAmpm;

	/** Order_Etc          	*/
	private String orderEtc;
	
	/** Order_Product_Nm       	*/
	private String orderProductNm;
	
	/** Order_Product_Capa     	*/
	private String orderProductCapa;
	
	/** Order_Process_CD   	
	 * 
	 * 0210 주문접수
	 * 0220 주문완료
	 * 0230 생산완료
	 * 0240 납품
	 * 0250  판매완료
	 * 0260 거래명세서완료
	 * 0270 세금계산서완료
	 * 0280 입금완료
	 * 0290 거래완료
	 * 0299 삭제			*/
	private String orderProcessCd;
	
	/** Order_Process_Nm   	*/
	private String orderProcessCdNm;

	/** Order_Delivery_Dt   	*/
	private String orderDeliveryDt;
	
	/** Sales_ID           	*/
	private String salesId;

	/** Order_Total_Amount     	*/
	private double orderTotalAmount;
	
	/** Deposit_Check_Dt   	*/
	private Date depositCheckDt;

	/** Deposit_Amount     	*/
	private double depositAmount;

	/** Deposit_Bank_Cd    	
	 * 0501	국민은행
	 *	0502	신한은행
	 *	0503	우리은행
	 *	0504	KEB하나은행
	 *	0505	한국씨티은행
	 *	0506	한국스탠다드차타드은행
	 *	0507	케이뱅크
	 *	0508	카카오뱅크
	 *	0509	중소기업은행
	 *	0510	NH농협은행
	 *	0511	한국산업은행
	 *	0512	수협은행
	 *	0513	한국수출입은행
	 *	0514	경남은행
	 *	0515	광주은행
	 *	0516	대구은행
	 *	0517	부산은행
	 *	0518	전북은행
	 *	0519	제주은행
	 *	0520	우체국			*/
	private String depositBankCd;

	/** productCount     	*/
	private int productCount;

	/** Create_Nm           	*/
	private String createNm;

	/** Customer_City           	*/
	private String customerCity;
	
	/** 검색용			*/
	/** SearchCustomerNm	*/
	private String  searchCustomerNm;	
	
	/** searchChargeDt	*/
	private String  searchOrderDt;
	
	/** searchChargeDtFrom	*/
	private String  searchOrderDtFrom;
	
	/** searchChargeDtEnd	*/
	private String  searchOrderDtEnd;
	
	/** 상태변경용 chOrderId	*/
	private Integer  chOrderId;
	
	/** 주문상태 검색	*/
	private String searchOrderProcessCd;
	
	/** 상태변경용 chOrderId	*/
	private String  orderIds;

	private List<Integer> orderList;
}

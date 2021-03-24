package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderBottlesVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 3221109822300528104L;
	
	/** Order_ID		*/
	private Integer orderId;
	
	/** Bottle_Work_CD	*/
	/*
	0301	입고
	0302	진공배기
	0303	누출확인
	0304	충전기한확인
	0305	충전
	0306	출고
	0307	상차
	0308	판매
	0309	회수
	0310	기타
	0399	폐기
	*/
	private String bottleWorkCd;
	
	/** Bottle_IDs		*/
	private String bottleIds;

}

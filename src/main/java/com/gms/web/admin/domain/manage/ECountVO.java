package com.gms.web.admin.domain.manage;

import com.gms.web.admin.common.config.PropertyFactory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ECountVO {
	private static final long serialVersionUID = -1304822960909123260L;
	
	//일자	순번	거래처코드	거래처명	담당자	출하창고	거래유형	통화	환율	수금내역	비고	품목코드	품목명	규격	수량	단가	외화금액	공급가액	부가세	적요	단가(VAT포함)	생산전표생성	Ecount
	
	private String customerId;
	
	private String customerEId;
	
	private String customerNm;
	
	private String salesNm;
	
	private String wareHouse ="대한특수가스";
	
	private String dealType = PropertyFactory.getProperty("ecount.dealtype.tax.include");		// 거래유형
	
	private String currency;		//통화
	
	private String changetRate;		// 환율
	
	private String collectionContents;		//수금 내역
	
	private String Etc;		// 비고
	
	private Integer productId;
	
	private String eCountCd;	//ECount품목코드
	/** Product_Price_Seq */
	private Integer productPriceSeq;
	/** ECount_CDS     */
	private String eCountCdS; 	//용기및사스 판매 ECount품목코드
	
	private String productNm;
	
	private String productCapa; // 규격
	
	private String eCountSpec; // Ecount규격
	
	private int orderCount;	//수량
	
	private double productPrice;	//단가
	
	private double currencyPrice;	//외화 금액
	
	private double supplyPrice;		//공급가액
		
	private double vat;			//부가세
	
	private String summary;			//적요
	
	private double prdouctPriceVAT;		//단가(VAT포함)
	
	private String receipt;		//생산전표생성
	
	private String EcountString ;
		
	/** Bottle_Sale_YN 	*/
	private String bottleSaleYn;	//용기 판매 여부
	
	private String bottleWorkCd;	
	
	/** Gas_CD */
	private String gasCd ;
	
	private String agencyYn = "N";
}

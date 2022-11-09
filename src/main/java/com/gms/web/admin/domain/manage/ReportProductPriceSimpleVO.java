package com.gms.web.admin.domain.manage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportProductPriceSimpleVO implements Comparable<ReportProductPriceSimpleVO> {

	/** Product_ID */
	private Integer productId;
		
	/**Product_Nm */
	private String productNm;	
	
	/** Product_Price_Seq */
	private Integer productPriceSeq;	
	
	/** Product_Capa     */
	private String productCapa;
	
	/** Same Name Cnt */
	private int sameNmCnt;
	
	public ReportProductPriceSimpleVO(Integer productId, String productNm, Integer productPriceSeq, String productCapa) {
		this.productId = productId;
		this.productNm = productNm;
		this.productPriceSeq = productPriceSeq;
		this.productCapa = productCapa;
	}

	public ReportProductPriceSimpleVO(Integer productId, String productNm, int sameNmCnt) {
		this.productId = productId;
		this.productNm = productNm;
		this.sameNmCnt = sameNmCnt;
	}
	
	@Override
	public int compareTo(ReportProductPriceSimpleVO arg0) {
		if (arg0.productId < productId) {            
			return 1;        
		} else if (arg0.productId > productId) {            
			return -1;     
		}else {
			if (arg0.productPriceSeq < productPriceSeq)       
				return 1; 
			else if (arg0.productPriceSeq > productPriceSeq)    
				return -1;    
		}        
		return 0;
	}


	

}

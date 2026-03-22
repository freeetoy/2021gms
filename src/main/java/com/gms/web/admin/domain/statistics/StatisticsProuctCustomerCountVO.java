package com.gms.web.admin.domain.statistics;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsProuctCustomerCountVO implements Serializable {
	private static final long serialVersionUID = 8483042342300538102L;
	
	/** Customer_Nm		*/
	private String customerNm;
	
	/** Work_CD_NM		*/
	private String workCdNm;
	
	/** sType		*/
	private int sType;
	
	/** SearchProductId		*/
	private String  searchProductId;
	
	/** Product_Price_Seq */
	private Integer productPriceSeq;
	
	/** searchDt	*/
	private String  searchDt;
	
	/** searchDtFrom	*/
	private String  searchDtFrom;
	
	/** searchDtEnd	*/
	private String  searchDtEnd;
	
	private int statCount;
	
	/** bottleWorkCds	*/
	private List<String> codeList;
	
	/** gubunType : daily (1)/monthly(2)		*/
	private int gubunType;
}

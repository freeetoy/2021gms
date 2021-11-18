package com.gms.web.admin.domain.statistics;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsAgencyResultVO implements Serializable {

	private static final long serialVersionUID = 8483043342300538104L;
	
	/**Customer_ID   */
	private Integer customerId ;
	
	/**Customer_Nm   */
	private String customerNm ;
	
	/**Product_ID   */
	private Integer productId ;	
	
	/**Product_Nm   */
	private String productNm ;	
	
	/**Product_Price_SEq   */
	private Integer productPriceSeq ;	
	
	/**Product_Capa   */
	private String productCapa ;		
	
	
	private Integer[] bottleOwnCountList;
	
	private Integer[] bottleRentCountList;
	
	private List<Integer> bottleOwnCountList1;
}

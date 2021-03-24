package com.gms.web.admin.domain.statistics;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsAgencyVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 8483003342300538104L;
	
	/**Stat_Dt      */
	private String statDt;
	
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
	
	/**Bottle_Own_Count  */
	private int bottleOwnCount;
	
	/**Bottle_Rent_Count */
	private int bottleRentCount;
		
	/** searchStatDt	*/
	private String  searchStatDt;	

	
	/** periodType
	 * 1 daily
	 * 2 monthly	*/
	private int  periodType;

	/**Parent_Customer_ID   */
	private Integer parentCustomerId ;
}

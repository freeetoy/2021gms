package com.gms.web.admin.domain.statistics;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsProductVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 8484003342300538104L;
	
	
	/**Product_ID   */
	private Integer productId ;
	
	/**Stat_Dt      */
	private String statDt;
	
	/**Sale_Count  */
	private int saleCount;
	
	/**Sale_Amount */
	private double saleAmount;
	
	/**Rent_Count  */
	private int rentCount;
	
	/**Rent_Amount */
	private double rentAmount;
	
	private Integer searchProductId;
	
	/** searchStatDt	*/
	private String  searchStatDt;
	
	/** searchStatDtFrom	*/
	private String  searchStatDtFrom;
	
	/** searchStatDtEnd	*/
	private String  searchStatDtEnd;
	
	/** periodType
	 * 1 daily
	 * 2 monthly	*/
	private int  periodType;

}

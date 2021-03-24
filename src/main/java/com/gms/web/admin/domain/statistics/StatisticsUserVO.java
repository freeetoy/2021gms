package com.gms.web.admin.domain.statistics;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsUserVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 8431003342300538104L;
	
	/**Stat_Dt		  */
	private String statDt;
	
	/**User_ID		  */
	private String userId;
	
	/**Sales_Count     */
	private int salesCount;
	
	/**Sales_Amount    */
	private double salesAmount;

	/**Rental_Count     */
	private int rentalCount;	
	
	/**Rental_Amount    */
	private double rentalAmount;
	
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

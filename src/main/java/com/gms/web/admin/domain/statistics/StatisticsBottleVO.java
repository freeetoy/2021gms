package com.gms.web.admin.domain.statistics;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsBottleVO extends AbstractVO implements Serializable {

	private static final long serialVersionUID = 8433004542300538104L;
	
	/**Stat_Dt		  */
	private String statDt;
	
	/**Bottle_Input_Count   */
	private int bottleInputCount;
	
	/**Bottle_Charge_Count  */
	private int bottleChargeCount;
	
	/**Bottle_Sales_Count   */
	private int bottleSalesCount;
	
	/**Bottle_Rental_Count   */
	private int bottleRentalCount;
	
	/**Bottle_Back_Count    */
	private int bottleBackCount;
	
	/**Bottle_Discard_Count */     
	private int bottleDiscardCount;
	
	/** searchStatDt	*/
	private String  searchStatDt;
	
	/** searchStatDtFrom	*/
	private String  searchStatDtFrom;
	
	/** searchStatDtEnd	*/
	private String  searchStatDtEnd;

}

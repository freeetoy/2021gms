package com.gms.web.admin.domain.statistics;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsOrderVO extends AbstractVO implements Serializable {

	private static final long serialVersionUID = 8433003342300538104L;
	
	/**Stat_Dt		  */
	private String statDt;
	
	/**Order_Total_Count     */
	private int orderTotalCount;
	
	/**Order_Total_Amount    */
	private double orderTotalAmount;
	
	/**Order_Completed_Count     */
	private int orderCompletedCount;
	
	/**Order_Completed_Amount    */
	private double orderCompletedAmount;
	
	/**Order_Product_Count   */
	private int orderProductCount;
	
	/**Order_Auto_Count   */
	private int orderAutoCount;
	
	/**Order_Cancel_Count    */
	private int orderCancelCount;
	
	/**Order_Retrieved_Count */
	private int orderRetrievedCount;
	
	/**Order_Check_Count     */
	private int orderCheckCount;
	
	/**Order_Etc_Count       */
	private int orderEtcCount;
	
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

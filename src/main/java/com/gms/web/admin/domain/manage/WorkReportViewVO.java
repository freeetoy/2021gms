package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.List;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkReportViewVO extends AbstractVO implements Serializable {

	
	/**Work_Report_Seq      */
	private Integer workReportSeq;
	
	/**User_ID              */
	private String userId;
	
	/** Customer_ID        	*/
	private Integer customerId;
	
	/** Customer_Nm        	*/
	private String customerNm;
	
	/** Order_Amount     	*/
	private double orderAmount;
	
	/** Income_Way		*/  
	/** CASH
	 *  CARD
	 */
	private String incomeWay;
	
	/**Received_Amount     */
	private double receivedAmount;
	
	/**Taxinvoice_Type */
	private String taxinvoiceType;
	
	/** searchDt	*/
	private String  searchDt;
	
	private String salesProducts;
	
	private String backProducts;
	

	/** Order_Amount_Today     	*/
	private double orderAmountToday;
	
	/**Received_Amount_Today     */
	private double receivedAmountToday;
	
	private List<WorkBottleVO> salesBottles;
	
	private List<WorkBottleVO> backBottles;
	
	private String strSalesBottles;
	
	private String strBackBottles;
}

package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractSearchVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CashFlowVO extends AbstractSearchVO implements Serializable {
	
	private static final long serialVersionUID = 3222009822300528104L;
	
	/** Cash_Flow_Seq	*/   
	private Integer cashFlowSeq;	
	
	/** Customer_ID		*/
	private Integer customerId;
	
	/** Customer_Nm        	*/
	private String customerNm;

	/** Income_Amount       */    
	private double incomeAmount;
	
	/** Receivable_Amount	*/    
	private double receivableAmount;
	
	/** Income_Way		*/  
	/** CASH
	 *  CARD
	 */
	private String incomeWay;
	
	/** 삭제여부  Delete_YN*/
	private String deleteYn;
	
	/** searchCreateDt	*/
	private String  searchCreateDt;
	
	/** searchCreateDtFrom	*/
	private String  searchCreateDtFrom;
	
	/** searchCreateDtEnd	*/
	private String  searchCreateDtEnd;
	
	/** User_Nm	*/
	private String  userNm;
}

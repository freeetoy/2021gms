package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.List;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportViewVO extends AbstractVO implements Serializable {

	/**Work_Report_Seq      */
	private Integer workReportSeq;
		
	/** Customer_ID        	*/
	private Integer customerId;
	
	/** Customer_Nm        	*/
	private String customerNm;
	
	private List<String> countList ;
	
	private String[] countString;
	
	private int[] fullCount;
	
	private int[] emptyCount;
}

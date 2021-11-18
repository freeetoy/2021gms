package com.gms.web.admin.domain.statistics;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsCustomerBottleVO implements Serializable {

	private static final long serialVersionUID = 8483043342300538102L;
	/**Work_Report_Seq      */
	private Integer workReportSeq;
	
	/** Customer_ID		*/
	private Integer customerId;

	/** Customer_Nm		*/
	private String customerNm;
	
	/** Create_Id           	*/
	private String createId;
	
	/** Sales_Nm           	*/
	private String salesNm;
		
	/** Product_ID */
	private Integer productId;
		
	/** Product_Price_Seq */
	private Integer productPriceSeq;
	
	/**Product_Nm */
	private String productNm;	
	
	/** Product_Capa     */
	private String productCapa;
	
	private String carNumber;
	
	private String strCD;
	
	private int rentCount;
	
	private int backCount;
	
	private int saleCount;
	
	private int asCount;
	
	private int chargeCount;
	
	private int outCount;
	
	private int inCount;
	
	/** Report_Etc           	*/
	private String reportEtc;
	
	/** searchStatDt	*/
	private String  searchStatDt;
	
}

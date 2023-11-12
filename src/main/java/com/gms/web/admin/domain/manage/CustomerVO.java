package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractSearchVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerVO extends AbstractSearchVO implements Serializable {

	private static final long serialVersionUID = 3484009822300528104L;
	
	
	/** Customer_ID       */
	private Integer customerId;
	
	/** Customer_Nm       */
	private String customerNm;
	
	/** Customer_NickNm       */
	private String customerNickNm;
	
	/** Business_Reg_ID   */
	private String businessRegId;
		
	/** Customer_Rep_Nm   */
	private String customerRepNm;
	
	/** Customer_Busi_Type */
	private String customerBusiType;
	
	/** Customer_Item     */
	private String customerItem;

	/** Customer_Addr     */
	private String customerAddr;

	/** Customer_Phone    */
	private String customerPhone;

	/** Customer_Email    */
	private String customerEmail;

	/** Member_Comp_Seq   */
	private Integer memberCompSeq;

	/** Sales_ID          */
	private String salesId;	
		
	/** Sales_NM          */
	private String salesNm;	
	
	/**Customer_Status */
	private String customerStatus;
	
	/**Car_YN */
	private String carYn = "N";
	
	/**Agency_YN */
	private String agencyYn = "N";
	
	/** Parent_Customer_ID       */
	private Integer parentCustomerId;
	
	/** 거래처명 검색	 */
	private String searchCustomerNm;
	
	/**ECount_CD */
	private String eCountCd;
	
	/**Bottle_Own_Count */
	private int bottleOwnCount;
	
	/**Bottle_Rent_Count */
	private int bottleRentCount;
	
	/**Summary */
	private String summary;

	/**Taxinvoice_Type */
	private String taxinvoiceType;
	
	//20221122 Ewha 컬럼 추가
	/**Sale_Type */
	private String saleType;
	
	/** Business_Nm   */
	private String businessNm;
	
	/** Customer_Dtl_Addr     */
	private String customerDtlAddr;
	
	/** Customer_Post     */
	private String customerPost;
	
	/** Customer_Mobile    */
	private String customerMobile;
	
	/** Customer_Fax    */
	private String customerFax;
	
	/** Tax_Type    */
	private String taxType;
	
	/** Pay_Type    */
	private String payType;
	
}

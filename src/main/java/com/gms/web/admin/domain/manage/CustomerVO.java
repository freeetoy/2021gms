package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractSearchVO;

import lombok.Getter;
import lombok.Setter;


public class CustomerVO extends AbstractSearchVO implements Serializable {

	private static final long serialVersionUID = 3484009822300528104L;
	
	
	/** Customer_ID       */
	private Integer customerId;
	
	/** Customer_Nm       */
	private String customerNm;
	
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

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getCustomerNm() {
		return customerNm;
	}

	public void setCustomerNm(String customerNm) {
		this.customerNm = customerNm;
	}

	public String getBusinessRegId() {
		return businessRegId;
	}

	public void setBusinessRegId(String businessRegId) {
		this.businessRegId = businessRegId;
	}

	public String getCustomerRepNm() {
		return customerRepNm;
	}

	public void setCustomerRepNm(String customerRepNm) {
		this.customerRepNm = customerRepNm;
	}

	public String getCustomerBusiType() {
		return customerBusiType;
	}

	public void setCustomerBusiType(String customerBusiType) {
		this.customerBusiType = customerBusiType;
	}

	public String getCustomerItem() {
		return customerItem;
	}

	public void setCustomerItem(String customerItem) {
		this.customerItem = customerItem;
	}

	public String getCustomerAddr() {
		return customerAddr;
	}

	public void setCustomerAddr(String customerAddr) {
		this.customerAddr = customerAddr;
	}

	public String getCustomerPhone() {
		return customerPhone;
	}

	public void setCustomerPhone(String customerPhone) {
		this.customerPhone = customerPhone;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public Integer getMemberCompSeq() {
		return memberCompSeq;
	}

	public void setMemberCompSeq(Integer memberCompSeq) {
		this.memberCompSeq = memberCompSeq;
	}

	public String getSalesId() {
		return salesId;
	}

	public void setSalesId(String salesId) {
		this.salesId = salesId;
	}

	public String getSalesNm() {
		return salesNm;
	}

	public void setSalesNm(String salesNm) {
		this.salesNm = salesNm;
	}

	public String getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(String customerStatus) {
		this.customerStatus = customerStatus;
	}

	public String getCarYn() {
		return carYn;
	}

	public void setCarYn(String carYn) {
		this.carYn = carYn;
	}

	public String getAgencyYn() {
		return agencyYn;
	}

	public void setAgencyYn(String agencyYn) {
		this.agencyYn = agencyYn;
	}

	public Integer getParentCustomerId() {
		return parentCustomerId;
	}

	public void setParentCustomerId(Integer parentCustomerId) {
		this.parentCustomerId = parentCustomerId;
	}

	public String getSearchCustomerNm() {
		return searchCustomerNm;
	}

	public void setSearchCustomerNm(String searchCustomerNm) {
		this.searchCustomerNm = searchCustomerNm;
	}

	public String geteCountCd() {
		return eCountCd;
	}

	public void seteCountCd(String eCountCd) {
		this.eCountCd = eCountCd;
	}

	public int getBottleOwnCount() {
		return bottleOwnCount;
	}

	public void setBottleOwnCount(int bottleOwnCount) {
		this.bottleOwnCount = bottleOwnCount;
	}

	public int getBottleRentCount() {
		return bottleRentCount;
	}

	public void setBottleRentCount(int bottleRentCount) {
		this.bottleRentCount = bottleRentCount;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}

package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarInventoryVO extends AbstractVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Customer_ID        	*/
	private Integer customerId;
	
	/** Customer_Nm        	*/
	private String customerNm;
	
	/** Inventory_Dt        */
	private String inventoryDt;
	
	/** Product_ID */
	private Integer productId;
	
	/**Product_Nm */
	private String productNm;	
	
	/** Product_Price_Seq */
	private Integer productPriceSeq;	
	
	/** Product_Capa     */
	private String productCapa;
	
	/** FULL_CNT   */
	private int fullCnt;
	
	/** EMPTY_CNT   */
	private int emptyCnt;

	public CarInventoryVO() {
	}

	public CarInventoryVO(Integer customerId, String inventoryDt, Integer productId, String productNm,
			Integer productPriceSeq, String productCapa, int fullCnt, int emptyCnt) {
		this.customerId = customerId;
		this.inventoryDt = inventoryDt;
		this.productId = productId;
		this.productNm = productNm;
		this.productPriceSeq = productPriceSeq;
		this.productCapa = productCapa;
		this.fullCnt = fullCnt;
		this.emptyCnt = emptyCnt;
	}

	public CarInventoryVO(Integer customerId, Integer productId, String productNm, Integer productPriceSeq,
			String productCapa, int fullCnt, int emptyCnt) {
		this.customerId = customerId;
		this.productId = productId;
		this.productNm = productNm;
		this.productPriceSeq = productPriceSeq;
		this.productCapa = productCapa;
		this.fullCnt = fullCnt;
		this.emptyCnt = emptyCnt;
	}

	public CarInventoryVO(Integer customerId, Integer productId, Integer productPriceSeq, int fullCnt, int emptyCnt) {
		this.customerId = customerId;
		this.productId = productId;
		this.productPriceSeq = productPriceSeq;
		this.fullCnt = fullCnt;
		this.emptyCnt = emptyCnt;
	}

	public CarInventoryVO(Integer customerId, String inventoryDt, Integer productId, Integer productPriceSeq,
			int fullCnt, int emptyCnt) {
		this.customerId = customerId;
		this.inventoryDt = inventoryDt;
		this.productId = productId;
		this.productPriceSeq = productPriceSeq;
		this.fullCnt = fullCnt;
		this.emptyCnt = emptyCnt;
	}
	
	
}

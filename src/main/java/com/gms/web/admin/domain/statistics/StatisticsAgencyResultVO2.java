package com.gms.web.admin.domain.statistics;

import java.io.Serializable;
import java.util.List;

import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsAgencyResultVO2 implements Serializable {

	private static final long serialVersionUID = 8483043342300538104L;
	
		
	/**Product_ID   */
	private Integer productId ;	
	
	/**Product_Nm   */
	private String productNm ;	
	
	/**Product_Price_SEq   */
	private Integer productPriceSeq ;	
	
	/**Product_Capa   */
	private String productCapa ;		
	
	
	private int[] bottleOwnCountList;
	
	private int[] bottleRentCountList;
}

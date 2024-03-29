package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductNewVO extends AbstractVO  implements Serializable {
	
	private static final long serialVersionUID = 8484009842300528104L;
	
	/**Product_ID */
	private Integer productId;
		
	/**Product_Nm */
	private String productNm;	
	
	/**Gas_ID */
	private Integer gasId;	
	
	/**Gas_CD */
	private String gasCd;	
		
	/**Prodcut_Status */
	private String productStatus;
	
	/**Member_Comp_Seq */ 
	private Integer memberCompSeq;
	
	/**Sale_CType */
	private String saleCtype;	
}

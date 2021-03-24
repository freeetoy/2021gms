package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GasVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 8484009842300538104L;
	
	/** Gas ID */
	private Integer gasId ;
	 
	/** Member_Comp_Seq */
	private Integer memberCompSeq ;
	
	/** Gas 이름 */
	private String gasNm;
	
	/** Gas Code */
	private String gasCd;
	
	/** 삭제여부 */
	private String deleteYn;
	
	
	
}

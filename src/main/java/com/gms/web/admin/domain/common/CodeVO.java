package com.gms.web.admin.domain.common;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeVO extends AbstractVO implements Serializable {

	private static final long serialVersionUID = 1331928141433422978L;
	
	/** CD_ID                */
	private String CdId;

	/** CD_NM                */
	private String CdNm;

	/** CD_GRP_ID            */
	private String CdGrpId;

	/** Delete_YN            */
	private String deleteYn;
}

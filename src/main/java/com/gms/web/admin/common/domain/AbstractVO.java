package com.gms.web.admin.common.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AbstractVO implements Serializable {

	// 멤버변수 설명 (brief 설명)
    private static final long serialVersionUID = -1704822960909123260L;
	/**
	 * 생성자 ID
	 */
	private String createId = "";
	
	/**
	 * 생성 일자
	 */
	private Date createDt;
	
	/**
	 * 수정자 ID
	 */
	private String updateId = "";
	
	/**
	 * 수정 일자
	 */
	private Date updateDt;
		
	
}

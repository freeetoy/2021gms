package com.gms.web.admin.common.domain;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractSearchVO extends AbstractVO implements Serializable{
	
	// 멤버변수 설명 (brief 설명)
    private static final long serialVersionUID = -1704822960909123260L;
	
	/**
	 * 조회 대상 페이지 
	 */
	private int currentPage;
	
	/**
	 * 페이지 당 row
	 */
	private int rowPerPage;
	
	/**
	 * 정렬조건 (기준 조회 컬럼명은 Action class에서 설정한다) 
	 */
	private String orderColumn;
	
	/**
	 * 정렬방향 (기준 조회 컬럼명은 Action class에서 설정한다)
	 */
	private String orderWay;
	
	/**
	 * 페이징 사용 여부.
	 */
	private boolean usePaging;
	
    /**
     * <pre>
     * 기본 정보를 셋팅한다.
     * </pre>
     *
     */
    public AbstractSearchVO() {
        super();
        this.currentPage = 1;
        this.orderWay = "asc";
        this.rowPerPage = 15;
    }
	
}

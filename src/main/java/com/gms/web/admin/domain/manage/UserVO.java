package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.Date;

import com.gms.web.admin.common.domain.AbstractSearchVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserVO  extends  AbstractSearchVO implements Serializable {
	
	private static final long serialVersionUID = 8484009842300238104L;

	/** User_ID	 */        
	private String userId;
	
	/** Member_Comp_Seq	 */
	private Integer memberCompSeq ;
	
	/** User_Nm	 */
	private String userNm;
	
	/** User_Phone 	 */
	private String userPhone;
	
	/** User_Part_CD	 */
	private String userPartCd;
	
	/** User_Part_Nm	 */
	private String userPartNm;
	
	/** User_Authority	 */
	private String userAuthority;
	
	/** User_Passwd	 */
	private String userPasswd;
	
	/** Last_Connect_Dt	 */
	private Date lastConnectDt;
	
	/** 삭제여부 */
	private String deleteYn;
	
	/** 이카운트코드 ECount_User_CD */
	private String eCountUserCd;
	
	
	/** 사용자명 검색	 */
	private String searchUserNm;
	
	
}

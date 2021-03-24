package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 8133009842300238104L;


	/** Schedule_Seq     */
	private Integer scheduleSeq;
	
	/** User_ID          */
	private String userId;
	
	/** User_Nm          */
	private String userNm;
	
	/** Schedule_Title         */
	private String scheduleTitle;	
	
	/** Schedule_Type    
	 * 1:정기휴가
	 * 2:공휴
	 * 3:국가공휴
	 * */
	private String scheduleType;
	
	private String scheduleTypeNm;
	
	/** Schedule_Start_Dt*/
	private String scheduleStartDt;
	
	/** Schedule_End_Dt  */
	private String scheduleEndDt;
	
	/** Schedule_End_Dt2  */
	private String scheduleEndDt2;
	
	/** Vacation_Gubun   
	 * 1:하루종일
	 * 2.오전반차
	 * 3:오후반차
	 * */
	private String vacationGubun;
	
	private String vacationGubunNm;
	
	/** Delete_YN        */
	private String deleteYn;
	
	/** SearchDt        */
	private String SearchDt;
	
	private int dateDiffs;
	
	private String searchYear;
}

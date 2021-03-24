package com.gms.web.admin.common.utils;

import java.util.List;

import com.gms.web.admin.domain.manage.CalendarVO;

public class utilClass {
	//nvl함수
		public boolean nvl(String msg){
			return msg == null || msg.trim().equals("")?true:false;
		}

		//날짜를 클릭하면 그날에 일정이 모두 보여지도록 하는 callist.jsp로 이동하는 태그를 생성하는 함수
		public String callist(int year, int month, int day){
			String str = "";
			
			str += String.format("<a href='%s?year=%d&month=%d&day=%d'>",
									"callist.do", year, month, day);
			str += String.format("%2d", day);
			str += "</a>";
			
			return str;
		}

		// 일정을 기입하기 위해서 pen 이미지를 클릭하면, calwrite.jsp 이동시키는 함수
		public String showPen(int year, int month, int day){
			String str = "";
			
			String image = "<img src='image/pen.gif'>";
			
			str = String.format("<a href='%s?year=%d&month=%d&day=%d'>%s</a>", 
									"calwrite.do", year, month, day, image);
			return str;
		}

		// 1 -> 01
		public String two(String msg){
			return msg.trim().length() < 2?"0"+msg.trim():msg.trim();
		}
		
		//각 날짜별로 테이블을 생성하는 함수
		public String makeTable(int year, int month, int day, List<CalendarVO> list){
			String str="";
			
			String dates = (year+"") + two(month+"") + two(day+"");
			
			str += "<table>";
			str += "<col width='98'>";
			for(CalendarVO dto : list){
				if(dto.getCalDate().substring(0, 8).equals(dates)){
					str += "<tr>";
					str += "<td>";
					str += "<a href='caldetail.do?seq=" + dto.getCalSeq() + "'>";
					
					//str += "<font style='font-size:8;color:red'>";
					str += dot3(dto.getCalTitle());
					
					//str += "</font>";
					str += "</a>";
					
					str += "</td>";
					str += "</tr>";
				}
			}
			
			str += "</table>";	
			return str;
		}

		//제목이 너무 길면 제목 +...으로 처리하는 함수
		public String dot3(String msg){
			String str = "";
			if(msg.length() >= 5){
				str = msg.substring(0, 5);
				str += "...";
			}else{
				str = msg.trim();
			}
			
			return str;
		}



}

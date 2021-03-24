package com.gms.web.admin.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <pre>
 * 날짜 관련 유틸
 * </pre>
 *
 * @author jjpark
 * @since 2014. 8. 14.
 */
public class DateUtils {
	/**
	 * Set Logger
	 */
	private static Log logger = LogFactory.getLog(DateUtils.class);

	/**
	 * Should set these static final variables into Common table or
	 * Constant Variables Class Set Static final variables : BASE_DATE_FORMAT
	 */
	private static final String BASE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * get Current DateTime with BASE_DATE_FORMAT
	 * 
	 * @return the String
	 */
	public static String getDate() {
		return getDate(DateUtils.BASE_DATE_FORMAT);
	}

	/**
	 * <pre>
	 * get Current DateTime with specific Date Format
	 * </pre>
	 *
	 * @param dateFormat
	 * @return
	 */
	public static String getDate(String dateFormat) {
		return DateUtils.convertDateFormat(new Date(), new SimpleDateFormat(dateFormat));
	}

	/**
	 * <pre>
	 * nextDays일 이후의 일자정보를 얻는다.
	 * </pre>
	 *
	 * @param nextDays
	 * @param dateFormat
	 * @return
	 */
	public static String getNextDate(int nextDays, String dateFormat) {
		Calendar cal = Calendar.getInstance();
		cal.add( Calendar.DATE, nextDays );
		return DateUtils.convertDateFormat(cal.getTime(), new SimpleDateFormat(dateFormat));
	}

	/**
	 * <pre>
	 * srcDateFormat 형으로 지정된 일자 정보를 trgDateFormat형태로 변경
	 * </pre>
	 * 
	 * @param sourceString
	 * @param srcDateFormat
	 * @param trgDateFormat
	 * @return the String
	 */
	public static String convertDateFormat(String sourceString, String srcDateFormat, String trgDateFormat) {
		if( sourceString == null || StringUtils.isEmpty(sourceString) || sourceString.startsWith( "0000" ) || sourceString.startsWith( "00:00" ) )
			return "";

        Date sourceDate = null;
		try {
			sourceDate = new SimpleDateFormat(srcDateFormat).parse(sourceString);
		} catch (ParseException e) {
			return "";
		}
		return DateUtils.convertDateFormat(sourceDate, new SimpleDateFormat(trgDateFormat));
	}

	/**
	 * <pre>
	 * 일자 정보(yyyyMMddHHmmss)를 지정된 포멧으로 변경
	 * </pre>
	 * 
	 * @param sourceString
	 * @param dateFormat
	 * @return the String
	 */
	public static String convertDateFormat(String sourceString, String dateFormat) {
		Date sourceDate = null;

		try {
			sourceDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(sourceString);
		} catch (ParseException e) {
		}
		return DateUtils.convertDateFormat(sourceDate, new SimpleDateFormat(dateFormat));
	}

	/**
	 * <pre>
	 * 일자 정보(yyyyMMddHHmmss)를 지정된 포멧으로 변경
	 * </pre>
	 * 
	 * @param sourceDate
	 * @param dateFormat
	 * @return the String
	 */
	public static String convertDateFormat(Date sourceDate, String dateFormat) {
		return DateUtils.convertDateFormat(sourceDate, new SimpleDateFormat(dateFormat));
	}

	/**
	 * <pre>
	 * get format converted Date string with specific Date Format
	 * </pre>
	 * 
	 * @param sourceDate
	 * @param dateForm
	 * @return  the String
	 */
	public static String convertDateFormat(Date sourceDate, SimpleDateFormat dateForm) {
		return dateForm.format(sourceDate);
	}
	
	/**
	 * <pre>
	 * 지정된 포맷의 시간정보에 특정시간을 더하거나 빼서 시간정보를 리턴한다. 
	 * </pre>
	 *
	 * @param sourceDate
	 * @param dateFormat
	 * @param field
	 * @param amount
	 * @return
	 */
	public static String addTime(String sourceDate, String dateFormat, int field, int amount) {
		Date date = new Date();
		try {
			date = new SimpleDateFormat(dateFormat).parse(sourceDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount);
		return new SimpleDateFormat(dateFormat).format(cal.getTime());
	}
	
	
	/**
	 * 기간 선택시 선택한 기간(일별, 주별, 월별)에 따른 검색 기간 조건( 1개월, 3개월, 6개월) 여부 검사
	 * 검색 시작일이 검색 종료일 보다 이 후 일경우 false 리턴 이외의 조건엔 true.
	 * @param startDate
	 * @param term
	 * @param endDate
	 * @return
	 */
	public static boolean getDateDiff(String startDate, int term, String endDate){
		if(startDate == null || "".equals(startDate) || endDate == null || "".equals(endDate)){
			return false;
		}
		
		int sYear 		= Integer.parseInt(startDate.substring(0, 4));
		int sMonth 		= Integer.parseInt(startDate.substring(4, 6)) - 1;
		int sDay 		= Integer.parseInt(startDate.substring(6, 8));
		
		int eYear		= Integer.parseInt(endDate.substring(0, 4));
		int eMonth		= Integer.parseInt(endDate.substring(4, 6)) - 1;
		int eDay		= Integer.parseInt(endDate.substring(6, 8));
		Calendar sDate 	= Calendar.getInstance();
		sDate.set(sYear, sMonth + term, sDay);
		
		Calendar eDate 	= Calendar.getInstance();
		eDate.set(eYear, eMonth, eDay);
		
		if(sDate.compareTo(eDate) > 0){
			return false;
		}
		
		return true;
	}
}
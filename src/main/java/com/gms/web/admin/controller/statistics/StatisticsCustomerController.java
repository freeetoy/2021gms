package com.gms.web.admin.controller.statistics;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.ExcelStyle;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.domain.manage.WorkBottleRegisterVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.domain.statistics.StatisticsCustomerBottleVO;
import com.gms.web.admin.domain.statistics.StatisticsCustomerVO;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.ProductService;
import com.gms.web.admin.service.manage.UserService;
import com.gms.web.admin.service.manage.WorkReportService;
import com.gms.web.admin.service.statistics.StatisticsCustomerService;

@Controller
public class StatisticsCustomerController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private StatisticsCustomerService statService;
	
	@Autowired
	private WorkReportService workService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping(value = "/gms/statistics/customer/daily.do")
	public ModelAndView getStatisticsCustomerDaily(StatisticsCustomerVO params) {

		logger.info("StatisticsCustomerContoller getStatisticsCustomerDaily");
		//logger.debug("StatisticsCustomerContoller searchStatisticsCustomerDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchCustomerId = params.getSearchCustomerId();
				
		String searchStatDt = params.getSearchStatDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(searchStatDt != null && searchStatDt.length() > 20) {						
			searchStatDtFrom = searchStatDt.substring(0, 10) ;			
			searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);			
		}else {				
			searchStatDtFrom = DateUtils.getNextDate(-31,"yyyy/MM/dd");
			//logger.debug("****** getStatisticsCustomerDaily else *****getSearchStatDtFrom===*"+searchStatDtFrom);
			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
			//logger.debug("****** getStatisticsCustomerDaily else *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
			params.setSearchStatDt(searchStatDt);
		}
		params.setSearchStatDt(searchStatDtFrom);
		
		List<StatisticsCustomerVO> statCustomerList = statService.getDailylStatisticsCustomerList(params);
		
		mav.addObject("statCustomerList", statCustomerList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);	
		mav.addObject("searchCustomerId", params.getSearchCustomerId());			
		
		Map<String, Object> map = customerService.searchCustomerList("");
		mav.addObject("customerList", map.get("list"));
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_customer"));	 		
		
		mav.setViewName("gms/statistics/customer/daily");
		
		return mav;
	}

	
	@RequestMapping(value = "/gms/statistics/customer/monthly.do")
	public ModelAndView getStatisticsCustomerMonthly(StatisticsCustomerVO params) {

		logger.info("StatisticsCustomerContoller getStatisticsCustomerMonthly");
		logger.debug("StatisticsCustomerContoller searchStatisticsCustomerDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
				
		String searchStatDt = params.getSearchStatDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(searchStatDt != null && searchStatDt.length() > 20) {						
			searchStatDtFrom = searchStatDt.substring(0, 8) ;			
			searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()-2) ;
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);			
		}else {									
			searchStatDtFrom = DateUtils.getNextDate(-366,"yyyy/MM");
			//logger.debug("****** getMonthlylStatisticsCustomerList else *****getSearchStatDtFrom===*"+searchStatDtFrom);			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM");
			//logger.debug("****** getMonthlylStatisticsCustomerList else *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
			
			params.setSearchStatDt(searchStatDt);
		}
		params.setSearchStatDt(searchStatDtFrom);
		
		List<StatisticsCustomerVO> statCustomerList = statService.getMontlylStatisticsCustomerList(params);
		
		mav.addObject("statCustomerList", statCustomerList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);	
		mav.addObject("searchCustomerId", params.getSearchCustomerId());	
		
		Map<String, Object> map = customerService.searchCustomerList("");
		mav.addObject("customerList", map.get("list"));
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_customer"));		
		
		mav.setViewName("gms/statistics/customer/monthly");
		
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/statistics/customer/excelDown.do")
	public void excelDownloadBottle(HttpServletResponse response,StatisticsCustomerVO params){
	// 게시판 목록조회

	   try {
		   // 가스 정보 불러오기
		   String searchStatDt = params.getSearchStatDt();	
			
		   String searchStatDtFrom = null;
		   String searchStatDtEnd = null;
					
			if(searchStatDt != null && searchStatDt.length() > 20) {						
				searchStatDtFrom = searchStatDt.substring(0, 10) ;			
				searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
				
				params.setSearchStatDtFrom(searchStatDtFrom);
				params.setSearchStatDtEnd(searchStatDtEnd);			
			}else {			
				searchStatDtFrom = DateUtils.getNextDate(-31,"yyyy/MM/dd");		
				searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");	
				
				params.setSearchStatDtFrom(searchStatDtFrom);
				params.setSearchStatDtEnd(searchStatDtEnd);
		
				searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
				
				params.setSearchStatDt(searchStatDt);
			}		
			
			CustomerVO customer = customerService.getCustomerDetails(params.getSearchCustomerId());
					
			String sheetName = "거래처";
			if(customer != null) sheetName = customer.getCustomerNm();
			
			List<StatisticsCustomerVO> statCustomerList = null;
			
			String fileName="Statistic_"+sheetName;
			if(params.getPeriodType()==1) {
				statCustomerList = statService.getDailylStatisticsCustomerList(params);
				fileName +="Daily_"+DateUtils.getDate();
			}else {
				statCustomerList = statService.getMontlylStatisticsCustomerList(params);
				fileName +="Monthly_"+DateUtils.getDate();
			}
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet(sheetName);
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
	
		    headStyle= ExcelStyle.getHeadStyle(headStyle);

		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    
		    bodyStyle= ExcelStyle.getBodyStyle(bodyStyle);
		   
		    // 헤더 생성 날짜		주문건수	주문금액
		    //			0	1		2		
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.stat.customer.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {
		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		    
		   // 날짜		주문건수	주문금액
		    // 데이터 부분 생성
		    for(StatisticsCustomerVO vo : statCustomerList) {
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(0);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getStatDt());
		        
		        cell = row.createCell(1);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderCount());
		        
		        cell = row.createCell(2);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderAmount());	        
		        
		        cell = row.createCell(3);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getIncomeAmount());
		        
		        cell = row.createCell(4);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getReceivableAmount());	  
		       
		    }	
	
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel"); 
		    //response.setHeader("Content-Disposition", "attachment;filename="+fileName);	
		    response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"ISO8859_1") + ".xls");
	
		    // 엑셀 출력
		    wb.write(response.getOutputStream());
		    wb.close();
		    
	   } catch (DataAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
							 
	@RequestMapping(value = "/gms/statistics/customer/bottle.do")
	public ModelAndView getStatisticsCustomerBottle(StatisticsCustomerBottleVO param) {

//		logger.info("StatisticsCustomerContoller getStatisticsCustomerBottle");
		

		ModelAndView mav = new ModelAndView();
				
		String searchStatDt = param.getSearchStatDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(searchStatDt != null && searchStatDt.length() > 20) {						
			searchStatDtFrom = searchStatDt.substring(0, 10) ;			
			searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
			
			param.setSearchStatDtFrom(searchStatDtFrom);
			param.setSearchStatDtEnd(searchStatDtEnd);			
		}else {			
			searchStatDtFrom = DateUtils.getNextDate(-0,"yyyy/MM/dd");		
			searchStatDtEnd = DateUtils.getNextDate(-0,"yyyy/MM/dd");	
			
			param.setSearchStatDtFrom(searchStatDtFrom);
			param.setSearchStatDtEnd(searchStatDtEnd);
	
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
			
			param.setSearchStatDt(searchStatDt);
		}		
		if(param.getSearchCustomerId() != null && Integer.parseInt(param.getSearchCustomerId()) > 0) {
			param.setParamCustomerId(Integer.parseInt(param.getSearchCustomerId()));
		}else {
			param.setSearchCustomerId(null);
		}
		
		//param.setSearchUserId(param.getSearchUserId());
		
		UserVO tempUser = new UserVO();	
		tempUser.setUserPartCd(PropertyFactory.getProperty("common.user.part.account"));
		List<UserVO> userList = userService.getUserListPartNot(tempUser);
		
		List<StatisticsCustomerBottleVO> statCustomerBottleList = statService.getStatisticsCustomerBottleList(param);
		
		for(int i =0 ; i < statCustomerBottleList.size() ; i++) {
			StatisticsCustomerBottleVO statisticsCustomerBottle = statCustomerBottleList.get(i);
			
			if(statisticsCustomerBottle.getProductId().equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId")) ) ) {
				int chargeCount = 0;
				String strCapa = statisticsCustomerBottle.getProductCapa().replace("병_", "");
				chargeCount = Integer.parseInt(strCapa) * statisticsCustomerBottle.getSaleCount();
				
				statisticsCustomerBottle.setProductCapa("");
				statisticsCustomerBottle.setSaleCount(0);
				statisticsCustomerBottle.setChargeCount(chargeCount);
			}
		}
		mav.addObject("userList", userList);	
		mav.addObject("searchUserId", param.getSearchUserId());	
		
		mav.addObject("statCustomerList", statCustomerBottleList);	
		mav.addObject("searchStatDt", searchStatDt);	
		
		Map<String, Object> map = customerService.searchCustomerList("");
		mav.addObject("customerList", map.get("list"));
		mav.addObject("searchCustomerId", param.getParamCustomerId());
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_cbottle"));
		
		mav.setViewName("gms/statistics/customer/bottle");
		
		return mav;
	}
							 
	@RequestMapping(value = "/gms/statistics/customer/bottleExcelDown.do")
	public void excelDownloadCustomerBottle(HttpServletResponse response,StatisticsCustomerBottleVO param){
	   try {
		   String searchStatDt = param.getSearchStatDt();	
			
		   String searchStatDtFrom = null;
		   String searchStatDtEnd = null;
					
		   if(searchStatDt != null && searchStatDt.length() > 20) {						
				searchStatDtFrom = searchStatDt.substring(0, 10) ;			
				searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
				
				param.setSearchStatDtFrom(searchStatDtFrom);
				param.setSearchStatDtEnd(searchStatDtEnd);			
		   }else {			
				searchStatDtFrom = DateUtils.getNextDate(-0,"yyyy/MM/dd");		
				searchStatDtEnd = DateUtils.getNextDate(-0,"yyyy/MM/dd");	
				
				param.setSearchStatDtFrom(searchStatDtFrom);
				param.setSearchStatDtEnd(searchStatDtEnd);
		
				searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
				
				param.setSearchStatDt(searchStatDt);
		   }		
		   String titleDate = searchStatDt.replace("/","");
		   
		   if(param.getSearchCustomerId() != null && Integer.parseInt(param.getSearchCustomerId()) > 0) {
				param.setParamCustomerId(Integer.parseInt(param.getSearchCustomerId()));
			}else {
				param.setSearchCustomerId(null);
			}
		 logger.debug("StatisticsCustomerContoller searchUserId "+ param.getSearchUserId());
			List<StatisticsCustomerBottleVO> statCustomerBottleList = statService.getStatisticsCustomerBottleList(param);
					
			for(int i =0 ; i < statCustomerBottleList.size() ; i++) {
				StatisticsCustomerBottleVO statisticsCustomerBottle = statCustomerBottleList.get(i);
				
				if(statisticsCustomerBottle.getProductId().equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId")) ) ) {
					int chargeCount = 0;
					String strCapa = statisticsCustomerBottle.getProductCapa().replace("병_", "");
					
					chargeCount = Integer.parseInt(strCapa) * statisticsCustomerBottle.getSaleCount();
					
					statisticsCustomerBottle.setProductCapa("");
					statisticsCustomerBottle.setSaleCount(0);
					statisticsCustomerBottle.setChargeCount(chargeCount);
				}
			}
			
			String sheetName = "거래처용기현황";
			
		    // 워크북 생성
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet(sheetName);
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
	
		    headStyle= ExcelStyle.getHeadStyle(headStyle);

		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    
		    bodyStyle= ExcelStyle.getBodyStyle(bodyStyle);
		    
		    CellStyle styleCenter = wb.createCellStyle();
			//정렬
			styleCenter.setAlignment(HorizontalAlignment.CENTER);
			styleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
			//배경색
			styleCenter.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex()); //.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
			styleCenter.setFillPattern(FillPatternType.SOLID_FOREGROUND); //setFillPattern(CellStyle.SOLID_FOREGROUND);
			//폰트 설정
			HSSFFont font = (HSSFFont) wb.createFont(); //폰트 객체 생성

			font.setFontHeightInPoints((short)15); //폰트 크기
			font.setBold(true);// .setBoldweight(HSSF HSSFFont.BOLDWEIGHT_BOLD); //폰트 굵게

			styleCenter.setFont(font); //폰트 스타일 적용
			
			//테두리 선 (우,좌,위,아래)
			styleCenter.setBorderTop(BorderStyle.THIN);
			styleCenter.setBorderLeft(BorderStyle.THIN);
			styleCenter.setBorderRight(BorderStyle.THIN);
			styleCenter.setBorderBottom(BorderStyle.THIN);
			
			CellStyle styleRight = wb.createCellStyle();
			//정렬
		    styleRight.setAlignment(HorizontalAlignment.CENTER);
		    styleRight.setVerticalAlignment(VerticalAlignment.CENTER);
			
			//테두리 선 (우,좌,위,아래)
		    styleRight.setBorderTop(BorderStyle.THIN);
		    styleRight.setBorderLeft(BorderStyle.THIN);
		    styleRight.setBorderRight(BorderStyle.THIN);
		    styleRight.setBorderBottom(BorderStyle.THIN);
			HSSFDataFormat df = (HSSFDataFormat) wb.createDataFormat();
			styleRight.setDataFormat(df.getFormat("#,###"));
		   
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
	        cell = row.createCell(0);
	        cell.setCellStyle(styleCenter);
	        cell.setCellValue(searchStatDt+" 거래처 용기 관리 (통합)");
	        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));
	        
	        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
	        cell = row.createCell(1);
	        cell.setCellStyle(bodyStyle);
	        
		    // 헤더 생성 날짜		주문건수	주문금액
		    //			0	1		2		
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.stat.customer.bottle.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		    
		    // 데이터 부분 생성
		    for(StatisticsCustomerBottleVO vo : statCustomerBottleList) {
		    	int i=0;
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getSaleDt());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCarNumber());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getSalesNm());	        
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getStrCD());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerNm());	  
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        if(vo.getProductCapa().equals("")) 
		        		cell.setCellValue(vo.getProductNm());
		        else
		        	cell.setCellValue(vo.getProductNm()+" ("+vo.getProductCapa()+")");
		        	
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getRentCount());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getBackCount());	
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getSaleCount());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getAsCount());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getChargeCount());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getOutCount());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getInCount());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getWorkEtc());
		       
		    }	
		    for (int x = 0; x < sheet.getRow(2).getPhysicalNumberOfCells(); x++) {
//		    	if(x < 6) {
			    	sheet.autoSizeColumn(x);
	 				int width = sheet.getColumnWidth(x);
	 				int minWidth = list.get(x).getBytes().length * 450;
	 				int maxWidth = 18000;
	 				
	 				if (minWidth > width) {
	 					sheet.setColumnWidth(x, minWidth);
	 				} else if (width > maxWidth) {
	 					sheet.setColumnWidth(x, maxWidth);
	 				} else {
	 					sheet.setColumnWidth(x, width + 2000);
	 				}
// 				}else {
// 					sheet.autoSizeColumn(x);
// 					int minWidth = list.get(x).getBytes().length * 300;
// 					sheet.setColumnWidth(x, minWidth);
// 				}
 			}
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel"); 
		    //response.setHeader("Content-Disposition", "attachment;filename="+fileName);	
		    response.setHeader("Content-disposition", "attachment; filename=" + new String(sheetName.getBytes(),"ISO8859_1") + "_"+titleDate+ ".xls");
	
		    // 엑셀 출력
		    wb.write(response.getOutputStream());
		    wb.close();
		    
	   } catch (DataAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/gms/statistics/customer/reportExcelDown.do")
	public void excelDownloadCustomerReport(HttpServletResponse response,StatisticsCustomerBottleVO param){
	   try {
		   String searchStatDt = param.getSearchStatDt();	
			
			String searchStatDtFrom = null;
			String searchStatDtEnd = null;
					
			if(searchStatDt != null && searchStatDt.length() > 20) {						
				searchStatDtFrom = searchStatDt.substring(0, 10) ;			
				searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
				
				param.setSearchStatDtFrom(searchStatDtFrom);
				param.setSearchStatDtEnd(searchStatDtEnd);			
			}else {			
				searchStatDtFrom = DateUtils.getNextDate(-0,"yyyy/MM/dd");		
				searchStatDtEnd = DateUtils.getNextDate(-0,"yyyy/MM/dd");	
				
				param.setSearchStatDtFrom(searchStatDtFrom);
				param.setSearchStatDtEnd(searchStatDtEnd);
		
				searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
				
				param.setSearchStatDt(searchStatDt);
			}
			
			if(param.getSearchCustomerId() != null && Integer.parseInt(param.getSearchCustomerId()) > 0) {
				param.setParamCustomerId(Integer.parseInt(param.getSearchCustomerId()));
			}else {
				param.setSearchCustomerId(null);
			}
			
			String titleDate = searchStatDt.replace("/","");
			
//			List<StatisticsCustomerVO> statCustomerReportList = statService.getStatSalesCustomerCount(param);
			List<StatisticsCustomerBottleVO> statCustomerBottleList = statService.getStatSalesCustomerBottleList(param);
			List<WorkBottleRegisterVO> mergeList = new ArrayList<WorkBottleRegisterVO>();	
			
			for(int i =0 ; i < statCustomerBottleList.size() ; i++) {
				StatisticsCustomerBottleVO statisticsCustomerBottle = statCustomerBottleList.get(i);
				
				if(statisticsCustomerBottle.getProductId().equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId")) ) ) {
					int chargeCount = 0;
					String strCapa = statisticsCustomerBottle.getProductCapa().replace("병_", "");
					chargeCount = Integer.parseInt(strCapa) * statisticsCustomerBottle.getSaleCount();
					
					statisticsCustomerBottle.setProductCapa("");
					statisticsCustomerBottle.setSaleCount(0);
					statisticsCustomerBottle.setChargeCount(chargeCount);
				}
			}
			
			String sheetName = "거래처업무일지";
			
		    // 워크북 생성
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet(sheetName);
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
	
		    headStyle= ExcelStyle.getHeadStyle(headStyle);

		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    
		    bodyStyle= ExcelStyle.getBodyStyle(bodyStyle);
		    bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		   
		    CellStyle styleRight = wb.createCellStyle();
			//정렬
		    styleRight.setAlignment(HorizontalAlignment.CENTER);
		    styleRight.setVerticalAlignment(VerticalAlignment.CENTER);
			
			//테두리 선 (우,좌,위,아래)
		    styleRight.setBorderTop(BorderStyle.THIN);
		    styleRight.setBorderLeft(BorderStyle.THIN);
		    styleRight.setBorderRight(BorderStyle.THIN);
		    styleRight.setBorderBottom(BorderStyle.THIN);
			HSSFDataFormat df = (HSSFDataFormat) wb.createDataFormat();
			styleRight.setDataFormat(df.getFormat("#,###"));
		    // 헤더 생성 날짜		주문건수	주문금액
		    //			0	1		2		
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.stat.customer.report.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }

			int seq =1;
			Integer preCustomerId = 0;
		    // 데이터 부분 생성
//		    for(StatisticsCustomerBottleVO vo : statCustomerBottleList) {
			StatisticsCustomerBottleVO oldVo = null;
			WorkBottleRegisterVO scVo = new WorkBottleRegisterVO();
			int ordeCount = 0;
			for(int j=0; j < statCustomerBottleList.size() ; j++) {
				
				StatisticsCustomerBottleVO vo = statCustomerBottleList.get(j);

		    	int i=0;
		    	if(rowNo > 1 && !preCustomerId.equals(vo.getCustomerId()) ) seq++; 
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);

		        if(j==0) {
		        	ordeCount++;
		        	scVo.setRegisteredCount(ordeCount);
		        }else  if( j < statCustomerBottleList.size()-1){
		        	if(vo.getCustomerId().equals(oldVo.getCustomerId())) {
		        		ordeCount++;
		        		scVo.setRegisteredCount(ordeCount);
		        	}else {
						scVo.setRegisteredCount(ordeCount);
		        		mergeList.add(scVo);
		        		scVo = new WorkBottleRegisterVO();
		        		ordeCount =1;
		        	}
		        }else {
		        	if(!vo.getCustomerId().equals(oldVo.getCustomerId())) {
		        		ordeCount=1;
		        		scVo.setRegisteredCount(ordeCount);
		        		mergeList.add(scVo);
		        		scVo = new WorkBottleRegisterVO();
		        	}else {
		        		ordeCount++;
		        		scVo.setRegisteredCount(ordeCount);
		        		mergeList.add(scVo);
		        	}
		        }
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(seq);
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getSalesNm());	        
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerNm());	  
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        if(vo.getProductCapa().equals("")) 
	        		cell.setCellValue(vo.getProductNm());
		        else
		        	cell.setCellValue(vo.getProductNm()+" ("+vo.getProductCapa()+")");
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getRentCount());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getBackCount());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getSaleCount());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getAsCount());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(styleRight);
		        cell.setCellValue(vo.getChargeCount());
		        
		        cell = row.createCell(i++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getWorkEtc());
		        preCustomerId = vo.getCustomerId();
		        oldVo= vo;
		    }	

		    if(statCustomerBottleList.size() > 0) {
			    for (int x = 0; x < sheet.getRow(2).getPhysicalNumberOfCells(); x++) {
	//		    	if(x < 6) {
				    	sheet.autoSizeColumn(x);
		 				int width = sheet.getColumnWidth(x);
		 				int minWidth = list.get(x).getBytes().length * 450;
		 				int maxWidth = 18000;
		 				
		 				if (minWidth > width) {
		 					sheet.setColumnWidth(x, minWidth);
		 				} else if (width > maxWidth) {
		 					sheet.setColumnWidth(x, maxWidth);
		 				} else {
		 					sheet.setColumnWidth(x, width + 2000);
		 				}
	// 				}else {
	// 					sheet.autoSizeColumn(x);
	// 					int minWidth = list.get(x).getBytes().length * 300;
	// 					sheet.setColumnWidth(x, minWidth);
	// 				}
	 			}
		    }
		    
		    
		    int startC =1;
		    int lastC = 0;
		    int aCount = 0;
			for(int i=0; i < mergeList.size() ; i++) {
				if( mergeList.get(i).getRegisteredCount() > 1) lastC = startC+mergeList.get(i).getRegisteredCount()-1;
				else lastC = startC;
				aCount += startC+mergeList.get(i).getRegisteredCount();
				if(i==0) startC=1;
				
				if(lastC > startC) {
					sheet.addMergedRegion(new CellRangeAddress(startC, lastC, 0, 0));
				    sheet.addMergedRegion(new CellRangeAddress(startC, lastC, 2, 2));
				}
				startC = lastC+1;
			}
			
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel"); 
		    //response.setHeader("Content-Disposition", "attachment;filename="+fileName);	
		    response.setHeader("Content-disposition", "attachment; filename=" + new String(sheetName.getBytes(),"ISO8859_1") + "_"+titleDate+ ".xls");
	
		    // 엑셀 출력
		    wb.write(response.getOutputStream());
		    wb.close();
		    
	   } catch (DataAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/gms/statistics/customer/update.do")
	public ModelAndView getWorkReportUpdate(
			HttpServletRequest request
			, HttpServletResponse response
			, StatisticsCustomerBottleVO param) {
		
		//RequestUtils.initUserPrgmInfo(request, param);				
		
		ModelAndView mav = new ModelAndView();
		WorkReportVO workReport = workService.getWorkReport(param.getWorkReportSeq());
		
		mav.addObject("workReport", workReport);	 	
		mav.addObject("searchUserId", workReport.getUserId());
		mav.addObject("productId", param.getProductId());
		mav.addObject("productPriceSeq", param.getProductPriceSeq());
		mav.addObject("searchStatDt", param.getSearchStatDt());
		
		ProductPriceSimpleVO sProduct = new ProductPriceSimpleVO();
		sProduct.setProductId(param.getProductId());
		sProduct.setProductPriceSeq(param.getProductPriceSeq());
		
		ProductPriceSimpleVO productPriceSimple =  productService.getProductPriceSimple(sProduct);
		mav.addObject("productPriceSimple", productPriceSimple);
		
		if(request.getParameter("action") != null) mav.addObject("action", request.getParameter("action"));
		else mav.addObject("action", "update");	 
		//mav.addObject("searchDt", DateUtils.convertDateFormat(workReport.getSearchDt(), "yyyy/MM/dd"));
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_cbottle"));
		mav.setViewName("gms/statistics/customer/update");
		return mav;
	}
	
	@RequestMapping(value = "/gms/statistics/customer/modify.do")
	public ModelAndView getWorkReportModify(HttpServletRequest request
			, HttpServletResponse response
			, WorkReportVO param) {

		int result = 1;
		try {			
			//RequestUtils.initUserPrgmInfo(request, param);	
			if(param.getUserId() !=null ) {			
				param.setUserId(param.getUserId());
				param.setCreateId(param.getUserId());
				param.setUpdateId(param.getUserId());
			}else {				
				RequestUtils.initUserPrgmInfo(request, param);	
				param.setUserId(param.getCreateId());
			}
			ModelAndView mav = new ModelAndView();		
			result = workService.modifyWorkBottleManual(request,param);
			// WorkReport 정보 변경			
			
			mav.addObject("workReportSeq", param.getWorkReportSeq());	 	
			mav.addObject("action", "modify");	 	
					
			mav.setViewName("gms/statistics/customer/update");
			if(result > 0){
				String alertMessage = "수정되었습니다.";
				RequestUtils.responseWriteException(response, alertMessage,
						"/gms/statistics/customer/update.do?workReportSeq="+param.getWorkReportSeq()+"&productId="+request.getParameter("productId")
						+"&productPriceSeq="+request.getParameter("productPriceSeq")+ "&searchStatDt="+param.getSearchStatDt()+"&searchUserId="+param.getSearchUserId()+"&action=modify");
			}
		} catch (DataAccessException e) {		
			logger.error(" getWorkReportModify DataAccessException==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {			
			logger.error(" getWorkReportModify Exception==="+e.toString());
			e.printStackTrace();
		}
		return null;		
	}
	
}

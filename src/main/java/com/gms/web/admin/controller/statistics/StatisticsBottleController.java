package com.gms.web.admin.controller.statistics;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import com.gms.web.admin.domain.statistics.StatisticsBottleVO;
import com.gms.web.admin.service.statistics.StatisticsBottleService;

@Controller
public class StatisticsBottleController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	
	@Autowired
	private StatisticsBottleService statService;
	
	
	@RequestMapping(value = "/gms/statistics/bottle/daily.do")
	public ModelAndView getStatisticsBottleDaily(StatisticsBottleVO params) {

		//logger.info("StatisticsBottleContoller getStatisticsBottleDaily");
		//logger.debug("StatisticsBottleContoller searchStatisticsBottleDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchBottleId = params.getSearchBottleId();
				
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
			//logger.debug("****** getStatisticsBottleDaily else *****getSearchStatDtFrom===*"+searchStatDtFrom);
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
			//logger.debug("****** getStatisticsBottleDaily else *****getSearchStatDtEnd===*"+searchStatDtEnd);			
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
		}		
		params.setSearchStatDt(searchStatDtFrom);
		
		List<StatisticsBottleVO> statBottleList = statService.getDailylStatisticsBottleList(params);
		
		mav.addObject("statList", statBottleList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);		
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_bottle"));	 		
		
		mav.setViewName("gms/statistics/bottle/daily");
		
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/statistics/bottle/monthly.do")
	public ModelAndView getStatisticsBottleMonthly(StatisticsBottleVO params) {

		logger.info("StatisticsBottleContoller getStatisticsBottleMonthly");
		//logger.debug("StatisticsBottleContoller searchStatisticsBottleDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
				
		String searchStatDt = params.getSearchStatDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(searchStatDt != null && searchStatDt.length() > 20) {						
			searchStatDtFrom = searchStatDt.substring(0, 10) ;			
			searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);			
		}else {			
			searchStatDtFrom = DateUtils.getNextDate(-366,"yyyy/MM/dd");
			//logger.debug("****** getStatisticsBottleMonthly *****getSearchStatDtFrom===*"+searchStatDtFrom);			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
			//logger.debug("****** getStatisticsBottleMonthly *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
		}		
		params.setSearchStatDt(searchStatDtFrom);
		List<StatisticsBottleVO> statBottleList = statService.getMontlylStatisticsBottleList(params);
		
		mav.addObject("statList", statBottleList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);			
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_bottle"));	 		
		
		mav.setViewName("gms/statistics/bottle/monthly");
		
		return mav;
	}
	
	
	
	@RequestMapping(value = "/gms/statistics/bottle/excelDaily.do")
	public void excelDownloadBottle(HttpServletResponse response,StatisticsBottleVO params){
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
				//logger.debug("****** getStatisticsBottleDaily else *****getSearchStatDtFrom===*"+searchStatDtFrom);
		
				searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
				//logger.debug("****** getStatisticsBottleDaily else *****getSearchStatDtEnd===*"+searchStatDtEnd);		
				
				params.setSearchStatDtFrom(searchStatDtFrom);
				params.setSearchStatDtEnd(searchStatDtEnd);
		
				searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
				
				params.setSearchStatDt(searchStatDt);
			}		
			
			List<StatisticsBottleVO> statBottleList = statService.getDailylStatisticsBottleList(params);
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("용기");
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
	
		    headStyle= ExcelStyle.getHeadStyle(headStyle);

		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    
		    bodyStyle= ExcelStyle.getBodyStyle(bodyStyle);

		    // 헤더 생성 날짜,입고,충전,판매,회수,폐기
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.stat.bottle.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {
		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		    
		   // 날짜,입고,충전,판매,회수,폐기
		    // 데이터 부분 생성
		    for(StatisticsBottleVO vo : statBottleList) {
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(0);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getStatDt());
		        
		        cell = row.createCell(1);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleInputCount());
		        
		        cell = row.createCell(2);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleChargeCount());
		        
		        cell = row.createCell(3);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleSalesCount());
		        
		        cell = row.createCell(4);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleRentalCount());
		        
		        cell = row.createCell(5);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleBackCount());
		    }	
	
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename=StatisticsBottleDaily_"+DateUtils.getDate()+".xls");	
	
		    // 엑셀 출력
		    wb.write(response.getOutputStream());
		    wb.close();
		    
	   } catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value = "/gms/statistics/bottle/excelMonthly.do")
	public void excelDownloadStatBottleMonthly(HttpServletResponse response,StatisticsBottleVO params){
	
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
				searchStatDtFrom = DateUtils.getNextDate(-366,"yyyy/MM/dd");
				//logger.debug("****** getStatisticsBottleDaily else *****getSearchStatDtFrom===*"+searchStatDtFrom);		
				searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
				//logger.debug("****** getStatisticsBottleDaily else *****getSearchStatDtEnd===*"+searchStatDtEnd);
		
				params.setSearchStatDtFrom(searchStatDtFrom);
				params.setSearchStatDtEnd(searchStatDtEnd);
		
				searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
				
				params.setSearchStatDt(searchStatDt);
			}		
			
			List<StatisticsBottleVO> statBottleList = statService.getMontlylStatisticsBottleList(params);
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("용기");
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
	
		    headStyle= ExcelStyle.getHeadStyle(headStyle);

		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    
		    bodyStyle= ExcelStyle.getBodyStyle(bodyStyle);
		 // 헤더 생성 날짜,입고,충전,판매,회수,폐기
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.stat.bottle.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {
		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		   
	
		   // 용기코드	바코드	가스	용량	작업	거래처	등록일
		    // 데이터 부분 생성
		    for(StatisticsBottleVO vo : statBottleList) {
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(0);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getStatDt());
		        
		        cell = row.createCell(1);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleInputCount());
		        
		        cell = row.createCell(2);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleChargeCount());
		        
		        cell = row.createCell(3);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleSalesCount());
		        
		        cell = row.createCell(4);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleRentalCount());
		        
		        cell = row.createCell(5);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleBackCount());
		        
		    }	
	
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename=StatisticsBottleMonthly_"+DateUtils.getDate()+".xls");	
	
		    // 엑셀 출력
		    wb.write(response.getOutputStream());
		    wb.close();
		    
	   } catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	}
	
}

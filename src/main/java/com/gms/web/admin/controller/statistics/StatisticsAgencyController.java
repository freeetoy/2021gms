package com.gms.web.admin.controller.statistics;

import java.util.List;
import java.util.Map;

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
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.statistics.StatisticsAgencyResultVO;
import com.gms.web.admin.domain.statistics.StatisticsAgencyResultVO2;
import com.gms.web.admin.domain.statistics.StatisticsAgencyVO;
import com.gms.web.admin.service.statistics.StatisticsAgencyService;

@Controller
public class StatisticsAgencyController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private StatisticsAgencyService statService;	
	
	
	@RequestMapping(value = "/gms/statistics/agency/daily1.do")
	public ModelAndView getStatisticsAgencyDaily(StatisticsAgencyVO param) {

		logger.info("StatisticsAgencyController getStatisticsAgencyDaily");
		//logger.debug("StatisticsCustomerContoller searchStatisticsCustomerDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchCustomerId = params.getSearchCustomerId();
				
		//String searchStatDt = param.getSearchStatDt();			
		/*
		if(searchStatDt == null ||  (searchStatDt != null && searchStatDt.length() < 6 ) ){
			searchStatDt = DateUtils.getNextDate(-1,"yyyy/MM/dd");
			param.setSearchStatDt(searchStatDt);
		}
		*/
		List<CustomerVO> customerList = statService.getStatisticsAgencyCustomerList(param);
		
		List<StatisticsAgencyResultVO> statAgency = statService.getDailylStatisticsAgencyList(param);
		
		mav.addObject("customerList", customerList);	
		mav.addObject("statAgency", statAgency);	
		/*
		for(int i=0 ; i < customerList.size() ; i++) {
			logger.debug("StatisticsCustomerContoller getCustomerId "+ customerList.get(i).getCustomerId());
			logger.debug("StatisticsCustomerContoller getCustomerNm "+ customerList.get(i).getCustomerNm());
			logger.debug("StatisticsCustomerContoller getParentCustomerId "+ customerList.get(i).getParentCustomerId());
			for ( int j = 0 ; j < statAgency.size() ; j++) {
				logger.debug("StatisticsCustomerContoller stat "+ statAgency.get(j).getProductNm());
				Integer[] countOwnList = statAgency.get(j).getBottleOwnCountList();
				for(int k=0;k<countOwnList.length ; k++) {
					logger.debug("StatisticsCustomerContoller countOwnList "+ countOwnList[k]);
				}
			};
		}
		*/
		//검색어 셋팅
		mav.addObject("searchStatDt", param.getSearchStatDt());				
	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_agency"));	 		
		
		mav.setViewName("gms/statistics/agency/daily");
		
		return mav;
	}
	
	@RequestMapping(value = "/gms/statistics/agency/daily.do")
	public ModelAndView getStatisticsAgencyDaily1(StatisticsAgencyVO param) {

		logger.info("StatisticsAgencyController getStatisticsAgencyDaily1");
		//logger.debug("StatisticsCustomerContoller searchStatisticsCustomerDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//List<CustomerVO> customerList = statService.getStatisticsAgencyCustomerList(param);
		Map<String, Object>  statResult = statService.getDailylStatisticsAgencyList1(param);
	//	List<StatisticsAgencyResultVO2> statAgency = statService.getDailylStatisticsAgencyList1(param);
		
		mav.addObject("customerList", statResult.get("customerList"));	
		mav.addObject("statAgency", statResult.get("statAgency"));	
		/*
		for(int i=0 ; i < customerList.size() ; i++) {
			logger.debug("StatisticsCustomerContoller getCustomerId "+ customerList.get(i).getCustomerId());
			logger.debug("StatisticsCustomerContoller getCustomerNm "+ customerList.get(i).getCustomerNm());
			logger.debug("StatisticsCustomerContoller getParentCustomerId "+ customerList.get(i).getParentCustomerId());
			for ( int j = 0 ; j < statAgency.size() ; j++) {
				logger.debug("StatisticsCustomerContoller stat "+ statAgency.get(j).getProductNm());
				Integer[] countOwnList = statAgency.get(j).getBottleOwnCountList();
				for(int k=0;k<countOwnList.length ; k++) {
					logger.debug("StatisticsCustomerContoller countOwnList "+ countOwnList[k]);
				}
			};
		}
		*/
		//검색어 셋팅
		mav.addObject("searchStatDt", param.getSearchStatDt());				
	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_agency"));	 		
		
		mav.setViewName("gms/statistics/agency/daily");
		
		return mav;
	}

	
	@RequestMapping(value = "/gms/statistics/agency/monthly.do")
	public ModelAndView getStatisticsAgencyMonthly(StatisticsAgencyVO param) {

		logger.info("StatisticsAgencyController getStatisticsAgencyMonthly");

		ModelAndView mav = new ModelAndView();
		
		//Integer searchCustomerId = params.getSearchCustomerId();
				
		String searchStatDt = param.getSearchStatDt();			
		if(searchStatDt == null ||  (searchStatDt != null && searchStatDt.length() < 6 ) ){
			searchStatDt = DateUtils.getNextDate(-30,"yyyy/MM");
			param.setSearchStatDt(searchStatDt);
		}
		
		List<CustomerVO> customerList = statService.getStatisticsAgencyCustomerList(param);
		
		List<StatisticsAgencyResultVO> statAgency = statService.getMontlylStatisticsAgencyList(param);
		
		mav.addObject("customerList", customerList);	
		mav.addObject("statAgency", statAgency);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);				
	
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_agency"));	 		
		
		mav.setViewName("gms/statistics/agency/monthly");
		
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/statistics/agency/excelDown.do")
	public void excelDownloadAgency(HttpServletResponse response,StatisticsAgencyVO param){
	// 게시판 목록조회

	   try {
		   // 가스 정보 불러오기
		   String searchStatDt = param.getSearchStatDt();			
		   
		   //List<CustomerVO> customerList = statService.getStatisticsAgencyCustomerList(param);
		   List<CustomerVO> customerList = null;
		   String sheetName = "대리점";
		   String fileName="Statistic_"+sheetName+"_";
			
		   List<StatisticsAgencyResultVO2> statAgency = null;
		   Map<String, Object>  statResult = statService.getDailylStatisticsAgencyList1(param);
			if(param.getPeriodType()==1) {
				statAgency = (List<StatisticsAgencyResultVO2>) statResult.get("statAgency");//statService.getDailylStatisticsAgencyList(param);
				fileName +="Daily_"+DateUtils.getDate();
			}else {
				//statAgency = statService.getMontlylStatisticsAgencyList(param);
				fileName +="Monthly_"+DateUtils.getDate();
			}
			customerList = (List<CustomerVO>) statResult.get("customerList");	
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
		   
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);		   
		    cell = row.createCell(0);
		    
		    for(int i=1;i<customerList.size()+1;i++) {
		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(customerList.get(i-1).getCustomerNm());		    
		    }
		    
		   // 날짜		주문건수	주문금액
		    // 데이터 부분 생성
		    for(StatisticsAgencyResultVO2 vo : statAgency) {
		    	int ind = 0;
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(ind++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getProductNm()+" "+vo.getProductCapa());
		        
		        for(int i =0; i < vo.getBottleOwnCountList().length;i++) {
		        	cell = row.createCell(ind++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleOwnCountList()[i]);
		        }		 
		    }	
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    cell = row.createCell(0);
	        cell.setCellStyle(bodyStyle);
	        cell.setCellValue("대여현황");
	        
	        for(StatisticsAgencyResultVO2 vo : statAgency) {
		    	int ind = 0;
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(ind++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getProductNm()+" "+vo.getProductCapa());
		        
		        for(int i =0; i < vo.getBottleRentCountList().length;i++) {
		        	cell = row.createCell(ind++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleRentCountList()[i]);
		        }		 
		    }	
		    
		    for (int x = 0; x < sheet.getRow(1).getPhysicalNumberOfCells(); x++) {
				sheet.autoSizeColumn(x);
				int width = sheet.getColumnWidth(x);
				int minWidth =  450;
				int maxWidth = 18000;
				if (minWidth > width) {
					sheet.setColumnWidth(x, minWidth);
				} else if (width > maxWidth) {
					sheet.setColumnWidth(x, maxWidth);
				} else {
					sheet.setColumnWidth(x, width + 2000);
				}
			}
		    
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel"); 
		    //response.setHeader("Content-Disposition", "attachment;filename="+fileName);	
		    response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"ISO8859_1") + ".xls");
	
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

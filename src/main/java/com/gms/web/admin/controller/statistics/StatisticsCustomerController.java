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
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.statistics.StatisticsCustomerVO;
import com.gms.web.admin.service.manage.CustomerService;
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
	private CustomerService customerService;
	
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
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	}
}

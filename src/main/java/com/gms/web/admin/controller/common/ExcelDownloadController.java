package com.gms.web.admin.controller.common;

import java.text.DecimalFormat;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.spi.http.HttpContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.ExcelStyle;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.OrderService;

@Controller
public class ExcelDownloadController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CustomerService customerService;


   @RequestMapping(value = "/gms/bottle/excelDownload.do")
   public void excelDownloadBottle(HttpServletResponse response, BottleVO param){
	
	   try {
		   
		   // 용기 정보 불러오기
		    param.setStartRow(0);
			List<BottleVO> bottleList = bottleService.getBottleListToExcel(param);
		    // 워크북 생성
			XSSFWorkbook wb = new XSSFWorkbook();
		    //orkbook wb = new HSSFWorkbook();
		    //Sheet sheet = (Sheet) wb.createSheet("용기");
			XSSFSheet sheet =  wb.createSheet("용기");
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
	
		    headStyle= ExcelStyle.getHeadStyle(headStyle);

		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    
		    bodyStyle= ExcelStyle.getBodyStyle(bodyStyle);
		    
		    // 헤더 생성
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    
		    List<String> list = null;		    
		    
		    if(param.getSearchWorkCd() != null  && param.getSearchWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.charge"))) {			
			    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.bottle.charge.title"), ","); 		
			    
			    for(int i =0;i<list.size();i++) {
			    
				    cell = row.createCell(i);
				    cell.setCellStyle(headStyle);
				    cell.setCellValue(list.get(i));		   
				    sheet.autoSizeColumn(i);
			    }
			   
			    //순번,바코드번호,용기번호,가스종류,품명,충전용량,충전압력,충전기한확인,진공배기,누출확인,용기타입,충전기한,작업자,삭제요청	
	            //0		1		2	3		4	5		6		7	8		9	 10		11	 12, 	13
		
			    // 데이터 부분 생성
			    for(BottleVO vo : bottleList) {
			    	int k=0;
			        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(k+1);
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleBarCd());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleId());  			        
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getGasCd());
			        
			        cell = row.createCell(k++);;
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getProductNm());
			        
			        cell = row.createCell(k++);;
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleCapa());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleChargePrss());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue("○");
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue("○");
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue("○");
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getBottleType().equals("F"))
			        	cell.setCellValue("실병");
			        else	
			        	cell.setCellValue("공병");
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getBottleChargeDt() !=null) 
			        	cell.setCellValue(DateUtils.convertDateFormat(vo.getBottleChargeDt(),"yyyy-MM-dd"));
			        else
			        	cell.setCellValue("");
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getUpdateId());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue("N");
		
			    }	
		    }else {	// 충전을 제외한 분류
		    	
		    	list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.bottle.title"), ","); 		
			    
			    for(int i =0;i<list.size();i++) {
			    
				    cell = row.createCell(i);
				    cell.setCellStyle(headStyle);
				    cell.setCellValue(list.get(i));		   
				    sheet.autoSizeColumn(i);
			    }
			   
			  //용기번호	바코드번호	가스종류	충전용량	제조월	충전기한	용기체적	사업자등록번호	GMP여부(Y/N)	품명	충전압력	용기소유(자사-self,타사-other)
            	//0		1			2	3		4		5		6		7			8			9		10		11	
			    // 데이터 부분 생성
			    //20210105 변경
			    //바코드번호	용기번호	가스종류	품명	용기체적	충전용량	충전압력	거래처	작업구분	용기타입	용기제조일	충전기한	작업일	작업자	삭제요청	GMP

			    for(BottleVO vo : bottleList) {
			    	int k=0;
			        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleBarCd());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleId());			        
			       
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getGasCd());
			        
			        cell = row.createCell(k++);;
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getProductNm());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleVolumn());
			        
			        cell = row.createCell(k++);;
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleCapa());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleChargePrss());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getCustomerNm());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleWorkCdNm());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getBottleType() !=null && vo.getBottleType().equals("F"))
			        	cell.setCellValue("실병");
			        else
			        	cell.setCellValue("공병");
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getBottleCreateDt()!=null)
			        	cell.setCellValue(DateUtils.convertDateFormat(vo.getBottleCreateDt(),"yyyy-MM-dd"));
			        else
			        	cell.setCellValue("");			        
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getBottleChargeDt() !=null) 
			        	cell.setCellValue(DateUtils.convertDateFormat(vo.getBottleChargeDt(),"yyyy-MM-dd"));
			        else
			        	cell.setCellValue("");
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getBottleWorkDt() !=null) 
			        	cell.setCellValue(DateUtils.convertDateFormat(vo.getBottleWorkDt(),"yyyy-MM-dd : hh:mm:ss"));
			        else
			        	cell.setCellValue("");
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleWorkId());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue("N");
			        
			        //GMP여부
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue("Y");
			        
			       
			    }	
	/*		    
			    param.setStartRow(30001);
			    
				//bottleList = bottleService.getBottleListToExcel(param);
				
				for(BottleVO vo : bottleList) {
			    	int k=0;
			        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleId());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleBarCd());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getGasCd());
			        
			        cell = row.createCell(k++);;
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleCapa());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getBottleCreateDt()!=null)
			        	cell.setCellValue(DateUtils.convertDateFormat(vo.getBottleCreateDt(),"yyyy-MM-dd"));
			        else
			        	cell.setCellValue("");			        
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getBottleChargeDt() !=null) 
			        	cell.setCellValue(DateUtils.convertDateFormat(vo.getBottleChargeDt(),"yyyy-MM-dd"));
			        else
			        	cell.setCellValue("");
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleVolumn());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(PropertyFactory.getProperty("common.Member.Comp.Daehan.businessNum"));
			        
			        //GMP여부
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue("");
			        
			        cell = row.createCell(k++);;
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getProductNm());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getBottleChargePrss());
			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getBottleOwnYn()!=null && vo.getBottleOwnYn().equals("Y"))
			        	cell.setCellValue("self");
			        else
			        	cell.setCellValue("other");					       
			        
			    }	
		    */
		    }
		    // width 자동조절
			for (int x = 0; x < sheet.getRow(1).getPhysicalNumberOfCells(); x++) {
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
			}
	
		    // 컨텐츠 타입과 파일명 지정
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); 		
			
		    //response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename=Bottle_"+DateUtils.getDate()+".xlsx");	
	
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
      
   @RequestMapping(value = "/gms/order/excelDownload.do")
   public void excelDownloadOrder(HttpServletResponse response, OrderVO param){
	// 게시판 목록조회

	   try {
		   	String searchOrderDt = param.getSearchOrderDt();	
			
			String searchOrderDtFrom = null;
			String searchOrderDtEnd = null;
					
			if(searchOrderDt != null && searchOrderDt.length() > 20) {
		
				searchOrderDtFrom = searchOrderDt.substring(0, 10) ;
				
				searchOrderDtEnd = searchOrderDt.substring(13, searchOrderDt.length()) ;
				
				param.setSearchOrderDtFrom(searchOrderDtFrom);
				param.setSearchOrderDtEnd(searchOrderDtEnd);				
			}		

		   // 가스 정보 불러오기
			List<OrderVO> orderlist = orderService.getOrderListToExcel(param);			
			
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("주문");
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
		    //순번,거래처명,상품명,용량,접수자,상태,요청일자,접수일
		    // 헤더 생성
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.order.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		    
		    //순번	거래처	품명	용량 주문액	접수자	상태	요청일자	접수일
		    // 데이터 부분 생성
		    int i = 1;
		    int idx = 0;
		    for(OrderVO vo : orderlist) {
		    	idx = 0;
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(i++);
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerNm());
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderProductNm());
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderProductCapa());
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderTotalAmount());
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCreateNm()+"("+vo.getCreateId()+")");
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderProcessCdNm());
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(DateUtils.convertDateFormat(vo.getDeliveryReqDt(), "yyyy/MM/dd"));
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(DateUtils.convertDateFormat(vo.getCreateDt(), "yyyy/MM/dd"));
	
		    }	
	
		 // width 자동조절
 			for (int x = 0; x < sheet.getRow(1).getPhysicalNumberOfCells(); x++) {
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
 			}
		 			
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename=Order_"+DateUtils.getDate()+".xls");	
	
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
   
   @RequestMapping(value = "/gms/order/excelOrder.do")
   public void excelOrder(HttpServletResponse response, OrderVO param){
	// 게시판 목록조회

	   try {
		   	String searchOrderDt = param.getSearchOrderDt();	
			
			String searchOrderDtFrom = null;
			String searchOrderDtEnd = null;
					
			if(searchOrderDt != null && searchOrderDt.length() > 20) {
		
				searchOrderDtFrom = searchOrderDt.substring(0, 10) ;
				
				searchOrderDtEnd = searchOrderDt.substring(13, searchOrderDt.length()) ;
				
				param.setSearchOrderDtFrom(searchOrderDtFrom);
				param.setSearchOrderDtEnd(searchOrderDtEnd);				
			}		

		   // order 정보 불러오기
			List<OrderVO> orderlist = orderService.getOrderListExcel(param);	
			
			// order 정보 불러오기
			List<OrderProductVO> orderProductList = orderService.getAllOrderListExcel(param);	
			
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("주문");
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
		    //순번,지역,거래처명,상품명,용량,접수자,상태,요청일자,접수일
		    // 헤더 생성
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.stat.order.0210.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		    DecimalFormat df = new DecimalFormat("###,###.##");
		    //주문건수,거래처,주문상품,주문금액,기타,확인
		    // 데이터 부분 생성
		    int i = 1;
		    int idx = 0;
		    for(OrderVO vo : orderlist) {
		    	idx = 0;
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(i++);
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerCity());  
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerNm());      
		        		        		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        StringBuffer sb = new StringBuffer();
		        for(OrderProductVO prd : orderProductList) {
		        	if(vo.getOrderId().equals( prd.getOrderId()) ) {
		        		sb.append(prd.getProductNm()).append(" ").append(prd.getProductCapa());
				        if(prd.getGasId() > 0) {
							if(prd.getProductCapa().indexOf("Kg") < 0) sb.append("L");
						}
				        
				        double price = prd.getOrderAmount() / prd.getOrderCount();
				        String strPrice = df.format(price);
		        		sb.append("   ").append(prd.getOrderCount()).append(", ").append(strPrice).append(" ");
		        		if(prd.getBottleChangeYn() !=null && prd.getBottleChangeYn().equals("Y") ){
		        			sb.append("대여 / ");
		        		}else {
		        			sb.append("판매 / ");
		        		}
		        		
		        		if(prd.getRetrievedYn().equals("Y")) {
		        			sb.append(" ").append(prd.getProductNm()).append(" ").append(prd.getProductCapa()).append(" 회수 / ");
		        		}
		        		if(prd.getAsYn().equals("Y")) {
		        			sb.append(" ").append(prd.getProductNm()).append(" ").append(prd.getProductCapa()).append(" AS / ");
		        		}
		        	}
		        }
		        cell.setCellValue(sb.toString());  
			        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(df.format(vo.getOrderTotalAmount()*1.1));
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getOrderEtc());
		        
		        cell = row.createCell(idx++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue("               ");
	
		    }	
		    idx = 0;
	        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
	        cell = row.createCell(0);
	       // cell.setCellStyle(bodyStyle);
	        cell.setCellValue("주문금액은 부가세 포함입니다");
	
		 // width 자동조절
		    if(orderlist.size() > 0 ) {
	 			for (int x = 1; x < sheet.getRow(1).getPhysicalNumberOfCells(); x++) {
	 				sheet.autoSizeColumn(x);
	 				int width = sheet.getColumnWidth(x);
	 				int minWidth = list.get(x).getBytes().length * 350;
	 				int maxWidth = 18000;
	 				if (minWidth > width) {
	 					sheet.setColumnWidth(x, minWidth);
	 				} else if (width > maxWidth) {
	 					sheet.setColumnWidth(x, maxWidth);
	 				} else {
	 					sheet.setColumnWidth(x, width + 2000);
	 				}
	 			}
		    }
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename=Order_"+searchOrderDt+".xls");	
	
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
   
   @RequestMapping(value = "/gms/customer/excelDownload.do")
   public void excelDownloadCustomer(HttpServletResponse response,CustomerVO param){
	// 게시판 목록조회

	   try {
		   // 가스 정보 불러오기
			List<CustomerVO> customerlist = customerService.searchCustomerListExcel(param.getSearchCustomerNm());
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("거래처");
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
		    //거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
		    //0		1		2				3		4		5	6	7

		    // 헤더 생성
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.customer.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		    
		  //거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
		    //0		1		2				3		4		5	6	7
		    // 데이터 부분 생성
		   		    
		    for(CustomerVO vo : customerlist) {
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);		        
		        
		        cell = row.createCell(0);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerNm());
		        
		        cell = row.createCell(1);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerAddr());
		        
		        cell = row.createCell(2);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBusinessRegId());
		        
		        cell = row.createCell(3);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerPhone());
		        
		        cell = row.createCell(4);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerRepNm());
		        
		        cell = row.createCell(5);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerBusiType());
		        
		        cell = row.createCell(6);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerItem());
		        
		        cell = row.createCell(7);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerEmail());
	
		    }	
	
		 // width 자동조절
 			for (int x = 0; x < sheet.getRow(1).getPhysicalNumberOfCells(); x++) {
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
 			}
		 			
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("application/vnd.ms-excel");
		    response.setHeader("Content-Disposition", "attachment;filename=Customer_"+DateUtils.getDate()+".xls");	
	
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
   
   
   @RequestMapping(value = "/gms/customer/excelDownloadBottle.do")
   public void excelDownloadCustomerBottle(HttpServletResponse response,BottleVO param){
	// 게시판 목록조회

	   try {
		   // 가스 정보 불러오기
		   	BottleVO bottle = new BottleVO();
			bottle.setCustomerId(param.getCustomerId());
			
			CustomerVO customer = customerService.getCustomerDetails(param.getCustomerId());
			
			String fileName = customer.getCustomerNm()+"_용기_";
			//bottle.
			String searchChargeDt = param.getSearchChargeDt();	
			
			String searchChargeDtFrom = null;
			String searchChargeDtEnd = null;
					
			if(searchChargeDt != null && searchChargeDt.length() > 20) {
				
				//logger.debug("BottleContoller searchChargeDt "+ searchChargeDt.length());
				searchChargeDtFrom = searchChargeDt.substring(0, 10) ;
				
				searchChargeDtEnd = searchChargeDt.substring(13, searchChargeDt.length()) ;
				
				bottle.setSearchChargeDt(searchChargeDt);		
				bottle.setSearchChargeDtFrom(searchChargeDtFrom);
				bottle.setSearchChargeDtEnd(searchChargeDtEnd);
				
			}else {
				
				searchChargeDtFrom = DateUtils.getDate("yyyy/MM/dd");				
				searchChargeDtEnd = DateUtils.getDate("yyyy/MM/dd");
				
				bottle.setSearchChargeDt(searchChargeDtFrom+" - "+searchChargeDtEnd);
				bottle.setSearchChargeDtFrom(searchChargeDtFrom);
				bottle.setSearchChargeDtEnd(searchChargeDtEnd);
			}
			
			List<BottleVO> bottleList = bottleService.getCustomerBottleListDate(bottle);	
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("거래처");
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
		   
		    // 헤더 생성
		    List<String> list = null;		    
		    //list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.customer.bottle.title"), ","); 
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.bottle.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		    
		  //용기,바코드/RFID, 품명,가스용량,구분,타입,날짜
		    //0		1		2	3	 4	 5	6	
		    // 데이터 부분 생성
		
		    for(BottleVO vo : bottleList) {
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);		        
		        int k=0;
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleId());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleBarCd());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getGasCd());
		        
		        cell = row.createCell(k++);;
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleCapa());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        if(vo.getBottleCreateDt()!=null)
		        	cell.setCellValue(DateUtils.convertDateFormat(vo.getBottleCreateDt(),"yyyy-MM-dd"));
		        else
		        	cell.setCellValue("");			        
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        if(vo.getBottleChargeDt() !=null) 
		        	cell.setCellValue(DateUtils.convertDateFormat(vo.getBottleChargeDt(),"yyyy-MM-dd"));
		        else
		        	cell.setCellValue("");
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleVolumn());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(PropertyFactory.getProperty("common.Member.Comp.Daehan.businessNum"));
		        
		        //GMP여부
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue("");
		        
		        cell = row.createCell(k++);;
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getProductNm());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getBottleChargePrss());
		        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        if(vo.getBottleOwnYn()!=null && vo.getBottleOwnYn().equals("Y"))
		        	cell.setCellValue("self");
		        else
		        	cell.setCellValue("other");		
		        
		    }	
	
		    
		 // width 자동조절
 			for (int x = 0; x < sheet.getRow(1).getPhysicalNumberOfCells(); x++) {
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
 			}
		 			
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("application/vnd.ms-excel");
		    response.setHeader("Content-Disposition", "attachment;filename="+ new String(fileName.getBytes(),"ISO8859_1")+DateUtils.getDate()+".xls");	
	
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
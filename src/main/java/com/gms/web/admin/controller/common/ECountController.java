package com.gms.web.admin.controller.common;

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

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.ExcelStyle;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.ECountVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.service.manage.ECountService;

@Controller
public class ECountController {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ECountService eService;
	
	@RequestMapping(value = "/gms/excelDownloadEcount.do")
   public void excelDownloadBottle(HttpServletResponse response, WorkReportVO workReport){

	   try {			   
		   
		   String searchDt = workReport.getSearchDt();
		  
		   // 가스 정보 불러오기
			List<ECountVO> eList = eService.getECountList(workReport); 
			List<ECountVO> eMList = eService.getECountMinusList(workReport); 
		    // 워크북 생성
	
		    Workbook wb = new HSSFWorkbook();
		    Sheet sheet = (Sheet) wb.createSheet("판매입력");
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
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.ecount.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {
		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }
		   
		    //순번,거래일,구분,코드,거래처명,유형,적요,결제장부,거래금액,품목코드,품목명,규격,단위,수량,단가,공급가,부가세,합계금액,창고코드,창고명,비고,프로젝트,은행코드,카드코드
            // 0   1	2	3	4	 5	6	  7	   8		9  10	11  12 13  14   15	 16   17    18    19  20   21    22    23
	
		    String curDate = searchDt.replace("/", "-");		    
		    
		    int excelIndex = 1;
		    // 데이터 부분 생성
		    ECountVO prevEcount = null;
		    String productNm = "";
	        String productCapa = "";
	        String productSpec = "";
	        int orderCount = 0;
	        double supplyPrice = 0;	
	        double vat = 0;
		    for(ECountVO vo : eList) {
		    	
		    	if(prevEcount != null && !prevEcount.getCustomerNm().equals(vo.getCustomerNm()) ){
		    		excelIndex++;																						
		    	}
		    	int k=0;
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		      //  순번,거래일,구분,코드,거래처명,유형,적요,결제장부,거래금액,품목코드,품목명,규격,단위,수량,단가,공급가,부가세,합계금액,창고코드,창고명,비고,프로젝트,은행코드,카드코드
		        
		        supplyPrice = Math.round(vo.getSupplyPrice());
		        vat = Math.round(vo.getVat());
		        
		        if(StringUtils.isTankProduct(vo.getProductId()) ) {
		        	supplyPrice = Math.round(vo.getProductPrice()*vo.getChargeVolumn());
		        	vat = Math.round(supplyPrice * 0.1);
		        }
		       
		        if(vo.getAgencyYn().equals("Y")) {
		        	for(int i= 0 ; i < eMList.size() ; i++) {
		        		ECountVO mVo = eMList.get(i);
		        		
		        		if(mVo.getCustomerId().equals(vo.getCustomerId()) && mVo.getProductId().equals(vo.getProductId()) 
		        				&& mVo.getProductPriceSeq().equals(vo.getProductPriceSeq()) ) {
		        			orderCount = orderCount - mVo.getOrderCount();		        
		        			supplyPrice = Math.round(orderCount * vo.getProductPrice());
		        			vat = Math.round(supplyPrice * 0.1);
		        		}
		        	}
		        }
		        
		      //품목명	
		        productNm = vo.getProductNm();
		        productCapa = vo.getProductCapa();	
//		        productCapa = vo.getECountSpec();
		        orderCount = vo.getOrderCount();
		        //20211123 TB_Work_Bottle에 Charge_Volumn 컬럼 추가
		        if(StringUtils.isTankProduct(vo.getProductId()) )
		        	orderCount = vo.getChargeVolumn();
		       
		        if(vo.getGasId() > 0) productSpec = "B/T";
		        
		        if(vo.getProductId().equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId"))) ) {
		        	if(vo.getProductCapa().indexOf("_") >= 0 ) {		        		
		        		productNm = vo.getProductNm()+"("+ vo.getProductCapa().substring(2)+"L)";
		        		productCapa = "병";
		        	}
		        	else {
		        		productNm = vo.getProductNm();
		        		productCapa = "L";
		        		orderCount = Integer.parseInt(vo.getProductCapa())*vo.getOrderCount();
		        	}
		        }
		        if(productCapa.toLowerCase().indexOf("kg") < 0) productCapa +="L";
		        
		        //순번			        
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(Integer.toString(excelIndex));
		        
		        //일자
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(curDate);
		        
		        //구분
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(1);
		        
		        //거래처코드
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerEId());
		        
		        //거래처명
		        cell = row.createCell(k++);;
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getCustomerNm());
		        
		        //유형,
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getTaxType());
		        
		        //적요
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getSummary());
		        
		        //결제장부
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getPayType());
		        
		        //거래금액	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(supplyPrice+vat);
		        
		        //품목코드	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        if(vo.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale") ) ) {
		        	if(vo.getMultiYn().equals("Y")) cell.setCellValue(vo.getECountCd());
		        	else cell.setCellValue(vo.getECountCdS());
		        }else
		        	cell.setCellValue(vo.getECountCd());
		        
		        //품목명
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        if(vo.getProductNm().indexOf("믹스가스") > -1 && (vo.getGasCd()!=null && vo.getGasCd().length() > 0 ) )
		        	cell.setCellValue(vo.getProductNm()+"("+vo.getGasCd()+")");
		        else
		        	cell.setCellValue(productNm);
		        	//cell.setCellValue(vo.getProductNm());
		        
		        //규격	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(productCapa);
		        
		        //단위	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(productSpec);
		        //cell.setCellValue(vo.getProductCapa());
		   
		        //수량	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(orderCount);
		        
		        //단가	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        if(vo.getProductId() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId"))) {
		        	if(vo.getProductCapa().indexOf("_") >= 0 ) {	
		        		cell.setCellValue(vo.getProductPrice());
		        	}else {
//		        		logger.debug("****** registerWorkReportNoGas orderCount*****="+orderCount);
		        		cell.setCellValue(supplyPrice/orderCount);
		        	}
		        }else {
	        		cell.setCellValue(Math.round(vo.getProductPrice()));
		        }
		      
		        //공급가
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(supplyPrice);
		        
		        //부가세	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vat);
		        
		        //합계금액
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(supplyPrice+vat);
		        
		        //창고코드	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getEtc());

		        //창고명	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getEtc());
		        
		        //비고	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getEtc());
		     
		        //프로젝트	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getEtc());
		        
		        //은행코드	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getEtc());
		        
		        //카드코드	
		        cell = row.createCell(k++);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getEtc());
		        
		        
		        //판매주문 (판매 + 가스판매)/ 주문은 하나로, 이카운트 & 명세서는 분리, 용기목록 엑셀다운로드 수정
		       /* if(vo.getAgencyYn().equals("N") && vo.getGasPrice() > 0 && vo.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale"))) {
		        	k=0;
			        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
			        
			        //순번			        
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(Integer.toString(excelIndex));
			        
			        //일자
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(curDate);
			        
			        //구분
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(1);
			        
			        //거래처코드
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getCustomerEId());
			        
			        //거래처명
			        cell = row.createCell(k++);;
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getCustomerNm());
			        
			        //유형,
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getSalesNm());
			        
			        //적요
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getSummary());
			        
			        //결제장부
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getDealType());
			        
			        //거래금액	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getCurrency());
			        
			        //품목코드	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale") ) ) {
			        	if(vo.getMultiYn().equals("Y")) cell.setCellValue(vo.getECountCd());
			        	else cell.setCellValue(vo.getECountCdS());
			        }else
			        	cell.setCellValue(vo.getECountCd());
			       
			        //품목명	
			        productNm = vo.getProductNm();
			        //productCapa = vo.getProductCapa();	
			        productCapa = vo.getECountSpec();
			        orderCount = vo.getOrderCount();
			        //20211123 TB_Work_Bottle에 Charge_Volumn 컬럼 추가
			        if(StringUtils.isTankProduct(vo.getProductId()) )
			        	orderCount = vo.getChargeVolumn();
			  
			        supplyPrice = Math.round(vo.getSupplyPrice());
			        vat = Math.round(vo.getVat());
			        
			        if(StringUtils.isTankProduct(vo.getProductId()) ) {
			        	supplyPrice = Math.round(vo.getProductPrice()*vo.getChargeVolumn());
			        	vat = Math.round(supplyPrice * 0.1);
			        }
			       
			        if(vo.getAgencyYn().equals("Y")) {
			        	for(int i= 0 ; i < eMList.size() ; i++) {
			        		ECountVO mVo = eMList.get(i);
			        		
			        		if(mVo.getCustomerId().equals(vo.getCustomerId()) && mVo.getProductId().equals(vo.getProductId()) 
			        				&& mVo.getProductPriceSeq().equals(vo.getProductPriceSeq()) ) {
			        			orderCount = orderCount - mVo.getOrderCount();		        
			        			supplyPrice = Math.round(orderCount * vo.getProductPrice());
			        			vat = Math.round(supplyPrice * 0.1);
			        		}
			        	}
			        }
			        if(vo.getProductId().equals(Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId"))) ) {
			        	if(vo.getProductCapa().indexOf("_") >= 0 ) {		        		
			        		productNm = vo.getProductNm()+"("+ vo.getProductCapa().substring(2)+"L)";
			        		productCapa = "병";
			        	}
			        	else {
			        		productNm = vo.getProductNm();
			        		productCapa = "L";
			        		orderCount = Integer.parseInt(vo.getProductCapa())*vo.getOrderCount();
			        	}
			        }
			        //품목명
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getProductNm().indexOf("믹스가스") > -1 && (vo.getGasCd()!=null && vo.getGasCd().length() > 0 ) )
			        	cell.setCellValue(vo.getProductNm()+"("+vo.getGasCd()+")");
			        else
			        	cell.setCellValue(productNm);
			        	//cell.setCellValue(vo.getProductNm());
			        
			        //규격	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(productCapa);
			        
			        //단위	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(productCapa);
			        //cell.setCellValue(vo.getProductCapa());
			   
			        //수량	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(orderCount);
			        
			        //단가	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        if(vo.getProductId() == Integer.parseInt(PropertyFactory.getProperty("product.LN2.divide.new.productId"))) {
			        	if(vo.getProductCapa().indexOf("_") >= 0 ) {	
			        		cell.setCellValue(vo.getProductPrice());
			        	}else {
	//		        		logger.debug("****** registerWorkReportNoGas orderCount*****="+orderCount);
			        		cell.setCellValue(supplyPrice/orderCount);
			        	}
			        }else {
		        		cell.setCellValue(Math.round(vo.getProductPrice()));
			        }
			      
			        //공급가
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(supplyPrice);
			        
			        //부가세	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vat);
			        
			        //합계금액
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(supplyPrice);
			        
			        //창고코드	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getEtc());
	
			        //창고명	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getEtc());
			        
			        //비고	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getEtc());
			     
			        //프로젝트	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getEtc());
			        
			        //은행코드	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getEtc());
			        
			        //카드코드	
			        cell = row.createCell(k++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getEtc());
		        }*/
		        prevEcount = vo;
		    }	
		    // width 자동조절
		    if(eList.size() > 0) {
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
		    }
		    String fileName = "ERP_"+curDate+".xls";
		    if(workReport.getSearchUserId()!=null && workReport.getSearchUserId().length() > 0) fileName = "ERP_"+workReport.getSearchUserId()+"_"+curDate+".xls";
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");
		    response.setHeader("Content-Disposition", "attachment;filename="+fileName);	
	
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

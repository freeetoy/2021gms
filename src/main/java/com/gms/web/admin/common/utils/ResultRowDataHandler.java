package com.gms.web.admin.common.utils;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.ResultContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gms.web.admin.domain.manage.BottleVO;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

public class ResultRowDataHandler implements ResultHandler<BottleVO> {
	   private Logger logger = LoggerFactory.getLogger(getClass());
	   
	   private HttpServletResponse response;
	   private SXSSFWorkbook workbook;
	   private SXSSFSheet sheet;
	   private boolean isStarted = false;


	   public ResultRowDataHandler(HttpServletResponse response){
	      this.response=response;

	      //메모리에 100개의 행을 누적후 행의 수가 10000개를 넘을시 디스크에 기록한다.
	      workbook = new SXSSFWorkbook(10000);
	      sheet = workbook.createSheet();
	   }

	   @Override
	   public void handleResult(ResultContext<? extends BottleVO> resultContext) {
		   BottleVO commonVo = resultContext.getResultObject();
	      if(!isStarted){
	         open(resultContext.getResultObject());
	         isStarted = true;
	      }

	      // 현재 처리중인 행번호(1부터 시작됨.)
	      Row row = sheet.createRow(resultContext.getResultCount() - 1);
	      Cell cell = null;
	      cell = row.createCell(0);
	      cell.setCellValue(commonVo.getBottleId());

	      cell = row.createCell(1);
	      cell.setCellValue(commonVo.getBottleBarCd());

	      cell = row.createCell(2);
	      cell.setCellValue(commonVo.getGasNm());

	      cell = row.createCell(3);
	      cell.setCellValue(commonVo.getBottleVolumn());

	      cell = row.createCell(4);
	      cell.setCellValue(commonVo.getBottleWorkCdNm());

	      cell = row.createCell(5);
	      cell.setCellValue(commonVo.getCustomerNm());
	   }

	   public void open(BottleVO resultObject){
	      response.setHeader("Set-Cookie", "fileDownload=true; path=/");
	      response.setHeader("Content-Disposition", String.format("attachment; filename=\"test.xlsx\""));
	   }

	   public void close(){
	      try {
	         workbook.write(response.getOutputStream());
	         workbook.dispose();
	      }catch (Exception e){
	         logger.error("Exception : {}", e);
	         response.setHeader("Set-Cookie", "fileDownload=false; path=/");
	         response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	         response.setHeader("Content-Type","text/html; charset=utf-8");
	         OutputStream out = null;
	         try {
	            out = response.getOutputStream();
	            byte[] data = new String ("fail Download......").getBytes();
	            out.write(data,0,data.length);
	         }catch (Exception ex){
	            logger.error("Exception : {}", ex);
	         }
	      }finally {
	         if(workbook != null){
	            try { workbook.close(); }catch(Exception ex){}
	         }
	      }
	   }


	}
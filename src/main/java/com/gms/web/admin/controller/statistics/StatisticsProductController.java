package com.gms.web.admin.controller.statistics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.DeduplicationUtils;
import com.gms.web.admin.common.utils.ExcelStyle;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.ProductVO;
import com.gms.web.admin.domain.statistics.StatisticsAllProductVO;
import com.gms.web.admin.domain.statistics.StatisticsProductVO;
import com.gms.web.admin.domain.statistics.StatisticsProuctCustomerCountVO;
import com.gms.web.admin.service.manage.ProductService;
import com.gms.web.admin.service.statistics.StatisticsProductService;

@Controller
public class StatisticsProductController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private StatisticsProductService statService;
	
	@Autowired
	private ProductService productService;
	
	@RequestMapping(value = "/gms/statistics/product/daily.do")
	public ModelAndView getStatisticsProductDaily(StatisticsProductVO params) {

		logger.info("StatisticsProductContoller getStatisticsProductDaily");
		//logger.debug("StatisticsProductContoller searchStatisticsProductDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();		
		//Integer searchProductId = params.getSearchProductId();				
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
			//logger.debug("****** getStatisticsProductDaily else *****getSearchStatDtFrom===*"+searchStatDtFrom);			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
			//logger.debug("****** getStatisticsProductDaily else *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
			params.setSearchStatDt(searchStatDt);
		}
		
		
		List<StatisticsProductVO> statProductList = statService.getDailylStatisticsProductList(params);
		
		mav.addObject("statProductList", statProductList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);	
		mav.addObject("searchProductId", params.getSearchProductId());	
		if(params.getProductPriceSeq() !=null)
			mav.addObject("productPriceSeq", params.getProductPriceSeq());	
		else 
			mav.addObject("productPriceSeq", "0");
		
		List<ProductVO> productList = productService.getProductList();
		mav.addObject("productList", productList);
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_product"));			
		
		mav.setViewName("gms/statistics/product/daily");
		
		return mav;
	}

	
	@RequestMapping(value = "/gms/statistics/product/monthly.do")
	public ModelAndView getStatisticsProductMonthly(StatisticsProductVO params) {

		logger.info("StatisticsProductContoller getStatisticsProductMonthly");
		//logger.debug("StatisticsProductContoller searchStatisticsProductDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchProductId = params.getSearchProductId();
				
		String searchStatDt = params.getSearchStatDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(searchStatDt != null && searchStatDt.length() > 20) {						
			searchStatDtFrom = searchStatDt.substring(0, 7) ;			
			searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()-3) ;
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);			
		}else {			
			searchStatDtFrom = DateUtils.getNextDate(-366,"yyyy/MM");
			//logger.debug("****** getMonthlylStatisticsProductList else *****getSearchStatDtFrom===*"+searchStatDtFrom);			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM");
			//logger.debug("****** getMonthlylStatisticsProductList else *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
			
			params.setSearchStatDt(searchStatDt);
		}
		
		
		List<StatisticsProductVO> statProductList = statService.getMontlylStatisticsProductList(params);
		
		mav.addObject("statProductList", statProductList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);	
		mav.addObject("searchProductId", params.getSearchProductId());
		if(params.getProductPriceSeq() !=null)
			mav.addObject("productPriceSeq", params.getProductPriceSeq());	
		else 
			mav.addObject("productPriceSeq", "0");
		List<ProductVO> productList = productService.getProductList();
		mav.addObject("productList", productList);
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_product"));	 		
		
		mav.setViewName("gms/statistics/product/monthly");
		
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/statistics/product/excelDown.do")
	public void excelDownloadBottle(HttpServletResponse response,StatisticsProductVO params){
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
				searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
				
				if(params.getPeriodType()==1) {	
					searchStatDtFrom = DateUtils.getNextDate(-31,"yyyy/MM/dd");					
				}else {
					searchStatDtFrom = DateUtils.getNextDate(-366,"yyyy/MM/dd");
					searchStatDtFrom = searchStatDtFrom.substring(0,7);
					searchStatDtEnd = searchStatDtEnd.substring(0,7);
				}
				params.setSearchStatDtFrom(searchStatDtFrom);
				params.setSearchStatDtEnd(searchStatDtEnd);
		
				searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
				
				params.setSearchStatDt(searchStatDt);
			}		
			
			BottleVO prdouctPriceParm = new BottleVO();
			prdouctPriceParm.setProductId(params.getSearchProductId());
			prdouctPriceParm.setProductPriceSeq(params.getProductPriceSeq());
			
			ProductTotalVO productPrice = productService.getBottleGasCapa(prdouctPriceParm);
			String sheetName = "상품";
			if(productPrice!= null) sheetName = productPrice.getProductNm()+"_"+productPrice.getProductCapa()+"_";
			
			List<StatisticsProductVO> statProductList = null;
			
			String fileName="StatisticProduct_"+sheetName;
			if(params.getPeriodType()==1) {
				statProductList = statService.getDailylStatisticsProductList(params);
				fileName +="Daily_"+DateUtils.getDate();
			}else {
				statProductList = statService.getMontlylStatisticsProductList(params);
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
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.stat.product.title"), ","); 		
		    
		    for(int i =0;i<list.size();i++) {
		    
			    cell = row.createCell(i);
			    cell.setCellStyle(headStyle);
			    cell.setCellValue(list.get(i));		    
		    }


		   // 날짜		주문건수	주문금액
		    // 데이터 부분 생성
		    for(StatisticsProductVO vo : statProductList) {
		        row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		        cell = row.createCell(0);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getStatDt());
		        
		        cell = row.createCell(1);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getSaleCount());
		        
		        cell = row.createCell(2);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getSaleAmount());	        
		       
		        cell = row.createCell(3);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getRentCount());
		        
		        cell = row.createCell(4);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(vo.getRentAmount());	  
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
	
	@RequestMapping(value = "/gms/statistics/product/newExcelDown.do")
	public void newExcelDownloadBottle(HttpServletResponse response,StatisticsProductVO params){
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
				searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
				
				if(params.getPeriodType()==1) {	
					searchStatDtFrom = DateUtils.getNextDate(-31,"yyyy/MM/dd");					
				}else {
					searchStatDtFrom = DateUtils.getNextDate(-366,"yyyy/MM/dd");
					searchStatDtFrom = searchStatDtFrom.substring(0,7);
					searchStatDtEnd = searchStatDtEnd.substring(0,7);
				}
				params.setSearchStatDtFrom(searchStatDtFrom);
				params.setSearchStatDtEnd(searchStatDtEnd);
		
				searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
				
				params.setSearchStatDt(searchStatDt);
			}		
			
			BottleVO prdouctPriceParm = new BottleVO();
			prdouctPriceParm.setProductId(params.getSearchProductId());
			prdouctPriceParm.setProductPriceSeq(params.getProductPriceSeq());
			
//			ProductTotalVO productPrice = productService.getBottleGasCapa(prdouctPriceParm);
			
			List<ProductPriceSimpleVO> allProudctList = productService.getTotalProductSimpleList();
			List<ProductPriceSimpleVO> rProudctList = new ArrayList<ProductPriceSimpleVO>();
			
			String sheetName = "상품";
//			if(productPrice!= null) sheetName = productPrice.getProductNm()+"_"+productPrice.getProductCapa()+"_";
			
			List<StatisticsProductVO> statProductList = null;
			
			//중간값 모음
			List<StatisticsProductVO> statResultList = new ArrayList<StatisticsProductVO>();
			
			// 총 결과
			List<StatisticsAllProductVO> statAllResultist = new ArrayList<StatisticsAllProductVO>();
			
			
			String fileName="상품통계_";
			if(params.getPeriodType()==1) {
				statProductList = statService.getDailylStatisticsAllProductList(params);
				fileName +="일별_"+DateUtils.getDate();
			}else {
				statProductList = statService.getMontlylStatisticsAllProductList(params);
				fileName +="월별_"+DateUtils.getDate();
			}
		    // 워크북 생성
	
		    Workbook wb = new XSSFWorkbook();			//cell 255 초과
		    Sheet sheet = (Sheet) wb.createSheet(sheetName);
		    Row row = null;
		    Cell cell = null;
	
		    int rowNo = 0;
		    
		    // 테이블 헤더용 스타일
		    CellStyle headStyle = wb.createCellStyle();
		    headStyle= ExcelStyle.getHeadStyle(headStyle);

		    // 데이터용 경계 스타일 테두리만 지정
		    CellStyle bodyStyle = wb.createCellStyle();
		    bodyStyle = ExcelStyle.getBodyStyle(bodyStyle);
		    
		    CellStyle rightStyle = wb.createCellStyle();
		    rightStyle = ExcelStyle.getBodyStyle(rightStyle);
		    rightStyle.setAlignment(HorizontalAlignment.RIGHT);
		   
		    // 헤더 생성 날짜		주문건수	주문금액
		    //			0	1		2		
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    
		    List<String> list = null;		    
		    list = StringUtils.makeForeach(PropertyFactory.getProperty("excel.stat.allproduct.title"), ","); 		
		    
		    // TODO
		    // 날짜 목록과 제품 목록 가져와서 List 조합
//		  //이름이 같은 사람은 제거
	        List<StatisticsProductVO> statDtist = DeduplicationUtils.deduplication(statProductList, StatisticsProductVO::getStatDt);
//
//	        statDtist.forEach(lists -> System.out.println(lists.toString()));
	        
	        //이름이 같은 사람은 제거
//	        List<StatisticsProductVO> productList = DeduplicationUtils.deduplication(statProductList, StatisticsProductVO::getProductId);
	        List<StatisticsProductVO> productList =
	                DeduplicationUtils.deduplication(
	                		statProductList,
	                        p -> java.util.Arrays.asList(
	                                p.getProductId(),
	                                p.getProductPriceSeq())
	                );
	        productList.sort(
	        	    Comparator.comparing(StatisticsProductVO::getProductId)
	        	              .thenComparing(StatisticsProductVO::getProductPriceSeq)
	        	);
//	        productList.forEach(lists -> System.out.println(lists.toString()));
//	        System.out.println("productList size "+productList.size());
	        
	        for(StatisticsProductVO vo1 : productList) {
	        	
	        	for(StatisticsProductVO vo : statDtist) {
	        		StatisticsProductVO statProduct = new  StatisticsProductVO();
	        		statProduct.setStatDt(vo.getStatDt());
	        		statProduct.setProductId(vo1.getProductId());
	        		statProduct.setProductPriceSeq(vo1.getProductPriceSeq());
	       
	        		statResultList.add(statProduct);
	        	}
	        	
	        	for(ProductPriceSimpleVO vo : allProudctList) {
	        		if(vo1.getProductId().equals(vo.getProductId()) && vo1.getProductPriceSeq().equals(vo.getProductPriceSeq())) {
//	        			logger.debug("##### ProductPriceSimpleVO  ******"+vo.getProductNm()+"_" +vo.getProductCapa());		
	        			rProudctList.add(vo);
	        		}
	        	}
	        }
	       
//	        for(ProductPriceSimpleVO rP : rProudctList) {
//	        	logger.debug("##### ProductPriceSimpleVO  ******"+rP.getProductNm()+"_" +rP.getProductCapa());		
//	        }
//	        
	     // Product 정보 노출칸 java.lang.IllegalArgumentException: Maximum column number is 255
		    int col = 1;
		    for(ProductPriceSimpleVO rP : rProudctList) {

		        Cell cell1 = row.createCell(col);
		        cell1.setCellStyle(headStyle);
		        cell1.setCellValue(rP.getProductNm()+"_" +rP.getProductCapa());

		        // 4칸 병합
		        sheet.addMergedRegion(
		                new CellRangeAddress(
		                        0,      // 시작 행
		                        0,      // 끝 행
		                        col,    // 시작 열
		                        col+3   // 끝 열
		                ));

		        col += 4;
		    }
	        
		    row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
		    cell = row.createCell(0);
		    cell.setCellStyle(headStyle);
		    cell.setCellValue("날짜");		
		    int cellNum = 1;
		    for(ProductPriceSimpleVO rP : rProudctList) {
		    	 for(int i =0;i<list.size();i++) {
		 		    
					    cell = row.createCell(cellNum++);
					    cell.setCellStyle(headStyle);
					    cell.setCellValue(list.get(i));		    
				    }
		    }
		    
	        for(StatisticsProductVO vo : statResultList) {
	        	
	        	for(StatisticsProductVO vo1 : statProductList) {
	        		if(vo.getStatDt().equals(vo1.getStatDt()) && vo.getProductId().equals(vo1.getProductId()) && vo.getProductPriceSeq().equals(vo1.getProductPriceSeq())) {
	        			vo.setSaleCount(vo1.getSaleCount());
	        			vo.setSaleAmount(vo1.getSaleAmount());
	        			vo.setRentCount(vo1.getRentCount());
	        			vo.setRentAmount(vo1.getRentAmount());
	        		}
	        	}
	        }
//	        System.out.println("statResultList size "+statResultList.size());
//	        statResultList.forEach(lists -> System.out.println(lists.toString()));
	        
	        // 통계 날짜 셋팅
	        for(StatisticsProductVO vo : statDtist) {
	        	StatisticsAllProductVO statisticsAllProductVO = new StatisticsAllProductVO();
	        	statisticsAllProductVO.setStatDt(vo.getStatDt());
	        	
	        	List<StatisticsProductVO> statAllList =  new ArrayList<StatisticsProductVO>();
	        	statisticsAllProductVO.setStatAllList(statAllList);
	        	statAllResultist.add(statisticsAllProductVO);
	        }
	        // 통계 날짜별 상품
	        for(StatisticsAllProductVO allVo : statAllResultist) {
	        	List<StatisticsProductVO> productResultList = allVo.getStatAllList();
	        	
	        	for(StatisticsProductVO vo : statResultList) {
//	        		logger.debug("@@@  StatisticsAllProductVO  *****getSearchStatDtFrom===*"+vo.getStatDt());	
	        		if(allVo.getStatDt().equals(vo.getStatDt())) {
//	        			logger.debug("*@@@@@@@@@@ StatisticsAllProductVO *****getProductId="+vo.getProductId()+" getProductPriceSeq = "+vo.getProductPriceSeq() +" getRentCount = "+vo.getRentCount()+" getSaleCount = "+vo.getSaleCount());
	        			productResultList.add(vo);
	        		}
	        	}
	        }
	        
	        DecimalFormat df1 = new DecimalFormat();
	        
	        for(StatisticsAllProductVO allVo : statAllResultist) {
	        	row = ((org.apache.poi.ss.usermodel.Sheet) sheet).createRow(rowNo++);
	        	cell = row.createCell(0);
		        cell.setCellStyle(bodyStyle);
		        cell.setCellValue(allVo.getStatDt());
		        
	        	List<StatisticsProductVO> productResultList = allVo.getStatAllList();
	        	int i = 1;
	        	for(StatisticsProductVO vo : productResultList) {
	        		cell = row.createCell(i++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getSaleCount());
			        
			        cell = row.createCell(i++);
			        cell.setCellStyle(rightStyle);
			        cell.setCellValue(df1.format(vo.getSaleAmount()));	        
			       
			        cell = row.createCell(i++);
			        cell.setCellStyle(bodyStyle);
			        cell.setCellValue(vo.getRentCount());
			        
			        cell = row.createCell(i++);
			        cell.setCellStyle(rightStyle);
			        cell.setCellValue(df1.format(vo.getRentAmount()));	
//	        		logger.debug("****** StatisticsAllProductVO *****getProductId="+vo.getProductId()+" getProductPriceSeq = "+vo.getProductPriceSeq() +" getRentCount = "+vo.getRentCount()+" getSaleCount = "+vo.getSaleCount());	
	        	}
	        }
	        
//	        statProductList.sort(
//	        	    Comparator.comparing(StatisticsProductVO::getStatDt, Comparator.reverseOrder())
//	        	              .thenComparing(StatisticsProductVO::getProductId)
//	        	              .thenComparing(StatisticsProductVO::getProductPriceSeq)
//	        	);
//	        statProductList.forEach(lists -> System.out.println(lists.toString()));
	        
//* CellRangeAddress(행번호,행몇개, 열번호, 열몇개) 나는 첫번째 행에 첫번째 열부터 네번째 열까지 병합을 위해서 (0,0,0,3)으로 병합하였다.
//CellRangeAddress mergedRegion = new  CellRangeAddress(0,0,0,3);// 첫 번째 행, 첫 번째 열부터 네 번째 열까지 병합
//
//sheet.addMergedRegion(mergedRegion);
//CellRangeAddress mergedRegion1 = new  CellRangeAddress(0,0,5,8);
//sheet.addMergedRegion(mergedRegion1);

	
		    // 컨텐츠 타입과 파일명 지정
		    response.setContentType("ms-vnd/excel");    
		    //response.setHeader("Content-Disposition", "attachment;filename="+fileName);	
		    response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"ISO8859_1") + ".xlsx"); //cell 255 초과
	
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
	
	@RequestMapping(value = "/gms/statistics/product/detailList.do")
	@ResponseBody
	public List<StatisticsProuctCustomerCountVO> getStatisticsProductBottleList(StatisticsProuctCustomerCountVO param) {

		logger.info("StatisticsProductContoller getStatisticsProductBottleList");
//		logger.info("StatisticsProductContoller param.getSearchDt() "+param.getSearchDt());
//		logger.debug("StatisticsProductContoller sType "+ param.getSType());

		//Integer searchProductId = params.getSearchProductId();				
		String searchStatDt = param.getSearchDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(param.getGubunType() ==1 && searchStatDt != null && searchStatDt.length() > 9) {						
			searchStatDt = searchStatDt.replace("/","-");
			param.setSearchDtFrom(searchStatDt+" 00:00:00");
//			logger.error("****** getStatisticsProductDaily else *****searchStatDtEnd===*"+DateUtils.addTime(searchStatDt, "yyyy-MM-dd", Calendar.DATE, 1));	 
			param.setSearchDtEnd(DateUtils.addTime(searchStatDt, "yyyy-MM-dd", Calendar.DATE, 1)+" 00:00:00");			
		}else {		
			searchStatDt = searchStatDt.replace("/","-");
			searchStatDt = searchStatDt + "-01";
			param.setSearchDtFrom(searchStatDt+" 00:00:00");
//			logger.error("****** getStatisticsProductMonthly *****searchStatDtEnd===*"+DateUtils.addTime(searchStatDt, "yyyy-MM-dd", Calendar.MONTH, 1));	 
			param.setSearchDtEnd(DateUtils.addTime(searchStatDt, "yyyy-MM-dd", Calendar.MONTH, 1)+" 00:00:00");		
		}
		
		List<String> list = new ArrayList<>();
		if(param.getSType() == 0) {
			list.add("0309");
			list.add("0316");
			list.add("0308");
			list.add("0314");
			
		} else if(param.getSType() == 1) {
			list.add("0308");
			list.add("0314");
		}else {
			list.add("0309");
			list.add("0316");
		}
		param.setCodeList(list);
		
		return statService.getWorkBottleListOfProductStat(param);
	}

}

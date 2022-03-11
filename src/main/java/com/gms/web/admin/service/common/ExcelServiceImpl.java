package com.gms.web.admin.service.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerBottleVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.OrderService;
import com.gms.web.admin.service.manage.ProductService;
import com.gms.web.admin.service.manage.UserService;

@Service
public class ExcelServiceImpl implements ExcelService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Override
	@Transactional
	public Map<String,Object> uploadBottleExcelFile(MultipartHttpServletRequest request,
			MultipartFile excelFile) {
		
        List<BottleVO> list = new ArrayList<BottleVO>();
        
        List<BottleVO> bottlelist = bottleService.getBottleListAll();
        
        //List<CustomerSimpleVO> customerList = customerService.searchCustomerSimpleList("");
        
        List<ProductTotalVO> productList = productService.getProductTotalDetailList();
        
        int result = 0;
        int COLUMN_COUNT  = 12;
        int updateCount = 0;
        int insertCount = 0;
        Map<String, Object> map = new HashMap<String, Object>();	
        
        try {
        	
    		FileInputStream excelFIS = (FileInputStream)(excelFile.getInputStream());    		
    		
    		logger.debug("$$$$$$$$$$$$$$ ExcelService XSSFSheet start ");
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            boolean isRegisteFlag = false;
            StringBuffer sb = new StringBuffer();
            
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
            	
            	isRegisteFlag = true;
            	
                BottleVO bottle = new BottleVO();
                XSSFRow row = sheet.getRow(i);
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }                
                
              //용기	바코드/RFID	가스	품명	용기체적	가스용량	충전용량	충전기한	충전압력	제조일	거래처	작업	소유	
                //0		1			2	3	4		5		6		7		8		9		10		11	12

                //N:자사소유                //Y:타사소유                
                
                String colValue="";
               
                String productNm = "";
            	String productCapa = "";
            	
            	if(row.getCell(0).toString() !=null && row.getCell(0).toString().length() > 0) {
	                for(int j=0; j< COLUMN_COUNT; j++) {
	                	XSSFCell cell = row.getCell(j);
	                	
	                	switch (cell.getCellType()) {
		                    case Cell.CELL_TYPE_STRING:
		                        colValue = cell.getRichStringCellValue().getString();
		                        
		                        break;
		                    case Cell.CELL_TYPE_NUMERIC:
		                        if (DateUtil.isCellDateFormatted(cell)) {
		                            //colValue = cell.getDateCellValue().toString();
		                            Date date = cell.getDateCellValue();
		                            colValue = new SimpleDateFormat("yyyy-MM-dd").format(date);
		                            
		                            if(j==6) bottle.setBottleChargeDt(cell.getDateCellValue());
		                            else if(j==8) bottle.setBottleCreateDt(cell.getDateCellValue());
		                        } else {
		                            Long roundVal = Math.round(cell.getNumericCellValue());
		                            Double doubleVal = cell.getNumericCellValue();
		                            if (doubleVal.equals(roundVal.doubleValue())) {
		                                colValue = String.valueOf(roundVal);
		                            } else {
		                                colValue = String.valueOf(doubleVal);
		                            }
		                        }
		                        break;
		                    case Cell.CELL_TYPE_BOOLEAN:
		                        colValue = String.valueOf(cell.getBooleanCellValue());
		                        break;
		                    case Cell.CELL_TYPE_FORMULA:
		                        colValue = cell.getCellFormula();
		                        break;
		         
		                    default:
		                        colValue = "";
	                    }
	                	
	                	//if(i==0 && colValue.equals("end")) break;
	//                	logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ colValue);
	                	
	                	//용기	바코드/RFID	가스	품명	용기체적	가스용량	충전용량	충전기한	충전압력	제조일	거래처	작업	소유	
	                    //0		1			2	3	4		5		6		7		8		9		10		11	12
	
	                	if(j == 0) bottle.setBottleId(colValue);
	                	else if(j == 1) bottle.setBottleBarCd(colValue);
	                	else if(j == 2) bottle.setGasCd(colValue);	                	
	                	else if(j == 3) {
	                		bottle.setBottleCapa(colValue);
	                		productCapa = colValue;
	                		bottle.setChargeCapa(colValue);
	                	}
	                	else if(j == 4) {
	                		if(colValue!=null && colValue.length() > 9) {
		                		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		                		Date date = sdf.parse(colValue);
		                		
		                		bottle.setBottleCreateDt(date);
	                		}
	                	}
	                	else if(j == 5) {
	                		if(colValue!=null && colValue.length() > 9) {
		                		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		                		Date date = sdf.parse(colValue);
		                		
		                		bottle.setBottleChargeDt(date);
	                		}
	                	}
	                	else if(j == 6) bottle.setBottleVolumn(colValue);//  productNm = colValue;
	                	else if(j == 9) productNm = colValue;
	                	else if(j == 10) bottle.setBottleChargePrss(colValue);	                	
	                	else if(j == 11) {
	                		if(colValue.equals("self"))
	                			bottle.setBottleOwnYn("Y");
	                		else
	                			bottle.setBottleOwnYn("N");
	                	}
	                	
	                	bottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.new"));
	                }
            	}
            	
                ProductTotalVO productTotal = new ProductTotalVO();
                productTotal.setProductNm(productNm);
                productTotal.setProductCapa(productCapa);
                //productTotal = productService.getProductTotalDetails(productTotal);
               
                for(int k=0;k<productList.size();k++) {
                	ProductTotalVO productTemp = productList.get(k);
                	if(productTemp.getProductNm().toLowerCase().equals(productNm.toLowerCase()) && productTemp.getProductCapa().toLowerCase().equals(productCapa.toLowerCase())) {
                		productTotal = productTemp;
                	}                	
                }
                
                if(productTotal != null && productTotal.getProductId() != null && productTotal.getProductId() > 0) {
	                
	                bottle.setProductId(productTotal.getProductId());
	                bottle.setProductPriceSeq(productTotal.getProductPriceSeq());
	                bottle.setGasId(productTotal.getGasId());
	                RequestUtils.initUserPrgmInfo(request, bottle);
	                bottle.setBottleWorkId(bottle.getCreateId());
	                
	                bottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
	                bottle.setMemberCompSeq(Integer.valueOf(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
	                //logger.debug("$$$$$$$$$$$$$$ ExcelService bottle.getBottleid "+ bottle.getBottleId());
	                for(int k=0 ; k < bottlelist.size() ; k++) {
	                	
	                	if(bottle.getBottleBarCd().equals(bottlelist.get(k).getBottleBarCd())) {
	                		isRegisteFlag = false;
	                		result = bottleService.modifyBottle(bottle);
	                		updateCount++;
	                	}	                		
	                }
	                if(isRegisteFlag) {
	                	boolean isBeen = false;
	                	for(int k=0; k < list.size() ; k++) {
	                		if(list.get(k).getBottleBarCd().equals(bottle.getBottleBarCd())) isBeen = true;
	                	}
	                	if(!isBeen) {
	                		list.add(bottle);	                	
	                		insertCount++;
	                		
	                	}
	                }
                }else {
                	if(bottle.getBottleBarCd() !=null) {
	                	sb.append(bottle.getBottleBarCd());
	                	sb.append(";");
                	}
                	
                }
            }
            logger.error("$$$$$$$$$$$$$$ ExcelService sb "+ sb.toString());
            if(list.size() > 0)
            	result = bottleService.registerBottles(list);
            
            map.put("insertCount", insertCount);
            map.put("updateCount", updateCount);
            map.put("exception",sb.toString());
            map.put("result", result);
            
            workbook.close();
    		
    
            logger.info("$$$$$$$$$$$$$$ ExcelService result "+ result+"==updateCount ="+updateCount+" insertCount=="+insertCount);
            
        } catch (org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException e) {
        	try {
        		
        		logger.debug("$$$$$$$$$$$$$$ ExcelService HSSFSheet start ");
        		
        		FileInputStream excelFIS = (FileInputStream)(excelFile.getInputStream());
        		
        		//HSSFWorkbook excelWB = new HSSFWorkbook(excelFIS);
        		
        		XSSFWorkbook workbook = new XSSFWorkbook(excelFIS);

                // Get first/desired sheet from the workbook
                XSSFSheet sheet =  workbook.getSheetAt(0);
        		
                // 첫번째 시트 불러오기
                //XSSFSheet sheet = workbook.getSheetAt(0);
                //HSSFSheet sheet = excelWB.getSheetAt(0);
                
                boolean isRegisteFlag = false;
                StringBuffer sb = new StringBuffer();
                
                for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
                	
                	isRegisteFlag = true;
                	
                    BottleVO bottle = new BottleVO();
                    //HSSFRow row = sheet.getRow(i);
                    XSSFRow row = sheet.getRow(i);
                    
                    // 행이 존재하기 않으면 패스
                    if(null == row) {
                        continue;
                    }                
                    //용기번호	바코드번호	가스종류	충전용량	제조월	충전기한	용기체적	사업자등록번호	GMP여부(Y/N)	품명	충전압력	용기소유(자사-self,타사-other)

                  //용기	바코드/RFID	가스	품명	용기체적	가스용량	충전용량	충전기한	충전압력	제조일	거래처	작업	소유	
                    //0		1			2	3	4		5		6		7		8		9		10		11	12
                    //N:자사소유                //Y:타사소유                
                    
                    String colValue="";
                   
                    String productNm = "";
                	String productCapa = "";
                	
                    for(int j=0; j< COLUMN_COUNT; j++) {
                    	//HSSFCell cell = row.getCell(j);
                    	XSSFCell cell = row.getCell(j);

                    	if(cell !=null) {
    	                	switch (cell.getCellType()) {
    		                    case Cell.CELL_TYPE_STRING:
    		                        colValue = cell.getRichStringCellValue().getString();
    		                        //logger.debug("ExcelSerive uploadExcelFile j ==="+ j);
    		                        break;
    		                    case Cell.CELL_TYPE_NUMERIC:
    		                        if (DateUtil.isCellDateFormatted(cell)) {
    		                            colValue = cell.getDateCellValue().toString();
    		                            
    		                            if(j==6) bottle.setBottleChargeDt(cell.getDateCellValue());
    		                            else if(j==8) bottle.setBottleCreateDt(cell.getDateCellValue());
    		                        } else {
    		                            Long roundVal = Math.round(cell.getNumericCellValue());
    		                            Double doubleVal = cell.getNumericCellValue();
    		                            if (doubleVal.equals(roundVal.doubleValue())) {
    		                                colValue = String.valueOf(roundVal);
    		                            } else {
    		                                colValue = String.valueOf(doubleVal);
    		                            }
    		                        }
    		                        break;
    		                    case Cell.CELL_TYPE_BOOLEAN:
    		                        colValue = String.valueOf(cell.getBooleanCellValue());
    		                        break;
    		                    case Cell.CELL_TYPE_FORMULA:
    		                        colValue = cell.getCellFormula();
    		                        break;
    		         
    		                    default:
    		                        colValue = "";
    	                    }
    	                	             	
    	                	//용기	바코드/RFID	가스	품명	용기체적	가스용량	충전용량	충전기한	충전압력	제조일	거래처	작업	소유	
    	                    //0		1			2	3	4		5		6		7		8		9		10		11	12	
    	                    //N:자사소유
    	                    //Y:타사소유                	
    	                	//2020-08-18 수정
    	                	//용기번호	바코드번호	가스종류	충전용량	제조월	충전기한	용기체적	사업자등록번호	GMP여부(Y/N)	품명	충전압력	용기소유(자사-self,타사-other)
    	                	//0		1			2	3		4		5		6		7			8			9		10		11	
    	                	   
    	                	if(j == 0) bottle.setBottleId(colValue);
    	                	else if(j == 1) bottle.setBottleBarCd(colValue);
    	                	else if(j == 2) bottle.setGasCd(colValue);	                	
    	                	else if(j == 3) {
    	                		bottle.setBottleCapa(colValue);
    	                		productCapa = colValue;
    	                		bottle.setChargeCapa(colValue);
    	                	}
    	                	else if(j == 4) {
    	                		if(colValue!=null && colValue.length() > 9) {
    		                		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
    		                		Date date = sdf.parse(colValue);
    		                		
    		                		bottle.setBottleCreateDt(date);
    	                		}
    	                	}
    	                	else if(j == 5) {
    	                		if(colValue!=null && colValue.length() > 9) {
    		                		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
    		                		Date date = sdf.parse(colValue);
    		                		
    		                		bottle.setBottleChargeDt(date);
    	                		}
    	                	}
    	                	else if(j == 6) bottle.setBottleVolumn(colValue);//  productNm = colValue;
    	                	else if(j == 9) productNm = colValue;
    	                	else if(j == 10) bottle.setBottleChargePrss(colValue);	                	
    	                	else if(j == 11) {
    	                		if(colValue.equals("self"))
    	                			bottle.setBottleOwnYn("Y");
    	                		else
    	                			bottle.setBottleOwnYn("N");
    	                	}
    	                	
    	                	bottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.new"));
    	                	
                    	}
                    }
                    
                    ProductTotalVO productTotal = null;
                    
                    for(int k=0;k<productList.size();k++) {
                    	ProductTotalVO productTemp = productList.get(k);
                   
                    	if(productTemp.getProductNm().toLowerCase().equals(productNm.toLowerCase()) && productTemp.getProductCapa().toLowerCase().equals(productCapa.toLowerCase())) {
                    		
                    		logger.debug("ExcelSerive uploadExcelFile Equal");
                    		productTotal = productTemp;
                    	}                	
                    }
                    /*
                    ProductTotalVO productTotal = new ProductTotalVO();
                    productTotal.setProductNm(productNm);
                    productTotal.setProductCapa(productCapa);
                                    
                    productTotal = productService.getProductTotalDetails(productTotal);
                    */            
                    
                    if(productTotal != null && productTotal.getProductId() > 0 ) {
    	                
    	                bottle.setProductId(productTotal.getProductId());
    	                bottle.setProductPriceSeq(productTotal.getProductPriceSeq());
    	                bottle.setGasId(productTotal.getGasId());
    	                RequestUtils.initUserPrgmInfo(request, bottle);
    	                bottle.setBottleWorkId(bottle.getCreateId());
    	                
    	                bottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
    	                bottle.setMemberCompSeq(Integer.valueOf(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
    	              
    	                for(int k=0 ; k < bottlelist.size() ; k++) {
    	                	if(bottle.getBottleBarCd().equals(bottlelist.get(k).getBottleBarCd())) {
    	                		isRegisteFlag = false;
    	                		result = bottleService.modifyBottle(bottle);
    	                		
    	                		updateCount++;
    	                		logger.debug("ExcelSerive uploadExcelFileupdateCount=="+ updateCount);
    	                	}	                		
    	                }
    	                if(isRegisteFlag) {
    	                	boolean isBeen = false;
    	                	for(int k=0; k < list.size() ; k++) {
    	                		if(list.get(k).getBottleBarCd().equals(bottle.getBottleBarCd())) isBeen = true;
    	                	}
    	                	if(!isBeen) {
    	                		list.add(bottle);    	                	
    	                		insertCount++;
    	                		result = bottleService.registerBottle(bottle);
    	                	}
    	                }
                    }else {
                    	sb.append(bottle.getBottleBarCd());
                    	sb.append(";");                	
                    }                
                }
                logger.error("$$$$$$$$$$$$$$ ExcelService sb "+ sb.toString());
                if(list.size() > 0)
                	result = bottleService.registerBottles(list);
                
                map.put("insertCount", insertCount);
                map.put("updateCount", updateCount);
                map.put("exception",sb.toString());
                map.put("result", result);
                
                //excelWB.close();
                logger.info("$$$$$$$$$$$$$$ ExcelService result "+ result+"==updateCount ="+updateCount+" insertCount=="+insertCount);
        	} catch (Exception e1) {
        		e.printStackTrace();
        		map.put("result", -1);
        		return map;
        	}
        } catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
			map.put("result", -1);
    		return map;
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
			map.put("result", -1);
    		return map;
		}
        return map;
	}
	
	
	@Override
	@Transactional
	public int uploadBottleExcelFileGMS(MultipartHttpServletRequest request,
			MultipartFile excelFile) {
		
        List<BottleVO> list = new ArrayList<BottleVO>();
        
        List<BottleVO> bottlelist = bottleService.getBottleListAll();
        
        List<CustomerSimpleVO> customerList = customerService.searchCustomerSimpleList("");
        
        List<ProductTotalVO> productList = productService.getProductTotalDetailList();
        
        int result = 0;
        int updateCount = 0;
        int insertCount = 0;
        
        try {
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            boolean isRegisteFlag = false;
            StringBuffer sb = new StringBuffer();
            
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
            	
            	isRegisteFlag = true;
            	
                BottleVO bottle = new BottleVO();
                XSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }                
                
              //용기번호	바코드번호	가스종류	충전용량	제조월	충전기한	용기체적	사업자등록번호	GMP여부(Y/N)	품명	충전압력	용기소유(자사-self,타사-other)
                //0		1			2	3		4		5		6		7			8			9		10		11	

                String colValue="";
               
                String productNm = "";
            	String productCapa = "";
            	
                for(int j=0; j< 12; j++) {
                	XSSFCell cell = row.getCell(j);               	
                	
                	switch (cell.getCellType()) {
	                    case Cell.CELL_TYPE_STRING:
	                        colValue = cell.getRichStringCellValue().getString();
	                        //logger.debug("ExcelSerive uploadExcelFile j ==="+ j);
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        if (DateUtil.isCellDateFormatted(cell)) {
	                            colValue = cell.getDateCellValue().toString();
	                            
	                            if(j==6) bottle.setBottleChargeDt(cell.getDateCellValue());
	                            else if(j==8) bottle.setBottleCreateDt(cell.getDateCellValue());
	                        } else {
	                            Long roundVal = Math.round(cell.getNumericCellValue());
	                            Double doubleVal = cell.getNumericCellValue();
	                            if (doubleVal.equals(roundVal.doubleValue())) {
	                                colValue = String.valueOf(roundVal);
	                            } else {
	                                colValue = String.valueOf(doubleVal);
	                            }
	                        }
	                        break;
	                    case Cell.CELL_TYPE_BOOLEAN:
	                        colValue = String.valueOf(cell.getBooleanCellValue());
	                        break;
	                    case Cell.CELL_TYPE_FORMULA:
	                        colValue = cell.getCellFormula();
	                        break;
	         
	                    default:
	                        colValue = "";
                    }
                	
                	  //용기번호	바코드번호	가스종류	충전용량	제조월	충전기한	용기체적	사업자등록번호	GMP여부(Y/N)	품명	충전압력	용기소유(자사-self,타사-other)
                	  //0		1			2	3		4		5		6		7			8			9		10		11	
       	
                	if(j == 0) bottle.setBottleId(colValue);
                	else if(j == 1) bottle.setBottleBarCd(colValue);
                	else if(j == 2) bottle.setGasCd(colValue);
                	else if(j == 3) {
                		bottle.setBottleCapa(colValue);
                		productCapa = colValue;
                	}
                	else if(j == 4) {
                		if(colValue!=null && colValue.length() > 9) {
	                		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	                		Date date = sdf.parse(colValue);
	                		
	                		bottle.setBottleCreateDt(date);
                		}
                	}
                	else if(j == 5) {
                		if(colValue!=null && colValue.length() > 9) {
	                		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	                		Date date = sdf.parse(colValue);
	                		
	                		bottle.setBottleChargeDt(date);
                		}
                	}                	
                	else if(j == 6) bottle.setBottleVolumn(colValue);                	
                	else if(j == 9) productNm = colValue;
                	else if(j == 10) bottle.setBottleChargePrss(colValue);                 	
                	else if(j == 11)  { 
                		if(colValue.equals("self")) bottle.setBottleOwnYn("Y");
                		else bottle.setBottleOwnYn("N");
                	}
                }
                
                ProductTotalVO productTotal = null;

                for(int k=0;k<productList.size();k++) {
                	ProductTotalVO productTemp = productList.get(k);
                	if(productTemp.getProductNm().equals(productNm) && productTemp.getProductCapa().equals(productCapa)) {
                		productTotal = productTemp;
                	}                	
                }
                
                if(productTotal != null) {
	                
	                bottle.setProductId(productTotal.getProductId());
	                bottle.setProductPriceSeq(productTotal.getProductPriceSeq());
	                bottle.setGasId(productTotal.getGasId());
	                RequestUtils.initUserPrgmInfo(request, bottle);
	                bottle.setBottleWorkId(bottle.getCreateId());
	                
	                bottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
	                bottle.setMemberCompSeq(Integer.valueOf(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
	              
	                for(int k=0 ; k < bottlelist.size() ; k++) {
	                	if(bottle.getBottleBarCd().equals(bottlelist.get(k).getBottleBarCd())) {
	                		isRegisteFlag = false;
	                		result = bottleService.modifyBottle(bottle);
	                		
	                		updateCount++;
	                		logger.debug("ExcelSerive uploadBottleExcelFileGMS Count=="+ updateCount);
	                	}	                		
	                }
	                if(isRegisteFlag) {
	                	list.add(bottle);
	                	//result = bottleService.registerBottle(bottle);
	                	insertCount++;
	                }
                }else {
                	sb.append(bottle.getBottleBarCd());
                	sb.append(";");
                	
                }
            }
            logger.error("$$$$$$$$$$$$$$ ExcelService uploadBottleExcelFileGMS sb "+ sb.toString());
            if(list.size() > 0)
            	result = bottleService.registerBottles(list);
            
            workbook.close();
            logger.info("$$$$$$$$$$$$$$ ExcelService  uploadBottleExcelFileGMS result "+ result+"==updateCount ="+updateCount+" insertCount=="+insertCount);
           
        } catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
        return result;
	}
	
	
	@Override
	public int uploadCustomerExcelFile(MultipartHttpServletRequest request,
			MultipartFile excelFile) {
		List<CustomerVO> list = new ArrayList<CustomerVO>();
		List<CustomerVO> customerList = customerService.searchCustomerListExcel("");
		
        int result = 0;
        try {
        	
            //OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            FileInputStream excelFIS = (FileInputStream)(excelFile.getInputStream());

            HSSFWorkbook excelWB = new HSSFWorkbook(excelFIS);
           
            //XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            //XSSFSheet sheet = workbook.getSheetAt(0);
            HSSFSheet sheet = excelWB.getSheetAt(0);

           
            int COLUMN_COUNT = 8;
            boolean isRegisteFlag = false;
            
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
            	
            	isRegisteFlag = true;
            	
            	CustomerVO customer = new CustomerVO();
                //XSSFRow row = sheet.getRow(i);
            	HSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }                             
                
                //	거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
                //		0		1		2				3	4		5	6	7	
                             
                String colValue="";               
                
                for(int j=0; j< COLUMN_COUNT; j++) {
                	
                	HSSFCell cell = row.getCell(j);
               	
                	switch (cell.getCellType()) {
	                    case Cell.CELL_TYPE_STRING:
	                        colValue = cell.getRichStringCellValue().getString();
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        if (DateUtil.isCellDateFormatted(cell)) {
	                            colValue = cell.getDateCellValue().toString();	                            
	                        } else {
	                            Long roundVal = Math.round(cell.getNumericCellValue());
	                            Double doubleVal = cell.getNumericCellValue();
	                            if (doubleVal.equals(roundVal.doubleValue())) {
	                                colValue = String.valueOf(roundVal);
	                            } else {
	                                colValue = String.valueOf(doubleVal);
	                            }
	                        }
	                        break;
	                    case Cell.CELL_TYPE_BOOLEAN:
	                        colValue = String.valueOf(cell.getBooleanCellValue());
	                        break;
	                    case Cell.CELL_TYPE_FORMULA:
	                        colValue = cell.getCellFormula();
	                        break;
	         
	                    default:
	                        colValue = "";
                    }
                	
                	//사업자등록번호	거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
                    //			0		1		2				3		4		5	6	7	
                	if(j == 0) {
                		customer.setCustomerNm(colValue);
                		logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ colValue);
                	}
                	else if(j == 1) customer.setCustomerAddr(colValue);
                	else if(j == 2) customer.setBusinessRegId(colValue);
                	else if(j == 3) customer.setCustomerPhone(colValue);
                	else if(j == 4) customer.setCustomerRepNm(colValue);
                	else if(j == 5) customer.setCustomerBusiType(colValue);
                	else if(j == 6) customer.setCustomerItem(colValue);
                	else if(j == 7) customer.setCustomerEmail(colValue);
                	
                }
                RequestUtils.initUserPrgmInfo(request, customer);
                
                customer.setMemberCompSeq(Integer.valueOf(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
              
                for(int k=0;k<customerList.size();k++) {
                	if(customer.getBusinessRegId().equals(customerList.get(k).getBusinessRegId())) {
                		customer.setCustomerId(customerList.get(k).getCustomerId());
                		boolean result1 = customerService.modifyCustomer(customer);
                		isRegisteFlag = false;
                	}                 		
                }
                
                if(isRegisteFlag) list.add(customer);
            }
            if(list.size() > 0)
            	result = customerService.registerCustomers(list);
            else
            	result = 1;
            logger.info("$$$$$$$$$$$$$$ ExcelService result "+ result);
            excelWB.close();
        } catch (org.apache.poi.poifs.filesystem.OfficeXmlFileException e) {
        	try {
	        	OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
	            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
	            
	            // 첫번째 시트 불러오기
	            XSSFSheet sheet = workbook.getSheetAt(0);
	           
	            int COLUMN_COUNT = 8;
	            boolean isRegisteFlag = false;
	            
	            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
	            	
	            	isRegisteFlag = true;
	            	
	            	CustomerVO customer = new CustomerVO();
	                XSSFRow row = sheet.getRow(i);
	            	
	                // 행이 존재하기 않으면 패스
	                if(null == row) {
	                    continue;
	                }                             
	                
	                //	거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
	                //		0		1		2				3	4		5	6	7	                             
	                String colValue="";               
	                
	                for(int j=0; j< COLUMN_COUNT; j++) {
	                	XSSFCell cell = row.getCell(j);                	
	               
	                	switch (cell.getCellType()) {
		                    case Cell.CELL_TYPE_STRING:
		                        colValue = cell.getRichStringCellValue().getString();
		                        break;
		                    case Cell.CELL_TYPE_NUMERIC:
		                        if (DateUtil.isCellDateFormatted(cell)) {
		                            colValue = cell.getDateCellValue().toString();	                            
		                        } else {
		                            Long roundVal = Math.round(cell.getNumericCellValue());
		                            Double doubleVal = cell.getNumericCellValue();
		                            if (doubleVal.equals(roundVal.doubleValue())) {
		                                colValue = String.valueOf(roundVal);
		                            } else {
		                                colValue = String.valueOf(doubleVal);
		                            }
		                        }
		                        break;
		                    case Cell.CELL_TYPE_BOOLEAN:
		                        colValue = String.valueOf(cell.getBooleanCellValue());
		                        break;
		                    case Cell.CELL_TYPE_FORMULA:
		                        colValue = cell.getCellFormula();
		                        break;
		         
		                    default:
		                        colValue = "";
	                    }	                	
	                	//사업자등록번호	거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
	                    //			0		1		2				3		4		5	6	7	
	                	if(j == 0) {
	                		customer.setCustomerNm(colValue);
	                		logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ colValue);
	                	}
	                	else if(j == 1) customer.setCustomerAddr(colValue);
	                	else if(j == 2) customer.setBusinessRegId(colValue);
	                	else if(j == 3) customer.setCustomerPhone(colValue);
	                	else if(j == 4) customer.setCustomerRepNm(colValue);
	                	else if(j == 5) customer.setCustomerBusiType(colValue);
	                	else if(j == 6) customer.setCustomerItem(colValue);
	                	else if(j == 7) customer.setCustomerEmail(colValue);
	                	
	                }
	                RequestUtils.initUserPrgmInfo(request, customer);
	                
	                customer.setMemberCompSeq(Integer.valueOf(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
	              
	                for(int k=0;k<customerList.size();k++) {
	                	if(customer.getCustomerNm().equals(customerList.get(k).getCustomerNm())) {
	                		customer.setCustomerId(customerList.get(k).getCustomerId());
	                		boolean result1 = customerService.modifyCustomer(customer);
	                		isRegisteFlag = false;
	                	}                 		
	                }	                
	                if(isRegisteFlag) list.add(customer);
	            }
	            if(list.size() > 0)
	            	result = customerService.registerCustomers(list);
	            else
	            	result = 1;
	            
	            workbook.close();
        	} catch (Exception e1) {
    			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
    			e.printStackTrace();
    		}
        } catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
        return result;
	}

	

	@Override
	public int uploadCustomerPriceExcelFile(MultipartHttpServletRequest request, 
			MultipartFile excelFile) {
		
			List<CustomerPriceVO> list = new ArrayList<CustomerPriceVO>();
        
        int result = 0;
        try {
        	
        	List<CustomerPriceVO> cPriceList = customerService.getCustomerPriceListAll();
        	boolean isRegisteFlag = false;
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            int COLUMN_COUNT = 283;
            //int COLUMN_COUNT = productService.getProductPriceCount();
            if(COLUMN_COUNT <= 0) COLUMN_COUNT = 283;
            int insertCount = 0;
            StringBuffer sb = new StringBuffer();
            String strProductPrice="";

            List<ProductTotalVO> productList = productService.getProductTotalList();
            
            List<String> strlist = null;
            List<ProductPriceSimpleVO> simpleList = new ArrayList<ProductPriceSimpleVO>();
            Map<String, Object> map = new HashMap<String, Object>();
            
            // 상품정보 가져옴
        	XSSFRow row1 = sheet.getRow(0);
        	  
        	// 상품 가져오기
        	for(int j=3; j< COLUMN_COUNT; j++) {
        		
        		isRegisteFlag = true;
        		XSSFCell cell = row1.getCell(j);
        		
        		switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                    	strProductPrice = cell.getRichStringCellValue().getString();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                        	strProductPrice = cell.getDateCellValue().toString();	                            
                        } else {
                            Long roundVal = Math.round(cell.getNumericCellValue());
                            Double doubleVal = cell.getNumericCellValue();
                            if (doubleVal.equals(roundVal.doubleValue())) {
                            	strProductPrice = String.valueOf(roundVal);
                            } else {
                            	strProductPrice = String.valueOf(doubleVal);
                            }
                        }
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                    	strProductPrice = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                    	strProductPrice = cell.getCellFormula();
                        break;
         
                    default:
                    	strProductPrice = "";
        		} 
        		
        		//strProductPrice = cell.getRichStringCellValue().getString();
        		//logger.debug(" *** ExcelSerive uploadExcelFile strProductPrice=="+j+ "== "+ strProductPrice);  
        		if(strProductPrice.length() >= 1) {
	        		strlist = StringUtils.makeForeach(strProductPrice, "_"); 	
	        		ProductPriceSimpleVO simpleProduct = new ProductPriceSimpleVO();
	        		simpleProduct.setProductNm(strlist.get(0));
	        		if(strlist.size()> 1)
	        			simpleProduct.setProductCapa(strlist.get(1));
	        		else
	        			simpleProduct.setProductCapa(" ");
	        		
					for(int k=0;k < productList.size() ; k++) {
						
	        			if(strlist.size() > 1 && simpleProduct.getProductNm().equals(productList.get(k).getProductNm()) 
	        					&& simpleProduct.getProductCapa().equals(productList.get(k).getProductCapa()) ) {

	        				simpleProduct.setProductId(productList.get(k).getProductId());
	        				simpleProduct.setProductPriceSeq(productList.get(k).getProductPriceSeq());
	        			}else if(strlist.size() == 1 && simpleProduct.getProductNm().equals(productList.get(k).getProductNm()) ) {
	        				
	        				logger.debug(" *** ExcelSerive uploadExcelFile else if *********** strlist.get(0) "+ strlist.get(0));  
	        				simpleProduct.setProductId(productList.get(k).getProductId());
	        				simpleProduct.setProductPriceSeq(productList.get(k).getProductPriceSeq());   				
	        			}
	        				
	        		}
	        		
	        		simpleList.add(simpleProduct);
        		}
        	}
        	
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {            	
            	
            	CustomerVO customer = null;
            	CustomerVO tempcustomer = new CustomerVO();            	
            	
                XSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }                             
              
                String colValue="";       
                float productPrice = 0;
                String customerNm ="";
            	String businessRegId="";            	
            	int productCheck = 0;
            	CustomerPriceVO customerPrice = null;
            	ProductPriceSimpleVO simpleProduct = null;
            	
                for(int j=0; j< COLUMN_COUNT; j++) {
                	
                	if(j%2==1) {
                		customerPrice = new CustomerPriceVO();
                		RequestUtils.initUserPrgmInfo(request, customerPrice);
                	}                	
                			
                	productPrice = 0;
                	XSSFCell cell = row.getCell(j);
                
                	//logger.debug(" *** ExcelSerive uploadExcelFile i=="+i+ "== "+ cell.getNumericCellValue());                	
                	
                	switch (cell.getCellType()) {
	                    case Cell.CELL_TYPE_STRING:
	                        colValue = cell.getRichStringCellValue().getString();
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        if (DateUtil.isCellDateFormatted(cell)) {
	                            colValue = cell.getDateCellValue().toString();	                            
	                        } else {
	                            Long roundVal = Math.round(cell.getNumericCellValue());
	                            Double doubleVal = cell.getNumericCellValue();
	                            productPrice = (float)cell.getNumericCellValue();
	                            if (doubleVal.equals(roundVal.doubleValue())) {
	                                colValue = String.valueOf(roundVal);
	                            } else {
	                                colValue = String.valueOf(doubleVal);
	                            }
	                        }
	                        break;
	                    case Cell.CELL_TYPE_BOOLEAN:
	                        colValue = String.valueOf(cell.getBooleanCellValue());
	                        break;
	                    case Cell.CELL_TYPE_FORMULA:
	                        colValue = cell.getCellFormula();
	                        break;
	         
	                    default:
	                        colValue = "";
                    }
                	if(productPrice > 0)
                		logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ productPrice);
                	
                	if(j == 0) customerNm = colValue;
                	else if(j==1) businessRegId = colValue;
                	
                	if(j==2) {
                		tempcustomer.setCustomerNm(customerNm);
	                	tempcustomer.setBusinessRegId(businessRegId);
	                	
                		customer = customerService.getCustomerDetailsByNmBusi(tempcustomer);
                		if(customer!=null) {
                			RequestUtils.initUserPrgmInfo(request, customer);
                			//logger.debug("ExcelSerive uploadExcelFile customerId ** =="+ customer.getCustomerId());
                		}else {
                			sb.append(customerNm).append(";");
                		}                	
                	}
                	
					if(j>2 && customer !=null) {						
						
						if(j%2==1) {						
							customerPrice = new CustomerPriceVO();
							customerPrice.setProductPrice(productPrice);        
							simpleProduct = simpleList.get(productCheck++);
						}
						else
							customerPrice.setProductBottlePrice(productPrice);  
											
						if(simpleProduct != null) {
							customerPrice.setProductId(simpleProduct.getProductId());
							customerPrice.setProductPriceSeq(simpleProduct.getProductPriceSeq());
						}

						if(j%2==0) {
							
							customerPrice.setCustomerId(customer.getCustomerId());
							
							if(customer != null &&  customer.getCustomerId() > 0 ) {
								isRegisteFlag = true;
								//logger.debug("*** ExcelSerive uploadExcelFile customer ----"+customer.getCustomerNm());
								for(int k=0;k<cPriceList.size();k++) {									
									
				                	if(customerPrice.getCustomerId()- cPriceList.get(k).getCustomerId() ==0 
				                			&& customerPrice.getProductId() >0 
				                			&& cPriceList.get(k).getProductId() > 0
				                			&& customerPrice.getProductId() - cPriceList.get(k).getProductId() ==0
				                			&& customerPrice.getProductPriceSeq() - cPriceList.get(k).getProductPriceSeq() == 0) {

				                		if(customerPrice.getProductPrice() > 0 || customerPrice.getProductBottlePrice() > 0) {
					                		logger.debug("*** ExcelSerive uploadExcelFile modifyCustomerPrice !!!! --------------");
											
					                		customerPrice.setUpdateId(customerPrice.getCreateId());
					                		result = customerService.modifyCustomerPrice(customerPrice);
					                		isRegisteFlag = false;
					                		
					                		//O2(의료용)_35의 경우 O2(의료용)_40 동일하게 처리
					                		if(customerPrice.getProductId()==1	&& customerPrice.getProductPriceSeq()==6) {
					                			
					                			CustomerPriceVO customerPrice40 = new CustomerPriceVO();
					                			
					                			customerPrice40.setProductId(customerPrice.getProductId());
					                			customerPrice40.setCustomerId(customerPrice.getCustomerId());
					                			customerPrice40.setCreateDt(customerPrice.getCreateDt());
					                			customerPrice40.setCreateId(customerPrice.getCreateId());
					                			customerPrice40.setUpdateId(customerPrice.getCreateId());
					                			customerPrice40.setProductPrice(customerPrice.getProductPrice());
					                			customerPrice40.setProductBottlePrice(customerPrice.getProductBottlePrice());
					                			customerPrice40.setProductPriceSeq(7);
					                			
					                			result = customerService.modifyCustomerPrice(customerPrice40);
					                			
					                		}
				                		}
				                	}                 		
				                }
								//logger.debug("*** ExcelSerive uploadExcelFile isRegisteFlag ----"+isRegisteFlag);
								
								if(isRegisteFlag) {
									if(customerPrice.getProductPrice() > 0 || customerPrice.getProductBottlePrice() > 0) {
										list.add(customerPrice);
										insertCount++;
										//logger.debug("*** ExcelSerive uploadExcelFile customerPrice.getProductId() -"+customerPrice.getProductId()+" ProductSeq ="+customerPrice.getProductPriceSeq());
										if(customerPrice.getProductId()==1 && customerPrice.getProductPriceSeq()==6 ) {
											//logger.debug("*** ExcelSerive uploadExcelFile O2의료용 35-40 --------------");
				                			CustomerPriceVO customerPrice40 = new CustomerPriceVO();
				                			
				                			customerPrice40.setProductId(customerPrice.getProductId());
				                			customerPrice40.setCustomerId(customerPrice.getCustomerId());
				                			customerPrice40.setCreateDt(customerPrice.getCreateDt());
				                			customerPrice40.setCreateId(customerPrice.getCreateId());
				                			customerPrice40.setProductPrice(customerPrice.getProductPrice());
				                			customerPrice40.setProductBottlePrice(customerPrice.getProductBottlePrice());
				                			customerPrice40.setProductPriceSeq(7);
				                			
				                			list.add(customerPrice40);
				                			insertCount++;				                			
				                		}
										
										customerPrice = null;
										if(list.size() > 0 && list.size()%500==0) {
											result = customerService.registerCustomerPrices(list);
											list.clear();
										}				
									}
								}
							}else {								
								
								logger.debug("*** ExcelSerive uploadExcelFile customer null= "+customerNm);
							}
						}// if(j%2==0) end 
						
					}                	
                }
            }

            if(list.size() > 0) result = customerService.registerCustomerPrices(list);
            else result = 1;
            logger.info("$$$$$$$$$$$$$$ ExcelService result "+ result+" insertCount="+insertCount+"==not insert =="+sb.toString());
            
            result = orderService.modifyOrderAmountAll();
            
            workbook.close();
	    } catch (DataAccessException e) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
	    	e.printStackTrace();
		} catch (Exception e) {
				// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int uploadCustomerPriceExcelFileLn2(MultipartHttpServletRequest request, 
			MultipartFile excelFile) {
		
		List<CustomerPriceVO> list = new ArrayList<CustomerPriceVO>();
        
        int result = 0;
        try {
        	
        	Integer productId = 60;
        	
        	List<CustomerPriceVO> cPriceList = customerService.getCustomerProductPriceList(productId);
        	boolean isRegisteFlag = false;
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            int COLUMN_COUNT = 26;
            
            int insertCount = 0;
            StringBuffer sb = new StringBuffer();
            String strProductPrice="";
            String strCapa = "";

            List<ProductPriceVO> productList = productService.getProductPriceList(productId);
            
            List<String> strlist = null;
            List<ProductPriceSimpleVO> simpleList = new ArrayList<ProductPriceSimpleVO>();
            Map<String, Object> map = new HashMap<String, Object>();
            
            // 상품정보 가져옴
        	XSSFRow row1 = sheet.getRow(0);
        	  
        	// 상품 가져오기
        	for(int j=3; j< COLUMN_COUNT; j++) {
        		
        		isRegisteFlag = true;
        		XSSFCell cell = row1.getCell(j);
        		
        		switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                    	strCapa = cell.getRichStringCellValue().getString();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                        	strCapa = cell.getDateCellValue().toString();	                            
                        } else {
                            Long roundVal = Math.round(cell.getNumericCellValue());
                            Double doubleVal = cell.getNumericCellValue();
                            if (doubleVal.equals(roundVal.doubleValue())) {
                            	strCapa = String.valueOf(roundVal);
                            } else {
                            	strCapa = String.valueOf(doubleVal);
                            }
                        }
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                    	strCapa = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                    	strCapa = cell.getCellFormula();
                        break;
         
                    default:
                    	strCapa = "";
        		} 
        		
        		//strProductPrice = cell.getRichStringCellValue().getString();
        		logger.debug(" *** ExcelSerive uploadExcelFile strCapa=="+j+ "== "+ strCapa);  
        		ProductPriceSimpleVO simpleProduct = new ProductPriceSimpleVO();
        		simpleProduct.setProductId(productId);
        		simpleProduct.setProductCapa(strCapa);
        		
        		for(int k=0;k < productList.size() ; k++) {
        			if(simpleProduct.getProductCapa().equals(productList.get(k).getProductCapa()) ) {
        				
        				simpleProduct.setProductPriceSeq(productList.get(k).getProductPriceSeq());
        			}
        		}
        		simpleList.add(simpleProduct);
        	}
        	
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {            	
            	
            	CustomerVO customer = null;
            	CustomerVO tempcustomer = new CustomerVO();            	
            	
                XSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }                             
              
                String colValue="";       
                float productPrice = 0;
                String customerNm ="";
            	String businessRegId="";            	
            	int productCheck = 0;
            	CustomerPriceVO customerPrice = null;
            	ProductPriceSimpleVO simpleProduct = null;
            	
                for(int j=0; j< COLUMN_COUNT; j++) {                	
                	
            		customerPrice = new CustomerPriceVO();
            		RequestUtils.initUserPrgmInfo(request, customerPrice);                	              	
                			
                	productPrice = 0;
                	XSSFCell cell = row.getCell(j);
                
                	//logger.debug(" *** ExcelSerive uploadExcelFile i=="+i+ "== "+ cell.getNumericCellValue());                	
                	
                	switch (cell.getCellType()) {
	                    case Cell.CELL_TYPE_STRING:
	                        colValue = cell.getRichStringCellValue().getString();
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        if (DateUtil.isCellDateFormatted(cell)) {
	                            colValue = cell.getDateCellValue().toString();	                            
	                        } else {
	                            Long roundVal = Math.round(cell.getNumericCellValue());
	                            Double doubleVal = cell.getNumericCellValue();
	                            productPrice = (float)cell.getNumericCellValue();
	                            if (doubleVal.equals(roundVal.doubleValue())) {
	                                colValue = String.valueOf(roundVal);
	                            } else {
	                                colValue = String.valueOf(doubleVal);
	                            }
	                        }
	                        break;
	                    case Cell.CELL_TYPE_BOOLEAN:
	                        colValue = String.valueOf(cell.getBooleanCellValue());
	                        break;
	                    case Cell.CELL_TYPE_FORMULA:
	                        colValue = cell.getCellFormula();
	                        break;
	         
	                    default:
	                        colValue = "";
                    }
                	if(productPrice > 0)
                		logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ productPrice);
                	
                	if(j == 0) customerNm = colValue;
                	else if(j==1) businessRegId = colValue;
                	
                	if(j==2) {
                		tempcustomer.setCustomerNm(customerNm);
	                	tempcustomer.setBusinessRegId(businessRegId);
	                	
                		customer = customerService.getCustomerDetailsByNmBusi(tempcustomer);
                		if(customer!=null) {
                			RequestUtils.initUserPrgmInfo(request, customer);
                			//logger.debug("ExcelSerive uploadExcelFile customerId ** =="+ customer.getCustomerId());
                		}else {
                			sb.append(customerNm).append(";");
                		}                	
                	}
                	
					if(j>2 && customer !=null) {		
						customerPrice = new CustomerPriceVO();
						customerPrice.setProductId(productId);
						customerPrice.setProductPrice(productPrice);       
						customerPrice.setProductBottlePrice(productPrice);  
						simpleProduct = simpleList.get(productCheck++);	
											
						if(simpleProduct != null) {
							
							customerPrice.setProductPriceSeq(simpleProduct.getProductPriceSeq());
						}				
						logger.debug("*** ExcelSerive uploadExcelFile customer.getProductPriceSeq ----"+customerPrice.getProductPriceSeq());
						customerPrice.setCustomerId(customer.getCustomerId());
						
						if(customer != null &&  customer.getCustomerId() > 0 ) {
							isRegisteFlag = true;
							logger.debug("*** ExcelSerive uploadExcelFile cPriceList.size() ----"+cPriceList.size());
							for(int k=0;k<cPriceList.size();k++) {									
								logger.debug("*** ExcelSerive uploadExcelFile customer ----"+customer.getCustomerNm());
			                	if(customerPrice.getCustomerId()- cPriceList.get(k).getCustomerId() ==0 
			                			&& customerPrice.getProductPriceSeq() - cPriceList.get(k).getProductPriceSeq() == 0) {

			                		if(customerPrice.getProductPrice() > 0 || customerPrice.getProductBottlePrice() > 0) {
				                		logger.debug("*** ExcelSerive uploadExcelFile modifyCustomerPrice !!!! --------------");
										
				                		customerPrice.setUpdateId(customerPrice.getCreateId());
				                		result = customerService.modifyCustomerPrice(customerPrice);
				                		isRegisteFlag = false;
				                		
				                		//O2(의료용)_35의 경우 O2(의료용)_40 동일하게 처리
				                		if(customerPrice.getProductId()==1	&& customerPrice.getProductPriceSeq()==6) {
				                			
				                			CustomerPriceVO customerPrice40 = new CustomerPriceVO();
				                			
				                			customerPrice40.setProductId(customerPrice.getProductId());
				                			customerPrice40.setCustomerId(customerPrice.getCustomerId());
				                			customerPrice40.setCreateDt(customerPrice.getCreateDt());
				                			customerPrice40.setCreateId(customerPrice.getCreateId());
				                			customerPrice40.setUpdateId(customerPrice.getCreateId());
				                			customerPrice40.setProductPrice(customerPrice.getProductPrice());
				                			customerPrice40.setProductBottlePrice(customerPrice.getProductBottlePrice());
				                			customerPrice40.setProductPriceSeq(7);
				                			
				                			result = customerService.modifyCustomerPrice(customerPrice40);
				                			
				                		}
			                		}
			                	}   
							}
				            
								//logger.debug("*** ExcelSerive uploadExcelFile isRegisteFlag ----"+isRegisteFlag);
								
							if(isRegisteFlag) {
								logger.debug("*** ExcelSerive uploadExcelFile isRegisteFlag ");
								if(customerPrice.getProductPrice() > 0 || customerPrice.getProductBottlePrice() > 0) {
									list.add(customerPrice);
									insertCount++;
									//logger.debug("*** ExcelSerive uploadExcelFile customerPrice.getProductId() -"+customerPrice.getProductId()+" ProductSeq ="+customerPrice.getProductPriceSeq());
																			
									customerPrice = null;
									if(list.size() > 0 && list.size()%500==0) {
										result = customerService.registerCustomerPrices(list);
										list.clear();
									}				
								}
							}
						}else {								
							
							logger.debug("*** ExcelSerive uploadExcelFile customer null= "+customerNm);
						}
					}                	
                }
            }
            logger.debug("*** ExcelSerive uploadExcelFile list.size()= "+list.size());
            if(list.size() > 0) result = customerService.registerCustomerPrices(list);
            else result = 1;
            logger.info("$$$$$$$$$$$$$$ ExcelService result "+ result+" insertCount="+insertCount+"==not insert =="+sb.toString());
            
           // result = orderService.modifyOrderAmountAll();
            
            workbook.close();
	    } catch (DataAccessException e) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
	    	e.printStackTrace();
		} catch (Exception e) {
				// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		return result;
	}

	
	public void readExcelXLSX(String excel) {
		
        try {
            String[] tempFileName = excel.split("/");
            String fileName = tempFileName[excel.split("/").length - 1];
 
           
            File file = new File(excel.trim());
 
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(excel.trim());
            }
 
            XSSFWorkbook wb = new XSSFWorkbook(excel.trim());
            XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);
 
            makeExcelToObject(wb.getSheetAt(0), "xlsx", fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void makeExcelToObject(Object sheet, String flag, String fileName) throws IOException {
        Row titles = null;
 
        for (Row row : (flag.equals("xls") ? (HSSFSheet) sheet : (XSSFSheet) sheet)) {
            if (row.getRowNum() == 0) {
                titles = row;
                continue;
            }
 
            updateData(titles, row);
        }
    }
    
    @Transactional
    private void updateData(Row titles, Row row) throws IOException {
        String colName = null;
        String colValue = null;
        String name = null;
        String value = null;
 
        // FOR Section (S)
        for (Cell cell : row) {
            if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
                break;
            }
 
            colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();
 
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                colValue = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    colValue = cell.getDateCellValue().toString();
                } else {
                    Long roundVal = Math.round(cell.getNumericCellValue());
                    Double doubleVal = cell.getNumericCellValue();
                    if (doubleVal.equals(roundVal.doubleValue())) {
                        colValue = String.valueOf(roundVal);
                    } else {
                        colValue = String.valueOf(doubleVal);
                    }
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                colValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                colValue = cell.getCellFormula();
                break;
 
            default:
                colValue = "";
            }
            
            colValue = colValue.trim();
 
            if (colName.equals("NAME")) {
                //if (!StringUtil.nullToBlank(colValue).isEmpty()) {
                    name = colValue;
                //}
            }
 
            if (colName.equals("VALUE")) {
                //if (!StringUtil.nullToBlank(colValue).isEmpty()) {
                    value = colValue;
                //}
            }
        }
        // FOR Section (E) 
        // TODO : DAO 영역 메소드 호출 후 Data insert 또는 update 로직 구현
        // excel로부터 읽어들인 name, value 값을 DB에 insert하거나 update하는 로직을 구현할 수 있다. 
    }


	@Override
	public int uploadCustomerBottleExcelFile(MultipartHttpServletRequest request, MultipartFile excelFile) {
		
		List<CustomerBottleVO> list = new ArrayList<CustomerBottleVO>();
        
        int result = 0;
        try {
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            int COLUMN_COUNT = 239;
            //int COLUMN_COUNT = productService.getProductPriceCount();
            if(COLUMN_COUNT <= 0) COLUMN_COUNT = 239;
            int insertCount = 0;
            StringBuffer sb = new StringBuffer();
            String strProductPrice="";

            List<ProductPriceSimpleVO> productList = productService.getGasProductPriceList();
            
            List<String> strlist = null;
            List<ProductPriceSimpleVO> simpleList = new ArrayList<ProductPriceSimpleVO>();
            
            UserVO tUser = new UserVO();
    		tUser.setUserPartCd(PropertyFactory.getProperty("common.user.part.sales"));
            List<UserVO> salesList = userService.getUserListPart(tUser) ;
           
            // 상품정보 가져옴
        	XSSFRow row1 = sheet.getRow(0);
        	  
        	// 상품 가져오기
        	for(int j=5; j< COLUMN_COUNT; j++) {
        		
        		XSSFCell cell = row1.getCell(j);
        		
        		switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                    	strProductPrice = cell.getRichStringCellValue().getString();
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                        	strProductPrice = cell.getDateCellValue().toString();	                            
                        } else {
                            Long roundVal = Math.round(cell.getNumericCellValue());
                            Double doubleVal = cell.getNumericCellValue();
                            if (doubleVal.equals(roundVal.doubleValue())) {
                            	strProductPrice = String.valueOf(roundVal);
                            } else {
                            	strProductPrice = String.valueOf(doubleVal);
                            }
                        }
                        break;
                    case Cell.CELL_TYPE_BOOLEAN:
                    	strProductPrice = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case Cell.CELL_TYPE_FORMULA:
                    	strProductPrice = cell.getCellFormula();
                        break;
         
                    default:
                    	strProductPrice = "";
        		} 
        		
        		//strProductPrice = cell.getRichStringCellValue().getString();
        		//logger.debug(" *** ExcelSerive uploadExcelFile strProductPrice=="+j+ "== "+ strProductPrice);  
        		if(strProductPrice.length() >= 1) {
	        		strlist = StringUtils.makeForeach(strProductPrice, "_"); 	
	        		ProductPriceSimpleVO simpleProduct = new ProductPriceSimpleVO();
	        		simpleProduct.setProductNm(strlist.get(0));
	        		if(strlist.size()> 1)
	        			simpleProduct.setProductCapa(strlist.get(1));
	        		else
	        			simpleProduct.setProductCapa(" ");	        		

	        		//logger.debug(" *** ExcelSerive uploadExcelFile simpleProduct.getProductNm() "+ simpleProduct.getProductNm()); 
	        		//logger.debug(" *** ExcelSerive uploadExcelFile simpleProduct.getProductCapa() "+ simpleProduct.getProductCapa());
	        		
					for(int k=0;k < productList.size() ; k++) {
						
	        			if(strlist.size() > 1 && simpleProduct.getProductNm().equals(productList.get(k).getProductNm()) 
	        					&& simpleProduct.getProductCapa().equals(productList.get(k).getProductCapa()) ) {
	        				
	        				simpleProduct.setProductId(productList.get(k).getProductId());
	        				simpleProduct.setProductPriceSeq(productList.get(k).getProductPriceSeq());
	        			}else if(strlist.size() == 1 && simpleProduct.getProductNm().equals(productList.get(k).getProductNm()) ) {
	        				
	        				
	        				simpleProduct.setProductId(productList.get(k).getProductId());
	        				simpleProduct.setProductPriceSeq(productList.get(k).getProductPriceSeq());   				
	        			}
	        				
	        		}
	        		
	        		simpleList.add(simpleProduct);
        		}
        	}

            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {            	
            	
            	CustomerVO customer = null;
            	CustomerVO tempcustomer = new CustomerVO();            	
            	
                XSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }                             
              
                String colValue="";       
                int productPrice = 0;
                String customerNm ="";
                String salesNm = "";
                String workDt ="";
            	String businessRegId="";            	
            	int productCheck = 0;
            	
            	CustomerBottleVO customerBottle = null;
            	ProductPriceSimpleVO simpleProduct = null;
            	UserVO user = null;
            	
                for(int j=0; j< COLUMN_COUNT; j++) {
                			
                	productPrice = 0;
                	XSSFCell cell = row.getCell(j);
                    	
                	
                	switch (cell.getCellType()) {
	                    case Cell.CELL_TYPE_STRING:
	                        colValue = cell.getRichStringCellValue().getString();
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        if (DateUtil.isCellDateFormatted(cell)) {
	                            colValue = cell.getDateCellValue().toString();	                            
	                        } else {
	                            Long roundVal = Math.round(cell.getNumericCellValue());
	                            Double doubleVal = cell.getNumericCellValue();
	                            productPrice = (int)cell.getNumericCellValue();
	                            if (doubleVal.equals(roundVal.doubleValue())) {
	                                colValue = String.valueOf(roundVal);
	                            } else {
	                                colValue = String.valueOf(doubleVal);
	                            }
	                        }
	                        break;
	                    case Cell.CELL_TYPE_BOOLEAN:
	                        colValue = String.valueOf(cell.getBooleanCellValue());
	                        break;
	                    case Cell.CELL_TYPE_FORMULA:
	                        colValue = cell.getCellFormula();
	                        break;
	         
	                    default:
	                        colValue = "";
                    }
                	if(productPrice > 0)
                		logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ productPrice);
                	
                	if(j == 0) customerNm = colValue;
                	else if(j==1) businessRegId = colValue;
                	else if(j==2) {
                		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
                		Date date = sdf.parse(colValue);
                		workDt = DateUtils.convertDateFormat(date, "yyyy-MM-dd");
                	}
                	else if(j==3) salesNm = colValue;
                	//logger.debug("ExcelSerive uploadExcelFile workDt="+ workDt);
                	if(j==4) {
                		
                		
                		tempcustomer.setCustomerNm(customerNm);
	                	tempcustomer.setBusinessRegId(businessRegId);
	                	
                		customer = customerService.getCustomerDetailsByNmBusi(tempcustomer);
                		if(customer!=null) {
                			RequestUtils.initUserPrgmInfo(request, customer);
                		}else {
                			sb.append(customerNm).append(";");
                		}          
                		
                		if(salesNm != null && salesNm.length() > 0)
                		for(int k=0;k<salesList.size();k++) {
                			if(salesNm.equals(salesList.get(k).getUserNm()) )
                					user = salesList.get(k);
                		}
                		
                		if(workDt == null || (workDt!=null && workDt.length() <= 0) ) {
                			workDt = DateUtils.getDate("YYYY-MM-dd");
                		}
                	}
                	
					if(j>4 && customer !=null) {						
						
						if(j%2==1) {						
							customerBottle = new CustomerBottleVO();   
							RequestUtils.initUserPrgmInfo(request, customerBottle);
							
							customerBottle.setRentCount(productPrice);
							if(user !=null) customerBottle.setSalesId(user.getUserId());
							
							customerBottle.setWorkDt(workDt);
							
							simpleProduct = simpleList.get(productCheck++);
						}
						else
							customerBottle.setBackCount(productPrice);							
											
						if(simpleProduct != null) {
							customerBottle.setProductId(simpleProduct.getProductId());
							customerBottle.setProductPriceSeq(simpleProduct.getProductPriceSeq());
						}
						
						if(j%2==0) {							
							customerBottle.setCustomerId(customer.getCustomerId());
							
							if(customer != null &&  customer.getCustomerId() > 0 ) {								
								
								if(customerBottle.getRentCount() > 0 || customerBottle.getBackCount() > 0 ) {
									list.add(customerBottle);
									insertCount++;
								}
								if(list.size() > 0 && list.size()%500==0) {
									result = customerService.registerCustomerBottles(list);
									list.clear();
								}									
							}else {								
								
								logger.debug("*** ExcelSerive uploadExcelFile customer null= "+customerNm);
							}
						}// if(j%2==0) end 						
					}                	
                }
            }
            
           
            if(list.size() > 0) result = customerService.registerCustomerBottles(list);
            else result = 1;
            logger.debug("$$$$$$$$$$$$$$ ExcelService result "+ result+" insertCount="+insertCount+"==not insert =="+sb.toString());
            workbook.close();
	    } catch (DataAccessException e) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
	    	e.printStackTrace();
		} catch (Exception e) {
				// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		return result;
	}

	private static XSSFSheet createSheet(XSSFWorkbook wb, String prefix, boolean isHidden) {
	    XSSFSheet sheet = null;
	    int count = 0;

	    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
	        String sName = wb.getSheetName(i);
	        if (sName.startsWith(prefix))
	            count++;
	    }

	    if (count > 0) {
	        sheet = wb.createSheet(prefix + count);
	    } else
	        sheet = wb.createSheet(prefix);

	    if (isHidden)
	        wb.setSheetHidden(wb.getNumberOfSheets() - 1, XSSFWorkbook.SHEET_STATE_VERY_HIDDEN);

	        return sheet;
	   }

	
}

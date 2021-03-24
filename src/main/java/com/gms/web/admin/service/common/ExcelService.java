package com.gms.web.admin.service.common;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface ExcelService {

	public Map<String,Object> uploadBottleExcelFile(MultipartHttpServletRequest request, MultipartFile excelFile);	
	
	public int uploadBottleExcelFileGMS(MultipartHttpServletRequest request, MultipartFile excelFile);
	
	public int uploadCustomerExcelFile(MultipartHttpServletRequest request, MultipartFile excelFile);
	
	public int uploadCustomerPriceExcelFile(MultipartHttpServletRequest request, MultipartFile excelFile);
	
	public int uploadCustomerPriceExcelFileLn2(MultipartHttpServletRequest request, MultipartFile excelFile);
	
	public int uploadCustomerBottleExcelFile(MultipartHttpServletRequest request, MultipartFile excelFile);
}

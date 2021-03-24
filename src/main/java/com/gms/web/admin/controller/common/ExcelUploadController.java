package com.gms.web.admin.controller.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.service.common.ExcelService;


@Controller
public class ExcelUploadController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ExcelService excelService;
	
	private long SIZE_LIMIT = 1024*1024*3;
	
	private File convertInputStreamToFile(InputStream is) throws IOException {

		File file = File.createTempFile("C:\\home\\data\test", "tmp");

		OutputStream outputStream = new FileOutputStream(file);

		IOUtils.copy(is, outputStream);

		outputStream.close();

		return file;
	}
	
	@RequestMapping(value = "/gms/upload.do")
	public String openUpload() {
					
		return "gms/upload";
	}

	@RequestMapping(value = "/uploadFileJar", method = RequestMethod.POST)	
    public ModelAndView uploadFileJar(MultipartHttpServletRequest request
    		, HttpServletResponse response) {
		MultipartFile file = null;
		Integer result = 0;
		Map<String, Object> map = null;
		
		try {
        
	        Iterator<String> iterator = request.getFileNames();
	        if(iterator.hasNext()) {
	            file = request.getFile(iterator.next());
	        }
	        //long SIZE_LIMIT = 1024*1024*3;
	        long fileSize = file.getSize();
        	
	        InputStream input = file.getInputStream();
	        File fileNew = convertInputStreamToFile(input);

	        logger.debug( "aaa =="+fileNew.getAbsoluteFile());
		} catch (IOException e) {

            return null;
        }
		if(result > 0){
			String alertMessage = "성공";
			RequestUtils.responseWriteException(response, alertMessage,
					"/upload.do");
		
		}
		return null;
		
    }
	@RequestMapping(value = "/uploadExcelFile", method = RequestMethod.POST)	
    public ModelAndView uploadExcelFile(MultipartHttpServletRequest request
    		, HttpServletResponse response) {
		
		MultipartFile file = null;
		Integer result = 0;
		Map<String, Object> map = null;
		try {
        
	        Iterator<String> iterator = request.getFileNames();
	        if(iterator.hasNext()) {
	            file = request.getFile(iterator.next());
	        }
	        //long SIZE_LIMIT = 1024*1024*3;
	        long fileSize = file.getSize();
        	if(fileSize > SIZE_LIMIT) {
        		String alertMessage = "파일은 "+SIZE_LIMIT/(1024*1024)+"M 이하로 업로드 해주세요.";
    			RequestUtils.responseWriteException(response, alertMessage,
    					"/gms/bottle/list.do");
    			return null;
        	}
        	logger.debug("$$$$$$$$$$$$$$ ExcelService fileSize "+ fileSize);
        	
        	map = excelService.uploadBottleExcelFile(request, file);	        
	      
	        //model.addAttribute("gaslist", gaslist);
	        result = (Integer) map.get("result");
	        logger.debug("ExcelUploadContoller result "+ result);
		} catch (Exception e) {
            e.printStackTrace();
            String alertMessage = "엑셀 등록하는데 오류가 발생하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/bottle/list.do");
            return null;
        }
		if(result > 0){
			String alertMessage = "엑셀 등록하였습니다.\\n 등록건수="+map.get("insertCount")+" 수정건수 ="+map.get("updateCount") +"\\n 미처리 용기번호 ="+map.get("exception");
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/bottle/list.do");
		}else {
			 String alertMessage = "엑셀 등록하는데 오류가 발생하였습니다.";
				RequestUtils.responseWriteException(response, alertMessage,
						"/gms/bottle/list.do");
		}
		return null;
		
    }
	
	@RequestMapping(value = "/uploadExcelFileGMS", method = RequestMethod.POST)	
    public ModelAndView uploadExcelFileGMS(MultipartHttpServletRequest request
    		, HttpServletResponse response) {
		
		MultipartFile file = null;
		int result = 0;
		try {
        
	        Iterator<String> iterator = request.getFileNames();
	        if(iterator.hasNext()) {
	            file = request.getFile(iterator.next());
	        }
	        //long SIZE_LIMIT = 1024*1024*3;
	        long fileSize = file.getSize();
        	if(fileSize > SIZE_LIMIT) {
        		String alertMessage = "파일은 "+SIZE_LIMIT/(1024*1024)+"M 이하로 업로드 해주세요.";
    			RequestUtils.responseWriteException(response, alertMessage,
    					"/gms/bottle/list.do");
    			return null;
        	}
        	logger.debug("$$$$$$$$$$$$$$ ExcelService fileSize "+ fileSize);
        	
	        result = excelService.uploadBottleExcelFileGMS(request, file);	        
	      
	        //model.addAttribute("gaslist", gaslist);
	        
	        logger.debug("ExcelUploadContoller result "+ result);
		} catch (Exception e) {
            e.printStackTrace();
            String alertMessage = "엑셀 등록하는데 오류가 발생하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/bottle/list.do");
            return null;
        }
		if(result > 0){
			String alertMessage = "엑셀 등록하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/bottle/list.do");
		}
		return null;
		
    }
	
	
	@RequestMapping(value = "/gms/customer/uploadExcelFile", method = RequestMethod.POST)	
    public ModelAndView uploadExcelFileCustomer(MultipartHttpServletRequest request
    		, HttpServletResponse response
    		, Model model) {
		
		MultipartFile file = null;
		int result = 0;
		try {
        
	        Iterator<String> iterator = request.getFileNames();
	        if(iterator.hasNext()) {
	            file = request.getFile(iterator.next());
	        }
	        //long SIZE_LIMIT = 1024*1024;
	        long fileSize = file.getSize();
        	if(fileSize > SIZE_LIMIT) {
        		String alertMessage = "파일은 1M 이하로 업로드 해주세요.";
    			RequestUtils.responseWriteException(response, alertMessage,
    					"/gms/customer/list.do");
    			return null;
        	}
        	logger.debug("ExcelUploadContoller file "+ file.getOriginalFilename());
	        result = excelService.uploadCustomerExcelFile(request,file);	        
	      
	        //model.addAttribute("gaslist", gaslist);
	        
	        logger.debug("ExcelUploadContoller result "+ result);
		} catch (Exception e) {
            e.printStackTrace();
            
            return null;
        }
		
		if(result > 0){
			String alertMessage = "엑셀 등록하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/customer/list.do");
		}
		return null;
		
    }

	
	@RequestMapping(value = "/gms/price/uploadExcelFile", method = RequestMethod.POST)	
    public ModelAndView uploadExcelFileCustomerPrice(MultipartHttpServletRequest request
    		, HttpServletResponse response) {
		
		MultipartFile file = null;
		int result = 0;
		try {
        
	        Iterator<String> iterator = request.getFileNames();
	        if(iterator.hasNext()) {
	            file = request.getFile(iterator.next());
	        }
	        //long SIZE_LIMIT = 1024*1024;
	        long fileSize = file.getSize();
        	if(fileSize > SIZE_LIMIT) {
        		String alertMessage = "파일은 1M 이하로 업로드 해주세요.";
    			RequestUtils.responseWriteException(response, alertMessage,
    					"/gms/price/write.do");
    			return null;
        	}
        	
	        result = excelService.uploadCustomerPriceExcelFile(request,file);	        
	      
	        //model.addAttribute("gaslist", gaslist);
	        
	        logger.debug("ExcelUploadContoller result "+ result);
		} catch (Exception e) {
            e.printStackTrace();
            
            return null;
        }
		
		if(result > 0){
			String alertMessage = "엑셀 등록하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/price/write.do");
		}
		return null;
		
    }
	
	@RequestMapping(value = "/gms/price/uploadExcelFileLn2", method = RequestMethod.POST)	
    public ModelAndView uploadExcelFileCustomerPriceLn2(MultipartHttpServletRequest request
    		, HttpServletResponse response) {
		
		MultipartFile file = null;
		int result = 0;
		try {
        
	        Iterator<String> iterator = request.getFileNames();
	        if(iterator.hasNext()) {
	            file = request.getFile(iterator.next());
	        }
	        //long SIZE_LIMIT = 1024*1024;
	        long fileSize = file.getSize();
        	if(fileSize > SIZE_LIMIT) {
        		String alertMessage = "파일은 1M 이하로 업로드 해주세요.";
    			RequestUtils.responseWriteException(response, alertMessage,
    					"/gms/price/write.do");
    			return null;
        	}
        	
	        result = excelService.uploadCustomerPriceExcelFileLn2(request,file);	        
	      
	        //model.addAttribute("gaslist", gaslist);
	        
	        logger.debug("ExcelUploadContoller result "+ result);
		} catch (Exception e) {
            e.printStackTrace();
            
            return null;
        }
		
		if(result > 0){
			String alertMessage = "엑셀 등록하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/price/write.do");
		}
		return null;
		
    }
	
	
	@RequestMapping(value = "/gms/cbottle/uploadExcelFile", method = RequestMethod.POST)	
    public ModelAndView uploadExcelFileCustomerBottle(MultipartHttpServletRequest request
    		, HttpServletResponse response) {
		
		MultipartFile file = null;
		int result = 0;
		try {
        
	        Iterator<String> iterator = request.getFileNames();
	        if(iterator.hasNext()) {
	            file = request.getFile(iterator.next());
	        }
	        //long SIZE_LIMIT = 1024*1024;
	        long fileSize = file.getSize();
        	if(fileSize > SIZE_LIMIT) {
        		String alertMessage = "파일은 1M 이하로 업로드 해주세요.";
    			RequestUtils.responseWriteException(response, alertMessage,
    					"/gms/cbottle/write.do");
    			return null;
        	}
        	
	        result = excelService.uploadCustomerBottleExcelFile(request,file);	        
	      
	        //model.addAttribute("gaslist", gaslist);
	        
	        logger.debug("ExcelUploadContoller result "+ result);
		} catch (Exception e) {
            e.printStackTrace();
            
            return null;
        }
		
		if(result > 0){
			String alertMessage = "엑셀 등록하였습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/cbottle/write.do");
		}
		return null;
		
    }
}

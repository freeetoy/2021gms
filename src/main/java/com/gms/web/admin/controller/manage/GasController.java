package com.gms.web.admin.controller.manage;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.service.manage.GasService;
import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.GasVO;

@Controller
public class GasController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * BoardService 빈(Bean) 선언
	 */
	@Autowired
	private GasService gasService;
	
	@RequestMapping(value = "/gms/gas/list.do")
	public String openGasList(Model model) {

		List<GasVO> gasList = gasService.getGasList();
		model.addAttribute("gasList", gasList);		
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.gas"));	    
		return "gms/gas/list";
	}
	
	@RequestMapping(value = "/gms/gas/register.do", method = RequestMethod.POST)
	public ModelAndView registerGas(
			HttpServletRequest request
			, HttpServletResponse response
			, GasVO params) {
		
		logger.debug("GasContoller registerGas");
		
		RequestUtils.initUserPrgmInfo(request, params);
		ModelAndView mav = new ModelAndView();		
		
		boolean result =  false;
		try {
			//임시
			params.setMemberCompSeq(1);
			
			mav.addObject("menuId", PropertyFactory.getProperty("common.menu.gas"));	
			
			GasVO gas = gasService.getGasDetailsByNm(params.getGasNm());
			if(gas !=null) {
				
				String alertMessage = "가스명이 존재합니다.";
				RequestUtils.responseWriteException(response, alertMessage,
						"/gms/gas/list.do");				
				
			}else {			
				gas = gasService.getGasDetailsByCd(params.getGasCd());
				
				if(gas !=null) {
					
					String alertMessage = "가스코드가 존재합니다.";
					RequestUtils.responseWriteException(response, alertMessage,
							"/gms/gas/list.do");				
					
				}else {	
					result = gasService.registerGas(params);					
				}
			}
			
			if (result) {
				String alertMessage = "가스를 등록하였습니다.";
				RequestUtils.responseWriteException(response, alertMessage,
						"/gms/gas/list.do");	
			}
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		//return "redirect:/gms/gas/list.do";
		return null;
	}
	
	
	@RequestMapping(value = "/gms/gas/modify.do", method = RequestMethod.POST)
	public String modifyGas(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, GasVO params) {
		
		logger.info("GasContoller modifyGas");
		RequestUtils.initUserPrgmInfo(request, params);
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.gas"));	
		
		try {
			//임시
			params.setMemberCompSeq(1);
			
			GasVO gas = gasService.getGasDetailsByNm(params.getGasNm());
			if(gas !=null)
			
			logger.debug("******params.getGasId()()) *****===*"+params.getGasId());
			boolean result = gasService.registerGas(params);
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			}
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
	
		return "redirect:/gms/gas/list.do";
	}
	
	@RequestMapping(value = "/gms/gas/delete.do")
	public String deleteGas(@RequestParam(value = "gasId", required = false) Integer gasId, Model model) {

		logger.debug("******deleteGas params.getGasId()()) *****===*"+gasId);
		try { 
			//임시
			model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.gas"));	
			boolean result = gasService.deleteGas(gasId);
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			}
			
			List<GasVO> gasList = gasService.getGasList();
			model.addAttribute("gasList", gasList);
			
		} catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
		
		
		return "redirect:/gms/gas/list.do";
	}
	
	
	@RequestMapping(value = "/gms/gas/detail.do")
	@ResponseBody
	public GasVO getGasDetails(@RequestParam(value = "gasId", required = false) Integer gasId, Model model)	{
		
		GasVO result = gasService.getGasDetails(gasId);
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.gas"));	
		
		if(result != null) logger.debug("******result *****===*"+result.getGasId());
		else logger.debug("******result is null  *****===*"); 
		
		return result;
	}
}

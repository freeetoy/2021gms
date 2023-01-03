package com.gms.web.admin.controller.manage;

import java.util.List;
import java.util.Map;

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

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.service.manage.UserService;


@Controller
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private UserService userService;
	
	
	@RequestMapping(value = "/gms/user/list.do")
	public String openUserList(UserVO params, Model model) {

		//List<UserVO> userList = userService.getUserList(currentPage);
		
		//model.addAttribute("map", userService.getUserList(currentPage));
		Map<String, Object> map = userService.getUserList(params);
		model.addAttribute("userList", map.get("list"));
		model.addAttribute("searchUserNm", params.getSearchUserNm());
		model.addAttribute("currentPage", map.get("currentPage"));
		model.addAttribute("lastPage", map.get("lastPage"));
		model.addAttribute("startPageNum", map.get("startPageNum"));
		model.addAttribute("lastPageNum", map.get("lastPageNum"));
		model.addAttribute("totalCount", map.get("totalCount"));
		model.addAttribute("rowPerPage", params.getRowPerPage());
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.user"));	    
		return "gms/user/list";
	}
	
	@RequestMapping(value = "/gms/user/write.do")
	public String openUserWrite(@RequestParam(value = "userId", required = false) String userId, Model model) {

		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.user"));	
		return "gms/user/write";
	}
	
	@RequestMapping(value = "/gms/user/register.do", method = RequestMethod.POST)
	public String registerUser(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, UserVO params) {
//		logger.info("registerUser");
		
		RequestUtils.initUserPrgmInfo(request, params);
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.user"));
		
		try {
			//임시
			params.setMemberCompSeq(1);
			
			//ID 중복체크			
			boolean result = userService.registerUser(params);
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			}
		} catch (DataAccessException e) {
			logger.error("UserController registerUser Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("UserController registerUser Exception==="+e.toString());
			e.printStackTrace();
		}
	
		return "redirect:/gms/user/list.do";
	}
	
	@RequestMapping(value = "/gms/user/update.do")
	public String openUserUpdate(@RequestParam(value = "userId", required = false) String userId, Model model) {
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.user"));
		
		if (userId == null) {
			return "redirect:/gms/user/list.do";
		} else {
			
			UserVO user = userService.getUserDetails(userId);
			
			if (user == null) {
				return "redirect:/gms/user/list.do";
			}
			
			model.addAttribute("user", user);
		}
		
		return "gms/user/update";
	}
	
	@RequestMapping(value = "/gms/user/modify.do", method = RequestMethod.POST)
	public String modifyUser(HttpServletRequest request
			, HttpServletResponse response
			, Model model
			, UserVO params) {
//		logger.info(" modifyUser");
		
		RequestUtils.initUserPrgmInfo(request, params);
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.user"));
		
		try {
			//임시			
			logger.debug("******params.getUserId()()) *****===*"+params.getUserId());
			boolean result = userService.modifyUser(params);
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			}
		} catch (DataAccessException e) {
			logger.error(" modifyUser Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" modifyUser Exception==="+e.toString());
			e.printStackTrace();
		}
	
		return "redirect:/gms/user/list.do";
	}
	
	@RequestMapping(value = "/gms/user/delete.do")
	public String deleteUser(UserVO params, Model model) {

//		logger.info("******deleteUser params.getUserId()()) *****===*"+params.getUserId());		
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.user"));
		
		try { 
			
			boolean result = userService.deleteUser(params.getUserId());
			if (result == false) {
				// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			}
			
			Map<String, Object> map = userService.getUserList(params);
			
			model.addAttribute("userList", map.get("list"));
			model.addAttribute("currentPage", map.get("currentPage"));
			model.addAttribute("lastPage", map.get("lastPage"));
			model.addAttribute("startPageNum", map.get("startPageNum"));
			model.addAttribute("lastPageNum", map.get("lastPageNum"));
			model.addAttribute("totalCount", map.get("totalCount"));
			
		} catch (DataAccessException e) {
			logger.error(" deleteUser Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error(" deleteUser Exception==="+e.toString());
			e.printStackTrace();
		}
		
		return "redirect:/gms/user/list.do?currentPage="+params.getCurrentPage();
	}
		
		
	@RequestMapping(value = "/gms/user/detail.do")
	@ResponseBody
	public UserVO getUserDetails(@RequestParam(value = "userId", required = false) String userId, Model model)	{
		
		UserVO result = userService.getUserDetails(userId);
		
		model.addAttribute("menuId", PropertyFactory.getProperty("common.menu.user"));
		
		if(result != null) logger.debug("******result *****===*"+result.getUserId());
		else logger.debug("******result is null  *****===*"); 
		
		return result;
	}
	
	@RequestMapping(value = "/gms/user/salesList.do")
	@ResponseBody
	public List<UserVO> getSalesList()	{
		
		UserVO user = new UserVO();
		
		return userService.getUserListPart(user); 
	}
	
	
	@RequestMapping(value = "/gms/user/userIdCheck.do")
	@ResponseBody
	public Object userIdCheck(UserVO param){
		Map<String, Object> result = userService.checkUserIdDuplicate(param);
		
		return result;
	}
}

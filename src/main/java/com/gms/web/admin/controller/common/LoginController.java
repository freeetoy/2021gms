package com.gms.web.admin.controller.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.web.utils.SessionUtil;
import com.gms.web.admin.domain.common.LoginUserVO;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.service.common.LoginService;
import com.gms.web.admin.service.manage.UserService;


@Controller
public class LoginController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String RETURN_LOGINPAGE = "/login";
	
	private static final String AFTER_LOGIN_PAGE = "start";
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private UserService userService;
	
		
	@RequestMapping(value="/login")
	public String loginByGet(Model model,HttpServletRequest req){
		model.addAttribute("message",req.getServletContext());
		
		HttpSession session = req.getSession();
		String Referer = req.getHeader("referer");
		model.addAttribute("returnUrl", Referer);
		//logger.info("LoginContoller Referer-"+Referer);		
		LoginUserVO sessionInfo = SessionUtil.getSessionInfo(req);
		if(sessionInfo!=null) return "redirect:/gms/start";
		//req.getSession().invalidate();
		else return "gms/login";
	}
	
	@RequestMapping(value="/loginAction.do")
	public String loginAction(Model model,
			HttpServletRequest request
			, HttpServletResponse response
			, LoginUserVO param) {
		
		UserVO user = userService.getUserDetails(param.getUserId());
		String alertMessage = "로그인 되었습니다.";
		String returnUrl = request.getParameter("returnUrl");
		
		if(user != null) {		
			LoginUserVO loginUser = loginService.getUserInfo(param);
			
			HttpSession session = request.getSession();			
			
			if(loginUser!= null && loginUser.getUserId() != null) {

				loginUser.setUserId(param.getUserId());
				session.setAttribute(LoginUserVO.ATTRIBUTE_NAME, loginUser);		
				
				session.setAttribute("userId", loginUser.getUserId());		
				
				//Cookie 설정(3시간)
				Cookie cookie = new Cookie(LoginUserVO.ATTRIBUTE_NAME,loginUser.getUserId());
				cookie.setMaxAge(60*60*3);
				
				response.addCookie(cookie);
				logger.info("LoginContollern loginUser.getUserId() ",loginUser.getUserId());	
				//RequestUtils.responseWriteException(response, alertMessage, "/gms/start");
			}else {
				//alertMessage = "비밀번호를 확인해주세요";	
				//RequestUtils.responseWriteException(response, alertMessage, "/login");
				return "redirect:/login";
			}
		}else {
			//alertMessage = "사용자 정보가 없습니다. 아이디를 확인해주세요";	
			//RequestUtils.responseWriteException(response, alertMessage, "/login");
			return "redirect:/login"; //gms/order/list.do";
		}
		
		//return null;
		if(returnUrl!=null && returnUrl.length() > 1) return "redirect:"+returnUrl;
		else return "redirect:/gms/start"; //gms/order/list.do";
		
	}
	
	@RequestMapping(value="/api/loginAction.do")
	@ResponseBody
	public LoginUserVO apiLoginAction(
			HttpServletRequest request
			, HttpServletResponse response
			, String id, String pw) throws UnsupportedEncodingException {
		
		String result = "";
		boolean res=false;
		
		LoginUserVO param = new LoginUserVO();
		param.setUserId(id);
		param.setUserPasswd(pw);
			
		LoginUserVO user = loginService.getUserInfo(param);			

		if(user != null && user.getUserNm() !=null)
			user.setUserNm(URLEncoder.encode(user.getUserNm(), "UTF-8"));
		
		if(user != null && user.getUserId() != null){
			result = "success";
			res = true;
			user.setSuccess(res);
			
		}else {
			result = "fail";
			res = false;
			user.setSuccess(res);
		}
		return user;
		
	}
	
	@RequestMapping(value="/api/loginEncodeAction.do")
	@ResponseBody
	public LoginUserVO apiLoginEncodeAction(
			HttpServletRequest request
			, HttpServletResponse response
			, String id, String pw) throws UnsupportedEncodingException {
		
		String result = "";
		boolean res=false;
		
		logger.info("LoginContoller /api/loginEncodeAction Start");		
		
		LoginUserVO param = new LoginUserVO();	
		
		byte[] idBytes = id.getBytes();
        byte[] pwBytes = pw.getBytes();
        
		// Base64 디코딩 /////////////////////////////////////////////////// 
		Decoder decoder = Base64.getDecoder(); 
		byte[] decodedIdBytes = decoder.decode(idBytes);
		byte[] decodedPwBytes = decoder.decode(pwBytes);

		String newId= new String(decodedIdBytes);
		String newPw= new String(decodedPwBytes);
		//logger.info("LoginContoller new Id ="+newId);		
		param.setUserId(newId);
		param.setUserPasswd(newPw);
			
		LoginUserVO user = loginService.getUserInfo(param);			
		
		if(user != null && user.getUserNm() !=null)
			user.setUserNm(URLEncoder.encode(user.getUserNm(), "UTF-8"));
		
		if(user != null && user.getUserId() != null){
			result = "success";
			res = true;
			user.setSuccess(res);
			
		}else {
			result = "fail";
			res = false;
			user.setSuccess(res);
		}
		return user;
		
	}
	
	@RequestMapping(value="/logout")
	public String logout(Model model,HttpServletRequest req , HttpServletResponse res){
		model.addAttribute("message",req.getServletContext());
		
		req.getSession().invalidate();
		Cookie cookie = new Cookie(LoginUserVO.ATTRIBUTE_NAME,null);
		cookie.setMaxAge(0);
		
		res.addCookie(cookie);
		
		return "gms/login";
	}
	

//	@RequestMapping(value="/command")
//	public String command(Model model,HttpServletRequest req){
//		model.addAttribute("message",req.getServletContext());
//		
//		ShRunner shRunner = new ShRunner();
//		logger.debug("LoginContoller command start ");
//		String cmds = "sh /home/data/restart.sh";
//        String[] callCmd = {"/bin/bash", "-c", cmds};
//        Map map = shRunner.execCommand(callCmd);
//        
//		return "";
//	}
	
}

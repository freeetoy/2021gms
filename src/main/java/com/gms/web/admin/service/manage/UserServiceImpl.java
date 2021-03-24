package com.gms.web.admin.service.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.AES256Util;
import com.gms.web.admin.domain.manage.UserVO;
import com.gms.web.admin.mapper.manage.UserMapper;


@Service
public class UserServiceImpl implements UserService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserMapper userMapper;
	
	@Override
	@Transactional
	public boolean registerUser(UserVO param) {
		boolean successFlag = false;

		// 정보 등록
		int result = 0;
		logger.info("****** registerUser.getUserId()()) *****===*"+param.getUserId());
		try {
			AES256Util aescipher = new AES256Util(PropertyFactory.getProperty("common.crypto.key"));
			
			param.setUserPasswd(aescipher.aesEncode( param.getUserPasswd()) );
	
		}catch(Exception e) {
			logger.error("****** registerUser. Exception===*"+e.getMessage());
		}
		result = userMapper.insertUser(param);
		if (result > 0) {
			successFlag = true;
		}
		
		return successFlag;
	}

	@Override
	@Transactional
	public boolean modifyUser(UserVO param) {
		boolean successFlag = false;

		// 가스정보 등록
		int result = 0;
		logger.info("****** modifyUser()()) *****===*"+param.getUserId());
		
		try {
			AES256Util aescipher = new AES256Util(PropertyFactory.getProperty("common.crypto.key"));
			
			String strPassword = param.getUserPasswd();
			
			param.setUserPasswd(aescipher.aesEncode( strPassword) );
	
		}catch(Exception e) {
			logger.error("****** modifyUser. Exception===*"+e.getMessage());
		}
		
		result = userMapper.updateUser(param);
		if (result > 0) {
			successFlag = true;
		}	
		
		return successFlag;
		
	}
	
	@Override
	public UserVO getUserDetails(String userId) {
		
		UserVO user = userMapper.selectUserDetail(userId);
		
		try {
			//user.setUserPasswd(CryptoUtils.decryptAES256(user.getUserPasswd(),  PropertyFactory.getProperty("common.crypto.key")) );
			//logger.debug("****** getUserDetails. start user.getUserPasswd()===*"+user.getUserPasswd());
			AES256Util aescipher = new AES256Util(PropertyFactory.getProperty("common.crypto.key"));
			//String rsaEn = aescipher.aesEncode( user.getUserPasswd());
			String rsaEn = aescipher.aesDecode( user.getUserPasswd());
			//logger.debug("****** getUserInfo. param.rsaEn ()===*"+rsaEn);
			user.setUserPasswd(rsaEn  );
			//logger.debug("****** getUserDetails. user.getUserPasswd()===*"+user.getUserPasswd());
			
		}catch(Exception e) {
			logger.error("****** getUserDetails. Exception===*"+e.getMessage());
			return null;
		}
		return user;	
	}

	@Override
	@Transactional
	public boolean deleteUser(String userId) {
		UserVO user = userMapper.selectUserDetail(userId);
		
		logger.info("****** deleteUser.userId *****===*"+userId);
		
		if (user == null || "Y".equals(user.getDeleteYn())) {
			return false;
		}

		int result = userMapper.deleteUser(userId);
		if (result < 1) {
			return false;
		}
		
		return true;
	}

	@Override
	public Map<String, Object> checkUserIdDuplicate(UserVO param) {
		// 중복체크
		int count 							= userMapper.selectUserIdCheck(param);
		// 결과 변수
		Map<String, Object> result 			= new HashMap<String, Object>();
		if(count > 0){
			result.put("result", "fail");
			result.put("message", "아이디가 존재 합니다. 확인 후 입력해 주세요.");
			return result;
		}
		
		result.put("result", "success");
		
		return result;
	}

	@Override
	public Map<String , Object> getUserList(UserVO param) {
		
		int currentPage = param.getCurrentPage();
		int ROW_PER_PAGE = param.getRowPerPage();
		
		int starPageNum =1;
		
		int lastPageNum = ROW_PER_PAGE;
		
		if(currentPage > (ROW_PER_PAGE/2)) {
			lastPageNum += (starPageNum-1);
		}
		
		int startRow = (currentPage-1) * ROW_PER_PAGE;
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		
		map.put("startRow", startRow);
		map.put("rowPerPage", ROW_PER_PAGE);	
		map.put("searchUserNm", param.getSearchUserNm());	
		
		if(param.getUserPartCd() != null) {
			map.put("userPartCd", param.getUserPartCd());
			logger.debug("****** getUserList *****userPartCd===*"+param.getUserPartCd());
		}
		
		int userCount = userMapper.selectUserCount(map);
		
		//int lastPage = (int)(Math.ceil(userCount/ROW_PER_PAGE));
		int lastPage = (int)((double)userCount/ROW_PER_PAGE+0.95);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		//수정 Start
		int pages = (userCount == 0) ? 1 : (int) ((userCount - 1) / ROW_PER_PAGE) + 1; // * 정수형이기때문에 소숫점은 표시안됨
		       
        int block;
       
        block = (int) Math.ceil(1.0 * currentPage / ROW_PER_PAGE); // *소숫점 반올림
        starPageNum = (block - 1) * ROW_PER_PAGE + 1;
        lastPageNum = block * ROW_PER_PAGE;
        
        
        if (lastPageNum > pages){
        	lastPageNum = pages;
        }
		//수정 end
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<UserVO> userList = userMapper.selectUserList(map);
		
		logger.info("****** getUserList.userList *****===*"+userList.size());
		
		resutlMap.put("list",  userList);
		resutlMap.put("searchUserNm", param.getSearchUserNm());
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", starPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", userCount);
		
		return resutlMap;
		//return userMapper.selectUserList(currentPage);
	}

	@Override
	public List<UserVO> getUserListPart(UserVO param) {
		// TODO Auto-generated method stub
		param.setUserPartCd(PropertyFactory.getProperty("common.user.part.sales"));
		return userMapper.selectUserListPart(param);
	}

	@Override
	public List<UserVO> getUserListPartNot(UserVO param) {
		// TODO Auto-generated method stub
		param.setUserPartCd(PropertyFactory.getProperty("common.user.part.account"));
		return userMapper.selectUserListPartNot(param);
	}
	@Override
	public UserVO getUserOfName(String userNm) {
		
		return userMapper.selectUserofName(userNm);
		
	}
}

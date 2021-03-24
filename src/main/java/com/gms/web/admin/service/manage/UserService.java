package com.gms.web.admin.service.manage;

import java.util.List;
import java.util.Map;

import com.gms.web.admin.domain.manage.UserVO;


public interface UserService {
	public boolean registerUser(UserVO param);
	
	public boolean modifyUser(UserVO param);

	public UserVO getUserDetails(String userId);

	public boolean deleteUser(String userId);

	//public int getUserCount();
	
	public Map<String, Object> checkUserIdDuplicate(UserVO param);
	
	//public int getUserCount(String searchUserNm);
	  
	//public List<UserVO> getUserList(Criteria cri);
	public Map<String,Object> getUserList(UserVO param);
	//public List<UserVO> getUserList(Criteria cri, String searchUserNm);
	
	
	public List<UserVO> getUserListPart(UserVO param);
	
	public List<UserVO> getUserListPartNot(UserVO param);
	
	public UserVO getUserOfName(String userNm);
}

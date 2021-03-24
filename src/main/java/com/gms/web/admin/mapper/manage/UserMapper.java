package com.gms.web.admin.mapper.manage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.UserVO;

@Mapper
public interface UserMapper {

	public List<UserVO> selectUserList(Map<String, Object> map);	
	
	public List<UserVO> selectUserListOfPart(Map<String, Object> map);	
	
	public List<UserVO> selectUserListPart(UserVO param);	
	
	public List<UserVO> selectUserListPartNot(UserVO param);	
	
	public int insertUser(UserVO param);

	public UserVO selectUserDetail(String userId) ;
	
	public UserVO selectUserofName(String userNm) ;

	public int updateUser(UserVO param);

	public int deleteUser(String userId);

	public int selectUserCount(Map<String, Object> map);	
	
	public int selectUserIdCheck(UserVO param);	
	
	
	//public int selectUserCount(String searchUserNm);
	
	
	//public List<UserVO> selectUserList(Criteria cri);	
	
	//public List<UserVO> selectUserList(Criteria cri,String searchUserNm);
}

package com.gms.web.admin.mapper.manage;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CashSumVO;

@Mapper
public interface CashFlowMapper {
	
	public List<CashFlowVO> selectCashFlowList(Map<String, Object> map);	
	
	public int selectCashFlowCount(Map<String, Object> map);	
	
	public List<CashFlowVO> selectAllCashFlowList(Map<String, Object> map);	
	
	public int selectAllCashFlowCount(Map<String, Object> map);
	
	public CashFlowVO selectCashFlow(CashFlowVO param);	
	
	public CashSumVO selectCashFlowSum(CashFlowVO param);	
	
	public int insertCashFlow(CashFlowVO param);
	
	public int updateCashFlow(CashFlowVO param);
	
	public int deleteCashFlow(CashFlowVO param);

}

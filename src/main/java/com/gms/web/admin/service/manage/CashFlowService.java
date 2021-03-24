package com.gms.web.admin.service.manage;

import java.util.Map;

import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CashSumVO;

public interface CashFlowService {
	
	public int registerCashFlow(CashFlowVO param);
	
	public int modifyCashFlow(CashFlowVO param);
	
	public int deleteCashFlow(CashFlowVO param);
	
	public Map<String , Object> getCashFlowList(CashFlowVO param);
	
	public CashFlowVO getCashFlowDetail(CashFlowVO param);
	
	public CashSumVO getCashFlowSum(CashFlowVO param);

}

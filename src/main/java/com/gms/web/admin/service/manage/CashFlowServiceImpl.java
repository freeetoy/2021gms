package com.gms.web.admin.service.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.CashSumVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.mapper.manage.CashFlowMapper;

@Service
public class CashFlowServiceImpl implements CashFlowService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CashFlowMapper cashMapper;
	
	@Autowired
	private WorkReportService workService;
		
	@Override
	public int registerCashFlow(CashFlowVO param) {
		/*
		OrderVO order = orderService.getTodayOrderForCustomer (param.getCustomerId());
		
		int orderAmount = 0;
		if(order !=null) {
			orderAmount = order.getOrderTotalAmount();			
		}
		int receivableAmount = param.getReceivableAmount()+orderAmount;		
		
		param.setReceivableAmount(receivableAmount);
		*/
		return cashMapper.insertCashFlow(param);
	}

	@Override
	public int modifyCashFlow(CashFlowVO param) {
		
		int result=0;
	
		CashFlowVO cashFlow = cashMapper.selectCashFlow(param);	
		
		cashMapper.updateCashFlow(param);
		cashFlow.setIncomeAmount(param.getIncomeAmount() - cashFlow.getIncomeAmount());
		
		result = modifyWorkReceivedAmount(cashFlow);
		return cashMapper.updateCashFlow(param);
	}

	private int modifyWorkReceivedAmount(CashFlowVO param) {
		
		WorkReportVO workReport = new WorkReportVO();
		workReport.setCustomerId(param.getCustomerId());
		workReport.setCreateDt(param.getCreateDt());
		
		int workReportSeq = workService.getWorkReportSeqForCustomer(workReport);		
		
		workReport.setUpdateId(param.getCreateId());
		workReport.setReceivedAmount(param.getIncomeAmount());
		workReport.setIncomeWay(param.getIncomeWay());
		workReport.setWorkReportSeq(workReportSeq);
		
		return workService.modifyWorkReportReceivedAmount(workReport);
	}
	
	@Override
	public Map<String , Object> getCashFlowList(CashFlowVO param) {
		
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
		map.put("customerId", param.getCustomerId());
		
		String searchCreateDt = param.getSearchCreateDt();	
		
		String searchCreateDtFrom = null;
		String searchCreateDtEnd = null;
		if(searchCreateDt != null && searchCreateDt.length() > 20) {						
			searchCreateDtFrom = searchCreateDt.substring(0, 10) ;			
			searchCreateDtEnd = searchCreateDt.substring(13, searchCreateDt.length()) ;
			
			map.put("searchCreateDt", searchCreateDt);	
			map.put("searchCreateDtFrom", searchCreateDtFrom+" 00");	
			map.put("searchCreateDtEnd", searchCreateDtEnd+ " 23");					
		}
		
		int sCount = 0;
		
		if(param.getCustomerId() != null && param.getCustomerId() > 0)
			sCount = cashMapper.selectCashFlowCount(map);
		else
			sCount = cashMapper.selectAllCashFlowCount(map);
		//int lastPage = (int)(Math.ceil(userCount/ROW_PER_PAGE));
		
		//logger.debug("CashFlowService getCashFlowList sCount =="+sCount);
		
		int lastPage = (int)((double)sCount/ROW_PER_PAGE+0.95);
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		//수정 Start
		int pages = (sCount == 0) ? 1 : (int) ((sCount - 1) / ROW_PER_PAGE) + 1; // * 정수형이기때문에 소숫점은 표시안됨
		       
        int block;        
        block = (int) Math.ceil(1.0 * currentPage / ROW_PER_PAGE); // *소숫점 반올림
        starPageNum = (block - 1) * ROW_PER_PAGE + 1;
        lastPageNum = block * ROW_PER_PAGE;        
        
        if (lastPageNum > pages){
        	lastPageNum = pages;
        }
		//수정 end
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<CashFlowVO> cashList = null;
		if(param.getCustomerId() != null  && param.getCustomerId() > 0)
			cashList = cashMapper.selectCashFlowList(map);	
		else
			cashList = cashMapper.selectAllCashFlowList(map);			
		
		resutlMap.put("list",  cashList);
		resutlMap.put("searchCreateDt",  searchCreateDt);
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", starPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", sCount);
		
		return resutlMap;
	}

	@Override
	public CashFlowVO getCashFlowDetail(CashFlowVO param) {
		
		return cashMapper.selectCashFlow(param);
	}

	@Override
	public int deleteCashFlow(CashFlowVO param) {
		
		int result=0;
		
		CashFlowVO cashFlow = cashMapper.selectCashFlow(param);	
				
		cashFlow.setIncomeAmount(param.getIncomeAmount() - cashFlow.getIncomeAmount());
		
		result = modifyWorkReceivedAmount(cashFlow);
		
		return cashMapper.deleteCashFlow(param);
	}

	@Override
	public CashSumVO getCashFlowSum(CashFlowVO param) {
		
		return cashMapper.selectCashFlowSum(param);
	}

}

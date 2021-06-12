package com.gms.web.admin.service.manage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.StringUtils;
import com.gms.web.admin.domain.manage.BottleHistoryVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.OrderBottleVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.SimpleBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.mapper.manage.BottleMapper;

@Service
public class BottleServiceImpl implements BottleService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private BottleMapper bottleMapper;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private WorkReportService workService;
	
	@Override
	public Map<String,Object> getBottleList(BottleVO param) {
		logger.debug("****** getBottleList *****start===*");		
		
		int currentPage = param.getCurrentPage();
		int ROW_PER_PAGE = param.getRowPerPage();
		
		int startPageNum =1;
		
		int lastPageNum = ROW_PER_PAGE;
		
		if(currentPage > (ROW_PER_PAGE/2)) {
			lastPageNum += (startPageNum-1);
		}
		
		int startRow = (currentPage-1) * ROW_PER_PAGE;
		
		Map<String, Object> map = new HashMap<String, Object>();		
		
		map.put("startRow", startRow);
		map.put("rowPerPage", ROW_PER_PAGE);	
		map.put("searchBottleId", param.getSearchBottleId());	
		map.put("searchBottleBarCd", param.getSearchBottleBarCd());	
		
		if(param.getSearchGasId() != null) {
			map.put("searchGasId", param.getSearchGasId());
		}		

		if(param.getSearchProductId() != null  ) {			
			map.put("searchProductId", param.getSearchProductId());
		}
		if(param.getSearchWorkCd() != null  ) {			
			map.put("searchWorkCd", param.getSearchWorkCd());
		}
		
		if(param.getSearchChargeDt() != null) {
			map.put("searchChargeDt", param.getSearchChargeDt());
			//logger.debug("****** getBottleList *****getSearchChargeDt===*"+param.getSearchChargeDt());
		}		
		
		if(param.getSearchChargeDtFrom() != null) {
			map.put("searchChargeDtFrom", param.getSearchChargeDtFrom());
			//logger.debug("****** getBottleList *****getSearchChargeDtFrom===*"+param.getSearchChargeDtFrom());
		}
		
		if(param.getSearchChargeDtEnd() != null) {
			map.put("searchChargeDtEnd", param.getSearchChargeDtEnd());
			//logger.debug("****** getBottleList *****getSearchChargeDtEnd===*"+param.getSearchChargeDtEnd());
		}
		
		if(param.getSearchDt() != null) {
			map.put("searchDt", param.getSearchDt());
			//logger.debug("****** getBottleList *****getSearchChargeDt===*"+param.getSearchChargeDt());
		}		
		
		if(param.getSearchDtFrom() != null) {
			map.put("searchDtFrom", param.getSearchDtFrom());
			//logger.debug("****** getBottleList *****getSearchChargeDtFrom===*"+param.getSearchChargeDtFrom());
		}
		
		if(param.getSearchDtEnd() != null) {
			map.put("searchDtEnd", param.getSearchDtEnd());
			//logger.debug("****** getBottleList *****getSearchChargeDtEnd===*"+param.getSearchChargeDtEnd());
		}
		
		if(param.getSearchSalesYn() != null) {
			map.put("searchSalesYn", param.getSearchSalesYn());
			map.put("bottleWorkCd", param.getBottleWorkCd());
			//logger.debug("****** getBottleList *****getSearchSalesYn===*"+param.getSearchSalesYn());
		}		
		
		String ownCustomerId = "";
		if(param.getOwnCustomerId() !=null) {
			ownCustomerId = param.getOwnCustomerId();
			map.put("ownCustomerId", ownCustomerId);
		}
	
		int bottleCount = 0;
//		if(param.getOwnCustomerId()!=null  && param.getOwnCustomerId().length() > 0 )
//			bottleCount = bottleMapper.selectBottleHistCountOfCustomer(map);
//		else
			bottleCount = bottleMapper.selectBottleCount(map);
		
		//int lastPage = (int)(Math.ceil(bottleCount/ROW_PER_PAGE));
		int lastPage = (int)((double)bottleCount/ROW_PER_PAGE+0.95);		
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		if(lastPageNum ==0) lastPageNum=1;		
		
		//수정 Start
		int pages = (bottleCount == 0) ? 1 : (int) ((bottleCount - 1) / ROW_PER_PAGE) + 1; // * 정수형이기때문에 소숫점은 표시안됨		
        
        int block;        
        block = (int) Math.ceil(1.0 * currentPage / ROW_PER_PAGE); // *소숫점 반올림
        startPageNum = (block - 1) * ROW_PER_PAGE + 1;
        lastPageNum = block * ROW_PER_PAGE;        
        
        if (lastPageNum > pages){
        	lastPageNum = pages;
        }
		//수정 end
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		List<BottleVO> bottleList = null;
//		if(param.getOwnCustomerId()!=null  && param.getOwnCustomerId().length() > 0 )
//			bottleList = bottleMapper.selectBottleHisListOfCustomer(map);
//		else
			bottleList = bottleMapper.selectBottleList(map);
		
		resutlMap.put("list",  bottleList);
		
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", startPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", bottleCount);
		
		return resutlMap;
	}
	
	

	@Override
	public List<BottleVO> getBottleListAll() {
		return bottleMapper.selectBottleListAll();
	}
	
	
	@Override
	public List<BottleVO> getBottleListToExcel(BottleVO param) {
		logger.debug("****** getBottleListToExcel *****start===*");			
		
		Map<String, Object> map = new HashMap<String, Object>();			
		
		if(param.getSearchGasId() != null) {
			map.put("searchGasId", param.getSearchGasId());
			//logger.debug("****** getBottleListToExcel *****searchGasId===*"+param.getSearchGasId());
		}
		
		if(param.getSearchBottleId() != null) {
			map.put("searchBottleId", param.getSearchBottleId());
			//logger.debug("****** getBottleListToExcel *****searchGasId===*"+param.getSearchBottleId());
		}
		
		if(param.getSearchProductId() != null  ) {			
			map.put("searchProductId", param.getSearchProductId());
		}
		
		if(param.getSearchWorkCd() != null  ) {			
			map.put("searchWorkCd", param.getSearchWorkCd());
		}
		
		if(param.getSearchDt() != null) {
			map.put("searchDt", param.getSearchDt());
			
			String searchDt = param.getSearchDt();	
			
			String searchDtFrom = null;
			String searchDtEnd = null;
					
			if(searchDt != null && searchDt.length() > 20) {
				searchDtFrom = searchDt.substring(0, 10) ;			
				searchDtEnd = searchDt.substring(13, searchDt.length()) ;
				
				param.setSearchDtFrom(searchDtFrom);
				param.setSearchDtEnd(searchDtEnd);			
				map.put("searchDtFrom", param.getSearchDtFrom());
				map.put("searchDtEnd", param.getSearchDtEnd());
			}
			
		}		
			
		if(param.getSearchChargeDt() != null) {
			map.put("searchChargeDt", param.getSearchChargeDt());
			logger.debug("****** getBottleListToExcel *****getSearchChargeDt===*"+param.getSearchChargeDt());
			
			if(param.getSearchChargeDt() != null && param.getSearchChargeDt().length() > 20) {
				String searchChargeDtFrom = param.getSearchChargeDt().substring(0, 10) ;				
				String searchChargeDtEnd = param.getSearchChargeDt().substring(13, param.getSearchChargeDt().length()) ;
				
				map.put("searchChargeDtFrom",searchChargeDtFrom);
				map.put("searchChargeDtEnd", searchChargeDtEnd);			
			}
		}else {
			if(param.getMenuType()==2) {		// 용기충전
				// Date 로 구하기
			    SimpleDateFormat fm1 = new SimpleDateFormat("yyyy/MM/dd");
			    String fromDate = fm1.format(new Date());
			    //logger.debug("현재시간 년월일 = " + fromDate);

			    Calendar cal = Calendar.getInstance();;
			    cal.setTime(new Date());
			    cal.add(Calendar.DAY_OF_YEAR, 7); // 하루를 더한다.
			    	    
			    String endDate = fm1.format(cal.getTime());
			    //logger.debug("현재시간 년월일 = " + endDate);
			    
			    String searchChargeDt = fromDate+" - "+endDate;
			    
			    param.setSearchChargeDt(searchChargeDt);
			    map.put("searchChargeDt", param.getSearchChargeDt());
				
				param.setSearchChargeDtFrom(fromDate);
				param.setSearchChargeDtEnd(endDate); 
			}
		}
		
		if(param.getSearchChargeDtFrom() != null) {
			map.put("searchChargeDtFrom", param.getSearchChargeDtFrom());
			//logger.debug("****** getBottleListToExcel *****getSearchChargeDtFrom===*"+param.getSearchChargeDtFrom());
		}
		
		if(param.getSearchChargeDtEnd() != null) {
			map.put("searchChargeDtEnd", param.getSearchChargeDtEnd());
			//logger.debug("****** getBottleListToExcel *****getSearchChargeDtEnd===*"+param.getSearchChargeDtEnd());
		}
		
		if(param.getSearchSalesYn() != null) {
			map.put("searchSalesYn", param.getSearchSalesYn());
			map.put("bottleWorkCd", param.getBottleWorkCd());
			//logger.debug("****** getBottleListToExcel *****getSearchSalesYn===*"+param.getSearchSalesYn());
		}
		
		if(param.getMenuType()==3 || param.getMenuType()==4) {			//용기 판매/대여 메뉴
			
			if(param.getMenuType()==3 )
				map.put("searchWorkCd", PropertyFactory.getProperty("common.bottle.status.sale"));
			else
				map.put("searchWorkCd", PropertyFactory.getProperty("common.bottle.status.rent"));
		}
		
		List<BottleVO> bottleList = null;
		if(param.getOwnCustomerId() !=null && param.getOwnCustomerId().length() > 0 ) {
			map.put("ownCustomerId", Integer.parseInt(param.getOwnCustomerId()) );
			bottleList = bottleMapper.selectBottleHistListToExcelOfCustomer(map);
		}else {
			map.put("startRow", param.getStartRow());
			map.put("rowPerPage",30000);
			bottleList = bottleMapper.selectBottleListToExcel(map);
		}
		
		//List<BottleVO> bottleList = bottleMapper.selectBottleListToExcel(map);
		
		
		return bottleList;
	}
	
	@Override
	public List<BottleVO> getCustomerBottleList(Integer customerId) {
		return bottleMapper.selectCustomerBottleList(customerId);	
	}
	
	@Override
	public List<BottleVO> getCustomerBottleListDate(BottleVO param) {
		return bottleMapper.selectCustomerBottleListDate(param);	
	}
	
	@Override
	public List<SimpleBottleVO> getCustomerSimpleBottleList(Integer customerId) {
		return bottleMapper.selectSimpleCustomerBottleList(customerId);	
	}
	
	@Override
	public BottleVO getBottleDetail(String bottleBarCd) {
		return bottleMapper.selectBottleDetail(bottleBarCd);	
	}
	
	@Override
	public BottleVO getBottleDetailForBarCd(String bottleBarCd) {
		return bottleMapper.selectBottleDetailForBarCd(bottleBarCd);	
	}
	
	@Override
	public int getBottleCount(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBottleIdCheck(BottleVO param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBottleBarCdCheck(BottleVO param) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	@Transactional
	public int registerBottle(BottleVO param) {

		// 정보 등록
		logger.debug("****** registerBottle.getBottleId()()) *****===*"+param.getBottleId());
		int result = 0;

		ProductTotalVO product = productService.getBottleGasCapa(param);
		
		param.setBottleCapa(product.getProductCapa());
		param.setGasId(product.getGasId());		
		
		param.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.new"));
		param.setBottleWorkId(param.getCreateId());
		param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
		//param.setBottleCapa(productPrice1.getProductCapa());
		
		result =  bottleMapper.insertBottle(param);		
		if(result > 0 ) result = bottleMapper.insertBottleHistory(param);
		
		return result;
		
	}

	
	@Override
	public int registerBottles(List<BottleVO> param) {

		int result = 0;
		result =  bottleMapper.insertBottles(param);	
		if(result > 0 ) result = bottleMapper.insertBottleHistorys(param);
		return result;
	}
	
	
	@Override
	public int modifyBottle(BottleVO param) {
				
		// 정보 등록
		logger.debug("****** modifyBottle.getBottleBarCd() *****===*"+param.getBottleBarCd());
		int result = 0;
		
		/*
		ProductTotalVO product = productService.getBottleGasCapa(param);
		
		param.setBottleCapa(product.getProductCapa());
		param.setGasId(product.getGasId());		
		*/
		
		result =  bottleMapper.updateBottle(param);
		
		//if(result > 0 ) result = bottleMapper.insertBottleHistory(param.getBottleId());
		
		return result;
	}
	
	@Override
	@Transactional
	public int changeBottleWorkCd(BottleVO param) {
		// 정보 등록
		logger.debug("****** modifyBottle.getChBottleId()()) *****===*"+param.getChBottleId());
		int result = 0;
		result =  bottleMapper.updateBottleWorkCd(param);			
		
		param.setBottleBarCd(param.getChBottleBarCd());
		
		if(result > 0 ) result = bottleMapper.insertBottleHistory(param);
				
		param = getBottleDetail(param.getChBottleBarCd());
		
		// TB_Work_Report & TB_Work_Bottle 등록
		WorkReportVO workReport = new WorkReportVO();
		
		//Work_Report_Seq 가져오기
		//int workReportSeq = workService.getWorkReportSeq();

		List<BottleVO> bottleList = new ArrayList<BottleVO>();
		
		bottleList.add(param );
		
		//workReport.setWorkReportSeq(workReportSeq);
		workReport.setBottleWorkCd(param.getBottleWorkCd());
		workReport.setBottleType(param.getBottleType());
		workReport.setUserId(param.getCreateId());
		workReport.setCreateId(param.getCreateId());
		workReport.setBottlesIds(param.getBottleIds());		
		workReport.setCustomerId(param.getCustomerId());
		
		result = workService.registerWorkReportByBottle(workReport, bottleList);
		if(result <= 0) return result;
		
		return result;
	}

	@Override
	@Transactional
	public int changeBottlesWorkCd(BottleVO param) {
		
		int result = 0;

		try {		
			List<String> list = null;
			
			if(param.getBottleIds()!=null && param.getBottleIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				list = StringUtils.makeForeach(param.getBottleIds(), ","); 		
				param.setBottList(list);
			}				
			
			if(param.getBottleType() == null) {
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.charge")) 
						|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))
						|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.incar")) 
						|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) )
					param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
				else
					param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
			}
			
			List<BottleVO> bottleList = getBottleDetails(param);
			String tempBottleIds = "";
			param.getBottList().clear();
			for(int i = 0; i< bottleList.size() ; i++) {
				//logger.debug("BottleServiceImpl changeBottlesWorkCdOnly  for in getBottlesId "+ bottleList.get(i).getBottleId());
				tempBottleIds += bottleList.get(i).getBottleId()+",";
				
				bottleList.get(i).setBottleWorkCd(param.getBottleWorkCd());
				bottleList.get(i).setBottleWorkId(param.getBottleWorkId());
				bottleList.get(i).setCustomerId(param.getCustomerId());
				bottleList.get(i).setBottleType(param.getBottleType());
				bottleList.get(i).setUpdateId(param.getCreateId());
				bottleList.get(i).setChBottleBarCd(bottleList.get(i).getBottleBarCd());
				if(param.getCustomerId()!=null) bottleList.get(i).setCarCustomerId(param.getCustomerId().toString());
				
				//20200208 하나씩 업데이트로 변경
				result =  bottleMapper.updateBottleWorkCd(bottleList.get(i));
				
				param.setBottList(list);
			}
			
			param.setBottleIds(tempBottleIds);
			logger.debug("BottleServiceImpl changeBottlesWorkCdOnly after **** getBottlesId "+ param.getBottleIds());
			
			if(bottleList.size() > 0 ) param.setCustomerId(bottleList.get(0).getCustomerId());
			
			// TB_Work_Report & TB_Work_Bottle 등록
			WorkReportVO workReport = new WorkReportVO();
			
			//Work_Report_Seq 가져오기
			//int workReportSeq = workService.getWorkReportSeq();
			
			//workReport.setWorkReportSeq(workReportSeq);
			workReport.setBottleWorkCd(param.getBottleWorkCd());
			workReport.setBottleType(param.getBottleType());
			workReport.setUserId(param.getCreateId());
			workReport.setCreateId(param.getCreateId());
			workReport.setBottlesIds(param.getBottleIds());		
			workReport.setCustomerId(param.getCustomerId());			
			
			result = workService.registerWorkReportByBottle(workReport, bottleList);
			if(result <= 0) return result;
			
			//20200208 하나씩 업데이트로 변경
			//result =  bottleMapper.updateBottlesWorkCd(param);
			
			if(result > 0 ) result = bottleMapper.insertBottleHistorys(bottleList);
		
		} catch (DataAccessException e) {
			logger.error("changeBottlesWorkCd Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("changeBottlesWorkCd Exception==="+e.toString());
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	@Override
	@Transactional
	public int deleteBottle(BottleVO param) {
		logger.debug("****** deleteBottle *****===*"+param.getBottleBarCd());
		
		int result = 0;
		result =   bottleMapper.deleteBottle(param);
		
		if(result > 0 ) result = bottleMapper.insertBottleHistory(param);
		
		return result;
		
	}

	@Override
	@Transactional
	public int deleteBottles(BottleVO param) {
		return bottleMapper.deleteBottles(param);
	}

	
	@Override
	public Map<String, Object> checkBottleIdDuplicate(BottleVO param) {		
		// 중복체크
		int count = bottleMapper.selectBottleBarCdCheck(param);
		// 결과 변수
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(count > 0){
			result.put("result", "fail");
			result.put("message", "용기코드가 존재 합니다. 확인 후 입력해 주세요.");
			return result;
		}
		
		result.put("result", "success");
		
		return result;	
	}

	@Override
	public List<BottleHistoryVO> selectBottleHistoryList(String bottleBarCd){
		
		return bottleMapper.selectBottleHistoryList(bottleBarCd);		
	}
	
	
	private int insertBottleHistory(String bottleBarCd) {
		// 정보 등록
		logger.debug("****** registerBottle.insertBottleHistory()()) *****===*" +bottleBarCd);
		
		BottleVO bottle = new BottleVO();
		bottle.setBottleBarCd(bottleBarCd);
		//bottleMapper.selectBottleDetail(bottleId);
				
		return bottleMapper.insertBottleHistory(bottle);		
	}

	@Override
	public int modifyBottleOrder(BottleVO param) {
		
		int result = 0; 
				
		result = insertBottleHistory(param.getBottleBarCd());
		
		result = bottleMapper.updateBottleOrderId(param);	
						
		return result ;
	}
	
	@Override
	public int modifyBottleOrderV2(BottleVO param) {
		
		int result = 0; 
				
		//result = insertBottleHistory(param.getBottleBarCd());
		
		result = bottleMapper.updateBottleOrderId(param);	
						
		return result ;
	}

	@Override
	public List<BottleVO> getBottleDetails(BottleVO param) {
		
		List<BottleVO> list = (List<BottleVO>) bottleMapper.selectBottleDetails(param);	
		
		return list;
				
	}



	@Override
	@Transactional
	public int changeBottlesWorkCdOnly(BottleVO param) {

		int result = 0;
		
		try {		
			List<String> list = null;
			
			if(param.getBottleIds()!=null && param.getBottleIds().length() > 0) {
				//bottleIds= request.getParameter("bottleIds");
				list = StringUtils.makeForeach(param.getBottleIds(), ","); 		
				param.setBottList(list);
			}				
			
			if(param.getBottleType() == null) {
				if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.charge")) 
						|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.out"))
						|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.incar")) 
						|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.sale")) )
					param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Full"));
				else
					param.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
			}
			
			List<BottleVO> bottleList = getBottleDetails(param);
			
			String tempBottleIds = "";
			param.getBottList().clear();
			
			for(int i = 0; i< bottleList.size() ; i++) {
				logger.debug("BottleServiceImpl changeBottlesWorkCdOnly  for in getBottlesId "+ bottleList.get(i).getBottleId());
				tempBottleIds += bottleList.get(i).getBottleId()+",";
				param.setBottList(list);
			}
			
			param.setBottleIds(tempBottleIds);
			logger.debug("BottleServiceImpl changeBottlesWorkCdOnly after **** getBottlesId "+ param.getBottleIds());
			
			if(bottleList.size() > 0 ) param.setCustomerId(bottleList.get(0).getCustomerId());			
			
			result =  bottleMapper.updateBottlesWorkCd(param);
			
			if(result > 0 ) result = bottleMapper.insertBottleHistorys(bottleList);
		
		} catch (DataAccessException e) {
			logger.error("changeBottlesWorkCdOnly Exception==="+e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("changeBottlesWorkCdOnly Exception==="+e.toString());
			e.printStackTrace();
		}
		
		return result;
	}



	@Override
	public List<BottleVO> getBottleListByBarCds(BottleVO param) {
		List<BottleVO> list = (List<BottleVO>) bottleMapper.selectBottleListByBarCds(param);	
		
		return list;
	}



	@Override	
	public int changeWorkCdsAndHistory(BottleVO param, List<BottleVO> params) {
		int result = 0;
		
		if(param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.vacuum")) 
				|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.hole")) 
				|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.chargeDt")) 
				|| param.getBottleWorkCd().equals(PropertyFactory.getProperty("common.bottle.status.charge")) ) {
		
			for(int i = 0 ; i < params.size() ; i++) {
				//params.get(i).setChBottleId(params.get(i).getBottleId());
				params.get(i).setChBottleBarCd(params.get(i).getBottleBarCd());
				params.get(i).setCarCustomerId(param.getCustomerId().toString());
				params.get(i).setBottleWorkCd(param.getBottleWorkCd());
				result =  bottleMapper.updateBottleWorkCd(params.get(i));
			}
		}else {
			result =  bottleMapper.updateBottlesWorkCd(param);
		}
		
		if(result > 0 ) result = bottleMapper.insertBottleHistorys(params);
		
		return result;
	}



	@Override
	public BottleVO getCustomerBottleRecent(Integer customerId) {
		return bottleMapper.selectCustomerBottleRecent(customerId);
	}



	@Override
	public List<BottleVO> getDummyBottleList() {
		
		return bottleMapper.selectDummyBottleList();
	}



	@Override
	public BottleVO getDummyBottle(BottleVO param) {

		return bottleMapper.selectDummyBottle(param);
	}



	@Override
	public BottleVO getLastBottleHist(String bottleId) {
		
		return bottleMapper.selectLastBottleHist(bottleId);
	}



	@Override
	public int deleteCustomerIdOfBottle(BottleVO param) {
		
		int result = 0;
		
		result = bottleMapper.deleteCustomerIdOfBottleHist(param);
		
		if(result > 0)
			return bottleMapper.deleteCustomerIdOfBottle(param);
		else
			return -1;
	}



	@Override
	public List<BottleVO> getSimpleDummyBottleList() {
		
		return bottleMapper.selectSimpleDummyBottleList();
	}



	@Override
	public int modifyBottleAfterDelete(OrderBottleVO param) {
		
		return bottleMapper.updateBottleAfterDelete(param);
	}



	@Override
	public int registerBottlesHistory(List<BottleVO> param) {
	
		return bottleMapper.insertBottleHistorys(param);
	}
	

}

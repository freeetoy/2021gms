package com.gms.web.admin.service.manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.domain.manage.CustomerBottleVO;
import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.CustomerProductVO;
import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.mapper.manage.CustomerMapper;

import net.sf.ehcache.CacheManager;

@Service
public class CustomerServiceImpl implements CustomerService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	//protected static final Logger fileLogger = LoggerFactory.getLogger("fileLogger");
	
	@Autowired
	private CustomerMapper customerMapper;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	CacheManager cacheManager;

	@Override	
	public Map<String, Object> getCustomerList(CustomerVO param) {
		
		
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
		map.put("searchCustomerNm", param.getSearchCustomerNm());				
		
		int customerCount = customerMapper.selectCustomerCount(map);
		
		int lastPage = (int)((double)customerCount/ROW_PER_PAGE+0.95);
		if(lastPage == 0) lastPage = 1;
		
		if(currentPage >= (lastPage-4)) {
			lastPageNum = lastPage;
		}
		
		int pages = (customerCount == 0) ? 1 : (int) ((customerCount - 1) / ROW_PER_PAGE) + 1; // * 정수형이기때문에 소숫점은 표시안됨
		
      
        int block;
        
        block = (int) Math.ceil(1.0 * currentPage / ROW_PER_PAGE); // *소숫점 반올림
        startPageNum = (block - 1) * ROW_PER_PAGE + 1;
        lastPageNum = block * ROW_PER_PAGE;
                
        if (lastPageNum > pages){
        	lastPageNum = pages;
        }		
			
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<CustomerVO> customerList = customerMapper.selectCustomerList(map);		

		resutlMap.put("list",  customerList);
		resutlMap.put("searchCustomerNm", param.getSearchCustomerNm());
		resutlMap.put("currentPage", currentPage);
		resutlMap.put("lastPage", lastPage);
		resutlMap.put("startPageNum", startPageNum);
		resutlMap.put("lastPageNum", lastPageNum);
		resutlMap.put("totalCount", customerCount);
		
		return resutlMap;
	}

	@Override
	public CustomerVO getCustomerDetails(Integer customerId) {
		return customerMapper.selectCustomerDetail(customerId);	
	}


	@Override
	public CustomerVO getCustomerDetailsByNm(String customerNm) {
		return customerMapper.selectCustomerDetailByNm(customerNm);
	}
	
	
	@Override
	public CustomerVO getCustomerDetailsByNmBusi(CustomerVO param) {
		return customerMapper.selectCustomerDetailByNmBusi(param);
	}

	
	
	@Override
	@Transactional
	@CacheEvict(value = "userCache")
	public boolean registerCustomer(CustomerVO param) {
		boolean successFlag = false;

		// 정보 등록
		int result = 0;
		
		result = customerMapper.insertCustomer(param);
		if (result > 0) {
			successFlag = true;
		}
		cacheManager.getCache("userCache").removeAll();
		return successFlag;
	}
	
	@Override
	@CacheEvict(value = "userCache")
	public int registerCustomers(List<CustomerVO> param) {
		
		// 정보 등록
		int result = 0;		
		result = customerMapper.insertCustomers(param);
		cacheManager.getCache("userCache").removeAll();
		return result;
	}


	@Override
	@Transactional
	@CacheEvict(value = "userCache")
	public boolean modifyCustomer(CustomerVO param) {
		boolean successFlag = false;

		// 거래처 정보 수정
		int result = 0;
		
		result = customerMapper.updateCustomer(param);
		if (result > 0) {
			successFlag = true;
			
			//미지정된 해당 거래처
		}	
		 cacheManager.getCache("userCache").removeAll();
		return successFlag;
	}

	@Override
	@CacheEvict(value = "userCache")
	public boolean modifyCustomerExcel(CustomerVO param) {
		boolean successFlag = false;

		// 거래처 정보 수정
		int result = 0;
		
		result = customerMapper.updateCustomerExcel(param);
		if (result > 0) {
			successFlag = true;
			
			//미지정된 해당 거래처
		}	
		cacheManager.getCache("userCache").removeAll();
		return successFlag;
	}

	
	@Override
	@Transactional
	public boolean modifyCustomerStatus(CustomerVO param) {
		boolean successFlag = false;

		// 정보 등록
		int result = 0;
		
		result = customerMapper.updateCustomerStatus(param);
		if (result > 0) {
			successFlag = true;
		}
		cacheManager.getCache("userCache").removeAll();
		return successFlag;
	}

	@Override
	@Transactional
	public int deleteCustomer(CustomerVO param) {
		
		return customerMapper.deleteCustomer(param);
	}

	@Override
	public Map<String, Object> checkBusinessRegIdDuplicate(CustomerVO param) {
		// 중복체크
		int count = customerMapper.selectBusinessRegId(param);
		// 결과 변수
		Map<String, Object> result = new HashMap<String, Object>();
		
		if(count > 0){
			result.put("result", "fail");
			result.put("message", "아이디가 존재 합니다. 확인 후 입력해 주세요.");
			return result;
		}
		
		result.put("result", "success");
		
		return result;
	}

	@Override
	@Cacheable(value = "userCache")
	public Map<String, Object> searchCustomerList(String param) {
		
		Map<String, Object> resutlMap = new HashMap<String, Object>();
		
		List<CustomerVO> customerList = customerMapper.searchCustomerList(param);
		
		
		resutlMap.put("list",  customerList);
		resutlMap.put("searchCustomerNm", param);
		
		return resutlMap;	
	}

	@Override
	public List<CustomerVO> searchCustomerListExcel(String param) {
		
		List<CustomerVO> customerList = customerMapper.searchCustomerList(param);
		
		return customerList;
	}


	@Override
	public List<CustomerVO> searchCustomerListCar() {
		List<CustomerVO> customerList = customerMapper.selectCustomerListCar();
		
		return customerList;
	}

	
	@Override
	@Transactional
	public boolean registerCustomerPrice(CustomerPriceVO[] param) {
		boolean successFlag = false;
		Integer customerId = 0;
		// 정보 등록
		int result = 0;
		
		boolean deleteResult = false;
		for(int i = 0 ; i < param.length ; i++ ) {
			
			if( i == 0 ) {
				deleteResult = deleteCustomerPrice(param[i].getCustomerId());
				customerId = param[i].getCustomerId();
			}
			
			result = customerMapper.insertCustomerPrice(param[i]);
			
			if (deleteResult != false && result > 0) {
				successFlag = true;
			}
		}
		
		result = orderService.modifyOrderAmount(customerId);
		
		return successFlag;
	}

	@Override
	@Transactional
	public int registerCustomerPrices(List<CustomerPriceVO> param) {
		int result = 0;	
				
		result = customerMapper.insertCustomerPrices(param);
				
		return result;
	}

	
	@Override
	@Transactional
	public boolean deleteCustomerPrice(Integer customerId) {
		boolean successFlag = false;
		// 정보 등록
		int result = 0;
		
		result = customerMapper.deleteCustomerPrice(customerId);
		if (result > 0) {
			successFlag = true;
		}
		
		return successFlag;
	}

	@Override
	public List<CustomerPriceExtVO> getCustomerPriceList(Integer customerId) {
		return customerMapper.selectCustomerPriceList(customerId);
	}

	@Override
	public int deleteCustomerPrices(List<CustomerPriceVO> param) {
		return customerMapper.deleteCustomerPrices(param);
	}
	
	@Override
	public int modifyCustomerBottleCount(CustomerVO param) {
		return customerMapper.updateCustomerBottleCount(param);
	}
	
	@Override
	public int modifyCustomerBottleRentCount(CustomerVO param) {
		return customerMapper.updateCustomerBottleRentCount(param);
	}
	
	@Override
	public List<CustomerPriceVO> getCustomerPriceListAll() {		
		return customerMapper.selectCustomerPriceListAll();
	}
	
	@Override
	public List<CustomerPriceVO> getCustomerProductPriceList(Integer productId) {		
		return customerMapper.selectCustomerProductPriceList(productId);
	}

	@Override
	public int modifyCustomerPrice(CustomerPriceVO param) {
		return customerMapper.updateCustomerPrice(param);
	}

	@Override
	public List<CustomerSimpleVO> searchCustomerSimpleList(String customerNm) {
		return customerMapper.searchCustomerSimpleList(customerNm);
	}

	@Override
	public List<CustomerSimpleVO> getCarSimpleList(String carYn) {
		return customerMapper.selectCarSimpleList(carYn);
	}

	@Override
	public String searchCustomerSimpleListString(String customerNm) {
		return customerMapper.searchCustomerSimpleListString(customerNm);
	}

	@Override
	public List<CustomerSimpleVO> getAgencyCustomerList() {
		return customerMapper.selectAgencyCustomerList();
	}

	@Override
	public int registerCustomerProduct(CustomerProductVO param) {
		return customerMapper.insertCustomerProduct(param);
	}

	@Override
	public int modifyCustomerProductOwnCount(CustomerProductVO param) {
		return customerMapper.updateCustomerProductOwnCount(param);
	}

	@Override
	public int modifyCustomerProductRentCount(CustomerProductVO param) {
		return customerMapper.updateCustomerProductRentCount(param);
	}

	@Override
	public CustomerProductVO getCustomerProduct(CustomerProductVO param) {
		return customerMapper.selectCustomerProduct(param);
	}

	@Override
	public List<CustomerProductVO> getCustomerProductList(Integer customerId) {
		return customerMapper.selectCustomerProductList(customerId);
	}

	@Override
	public int registerCustomerBottle(CustomerBottleVO param) {
		return customerMapper.insertCustomerBottle(param);
	}

	@Override
	public int registerCustomerBottles(List<CustomerBottleVO> param) {
		return customerMapper.insertCustomerBottles(param);
	}

	@Override
	public List<CustomerBottleVO> getCustomerBottleList(Integer customerId) {
		return customerMapper.selectCustomerBottleList(customerId);
	}

	@Override
	public List<CustomerPriceVO> getCustomerPriceListAllNow() {
		
		return customerMapper.selectCustomerPriceListAllNow();
	}

	@Override
	public int registerCustomerProducts(List<CustomerProductVO> params) {
		
		return customerMapper.insertCustomerProducts(params);
	}

	@Override
	public int deleteCustomerProducts(Integer customerId) {
		
		return customerMapper.deleteCustomerProduct(customerId);
	}

	@Override
	public CustomerPriceVO getCustomerLn2Capa(WorkBottleVO param) {
		// TODO Auto-generated method stub
		return customerMapper.selectCustomerLn2Capa(param);
	}	


}

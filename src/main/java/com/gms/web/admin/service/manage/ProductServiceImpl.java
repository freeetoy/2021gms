package com.gms.web.admin.service.manage;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.ProductVO;
import com.gms.web.admin.mapper.manage.ProductMapper;

@Service
public class ProductServiceImpl implements ProductService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ProductMapper productMapper;
/*
	
	@Override
	@Transactional
	public int registerProduct(ProductVO param) {
		boolean successFlag = false;
		int productId = 0;
		
		// 가스정보 등록
		int result = 0;
		logger.info("****** registerProductparam.getProductId()()) *****===*"+param.getProductId());
		if (param.getProductId() == null) {
			
			productId = getProductId();
			
			param.setProductId(Integer.valueOf(productId));
			logger.info("****** registerProductparam.getProductId()()) *****===*"+param.getProductId());
			
			result = productMapper.insertProduct(param);
			if (result > 0) {
				
				successFlag = true;
			}
		} 
		
		return productId;
	}
*/
	@Override
	@Transactional
	public boolean modifyProduct(ProductVO param) {
		boolean successFlag = false;

		// 가스정보 등록
		int result = 0;
		
			result = productMapper.updateProduct(param);
		if (result > 0) {
			successFlag = true;
		}		
		return successFlag;
	}
	
	@Transactional
	public boolean modifyProductPrice(ProductPriceVO param) {
		boolean successFlag = false;

		// 가스정보 등록
		int result = 0;
		
			result = productMapper.updateProductPrice(param);
		if (result > 0) {
			successFlag = true;
		}
		
		return successFlag;
	}
	
	@Override
	public ProductVO getProductDetails(Integer prodcutId) {
		return productMapper.selectProductDetail(prodcutId);	
	}
	

	@Override
	public ProductTotalVO getProductTotalDetails(ProductTotalVO param) {
		return productMapper.selectProductTotalDetail(param);	
	}

	
	@Override
	public ProductPriceVO getProductPriceDetails(ProductPriceVO param) {
		return productMapper.selectProductPriceDetail(param);	
	}

	@Override
	public ProductPriceVO getProductPriceDetailsByCapa(ProductPriceVO param) {
		return productMapper.selectProductPriceDetailByCapa(param);	
	}


	@Override
	@Transactional
	public boolean deleteProduct(Integer productId) {
		ProductVO product = productMapper.selectProductDetail(productId);
				
		if (product == null || "0".equals(product.getProductStatus())) {
			return false;
		}

		int result = productMapper.deleteProduct(productId);
		if (result < 1) {
			return false;
		}
		
		return true;
	}

	@Override
	@Transactional
	public boolean registerProductPrice(ProductPriceVO param) {
		boolean successFlag = false;
		// 가스정보 등록
		int result = 0;
		
		if (param.getProductId() != null) {			
			result = productMapper.insertProductPrice(param);
			if (result > 0) {				
				successFlag = true;
			}
		} 
		
		return successFlag;
	}

	@Override
	public List<ProductPriceVO> getProductPriceList(Integer productId) {
		logger.debug("****** getProductPriceList *****===*");
		return productMapper.selectProductPriceList(productId);
	}

	@Override
	public List<ProductTotalVO> getProductTotalList() {
		logger.debug("****** getProductTotalList *****===*");
		return productMapper.selectProductTotalList();
	}

	@Override
	public List<ProductTotalVO> getCustomerProductTotalList(Integer customerId) {
		return productMapper.selectCustomerProductTotalList(customerId);
	}
	
	@Override
	public ProductTotalVO getBottleGasCapa(BottleVO param) {
	
		return productMapper.selectBottleGasCapa(param);
	}	
	
	@Override
	public int getProductId() {
		int result = productMapper.selectProductCount()+1;
		
		return result;
	}

	@Override
	public int getProductPriceCount() {
		return productMapper.selectProductPriceCount();
	}
	
	@Override
	public int getProductProductSeq(Integer productId) {
		
		int result = productMapper.selectProductPriceSeq(productId)+1;
		
		return result;
	}

	@Override
	public boolean registerProduct(ProductVO param, ProductPriceVO[] param1) {
		boolean successFlag = false;
		int productId = 0;
		
		// 가스정보 등록
		int result = 0;
		
		logger.debug("****** Start registerProduct param.getProductId()()) *****===*"+param.getProductId());
		if (param.getProductId() == null) {
			
			productId = getProductId();
			
			param.setProductId(Integer.valueOf(productId));
			param.setMemberCompSeq(1);				
					
			//logger.debug("****** before registerProduct param. result *****===*"+result);
			result = productMapper.insertProduct(param);
			
			if (result > 0) {
				ProductPriceVO priceVo = null;				
				
				boolean pResult = false;
				for(int i =0 ; i < param1.length ; i++ ) {
					pResult = false;
					priceVo = param1[i];
					priceVo.setProductId(productId);
					priceVo.setProductPriceSeq(i+1);
					
					pResult = registerProductPrice(priceVo);
					
					if (pResult == false) {
						// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
						successFlag = false;
					}				
				}				
				successFlag = true;
			}
		} 
		
		return successFlag;
	}

	@Override
	public boolean modifyProduct(ProductVO param, ProductPriceVO[] param1) {
		boolean successFlag = false;
		int productId = 0;
		
		// 가스정보 등록
		int result = 0;
		
		logger.debug("****** Start modifyProduct param.getProductId()()) *****===*"+param.getProductId());
		if (param.getProductId() != null) {
			
			productId = param.getProductId();
		
			result = productMapper.updateProduct(param);
			
			if (result > 0) {
				
				int delProductPrice = productMapper.deleteProductPrice(param.getProductId());
				
				if(delProductPrice > 0) {
					ProductPriceVO priceVo = null;
					
					boolean pResult = false;
					for(int i =0 ; i < param1.length ; i++ ) {
						pResult = false;
						priceVo = param1[i];
												
						//pResult = modifyProductPrice(priceVo);
						pResult = registerProductPrice(priceVo);
						
						if (pResult == false) {
							// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
							successFlag = false;
						}				
					}		
					successFlag = true;
				}				
			}
		} 
		
		return successFlag;
	}

	@Override
	public boolean deleteProductPrice(Integer productId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean modifyProductPriceStatus(ProductPriceVO param) {
		boolean successFlag = false;

		// 정보 등록
		int result = 0;
		
		result = productMapper.updateProductPriceStatus(param);
		if(param.getProductPriceSeq() == 1 && param.getProductStatus().equals("0")) {
			result = productMapper.deleteProductStatus(param);
		}
		if (result > 0) {
			successFlag = true;
		}
		
		return successFlag;
	}

	@Override
	public List<ProductVO> getProductList() {
		//logger.info("****** getProductTotalList *****===*");
		return productMapper.selectProductList();
	}

	@Override
	public List<ProductVO> getGasProductList() {
		//logger.info("****** getGasProductList *****===*");
		return productMapper.selectGasProductList();
	}

	@Override
	public ProductTotalVO getPrice(BottleVO param) {
		return productMapper.selectPrice(param);
	}

	@Override
	public List<ProductVO> getNoGasProductList() {
		return productMapper.selectNoGasProductList();
	}

	@Override
	public List<ProductPriceSimpleVO> getNoGasProductPriceList() {
		return productMapper.selectNoGasProductPriceList();
	}
	
	@Override
	public List<ProductPriceSimpleVO> getNoGasProductPriceListV2(ProductVO param) {
		return productMapper.selectNoGasProductPriceListV2(param);
	}

	@Override
	public List<ProductPriceSimpleVO> getGasProductPriceList() {
		return productMapper.selectGasProductPriceList();
	}

	@Override
	public List<ProductTotalVO> getProductTotalDetailList() {
		return productMapper.selectProductTotalDetailList();
	}

	@Override
	public List<ProductPriceSimpleVO> getCustomerLn2List() {
		return productMapper.selectCustomerLn2List();
	}

	@Override
	public List<ProductPriceSimpleVO> getTankProductPriceList() {
		return productMapper.selectTankProductPriceList();
	}

	@Override
	public List<ProductPriceSimpleVO> getLn2LProductList() {
		return productMapper.selectLn2LProductList();
	}
	
}

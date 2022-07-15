package com.gms.web.admin.service.manage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.domain.manage.ProductPriceSimpleVO;
import com.gms.web.admin.domain.manage.ProductPriceVO;
import com.gms.web.admin.domain.manage.ProductTotalVO;
import com.gms.web.admin.domain.manage.ProductVO;
import com.gms.web.admin.domain.manage.WorkReportViewVO;
import com.gms.web.admin.mapper.manage.ProductMapper;

@Service
public class ProductServiceImpl implements ProductService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ProductMapper productMapper;
	
	@Autowired
	private GasService gasService;
	
	@Autowired
	private BottleService bottleService;
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
		return productMapper.selectProductPriceList(productId);
	}

	@Override
	public List<ProductTotalVO> getProductTotalList() {
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
		String productNm = param.getProductNm();
		String productNm1 = productNm.replace("의료용", "M").replace("(","").replace(")", "").replace("일반","G").replace(" ","") ;
		
		if(productNm.indexOf("고순도믹스가스") >=0) productNm1 = productNm.replace("고순도믹스가스","HMix");
		else if(productNm.indexOf("대한의료용산화에틸렌20%") >=0) productNm1 = productNm.replace("대한의료용산화에틸렌20%","EO20");
		else if(productNm.indexOf("대한E.O가스") >=0) productNm1.replace("대한E.O가스","EOHF");
		else if(productNm.indexOf("믹스가스") >=0) productNm1.replace("믹스가스","Mix");
		else if(productNm.indexOf("냉매가스") >=0) productNm1.replace("냉매가스","ICE");
		else if(productNm.indexOf("특수가스") >=0) productNm1.replace("특수가스","SP");
		
		int result = 0;
		
		if (param.getProductId() == null) {
			
			productId = getProductId();
			
			param.setProductId(Integer.valueOf(productId));
			param.setMemberCompSeq(1);				
			
			GasVO gas = gasService.getGasDetails(param.getGasId()) ;
		
			result = productMapper.insertProduct(param);
			
			if (result > 0) {
				ProductPriceVO priceVo = null;				
				
				boolean pResult = false;
				for(int i =0 ; i < param1.length ; i++ ) {
					pResult = false;
					priceVo = param1[i];
					priceVo.setProductId(productId);
					priceVo.setProductPriceSeq(i+1);
					
					String productCapa = priceVo.getProductCapa().replace(",", "").replace("{", "").replace(")", "");
					pResult = registerProductPrice(priceVo);
					
					List<BottleVO> insertBottleList = new ArrayList<BottleVO>();
					
					//2022-03-28 Dummy  용기등록
					if(gas != null && gas.getGasId() > 0) {
					
						for(int j=0 ; j < 5 ; j++) {
							BottleVO bottle = new BottleVO();
							
							if(j==0) {
								bottle.setBottleBarCd("DH"+productNm1+productCapa);
								bottle.setBottleId("DH"+productNm1+productCapa);
								bottle.setDummyYn("Y");
							}else {
								
								bottle.setBottleBarCd("DH"+productNm1  +productCapa+"_"+(j));
								bottle.setBottleId("DH"+productNm1 + productCapa+"_"+(j));
								bottle.setDummyYn("X");
							}
							bottle.setMemberCompSeq(1);
							bottle.setGasId(gas.getGasId());
							bottle.setGasCd(gas.getGasCd());
							bottle.setProductId(productId);
							bottle.setProductPriceSeq(priceVo.getProductPriceSeq());
							bottle.setBottleCapa(priceVo.getProductCapa());
							bottle.setChargeCapa(priceVo.getProductCapa());
							bottle.setBottleOwnYn("Y");
							bottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.come"));
							bottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
							Calendar sDate 	= Calendar.getInstance();
							sDate.set(3000, 0, 0);

							bottle.setBottleChargeDt(sDate.getTime());
							bottle.setCreateId(param.getCreateId());
							
							insertBottleList.add(bottle);
						}
					}
					
					if(insertBottleList.size() > 0 )
						result = bottleService.registerBottles(insertBottleList);
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
		String productNm = param.getProductNm();
//		logger.debug("****** before registerProduct param. indexOf *****===*"+productNm.indexOf("특수가스"));
		String productNm1 = productNm.replace("의료용", "M").replace("(","").replace(")", "").replace("일반","G").replace(" ","") ;
		
		if(productNm.indexOf("고순도믹스가스") >=0) productNm1 = productNm.replace("고순도믹스가스","HMix");
		else if(productNm.indexOf("대한의료용산화에틸렌20%") >=0) productNm1 = productNm.replace("대한의료용산화에틸렌20%","EO20");
		else if(productNm.indexOf("대한E.O가스") >=0) productNm1 = productNm.replace("대한E.O가스","EOHF");
		else if(productNm.indexOf("믹스가스") >=0) productNm1 = productNm.replace("믹스가스","Mix");
		else if(productNm.indexOf("냉매가스") >=0) productNm1 = productNm.replace("냉매가스","ICE");
		else if(productNm.indexOf("특수가스") >=0) productNm1 = productNm.replace("특수가스","SP");
		
		// 가스정보 등록
		int result = 0;
		
		if (param.getProductId() != null) {
			
			productId = param.getProductId();
		
			result = productMapper.updateProduct(param);
			
			GasVO gas = gasService.getGasDetails(param.getGasId()) ;
			
			if (result > 0) {
				
				int delProductPrice = bottleService.deleteProductDummyBottle(param);
				delProductPrice = productMapper.deleteProductPrice(param.getProductId());
				
				List<BottleVO> insertBottleList = new ArrayList<BottleVO>();
				if(delProductPrice > 0) {
					ProductPriceVO priceVo = null;
					
					boolean pResult = false;
					for(int i =0 ; i < param1.length ; i++ ) {
						pResult = false;
						priceVo = param1[i];
												
						String productCapa = priceVo.getProductCapa().replace(",", "").replace("{", "").replace(")", "");
//						logger.debug("****** before registerProduct param. productCapa *****===*"+productCapa);
						//pResult = modifyProductPrice(priceVo);
						pResult = registerProductPrice(priceVo);
	
						if(gas != null && gas.getGasId() > 0) {
							
							for(int j=0 ; j < 5 ; j++) {
								BottleVO bottle = new BottleVO();
								
								if(j==0) {
									bottle.setBottleBarCd("DH"+productNm1+productCapa);
									bottle.setBottleId("DH"+productNm1+productCapa);
									bottle.setDummyYn("Y");
								}else {
									
									bottle.setBottleBarCd("DH"+productNm1  +productCapa+"_"+(j));
									bottle.setBottleId("DH"+productNm1 + productCapa+"_"+(j));
									bottle.setDummyYn("X");
								}
								bottle.setMemberCompSeq(1);
								bottle.setGasId(gas.getGasId());
								bottle.setGasCd(gas.getGasCd());
								bottle.setProductId(productId);
								bottle.setProductPriceSeq(priceVo.getProductPriceSeq());
								bottle.setBottleCapa(priceVo.getProductCapa());
								bottle.setChargeCapa(priceVo.getProductCapa());
								bottle.setBottleOwnYn("Y");
								bottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.come"));
								bottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
								Calendar sDate 	= Calendar.getInstance();
								sDate.set(3000, 0, 0);

								bottle.setBottleChargeDt(sDate.getTime());
								bottle.setCreateId(param.getCreateId());
								
								insertBottleList.add(bottle);
							}
						}
							
					}		
					logger.debug("****** before modifyProduct 3 *****===*");
					if(insertBottleList.size() > 0 )
						result = bottleService.registerBottles(insertBottleList);
					if (pResult == false) {
						// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
						successFlag = false;
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
	public List<ProductTotalVO> getPriceList(HashMap<String, Object> param) {
		return productMapper.selectPriceList(param);
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

	@Override
	public ProductPriceSimpleVO getProductPriceSimple(ProductPriceSimpleVO param) {
		return productMapper.selectProductPriceSimple(param);
	}

	@Override
	public List<ProductPriceSimpleVO> getAllProductSimpleList() {
		return productMapper.selectAllProductPriceSimpleList();
	}
	
}

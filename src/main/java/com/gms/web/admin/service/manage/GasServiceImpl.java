package com.gms.web.admin.service.manage;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.mapper.manage.GasMapper;

@Service
public class GasServiceImpl implements GasService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private GasMapper gasMapper;

	@Override
	@Transactional
	public boolean registerGas(GasVO param) {
		boolean successFlag = false;
		// 가스정보 등록
		int result = 0;
		if (param.getGasId() == null) {
			result = gasMapper.insertGas(param);
			if (result > 0) {
				successFlag = true;
			}
		} else {
			result = gasMapper.updateGas(param);
			if (result > 0) {
				successFlag = true;
			}
		}
		
		return successFlag;
	}

	@Override
	public GasVO getGasDetails(Integer gasId) {
		return gasMapper.selectGasDetail(gasId);		
	}

	
	@Override
	public GasVO getGasDetailsByNm(String gasNm) {
		return gasMapper.selectGasDetailByNm(gasNm);	
	}
	
	@Override
	@Transactional
	public boolean deleteGas(Integer gasId) {
		GasVO gas = gasMapper.selectGasDetail(gasId);
		
		logger.info("****** deleteGas.getGasId()()) *****===*"+gasId);
		logger.debug("****** deleteGas.getGasId()()) *****===*"+gas.getGasId());
		
		if (gas == null || "Y".equals(gas.getDeleteYn())) {
			return false;
		}

		int result = gasMapper.deleteGas(gasId);
		if (result < 1) {
			return false;
		}
		
		return true;
	}

	@Override
	public List<GasVO> getGasList() {
		
		return gasMapper.selectGasList();
	}

	@Override
	public GasVO getGasDetailsByCd(String gasCd) {
		return gasMapper.selectGasDetailByCd(gasCd);	
	}

}

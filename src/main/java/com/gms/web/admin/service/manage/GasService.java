package com.gms.web.admin.service.manage;

import java.util.List;

import com.gms.web.admin.domain.manage.GasVO;


public interface GasService {

	public boolean registerGas(GasVO param);

	public GasVO getGasDetails(Integer gasId);
	
	public GasVO getGasDetailsByNm(String gasNm);
	
	public GasVO getGasDetailsByCd(String gasCd);

	public boolean deleteGas(Integer gasId);

	public List<GasVO> getGasList();
}

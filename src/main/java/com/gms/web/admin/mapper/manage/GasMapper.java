package com.gms.web.admin.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.GasVO;


@Mapper
public interface GasMapper {

	public int insertGas(GasVO param);

	public GasVO selectGasDetail(Integer gasId) ;
	
	public GasVO selectGasDetailByNm(String gasNm) ;
	
	public GasVO selectGasDetailByCd(String gasCd) ;

	public int updateGas(GasVO param);

	public int deleteGas(Integer gasId);

	public List<GasVO> selectGasList();
}

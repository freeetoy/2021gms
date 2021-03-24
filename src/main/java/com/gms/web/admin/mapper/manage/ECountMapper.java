package com.gms.web.admin.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.gms.web.admin.domain.manage.ECountVO;
import com.gms.web.admin.domain.manage.WorkReportVO;


@Mapper
public interface ECountMapper {

	public List<ECountVO> selectECount(WorkReportVO workReport);
}

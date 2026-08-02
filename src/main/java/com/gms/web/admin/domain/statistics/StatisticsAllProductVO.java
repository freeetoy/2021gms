package com.gms.web.admin.domain.statistics;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsAllProductVO implements Serializable {
	
	private static final long serialVersionUID = 8684003342300538104L;

	/**Stat_Dt      */
	private String statDt;
	
	private List<StatisticsProductVO> statAllList;
}

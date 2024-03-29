package com.gms.web.admin.service.statistics;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gms.web.admin.domain.statistics.StatisticsUserVO;
import com.gms.web.admin.mapper.statistics.StatisticsUserMapper;

@Service
public class StatisticsUserServiceImpl implements StatisticsUserService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatisticsUserMapper statMapper;
	
	@Override
	public List<StatisticsUserVO> getDailylStatisticsUserList(StatisticsUserVO param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StatisticsUserVO> getMontlylStatisticsUserList(StatisticsUserVO param) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int registerDailyStatisticsUser() {
		int result = 0;
		try {
			 //result = statMapper.inserDailyStatisticsUser();
			
			List<StatisticsUserVO> userList = statMapper.inserDailyStatisticsUserOrigin();
			
			result  = statMapper.inserDailyStatisticsUserOrigin(userList);
			
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return result;
	}

	@Override
	public int registerMonthlyStatisticsUser() {
		return statMapper.inserMonthlyStatisticsUser();
	}

}

package com.twm.mgmt.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import com.twm.mgmt.persistence.repository.MomoidChangeMainRepository;


@Component
public class GetDateSerialNumUtils{
	
	
	private static MomoidChangeMainRepository momoidChangeMainRepo;
	
	private static ReentrantLock lock = new ReentrantLock();

	public static BigDecimal getNum() {

		lock.lock();
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String datetime = sdf.format(date);
		
		String maxMomoidChangeMainIdByDateStr = momoidChangeMainRepo.getMaxMomoidChangeMainIdByDateStr(datetime);
		maxMomoidChangeMainIdByDateStr= datetime + maxMomoidChangeMainIdByDateStr;
		BigDecimal bigDecimal = new BigDecimal(maxMomoidChangeMainIdByDateStr);
		bigDecimal = bigDecimal.add(BigDecimal.ONE);
		
		lock.unlock();
		
		return bigDecimal;
	}
	
	
	   @Autowired
	    public void setMomoidChangeMainRepo(MomoidChangeMainRepository momoidChangeMainRepo){
		   GetDateSerialNumUtils.momoidChangeMainRepo = momoidChangeMainRepo;
	    }

}

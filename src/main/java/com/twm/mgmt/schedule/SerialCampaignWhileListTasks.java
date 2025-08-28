package com.twm.mgmt.schedule;

import java.net.InetAddress;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.twm.mgmt.Enum.LogFormat;
import com.twm.mgmt.service.SerialCampaignWhileListTasksService;

import lombok.extern.slf4j.Slf4j;



@Slf4j
@Component
@Configurable
@EnableScheduling
public class SerialCampaignWhileListTasks {
	
	@Value("${environment.name}")
	private String envName;
	
	@Autowired
	private SerialCampaignWhileListTasksService serialCampaignWhileListTasksService;
		
	private String commonFormat = LogFormat.CommonLog.getName();
	private String errorFormat = LogFormat.SqlError.getName();
	
	private SimpleDateFormat dateFormat() {
		return new SimpleDateFormat("yyyyMMdd");
	}


	/**
	 * 發幣條件適用期間起日判斷未到跟已到
	 * @throws UnknownHostException
	 */
	@Scheduled(cron = "${cron.serialCampaignWhileListExpired}")
	public void serialCampaignWhileListExpired() throws UnknownHostException{
		String schedule_id = "serialCampaignWhileListExpired_序號簡訊白名單逾期排程執行" + dateFormat().format(new Date());
		
		InetAddress addr = InetAddress.getLocalHost();
		int ipTail = Integer.parseInt(StringUtils.split(addr.getHostAddress(),".")[3]);

		if (!envName.equals("DEV") && ipTail % 2 == 0) return; 
		try {
			log.info(commonFormat, "-", schedule_id, "-", "只有在每日02:00，才會啟動排程", "-", "-", "-", "-");
			log.info(commonFormat, "-", schedule_id, "-", "addr:", addr, "UAT和PROD環境只有ip尾數是奇數的server才會執行此排程", "-", "-");
			
			serialCampaignWhileListTasksService.processApprovalBatches();
		} catch (Exception e) {
			log.info(commonFormat, "-", schedule_id, "-", "-", "失敗", "-", "-", e.toString(),e);
			log.error(errorFormat, "-", schedule_id, "-", "失敗", e.toString(), e);
		}
		
	}
	

	
}

package com.xzymon.sylar.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class MainScheduler {
	private static final Logger LOGGER = LoggerFactory.getLogger(MainScheduler.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	//@Scheduled(fixedRate = 60 * 1000)
	@Scheduled(cron = "0 * * * * *")
	public void reportCurrentTime() {
		LOGGER.info("The time is now {}", dateFormat.format(new Date()));
	}
}

package com.hisign.solr;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Date;
import java.util.Properties;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzI {
	
	public void run(){
		try {
			Properties prop = PropertiesUtil.getProp();
			String dayQuartz = prop.getProperty("day.quartz");
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler scheduler = sf.getScheduler();
			
			
			JobDetail job = newJob(MidFileCreator.class).withIdentity("day", "group")
		            .build();
			CronTrigger cronTrigger = newTrigger().withIdentity("cronTrigger","group")
					.withSchedule(cronSchedule(dayQuartz)).build();
			Date ft = scheduler.scheduleJob(job,cronTrigger);
			scheduler.start();
			SchedulerMetaData metaData = scheduler.getMetaData();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		QuartzI quartzi = new QuartzI();
		quartzi.run();
	}
}

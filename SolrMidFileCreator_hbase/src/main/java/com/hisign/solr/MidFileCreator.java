package com.hisign.solr;

import java.util.Properties;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class MidFileCreator implements Job {
	
	public static void main(String[] args) {
		try {
			Properties prop = PropertiesUtil.getProp();
			ModuleOptions.setConnectString(prop.getProperty("connection"));
			ModuleOptions.setMiddleFileRoot(prop.getProperty("root"));
			ModuleOptions.setProcessorIP(prop.getProperty("myip"));

			if (!MySQLManager.initDBDriver()) {
				System.out.println("falied to load mysql driver");
			}

			DispatchProcess sp = new DispatchProcess();
			long start = System.currentTimeMillis();
			do {
				sp.startThreadPool();
			} while (sp.execute());
			long end = System.currentTimeMillis();
			System.out.println("耗费时长为:" + (end - start));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		try {
			Properties prop = PropertiesUtil.getProp();
			ModuleOptions.setConnectString(prop.getProperty("connection"));
			ModuleOptions.setMiddleFileRoot(prop.getProperty("root"));
			ModuleOptions.setProcessorIP(prop.getProperty("myip"));

			if (!MySQLManager.initDBDriver()) {
				System.out.println("falied to load mysql driver");
			}

			DispatchProcess sp = new DispatchProcess();
			long start = System.currentTimeMillis();
			do {
				sp.startThreadPool();
			} while (sp.execute());
			long end = System.currentTimeMillis();
			System.out.println("耗费时长为:" + (end - start));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

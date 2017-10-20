package com.hisign.solr;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class DispatchProcess {
	private ExecutorService exeSvr = null;

	public void startThreadPool() {
		int threadCount = Runtime.getRuntime().availableProcessors() * 2;
		exeSvr = Executors.newFixedThreadPool(threadCount);
	}

	public boolean execute() {
		boolean return_value = true;
		List<DBItemContent> tmpItems = new ArrayList<DBItemContent>();
		try {
			synchronized (ModuleOptions.synDatabse) {
				MySQLManager.openDbConnection();
				Statement stmt = (Statement) MySQLManager.getDbConnection().createStatement();
				String sql = "SELECT id, data_ip, md5, file_path ,user_name, update_time FROM "
						+ "upload WHERE (flag=2 or flag=4)  AND data_ip='" + ModuleOptions.getProcessorIP() + "'"
						+ " AND (file_type = 'xls' OR file_type = 'xlsx')"
						+ " AND md5 is NOT null ORDER BY ID limit 1000";

				ResultSet rs = stmt.executeQuery(sql);
				int num = 0;
				String recordID, mountPath, dataIP, md5, userName, updateTime;
				while (rs.next()) {
					num++;
					recordID = String.valueOf(rs.getInt(1));
					dataIP = rs.getString(2);
					md5 = rs.getString(3);
					mountPath = rs.getString(4);
					userName = rs.getString(5);
					updateTime = rs.getString(6);
					DBItemContent dbItem = new DBItemContent(recordID, mountPath, dataIP, md5, userName, updateTime);
					tmpItems.add(dbItem);
				}
				if (num < 1000) {
					return_value = false;
				}
				rs.close();
				stmt.close();
				sql = "UPDATE upload set flag=4 WHERE (flag=2 or flag=4) AND data_ip='" + ModuleOptions.getProcessorIP()
						+ "'  AND (file_type ='xls' OR file_type = 'xlsx')"
						+ " AND md5 is NOT null ORDER BY ID limit 1000";
				PreparedStatement pstam = (PreparedStatement) MySQLManager.getDbConnection().prepareStatement(sql);
				pstam.executeUpdate();
				MySQLManager.closeDbConnection();
			}
			System.out.println("Find " + tmpItems.size() + " file to process");
			for (int i = 0; i < tmpItems.size(); ++i) {
				exeSvr.execute(new WorkThread(tmpItems.get(i)));
			}
			tmpItems.clear();

			exeSvr.shutdown();
			try {
				exeSvr.awaitTermination(5, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return return_value;
	}
}

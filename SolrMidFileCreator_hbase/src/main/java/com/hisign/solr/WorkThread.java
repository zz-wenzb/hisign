package com.hisign.solr;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.map.MultiValueMap;

import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.common.Term;

public class WorkThread implements Runnable {
	private String fileMD5;
	private String mountDir;
	private String locationIP;
	private String recordID;
	private String userName;
	private String updateTime;

	/**
	 * max segment size to enroll solr (exceed this value will lead to poor
	 * query performance)
	 */
	private static final int MAX_SEG_SIZE = 10240;

	public WorkThread(String recordID, String dataIP, String md5, String mountPath) {
		this.fileMD5 = md5;
		this.mountDir = mountPath;
		this.locationIP = dataIP;
		this.recordID = recordID;
	}

	public WorkThread(DBItemContent itemContent) {
		this.fileMD5 = itemContent.getMd5();
		this.mountDir = itemContent.getMountPath();
		this.locationIP = itemContent.getDataIP();
		this.recordID = itemContent.getRecordID();
		this.userName = itemContent.getUserName();
		this.updateTime = itemContent.getUpdateTime();
	}

	public void run() {
		String fileContent = ExcelUtil.getContent01(mountDir);
		
		if (null == fileContent || 0 == fileContent.length()) {
			SolrMiddleCache.addFailedRecord(recordID);
		} else if (indexFileContent(fileContent, new File(mountDir).getName())) {
			// 更新数据库表
//			String s = "s";
//			String d = "d";
//			String file = SolrMiddleCache.getFile();
//			boolean ss = DataImportUtils.getFileNum_(file + "s/", recordID);
//			boolean dd = DataImportUtils.getFileNum_(file + "d/", recordID);
//			Date date = new Date();
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			String gnerTime = sdf.format(date);
//			if (ss && dd) {
//				DataImportUtils.updateSQL(s + "_" + recordID, fileMD5, userName, s, gnerTime);
//				DataImportUtils.updateSQL(d + "_" + recordID, fileMD5, userName, d, gnerTime);
//			} else {
//				if (ss && !dd) {
//					DataImportUtils.updateSQL(s + "_" + recordID, fileMD5, userName, s, gnerTime);
//				}
//				if (dd && !ss) {
//					DataImportUtils.updateSQL(d + "_" + recordID, fileMD5, userName, d, gnerTime);
//				}
//			}
		} else {
			SolrMiddleCache.addFailedRecord(recordID);
		}

	}

	/**
	 * true
	 * 
	 * @param filterSense
	 * @param segIndex
	 * @return
	 */
	private MultiValueMap NameingExtract(StringBuffer filterSense, int segIndex) {

		Segment segment = new NShortSegment().enableCustomDictionary(false).enablePlaceRecognize(true)
				.enableOrganizationRecognize(true);
		String filterTabRet = filterSense.toString().replaceAll("\\t", " ");
		filterTabRet = filterTabRet.replaceAll("\\n", " ");
		// filterTabRet = DataImportUtils.qj2bj(filterTabRet);
		List<Term> termList = segment.seg(filterTabRet);
		MultiValueMap segMap = DataImportUtils.extractNamingEntity(termList);
		segMap.put("content", filterSense.toString());
		return segMap;
	}

	private boolean indexFileContent(String fileContent, String mountDirName) {
		if (fileContent.length() == 0) {
			return false;
		}
		int segIndex = 0;
		try {
			int endPos = 0;
			List<MultiValueMap> segMapList = new ArrayList<>();
			do {
				endPos = (fileContent.length() - (segIndex + 1) * MAX_SEG_SIZE) > 0 ? (segIndex + 1) * MAX_SEG_SIZE
						: fileContent.length();
				String tmpContent = fileContent.substring(segIndex * MAX_SEG_SIZE, endPos);
				if ((tmpContent != null) && (tmpContent.length() > 0)) {
					StringBuffer filterSense = new StringBuffer();
					filterSense.append(tmpContent);
					MultiValueMap segMap = NameingExtract(filterSense, segIndex++);
					segMapList.add(segMap);
				} else {
					System.out.println("Range======[" + segIndex * MAX_SEG_SIZE + ", " + endPos + " ]");
					break;
				}
			} while ((fileContent.length() - endPos) > 0);
			SolrMiddleCache.createMiddleFile(recordID, segMapList, mountDirName);
		} catch (Exception e) {
			return false;
		}
		return segIndex > 0;
	}
}

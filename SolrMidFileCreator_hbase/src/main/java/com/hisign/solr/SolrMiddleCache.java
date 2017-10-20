package com.hisign.solr;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiValueMap;

import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.common.Term;
import com.mysql.jdbc.PreparedStatement;

/**
 * @brief multi process enroll will lead to solr server break down with this
 *        object cache the file to be commit and reduce solr access count
 * @author lukeleo
 *
 */
public class SolrMiddleCache {
	static Segment segment = new NShortSegment().enableCustomDictionary(false).enablePlaceRecognize(true)
			.enableOrganizationRecognize(true);
	private static String fieldNames[] = { "wechatnum", "enginenum", "name", "location", "organization", "company",
			"id", "mobilenum", "idnum", "affair", "goods", "md5", "telenum", "bankcard", "email", "QQnum", "platenum",
			"webaddr", "rawstr", "occupation", "content" };

	private static String file;

	private SolrMiddleCache() {

	}

	public static String getFile() {
		return file;
	}

	public static void addFailedRecord(String recID) {
		updateFinished(recID, 3);
	}

	public static void updateFinished(String recID, int flag) {
		synchronized (ModuleOptions.synDatabse) {
			System.out.println("start to update id:" + recID);
			Connection conn = null;
			try {
				MySQLManager.openDbConnection();
				String sql = "UPDATE upload set flag = " + flag + " WHERE id=\'" + recID + "\'";
				PreparedStatement pstam = (PreparedStatement) MySQLManager.getDbConnection().prepareStatement(sql);
				pstam.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				MySQLManager.closeDbConnection();
				SQLManager.closeConnection(conn);
			}
			System.out.println("finished update id:" + recID);
		}
	}

	/**
	 * 
	 * 
	 * @param recID
	 * @param md5
	 * @param segMapList
	 * @param mountDir
	 * @param i
	 */
	public static void createMiddleFile(String recID, List<MultiValueMap> segMapList, String mountDirName) {
		String fileNameBase = ModuleOptions.getMiddleFileRoot();
		File folder = new File(fileNameBase);
		if (!folder.exists() || folder.isFile()) {
			folder.mkdirs();
		}
		String fileName = "";
		for (int index = 0; index < segMapList.size(); ++index) {
			fileName = fileNameBase + "/";
			// file: 为了判断生成的中间文件有几种类型（s/d）
			file = fileName;
			MultiValueMap mapData = segMapList.get(index);
			createSegFile(recID, fileName, mapData, mountDirName);
		}
		updateFinished(recID, 21);
	}

	private static void createSegFile(String recID, String fileName, MultiValueMap mapData, String mountDirName) {
		// int qq_col = 0;
		List qq_col = new ArrayList<>();

		String filename01 = fileName + "s/";
		String filename02 = fileName + "d/";
		try {
			File folder01 = new File(filename01);
			if (!folder01.exists() || folder01.isFile()) {
				folder01.mkdirs();
			}
			File folder02 = new File(filename02);
			if (!folder02.exists() || folder02.isFile()) {
				folder02.mkdirs();
			}
			filename01 = filename01 + "s_" + recID;
			filename02 = filename02 + "d_" + recID;
			Collection<?> mapcoll = mapData.getCollection("content");
			Iterator<?> ii = mapcoll.iterator();

			while (ii.hasNext()) {

				String value = (String) ii.next();
				String[] content = value.split("\n");
				String[] str;
				StringBuffer s = new StringBuffer();// 写入剩余的字段
				int flag = 1;
				for (int a = 0; a < content.length; a++) {
					int num = 0;

					String mobile = "";
					String id = "";
					String name = "";
					String email = "";
					String qq = "";
					String ip = "";

					List<String> list_mobile = new ArrayList<>();
					List<String> list_id = new ArrayList<>();
					List<String> list_name = new ArrayList<>();
					List<String> list_email = new ArrayList<>();
					List<String> list_qq = new ArrayList<>();
					List<String> list_ip = new ArrayList<>();

					str = new String[12];
					String[] colum = content[a].split("\t");
					for (int b = 0; b < colum.length; b++) {
						// colum[b] = DataImportUtils.qj2bj(colum[b]);
						colum[b] = colum[b].replace("", "").replace("\n", "").replace("\r", "").toUpperCase();

						if (a == 0) {
							colum[b] = colum[b].toLowerCase();
							if ("qq".equals(colum[b]) || "qqhm".equals(colum[b]) || "qq号".equals(colum[b])
									|| "qq_id".equals(colum[b])) {
								qq_col.add(b);
							}
						}

						// 判断单元格是否被提取关键字段
						boolean fg = false;
						String word = null;
						List<Term> termList = segment.seg(colum[b]);
						for (int iword = 0; iword < termList.size(); iword++) {

							word = termList.get(iword).word;
							// 0-1
							if (mapData.getCollection("mobilenum") != null
									&& mapData.getCollection("mobilenum").contains(word)) {
								num++;
								fg = true;
								if ("".equals(mobile)) {// 是第一个
									mobile = word;
								} else {
									// 去重
									if (!word.equals(mobile) && !list_mobile.contains(word)) {
										flag = 2;
										list_mobile.add(word);
									} else {
										num--;
									}
								}
							}
							// 2-3
							else if (mapData.getCollection("idnum") != null
									&& mapData.getCollection("idnum").contains(word)) {
								num++;
								fg = true;
								if ("".equals(id)) {// 是第一个
									id = word;
								} else {
									if (!word.equals(id) && !list_id.contains(word)) {
										flag = 2;
										list_id.add(word);
									} else {
										num--;
									}
								}
							}
							// 4-5
							else if (mapData.getCollection("name") != null
									&& mapData.getCollection("name").contains(word)) {
								num++;
								fg = true;
								if ("".equals(name)) {// 是第一个
									name = word;
								} else {
									if (!word.equals(name) && !list_name.contains(word)) {
										flag = 2;
										list_name.add(word);
									} else {
										num--;
									}
								}
							}

							// else if (mapData.getCollection("QQnum") != null
							// && mapData.getCollection("QQnum").contains(word))
							// {
							// num++;
							// if (qq_col.contains(b)) {
							// fg = true;
							// if ("".equals(qq)) {// 是第一个
							// qq = word;
							// } else {
							// flag = 2;
							// list_qq.add(word);
							// }
							// }
							// }
							// 8-9 只有列名和正则匹配都符合qq，才认定
							else if (DataImportUtils.IsQQNumPossible(word)) {
								if (qq_col.contains(b)) {
									num++;
									fg = true;
									if ("".equals(qq)) {// 是第一个
										qq = word;
									} else {
										if (!word.equals(qq) && !list_qq.contains(word)) {
											flag = 2;
											list_qq.add(word);
										} else {
											num--;
										}
									}
								}
							}
						}
						// 6-7 email
						if (DataImportUtils.IsEmailFormat(colum[b])) {
							num++;
							colum[b] = colum[b].toLowerCase();
							String email_sub = colum[b].substring(colum[b].indexOf("@") + 1, colum[b].lastIndexOf("."));
							email_sub = email_sub.toLowerCase();
							// 截取qq邮箱里面的qq号
							if ("qq".equals(email_sub)) {
								String qq_sub = colum[b].substring(0, colum[b].indexOf("@"));
								// 判断截取出来的字符串是不是qq
								if (DataImportUtils.IsQQNumPossible(qq_sub)) {
									num++;
									fg = true;
									if ("".equals(qq)) {// 是第一个
										qq = qq_sub;
									} else {
										if (!qq_sub.equals(qq) && !list_qq.contains(qq_sub)) {
											flag = 2;
											list_qq.add(qq_sub);
										} else {
											num--;
										}
									}
								}
							}

							fg = true;
							if ("".equals(email)) {// 是第一个
								email = colum[b];
							} else {
								if (!colum[b].equals(email) && !list_email.contains(colum[b])) {
									flag = 2;
									list_email.add(colum[b]);
								} else {
									num--;
								}
							}
						} // 10-11
						else if (DataImportUtils.IsPossibleIp(colum[b])) {
							num++;
							fg = true;
							if ("".equals(ip)) {// 是第一个
								ip = colum[b];
							} else {
								if (!colum[b].equals(ip) && !list_ip.contains(colum[b])) {
									flag = 2;
									list_ip.add(colum[b]);
								} else {
									num--;
								}
							}
						}
						if (!fg) {
							// 判断一下这一列是否属于银行卡，车牌号，公司
							for (int info = 0; info < termList.size(); info++) {
								word = termList.get(info).word;
								if (mapData.getCollection("bankcard") != null
										&& mapData.getCollection("bankcard").contains(word)) {
									// 银行卡号s
									colum[b] = colum[b].replace(word, "bankcard:" + word);
								}
							}
							if (DataImportUtils.IsValidPlateNum_t(colum[b])) {
								// 车牌号
								colum[b] = colum[b].replace(colum[b], "PlateNum:" + colum[b]);
							}

							if (mapData.getCollection("company") != null
									&& mapData.getCollection("company").contains(colum[b])) {
								// 公司
								colum[b] = colum[b].replace(colum[b], "company:" + colum[b]);
							}

							if (s.length() > 0) {
								s.append("~$#");
							}
							colum[b] = colum[b].replace("\n", "").replace(" ", "").replace("\r", "").replace("\t", "");
							s.append(colum[b]);
						}
					}
					if (num > 1) {
						Map<Integer, List> map_list = new HashMap<>();
						map_list.put(1, list_mobile);
						map_list.put(2, list_id);
						map_list.put(3, list_name);
						map_list.put(4, list_email);
						map_list.put(5, list_qq);
						map_list.put(6, list_ip);
						Map<String, String> map_ = new HashMap<>();
						map_.put("mobile", mobile);
						map_.put("id", id);
						map_.put("name", name);
						map_.put("email", email);
						map_.put("qq", qq);
						map_.put("ip", ip);
						digui(map_list, map_,s.toString(), flag, filename01, filename02, mountDirName, recID);
					}
					s.setLength(0);
					 flag = 1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("报错了...");
		}
	}

	/**
	 * 递归操作，写入文件s
	 * @param map_list
	 * @param map_
	 */
	public static void digui(Map<Integer, List> map_list, Map<String, String> map_,String s,int flag
			,String filename01,String filename02,String mountDirName,String recID) {

		String mobile = "";
		String id = "";
		String name = "";
		String email = "";
		String qq = "";
		String ip = "";
		List<String> list_mobile = new ArrayList<>();
		List<String> list_id = new ArrayList<>();
		List<String> list_name = new ArrayList<>();
		List<String> list_email = new ArrayList<>();
		List<String> list_qq = new ArrayList<>();
		List<String> list_ip = new ArrayList<>();

		String[] str = new String[12];

		for (Integer i : map_list.keySet()) {
			if (i == 1) {
				list_mobile = map_list.get(i);
			} else if (i == 2) {
				list_id = map_list.get(i);
			} else if (i == 3) {
				list_name = map_list.get(i);
			} else if (i == 4) {
				list_email = map_list.get(i);
			} else if (i == 5) {
				list_qq = map_list.get(i);
			} else {
				list_ip = map_list.get(i);
			}
		}

		if (list_mobile.size() == 0) {
			list_mobile.add("");
		}
		if (list_id.size() == 0) {
			list_id.add("");
		}
		if (list_name.size() == 0) {
			list_name.add("");
		}
		if (list_email.size() == 0) {
			list_email.add("");
		}
		if (list_qq.size() == 0) {
			list_qq.add("");
		}
		if (list_ip.size() == 0) {
			list_ip.add("");
		}

		for (String key : map_.keySet()) {
			if ("mobile".equals(key)) {
				mobile = map_.get(key);
			} else if ("id".equals(key)) {
				id = map_.get(key);
			} else if ("name".equals(key)) {
				name = map_.get(key);
			} else if ("email".equals(key)) {
				email = map_.get(key);
			} else if ("qq".equals(key)) {
				qq = map_.get(key);
			} else if ("ip".equals(key)) {
				ip = map_.get(key);
			}
		}

		// 0-1
		if (mobile != null) {
			str[0] = mobile;
			if (list_mobile.size() > 0) {
				str[1] = list_mobile.get(0);
				list_mobile.remove(0);
			}
		}
		// 2-3
		if (id != null) {
			str[2] = id;
			if (list_id.size() > 0) {
				str[3] = list_id.get(0);
				list_id.remove(0);
			}
		}
		// 4-5
		if (name != null) {
			str[4] = name;
			if (list_name.size() > 0) {
				str[5] = list_name.get(0);
				list_name.remove(0);
			}
		}
		// 6-7
		if (email != null) {
			str[6] = email;
			if (list_email.size() > 0) {
				str[7] = list_email.get(0);
				list_email.remove(0);
			}
		}

		// 8-9
		if (qq != null) {
			str[8] = qq;
			if (list_qq.size() > 0) {
				str[9] = list_qq.get(0);
				list_qq.remove(0);
			}
		}
		// 10-11
		if (ip != null) {
			str[10] = ip;
			if (list_ip.size() > 0) {
				str[11] = list_ip.get(0);
				list_ip.remove(0);
			}
		}

		DataImportUtils.method(str, s.toString(), flag, filename01, filename02, mountDirName, recID);

		map_.clear();
		map_list.clear();
		if (list_mobile.size() != 0 || list_id.size() != 0 || list_name.size() != 0 || list_email.size() != 0
				|| list_qq.size() != 0 || list_ip.size() != 0) {
			if (list_mobile != null && list_mobile.size() == 0) {
				list_mobile.add(str[1]);
			}
			if (list_id != null && list_id.size() == 0) {
				list_id.add(str[3]);
			}
			if (list_name != null && list_name.size() == 0) {
				list_name.add(str[5]);
			}
			if (list_email != null && list_email.size() == 0) {
				list_email.add(str[7]);
			}
			if (list_qq != null && list_qq.size() == 0) {
				list_qq.add(str[9]);
			}
			if (list_ip != null && list_ip.size() == 0) {
				list_ip.add(str[11]);
			}
			map_list.put(1, list_mobile);
			map_.put("mobile", mobile);
			map_list.put(2, list_id);
			map_.put("id", id);
			map_list.put(3, list_name);
			map_.put("name", name);
			map_list.put(4, list_email);
			map_.put("email", email);
			map_list.put(5, list_qq);
			map_.put("qq", qq);
			map_list.put(6, list_ip);
			map_.put("ip", ip);

			str = null;
			str = new String[12];
			digui(map_list, map_,s.toString(), flag, filename01, filename02, mountDirName, recID);
		}
	}
}

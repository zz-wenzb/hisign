package com.hisign.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.NShort.NShortSegment;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import com.hankcs.hanlp.tokenizer.SpeedTokenizer;
import com.hisign.solr.DataImportUtils;
import com.hisign.solr.ExcelUtil;
import com.hisign.solr.SolrMiddleCache;

public class Test {

	public static void main(String[] args) {
		method28();
	}

	public static void method28(){
		String sb = ExcelUtil.getContent01("G:\\xx\\test__.xlsx");
		System.out.println(sb);
	}
	
	public static void method27(){
		Map<String,String> map_ = new HashMap<>();
		map_.put("mobile", "13027729317");
		map_.put("id", "123456789");
		map_.put("name", "文志斌");
		for(String key : map_.keySet()){
			System.out.println(map_.get(key));
		}
		map_.clear();
		for(String key : map_.keySet()){
			System.out.println(map_.get(key));
			System.out.println("输出东西了吗");
		}
		map_.put("mobile", "13027729317");
		map_.put("id", "123456789");
		map_.put("name", "文志斌");
		for(String key : map_.keySet()){
			System.out.println(map_.get(key));
		}
	}
	
	public static void method26(){
		Map<Integer,List> map = new HashMap<>();
		Map<String,String> map_ = new HashMap<>();
		map_.put("mobile", "13027729317");
		map_.put("id", "123456789");
		map_.put("name", "文志斌");
		StringBuffer sb = new StringBuffer();
		List list01 = new ArrayList<String>();
		List list02 = new ArrayList<String>();
		List list03 = new ArrayList<String>();
		List list04 = new ArrayList<String>();
		List list05 = new ArrayList<String>();
		List list06 = new ArrayList<String>();
		
		list01.add("987654321");
		list01.add("456123789");
		list01.add("789123456");
		list01.add("654321987");
		
		list02.add("18525509251");
		list02.add("18039235276");
		list02.add("15149045524");
		
		list03.add("张三");
		list03.add("李四");
		list03.add("王武");
		map.put(2, list01);
		map.put(1, list02);
		map.put(3, list03);
//		SolrMiddleCache.digui(map, map_);
//		String qq  = "";
//		String mob  = "";
//		for(int a=0;a<list01.size();a++){
//			if(a == 0){
//				qq = (String) list01.get(a);
//				sb.append(qq+" ");
//			}else if(a == 1){
//				sb.append(list01.get(a)+" ").append("\n");
//			}else{
//				sb.append(qq+" ").append(list01.get(a)+" ").append("\n");
//			}
//			
//		}
//		for(int b=0;b<list02.size();b++){
//			if(b == 0){
//				mob = (String) list02.get(b);
//				sb.insert(0, list02.get(b)+" ");
//			}else if(b == 1){
//				sb.insert(sb.indexOf(" ")+1, list02.get(b)+" ");
//			}else{
//				
//			}
//		}
//		System.out.println(sb);
//		StringBuffer mBuffer = new StringBuffer("=="+"\n"+"==");
//	    int num = 11;
//	    StringBuffer sa = mBuffer.append(num);
////	    System.out.println(sa);
//
//	    mBuffer.append("88");
//	    mBuffer.append("mm");
//	    mBuffer.append("$$$");
//	    mBuffer.append("java  html   sql ");
////	    System.out.println(mBuffer.toString());
//	    mBuffer.insert(2,"w");//在第二个位置上插入空格
//	    System.out.println(mBuffer.toString());
//	    mBuffer.insert(0, " ");
//	    System.out.println(mBuffer.toString());
//	    mBuffer.insert(6, " ");
//	    System.out.println(mBuffer.toString());
	}
	
	public static void method25(){
		String s = ExcelUtil.getContent("G:\\xx\\text.xlsx");
		System.out.println(s);
	}
	
	public static void method24(){
		String s = "１２３４５６＠１２.com";
		s = DataImportUtils.qj2bj(s);
		System.out.println(s);
	}
	
	public static void method23() {
		String s = "aaa,bbb,ccc,ddd";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 100; i++) {
			sb.append(s).append(",");
		}
		long a = System.currentTimeMillis();
		for (int m = 0; m < 100000; m++) {
			sb.toString().split(",");
		}
		long b = System.currentTimeMillis();
		for (int m = 0; m < 100000; m++) {
			String[] str = StringUtils.split(sb.toString(), ",");
		}
		// for(int j=0;j<str.length;j++){
		// System.out.println(str[j]);
		// }
		long c = System.currentTimeMillis();
		System.out.println(b - a);
		System.out.println(c - b);
	}

	public static void method22() {
		String qq = "1615312491";
		System.out.println(DataImportUtils.IsQQNumPossible(qq));
	}

	public static void method21() {
		String s = "11x";
		System.out.println(s.toUpperCase());
		System.out.println(s);
	}

	public static void method20() {
		String s = "1" + (char) 1 + "2" + (char) 1 + "3";
		System.out.println(s);
		s = s.replace("", "");
		System.out.println(s);
	}

	public static void method19() {
		String email = "1615312491@qq.com";
		email = email.substring(0, email.indexOf("@"));
		System.out.println(email);
	}

	public static void method18() {
		StringBuffer sbu = new StringBuffer();
		String value = "001";
		String[] chars = value.split(",");
		for (int i = 0; i < chars.length; i++) {
			sbu.append((char) Integer.parseInt(chars[i]));
		}
		System.out.println(sbu.toString().replace("", "+"));
	}

	public static void method17() {
		System.out.println((char) 1);
	}

	public static void method16() {
		// for(int i=0;i<10;i++){
		// UUID uuid = UUID.randomUUID();
		// System.out.println(uuid.toString());
		// }

		Date date = new Date();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String d = s.format(date);
		System.out.println(d);

	}

	public static void method15() {
		String s = "川RBV979";

		boolean flag = DataImportUtils.IsValidPlateNum_t(s);
		System.out.println(flag);
	}

	public static void method14() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ftp", "root", "123456");
			Statement st = conn.createStatement();

			String sql = "SELECT id, data_ip, md5, file_path ,user_name, update_time FROM "
					+ "upload WHERE flag=2  AND data_ip='10.1.17.53'" + " AND (file_type = 'xls' OR file_type = 'xlsx')"
					+ " AND md5 is NOT null ORDER BY ID limit 1000";
			ResultSet rs = st.executeQuery(sql);
			String recordID, mountPath, dataIP, md5, userName, updateTime;
			while (rs.next()) {
				recordID = String.valueOf(rs.getInt(1));
				dataIP = rs.getString(2);
				md5 = rs.getString(3);
				mountPath = rs.getString(4);
				userName = rs.getString(5);
				updateTime = rs.getString(6);
				System.out.println(recordID);
			}
		} catch (Exception e) {
		}
	}

	public static void method13() {
		String[] str = new String[2];
		str[0] = "a";
		str[1] = "b";
		for (int i = 0; i < str.length; i++) {
			System.out.println(str[i]);
		}
		str = null;
		str = new String[12];
		for (int i = 0; i < str.length; i++) {
			System.out.println(str[i]);
		}
	}

	public static void method12() {
		String[] s = new String[4];
		String a = " ";
		String b = " ";
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				s[0] = a;
				s[1] = "i" + i;
				s[2] = b;
				s[3] = "j" + j;
				for (int str = 0; str < s.length; str++) {
					System.out.print(s[str]);
				}
				System.out.println();
			}
		}

	}

	public static void method11() {
		String s = "13027729317!@333#$18525509251!@#$15149045524";

		boolean flag = DataImportUtils.IsPossibleMobile_bak(s);
		System.out.println(flag);
	}

	public static void method10() {
		List<String> list_mobile = new ArrayList<>();
		List<String> list_id = new ArrayList<>();
		List<String> list_name = new ArrayList<>();
		list_mobile.add("");
		list_mobile.add("mobile2");
		list_name.add("name1");
		list_name.add("name2");
		if (list_mobile.size() == 0) {
			list_mobile.add("");
		}
		if (list_id.size() == 0) {
			list_id.add("");
		}
		if (list_name.size() == 0) {
			list_name.add("");
		}
		for (int a = 0; a < list_mobile.size(); a++) {
			for (int b = 0; b < list_id.size(); b++) {
				// 中间for循环没有值，整个循环不走
				for (int c = 0; c < list_name.size(); c++) {
					System.out.println(list_mobile.get(a) + " " + list_id.get(b) + " " + list_name.get(c));
				}
			}
		}
	}

	public static void method09() {
		String s = null;
		String[] str = new String[2];
		str[0] = s;
		for (int i = 0; i < str.length; i++) {
			System.out.println(str[i]);
		}
	}

	public static void method08() {
		Segment segment = HanLP.newSegment().enableAllNamedEntityRecognize(true).enableCustomDictionary(true)
				.enableOffset(true).enableIndexMode(true).enablePartOfSpeechTagging(true);
		List<Term> termList = segment.seg("6217002430008593312vava蒙F5B773");
		for (int i = 0; i < termList.size(); i++) {
			System.out.println(termList.get(i).word);
		}
	}

	public static void method07() {
		String s = "黑龙江哈尔滨市道里区工农大街98号";
		// s.substring(0, s.indexOf("哈尔滨"));
		s = s.replace("哈尔滨", "11:哈尔滨");
		System.out.println(s);
	}

	public static void method06() {
		String s = "";
		String ss = "222";
		s += "11" + ss;
		s += "88" + ss;
		System.out.println(s);
	}

	public static void method05() {
		Segment segment = HanLP.newSegment().enableAllNamedEntityRecognize(true).enableCustomDictionary(true)
				.enableOffset(true).enableIndexMode(true).enablePartOfSpeechTagging(true);
		List<Term> termList = segment.seg("6217002430008593312vava蒙F5B773");
		for (int i = 0; i < termList.size(); i++) {
			System.out.println(termList.get(i).word);
		}
	}

	public static void method04() {
		Segment segment = new NShortSegment().enableCustomDictionary(false).enablePlaceRecognize(true)
				.enableOrganizationRecognize(true);
		try {
			StringBuffer sb = new StringBuffer();
			File file = new File("G:\\4_0.log");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			while (br.readLine() != null) {
				if (sb.length() > 0) {
					sb.append("\n");
				}
				sb.append(br.readLine());
			}
			String filterTabRet = sb.toString().replaceAll("\\t", " ");
			// filterTabRet = filterTabRet.replaceAll("\\n", " ");
			List<Term> termList = segment.seg(filterTabRet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 索引分词
	 */
	// 主副食品/n [0:4]
	// 主副食/j [0:3]
	// 副食/n [1:3]
	// 副食品/n [1:4]
	// 食品/n [2:4]
	public static void method03() {
		// List<Term> termList = IndexTokenizer.segment("主副食品");
		List<Term> termList = IndexTokenizer.segment("阿里巴巴集团");
		for (Term term : termList) {
			System.out.println(term + " [" + term.offset + ":" + (term.offset + term.word.length()) + "]");
		}
	}

	/**
	 * 极速词典分词 [江西/null, 鄱阳湖/null, 干枯/null, ，/null, 中国/null, 最/null, 大/null,
	 * 淡水湖/null, 变成/null, 大/null, 草原/null] 分词速度：33726812.82字每秒
	 */
	public static void method02() {
		String text = "江西鄱阳湖干枯，中国最大淡水湖变成大草原";
		System.out.println(SpeedTokenizer.segment(text));
		long start = System.currentTimeMillis();
		int pressure = 1000000;
		for (int i = 0; i < pressure; ++i) {
			SpeedTokenizer.segment(text);
		}
		double costTime = (System.currentTimeMillis() - start) / (double) 1000;
		System.out.printf("分词速度：%.2f字每秒", text.length() * pressure / costTime);
	}

	/**
	 * N-最短路径分词 nt:公司名
	 */
	// N-最短分词：[刘喜杰/nr, 石国祥/nr, 会见/v, 吴亚琴/nr, 先进/a, 事迹/n, 报告团/n, 成员/n]
	// 最短路分词：[刘喜杰/nr, 石国祥/nr, 会见/v, 吴亚琴/nr, 先进/a, 事迹/n, 报告团/n, 成员/n]
	// N-最短分词：[大连/ns, 博采/v, 科技/n, 有限公司/n]
	// 最短路分词：[大连/ns, 博采/v, 科技/n, 有限公司/n]
	// N-最短分词：[北京海鑫科技有限公司/nt]
	// 最短路分词：[北京海鑫科技有限公司/nt]
	public static void method01() {
		Segment nShortSegment = new NShortSegment().enableCustomDictionary(false).enablePlaceRecognize(true)
				.enableOrganizationRecognize(true);
		Segment shortestSegment = new ViterbiSegment().enableCustomDictionary(false).enablePlaceRecognize(true)
				.enableOrganizationRecognize(true);
		String[] testCase = new String[] { "刘喜杰石国祥会见吴亚琴先进事迹报告团成员", "大连博采科技有限公司", "北京海鑫科技有限公司", "阿里巴巴集团",
				"辽宁省大连市高新园区火炬路创业大厦A座", "大连海事大学", "北京大学", "大连理工大学", "肯德基", "万达电影院", "锦辉购物广场", "海澜之家", "13027729317",
				"1615312491", "wen_zhi_bin@126.com" };
		String[] s = new String[] {
				"623598477@qq.com"+(char)1+"吴京"};
		for (String sentence : s) {
			System.out.println("N-最短分词：" + nShortSegment.seg(sentence) + "\n最短路分词：" + shortestSegment.seg(sentence));
		}
	}
}

package com.mars;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dao.MarsDao;
import com.dao.MarsPreprocessingDao;
import com.entity.Mars;
import com.entity.Mars_Preprocessing;

public class DataPreprocessing {
	public  static double[][] matrix_X;
	public  static double[][] matrix_Y;
	public  static double[] Test;
	public static double min;
	public static double max;
	public static void DataCompression() {
		MarsDao md = new MarsDao();
		MarsPreprocessingDao mpd = new MarsPreprocessingDao();
		String sql = "select min(star_time) as star_time,max(star_time) as receiving_time from mars";
		List<Mars> list = md.find(sql);
		String sql_preprocessing = "select max(star_time) as star_time from mars_preprocessing";
		List<Mars_Preprocessing> list2 = mpd.find(sql_preprocessing);
		Date min_time;
		if (list2.get(0).getStar_time() == null) {
			min_time = list.get(0).getStar_time();
		} else {
			min_time = new Date(list2.get(0).getStar_time().getTime() + 60000);
		}
		Date max_time = list.get(0).getReceiving_time();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time = min_time;
		while (time.before(max_time)) {
			String sql1 = "select to_date('"
					+ sdf.format(time)
					+ "','yyyy-mm-dd hh24:mi:ss') as star_time,avg(vkz019) as vkz019,avg(vkz021) as vkz021,avg(vkz022) as vkz022,avg(vkz026) as vkz026"
					+ " from mars " + " where star_time>=to_date('"
					+ sdf.format(time)
					+ "','yyyy-mm-dd hh24:mi:ss') and star_time<to_date('"
					+ sdf.format(new Date(time.getTime() + 60000))
					+ "','yyyy-mm-dd hh24:mi:ss')";
			System.out.println(sql1);
			List<Mars> list1 = md.find(sql1);
			if (list1.get(0).getVkz019() != 0) {
				Mars m = list1.get(0);
				int v = mpd.insert(m);
			}
			list1.clear();
			time = new Date(time.getTime() + 60000);
		}
	}

	public static void DataConversion(String attribute, int step,Date time) {
		MarsPreprocessingDao mpd = new MarsPreprocessingDao();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = "select "+attribute+" from mars_preprocessing "+
					 " where star_time<=to_date('"+sdf.format(time)+"','yyyy-mm-dd hh24:mi:ss')"
					 +" and star_time>=to_date('"+sdf.format(new Date(time.getTime()-60*60000))+"','yyyy-mm-dd hh24:mi:ss')"
					 +" order by star_time asc";
		System.out.println(sql);
		List<Mars_Preprocessing> list = mpd.find(sql);
		String min_max="select max("+attribute+") as vkz019,min("+attribute+") as vkz021 from (select * from mars_preprocessing "+
					 " where star_time<=to_date('"+sdf.format(time)+"','yyyy-mm-dd hh24:mi:ss')"
					 +" and star_time>=to_date('"+sdf.format(new Date(time.getTime()-60*60000))+"','yyyy-mm-dd hh24:mi:ss')"
					 +" order by star_time asc)";
		List<Mars_Preprocessing> list_min_max = mpd.find(min_max);
		max = list_min_max.get(0).getVkz019();
		min = list_min_max.get(0).getVkz021();
		double[] error =new double[10];
		for (int m = 1; m <= 10; m++) {// 构造转换矩阵
			int k = 0, l = 0, rownum = 0;
			System.out.println(list.size());
			matrix_X = new double[list.size() - step - m+1][m];
			matrix_Y = new double[list.size() - step - m+1][1];
			for (int i = 0; i < list.size() - step - m + 1; i++) {
				for (int j = 0; j < m; j++) {
					if (attribute == "vkz019") {
						//matrix_X[i][j] = ((list.get(k).getVkz019()-min)/(max-min));
						matrix_X[i][j] = list.get(k).getVkz019();
					} else if (attribute == "vkz021") {
						//matrix_X[i][j] = ((list.get(k).getVkz021()-min)/(max-min));
						matrix_X[i][j] = list.get(k).getVkz021();
					} else if (attribute == "vkz022") {
						//matrix_X[i][j] = ((list.get(k).getVkz022()-min)/(max-min));
						matrix_X[i][j] = list.get(k).getVkz022();
					} else if (attribute == "vkz026") {
						//matrix_X[i][j] = ((list.get(k).getVkz026()-min)/(max-min));
						matrix_X[i][j] = list.get(k).getVkz026();
					}
					k++;
				}
				if (attribute == "vkz019") {
					//matrix_Y[i][0] = ((list.get(k+21).getVkz019()-min)/(max-min));
					matrix_Y[i][0] = list.get(k+step-1).getVkz019();
				} else if (attribute == "vkz021") {
					//matrix_Y[i][0] = ((list.get(k+21).getVkz021()-min)/(max-min));
					matrix_Y[i][0] = list.get(k+step-1).getVkz021();
				} else if (attribute == "vkz022") {
					//matrix_Y[i][0] = ((list.get(k+21).getVkz022()-min)/(max-min));
					matrix_Y[i][0] = list.get(k+step-1).getVkz022();
				} else if (attribute == "vkz026") {
					//matrix_Y[i][0] = ((list.get(k+21).getVkz026()-min)/(max-min));
					matrix_Y[i][0] = list.get(k+step-1).getVkz026();
				}
				l++;
				k = l;
				rownum++;
			}//for i
			error[m-1]=KELM.getError(matrix_X, matrix_Y);
		}//for m
		int sign=0;
		double min_error=error[0];
		for(int i=1;i<error.length;i++){//获得误差最小的m
			if(error[i]<min_error){
				min_error=error[i];
				sign=i;
			}
		}
		int m=sign+1;
		int k = 0, l = 0, rownum = 0;
		matrix_X = new double[list.size() - step - m+1][m];
		matrix_Y = new double[list.size() - step - m+1][1];
		for (int i = 0; i < list.size() - step - m + 1; i++) {//重新构造转换矩阵
			for (int j = 0; j < m; j++) {
				if (attribute == "vkz019") {
					//matrix_X[i][j] = ((list.get(k).getVkz019()-min)/(max-min));
					matrix_X[i][j] = list.get(k).getVkz019();
				} else if (attribute == "vkz021") {
					//matrix_X[i][j] = ((list.get(k).getVkz021()-min)/(max-min));
					matrix_X[i][j] = list.get(k).getVkz021();
				} else if (attribute == "vkz022") {
					//matrix_X[i][j] = ((list.get(k).getVkz022()-min)/(max-min));
					matrix_X[i][j] = list.get(k).getVkz022();
				} else if (attribute == "vkz026") {
					//matrix_X[i][j] = ((list.get(k).getVkz026()-min)/(max-min));
					matrix_X[i][j] = list.get(k).getVkz026();
				}
				k++;
			}
			if (attribute == "vkz019") {
				//matrix_Y[i][0] = ((list.get(k+21).getVkz019()-min)/(max-min));
				matrix_Y[i][0] = list.get(k+step-1).getVkz019();
			} else if (attribute == "vkz021") {
				//matrix_Y[i][0] = ((list.get(k+21).getVkz021()-min)/(max-min));
				matrix_Y[i][0] = list.get(k+step-1).getVkz021();
			} else if (attribute == "vkz022") {
				//matrix_Y[i][0] = ((list.get(k+21).getVkz022()-min)/(max-min));
				matrix_Y[i][0] = list.get(k+step-1).getVkz022();
			} else if (attribute == "vkz026") {
				//matrix_Y[i][0] = ((list.get(k+21).getVkz026()-min)/(max-min));
				matrix_Y[i][0] = list.get(k+step-1).getVkz026();
			}
			l++;
			k = l;
			rownum++;
		}//for i
		Test=new double[m];
		for(int i=0;i<m;i++){
			Test[i]=matrix_Y[matrix_Y.length-m+i][0];
		}
	}
}

package com.mars;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dao.MarsDao;
import com.dao.MarsPreprocessingDao;
import com.dao.PredictionDao;
import com.entity.Mars;
import com.entity.Mars_Preprocessing;
import com.entity.Prediction;

import Jama.Matrix;

public class main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//DataPreprocessing.DataCompression();
		while(true){
			Date d = new Date();
			if(d.getSeconds()==0){
				String[] attribute = {"vkz019","vkz021","vkz026"};
				int step = 22;
				Date now=new Date();
				Date time = new Date(now.getTime()-step*60000);
				//predict(attribute[0],step,time);
				for(int i=0;i<attribute.length;i++){
					predict(attribute[i],step,time);
				}
			}
		}
	}
	public static void predict(String attribute,int step,Date time) {
		//DataPreprocessing.DataCompression();
		//String attribute = "vkz019";
		//int step = 22;
		DataPreprocessing.DataConversion(attribute, step,time);
		Matrix X=new Matrix(DataPreprocessing.matrix_X);
		Matrix Y=new Matrix(DataPreprocessing.matrix_Y);
		Matrix Test=new Matrix(DataPreprocessing.Test,DataPreprocessing.Test.length);
		double[] paras=DE.DE_Kernel();
		//double[] paras={10,100};
		KELM.ELM_Kernel(paras[0],paras[1], X, Y, Test);
		MarsPreprocessingDao mpd = new MarsPreprocessingDao();
		//String min_max="select max("+attribute+") as vkz019,min("+attribute+") as vkz021 from (select * from mars_preprocessing order by star_time desc) where rownum<=120";
		//List<Mars_Preprocessing> list_min_max = mpd.find(min_max);
		double max = DataPreprocessing.max;
		double min = DataPreprocessing.min;
		double[] alf_bta=DE.DE_PISI();
		double predictedValue=Test.get(0, 0);
		double upper =predictedValue*(1+alf_bta[0]);
		double lower =predictedValue*(1-alf_bta[1]);
		System.out.println("predictedValue="+predictedValue);
		System.out.println("upper="+upper);
		System.out.println("lower="+lower);
		Prediction p =new Prediction();
		p.setAttribute(attribute);
		p.setTime(new Date(time.getTime()+22*60000));
		p.setPredictedvalue(predictedValue);
		p.setUpper(upper);
		p.setLower(lower);
		PredictionDao pd = new PredictionDao();
		int v = pd.insert(p);
		System.out.println("Ìí¼Ó"+v+"Ìõ¼ÇÂ¼");
	}
}

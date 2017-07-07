package com.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.entity.Prediction;


public class PredictionDao extends BaseDao{
	/**
	 * 添加数据
	 */
	public int insert(Prediction p){
		//String sql="insert into Mars_Preprocessing(star_time,vkz019,vkz021,vkz022,vkz026) values(to_date('?','yyyy-mm-dd hh24:mi:ss'),?,?,?,?)";
		List params=new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		params.add(sdf.format(m.getStar_time()));
//		params.add(m.getVkz019());
//		params.add(m.getVkz021());
//		params.add(m.getVkz022());
//		params.add(m.getVkz026());
		String sql="insert into Prediction(attribute,time,predictedvalue,upper,lower) values('"+p.getAttribute()+"',to_date('"+sdf.format(p.getTime())+"','yyyy-mm-dd hh24:mi:ss'),"+p.getPredictedvalue()+","+p.getUpper()+","+p.getLower()+")";
		System.out.println(sql);
		int i=super.executeUpdate(sql, params);
		return i;
	}
	/**
	 * 查询数据
	 */
	public List find(String sql) {
		List<Prediction> list = super.executeQuery(sql, null,Prediction.class);
		return list;
	}
	
}

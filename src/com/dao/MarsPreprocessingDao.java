package com.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.entity.Mars;
import com.entity.Mars_Preprocessing;


public class MarsPreprocessingDao extends BaseDao{
	/**
	 * 添加数据
	 */
	public int insert(Mars m){
		//String sql="insert into Mars_Preprocessing(star_time,vkz019,vkz021,vkz022,vkz026) values(to_date('?','yyyy-mm-dd hh24:mi:ss'),?,?,?,?)";
		List params=new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		params.add(sdf.format(m.getStar_time()));
//		params.add(m.getVkz019());
//		params.add(m.getVkz021());
//		params.add(m.getVkz022());
//		params.add(m.getVkz026());
		String sql="insert into Mars_Preprocessing(star_time,vkz019,vkz021,vkz022,vkz026) values(to_date('"+sdf.format(m.getStar_time())+"','yyyy-mm-dd hh24:mi:ss'),"+m.getVkz019()+","+m.getVkz021()+","+m.getVkz022()+","+m.getVkz026()+")";
		System.out.println(sql);
		int i=super.executeUpdate(sql, params);
		return i;
	}
	/**
	 * 查询数据
	 */
	public List find(String sql) {
		List<Mars_Preprocessing> list = super.executeQuery(sql, null,Mars_Preprocessing.class);
		return list;
	}
}

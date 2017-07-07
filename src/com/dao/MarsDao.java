package com.dao;

import java.util.List;

import com.entity.Mars;


public class MarsDao extends BaseDao{
	public List find(String sql) {
		List<Mars> list = super.executeQuery(sql, null,Mars.class);
		return list;
	}
}

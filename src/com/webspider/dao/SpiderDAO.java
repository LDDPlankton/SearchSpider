package com.webspider.dao;

public class SpiderDAO extends DAOBase
{
	public SpiderDAO()
	{
		
	}
	
	/*
	public Map<String,Object> findAndModify()// throws NullPointerException
	{
		BasicDBObject query = new BasicDBObject();
		BasicDBObject update = new BasicDBObject();//"$set", new BasicDBObject("remove", true));

		//QUERY, FIELDS, SORT, REMOVE, UPDATE, RETURN NEW, UPSERT
		DBObject obj = this.conn.getCollection().findAndModify(query, null, null, true, update, false, false);
		Map<String,Object> tmp_map = obj.toMap();
		return tmp_map;
	}
	*/
}

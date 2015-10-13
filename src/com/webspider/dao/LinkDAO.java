package com.webspider.dao;

import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

public class LinkDAO extends DAOBase
{
	public LinkDAO()
	{
		
	}
	
	/**
	 * Returns a DBCursor containing a list of rows from the MongoDB.
	 * Example Usage: 	Map<String, Object> query = new HashMap<String, Object>();
	 *					query.put("site_status", "Online");
	 *					DBCursor cur = class.findBy(query);
	 *
	 * @param	params	A Map containing String, Object value K/V pairs.
	 * @return 			The DBCursor referencing a list of DB rows.
	 */
	public DBCursor findBy(Map<String, Object> params)
	{
		BasicDBObject query = new BasicDBObject();
		for(Map.Entry<String, Object> o : params.entrySet() )
		{
			String key = o.getKey();
			Object val = o.getValue();
			query.put(key, java.util.regex.Pattern.compile(val.toString()) );
		}
		DBCursor res = this.conn.find(query);
		return res;
	}
	
}

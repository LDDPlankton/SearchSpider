package com.webspider.dao;

import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.webspider.backend.MongoDBConnector;

public class DAOBase
{
	protected MongoDBConnector conn;
	protected ObjectId lastinsert_id;
	
	public DAOBase()
	{
		this.conn = new MongoDBConnector();//MongoDBConnector.getInstance();
	}
	
	/*
	 * This function should set a collection.
	 * 
	 * @param	collection_name		The new collection name to use.
	 */
	public void setCollection(String collection_name)
	{
		this.conn.setCollection( collection_name );
	}
	
	/*
	 * This function will set a last insert ID to a particular value.
	 * 
	 * @param	last_id		A new ObjectId that has the last id value.
	 */
	public void setLastInsertID(ObjectId last_id)
	{
		this.lastinsert_id = last_id;
	}
	
	/*
	 * This function returns the last insert id.
	 * 
	 * @return String
	 */
	public String getLastInsertID()
	{
		return this.lastinsert_id.toString();
	}
	
	/**
	 * Returns a DBOBject containing a row from the MongoDB. Requires an existing '_id' key.
	 * Example Usage: DBObject query = class.findByID("..."); 
	 *
	 * @param	id	A string value containing the ID field for a particular mongoDB row.
	 * @return 		The DBObject referencing a particular row in the NoSQL DB.
	 */
	public DBObject findByID(String id)
	{
		DBObject res = null;
		
		try
		{
			BasicDBObject query = new BasicDBObject("_id", new ObjectId(id) );
			res = this.conn.findOne(query);
		}
		catch(IllegalArgumentException e)
		{
			res = null;
		}
		
		return res;
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
			query.put(key, val);
		}

		DBCursor res = this.conn.find(query);
		return res;
	}
	public DBCursor findBy(Map<String, Object> params, int limit)
	{
		BasicDBObject query = new BasicDBObject();
		for(Map.Entry<String, Object> o : params.entrySet() )
		{
			String key = o.getKey();
			Object val = o.getValue();
			query.put(key, val);
		}

		DBCursor res = this.conn.find(query).limit(limit);
		return res;
	}
	
	/**
	 * Updates row(s) by using a ID reference.
	 * Example Usage:
	 * 		BasicDBObject changes = new BasicDBObject();
	 *		changes.put("site_status", "Offline");
	 *		class.updateByID("...", changes,false, true);
	 *
	 * @param	id	A string value containing the ID field for a particular mongoDB row.
	 * @param	changes	A BasicDBObject containing changes to make to our row(s).
	 * @param	upsert
	 * @param 	multi	Specifies if we update multiple rows or not
	 */	
	public void updateByID(String id, BasicDBObject changes, boolean upsert, boolean multi)
	{
		BasicDBObject obj = null;
		try
		{
			obj = new BasicDBObject("_id", new ObjectId(id) );
			this.conn.update(obj, changes, upsert, multi);
		}
		catch(IllegalArgumentException e)
		{
			
		}

	}
	
	/**
	 * Updates row(s) by using field reference values.
	 * Example Usage:
	 *		BasicDBObject obj = new BasicDBObject();
	 *		obj.put("site_desc", "Best Biz Site");
	 *
	 *		BasicDBObject changes = new BasicDBObject();
	 *		changes.put("site_status", "Offline");
	 *		class.updateBy(obj, changes,false, true);
	 *
	 * @param	obj		A BasicDBObject that contains the key/values to match against for an update
	 * @param	changes	A BasicDBObject containing changes to make to our row(s).
	 * @param	upsert
	 * @param 	multi	Specifies if we update multiple rows or not
	 */	
	public void updateBy(BasicDBObject obj, BasicDBObject changes, boolean upsert, boolean multi)
	{
		this.conn.update(obj, changes, upsert, multi);
	}
	
	public void insert(BasicDBObject document)
	{	
		//INSERT INTO DB
		this.conn.getCollection().insert(document);
		
		//RECORD LAST INSERT ID
		this.setLastInsertID((ObjectId)document.get( "_id" ));
	}
	
	public void delete(BasicDBObject document)
	{
		this.conn.getCollection().remove(document);
	}
	
}

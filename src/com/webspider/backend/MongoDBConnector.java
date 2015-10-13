package com.webspider.backend;

import java.net.UnknownHostException;
import java.util.Set;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDBConnector
{
	public static MongoDBConnector conn = null;
	private MongoClient mongoClient = null;
	private DB db = null;
	private DBCollection collection = null;
	private String database;
	
	public MongoDBConnector()
	{
		//MongoCredential credential = MongoCredential.createMongoCRCredential(userName, database, password);
		//MongoClient mongoClient = new MongoClient(new ServerAddress(), Arrays.asList(credential));
		this.database = "mydb";
		this.connect();
	}
	
	/*
	 * This function will get a static instance so as to not redeclare our database connection.
	 * 
	 * @return	static MongoDBConnector
	 */
	public static MongoDBConnector getInstance()
	{
		if(conn == null)
			conn = new MongoDBConnector();
		return conn;
	}
	
	/*
	 * This function will attempt to connect to our local MongoDB Server.
	 */
	public void connect()
	{
		try
		{
			this.mongoClient = new MongoClient( "localhost" , 27017 );
			this.db = mongoClient.getDB(this.database);
		}
		catch (UnknownHostException e)
		{
			System.out.println("Unable to connect to MongoDB Server!");
			System.exit(1);
		}
	}
	
	/*
	 * This function will set the collection name to use.
	 */
	public void setCollection(String collection_name)
	{
		this.collection = this.db.getCollection( collection_name );
	}
	
	/*
	 * This function will return the collection we are currently using.
	 * 
	 * @return	DBCollection
	 */
	public DBCollection getCollection()
	{
		return this.collection;
	}
	
	/*
	 * This function will list everything in our current collection.
	 */
	public void listCollections()
	{
		Set<String> colls = db.getCollectionNames();
		for (String s : colls)
		{
		    System.out.println("Collection:" + s);
		}	
	}
		
	/*
	 * This function will query a collections for a particular query param.
	 * 
	 * @param	query	A DBOBject with a particular key to find
	 * @return	DBCursor
	 */
	public DBCursor find(BasicDBObject query)
	{
		DBCursor result = this.collection.find(query);
		return result;
	}
	
	/*
	 * This function will query a collections for ONE particular query param.
	 * 
	 * @param	query	A DBOBject with a particular key to find
	 * @return	DBObject
	 */
	public DBObject findOne(BasicDBObject query)
	{
		DBObject result = this.collection.findOne(query);
		return result;
	}
	
	/*
	 * This function will insert a DBObject into our collection.
	 * 
	 * @param	obj_to_create	The DB object to add to our database.
	 */
	public void insert(BasicDBObject obj_to_create)
	{
		this.collection.insert(obj_to_create);
	}
	
	/*
	 * This function will update a collection with new values.
	 * 
	 * @param	obj	The DBObject with current values
	 * @param	changes	The DBObject with new values
	 * @param	upsert	Boolean for whether or not to use upsert
	 * @param	multi	Boolean for whether changes should effect multiple rows
	 */
	public void update(BasicDBObject obj, BasicDBObject changes, boolean upsert, boolean multi)
	{
		//SET CHANGES
	    BasicDBObject updateObj = new BasicDBObject();
	    updateObj.put("$set", changes);
	    
	    this.collection.update(obj, updateObj,false,true);
	}
	
}

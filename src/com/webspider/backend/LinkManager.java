package com.webspider.backend;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.webspider.dao.LinkDAO;
import com.webspider.library.ErrorManager;
import com.webspider.models.SiteLinkModel;

public class LinkManager extends ErrorManager
{
	private LinkDAO link_dao;
	
	public LinkManager()
	{
		this.link_dao = new LinkDAO();
		this.link_dao.setCollection("sites.links");
	}
	
	/*
	 * This function will return the last insert id.
	 * 
	 * @return String
	 */
	public String getLastInsertID()
	{
		return this.link_dao.getLastInsertID();
	}
	
	/*
	 * This function will add a link to our database. The FQDN + Path should be specified.
	 * 
	 * @param	linkURL		The link to be added.
	 */
	public boolean addLink(String linkURL)
	{
		//INIT MODEL
		SiteLinkModel linkModel = new SiteLinkModel();
		linkModel.linkURL = linkURL;
		//linkModel.pageDesc = pageDesc;
		//linkModel.pageTitle = "";
		
		//CREATE NEW MAP WITH KEY + VALUES
		Map<String,Object> myMap = linkModel.toMap();
		
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("linkURL", linkURL);
		
		//CHECK TO ENSURE SITE NOT ALREADY IN DB
		DBCursor res = this.link_dao.findBy(query);
		if( res.count() > 0)
		{
			this.setErrorMessage(String.format("The page [%s] already exists!", linkURL) );
			return false;
		}
		
		//ADD TO DB
		this.link_dao.insert( linkModel.toDBOject(myMap) );

		return true;
	}
	
	/*
	 * This function will remove a particular link from our database. The FQDN + Path should be specified.
	 * 
	 * @param	SiteLinkModel	The sitelinkmodel containing information about the link to be removed.
	 * @return	boolean
	 */
	public boolean removeLink(SiteLinkModel model)
	{
		//REMOVE PAGE LINK
		BasicDBObject document = new BasicDBObject();
		document.put("_id", model.Id);
		this.link_dao.delete(document);
	
		return true;
	}
	
	/*
	 * This function will remove a particular link from our database. The FQDN + Path should be specified.
	 * 
	 * @param	Id	The ObjectId to be removed from the database.
	 * @return	boolean
	 */
	public boolean removeLink(ObjectId Id)
	{
		//REMOVE PAGE LINK
		BasicDBObject document = new BasicDBObject();
		document.put("_id", Id);
		this.link_dao.delete(document);
		
		return true;
	}
	
	/*
	 * This function will determine if a particular page exists. The FQDN + Path should be specified.
	 * 
	 * @param	linkURL		The link we need to check if exists.
	 * @return	boolean
	 */
	public boolean isPageExist(String linkURL)
	{
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("linkURL", linkURL);
		DBCursor cur = this.link_dao.findBy(query);
		
		if( cur.count() > 0)
			return true;
		return false;
	}

	/*
	 * This function will find a page in our database and return the model. The FQDN + Path should be specified.
	 * 
	 * @param	linkURL		The link we need to find the model for.
	 * @return	SiteLinkModel
	 */
	public SiteLinkModel findPage(String linkURL)
	{
		SiteLinkModel model = new SiteLinkModel();
		
		//QUERY SITE
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("linkURL", linkURL);
		DBCursor cur = this.link_dao.findBy(query);
		
		//CHECK IF EXIST
		if(cur.count() == 0)
		{
			this.setErrorMessage( String.format("LinkManager::findPage() Unable to find: [%s]", linkURL) );
			return model;
		}
		
		//GET DB RECORD OBJECT
		DBObject dbobj = cur.next();
		
		//CONVERT DB OBJECT TO A MAP OF STRING,OBJ VALUES
		@SuppressWarnings("unchecked")
		Map<String, Object> myMap = dbobj.toMap();
		
		//CONVERT FROM MAP TO CLASS MODEL
		boolean status = model.fromMapToClass(myMap);
		if(!status)
		{
			this.setErrorMessage("LinkManager::findPage() Unable to complete Map Class Conversion");
			return model;
		}	
		
		return model;
	}


	
}

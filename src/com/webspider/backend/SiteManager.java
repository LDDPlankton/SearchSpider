package com.webspider.backend;

import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.webspider.dao.SiteDAO;
import com.webspider.library.ErrorManager;
import com.webspider.models.SiteModel;

public class SiteManager extends ErrorManager
{
	SiteDAO site_dao = null;
	
	public SiteManager()
	{
		this.site_dao = new SiteDAO();
		this.site_dao.setCollection("sites");
	}
	
	/*
	 * This function will return the last insert id.
	 * 
	 * @return String
	 */
	public String getLastInsertID()
	{
		return this.site_dao.getLastInsertID();
	}
	
	/*
	 * This function will add a new site to our database. The domain should be in the format [msn.com, me.google.com, etc]
	 * 
	 * @param	domainURL		The domain name to be added [msn.com, etc]
	 * @param	httpProtocol	The protocol used [http, https]
	 * @return	boolean
	 */
	public boolean addSite(String domainURL, String httpProtocol)
	{
		//INIT MODEL
		SiteModel siteModel = new SiteModel();
		siteModel.domainURL = domainURL;
		siteModel.httpProtocol = httpProtocol;
		
		//CREATE NEW MAP WITH KEY + VALUES
		Map<String,Object> myMap = siteModel.toMap();
		
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("domainURL", domainURL);
		
		//CHECK TO ENSURE SITE NOT ALREADY IN DB
		DBCursor res = this.site_dao.findBy(query);
		if( res.count() > 0)
		{
			this.setErrorMessage(String.format("The domain [%s] already exists!", domainURL) );
			return false;
		}
		
		//ADD TO DB
		this.site_dao.insert( siteModel.toDBOject(myMap) );

		return true;
	}
	
	/*
	 * This function will update a SiteModel will the new values.
	 * 
	 * @param	model	The model with new changes.
	 * @return	boolean
	 */
	public boolean update(SiteModel model)
	{
		//CREATE NEW MAP WITH KEY + VALUES
		Map<String,Object> myMap = model.toMap();
		
		this.site_dao.updateByID(model.Id.toString(), model.toDBOject(myMap), false, true);
		
		return true;
	}
	
	/*
	 * This function will determine if a particular domain exists. The domain should be in the format [msn.com, me.google.com, etc]
	 * 
	 * @param	domainURL	The domain of a site we want to determine if exists.
	 * @return	boolean
	 */
	public boolean isSiteExist(String domainURL)
	{
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("domainURL", domainURL);
		DBCursor cur = this.site_dao.findBy(query);
		
		if( cur.count() > 0)
			return true;
		return false;
	}
	
	/*
	 * This function will find a site model of a particular domain. The domain should be in the format [msn.com, me.google.com, etc]
	 * 
	 * @param	domainURL	The domain of a site we want to find.
	 */
	public SiteModel findSite(String domainURL)// throws BackendException
	{
		SiteModel model = null;//new SiteModel();
		
		//QUERY SITE
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("domainURL", domainURL);
		DBCursor cur = this.site_dao.findBy(query);

		//CHECK IF EXIST
		if(cur.count() == 0)
		{
			this.setErrorMessage( String.format("SiteManager::findSite() Unable to find: [%s]", domainURL) );
			return model;
		}
		
		//INIT
		model = new SiteModel();
		
		//GET DB RECORD OBJECT
		DBObject dbobj = cur.next();
		
		//CONVERT DB OBJECT TO A MAP OF STRING,OBJ VALUES
		@SuppressWarnings("unchecked")
		Map<String, Object> myMap = dbobj.toMap();
		
		//CONVERT FROM MAP TO CLASS MODEL
		boolean status = model.fromMapToClass(myMap);
		if(!status)
		{
			this.setErrorMessage("SiteManager::findSite() Unable to complete Map Class Conversion");
			return model;
		}	
		
		return model;
	}
	
	/*
	 * This function will determine if a page's URL is linked to a domain.
	 * 
	 * @param	linkId	The page's objectid, used to search sites collection to see if exists.
	 * @return	boolean
	 */
	public boolean isPageLinkedToDomain(ObjectId linkId)
	{
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("linkList", new ObjectId(linkId.toString()) );
		DBCursor cur = this.site_dao.findBy(query);
		if(cur.count() <=0)
			return false;
		else
			return true;
	}
	
	/*
	 * This function should be called after isPageLinkedToDomain(). Will find site model by searching for a particular linkId in linkedList.
	 * 
	 * @param	linkId	The ObjectId of a link that should be i na site's linkedList
	 * @return	SiteModel
	 */
	public SiteModel findSiteModelByLinkedPagefindSiteLink(ObjectId linkId)
	{
		SiteModel model = new SiteModel();
		
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("linkList", new ObjectId(linkId.toString()) );
		DBCursor cur = this.site_dao.findBy(query);
		
		if(cur.count() <=0)
			return null;
		
		//GET DB RECORD OBJECT
		DBObject dbobj = cur.next();
		
		//CONVERT DB OBJECT TO A MAP OF STRING,OBJ VALUES
		@SuppressWarnings("unchecked")
		Map<String, Object> myMap = dbobj.toMap();
		
		//CONVERT FROM MAP TO CLASS MODEL
		boolean status = model.fromMapToClass(myMap);
		if(!status)
		{
			this.setErrorMessage("SiteManager::findSiteModelByLinkedPagefindSiteLink() Unable to complete Map Class Conversion");
			return model;
		}	
		
		return model;
		
	}

}

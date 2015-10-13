package com.webspider.backend;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.webspider.dao.SiteDAO;
import com.webspider.models.SiteModel;

public class DomainSpiderTool
{
	SiteDAO site_dao = null;
	
	public DomainSpiderTool()
	{
		this.site_dao = new SiteDAO();
		this.site_dao.setCollection("sites");
	}
	
	/*
	 * This function will lock a site. This will prevent other spider threads from running into the same domain.
	 * 
	 * @param	domainURL	The domain URL of the site to lock.
	 */
	public void lockSite(String domainURL)
	{
		SiteManager siteManager = new SiteManager();
		SiteModel model = siteManager.findSite(domainURL);
		if(model != null)
		{
			model.lockStatus = 1;	//LOCKED
			siteManager.update(model);
		}
	}
	
	/*
	 * This function will unlock a site.
	 * 
	 * @param	domainURL	The domain URL of the site to unlock.
	 */
	public void unlockSite(String domainURL)
	{
		SiteManager siteManager = new SiteManager();
		SiteModel model = siteManager.findSite(domainURL);
		if(model != null)
		{
			model.lockStatus = 0;	//UNLOCKED
			siteManager.update(model);
		}
	}
	
	/*
	 * This function will return a link to spider.
	 */
	public String findLinkToSpider()
	{
		//GET CALENDER + SUB 2 WEEKS
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -14);
		
		//CONVERT TO A USEFUL DATE
		Date lteDate = new Date();
		Date myDate;
		try
		{
			myDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(calendar.getTime().toString());
			DateFormat mongoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			String xgtDate = mongoDateFormat.format(myDate);
			lteDate = mongoDateFormat.parse(xgtDate);			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, Object> query = new HashMap<String, Object>();
		query.put("lastScanDateTime", new BasicDBObject("$lte", lteDate ) );
		DBCursor cur = this.site_dao.findBy(query, 1);
		
		//IF FOUND NO SITE
		if( cur.count() <= 0)
			return null;
		
		SiteModel model = new SiteModel();
		
		//GET DB RECORD OBJECT
		DBObject dbobj = cur.next();
		
		//CONVERT DB OBJECT TO A MAP OF STRING,OBJ VALUES
		@SuppressWarnings("unchecked")
		Map<String, Object> myMap = dbobj.toMap();
		
		//CONVERT FROM MAP TO CLASS MODEL
		boolean status = model.fromMapToClass(myMap);
		
		return model.domainURL;
	}
	
}

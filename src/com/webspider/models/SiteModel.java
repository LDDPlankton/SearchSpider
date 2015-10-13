package com.webspider.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bson.types.BasicBSONList;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

public class SiteModel
{
	public ObjectId Id;
	public String domainURL;
	public String httpProtocol;
	//public String siteTitle;
	//public String siteDesc;
	public Date creationDateTime;
	public Date lastScanDateTime;
	public BasicBSONList linkList;
	public int lockStatus;
	
	public SiteModel()
	{
		this.Id = null;
		this.domainURL = "";
		this.httpProtocol = "";
		//this.siteTitle = "";
		//this.siteDesc = "";
		this.creationDateTime = new Date();
		try
		{
			this.lastScanDateTime = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse("01/01/1970");
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		this.linkList = new BasicBSONList();
		this.lockStatus = 0;
	}
	
	public Map<String,Object> toMap()
	{
		Map<String,Object> model  = new HashMap<String,Object>();
		
		if(this.Id != null)
			model.put("_id", this.Id);
		model.put("domainURL", this.domainURL);
		model.put("httpProtocol", this.httpProtocol);
		//model.put("siteTitle", this.siteTitle);
		//model.put("siteDesc", this.siteDesc);
		model.put("creationDateTime", this.creationDateTime);
		model.put("lastScanDateTime", this.lastScanDateTime);
		model.put("linkList", this.linkList);
		model.put("lockStatus", this.lockStatus);

		return model;
	}
	
	public boolean fromMapToClass(Map<String,Object> myMap)
	{
		//IF ANY MISSING THROWS NullPointerException
		
		this.Id = new ObjectId(myMap.get("_id").toString());
		this.domainURL = myMap.get("domainURL").toString();
		this.httpProtocol = myMap.get("httpProtocol").toString();
		//this.siteTitle = myMap.get("siteTitle").toString();
		//this.siteDesc = myMap.get("siteDesc").toString();
		try
		{
			//yyyy-MM-dd'T'HH:mm:ssZ
			String formatCreationDateTime = new SimpleDateFormat("MM/dd/yyyy").format(myMap.get("creationDateTime"));
			String formatLastScanDateTime = new SimpleDateFormat("MM/dd/yyyy").format(myMap.get("lastScanDateTime"));

			this.creationDateTime = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(formatCreationDateTime);
			this.lastScanDateTime = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(formatLastScanDateTime);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		this.linkList = (BasicBSONList)myMap.get("linkList");
		this.lockStatus = Integer.valueOf(myMap.get("lockStatus").toString());
		return true;
	}
	
	public BasicDBObject toDBOject(Map<String,Object> myMap)
	{
		BasicDBObject document = new BasicDBObject();
		
		//LOOP THROUGH SITEMAP APPENDING VALUES INTO DBOBJ
		for(Map.Entry<String, Object> tmp_map : myMap.entrySet() )
		{
			String key = tmp_map.getKey();
			Object val = tmp_map.getValue();
			document.append(key, val);			//ADD TO NEW DOCUMENT
		}
		
		return document;
	}

}

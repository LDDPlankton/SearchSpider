package com.webspider.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.bson.types.BasicBSONList;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;

public class SiteLinkModel
{
	public ObjectId Id;
	public String linkURL;
	public String pageDesc;
	public String pageTitle;
	public int incomingLinkCount;
	public BasicBSONList incomingLinksRef;
	public Date creationDateTime;
	public Date lastScanDateTime;
	public int lockStatus;
	
	public SiteLinkModel()
	{
		this.Id = null;
		this.linkURL = "";
		this.pageDesc = "";
		this.pageTitle = "";
		this.incomingLinkCount = 0;
		this.incomingLinksRef = new BasicBSONList();
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
		this.lockStatus = 0;
	}
	
	public Map<String,Object> toMap()
	{
		Map<String,Object> model  = new HashMap<String,Object>();
		
		if(this.Id != null)
			model.put("Id", this.Id);
		model.put("linkURL", this.linkURL);
		model.put("pageDesc", this.pageDesc);
		model.put("pageTitle", this.pageTitle);
		model.put("incomingLinkCount", this.incomingLinkCount);
		model.put("incomingLinksRef", this.incomingLinksRef);
		model.put("creationDateTime", this.creationDateTime);
		model.put("lastScanDateTime", this.lastScanDateTime);
		model.put("lockStatus", this.lockStatus);
		
		return model;
	}
	
	public boolean fromMapToClass(Map<String,Object> myMap)
	{
		//IF ANY MISSING THROWS NullPointerException
		
		this.Id = new ObjectId(myMap.get("_id").toString());
		this.linkURL = myMap.get("linkURL").toString();
		this.pageDesc = myMap.get("pageDesc").toString();
		this.pageTitle = myMap.get("pageTitle").toString();
		this.incomingLinkCount = Integer.valueOf(myMap.get("incomingLinkCount").toString());
		this.incomingLinksRef = (BasicBSONList)myMap.get("incomingLinksRef");
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

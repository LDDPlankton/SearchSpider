package com.webspider.main;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.webspider.backend.LinkFacade;
import com.webspider.backend.LinkManager;
import com.webspider.backend.LinkTool;
import com.webspider.backend.SiteManager;
import com.webspider.backend.DomainSpiderTool;
import com.webspider.dao.LinkDAO;
import com.webspider.models.SiteLinkModel;
import com.webspider.models.SiteModel;

public class DBSeeder
{
	public DBSeeder()
	{
		
	}
	
	public void seed()
	{
		LinkTool linkTool = new LinkTool();
		LinkFacade linkFacade = new LinkFacade();
		
		boolean status;
		String myDomain = "http://businessinsider.com";
		String myLink = "http://businessinsider.com/index.php";
		status = linkFacade.addDomain(myDomain);
		if(!status)
		{
			System.out.println("We were unable to add: " + myDomain);
		}

		status = linkFacade.addLink(myLink);
		if(!status)
		{
			System.out.println("We were unable to add link: " + myLink);
		}
		linkFacade.assignLinkToSite(myDomain,  myLink);
		
		if(linkFacade.isPageExist(myLink))
		{
			SiteLinkModel siteLinkModel = linkFacade.findLinkModel(myLink);
			System.out.println("Page ID = " + siteLinkModel.Id);
			
			//linkFacade.unAssignLink(myLink);
		}

	}
}

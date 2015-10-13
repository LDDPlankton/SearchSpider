package com.webspider.main;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Set;

import com.webspider.backend.DomainSpiderTool;
import com.webspider.backend.LinkFacade;
import com.webspider.backend.LinkTool;
import com.webspider.core.SpiderConnector;
import com.webspider.core.SpiderException;
import com.webspider.core.SpiderHTMLParser;
import com.webspider.core.SpiderLinks;

/*
 * The purpose of this class is to search for domains in our DB that have not been recently spidered.
 * 
 * The goal is to:
 * 1. Spider old sites.
 * 2. Find new pages.
 * 3. Insert that information into our DB.
 */

public class DomainSpider implements Runnable
{
	private SharedResource myshared;
	private SpiderConnector spider_conn;
	private SpiderHTMLParser html_parse;
	private SpiderLinks spider_links;
	private LinkTool linkTool;
	private LinkFacade linkFacade;
	
	public DomainSpider(SharedResource mysx)
	{
		this.myshared = mysx;
		this.spider_conn = new SpiderConnector();
		this.html_parse = new SpiderHTMLParser();
		this.spider_links = new SpiderLinks();
		this.linkTool = new LinkTool();
		this.linkFacade = new LinkFacade();
	}
		
	public void spiderDomain(String domainURL)
	{

		try
		{
			System.out.println("SPIDERING URL!");
			
			//OPEN CONNECTION TO URL + FETCH DATA
			spider_conn.setURL(domainURL);										//SET URL TO SPIDER
			spider_conn.fetchURLData();											//READ URL TO BUFFER AFTER OPENNING CONNECTION
			
			//PARSE HTML
			this.html_parse.jsoupParse( spider_conn.getURLData() );					//PARSE HTML

			//HANDLE LINKS			
			this.spider_links.setSpiderHTMLParser(this.html_parse);					//SET HTML PARSER
			this.spider_links.setDomainURL(domainURL);								//SET DOMAIN URL
			this.spider_links.processAllWebLinks();									//GETS ALL LINKS ON PAGE
			this.spider_links.processLinksByDomainAssociation();					//GETS ALL SITE RELATED LINKS
			this.spider_links.removeDomainAssociationLinksFromAllLinks();			//REMOVES ALL SITE LINKS FROM ALL LINKS LIST

			//GET SITE + ALL LINKS
			Set<String> all_links = this.spider_links.getAllLinks();
			Set<String> site_links = this.spider_links.getSiteLinks();
			
			//FIND REAL DOMAIN URL + ADD DOMAIN [IF NOT EXIST}
			this.linkFacade.addDomain(domainURL);
			/*
			//LOOP THROUGH SITE LINKS + ASSIGN
			for(String link : site_links)
			{
				//String mydomainURL = this.linkTool.findDomainName(link);
				String mylinkURL = link;
				
				System.out.println("domainURL=" + this.linkTool.findDomainName(link) + " linkURL="+link );
				
				//ADD PAGE
				this.linkFacade.addLink(mylinkURL);									//ADD LINK TO DB
				boolean status = this.linkFacade.assignLinkToSite(link, mylinkURL);			//ASSIGN LINK TO THIS DOMAIN
			}
			*/
			//LOOP THROUGH ALL LINKS + ASSIGN [
			for(String link : all_links)
			{
				//String mydomainURL = this.linkTool.findDomainName(link);
				//String mylinkURL = link;
				
				//ADD DOMAIN
				boolean status = this.linkFacade.addDomain(link);
				if(!status)
					continue;
				System.out.println("Adding Domain: " + link);
				
				//ADD PAGE
				this.linkFacade.addLink(link);
				status = this.linkFacade.assignLinkToSite(link, link);
				if(!status)
				{
					if(this.linkFacade.removeLink(link))
						System.out.println("Link removed!");
				}
			}
			

		
		}
		catch (SocketTimeoutException e1)
		{
			System.out.println("Error: Site Down:" + this.spider_conn.getURL());
			System.exit(1);			
		}
		catch (IOException e2)
		{
			System.out.println("Error: We could not spider:" + this.spider_conn.getURL());
			System.exit(1);
		}
		catch (SpiderException e3)
		{
			System.out.println("Error: URL: " + this.spider_conn.getURL() + " Message:" + e3.getMessage() );
			System.exit(1);
		}
	}
		
	public void run()
	{
		//this.spiderDomain("http://msn.com/");
		
		DomainSpiderTool domainSpider = new DomainSpiderTool();
		String result = domainSpider.findLinkToSpider();
		if(result == null)
		{
			System.out.println("Unable to find URL!");
		}
		else
		{
			System.out.println("Spidering:" + result);
		}
		
	}
}

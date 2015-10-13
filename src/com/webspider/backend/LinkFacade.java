package com.webspider.backend;

import com.webspider.models.SiteLinkModel;
import com.webspider.models.SiteModel;

public class LinkFacade
{
	private SiteManager siteManager;
	private LinkManager linkManager;
	private LinkTool linkTool;
	
	public LinkFacade()
	{
		this.siteManager = new SiteManager();
		this.linkManager = new LinkManager();
		this.linkTool = new LinkTool();
	}
	
	/*
	 * This function will take a FQDN (protocol + domain + path) and add it to our sites list
	 * 
	 * @param	domainURL	The domain to add to our system
	 * @return	boolean
	 */
	public boolean addDomain(String domainURL)
	{
		boolean status = true;
		
		//DETERMINE IF VALID LINK
		if(!this.linkTool.isValidLink(domainURL))
			return false;

		//GET PROTOCOL [http, https]
		String protocol = this.linkTool.getProtocol(domainURL);
		
		//FIND BASE DOMAIN
		domainURL = this.linkTool.findBaseDomainName(domainURL);

		//IF NOT ABLE TO FIND BASE DOMAIN RETURN FALSE
		if(domainURL == null)
			return false;
		
		//IF SITE NOT EXIST ADD IT
		if(!this.siteManager.isSiteExist(domainURL))
		{
			status = siteManager.addSite(domainURL, protocol);
			if(!status)
				System.out.println(siteManager.getErrorMessage());
			else
				System.out.println("Added Domain: " + domainURL);
		}
		
		return status;
	}
	
	/*
	 * This function will take a FQDN + path and add it to our database.
	 * 
	 * @param	linkURK	The URL to add to our database
	 * @return	boolean
	 */
	public boolean addLink(String linkURL)
	{
		boolean status = true;
		
		//DETERMINE IF VALID LINK
		if(!this.linkTool.isValidLink(linkURL))
			return false;
		
		//IF PAGE NOT EXIST ADD IT
		if(!this.linkManager.isPageExist( this.linkTool.findBaseLink(linkURL) ))
		{
			status = linkManager.addLink(linkURL);
			if(!status)
				System.out.println(linkManager.getErrorMessage());
			else
				System.out.println("Added Link: " + linkURL);
		}
		
		return status;
	}
	
	/*
	 * This function will take a FQDN + path link and delete it.
	 * 
	 * @param	linkURL	The link to remove from our database
	 * @return	boolean
	 */
	public boolean removeLink(String linkURL)
	{
		this.siteManager.reset();											//RESET ERROR STATUS
		
		SiteLinkModel linkModel = this.findLinkModel(linkURL);
		if(this.linkManager.isError())
		{
			System.out.println( String.format("Unable to find link [%s] -> [%s]", linkURL, this.linkManager.getErrorMessage() ));
			return false;
		}

		//FIND SITE MODEL
		if(this.siteManager.isPageLinkedToDomain(linkModel.Id))
		{
			SiteModel siteModel = this.siteManager.findSiteModelByLinkedPagefindSiteLink(linkModel.Id);
			if(!this.siteManager.isError())
			{
				//REMOVE LINK
				if(siteModel.linkList.contains(linkModel.Id))
				{
					siteModel.linkList.remove(linkModel.Id);
					this.siteManager.update(siteModel);
				}
			}
		}
		
		this.linkManager.removeLink(linkModel);
		return true;
	}
	
	/*
	 * This function will determine if a particular page exists. Requires a FQDN.
	 * 
	 * @param	linkURL	The link to check
	 * @return	boolean
	 */
	public boolean isPageExist(String linkURL)
	{
		//DETERMINE IF VALID LINK
		if(!this.linkTool.isValidLink(linkURL))
			return false;
		
		//IF PAGE NOT EXIST
		if(!this.linkManager.isPageExist( this.linkTool.findBaseLink(linkURL) ))
			return false;
		
		return true;
	}
	
	/*
	 * This function will assign a linkURL to a domainURL [requires FQDN's]
	 * 
	 * @param	domainURL	The domain to assign the link to
	 * @param	linkURL		The link to be assigned to the domain
	 * @param	boolean
	 */
	public boolean assignLinkToSite(String domainURL, String linkURL)
	{
		boolean status = true;
		this.siteManager.reset();											//RESET ERROR STATUS
		SiteModel siteModel = this.findSiteModel(domainURL);
		SiteLinkModel linkModel = this.findLinkModel(linkURL);
		
		//DETERMINE IF SITE + LINK MODELS FOUND
		if(this.siteManager.isError())
		{
			System.out.println( String.format("Unable to assign link [%s] to site [%s] (1): [%s]", linkURL, domainURL, this.siteManager.getErrorMessage() ));
			return false;
		}
		if(this.linkManager.isError())
		{
			System.out.println( String.format("Unable to assign link [%s] to site [%s] (2): [%s]", linkURL, domainURL, this.linkManager.getErrorMessage() ));
			return false;
		}
		
		//DETERMINE IF LINK EXISTS IN THE SITES LINK LIST
		if(!siteModel.linkList.contains(linkModel.Id))
		{
			siteModel.linkList.add(linkModel.Id);
			this.siteManager.update(siteModel);
		}

		return status;
	}
	
	/*
	 * This function will query a link URL, and determine if it's assigned to a domain, then unassign it.
	 * 
	 * @param	linkURL	This is the fully qualified domain name + path of the link to check
	 * @return	boolean
	 */
	public boolean unAssignLink(String linkURL)
	{
		boolean status = false;
		
		//FIND LINK MODEL
		SiteLinkModel linkModel = this.findLinkModel(linkURL);
		if(this.linkManager.isError())
		{
			System.out.println(this.linkManager.getErrorMessage());
			return false;
		}
		
		//FIND SITE MODEL
		if(!this.siteManager.isPageLinkedToDomain(linkModel.Id))
		{
			System.out.println("This link is not linked to a domain!");
			return false;
		}
		SiteModel siteModel = this.siteManager.findSiteModelByLinkedPagefindSiteLink(linkModel.Id);
		if(this.siteManager.isError())
		{
			System.out.println(this.siteManager.getErrorMessage());
			return false;
		}
		
		//REMOVE LINK
		if(siteModel.linkList.contains(linkModel.Id))
		{
			siteModel.linkList.remove(linkModel.Id);
			this.siteManager.update(siteModel);
			status = true;
		}
		
		return status;
	}
	
	public SiteModel findSiteModel(String domainURL)
	{
		//System.out.println("Search ["+domainURL + " == ["+ this.linkTool.findBaseDomainName(domainURL) +"]");
		SiteModel siteModel = this.siteManager.findSite(this.linkTool.findBaseDomainName(domainURL));
		return siteModel;
	}
	
	public SiteLinkModel findLinkModel(String linkURL)
	{
		SiteLinkModel linkPage = this.linkManager.findPage(this.linkTool.findBaseLink(linkURL));
		return linkPage;
	}
}

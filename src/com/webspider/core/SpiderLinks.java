package com.webspider.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import com.webspider.backend.LinkTool;

public class SpiderLinks
{
	private URLManipulation url_manip;
	private SpiderHTMLParser html_parse;
	private Set<String> all_links;
	private Set<String> site_links;
	private Set<String> seen_links;
	private LinkTool linkTool;
	private String domainURL;
	
	public SpiderLinks()
	{
		this.url_manip = new URLManipulation();
		this.html_parse = null;
		this.all_links = new TreeSet<String>();
		this.site_links = new TreeSet<String>();
		this.seen_links = new TreeSet<String>();
		this.linkTool = new LinkTool();
		this.domainURL = "";
	}
	
	/*
	 * This function will set our HTML parser, so we can use in this class.
	 * 
	 * @param	html	The SpiderHTMLParser to assign
	 */
	public void setSpiderHTMLParser(SpiderHTMLParser html)
	{
		this.html_parse = html;
	}
	
	/*
	 * This function will set our domain URL that we want to spider.
	 * 
	 * @param	URL		The URL to Spider.
	 */
	public void setDomainURL(String URL)
	{
		this.domainURL = URL;
	}
	
	/*
	 * This function will get all links gathered from the page.
	 * 
	 * @return Set<String>
	 */
	public Set<String> getAllLinks()
	{
		return this.all_links;
	}
	
	/*
	 * This function will get all site links, specific to the domain we are scanning.
	 * 
	 * @return Set<String>
	 */
	public Set<String> getSiteLinks()
	{
		return this.site_links;
	}
	
	/*
	 * This function will attempt to determine if we have encountered the link before.
	 * 
	 * @param	url		The Url we need to determine if we have seen.
	 * @param	boolean
	 */
	public boolean isLinkSeen(String url)
	{
		try
		{
		if( this.all_links.contains(url) )
			return true;
		else if( this.site_links.contains(url) )
			return true;
		}
		catch(Exception e)
		{
			System.out.println("SL ILS EXCEPTION!" + e.getMessage());
		}
		return false;
	}
	
	/*
	 * This function will add our link that has been seen to it's own Set<>.
	 * 
	 * @param	url		The URL we have already seen before.
	 */
	public void addSeenLink(String url)
	{
		if( !this.seen_links.contains(url) )
			this.seen_links.add(url);
	}
	
	/*
	 * This function takes our HTML links and scans them to determine all_sites and site_links.
	 * 
	 */
	public void processAllWebLinks() throws SpiderException
	{
		Set<String> real_links = new TreeSet<String>();
		List<String> ignore_tags = new ArrayList<String>();
		List<String> ignore_files = new ArrayList<String>();

		//IGNORE TAG LIST
		ignore_tags.add("#");
		ignore_tags.add("mailto");
		ignore_tags.add("javascript");

		//IGNORE FILE LIST
		ignore_files.add("jpg");
		ignore_files.add("png");
		ignore_files.add("jpeg");
		ignore_files.add("bmp");

		//GET HTML LINKS
		String links[] = this.html_parse.getHTMLLinks();

		//LOOP THROUGH LINKS
		for(String link : links)
		{
			boolean ignore_link = false;
			String tmp_link = link;

			//FOR SOME REASON JSOUP ALLOWS FOR LINKS TO BE BLANK ... SKIP THESE
			if(tmp_link.equals(""))
				continue;

			//URLS HAVE BEEN REPLACED BY '//' DUE TO MODERNIZATION ... FIX THIS
			//if(tmp_link.indexOf("//") == 0)
			//	tmp_link = tmp_link.replace("//", "http://");
			
			//IF URLS HAVE WWW. STRIP THIS
			//if(tmp_link.indexOf("www.") >= 0)
			//	tmp_link = tmp_link.replace("www.", "");
			
			//CHECK EACH LINK TO SEE IF IT MATCHES ANY IGNORE TAGS
			for(String itag : ignore_tags)
			{
				if(tmp_link.equals(itag))
					ignore_link = true;
				if(tmp_link.indexOf(itag) == 0)
					ignore_link = true;
			}
			//CHECK EACH LINK TO SEE IF IT CONTAINS ANY ENDING FILES WE HAVE CHOSEN TO SKIP
			for(String ifile : ignore_files)
			{
				String split[] = tmp_link.split("\\.");
				String ifile_end = split[split.length-1];	//PNG, GIF, ETC
				
				if(ifile_end.equals(ifile))
				{
					ignore_link = true;
				}
			}

			//IF A VALID LINK TO ADD
			if(!ignore_link)
				real_links.add(tmp_link);		//ADD LINK
		}
		
		//HANDLE SORTING REAL HTTP LINKS FOR FIXING/VERIFICATION TO ENSURE THEY ARE FQDN LISTS
		Queue<String> queue = new LinkedList<String>();
		Iterator<String> iterator = real_links.iterator();
		while(iterator.hasNext())
		{
			String old_link = iterator.next();
			try
			{
				//IF NOT A VALID LINK SINCE RELATIVE
				if(!this.html_parse.isRelativeLink(old_link))
				{
					//DELETE SINCE NOT FQDN
					iterator.remove();

					//FIX LINK
					if(old_link.indexOf("/") == 0)
					{
						old_link = this.url_manip.findBaseURL( this.domainURL ) + old_link;
					}
					else
					{
						old_link = this.url_manip.findBaseURL( this.domainURL ) + "/" + old_link;
					}
					//ADD TO QUEUE TO RE-ADD
					queue.add(old_link);
				}
			}
			catch (Exception e)
			{
				//System.out.println("EXCEPTION SL DELETE=" + e.getMessage() + " OLD_LINK=["+old_link+"]");
				iterator.remove();
			}
		}

		//LOOP THROUGH LINK QUEUE ... ADD TO REAL LINKS
		while(!queue.isEmpty())
		{
			String element = queue.remove();
			real_links.add(element);
		}

		//NOW APPEND
		for(String link : real_links)
		{
			if( !this.isLinkSeen(link) )
			{
				this.all_links.add(link);
			}
		}
		
		System.out.println("ALL LINKS COUNT=" + this.all_links.size() );
	}
	
	/*
	 * This function will sort our list of [all_links], finding which ones are related to the domain we are spidering.
	 */
	public void processLinksByDomainAssociation()
	{
		Set<String> real_links = new TreeSet<String>();
		String base_domain = this.linkTool.findDomainName(this.domainURL);
		//String base_domain = this.url_manip.findBaseDomain(this.spider_conn.getURL());

		//LOOP THROUGH ALL LINKS
		for(String link : this.all_links)
		{
			//System.out.println("MAIN LINK=" + link);
			String tmp =  this.linkTool.findDomainName(link);	//FIND BASE DOMAIN OF LINK

			//IF BASE DOMAIN == BASE DOMAIN OF LINK
			if(base_domain.indexOf(tmp) == 0)
			{
				//System.out.println(link);
				real_links.add(link);
			}
		}
		this.site_links = real_links;
		System.out.println("REAL LINKS COUNT=" + this.site_links.size() );
	}
	
	/*
	 * This function will compare our [all_sites] list to [site_links], removing the site links from the all sites list.
	 */
	public void removeDomainAssociationLinksFromAllLinks()
	{
		for(String link : this.site_links)
		{
			//IF THE LINK EXISTS IN LIST OF SITE LINKS REMOVE FROM ALL LINKS LIST
			this.all_links.remove(link);
		}
		
		//for(String link : this.all_links)
		//	System.out.println("LINK="+link + "|| BL=" + this.linkTool.findBaseLink(link) + "|| BD="+this.linkTool.findBaseDomainName(link) );
	}
}

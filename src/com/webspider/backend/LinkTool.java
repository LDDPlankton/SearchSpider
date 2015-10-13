package com.webspider.backend;

import java.util.ArrayList;
import java.util.List;

public class LinkTool
{
	private List<String> topLevelDomains;
	
	public LinkTool()
	{
        this.topLevelDomains = new ArrayList<String>();
        this.topLevelDomains.add("com");
        this.topLevelDomains.add("net");
        this.topLevelDomains.add("org");
        this.topLevelDomains.add("co.uk");
        
	}
	
	/*
	 * This function is designed to remove protocol information from a link [ftp://, http://, etc]
	 * 
	 * @param	link	A link to remove the protocol from.
	 * @return	The new link with the protocol removed.
	 */
	public String removeProtocol(String link)
	{
		String newLink = link;
		if(newLink.contains("://"))
		{
			//System.out.println("HAS PROTOCOL!");
			newLink = newLink.substring( newLink.indexOf("://")+3, newLink.length() );
		}
		return newLink;
	}
	
	/*
	 * This function will determine the protocol of a link [http, https, etc]
	 * 
	 * @param	link	The link to scan for the protocol
	 * @return	The protocol used by the link.
	 */
	public String getProtocol(String link)
	{
		boolean a = link.startsWith("http://");
		boolean b = link.startsWith("https://");
		
		if(a)
			return "http";
		else if(b)
			return "https";
		else
			return "";
	}
	
	/*
	 * This function checks to see if we actually have a link or not.
	 * 
	 * @param	link	The link to determine if it's something with valid characters
	 * @return	boolean
	 */
	public boolean isValidLink(String link)
	{
        //DETERMINE IF MAILTO:, AIM? ... OTHERS
		if (link.toLowerCase().indexOf("mailto:") >= 0
				|| link.toLowerCase().indexOf("javascript:") >= 0
				|| link.toLowerCase().indexOf("?") >= 0)
					return false;
		//DETERMINE IF NOT HAS PROTOCOL
		boolean a = link.startsWith("http://");
		boolean b = link.startsWith("https://");
		if(!a && !b)
			return false;
		
        return true;	//VALID
	}
	
	/*
	 * This function will take a URL, with a protocol or not and return the base domain: google.com, msn.com, yup.me.com, etc
	 * 
	 * @param link	Link of domain name to find
	 * @return	The domain name
	 */
	public String findDomainName(String link)
	{
		String newLink = link;
		newLink = this.removeProtocol(newLink);	//STRIP ANY :// PROTOCOL HEADERS
		
		//SPLIT ON "/"
		String[] split = newLink.split("/");
		newLink = split[0];
		
		return newLink;
	}
	
	/*
	 * This function will take a url/domain and strip out the subdomain, to find the real domain.
	 * This will convert me2.google.com/page1 to google.com
	 * 
	 * @param	link	The link to find the base domain from.
	 * @return	The base domain name.
	 */
	public String findBaseDomainName(String link)
	{
		String newLink = link;
		
		//IF NOT VALID LINK
		if(!this.isValidLink(newLink))
			return null;
		
		//REMOVE PROTOCOL
		newLink = this.removeProtocol(newLink);	//STRIP ANY :// PROTOCOL HEADERS
		
		//SPLIT ON "/" TO REMOVE ANY PAGE REQUEST
		String[] splittmp = newLink.split("/");
		newLink = splittmp[0];
		
		//SPLIT AGAIN TO GET DOMAIN INFO
		String[] split = newLink.split("\\.");
		String baseDomainPrefix = "";			//FIND DOMAIN SUFFIX [com, net...]
		String baseDomain = "";				//FIND DOMAIN NAME [google.com, not me2.google.com]

		//IF UNABLE TO SPLIT DOMAIN FOR SOME REASON
		if(split.length < 2)	//IF < TWO [domain, prefix] EXIT
			return null;
		
        //IF LAST PREFIX OF DOMAIN MATCHES A TOP LEVEL DOMAIN PREFIX
        if(this.topLevelDomains.contains(split[split.length - 1]) )
        {
        	//System.out.println("DEBUG 2A");
        	///FIND START POS [DOMAIN, PREFIX == 2]
        	int startPos = split.length - 2;	//SUBTRACT DOMAIN, PREFIX FROM START POS
        	
            baseDomainPrefix = split[split.length - 1];
            for(int i = startPos; i < split.length-1; i++)
            {
                baseDomain += split[i];
                if (i < split.length - 1)
                    baseDomain += ".";
            }
            baseDomain += baseDomainPrefix;
        }
        //IF LAST TWO PARTS OF DOMAIN MATCH A TOP LEVEL PREFIX [co.uk, etc]
        else if (this.topLevelDomains.contains(split[split.length - 2] + "." + split[split.length - 1]))
        {
        	//.out.println("DEBUG 3A");
        	
        	///FIND START POS [DOMAIN, PREFIX == 3]
        	int startPos = split.length - 3;	//SUBTRACT DOMAIN, PREFIX FROM START POS
        	
            baseDomainPrefix = split[split.length - 2] + "." + split[split.length - 1];
            for (int i = startPos; i < split.length - 2; i++)
            {
                baseDomain += split[i];
                if (i < split.length - 2)
                    baseDomain += ".";
            }
            baseDomain += baseDomainPrefix;
        }
        else
        {
        	//ANOTHER DOMAIN SUCH AS TESTING.ME, M.TESTING.ME, etc
        	
        	///FIND START POS [DOMAIN, PREFIX == 2]
        	int startPos = split.length - 2;	//SUBTRACT DOMAIN, PREFIX FROM START POS
        	
            baseDomainPrefix = split[split.length - 1];
            for(int i = startPos; i < split.length-1; i++)
            {
                baseDomain += split[i];
                if (i < split.length - 1)
                    baseDomain += ".";
            }
            baseDomain += baseDomainPrefix;
        	
        }

        return baseDomain;
	}
	
	/*
	 * This function will take a url and strip out the subdomain, to find the real domain.
	 * This will convert htttp://me2.google.com/page1 to me.google.com/page
	 * 
	 * Useful for checking if link is in db as the min returned string can be compared against the db using regex
	 * 
	 * @param	link	The link to find the base domain from.
	 * @return	The base domain name.
	 */
	public String findBaseLink(String link)
	{
		String newLink = link;
		newLink = this.removeProtocol(newLink);	//STRIP ANY :// PROTOCOL HEADERS
		newLink = newLink.replace("www.", "");
		
		//STRIP /?, ?=
		if(newLink.substring(newLink.length()-1, newLink.length()).equals("="))
			newLink = newLink.substring(0, newLink.length()-1);
		if(newLink.substring(newLink.length()-1, newLink.length()).equals("?"))
			newLink = newLink.substring(0, newLink.length()-1);
		
		return newLink;
	}
	
	public void test()
	{
		List<String> ltList = new ArrayList<String>();
		ltList.add("me1.com");
		ltList.add("http://abcnews.go.com");
		ltList.add("ftp://businessinsider.com");
		ltList.add("businessinsider.com");
		ltList.add("http://www.tommas.me/?fork=");
		ltList.add("http://me2.businessinsider.com/?");
		ltList.add("http://gif.businessinsider.com/page1.html");
		ltList.add("mailto:me@businessinsider.com");
		ltList.add("www.businessinsider.co.uk");
		ltList.add("yup.businessinsider.co.uk");
		ltList.add("giffry");
		
		for(String link : ltList)
		{
			System.out.println( String.format("Link=[%s]|findDomain=[%s]|findBaseDomain=[%s]|baseLink[%s]", link, this.findDomainName(link), this.findBaseDomainName(link), this.findBaseLink(link) ) );
			//if(this.findBaseDomainName(link)==null)
			//	System.out.println("This link is null!");
		}
		
		
	}
}

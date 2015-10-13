package com.webspider.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderHTMLParser
{
	private Document doc;
	
	public SpiderHTMLParser()
	{
		this.doc = null;
	}
	
	/*
	 * This function will determine if the link is relative to the domain or not.
	 * 
	 * @param	link	The HTML Link
	 * @return	boolean
	 */
	public boolean isRelativeLink(String link) throws URISyntaxException
	{
		URI is = new URI(link);
		if(is.isAbsolute())
			return true;
		return false;
	}
	
	/*
	 * This function will search a page for all the HTML links it can find.
	 * 
	 * @return	String[]
	 */
	public String[] getHTMLLinks() throws SpiderException
	{
		int pos = 0;
		String parts[];
		List<String> tmp = new ArrayList<String>();

		if(doc.select("a").size() <= 0)
			throw new SpiderException("The page has no elements to spider");

		for(Element e : doc.select("a"))
		{
			tmp.add( e.attr("href") );
		}
		parts = tmp.toArray(new String[0]);
		return parts;
	}
	
	/*
	 * This function will call jsoup and save the results.
	 * 
	 * @param	url_data	The URL's HTML content to scan.
	 */
	public void jsoupParse(String url_data)
	{
		this.doc = Jsoup.parse(url_data);
	}
	
	/*
	 * This function will search the HTML content's tags for new HTML content.
	 * 
	 * @return String
	 */
	public String getDocumentPageContent()
	{
		Set<String> document_html = new TreeSet<String>();
	    String tag_array[] = {"h1", "p"};
	    for(String tag : tag_array)
	    {
		    Elements elements = doc.select(tag);
		    for (Element element : elements)
		    {
		    	String html_line = element.ownText().toString().trim();
		    	
		    	//IF TEXT IS BLANK ... SKIP
		    	if(element.ownText().equals(""))
		    		continue;
		    	//IF END OF HTML LINE IS NOT A "."
		    	if( html_line.charAt(html_line.length()-1) != '.' )
		    		html_line += ".";		  
		    	
		    	//ADD
		    	document_html.add(html_line);

		    }
	    }
	    return document_html.toString();
	}
}

package com.webspider.main;

import java.util.List;
import java.util.Set;
import java.util.Vector;

public class SharedResource
{
	List<String> spider_links;
	List<String> spidered_links;
	
	public SharedResource()
	{
		this.spider_links = new Vector<String>();
		this.spidered_links = new Vector<String>();
	}
	
	public boolean addLinkToSpider(String item)
	{
		if( this.spider_links.contains(item) || this.spidered_links.contains(item) )
			return false;
		else
			this.spider_links.add(item);
		return true;
	}
	
	public boolean addLinkSpidered(String item)
	{
		if( this.spidered_links.contains(item) )
			return false;
		else
			this.spidered_links.add(item);
		return true;
	}
	
	public void print()
	{
		for(String i : this.spidered_links)
			System.out.println(i);
	}
}

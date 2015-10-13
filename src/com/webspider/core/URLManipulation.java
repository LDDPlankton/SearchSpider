package com.webspider.core;

public class URLManipulation
{
	public URLManipulation()
	{
		
	}
	
	//THIS CLASS + FUNCTION SHOULD BE REFACTORED INTO [LinkTool::]
	
	//FINDS THE BASE URL http:// [DOES NOT TAKE INTO ACCOUNT // URLS]
	public String findBaseURL(String url) throws SpiderException
	{
		String base_url = "";
		String prefixes[] = {"http://", "https://"};
		boolean found_base = false;

		for(String prefix : prefixes)
		{
			//IF FOUND THIS PREFIX
			if(url.indexOf(prefix) == 0)
			{
				String tmp_url = url.replace(prefix, "");
				String parts[] = tmp_url.split("/");
				base_url = prefix + parts[0];
				found_base = true;
				break;
			}
		}
		if(!found_base)
			throw new SpiderException("Unable to find baseURL!");
		
		return base_url;
	}
}

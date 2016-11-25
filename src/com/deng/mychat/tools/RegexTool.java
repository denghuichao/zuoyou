package com.deng.mychat.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTool {
	public static List<String> badFindUrls(String data)
	{
		Pattern p =Pattern.compile("http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?");
		Matcher m=p.matcher(data);
		
		List<String>res=new ArrayList<String>();
		while(m.find())
			res.add(m.group());
		return res;
	}


public static List<String>findUrls(String data)
{
		String SubDomain  = "(?i:[a-z0-9]|[a-z0-9][-a-z0-9]*[a-z0-9])";
		String TopDomains = "(?x-i:com\\b        \n" +
		                    "     |edu\\b        \n" +
		                    "     |biz\\b        \n" +
		                    "     |in(?:t|fo)\\b \n" +
		                    "     |mil\\b        \n" +
		                    "     |net\\b        \n" +
		                    "     |org\\b        \n" +
		                    "     |[a-z][a-z]\\b \n" + // country codes
		                    ")                   \n";
		String Hostname = "(?:" + SubDomain + "\\.)+" + TopDomains;
	
		String NOT_IN   = ";\"'<>()\\[\\]{}\\s\\x7F-\\xFF";
		String NOT_END  = "!.,?";
		String ANYWHERE = "[^" + NOT_IN + NOT_END + "]";
		String EMBEDDED = "[" + NOT_END + "]";
		String UrlPath  = "/"+ANYWHERE + "*("+EMBEDDED+"+"+ANYWHERE+"+)*";
		String Url = 
		  "(?x:                                                \n"+
		  "  \\b                                               \n"+
		  "  ## match the hostname part                        \n"+
		  "  (                                                 \n"+
		  "    (?: ftp | http s? ): // [-\\w]+(\\.\\w[-\\w]*)+ \n"+
		  "   |                                                \n"+
		  "    " + Hostname + "                                \n"+
		  "  )                                                 \n"+
		  "  # allow optional port                             \n"+
		  "  (?: :\\d+ )?                                      \n"+
		  "                                                    \n"+
		  "  # rest of url is optional, and begins with /      \n"+
		  " (?: " + UrlPath + ")?                              \n"+
		  ")";
	
		// Now convert string we've built up into a real regex object
		Pattern p = Pattern.compile(Url);
		Matcher m=p.matcher(data);
		
		List<String>res=new ArrayList<String>();
		while(m.find())
			res.add(m.group());
		return res;
	}
}
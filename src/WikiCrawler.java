// LEAVE THIS FILE IN THE DEFAULT PACKAGE
//  (i.e., DO NOT add 'package cs311.pa1;' or similar)

// DO NOT MODIFY THE EXISTING METHOD SIGNATURES
//  (you may, however, add member fields and additional methods)

// DO NOT INCLUDE LIBRARIES OUTSIDE OF THE JAVA STANDARD LIBRARY
//  (i.e., you may only include libraries of the form java.*)

/**
* @author Hugh Potter
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WikiCrawler
{
	static final String BASE_URL = "https://en.wikipedia.org/";
	private String seedURL;
	private int max;
	private List<String> topics;
	private String fileName;
	HashMap<Integer,String> visited = new HashMap<>();


	public WikiCrawler(String seedUrl, int max, ArrayList<String> topics, String fileName)
	{
		// implementation
		this.seedURL = seedUrl;
		this.max = max;
		this.topics = topics;
		this.fileName = fileName;
	}

	public void crawl()
	{
		// implementation
		String startPage = WebUtils.getPageAsString(BASE_URL,seedURL);

	}



	private boolean visited(String url){
		return visited.containsKey(url.hashCode());
	}

	public static void main(String[] args){
		ArrayList<String> topics = new ArrayList<>();
		topics.add("Iowa State");
		topics.add("Cyclones");
		WikiCrawler wc = new WikiCrawler("/wiki/Iowa_State_University", 100, topics, "WikiISU.txt");
		wc.crawl();
	}
}
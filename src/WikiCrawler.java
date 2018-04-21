// LEAVE THIS FILE IN THE DEFAULT PACKAGE
//  (i.e., DO NOT add 'package cs311.pa1;' or similar)

// DO NOT MODIFY THE EXISTING METHOD SIGNATURES
//  (you may, however, add member fields and additional methods)

// DO NOT INCLUDE LIBRARIES OUTSIDE OF THE JAVA STANDARD LIBRARY
//  (i.e., you may only include libraries of the form java.*)

/**
* @author Hugh Potter
*/

import javafx.util.Pair;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class WikiCrawler
{
	static final String BASE_URL = "https://en.wikipedia.org/";
	private String seedURL;
	private int max;
	private ArrayList<String> topics;
	HashMap<Integer, WebPage> visited = new HashMap<>();
	HashMap<Integer, Boolean> topicMap = new HashMap<>();
	HashMap<Integer, WebPage> loadMap = new HashMap<>();
	Queue<WebPage> visitQueue = new LinkedList<>();
	HashMap<Integer, Pair<String, String>> edges = new HashMap<>();
	DirectedGraph<String> graph = new DirectedGraph<>();
	PrintWriter out = null;



	public WikiCrawler(String seedUrl, int max, ArrayList<String> topics, String fileName)
	{
		// implementation
		this.seedURL = seedUrl;
		this.max = max;
		this.topics = topics;
		try {
			out = new PrintWriter(new File(fileName));
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void crawl() {

		out.println(max);

		// implementation
		WebPage seedPage = new WebPage(BASE_URL,seedURL);
		visitQueue.add(seedPage);

		while(!visitQueue.isEmpty()){
			crawl(visitQueue.remove());
		}

		out.close();
	}


	private void crawl(WebPage page){

		//Determine if page meets requirements
		if(isValidLink(page.getURL()) && determineValidity(page)){

			System.out.printf("Visiting %s\n",page.getURL());
			//
			for(String link: page.getLinks()){
				WebPage linkedPage = new WebPage(BASE_URL,link);

				//O(1) or O(downloadTime)
				boolean goodPage = determineValidity(linkedPage);
				if(graph.size() < max && goodPage) {
					graph.addNode(linkedPage.getURL());
					visitQueue.add(linkedPage);
				}

				if(goodPage){
					addEdge(page,linkedPage);
				}

			}
		}
	}


	/**
	 * O(1)
	 * @param from
	 * @param to
	 */
	private void addEdge(WebPage from, WebPage to){
		int hash = (from.getURL()+to.getURL()).hashCode();
		if(isValidEdge(from.getURL(),to.getURL())){
			edges.put(hash,null);
			out.println(String.format("%s %s",from.getURL(),to.getURL()));
			graph.addEdge(from.getURL(),to.getURL());
			System.out.printf("\tEDGE %s %s\n",from.getURL(),to.getURL());
		}
	}

	/**
	 * Probably takes 2n + c < O(n)
	 * @param from
	 * @param to
	 * @return
	 */
	private boolean isValidEdge(String from, String to){

		return !edges.containsKey((from+to).hashCode())// O(1)
				&& !from.equalsIgnoreCase(to) // O(n)
				&& isValidLink(to); //O(n)
	}

	/**
	 * O(n) where n = link.size
	 * @param link
	 * @return
	 */
	public boolean isValidLink(String link){
		return !(link.contains("#") || link.contains(":"));
	}




	/**
	 * Will determine if a page is valid
	 * takes time either
	 * 		O(1) - page was downloaded previously
	 * 		O(downloadTime) - page needs to be downloaded
	 *
	 * 	in the end O(n*downloadTime) where n = nodes in graph
	 * @param page
	 * @return
	 */
	private boolean determineValidity(WebPage page){


		if(graph.size() >= max){
			if(graph.contains(page.getURL())){

				return true;
			}
			return false;
		}

		//page has been downloaded
		if(topicMap.containsKey(page.hashCode())) {
			return topicMap.get(page.hashCode());
		}else{
			//page needs to be downloaded
			boolean valid = page.containsTopics(topics);
			topicMap.put(page.hashCode(),valid);
			return valid;
		}
	}

	public static void main(String[] args){
		WikiCrawler cs = new WikiCrawler("/wiki/Computer Science",100,new ArrayList<>(),"WikiCS.txt");
		cs.crawl();

		NetworkInfluence net = new NetworkInfluence("WikiCS.txt");
		for(String s: net.mostInfluentialModular(10)){
			System.out.println(s);
		}
	}
}
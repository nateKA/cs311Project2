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
	Debugger debugger;



	public WikiCrawler(String seedUrl, int max, ArrayList<String> topics, String fileName)
	{
		// implementation
		this.seedURL = seedUrl;
		this.max = max;
		this.topics = topics;
		try {
			out = new PrintWriter(new File(fileName));
			debugger  = new Debugger("debug.txt");
			WebPage.debugger = debugger;
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void crawl() {
		// implementation
		WebPage seedPage = new WebPage(BASE_URL,seedURL);
		visitQueue.add(seedPage);

		while(!visitQueue.isEmpty()){
			crawl(visitQueue.remove());
		}


		out.close();
		debugger.close();
	}

	private void crawl(WebPage page){
		debugger.printf("VISITING: %s",page.getURL());
		if(isValidLink(page.getURL()) && determineValidity(page)){
			debugger.printf("\tisValid = TRUE");
			for(String link: page.getLinks()){
				WebPage linkedPage = new WebPage(BASE_URL,link);

				boolean goodPage = determineValidity(linkedPage);
				if(graph.size() < max && goodPage) {
					graph.addNode(linkedPage.getURL());
					visitQueue.add(linkedPage);
					debugger.printf("\tADDED NODE: %s",link);
				}

				if(goodPage){
					addEdge(page,linkedPage);
				}

			}
		}else{

			debugger.printf("\tisValid = FALSE");
		}
	}


	private void addEdge(WebPage from, WebPage to){
		int hash = (from.getURL()+to.getURL()).hashCode();
		if(isValidEdge(from.getURL(),to.getURL())){
			edges.put(hash,null);
			out.println(String.format("%s %s",from.getURL(),to.getURL()));
			debugger.println(String.format("EDGE #%d: %s -> %s",edges.size(),from.getURL(),to.getURL()));
			graph.addEdge(from.getURL(),to.getURL());
		}
	}
	private boolean isValidEdge(String from, String to){

		return !edges.containsKey((from+to).hashCode())
				&& !from.equalsIgnoreCase(to)
				&& isValidLink(to);
	}
	public boolean isValidLink(String link){
		return !(link.contains("#") || link.contains(":"));
	}


	private boolean isVisited(String url){
		return visited.containsKey(WebUtils.combinePaths(BASE_URL, url).hashCode());
	}

	private boolean isVisited(WebPage page){
		return visited.containsKey(page.hashCode());
	}
	private void visit(WebPage page){
		visited.put(page.hashCode(),page);
	}

	private boolean determineValidity(WebPage page){

		if(graph.size() >= max){
			if(graph.contains(page.getURL())){

				debugger.printf("\tGRAPH CONTAINS. Approved: %s",page.getURL());
				return true;
			}
			debugger.printf("\tGRAPH FULL!! Rejected: %s",page.getURL());
			return false;
		}

		if(topicMap.containsKey(page.hashCode())) {
			debugger.println(String.format("\tPREV DETERMINED %s: %s",(topicMap.get(page.hashCode())==false)?"INVALID":"VALID",
					page.getURL()));
			return topicMap.get(page.hashCode());
		}else{
			boolean valid = page.containsTopics(topics);
			topicMap.put(page.hashCode(),valid);
			debugger.println(String.format("\tDETERMINED %s: %s",(valid)?"INVALID":"VALID",
					page.getURL()));
			return valid;
		}
	}


	public static void main(String[] args){
		ArrayList<String> topics = new ArrayList<>();
		//topics.add("Iowa State");
		//topics.add("Cyclones");
		WikiCrawler wc = new WikiCrawler("/wiki/Complexity_theory", 20, topics, "WikiISU.txt");
		wc.crawl();
	}
}
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
	Queue<Pair<WebPage, WebPage>> visitQueue = new LinkedList<>();
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
		debugger.println(String.format("VISITING: %s",WebUtils.combinePaths(BASE_URL,seedURL)));
		if(determineValidity(seedPage)){
			for(String link: seedPage.getLinks()){
				if(!isValidEdge(seedPage.getURL(), link))continue;
				addToQueue(seedPage, link);
				debugger.println(String.format("\tAdded to QUEUE: %s -> %s",seedPage.getURL(), link));
			}
		}

		while(edges.size() < max && !visitQueue.isEmpty()){
			Pair<WebPage, WebPage> head = visitQueue.remove();
			WebPage from = head.getKey();
			WebPage to = head.getValue();
			crawl(from,to);
		}

		out.close();
		debugger.close();
	}


	private void crawl(WebPage from, WebPage to){
		if(isVisited(to))return;
		debugger.println(String.format("VISITING: %s",WebUtils.combinePaths(BASE_URL,to.getURL())));
		visit(to);

		if(determineValidity(to)){
			addEdge(new Pair(from.getURL(),to.getURL()));

			for(String link: to.getLinks()){
				if(!isValidEdge(to.getURL(), link))continue;

				addToQueue(to,link);
				debugger.println(String.format("\tAdded to QUEUE: %s -> %s",to.getURL(), link));
			}
		}
	}

	private void addEdge(Pair<String,String> edge){
		String from = edge.getKey();
		String to = edge.getValue();
		int hash = (from+to).hashCode();
		if(!edges.containsKey(hash)){
			graph.addEdge(from,to);
			edges.put(hash,edge);
			out.println(String.format("%s %s",from,to));
			debugger.println(String.format("EDGE #%d: %s -> %s",edges.size(),from,to));
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

	private void addToQueue(WebPage from, String toURL){
		if(loadMap.containsKey(WebUtils.combinePaths(BASE_URL,toURL).hashCode())) {
			debugger.println(String.format("\t\tLOADED %s", toURL));
			visitQueue.add(new Pair(from, loadMap.get(WebUtils.combinePaths(BASE_URL, toURL).hashCode())));
		}else {
			WebPage toPage = new WebPage(BASE_URL, toURL);
			loadMap.put(toPage.hashCode(),toPage);
			debugger.println(String.format("\t\tNEW WEBPAGE: %s", toURL));
			visitQueue.add(new Pair(from,toPage));
		}
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
		if(topicMap.containsKey(page.hashCode())) {
			debugger.println(String.format("\tPREV DETERMINED %s: %s",(topicMap.get(page)==false)?"INVALID":"VALID",
					page.getURL()));
			return topicMap.get(page);
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
		WikiCrawler wc = new WikiCrawler("/wiki/complexity_theory", 100, topics, "WikiISU.txt");
		wc.crawl();
	}
}
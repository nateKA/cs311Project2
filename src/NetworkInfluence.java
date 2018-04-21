// LEAVE THIS FILE IN THE DEFAULT PACKAGE
//  (i.e., DO NOT add 'package cs311.pa1;' or similar)

// DO NOT MODIFY THE EXISTING METHOD SIGNATURES
//  (you may, however, add member fields and additional methods)

// DO NOT INCLUDE LIBRARIES OUTSIDE OF THE JAVA STANDARD LIBRARY
//  (i.e., you may only include libraries of the form java.*)

/**
* @author Hugh Potter
*/

import java.io.File;
import java.util.*;

public class NetworkInfluence
{
	private DirectedGraph<String> graph;


	// NOTE: graphData is an absolute file path that contains graph data, NOT the raw graph data itself
	public NetworkInfluence(String graphData)
	{
		// implementation
		try{
			graph = new DirectedGraph<>();
			File gData = new File(graphData);
			Scanner scanner = new Scanner(gData);
			int verts = Integer.parseInt(scanner.nextLine().trim());
			while(scanner.hasNextLine()){
				String[] line = scanner.nextLine().split("\\s+");
				graph.addEdge(line[0],line[1]);

			}
			if(verts != graph.getNodes().size()){
				//throw new IllegalArgumentException(String.format("You promised there'd be %d vertices! We only found %d"
						//			,verts,graph.getNodes().size()));
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public int outDegree(String v)
	{
		return graph.getEdges(v).size();
	}

	/**
	 * Returns all data involved with dijktar's Algorithm
	 * @param u
	 * @param v
	 * @return
	 */
	public HashMap<String,Object> dijktarsShortestPath(String u, String v){
		HashMap<String, Object> returnVals = new HashMap<>();
		// implementation
		HashMap<Integer, Boolean> visited = new HashMap<>();
		HashMap<Integer,Integer> previous = new HashMap<>();
		HashMap<Integer, Integer> shortestDist = new HashMap<>();
		HashMap<Integer,Integer> atDistCount = new HashMap<>();
		ArrayList<String> path = new ArrayList<>();
		Queue<Integer> visitQueue = new LinkedList<>();
		returnVals.put("distMap",shortestDist);
		returnVals.put("previousMap",previous);


		if(u.equalsIgnoreCase(v)){
			path.add(u);
			return returnVals;
		}

		visitQueue.add(u.hashCode());
		shortestPath(visited,visitQueue,previous,shortestDist);


		return returnVals;
	}
	public ArrayList<String> shortestPath(String u, String v)
	{
		// implementation
		HashMap<String,Object> vals = dijktarsShortestPath(u,v);
		ArrayList<String> path = new ArrayList<>();
		HashMap<Integer,Integer> previous = (HashMap<Integer, Integer>) vals.get("previousMap");

		try {
			String next = v;
			while (!next.equalsIgnoreCase(u)) {
				path.add(0, next);
				next = graph.getNode(previous.get(next.hashCode()));
			}
			path.add(0, u);
			return path;

		}catch (Exception e){
			return null;
		}
	}

	private void shortestPath(HashMap<Integer,Boolean> visited, Queue<Integer> visitQueue,
							  HashMap<Integer,Integer> previous, HashMap<Integer,Integer> shortestDist){
		if(visitQueue.isEmpty()) return;
		Integer n = visitQueue.remove();

		String node = graph.getNode(n);
		try {
			visited.put(node.hashCode(), true);
		}catch(Exception e){
			e.printStackTrace();
		}
		for(Integer hashCode: graph.getEdges(node.hashCode())){
			if(visited.containsKey(hashCode))continue;

			//update shortestDist
			int newDist = 1;
			if(shortestDist.containsKey(node.hashCode())){
				newDist = 1 + shortestDist.get(node.hashCode());
			}else{
				shortestDist.put(node.hashCode(),0);
			}
			int oldDist = shortestDist.containsKey(hashCode)?shortestDist.get(hashCode):-1;
			if(oldDist > newDist || oldDist == -1){
				shortestDist.put(hashCode,newDist);

				//update previous
				previous.put(hashCode,node.hashCode());
			}

			if(!visited.containsKey(hashCode))
			visitQueue.add(hashCode);
		}

		shortestPath(visited,visitQueue,previous,shortestDist);
	}

	public int distance(String u, String v)
	{

		return shortestPath(u,v).size()-1;
	}

	public int distance(ArrayList<String> s, String v)
	{
		int smallest = -1;
		for(String str: s){
			if(shortestPath(str,v)==null)continue;

			if(smallest == -1) smallest = distance(str,v);
			else smallest = Math.min(smallest, distance(str,v));
		}
		return smallest;
	}

	public float influence(String u)
	{
		float influence = 0;
		String v = getDifferentNode(u);
		HashMap<String,Object> vals = dijktarsShortestPath(u,v);
		HashMap<Integer,Integer> dists = (HashMap<Integer,Integer>)vals.get("distMap");
		Iterator<Integer> iter = dists.keySet().iterator();

		while(iter.hasNext()){
			int key = iter.next();

			influence += 1 / Math.pow(2,dists.get(key));
		}
		return influence;
	}

	private String getDifferentNode(String u){

		List<String> nodes = graph.getNodes();
		for(String v: nodes){
			if(!v.equalsIgnoreCase(u)){
				return v;
			}
		}
		return null;
	}

	public float influence(ArrayList<String> s)
	{
		float influence = 0;


			for(String u : graph.getNodes()) {

				int dist = distance(s,u);
				if(dist == -1)continue;
				double calc = 1 / Math.pow(2, dist);
				influence += calc;
			}


		return influence;
	}

	public ArrayList<String> mostInfluentialDegree(int k)
	{
		// implementation
		List<String> nodes = graph.getNodes();
		ArrayList<String> kNodes = new ArrayList<>();

		Collections.sort(nodes, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return graph.getEdges(o2).size() -  graph.getEdges(o1).size();
			}
		});

		for(int i = 0; i < k; i++){
			kNodes.add(nodes.get(i));
		}

		return kNodes;
	}

	public ArrayList<String> mostInfluentialModular(int k)
	{
		List<String> nodes = graph.getNodes();
		HashMap<String,Float> infMap = new HashMap<>();
		ArrayList<String> kNodes = new ArrayList<>();

		for(String u: nodes){
			infMap.put(u,influence(u));
		}

		Collections.sort(nodes, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				float val = (infMap.get(o2) -  infMap.get(o1));
				if(val > 0)return 1;
				if(val < 0)return -1;
				return 0;
			}
		});

		for(int i = 0; i < k; i++){
			kNodes.add(nodes.get(i));
		}

		return kNodes;
	}

	public ArrayList<String> mostInfluentialSubModular(int k)
	{
		List<String> nodes = graph.getNodes();
		HashMap<String,Float> infMap = new HashMap<>();
		ArrayList<String> kNodes = new ArrayList<>();
		ArrayList<String> intermediate = new ArrayList<>();
		String mostInf = mostInfluentialModular(1).get(0);
		kNodes.add(mostInf);
		intermediate.add(mostInf);
		nodes.remove(mostInf);

		for(int i = 1; i < k; i++){
			float highest = 0;
			String lastHighest = nodes.get(0);
			for(String u: nodes) {
				intermediate.add(u);
				float newInf = influence(intermediate);
				intermediate.remove(u);

				if(newInf >= highest){
					highest = newInf;
					lastHighest = u;
				}
			}

			intermediate.add(lastHighest);
			kNodes.add(lastHighest);
			nodes.remove(lastHighest);
		}



		return kNodes;
	}

	public static void main(String[] args){
		NetworkInfluence net = new NetworkInfluence("graph.txt");

		ArrayList<String> set = new ArrayList<>();
		set.add("a");
		set.add("c");

		for(String s : net.graph.getNodes()){
			System.out.println(s+" - "+s.hashCode());
		}
		System.out.println(net.influence(set));


//		for(String s: net.mostInfluentialSubModular(4)){
//			//System.out.println(s);
//			for(Integer i: net.graph.getEdges(s.hashCode())){
//				//System.out.println("\t"+net.graph.getNode(i));
//			}
//		}
	}
}
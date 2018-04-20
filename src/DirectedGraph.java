import java.util.*;

public class DirectedGraph<E> {
    private HashMap<Integer, List<Integer>> edges = new HashMap<>();
    private HashMap<Integer, E> nodes = new HashMap<>();

    public List<E> getNodes(){
       List<E> list = new ArrayList<>();
       Iterator<Integer> iter = nodes.keySet().iterator();
       while(iter.hasNext()){
           E value = nodes.get(iter.next());
           list.add(value);
       }

       return list;
    }

    public E getNode(Integer hashCode){
        return nodes.get(hashCode);
    }

    public void addEdge(E start, E end){
        if(edges.containsKey(start.hashCode())){
            edges.get(start.hashCode()).add(end.hashCode());
        }else{
            List<Integer> edges = new ArrayList<>();
            edges.add(end.hashCode());
            nodes.put(start.hashCode(),start);
            this.edges.put(start.hashCode(),edges);
        }

        if(!nodes.containsKey(start.hashCode()))
            addNode(start);
        if(!nodes.containsKey(end.hashCode()))
            addNode(end);
    }

    public void addNode(E node){
        if(!nodes.containsKey(node.hashCode())){
            nodes.put(node.hashCode(),node);
            edges.put(node.hashCode(),new ArrayList<>());
        }
    }

    public List<Integer> getEdges(Integer hashCode){
        return edges.get(hashCode);
    }
    public List<Integer> getEdges(E node){
        return edges.get(node.hashCode());
    }
    public boolean contains(E value){
        return nodes.containsKey(value.hashCode());
    }

    public List<E> BFS(E node){
        List<E> bfs = new ArrayList<>();
        HashMap<Integer, Boolean> visited = new HashMap<>();
        Queue<Integer> visitQueue = new LinkedList<>();
        visitQueue.add(node.hashCode());
        BFS(bfs,visited,visitQueue);
        return bfs;
    }

    private void BFS(List<E> bfs, HashMap<Integer, Boolean> visited, Queue<Integer> visitQueue){
        if(visitQueue.isEmpty()) return;

        E node = getNode(visitQueue.remove());
        visited.put(node.hashCode(),true);
        bfs.add(node);
        for(Integer hashCode: getEdges(node.hashCode())){
            if(visited.containsKey(hashCode))continue;

            visitQueue.add(hashCode);
        }

        BFS(bfs,visited,visitQueue);
    }

    public int size(){
        return nodes.size();
    }

    static String one = "one";
    static String two = "two";
    static String three = "three.one";
    static String four = "three.two";
    static String five = "four";

    public static void main(String[] args){
        DirectedGraph<String> g = new DirectedGraph<>();

        g.addEdge(five, two);
        g.addEdge(two,four);
        g.addEdge(three,five);
        g.addEdge(one,two);
        g.addEdge(two,three);


        for(String str: g.BFS("one")){
            System.out.println(str);
        }
    }
}

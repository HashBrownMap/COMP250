import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine {

	public HashMap<String, LinkedList<String>> wordIndex; // this will contain a
															// set of pairs
															// (String,
															// LinkedList of
															// Strings)
	public directedGraph internet; // this is our internet graph

	// Constructor initializes everything to empty data structures
	// It also sets the location of the internet files
	searchEngine() {
		// Below is the directory that contains all the internet files
		htmlParsing.internetFilesLocation = "internetFiles";
		wordIndex = new HashMap<String, LinkedList<String>>();
		internet = new directedGraph();
	} // end of constructor2015

	// Returns a String description of a searchEngine
	public String toString() {
		return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
	}

	// This does a graph traversal of the internet, starting at the given url.
	// For each new vertex seen, it updates the wordIndex, the internet graph,
	// and the set of visited vertices.

	void traverseInternet(String url) throws Exception {
		/* WRITE SOME CODE HERE */

		/*
		 * Hints 0) This should take about 50-70 lines of code (or less) 1) To
		 * parse the content of the url, call htmlParsing.getContent(url), which
		 * returns a LinkedList of Strings containing all the words at the given
		 * url. Also call htmlParsing.getLinks(url). and assign their results to
		 * a LinkedList of Strings. 2) To iterate over all elements of a
		 * LinkedList, use an Iterator, as described in the text of the
		 * assignment 3) Refer to the description of the LinkedList methods at
		 * http://docs.oracle.com/javase/6/docs/api/ . You will most likely need
		 * to use the methods contains(String s), addLast(String s), iterator()
		 * 4) Refer to the description of the HashMap methods at
		 * http://docs.oracle.com/javase/6/docs/api/ . You will most likely need
		 * to use the methods containsKey(String s), get(String s), put(String
		 * s, LinkedList l).
		 */

		Queue<String> queue = new ArrayDeque();
		internet.addVertex(url);
		internet.setVisited(url, true);
		LinkedList<String> words = htmlParsing.getContent(url);
		LinkedList<String> links = htmlParsing.getLinks(url);
		Iterator<String> j = links.iterator();
		while (j.hasNext()) {
			String link = j.next();
			internet.addVertex(link);
			internet.addEdge(url, link);
		}
		Iterator<String> q = words.iterator();
		while (q.hasNext()) {
			String word = q.next();
			if (wordIndex.containsKey(word)) {
				if (!(wordIndex.get(word).contains(url))) {
					wordIndex.get(word).addLast(url);
				}
			} else {

				LinkedList<String> nouveau = new LinkedList<String>();
				nouveau.addLast(url);
				wordIndex.put(word, nouveau);
			}
		}
		queue.add(url);
		while (!queue.isEmpty()) {
			String w = queue.remove();
			for (String v : internet.getNeighbors(w)) {
				if (internet.getVisited(v) == false) {
					internet.setVisited(v, true);
					queue.add(v);
					LinkedList<String> content = htmlParsing.getContent(v);
					LinkedList<String> linksss = htmlParsing.getLinks(v);

					Iterator<String> m = linksss.iterator();
					while (m.hasNext()) {
						String link = m.next();
						internet.addVertex(link);
						internet.addEdge(v, link);
					}

					Iterator<String> i = content.iterator();
					while (i.hasNext()) {
						String word = i.next();
						if (wordIndex.containsKey(word)) {
							if (!(wordIndex.get(word).contains(v))) {
								wordIndex.get(word).addLast(v);
							}
						} else {

							LinkedList<String> nouveau = new LinkedList<String>();
							nouveau.addLast(v);
							wordIndex.put(word, nouveau);
						}
					}

				}
			}
		}

	} // end of traverseInternet

	/*
	 * This computes the pageRanks for every vertex in the internet graph. It
	 * will only be called after the internet graph has been constructed using
	 * traverseInternet. Use the iterative procedure described in the text of
	 * the assignment to compute the pageRanks for every vertices in the graph.
	 * 
	 * This method will probably fit in about 30 lines.
	 */
	void computePageRanks() {
		/* WRITE YOUR CODE HERE */

		LinkedList<String> sites = internet.getVertices();
		Iterator<String> i = sites.iterator();
		while (i.hasNext()) {
			String site = i.next();
			internet.setPageRank(site, 1);
		}
		for (int q = 0; q < 100; q++) {
			i = sites.iterator();
			while (i.hasNext()) {
				double pr = 0.5;
				String site = i.next();
				LinkedList<String> refers = internet.getEdgesInto(site);
				Iterator<String> m = refers.iterator();
				while (m.hasNext()) {
					String ref = m.next();
					pr += 0.5 * (internet.getPageRank(ref) / internet.getOutDegree(ref));
				}
				internet.setPageRank(site, pr);
			}
		}
	}

	// end of computePageRanks

	/*
	 * Returns the URL of the page with the high page-rank containing the query
	 * word Returns the String "" if no web site contains the query. This method
	 * can only be called after the computePageRanks method has been executed.
	 * Start by obtaining the list of URLs containing the query word. Then
	 * return the URL with the highest pageRank. This method should take about
	 * 25 lines of code.
	 */
	String getBestURL(String query) {
		/* WRITE YOUR CODE HERE */

		String best ="";
		double bestpr = 0.0;
		if(wordIndex.containsKey(query)){
			System.out.println("the word exists");
			ListIterator<String> Iterator = wordIndex.get(query).listIterator();
			while(Iterator.hasNext()){
				String site = Iterator.next();
				if(internet.getPageRank(site) > bestpr){
					bestpr = internet.getPageRank(site);
					best = site;
				}
			}
		}else{
			best = "it doesn't exist";
		}
		System.out.println("pr ="+bestpr);
		return best; // remove this
	} // end of getBestURL

	public static void main(String args[]) throws Exception {
		searchEngine mySearchEngine = new searchEngine();
		// to debug your program, start with.
		 //mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");

		// When your program is working on the small example, move on to
		mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");

		// this is just for debugging purposes. REMOVE THIS BEFORE SUBMITTING
		System.out.println(mySearchEngine);

		mySearchEngine.computePageRanks();

		BufferedReader stndin = new BufferedReader(new InputStreamReader(
				System.in));
		String query;
		do {
			System.out.print("Enter query: ");
			query = stndin.readLine();
			if (query != null && query.length() > 0) {
				System.out.println("Best site = "
						+ mySearchEngine.getBestURL(query));
			}
		} while (query != null && query.length() > 0);
	} // end of main
}

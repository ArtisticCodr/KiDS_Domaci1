package result_retriever;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import threadSafeObj.ThreadSafeMap;

public class Retriever implements Callable<Map<String, Integer>> {

	private ThreadSafeMap<String, Future<Map<String, Integer>>> resultMap = null;
	private String resultName;
	private Future<Map<String, Integer>> result;

	// konstruktor za ubacivanje prosledjenih rezultata u odgovarajucu mapu
	public Retriever(ThreadSafeMap<String, Future<Map<String, Integer>>> resultMap, String resultName,
			Future<Map<String, Integer>> result) {
		this.resultMap = resultMap;
		this.resultName = resultName;
		this.result = result;
	}

	public ThreadSafeMap<String, Future<Map<String, Integer>>> corpusResultMap = null;
	public ThreadSafeMap<String, Future<Map<String, Integer>>> linkResultMap = null;
	public ThreadSafeMap<String, Map<String, Integer>> domenResultMap = null;
	public String query = null;

	// konstruktor za reagovanje na upit sa strane CLI
	public Retriever(ThreadSafeMap<String, Future<Map<String, Integer>>> corpusResultMap,
			ThreadSafeMap<String, Future<Map<String, Integer>>> linkResultMap,
			ThreadSafeMap<String, Map<String, Integer>> domenResultMap, String query) {
		this.corpusResultMap = corpusResultMap;
		this.linkResultMap = linkResultMap;
		this.domenResultMap = domenResultMap;
		this.query = query;
	}

	@Override
	public Map<String, Integer> call() throws Exception {

		if (resultMap != null) {
			// ubacujemo prosledjeni rezultat u odgovarajucu mapu
			resultMap.put(resultName, result);
			return null;
		} else {
			// reagujemo na upit sa strane CLI
			if (query.startsWith("file|")) {
				query = query.substring(5);

				if (corpusResultMap.contains(query)) {
					return corpusResultMap.get(query).get();
				}
			} else if (query.startsWith("web|")) {
				query = query.substring(4);
				if (domenResultMap.contains(query)) {
					if (domenResultMap.get(query) == null) {
						return mergeLinksToDomain();
					}
					return domenResultMap.get(query);
				} else {
					Map<String, Integer> resultMap = mergeLinksToDomain();
					return resultMap;
				}
			}

		}
		return null;
	}

	private Map<String, Integer> mergeLinksToDomain() {
		Map<String, Integer> resultMap = new HashMap<>();

		try {
			for (String key : linkResultMap.keySet()) {
				URL aURL = new URL(key);
				String domain = aURL.getHost();
				if (domain.startsWith("www.")) {
					domain = domain.substring(4);
				}

				if (domain.equals(query)) {
					Map<String, Integer> linkRes = linkResultMap.get(key).get();
					resultMap = merge(resultMap, linkRes);
				}
			}
			domenResultMap.put(query, resultMap);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return resultMap;
	}

	private Map<String, Integer> merge(Map<String, Integer> m1, Map<String, Integer> m2) {
		Map<String, Integer> returnMap = new HashMap<String, Integer>();

		returnMap.putAll(m1);

		for (String key : m2.keySet()) {
			if (returnMap.containsKey(key)) {
				Integer x = returnMap.get(key) + m2.get(key);
				returnMap.put(key, x);
			} else {
				returnMap.put(key, m2.get(key));
			}
		}

		return returnMap;
	}

}

package result_retriever;

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
	public ThreadSafeMap<String, Future<Map<String, Integer>>> domenResultMap = null;
	public String query = null;

	// konstruktor za reagovanje na query
	public Retriever(ThreadSafeMap<String, Future<Map<String, Integer>>> corpusResultMap,
			ThreadSafeMap<String, Future<Map<String, Integer>>> linkResultMap,
			ThreadSafeMap<String, Future<Map<String, Integer>>> domenResultMap, String query) {
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
			// reagujemo na query
			if (query.startsWith("file|")) {
				query = query.substring(5);

				if (corpusResultMap.contains(query)) {
					return corpusResultMap.get(query).get();
				}
			} else if (query.startsWith("web|")) {
				query = query.substring(4);

			}

		}
		return null;
	}

}

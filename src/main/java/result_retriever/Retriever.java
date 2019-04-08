package result_retriever;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import job.ScanType;
import threadSafeObj.ThreadSafeMap;

public class Retriever implements Callable<Result> {

	private ThreadSafeMap<String, Future<Map<String, Integer>>> resultMap = null;
	private String resultName;
	private Future<Map<String, Integer>> result;
	private ScanType type;

	// konstruktor za ubacivanje prosledjenih rezultata u odgovarajucu mapu
	public Retriever(ThreadSafeMap<String, Future<Map<String, Integer>>> resultMap, String resultName,
			Future<Map<String, Integer>> result, ScanType type,
			ThreadSafeMap<String, Map<String, Integer>> domenResultMap) {
		this.resultMap = resultMap;
		this.resultName = resultName;
		this.result = result;
		this.type = type;
		this.domenResultMap = domenResultMap;
	}

	private ThreadSafeMap<String, Future<Map<String, Integer>>> corpusResultMap = null;
	private ThreadSafeMap<String, Future<Map<String, Integer>>> linkResultMap = null;
	private ThreadSafeMap<String, Map<String, Integer>> domenResultMap = null;
	private String query = null;
	private String answearType;

	// konstruktor za reagovanje na upit sa strane CLI
	public Retriever(ThreadSafeMap<String, Future<Map<String, Integer>>> corpusResultMap,
			ThreadSafeMap<String, Future<Map<String, Integer>>> linkResultMap,
			ThreadSafeMap<String, Map<String, Integer>> domenResultMap, String query, String answearType) {
		this.corpusResultMap = corpusResultMap;
		this.linkResultMap = linkResultMap;
		this.domenResultMap = domenResultMap;
		this.query = query;
		this.answearType = answearType;
	}

	private Result finalResult = null;

	@Override
	public Result call() throws Exception {
		if (resultMap != null) {
			// proveravamo dal je web rezultat.. ako jest onda gledamo dal imamo domen
			// kesiran.. ako imamo brisemo kes za taj domen
			if (type.equals(ScanType.WEB)) {
				String domain = getDomain(resultName);
				if (domain != null) {
					if (domenResultMap.contains(domain)) {
						domenResultMap.remove(domain);
					}
				}
			}

			// ubacujemo prosledjeni rezultat u odgovarajucu mapu
			resultMap.put(resultName, result);
			return null;
		} else {
			// reagujemo na upit sa strane CLI
			if (query.startsWith("file|")) {
				query = query.substring(5);

				if (corpusResultMap.contains(query)) {
					// AKO JE QUERY
					if (answearType.contentEquals("query")) {
						return getQueryFileResult();
					}

					// AKO JE GET
					finalResult = new Result(corpusResultMap.get(query).get(), null);
					return finalResult;
				}
			} else if (query.startsWith("web|")) {
				query = query.substring(4);

				// QUERY--------------------------------------------------------------------------------
				if (answearType.contentEquals("query")) {
					if (domenResultMap.contains(query)) {
						if (domenResultMap.get(query) == null) {
							QueryResult qRes = queryMergeLinksToDomain();
							if (qRes.isFinished == false) {
								return new Result(null, "Request still in progress");
							}

							finalResult = new Result(qRes.result, null);
							if (finalResult.result == null) {
								return new Result(null, "No link with domain " + query + " has been scanned");
							}
							return finalResult;
						}
						finalResult = new Result(domenResultMap.get(query), null);
						return finalResult;
					} else {
						QueryResult qRes = queryMergeLinksToDomain();
						if (qRes.isFinished == false) {
							return new Result(null, "Request still in progress");
						}

						finalResult = new Result(qRes.result, null);
						if (finalResult.result == null) {
							return new Result(null, "No link with domain " + query + " has been scanned");
						}
						return finalResult;
					}
				}
				// -------------------------------------------------------------------------------------

				// GET----------------------------------------------------------------------------------
				if (domenResultMap.contains(query)) {
					if (domenResultMap.get(query) == null) {
						finalResult = new Result(mergeLinksToDomain(), null);
						if (finalResult.result == null) {
							return new Result(null, "No link with domain " + query + " has been scanned");
						}
						return finalResult;
					}
					finalResult = new Result(domenResultMap.get(query), null);
					return finalResult;
				} else {
					finalResult = new Result(mergeLinksToDomain(), null);
					if (finalResult.result == null) {
						return new Result(null, "No link with domain " + query + " has been scanned");
					}
					return finalResult;
				}
				// ---------------------------------------------------------------------------------------
			}

		}

		return new Result(null, "Could not find result for your request");
	}

	private Map<String, Integer> mergeLinksToDomain() {
		Map<String, Integer> resultMap = null;

		try {
			for (String key : linkResultMap.keySet()) {
				try {
					URL aURL = new URL(key);
					String domain = aURL.getHost();
					if (domain.startsWith("www.")) {
						domain = domain.substring(4);
					}

					if (domain.equals(query)) {
						Map<String, Integer> linkRes = linkResultMap.get(key).get();
						resultMap = initMap(resultMap);
						resultMap = merge(resultMap, linkRes);
					}
				} catch (Exception e) {
					System.err.println("Small Merge Error: " + e.getMessage());
				}
			}
			domenResultMap.put(query, resultMap);
		} catch (Exception e) {
			System.err.println("Big Merge Error: " + e.getMessage());
		}
		return resultMap;
	}

	private QueryResult queryMergeLinksToDomain() {
		Map<String, Integer> resultMap = null;

		try {
			for (String key : linkResultMap.keySet()) {
				try {
					URL aURL = new URL(key);
					String domain = aURL.getHost();
					if (domain.startsWith("www.")) {
						domain = domain.substring(4);
					}

					if (domain.equals(query)) {
						Future<Map<String, Integer>> res = linkResultMap.get(key);
						if(!res.isDone()) {
							return new QueryResult(false, resultMap);
						}
						Map<String, Integer> linkRes = res.get();
						resultMap = initMap(resultMap);
						resultMap = merge(resultMap, linkRes);
					}
				} catch (Exception e) {
					System.err.println("Small Merge Error: " + e.getMessage());
				}
			}
			domenResultMap.put(query, resultMap);
		} catch (Exception e) {
			System.err.println("Big Merge Error: " + e.getMessage());
		}
		return new QueryResult(true, resultMap);
	}

	private String getDomain(String link) {
		String domain = null;
		try {
			URL aURL = new URL(link);
			domain = aURL.getHost();
			if (domain.startsWith("www.")) {
				domain = domain.substring(4);
			}
		} catch (Exception e) {
			return null;
		}

		return domain;
	}

	private Map<String, Integer> initMap(Map<String, Integer> map) {
		if (map == null) {
			return new HashMap<>();
		}

		return map;
	}

	private Map<String, Integer> merge(Map<String, Integer> m1, Map<String, Integer> m2) {
		Map<String, Integer> returnMap = new HashMap<String, Integer>();

		returnMap.putAll(m1);

		for (String key : m2.keySet()) {
			if (returnMap.containsKey(key)) {
				Integer x = returnMap.get(key) + m2.get(key);
				returnMap.replace(key, x);
			} else {
				returnMap.put(key, m2.get(key));
			}
		}

		return returnMap;
	}

	private Result getQueryFileResult() {
		try {
			if (corpusResultMap.contains(query)) {
				Future<Map<String, Integer>> res = corpusResultMap.get(query);
				if (res.isDone()) {
					return new Result(res.get(), null);
				} else {
					return new Result(null, "Request still in progress");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new Result(null, "Could not find result for your request.. Request work not in progress");
	}

}

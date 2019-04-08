package result_retriever;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import job.ScanType;
import threadSafeObj.ThreadSafeMap;

public class ResultRetriever implements ResultRetrieverScheme {

	private final Lock lock = new ReentrantLock();

	private ExecutorService resRetrieverPool = Executors.newCachedThreadPool();
	public ThreadSafeMap<String, Future<Map<String, Integer>>> corpusResultMap = new ThreadSafeMap<>();
	public ThreadSafeMap<String, Future<Map<String, Integer>>> linkResultMap = new ThreadSafeMap<>();
	public ThreadSafeMap<String, Map<String, Integer>> domenResultMap = new ThreadSafeMap<>();

	public Result fileSummaryResult = null;
	public Result webSummaryResult = null;

	@Override
	public Result getResult(String query) {
		try {
			Future<Result> result = resRetrieverPool
					.submit(new Retriever(corpusResultMap, linkResultMap, domenResultMap, query, "get"));
			return result.get();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	@Override
	public Result queryResult(String query) {
		try {
			Future<Result> result = resRetrieverPool
					.submit(new Retriever(corpusResultMap, linkResultMap, domenResultMap, query, "query"));
			return result.get();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	@Override
	public void clearSummary(ScanType summarytype) {
		if (summarytype.equals(ScanType.FILE)) {
			fileSummaryResult = null;
		} else {
			webSummaryResult = null;
		}

	}

	@Override
	public Result getSummary(ScanType summaryType) {
		try {
			if (summaryType.equals(ScanType.FILE) && fileSummaryResult != null) {
				return fileSummaryResult;
			}
			if (summaryType.equals(ScanType.WEB) && webSummaryResult != null) {
				return webSummaryResult;
			}

			Future<Result> result = resRetrieverPool
					.submit(new Retriever(corpusResultMap, linkResultMap, domenResultMap, "get", summaryType));
			if (summaryType.equals(ScanType.FILE)) {
				fileSummaryResult = result.get();
			} else {
				webSummaryResult = result.get();
			}
			return result.get();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	@Override
	public Result querySummary(ScanType summaryType) {
		try {
			if (summaryType.equals(ScanType.FILE) && fileSummaryResult != null) {
				return fileSummaryResult;
			}
			if (summaryType.equals(ScanType.WEB) && webSummaryResult != null) {
				return webSummaryResult;
			}

			Future<Result> result = resRetrieverPool
					.submit(new Retriever(corpusResultMap, linkResultMap, domenResultMap, "query", summaryType));

			if (result.get().message == null) {
				if (summaryType.equals(ScanType.FILE)) {
					fileSummaryResult = result.get();
				} else {
					webSummaryResult = result.get();
				}
			}
			return result.get();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	@Override
	public void addCorpusResult(String corpusName, Future<Map<String, Integer>> corpusResult) {
		lock.lock();
		try {
			resRetrieverPool
					.submit(new Retriever(corpusResultMap, corpusName, corpusResult, ScanType.FILE, domenResultMap));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void addlinkResult(String linkName, Future<Map<String, Integer>> linkResult) {
		lock.lock();
		try {
			resRetrieverPool.submit(new Retriever(linkResultMap, linkName, linkResult, ScanType.WEB, domenResultMap));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			lock.unlock();
		}

	}

}

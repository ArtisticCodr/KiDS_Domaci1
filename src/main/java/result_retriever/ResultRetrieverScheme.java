package result_retriever;

import java.util.Map;
import java.util.concurrent.Future;

import job.ScanType;

public interface ResultRetrieverScheme {

	public Result getResult(String query);

	public Result queryResult(String query);

	public void clearSummary(ScanType summarytype);

	public Result getSummary(ScanType summaryType);

	public Result querySummary(ScanType summaryType);

	public void addCorpusResult(String corpusName, Future<Map<String, Integer>> corpusResult);
	
	public void addlinkResult(String linkName, Future<Map<String, Integer>> linkResult);

}

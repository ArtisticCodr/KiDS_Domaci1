package result_retriever;

import java.util.Map;

public class QueryResult {
	public boolean isFinished = false;
	public Map<String, Integer> result;

	public QueryResult(boolean isFinished, Map<String, Integer> result) {
		this.isFinished = isFinished;
		this.result = result;
	}

}

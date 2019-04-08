package result_retriever;

import java.util.Map;

public class Result {

	public Map<String, Integer> result = null;
	public String message = null;

	public volatile boolean isSummary;
	public Map<String, Map<String, Integer>> summaryResult = null;

	public Result(Map<String, Integer> result, String message) {
		this.result = result;
		this.message = message;
		this.isSummary = false;
	}

	public Result(Map<String, Map<String, Integer>> summaryResult, String message, boolean isSummary) {
		this.summaryResult = summaryResult;
		this.message = message;
		this.isSummary = isSummary;
	}

	@Override
	public String toString() {
		if (!isSummary) {
			if (message != null) {
				return message;
			} else {
				return result.toString();
			}
		} else {
			if (message != null) {
				return message;
			} else {
				StringBuilder sb = new StringBuilder();
				for (String key : summaryResult.keySet()) {
					if (!key.equals(""))
						sb.append(key + summaryResult.get(key) + "\n");
				}

				if (sb.toString().isEmpty()) {
					return "Nothing to summarize yet";
				}
				return sb.toString();
			}
		}
	}

}

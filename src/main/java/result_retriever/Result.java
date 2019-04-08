package result_retriever;

import java.util.Map;

public class Result {

	public Map<String, Integer> result = null;
	public String message = null;

	public Result(Map<String, Integer> result, String message) {
		this.result = result;
		this.message = message;
	}

	@Override
	public String toString() {
		if (message != null) {
			return message;
		} else {
			return result.toString();
		}
	}

}

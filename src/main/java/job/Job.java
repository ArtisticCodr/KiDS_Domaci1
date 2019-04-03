package job;

import java.util.Map;
import java.util.concurrent.Future;

public class Job implements ScanningJob {

	private ScanType scanType;
	private String query;

	public Job(ScanType scanType, String query) {
		super();
		this.scanType = scanType;
		this.query = query;
	}

	@Override
	public ScanType getType() {
		return this.scanType;
	}

	@Override
	public String getQuery() {
		return this.query;
	}

	@Override
	public Future<Map<String, Integer>> initiate() {
		// TODO Auto-generated method stub
		return null;
	}

}

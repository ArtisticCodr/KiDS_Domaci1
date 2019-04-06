package job;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import cli.SharedObjCollection;
import file_scanner.FileScanner;

public class Job implements ScanningJob {

	private ScanType scanType;
	private String query;
	private SharedObjCollection sharedColl;

	public Job(ScanType scanType, String query, SharedObjCollection sharedColl) {
		super();
		this.scanType = scanType;
		this.query = query;
		this.sharedColl = sharedColl;
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

		if (this.scanType.equals(ScanType.FILE)) {
			Future<Map<String, Integer>> result = sharedColl.fileScannerPool
					.submit(new FileScanner(this.query, sharedColl.file_scanning_size_limit, sharedColl.keywords));

		} else {
			// ovde stavljamo u pool za WEB
		}

		return null;
	}

}

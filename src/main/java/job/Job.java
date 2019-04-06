package job;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import cli.SharedObjCollection;
import file_scanner.FileScanner;
import web_scanner.WebScanner;

public class Job implements ScanningJob {

	private ScanType scanType;
	private String query;
	private SharedObjCollection sharedColl;
	private int hopCount;

	public Job(ScanType scanType, String query, SharedObjCollection sharedColl) {
		super();
		this.scanType = scanType;
		this.query = query;
		this.sharedColl = sharedColl;
	}

	public Job(ScanType scanType, String query, SharedObjCollection sharedColl, int hopCount) {
		super();
		this.scanType = scanType;
		this.query = query;
		this.sharedColl = sharedColl;
		this.hopCount = hopCount;
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
		Future<Map<String, Integer>> result;

		if (this.scanType.equals(ScanType.FILE)) {
			result = sharedColl.submitToFileScannerPool(
					new FileScanner(this.query, sharedColl.getFile_scanning_size_limit(), sharedColl.getKeywords()));
		} else {
			result = sharedColl.submitToWebScannerService(new WebScanner(this.hopCount, this.query, sharedColl.jobQueue,
					sharedColl.getKeywords(), sharedColl));
		}

		// brisi----------------------------------------------------
		try {
			Map<String, Integer> numbers = result.get();

			System.out.println(numbers);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		// ---------------------------------------------------------

		return result;
	}

}

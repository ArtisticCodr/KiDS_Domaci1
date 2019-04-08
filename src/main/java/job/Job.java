package job;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cli.SharedObjCollection;
import file_scanner.FileScanner;
import web_scanner.WebScanner;

public class Job implements ScanningJob {

	private ScanType scanType;
	private String query;
	private SharedObjCollection sharedColl;
	private int hopCount;
	private final Lock lock = new ReentrantLock();
	private boolean poison = false;

	public Job(boolean poison) {
		this.poison = poison;
	}

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

	public boolean isPoinson() {
		lock.lock();
		boolean value = this.poison;
		lock.unlock();
		return value;
	}

	@Override
	public ScanType getType() {
		lock.lock();
		ScanType value = this.scanType;
		lock.unlock();
		return value;
	}

	@Override
	public String getQuery() {
		lock.lock();
		String value = this.query;
		lock.unlock();
		return value;
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

		return result;
	}

}

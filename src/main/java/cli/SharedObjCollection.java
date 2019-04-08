package cli;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.directory.DirContext;

import file_scanner.FileScanner;
import job.Job;
import result_retriever.ResultRetriever;
import threadSafeObj.Connector;
import threadSafeObj.ThreadSafeList;
import threadSafeObj.ThreadSafePoolStack;
import threadSafeObj.ThreadSafeStack;
import web_scanner.WebScanner;

public class SharedObjCollection {

	public ThreadSafePoolStack<Byte> activePoolCount = new ThreadSafePoolStack<Byte>();

	private final Lock lock = new ReentrantLock();

	// config
	final private String[] keywords;
	final private String file_corpus_prefix;
	final private long dir_crawler_sleep_time;
	final private long file_scanning_size_limit;
	final private int hop_count;
	final private long url_refresh_time;

	// stop
	private final Lock stopLock = new ReentrantLock();
	private boolean stop = false;

	public SharedObjCollection(String[] keywords, String file_corpus_prefix, long dir_crawler_sleep_time,
			long file_scanning_size_limit, int hop_count, long url_refresh_time) {
		this.keywords = keywords;
		this.file_corpus_prefix = file_corpus_prefix;
		this.dir_crawler_sleep_time = dir_crawler_sleep_time;
		this.file_scanning_size_limit = file_scanning_size_limit;
		this.hop_count = hop_count;
		this.url_refresh_time = url_refresh_time;

		resultRetriever = new ResultRetriever(this);
	}

	// shared obj
	public ThreadSafeList<String> directoriesList = new ThreadSafeList<String>();
	public BlockingQueue<Job> jobQueue = new LinkedBlockingQueue<Job>();

	// File scanner thread pool
	private ForkJoinPool fileScannerPool = new ForkJoinPool();

	// Web scanner thread pool
	private ExecutorService webScannerPool = Executors.newCachedThreadPool();
	public ThreadSafeList<String> scanedUrls = new ThreadSafeList<String>();
	public Connector connector = new Connector();

	// ResultRetriever
	public ResultRetriever resultRetriever;

	public Future<Map<String, Integer>> submitToFileScannerPool(FileScanner fileScanner) {
		lock.lock();
		Future<Map<String, Integer>> retVal = null;

		try {
			retVal = fileScannerPool.submit(fileScanner);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public Future<Map<String, Integer>> submitToWebScannerService(WebScanner webScanner) {
		lock.lock();
		Future<Map<String, Integer>> retVal = null;

		try {
			retVal = webScannerPool.submit(webScanner);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return retVal;
	}

	public String[] getKeywords() {
		lock.lock();
		String[] retVal = null;

		try {
			retVal = new String[keywords.length];
			for (int i = 0; i < keywords.length; i++) {
				retVal[i] = keywords[i];
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public String getFile_corpus_prefix() {
		lock.lock();
		String retVal = null;

		try {
			retVal = new String(file_corpus_prefix);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public long getDir_crawler_sleep_time() {
		lock.lock();
		long retVal = 0;

		try {

			retVal = dir_crawler_sleep_time;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public long getFile_scanning_size_limit() {
		lock.lock();
		long retVal = 0;

		try {

			retVal = file_scanning_size_limit;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public int getHop_count() {
		lock.lock();
		int retVal = 0;

		try {

			retVal = hop_count;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public long getUrl_refresh_time() {
		lock.lock();
		long retVal = 0;

		try {

			retVal = url_refresh_time;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		return retVal;
	}

	public boolean isStoped() {
		stopLock.lock();
		boolean retVal = false;

		try {
			retVal = stop;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			stopLock.unlock();
		}

		return retVal;
	}

	public void stop() {
		stopLock.lock();

		try {
			stop = true;
			Job poisonJob = new Job(true);
			jobQueue.put(poisonJob);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			stopLock.unlock();
		}

		shutdownPools();
	}

	private void shutdownPools() {
		while (!activePoolCount.isEmpty()) {
			System.out.println("Cant stop now.. active threads: " + activePoolCount.size());
		}

		fileScannerPool.shutdown();
		webScannerPool.shutdown();
		resultRetriever.stop();

		System.out.println("Pools stopped..");
	}

}

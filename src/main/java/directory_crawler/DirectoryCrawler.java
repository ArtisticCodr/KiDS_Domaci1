package directory_crawler;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import javax.naming.directory.DirContext;

import cli.SharedObjCollection;
import job.Job;
import job.ScanType;
import threadSafeObj.ThreadSafeList;

public class DirectoryCrawler implements Runnable {

	private String file_corpus_prefix;
	private long dir_crawler_sleep_time;
	private ThreadSafeList<String> directoriesList;
	private BlockingQueue<Job> jobQueue;
	private SharedObjCollection sharedColl;

	public DirectoryCrawler(SharedObjCollection sharedColl) {
		this.file_corpus_prefix = sharedColl.getFile_corpus_prefix();
		this.dir_crawler_sleep_time = sharedColl.getDir_crawler_sleep_time();
		this.directoriesList = sharedColl.directoriesList;
		this.jobQueue = sharedColl.jobQueue;
		this.sharedColl = sharedColl;
	}

	@Override
	public void run() {

		while (sharedColl.isStoped() == false) {

			// prolazimo kroz sve direktoriume koje imamo zadato
			for (int i = 0; i < directoriesList.size(); i++) {
				searchDirectory(directoriesList.get(i));
			}

			try {
				Thread.sleep(dir_crawler_sleep_time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		System.out.println("Directory crawler finished...");
	}

	private void searchDirectory(String directoryName) {
		File directory = new File(directoryName);

		if (directory.getName().startsWith(file_corpus_prefix)) {
			createJob(directory);
			return;
		}

		File[] fList = directory.listFiles();
		if (fList != null)
			for (File file : fList) {
				if (file.isDirectory()) {
					if (file.getName().startsWith(file_corpus_prefix)) {
						createJob(file);
					} else {
						searchDirectory(file.getAbsolutePath());
					}
				}
			}
	}

	// direktoriumi za koje smo vec napravili Job.. key->path ; value->last_modified
	private HashMap<String, Long> corpus_directories = new HashMap<String, Long>();

	// zbog windowsa
	private HashMap<String, Long> file_directories = new HashMap<String, Long>();

	private void createJob(File dirFile) {

		// proveravamo dal se promenio TimeStamp ako ga ima
		if (corpus_directories.containsKey(dirFile.getAbsolutePath())) {
			if (corpus_directories.get(dirFile.getAbsolutePath()) == dirFile.lastModified()) {
				if (isTimestampChanged(dirFile) == false)
					return;
			}
		}

		// napravi Job
		Job job = new Job(ScanType.FILE, dirFile.getAbsolutePath(), sharedColl);
		// stavi Job u queue
		try {
			jobQueue.put(job);
		} catch (Exception e) {
			System.err.println("<DirectoryCrawler> Error: Cant put File job in jobQueue");
			return;
		}

		// ubacujemo directory sa TimeStamp-om
		corpus_directories.put(dirFile.getAbsolutePath(), dirFile.lastModified());
		saveFileTimestamps(dirFile);
	}

	// zbog windowsa
	private void saveFileTimestamps(File dirFile) {
		File[] files = dirFile.listFiles();
		if (files != null) {
			for (File file : files) {
				file_directories.put(file.getAbsolutePath(), file.lastModified());
			}
		}
	}

	// zbog windowsa
	private boolean isTimestampChanged(File dirFile) {
		File[] files = dirFile.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file_directories.get(file.getAbsolutePath()) != file.lastModified()) {
					return true;
				}
			}
			return false;
		}

		return true;
	}

}

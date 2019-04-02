package directory_crawler;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

import cli.SharedObjCollection;
import job.Job;
import job.ScanType;
import threadSafeObj.ThreadSafeList;

public class DirectoryCrawler implements Runnable {

	private String file_corpus_prefix;
	private long dir_crawler_sleep_time;
	private ThreadSafeList<String> directoriesList;
	private BlockingQueue<Job> jobQueue;

	public DirectoryCrawler(SharedObjCollection sharedColl) {
		this.file_corpus_prefix = sharedColl.file_corpus_prefix;
		this.dir_crawler_sleep_time = sharedColl.dir_crawler_sleep_time;
		this.directoriesList = sharedColl.directoriesList;
		this.jobQueue = sharedColl.jobQueue;
	}

	@Override
	public void run() {

		while (true) {

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

	private void createJob(File dirFile) {

		// proveravamo dal se promenio TimeStamp ako ga ima
		if (corpus_directories.containsKey(dirFile.getAbsolutePath())) {
			if (corpus_directories.get(dirFile.getAbsolutePath()) == dirFile.lastModified()) {
				return;
			}
		}

		System.out.println("Starting file scan for file|" + dirFile.getName());
		// napravi Job
		Job job = new Job(ScanType.FILE, dirFile.getAbsolutePath());
		// stavi Job u queue
		try {
			jobQueue.put(job);
		} catch (Exception e) {
			System.err.println("<DirectoryCrawler> Error: Cant put File job in jobQueue");
			return;
		}

		// ubacujemo directory sa TimeStamp-om
		corpus_directories.put(dirFile.getAbsolutePath(), dirFile.lastModified());
	}

}

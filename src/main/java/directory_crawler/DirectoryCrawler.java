package directory_crawler;

import java.io.File;
import java.util.HashMap;

import cli.SharedObjCollection;
import threadSafeObj.ThreadSafeList;

public class DirectoryCrawler implements Runnable {

	private String file_corpus_prefix;
	private long dir_crawler_sleep_time;
	private ThreadSafeList<String> directoriesList;

	public DirectoryCrawler(SharedObjCollection sharedColl) {
		this.file_corpus_prefix = sharedColl.file_corpus_prefix;
		this.dir_crawler_sleep_time = sharedColl.dir_crawler_sleep_time;
		this.directoriesList = sharedColl.directoriesList;
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
		// proveravamo dal se promenio timestamp
		if (corpus_directories.containsKey(dirFile.getAbsolutePath())) {
			if (corpus_directories.get(dirFile.getAbsolutePath()) == dirFile.lastModified()) {
				return;
			}
		}

		System.out.println("<Directory Crawler> creating job for: " + dirFile.getAbsolutePath());
		// napravi Job
		// stavi Job u queue

		// ubacujemo directory sa last modifeied podatkom
		corpus_directories.put(dirFile.getAbsolutePath(), dirFile.lastModified());
	}

}

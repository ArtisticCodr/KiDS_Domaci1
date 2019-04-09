package cli;

import java.io.File;
import java.util.Scanner;

import directory_crawler.DirectoryCrawler;
import job_dispatcher.JobDispatcher;
import web_scanner.UrlCleaner;

public class Main {

	static String[] keywords = new String[0];
	static String file_corpus_prefix = new String();
	static long dir_crawler_sleep_time = 0;
	static long file_scanning_size_limit = 0;
	static int hop_count = 0;
	static long url_refresh_time = 0;
	static SharedObjCollection sharedColl;

	public static void main(String[] args) {
		// reading config file
		readConfig();

		// init shared objects
		sharedColl = new SharedObjCollection(keywords, file_corpus_prefix, dir_crawler_sleep_time,
				file_scanning_size_limit, hop_count, url_refresh_time);

		// starting threads
		new Thread(new DirectoryCrawler(sharedColl)).start();
		new Thread(new JobDispatcher(sharedColl)).start();
		new Thread(new UrlCleaner(sharedColl)).start();

		// scanning user commands
		scanCommands();
		System.out.println("CLI finished..");
	}

	public static void scanCommands() {
		CommandManger cm = new CommandManger(sharedColl);
		System.out.println("Enter Command:");
		Scanner sc = new Scanner(System.in);
		while (true) {
			String line = sc.nextLine();

			if (line.startsWith("ad ")) {
				cm.ad(line);
				continue;
			}
			if (line.startsWith("aw ")) {
				cm.aw(line);
				continue;
			}
			if (line.startsWith("get ")) {
				cm.get(line);
				continue;
			}
			if (line.startsWith("query ")) {
				cm.query(line);
				continue;
			}
			if (line.equals("cws")) {
				cm.cws();
				continue;
			}
			if (line.equals("cfs")) {
				cm.cfs();
				continue;
			}
			if (line.equals("stop")) {
				cm.stop();
				break;
			}

			System.err.println("Nepostojeca komanda sa datim brojem argumenata");
		}
		sc.close();
	}

	public static void readConfig() {
		System.out.println("Scanning configuration file..");
		try {
			Scanner sc = new Scanner(new File("resources/app.properties"));
			while (sc.hasNextLine()) {
				String str = sc.nextLine();

				if (str.startsWith("keywords=")) {
					str = str.substring(9);
					keywords = str.split(",");
				}
				if (str.startsWith("file_corpus_prefix=")) {
					str = str.substring(19);
					file_corpus_prefix = str;
				}
				if (str.startsWith("dir_crawler_sleep_time=")) {
					str = str.substring(23);
					dir_crawler_sleep_time = Long.parseLong(str);
				}
				if (str.startsWith("file_scanning_size_limit=")) {
					str = str.substring(25);
					file_scanning_size_limit = Long.parseLong(str);
				}
				if (str.startsWith("hop_count=")) {
					str = str.substring(10);
					hop_count = Integer.parseInt(str);
				}
				if (str.startsWith("url_refresh_time=")) {
					str = str.substring(17);
					url_refresh_time = Long.parseLong(str);
				}

			}

			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Scanning finished");
	}

}

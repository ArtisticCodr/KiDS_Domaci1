package cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

	static String[] keywords = new String[0];
	static String file_corpus_prefix = new String();
	static long dir_crawler_sleep_time = 0;
	static long file_scanning_size_limit = 0;
	static int hop_count = 0;
	static long url_refresh_time = 0;

	public static void main(String[] args) {
		readConfig();

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

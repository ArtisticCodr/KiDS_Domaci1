package cli;

import directory_crawler.DirectoriesList;

public class SharedObjCollection {

	// config
	public String[] keywords = new String[0];
	public String file_corpus_prefix = new String();
	public long dir_crawler_sleep_time = 0;
	public long file_scanning_size_limit = 0;
	public int hop_count = 0;
	public long url_refresh_time = 0;

	public SharedObjCollection(String[] keywords, String file_corpus_prefix, long dir_crawler_sleep_time,
			long file_scanning_size_limit, int hop_count, long url_refresh_time) {
		this.keywords = keywords;
		this.file_corpus_prefix = file_corpus_prefix;
		this.dir_crawler_sleep_time = dir_crawler_sleep_time;
		this.file_scanning_size_limit = file_scanning_size_limit;
		this.hop_count = hop_count;
		this.url_refresh_time = url_refresh_time;
	}

	// shared obj
	public DirectoriesList directoiesList = new DirectoriesList();

}

package cli;

public class CommandManger {

	private String[] keywords = new String[0];
	private String file_corpus_prefix = new String();
	private long dir_crawler_sleep_time = 0;
	private long file_scanning_size_limit = 0;
	private int hop_count = 0;
	private long url_refresh_time = 0;

	public CommandManger(String[] keywords, String file_corpus_prefix, long dir_crawler_sleep_time,
			long file_scanning_size_limit, int hop_count, long url_refresh_time) {
		this.keywords = keywords;
		this.file_corpus_prefix = file_corpus_prefix;
		this.dir_crawler_sleep_time = dir_crawler_sleep_time;
		this.file_scanning_size_limit = file_scanning_size_limit;
		this.hop_count = hop_count;
		this.url_refresh_time = url_refresh_time;
	}

	public void ad(String line) {
		try {
			line = line.substring(3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void aw(String line) {
		try {
			line = line.substring(3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void get(String line) {
		try {
			line = line.substring(4);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void query(String line) {
		try {
			line = line.substring(6);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cws() {

	}

	public void cfs() {

	}

	public void stop() {

	}
}

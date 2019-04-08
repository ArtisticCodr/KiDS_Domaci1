package web_scanner;

import cli.SharedObjCollection;
import threadSafeObj.ThreadSafeList;

public class UrlCleaner implements Runnable {

	private ThreadSafeList<String> scanedUrls;
	private long urlRefreshTime;
	private SharedObjCollection sharedColl;

	public UrlCleaner(SharedObjCollection sharedColl) {
		this.scanedUrls = sharedColl.scanedUrls;
		this.urlRefreshTime = sharedColl.getUrl_refresh_time();
		this.sharedColl = sharedColl;
	}

	private long startTime = 0;

	@Override
	public void run() {
		startTime = System.currentTimeMillis();
		while (sharedColl.isStoped() == false) {
			try {
				if (urlRefreshTime >= (startTime - System.currentTimeMillis())) {
					scanedUrls.clear();
					startTime = System.currentTimeMillis();
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}

		}
		System.out.println("UrlCleaner finished..");
	}

}

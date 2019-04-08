package cli;

import java.util.Map;

import job.Job;
import job.ScanType;
import result_retriever.Result;

public class CommandManger {

	SharedObjCollection sharedColl;

	public CommandManger(SharedObjCollection sharedColl) {
		this.sharedColl = sharedColl;
	}

	public void ad(String line) {
		try {
			line = line.substring(3);
			System.out.println("Adding dir " + line);
			sharedColl.directoriesList.add(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void aw(String line) {
		try {
			line = line.substring(3);

			// ubacuje novi job u JobQueue
			Job job = new Job(ScanType.WEB, line, sharedColl, sharedColl.getHop_count());
			sharedColl.jobQueue.put(job);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void get(String line) {
		try {
			if (line.contentEquals("get web|summary")) {
				Result result = sharedColl.resultRetriever.getSummary(ScanType.WEB);
				System.out.println(result);
				return;
			}
			if (line.contentEquals("get file|summary")) {
				Result result = sharedColl.resultRetriever.getSummary(ScanType.FILE);
				System.out.println(result);
				return;
			}

			line = line.substring(4);
			if (line.startsWith("file|") || line.startsWith("web|")) {
				Result result = sharedColl.resultRetriever.getResult(line);
				System.out.println(result);
			} else {
				System.err.println("Nepostojeca komanda sa datim brojem argumenata");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void query(String line) {
		try {
			if (line.contentEquals("query web|summary")) {
				Result result = sharedColl.resultRetriever.querySummary(ScanType.WEB);
				System.out.println(result);
				return;
			}
			if (line.contentEquals("query file|summary")) {
				Result result = sharedColl.resultRetriever.querySummary(ScanType.FILE);
				System.out.println(result);
				return;
			}

			line = line.substring(6);
			if (line.startsWith("file|") || line.startsWith("web|")) {
				Result result = sharedColl.resultRetriever.queryResult(line);
				System.out.println(result);
			} else {
				System.err.println("Nepostojeca komanda sa datim brojem argumenata");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cws() {
		try {
			sharedColl.resultRetriever.clearSummary(ScanType.WEB);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cfs() {
		try {
			sharedColl.resultRetriever.clearSummary(ScanType.FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		sharedColl.stop();
	}
}

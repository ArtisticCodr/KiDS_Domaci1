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

	}

	public void cfs() {

	}

	public void stop() {

	}
}

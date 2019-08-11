# Keyword counter (concurrent and distributed systems)
The Keyword counter system supports counting the impressions of keywords that are upfront
given (in the configuration file) in corps of different type. This counting is concurrent with
the ability to add new corps as well as view the counting results for individual ones and summarizing the results. 
This implementation works for ASCII encoded text files and HTML files located on the web.
Keywords in these corpora are counted only when standing alone, not when they are part of other words,
and the search should be case-sensitive.
The user interacts with the system via the command line (CLI), by entering commands.

# System description
The system consists of three thread pool based components and several auxiliaries
components that execute each in its own thread.<br> <br>
They have their thread pool:<br>
● Web Scanner<br>
● File Scanner<br>
● Result Retriever<br><br>
While in individual threads, they execute:<br>
● Main / CLI<br>
● Job dispatcher<br>
● Directory crawler<br><br>
In addition to these active components, there is one (job queue) that is used to assign new
jobs and starting them. <br>The system can be represented graphically as follows:<br>
![alt text](https://raw.githubusercontent.com/ArtisticCodr/KiDS_Keyword_counter/master/img/graph.png)<br><br>

# Configuration file
The system is configured using the app.properties configuration file, which has the following
parameters:<br><br>
 list of keywords searched for in corpus, comma separated.<br>
 keywords are not counted in corpora if they are part of another word,<br>
 only when standing alone in the text<br>
● keywords = one, two, three<br><br>
 prefix for directories containing text corpora<br>
● file_corpus_prefix = corpus_<br><br>
 period of pauses for directory crawler, given in ms<br>
● dir_crawler_sleep_time = 1000<br><br>
 limit for file scanner component, given in bytes<br>
● file_scanning_size_limit = 1048576<br><br>
 the number of jumps the web scanner component will make when searching<br>
● hop_count = 1<br><br>
time after which the set of visited URLs, given in ms, is deleted<br>
● url_refresh_time = 86400000<br><br><br>
All of these parameters are read once at application startup, and will not change during operation. The only way to change the values of these parameters is to restart the application.

# Commands
Command Name: ad<br>
Parameter: String<br>
Description: Add directory. <br>Adds a new scan directory, which is passed to the Directory scanner
components. The directory is located within the project, and is given as a relative path. Inside this one
directories can be arbitrarily many subdirectories, but some of them should have one
a name that begins with the prefix specified in the configuration file (file_corpus_prefix), and
they represent the text corpora for our system.<br><br>

Command Name: aw<br>
Parameter: String<br>
Description: Add web. <br>Adds a page from which to start a new tour. Number of jumps for this job is taken from the configuration file (hop_count). For each URL found on the page
a new job will be created that will have a jump count of one less than the current one. For each
a page that is visited like this is counted by keyword impressions, and stored in the result retriever
components.<br><br>

Command name: get<br>
Parameter: String<br>
Description: Retrieves the result from the Result retriever component and prints it on the console. As an argument
states the query (described in Section 2.5). This command blocks further work until it receives a result.<br><br>

Command name: query<br>
Parameter: String<br>
Description: Retrieves the result from the Result retriever component and prints it on the console. As an argument
states the query. This command does not block further work, only prints a message
if results are not available or if the job for that query has not yet been started.<br><br>

Command name: cws / cfs<br>
Parameter: -<br>
Description: Clear web summary / Clear file summary. These two commands are specified without arguments and
when specified, the Result retriever components should be reported to delete the corresponding result summary, if any is preserved.<br><br>

Command name: stop<br>
Parameter: -<br>
Description: Turn off the application. Stops all thread pools and tells all threads to finish properly.<br><br>

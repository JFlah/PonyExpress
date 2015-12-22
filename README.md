# Pony-Express
A web crawler that takes in a link to a webpage and uses an algorithm to search all reachable pages from that link. It will return back a list of all of the emails listed on the website.

Setup:

1) Download PhantomJS at http://phantomjs.org/download.html

2) Extract the zip files and edit line 26 in Crawl to be the path to your particular .exe

3) Download Selenium Client & WebDriver Language Bindings (Java) at http://www.seleniumhq.org/download/

4) Extract the zip files and add selenium-java-2.48.2.jar as a dependency

5) Add the libs folder within your selenium folder as a Library dependency

6) usage: Java Crawl <link>

NOTE: My particular IDE has given me issues upon trying to run from a command line.
	The error is along the lines of "package <some selenium package> does not exist"
	Depending on your IDE and/or where your dependencies go in your file structure,
	you may need to do some experimenting.

If you are unable to get your dependencies found via the command-line usage,
simply comment out the command-line section in Crawl.main and
uncomment the IDE-run section.

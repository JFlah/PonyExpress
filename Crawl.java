/**
 * Created by Jack on 12/18/2015.
 * PonyExpress delivers (e)mail(s)!
 */
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.*;

public class Crawl {

    private static Queue<String> pageQ = new LinkedList<String>();      // queue holding our pages during the BFS

    private static HashSet<String> pageHash = new HashSet<String>();    // HashSet to check for pages we have already visited

    private static HashSet<String> emailHash = new HashSet<String>();   // HashSet for emails because I can only add 1 of each to this structure

    private static String fullLink;                                     // https:// + siteToScrape

                                                                        // CHANGE BELOW TO YOUR PHANTOMJS PATH
    /******************************************************************************************************************************************************************/
    private static final String PATH_TO_PHANTOMJS = "C:/Users/Jack/Desktop/Nerd-Stuff/Java Libraries/phantomjs-2.0.0-windows/phantomjs-2.0.0-windows/bin/phantomjs.exe";
    /******************************************************************************************************************************************************************/

    private static DesiredCapabilities caps = new DesiredCapabilities(); // selenium start

    private static WebDriver driver;                                     // browser driver

    public static void main(String[] args) {
                                                                         // Merge PhantomJS driver to Selenium
        caps.setJavascriptEnabled(true);
        caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, PATH_TO_PHANTOMJS);
        driver =  new PhantomJSDriver(caps);

                                                                         // Get input and make it into a full link

//                                                                        // USE THIS SECTION FOR IDE RUNS ///////
//        Scanner sc = new Scanner(System.in);                            ////////////////////////////////////////
//        System.out.print("Enter site: http://");                        ////////////////////////////////////////
//        String siteToScrape = sc.next();                                // String for homepage of site you want emails from
//        fullLink = "http://" + siteToScrape;                            ////////////////////////////////////////
//        pageQ.add(fullLink);                                            // first in BFS q ////END IDE RUN SXN///

                                                                        // USE THIS SECTION FOR COMMAND LINE RUNS
        if (args[0].length() < 3 || args.length != 1) {                 /////////////////////////////////////////
            System.out.println("Usage: java Crawl <link>");             /////////////////////////////////////////
            System.exit(1);                                             /////////////////////////////////////////
        }                                                               /////////////////////////////////////////
        String siteToScrape = args[0];                                  /////////////////////////////////////////
        if (!siteToScrape.startsWith("http://")) {                      /////////////////////////////////////////
            siteToScrape = "http://" + siteToScrape;                    /////////////////////////////////////////
        }                                                               /////////////////////////////////////////
        pageQ.add(siteToScrape);                                        ///////END COMMAND LINE SECTION//////////

        BFS();                                                          // Begin the search

        int count = 1;
        if (!emailHash.isEmpty()) {                                     // If our Hashset of emails is not empty.. print them
            System.out.println("\nEmails found: ");
            for (Object email : emailHash) {
                System.out.println(count + ") " + email.toString());
                count++;
            }
        } else
            System.out.println("\nNo emails found on " + siteToScrape);
            
        driver.close();                                                 // stop PhantomJS driver
        driver.quit();                                                  // quit totally
    }

    /*
     * Function will perform a breadth-first search on a queue of links
     * which begins only with the home page link.
     * When it finds that a new page has anchor and/or div tags
     * that contain a reference to another page of the site
     * it will perform the necessary functions to add
     * the full and correct link to the pageQ
     * and continue the BFS.
     */
    private static void BFS() {
        StringBuilder stb = new StringBuilder();
        System.out.print("Searching: ");

        while (pageQ.size() > 0) {
            stb.append("$");
            System.out.print(stb.toString());                           // for laughs
            String currentLink = pageQ.remove();                        // get next link
            if (!pageHash.contains(currentLink)) {                      // if !visited
                pageHash.add(currentLink);                              // mark visited
                driver.get(currentLink);                                // Get contents

                                                                        // get all anchor tags and div tags on page
                List<WebElement> aList = driver.findElements(By.tagName("a"));
                List<WebElement> divList = driver.findElements(By.tagName("div"));

                findEmails(aList, divList);                             // get emails from current page

                for (WebElement anchor : aList) {                       // anchor tag logic
                    String attrib = anchor.getAttribute("href");        // get href in <a>
                    if (attrib != null && attrib.startsWith(fullLink) && !pageQ.contains(attrib))
                        pageQ.add(attrib);                              // add if its another page of our site
                                                                        // if something like /contact, build full link then add to Q
                    else if (attrib != null && attrib.startsWith("/")) {
                        String fullAttrib = fullLink + attrib;
                        if (!pageQ.contains(fullAttrib))                // second check for / types to avoid redundancy
                            pageQ.add(fullAttrib);
                    }
                }
                for (WebElement div : divList) {                        // div tag logic
                    String attrib = div.getAttribute("href");           // get href in <div>
                    if (attrib != null && attrib.startsWith(fullLink) && !pageQ.contains(attrib))
                        pageQ.add(attrib);                              // add if its another page of our site
                                                                        // if something like /contact, build full link then add to Q
                    else if (attrib != null && attrib.startsWith("/")){
                        String fullAttrib = fullLink + attrib;
                        if (!pageQ.contains(fullAttrib))                // second check for / types to avoid redundancy
                            pageQ.add(fullAttrib);
                    }
                }

            }
        }
    }


    /*
     * Function will take in a current site page's list of <a> and <div> tags,
     * and (if it is an email)
     * add it to the email HashSet.
     * We will be left with a set of every
     * email from that page.
     *
     * NOTE: I could search the tag By.text
     * and avoid substring concatenation
     * but I am assuming not all websites will have
     * the text of the tag be the actual email
     * HOWEVER, all tags will have the form href="mailto:<email>"
     */
    private static void findEmails(List<WebElement> aList, List<WebElement> dList) {
        for (WebElement anchor : aList) {                               // anchor logic
            String attrib = anchor.getAttribute("href");                // get href from anchor
            if (attrib != null && attrib.contains("mailto:")) {         // if its not null and has mailto: in it
                String email = attrib.substring(7);                     // remove the mailto
                emailHash.add(email);                                   // add it to the email hash
            }
        }
        for (WebElement div : dList) {                                  // div logic, same as anchor ^
            String attrib = div.getAttribute("href");
            if (attrib != null && attrib.contains("mailto:")) {
                String email = attrib.substring(7);
                emailHash.add(email);
            }
        }
    }
}

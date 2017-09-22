/**
 * Assignment 2: Java regular expressions <br />
 * Test cookies using regular expressions
 *
 * Name: Nathan Klapstein
 * ID: 1449872
 *
 */

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CookieTest {
    /**
     * Verify a cookie Expires key's time
     * @param cookie        {@code String}  The cookie string containing a Expires key
     * @return              {@code boolean} True for a legal cookie; false for an illegal one
     */
    public static boolean verifyTime(String cookie){
        System.out.println("testing time'" + cookie + "'");
        try {
            ZonedDateTime zdt =
                    ZonedDateTime.parse(cookie, DateTimeFormatter.RFC_1123_DATE_TIME);
            return true;
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verify a cookie's keys
     * @param cookie        {@code String}  The cookie string
     * @return              {@code boolean} True for a legal cookie; false for an illegal one
     */
    public static boolean verifyCookieKeys(String cookie){

        boolean legalKeys = false;
        // TODO debug
        System.out.println("now looking at '" + cookie + "'");
        // todo deal with httpONly and SECURE case
        String keySearchPattern = "(\\s([^;]+)=([^;]+|\\Z))(\\Z|;)";
        Pattern ksr = Pattern.compile(keySearchPattern);
        Matcher m = ksr.matcher(cookie);
        if (m.find()){
            String key = m.group(2);
            // TODO debug
            System.out.println("Found cookie key: " + key);
            switch (key){
                case "Domain":
                    System.out.println("Found parsing domain");

                    // domain pattern bad
                    String domainPatternBad = "(Domain=(((\\.0)|(0))|(.*)([^A-Za-z0-9]$)))";
                    Pattern dr = Pattern.compile(domainPatternBad);
                    Matcher dm = dr.matcher(cookie);
                    if (dm.find()){
                        return false;
                    }

                case "Max-Age":
                    // max age pattern
                    String maxAgePattern = "(Max-Age=([^0][0-9]+))";
                    Pattern mar = Pattern.compile(maxAgePattern);
                    break;

                case "Expires":
                    // expires-av todo  wkday "," SP date1 SP time SP "GMT"
                    String expiresPattern = "(Expires=)([^;]+)(\\Z|;)";
                    Pattern er = Pattern.compile(expiresPattern);
                    Matcher em = er.matcher(cookie);
                    if (em.find()){
                        legalKeys = verifyTime(em.group(2));
                    }
                    break;

                case "Path":
                    // Path pattern bad
                    String pathPatternBad = "(Path=(;|\\Z|\\s))";
                    Pattern pr = Pattern.compile(pathPatternBad);
                    break;

                case "Secure":
                    // Secure pattern
                    String securePattern = "(Secure)";
                    Pattern sr = Pattern.compile(securePattern);
                    break;

                case "HttpOnly":
                    // HttpOnly pattern
                    String httpOnlyPattern = "(HttpOnly)";
                    Pattern hor = Pattern.compile(httpOnlyPattern);
                    break;

                default:
                    // we did not have a proper match for a key
                    legalKeys = false;
            }
        } else {
            return false;
        }

        return legalKeys;
    }

    /**
     * Verify a cookie and return the verification result
     * @param cookie        {@code String}  The cookie string
     * @return              {@code boolean} True for a legal cookie; false for an illegal one
     */
    public static boolean verifyCookie(String cookie) {
        System.out.println("verifying cookie: " + cookie );

        boolean legal = false;
        //todo
        String tokenPattern = "([^\\x00-\\x1E\\x7F\\]\\[<>/:;?={}@\\(\\)\\s]+=)";
        String cookieOctetPattern_1 = "\"[\\x21\\x23-\\x2B\\x2D-\\x3A\\x3C-\\x5B\\x5D-\\x7E]+\"";
        String cookieOctetPattern_2 = "[\\x21\\x23-\\x2B\\x2D-\\x3A\\x3C-\\x5B\\x5D-\\x7E]+";
        String cookieOctetPattern = "("+cookieOctetPattern_1+"|"+cookieOctetPattern_2+")?";
        String setCookiePattern = "(Set-Cookie: )" + tokenPattern + cookieOctetPattern + "(\\Z|;)";
        Pattern scr = Pattern.compile(setCookiePattern);
        Matcher m = scr.matcher(cookie);
        if (m.find()){
            // TODO debug
            System.out.println("Found cookie name and token: " + m.group(0));
            if (Objects.equals(m.group(4), ";")){
                String cookie_2 = m.replaceAll("");
                legal = verifyCookieKeys(cookie_2);
            } else {
                legal = true;
            }
        } else {
            legal = false;
        }

        // ensure no trailing ;
        Pattern ttr = Pattern.compile("(;$)");
        Matcher ttm = scr.matcher(cookie);
        if (m.find()){
            legal = false;
        }

        return legal;
    }

    /**
     * Main entry
     * @param args          {@code String[]} Command line arguments
     */
    public static void main(String[] args) {
        String [] cookies = {
                // Legal cookies:
                "Set-Cookie: ns1=\"alss/0.foobar^\"",                                           // 01 name=value
                "Set-Cookie: ns1=",                                                             // 02 empty value
                "Set-Cookie: ns1=\"alss/0.foobar^\"; Expires=Tue, 18 Nov 2008 16:35:39 GMT",    // 03 Expires=time_stamp
                "Set-Cookie: ns1=; Domain=",                                                    // 04 empty domain
                "Set-Cookie: ns1=; Domain=.srv.a.com-0",                                        // 05 Domain=host_name
                "Set-Cookie: lu=Rg3v; Expires=Tue, 18 Nov 2008 16:35:39 GMT; Path=/; Domain=.example.com; HttpOnly", // 06
                // Illegal cookies:
                "Set-Cookie:",                                              // 07 empty cookie-pair
                "Set-Cookie: sd",                                           // 08 illegal cookie-pair: no "="
                "Set-Cookie: =alss/0.foobar^",                              // 09 illegal cookie-pair: empty name
                "Set-Cookie: ns@1=alss/0.foobar^",                          // 10 illegal cookie-pair: illegal name
                "Set-Cookie: ns1=alss/0.foobar^;",                          // 11 trailing ";"
                "Set-Cookie: ns1=; Expires=Tue 18 Nov 2008 16:35:39 GMT",   // 12 illegal Expires value
                "Set-Cookie: ns1=alss/0.foobar^; Max-Age=01",               // 13 illegal Max-Age: starting 0
                "Set-Cookie: ns1=alss/0.foobar^; Domain=.0com",             // 14 illegal Domain: starting 0
                "Set-Cookie: ns1=alss/0.foobar^; Domain=.com-",             // 15 illegal Domain: trailing non-letter-digit
                "Set-Cookie: ns1=alss/0.foobar^; Path=",                    // 16 illegal Path: empty
                "Set-Cookie: ns1=alss/0.foobar^; httponly",                 // 17 lower case
        };
        for (int i = 0; i < cookies.length; i++)
            System.out.println(String.format("Cookie %2d: %s", i+1, verifyCookie(cookies[i]) ? "Legal" : "Illegal"));
    }

}
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
     * Exires key time should follow a <rfc1123-date, defined in [RFC2616], Section 3.3.1>
     * @param expiresTime        {@code String}  The string containing a Expires key's time
     * @return              {@code boolean} True for a legal Expires time; false for an illegal one
     */
    private static boolean verifyTime(String expiresTime){
        try {
            ZonedDateTime.parse(expiresTime, DateTimeFormatter.RFC_1123_DATE_TIME);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    /**
     * Verify a basic cookie key using regex
     * @param value        {@code String}  A string defining a cookie key's value
     * @param pattern      {@code String}  A string defining a regex pattern the cookie key should follow
     * @return             {@code boolean} True for a legal (regex wise) cookie key value; false for an illegal one
     */
    private static boolean verifyKeyValue(String value, String pattern) {
        // init the regex pattern and matcher variables
        Pattern r;
        Matcher m;
        r = Pattern.compile(pattern);
        m = r.matcher(value);
        return m.find();
    }


    /**
     * Recursive loop that verifys a cookie's keys
     * @param cookie        {@code String}  The cookie string
     * @return              {@code boolean} True if all the cookie strings keys are correct; false otherwise
     */
    private static boolean verifyCookieKeys(String cookie){
        // init the regex pattern and matcher variables
        Pattern r;
        Matcher m;
        boolean legalKeys = false;

        // search for standard cookie keys "key=value" or key flags "key"
        r = Pattern.compile("^([^;=]+)(=([^;]*|\\Z))?");
        m = r.matcher(cookie);

        if (m.find()){
            String key = m.group(1);
            String value = m.group(3);

            // delete the found key=value pair from the cookie string
            cookie = m.replaceAll("");

            switch (key){
                case "Domain":
                    legalKeys = verifyKeyValue(value, "^(((?=.{4,253})\\.?((?![-0])[a-zA-Z0-9\\-]{1,62}(?<!-)\\.)+((?![-])[a-zA-Z0-9\\-]{2,62}))|\\Z)");
                    break;

                case "Max-Age":
                    legalKeys = verifyKeyValue(value, "^([1-9][0-9]*)");
                    break;

                case "Expires":
                    legalKeys = verifyTime(value);
                    break;

                case "Path":
                    legalKeys = verifyKeyValue(value, "[^;\\x00-\\x1E\\x7F\\]]+");
                    break;

                case "Secure":
                    legalKeys = true;
                    break;

                case "HttpOnly":
                    legalKeys = true;
                    break;

                default:
                    legalKeys = false;
            }
        }

        // if last key check was successful and we have a ";"
        // at the start of the remaining string look for
        // another key and test it
        if (legalKeys){
            r = Pattern.compile("^; ");
            m = r.matcher(cookie);
            if (m.find()) {
                // remove the "; " off the start of the string
                cookie = m.replaceAll("");

                // verify the next cookie key
                legalKeys = verifyCookieKeys(cookie);
            }
        }
        return legalKeys;
    }


    /**
     * Verify a cookie and return the verification result
     * @param cookie        {@code String}  The cookie string
     * @return              {@code boolean} True for a legal cookie; false for an illegal one
     */
    private static boolean verifyCookie(String cookie) {
        // init the regex pattern and matcher variables
        Pattern r;
        Matcher m;
        boolean legalCookie = false;

        // ensure no trailing ; if one is present fail (return false)
        r = Pattern.compile("(;$)");
        m = r.matcher(cookie);
        if (m.find()){
            return false;
        }

        // match the cookie_name=cookie_token pattern
        String tokenPattern = "([^\\x00-\\x1E\\x7F\\]\\[<>/:;?={}@\\(\\)\\s]+=)";
        String cookieOctetPattern_1 = "\"[\\x21\\x23-\\x2B\\x2D-\\x3A\\x3C-\\x5B\\x5D-\\x7E]+\"";
        String cookieOctetPattern_2 = "[\\x21\\x23-\\x2B\\x2D-\\x3A\\x3C-\\x5B\\x5D-\\x7E]+";
        String cookieOctetPattern = "("+cookieOctetPattern_1+"|"+cookieOctetPattern_2+")?";
        String setCookiePattern = "^Set-Cookie: " + tokenPattern + cookieOctetPattern + "(\\Z|; )";

        r = Pattern.compile(setCookiePattern);
        m = r.matcher(cookie);

        if (m.find()){
            // if a cookie_name=cookie_token pattern was found
            // and we have a "; " present verify the cookie's keys
            if (Objects.equals(m.group(3), "; ")){
                cookie = m.replaceAll("");
                legalCookie = verifyCookieKeys(cookie);
            } else {
                legalCookie = true;
            }
        }
        return legalCookie;
    }


    /**
     * Main entry: start of the tests to check valid cookie functionality
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
                // Custom legal cookies
                "Set-Cookie: ns1=\"alss/0.foobar^\"; HttpOnly; Secure",
                // Custom illegal cookies
                "Set-Cookie: ns1=alss/0.foobar^; Path=; HttpOnly",          // 19 bad path
                "Set-Cookie: ns1=alss/0.foobar^; floop=doop",               // 20 bad key
                "Set-Cookie:   sdasd=asdsa",                                // 21 bad whitespace 1
                "Set-Cookie: ns1=; Domain=.02321.ca",
                "Set-Cookie: ns1=; Domain=.12312-.asda",
                "Set-Cookie: ns1=; Domain=.-12312.asda",
                "Set-Cookie: ns1=; Domain=.12312.-asda",
                "Set-Cookie: ns1=; Domain=.12312.asda-",
        };

        for (int i = 0; i < cookies.length; i++)
            System.out.println(String.format("Cookie %2d: %s", i+1, verifyCookie(cookies[i]) ? "Legal" : "Illegal"));
    }
}
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
     * @param cookie        {@code String}  The cookie string containing a Expires key
     * @return              {@code boolean} True for a legal Expires time; false for an illegal one
     */
    public static boolean verifyTime(String cookie){
        try {
            ZonedDateTime.parse(cookie, DateTimeFormatter.RFC_1123_DATE_TIME);
            return true;
        } catch (DateTimeParseException e) {
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
        String keySearchPattern = "(\\s?([^;]+)=([^;]+|\\Z))(\\Z|;)";
        Pattern ksr = Pattern.compile(keySearchPattern);
        Matcher m = ksr.matcher(cookie);
        if (m.find()){
            String key = m.group(2);
            switch (key){
                case "Domain":
                    String domainPattern1 = "(?=.{4,253})\\.?((?!-0)[a-zA-Z0-9\\-]{1,62}(?<!-0)\\.)+([a-zA-Z0-9\\-]{2,63}(?!-))(?=;|\\Z)";
                    String domainPattern = "Domain=(" + domainPattern1 + "|(?=;|\\Z))";
                    Pattern dr = Pattern.compile(domainPattern);
                    Matcher dm = dr.matcher(cookie);
                    if (dm.find()){
                        legalKeys = true;
                        cookie = dm.replaceAll("");
                    }

                case "Max-Age":
                    String maxAgePattern = "Max-Age=([1-9][0-9]*)(?=;|\\Z)";
                    Pattern mar = Pattern.compile(maxAgePattern);
                    Matcher mam = mar.matcher(cookie);
                    if (mam.find()){
                        legalKeys = true;
                        cookie = mam.replaceAll("");
                    }
                    break;

                case "Expires":
                    String expiresPattern = "Expires=([^;]+)(?=;|\\Z)";
                    Pattern er = Pattern.compile(expiresPattern);
                    Matcher em = er.matcher(cookie);
                    if (em.find()){
                        legalKeys = verifyTime(em.group(1));
                        cookie = em.replaceAll("");
                    }
                    break;

                case "Path":
                    // Path pattern
                    String pathPattern = "Path=([^;]+)(?=;|\\Z)";
                    Pattern pr = Pattern.compile(pathPattern);
                    Matcher pm = pr.matcher(cookie);
                    if (pm.find()){
                        legalKeys = true;
                        cookie = pm.replaceAll("");
                    }
                    break;

                default:
                    legalKeys = false;
            }
        } else {
            String keySearchPattern2 = "\\s?([^;]+)";
            Pattern ks2r = Pattern.compile(keySearchPattern2);
            Matcher m2 = ks2r.matcher(cookie);
            if (m2.find()) {
                String key = m2.group(1);
                switch (key) {
                    case "Secure":
                        String securePattern = "Secure(?=;|\\Z)";
                        Pattern sr = Pattern.compile(securePattern);
                        Matcher sm = sr.matcher(cookie);
                        if (m.find()) {
                            legalKeys = true;
                            cookie = sm.replaceAll("");
                        }
                        break;

                    case "HttpOnly":
                        String httpOnlyPattern = "HttpOnly(?=;|\\Z)";
                        Pattern hor = Pattern.compile(httpOnlyPattern);
                        Matcher hom = hor.matcher(cookie);
                        if (hom.find()) {
                            legalKeys = true;
                            cookie = hom.replaceAll("");
                        }
                        break;

                    default:
                        legalKeys = false;
                }
            }
        }

        // if last key check was successful and we have a ; look for
        // another key and test it
        if (legalKeys){
            Pattern hor = Pattern.compile("^\\s*;\\s*");
            Matcher hom = hor.matcher(cookie);
            if (hom.find()) {
                cookie = hom.replaceAll("");
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
    public static boolean verifyCookie(String cookie) {
        boolean legal = false;


        // ensure no trailing ; if one is present fail (return false)
        Pattern ttr = Pattern.compile("(;$)");
        Matcher ttm = ttr.matcher(cookie);
        if (ttm.find()){
            return false;
        }

        // search/match the cookie_name=cookie_token pattern
        String tokenPattern = "([^\\x00-\\x1E\\x7F\\]\\[<>/:;?={}@\\(\\)\\s]+=)";
        String cookieOctetPattern_1 = "\"[\\x21\\x23-\\x2B\\x2D-\\x3A\\x3C-\\x5B\\x5D-\\x7E]+\"";
        String cookieOctetPattern_2 = "[\\x21\\x23-\\x2B\\x2D-\\x3A\\x3C-\\x5B\\x5D-\\x7E]+";
        String cookieOctetPattern = "("+cookieOctetPattern_1+"|"+cookieOctetPattern_2+")?";
        String setCookiePattern = "Set-Cookie: " + tokenPattern + cookieOctetPattern + "(\\Z|;)";
        Pattern scr = Pattern.compile(setCookiePattern);
        Matcher m = scr.matcher(cookie);
        if (m.find()){
            if (Objects.equals(m.group(3), ";")){
                cookie = m.replaceAll("");
                legal = verifyCookieKeys(cookie);
            } else {
                legal = true;
            }
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
//        String [] cookies = {
//
//                "Set-Cookie: lu=Rg3v; Expires=Tue, 18 Nov 2008 16:35:39 GMT; Path=/; Domain=.example.com; HttpOnly", // 06
//
//        };
        for (int i = 0; i < cookies.length; i++)
            System.out.println(String.format("Cookie %2d: %s", i+1, verifyCookie(cookies[i]) ? "Legal" : "Illegal"));
    }
}
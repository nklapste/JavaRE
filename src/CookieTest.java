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


public class CookieTest {




    public static String verifyCookieKeys(String cookie){
        return  cookie;
    }


    /**
     * Verify a cookie and return the verification result
     * @param cookie        {@code String}  The cookie string
     * @return              {@code boolean} True for a legal cookie; false for an illegal one
     */
    public static boolean verifyCookie(String cookie) {

        boolean legal = false;
        //todo

        String tokenPattern = "([^\\x00-\\x1E\\x7F\\]\\[<>/:;?={}@\\(\\)\\s]+=)";
        String cookieOctetPattern = "(\"[\\x21\\x23-\\x2B\\x2D-\\x3A\\x3C-\\x5B\\x5D-\\x7E]+\"|[\\x21\\x23-\\x2B\\x2D-\\x3A\\x3C-\\x5B\\x5D-\\x7E]+|(\\Z|;))";
        String setCookiePattern = "(Set-Cookie: )" + tokenPattern + cookieOctetPattern;
        Pattern scr = Pattern.compile(setCookiePattern);
        Matcher m = scr.matcher(cookie);
        if (m.find()){
            System.out.println("Found value: " + m.group(0) );
            if (Objects.equals(m.group(4), ";")){
                System.out.println("more token");
            } else {
                System.out.println("end of token");
            }
            // todo
            String cookie_2 = m.replaceAll("");
            System.out.println("now looking at " + cookie_2);


            String cookieAvPattern = "([[:blank:]]*)(Expires=|Max-Age=|Domain=|Path=|Secure|HttpOnly)(\\Z|;)";
            Pattern cavr = Pattern.compile(cookieAvPattern);
            Matcher m2 = scr.matcher(cookie);
        }

        // expires-av todo  wkday "," SP date1 SP time SP "GMT"
        String expiresPattern = "(Expires=((W||||), () () () () ()))";
        Pattern er = Pattern.compile(expiresPattern);

        // max age pattern
        String maxAgePattern = "(Max-Age=([^0][0-9]+))";
        Pattern mar = Pattern.compile(maxAgePattern);

        // domain pattern bad
        String domainPatternBad = "(Domain=(((\\.0)|(0))|(.*)([^A-Za-z0-9]$)))";
        Pattern dr = Pattern.compile(domainPatternBad);

        // Path pattern bad
        String pathPatternBad = "(Path=(;|\\Z|\\s))";
        Pattern pr = Pattern.compile(pathPatternBad);

        // Secure pattern
        String securePattern = "(Secure)";
        Pattern sr = Pattern.compile(securePattern);

        // HttpOnly pattern
        String httpOnlyPattern = "(HttpOnly)";
        Pattern hor = Pattern.compile(httpOnlyPattern);


        // ensure no trailing ;
        String trailtest = "(;$)";
        Pattern ttr = Pattern.compile(trailtest);
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
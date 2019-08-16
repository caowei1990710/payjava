package com.example.demo.Utils;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by snsoft on 4/7/2019.
 */
public class FunctionUtil {
    public static String urlEncode(String url) throws UnsupportedEncodingException {
        if(url == null) {
            return null;
        }

        final String reserved_char = ";/?:@=&";
        String ret = "";
        for(int i=0; i < url.length(); i++) {
            String cs = String.valueOf( url.charAt(i) );
            if(reserved_char.contains(cs)){
                ret += cs;
            }else{
                ret += URLEncoder.encode(cs, "utf-8");
            }
        }
        return ret.replace("+", "%20");
    }
    public static boolean updateGoogleAuger(String code,String secret){
        long t = System.currentTimeMillis();
//        String secret = "WLCZW4IOCVHEWF4U";
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(5);
        boolean r = ga.check_code(secret, Long.parseLong(code), t);
        System.out.println("检查code是否正确？" + r);
        if (r)
            return true;
        else
            return false;
    }
}

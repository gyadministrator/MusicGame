package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/12/29.
 */

public class PhoneUtils {
    public static boolean checkPhone(String number) {
        Pattern pattern = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[01236789]))\\d{8}$");
        Matcher matcher = pattern.matcher(number);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
}

package futin.util;

public class TextUtils {

    public static boolean isEmpty(String a) {
        return a == null || a.equals("");
    }

    public static boolean equals(String arg1, String arg2) {
        if (arg1 == null || arg2 == null) {
            return false;
        }
        return arg1.equals(arg2);
    }
}

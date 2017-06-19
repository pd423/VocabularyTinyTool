package futin.util;

import java.io.Closeable;
import java.io.IOException;

public class Utils {

    public static void closeClosable(Closeable object) {
        if (object != null) {
            try {
                object.close();
            } catch (IOException ignore) {
            }
        }
    }
}

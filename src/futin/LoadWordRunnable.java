package futin;

import futin.util.TextUtils;
import futin.util.Utils;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoadWordRunnable implements Runnable {

    public interface LoadWordCallback {
        void onPostExecute(Vocabulary vocabulary);
    }

    private String mEnglish;
    private LoadWordCallback mCallback;

    public LoadWordRunnable(String english, LoadWordCallback callback) {
        mEnglish = english;
        mCallback = callback;
    }

    @Override
    public void run() {
        String htmlString = "";
        String chinese = "";
        String phonetic = "";
        if (!TextUtils.isEmpty(mEnglish)) {
            htmlString = getHtmlContent(Parameter.VOCABULARY_SEACH_URL_PREFIX + mEnglish);
            chinese = findChinese(htmlString);
            phonetic = findPhonetic(htmlString);
        }
        if (mCallback != null) {
            mCallback.onPostExecute(new Vocabulary(mEnglish, phonetic, chinese));
        }
    }

    /**
     * Based on link to retrieve the HTML content.
     *
     * @param link A url link
     * @return the HTML content string
     */
    private String getHtmlContent(String link) {
        if (TextUtils.isEmpty(link)) {
            return "";
        }

        BufferedReader br = null;
        StringBuilder content = new StringBuilder();
        try {
            // Get URL content
            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            // Open the stream and put it into BufferedReader
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                content.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeClosable(br);
        }

        return content.toString();
    }

    private String findChinese(String htmlString) {
        if (TextUtils.isEmpty(htmlString)) {
            return "";
        }

        String chinese = "";
        Pattern pattern = Pattern.compile("(<meta property=\"og:description\" content=\")(.*?)(\")");
        Matcher matcher = pattern.matcher(htmlString);
        if (matcher.find() && matcher.groupCount() > 1) {
            chinese = matcher.group(2);
        }
        return chinese;
    }

    private String findPhonetic(String htmlString) {
        if (TextUtils.isEmpty(htmlString)) {
            return "";
        }

        String phonetic = "";
        Pattern pattern = Pattern.compile("(<span class=\"phonetic\">)(.*?)(<\\/span>)");
        Matcher matcher = pattern.matcher(htmlString);
        if (matcher.find() && matcher.groupCount() > 1) {
            phonetic = matcher.group(2);
        }
        return phonetic;
    }
}

package futin;

import futin.util.TextUtils;
import futin.util.Util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Load the next vocabulary when time's up.
 */
public class LoadWordTask extends TimerTask {

    interface LoadWordTaskCallback {
        void preExecute();
        void update(Vocabulary vocabulary);
        void postExecute();
    }

    private Random mRandom;

    private LoadWordTaskCallback mCallback = null;

    public LoadWordTask() {
        createAnEmptyCallback();
        mRandom = new Random(System.currentTimeMillis());
    }

    private void createAnEmptyCallback() {
        mCallback = new LoadWordTaskCallback() {
            @Override
            public void preExecute() {
                // Do nothing
            }

            @Override
            public void update(Vocabulary vocabulary) {
                // Do nothing
            }

            @Override
            public void postExecute() {
                // Do nothing
            }
        };
    }

    public void setCallback(LoadWordTaskCallback callback) {
        mCallback = callback;
    }

    @Override
    public void run() {
        if (mCallback == null) {
            return;
        }

        mCallback.preExecute();

        String vocabulary = getRandomVocabulary();
        String htmlString = getHtmlContent(Parameter.VOCABULARY_SEACH_URL_PREFIX + vocabulary);
        String chinese = findChinese(htmlString);
        String phonetic = findPhonetic(htmlString);

        mCallback.update(new Vocabulary(vocabulary, phonetic, chinese));


        mCallback.postExecute();
    }

    /**
     * Get a vocabulary randomly from the word resource.
     *
     * @return A vocabulary or a empty string
     */
    private String getRandomVocabulary() {
        File wordDirectory = new File("res/raw/word");
        File[] wordFileList = wordDirectory.listFiles();
        int randomIndex = mRandom.nextInt(wordFileList.length);

        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<String> wordList = new ArrayList<>();

        try {
            fr = new FileReader(wordFileList[randomIndex]);
            br = new BufferedReader(fr);

            String tmpLine;

            while ((tmpLine = br.readLine()) != null) {
                wordList.add(tmpLine.split("%")[0]);
            }

            randomIndex = mRandom.nextInt(wordList.size());

            return wordList.get(randomIndex);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeClosable(fr);
            Util.closeClosable(br);
        }

        return "";
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
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                content.append(inputLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeClosable(br);
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

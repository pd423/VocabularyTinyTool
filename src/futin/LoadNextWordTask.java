package futin;

import futin.util.TextUtils;
import futin.util.Utils;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

/**
 * Load the next vocabulary when time's up.
 */
public class LoadNextWordTask extends TimerTask {

    interface LoadNextWordTaskCallback {
        void preExecute();
        void postExecute(Vocabulary vocabulary);
    }

    private Random mRandom;

    private LoadNextWordTaskCallback mCallback = null;

    LoadNextWordTask(LoadNextWordTaskCallback callback) {
        mCallback = callback;
        mRandom = new Random(System.currentTimeMillis());
    }

    @Override
    public void run() {
        if (mCallback == null) {
            return;
        }
        mCallback.preExecute();

        String vocabulary = getRandomVocabulary();
        LoadWordRunnable runnable = new LoadWordRunnable(
                vocabulary, (Vocabulary result) -> mCallback.postExecute(result));
        runnable.run();
    }

    /**
     * Get a vocabulary randomly from the word resource.
     *
     * @return A vocabulary or a empty string
     */
    private String getRandomVocabulary() {
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<String> wordList = new ArrayList<>();

        try {
            InputStream is = getClass().getResourceAsStream("/res/raw/word/word.txt");
            br = new BufferedReader(new InputStreamReader(is));

            String tmpLine;

            while ((tmpLine = br.readLine()) != null) {
                wordList.add(tmpLine.split("%")[0]);
            }

            int randomIndex = mRandom.nextInt(wordList.size());

            return wordList.get(randomIndex);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.closeClosable(fr);
            Utils.closeClosable(br);
        }
        return "";
    }
}

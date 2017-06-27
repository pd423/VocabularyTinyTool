package futin;

import futin.util.Utils;

import java.io.*;
import java.util.ArrayList;
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
        File wordDirectory = new File("res/raw/word");
        File[] wordFileList = wordDirectory.listFiles();
        if (wordFileList != null) {
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
                Utils.closeClosable(fr);
                Utils.closeClosable(br);
            }
        }

        return "";
    }
}

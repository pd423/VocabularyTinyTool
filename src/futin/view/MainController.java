package futin.view;

import futin.Main;
import futin.Parameter;
import futin.Vocabulary;
import futin.util.FileUtils;
import futin.util.TextUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class MainController {

    private Main mMain;

    @FXML
    private Label mEnglishLabel;

    @FXML
    private Label mPhoneticLabel;

    @FXML
    private Label mChineseLabel;

    @FXML
    private Hyperlink mOnlineLink;

    @FXML
    private Button mNextButton;

    @FXML
    private ProgressIndicator mLoadingProgress;

    @FXML
    private Label mLoadingLabel;

    @FXML
    private Button mStarButton;

    private Vocabulary mCurrentVocabulary;

    @FXML
    private void initialize() {
        mChineseLabel.setWrapText(true);
        mChineseLabel.setTextAlignment(TextAlignment.JUSTIFY);
    }

    public void setMain(Main main) {
        mMain = main;
    }

    public void updateVocabulary(Vocabulary vocabulary) {
        if (vocabulary != null) {
            mEnglishLabel.setText(vocabulary.getEnglish());
            mPhoneticLabel.setText(vocabulary.getPhonetic());
            mChineseLabel.setText(vocabulary.getChinese());

            mOnlineLink.setOnAction((ActionEvent event) -> {
                try {
                    Desktop.getDesktop().browse(
                            new URI(Parameter.VOCABULARY_SEACH_URL_PREFIX + vocabulary.getEnglish()));
                } catch (URISyntaxException | IOException e) {
                    e.printStackTrace();
                }
            });

            mCurrentVocabulary = vocabulary;
            updateStartItUI(isFavoriteVocabulary(mCurrentVocabulary));
        }
    }

    public void showLoadingNextVocabulary() {
        mLoadingProgress.setVisible(true);
        mLoadingLabel.setVisible(true);
    }

    public void hideLoadingNextVocabulary() {
        mLoadingProgress.setVisible(false);
        mLoadingLabel.setVisible(false);
    }

    public void loadingNextVocabulary() {
        showLoadingNextVocabulary();
        mNextButton.setVisible(false);
        mStarButton.setVisible(false);
    }

    public void finishLoadingNextVocabulary() {
        hideLoadingNextVocabulary();
        mNextButton.setVisible(true);
        mStarButton.setVisible(true);
    }

    @FXML
    private void handleNext() {
        if (mMain != null) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    mMain.nextVocabulary();
                }
            });
            thread.start();
        }
    }

    @FXML
    private void handleStarIt() {
        if (mCurrentVocabulary != null && !isFavoriteVocabulary(mCurrentVocabulary)) {
            java.util.List<Vocabulary> favoriteVocabularies = FileUtils.readVocabularyList(FileUtils.getSaveFile());
            favoriteVocabularies.add(mCurrentVocabulary);
            updateStartItUI(true);
            FileUtils.saveVocabularyList(FileUtils.getSaveFile(), favoriteVocabularies);
        }
    }

    void updateStartItUI(boolean isStar) {
        mStarButton.setText(isStar ? "★" : "☆");
    }

    boolean isFavoriteVocabulary(Vocabulary vocabulary) {
        if (vocabulary == null || TextUtils.isEmpty(vocabulary.getEnglish())) {
            return false;
        }
        java.util.List<Vocabulary> favoriteVocabularies = FileUtils.readVocabularyList(FileUtils.getSaveFile());
        for (Vocabulary tmpVoc : favoriteVocabularies) {
            if (TextUtils.equals(tmpVoc.getEnglish(), vocabulary.getEnglish())) {
                return true;
            }
        }
        return false;
    }
}

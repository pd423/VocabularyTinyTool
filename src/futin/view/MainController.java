package futin.view;

import futin.Main;
import futin.Parameter;
import futin.Vocabulary;
import futin.util.FileUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.*;
import javafx.scene.text.Font;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static futin.Vocabulary.isFavoriteVocabulary;

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

    @FXML
    private Button mSearchButton;

    @FXML
    private Button mFavoriteListButton;

    private Vocabulary mCurrentVocabulary;

    @FXML
    private void initialize() {
        mChineseLabel.setWrapText(true);
        mChineseLabel.setTextAlignment(TextAlignment.JUSTIFY);

        mOnlineLink.setOnAction((ActionEvent event) -> {
            try {
                Desktop.getDesktop().browse(new URI(Parameter.VOCABULARY_SEACH_URL_PREFIX));
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        });

        mPhoneticLabel.setFont(new Font("Arial", 13));
        mFavoriteListButton.setFont(new Font("Arial", 13));
        mStarButton.setFont(new Font("Arial", 13));
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

    public void loadingNextVocabulary() {
        mLoadingProgress.setVisible(true);
        mLoadingLabel.setVisible(true);
        mNextButton.setVisible(false);
        mStarButton.setVisible(false);
    }

    public void finishLoadingNextVocabulary() {
        mLoadingProgress.setVisible(false);
        mLoadingLabel.setVisible(false);
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

    @FXML
    private void openSearchWindow() {
        if (mMain != null) {
            mMain.showSearchWindow();
        }
    }

    @FXML
    private void openFavoriteListWindow() {
        if (mMain != null) {
            mMain.showFavoriteListWindow();
        }
    }
}

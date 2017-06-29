package futin.view;

import futin.LoadWordRunnable;
import futin.Main;
import futin.Parameter;
import futin.Vocabulary;
import futin.util.FileUtils;
import futin.util.TextUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.*;

import java.awt.*;
import java.awt.Font;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static futin.Vocabulary.isFavoriteVocabulary;

public class SearchController {

    private Main mMain;

    @FXML
    private TextField mEnglishTextField;

    @FXML
    private Label mPhoneticLabel;

    @FXML
    private Label mChineseLabel;

    @FXML
    private Hyperlink mOnlineLink;

    @FXML
    private ProgressIndicator mLoadingProgress;

    @FXML
    private Label mLoadingLabel;

    @FXML
    private Button mStarButton;

    @FXML
    private Button mSearchButton;

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

        mEnglishTextField.setOnKeyPressed((KeyEvent event) -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSearch();
            }
        });

        mPhoneticLabel.setFont(new javafx.scene.text.Font("Arial", 13));
        mStarButton.setFont(new javafx.scene.text.Font("Arial", 13));
    }

    public void setMain(Main main) {
        mMain = main;
    }

    public void loadingVocabulary() {
        mLoadingProgress.setVisible(true);
        mLoadingLabel.setVisible(true);
        mStarButton.setVisible(false);
        mSearchButton.setVisible(false);
    }

    public void finishLoadingVocabulary() {
        mLoadingProgress.setVisible(false);
        mLoadingLabel.setVisible(false);
        mStarButton.setVisible(true);
        mSearchButton.setVisible(true);
    }

    @FXML
    private void handleSearch() {
        if (!TextUtils.isEmpty(mEnglishTextField.getText())) {
            loadingVocabulary();
            Thread thread = new Thread(new LoadWordRunnable(mEnglishTextField.getText(), new LoadWordRunnable.LoadWordCallback() {
                @Override
                public void onPostExecute(Vocabulary vocabulary) {
                    updateVocabulary(vocabulary);
                    finishLoadingVocabulary();
                }
            }));
            thread.start();
        }
    }

    private void updateVocabulary(Vocabulary vocabulary) {
        if (vocabulary != null) {
            Platform.runLater(() -> {
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
            });
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
}

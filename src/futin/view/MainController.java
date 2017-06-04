package futin.view;

import futin.Main;
import futin.Parameter;
import futin.Vocabulary;
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
    }

    public void finishLoadingNextVocabulary() {
        hideLoadingNextVocabulary();
        mNextButton.setVisible(true);
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
        }
    }
}

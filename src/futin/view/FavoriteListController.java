package futin.view;

import com.sun.org.apache.xerces.internal.util.Status;
import futin.Vocabulary;
import futin.util.FileUtils;
import futin.util.TextUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class FavoriteListController {

    @FXML
    private TableView<Vocabulary> mVocabularyTable;

    @FXML
    private TableColumn<Vocabulary, String> mEnglish;

    @FXML
    private TableColumn<Vocabulary, String> mPhonetic;

    @FXML
    private TableColumn<Vocabulary, String> mExplain;

    @FXML
    private void initialize() {
        ObservableList<Vocabulary> vocabularyObservableList = FXCollections.observableArrayList();
        vocabularyObservableList.setAll(FileUtils.readVocabularyList(FileUtils.getSaveFile()));
        mVocabularyTable.setItems(vocabularyObservableList);
        mVocabularyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        mEnglish.setCellValueFactory(cellData -> cellData.getValue().getEnglishProperty());
        mPhonetic.setCellValueFactory(cellData -> cellData.getValue().getPhoneticProperty());
        mExplain.setCellValueFactory(cellData -> cellData.getValue().getChineseProperty());

        mVocabularyTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        enableCopyToClipboard();
        enableDeleteFavorites();
    }

    private void enableCopyToClipboard() {
        MenuItem item = new MenuItem("Copy");
        item.setOnAction((ActionEvent event) ->  {
            ObservableList posList = mVocabularyTable.getSelectionModel().getSelectedItems();
            StringBuilder clipboardString = new StringBuilder();
            for (Object object : posList) {
                if (object != null && object instanceof Vocabulary) {
                    Vocabulary vocabulary = (Vocabulary) object;
                    clipboardString.append(vocabulary.getEnglish());
                    clipboardString.append(", ");
                    clipboardString.append(vocabulary.getPhonetic());
                    clipboardString.append(", ");
                    clipboardString.append(vocabulary.getChinese());
                    clipboardString.append('\n');
                }
            }
            final ClipboardContent content = new ClipboardContent();
            content.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(content);
        });
        ContextMenu menu = mVocabularyTable.getContextMenu() == null ?
                new ContextMenu() : mVocabularyTable.getContextMenu();
        menu.getItems().add(item);
        mVocabularyTable.setContextMenu(menu);
    }

    private void enableDeleteFavorites() {
        MenuItem item = new MenuItem("Delete");
        item.setOnAction((ActionEvent event) ->  {
            ObservableList posList = mVocabularyTable.getSelectionModel().getSelectedItems();
            List<Vocabulary> allFavoriteList = FileUtils.readVocabularyList(FileUtils.getSaveFile());
            for (Object object : posList) {
                if (object != null && object instanceof Vocabulary) {
                    Vocabulary vocabulary = (Vocabulary) object;
                    allFavoriteList = removeVocabulary(allFavoriteList, vocabulary);
                }
            }
            // Save favorite vocabularies to disk.
            FileUtils.saveVocabularyList(FileUtils.getSaveFile(), allFavoriteList);
            // Update UI.
            mVocabularyTable.getItems().clear();
            ObservableList<Vocabulary> vocabularyObservableList = FXCollections.observableArrayList();
            vocabularyObservableList.setAll(allFavoriteList);
            mVocabularyTable.setItems(vocabularyObservableList);

        });
        ContextMenu menu = mVocabularyTable.getContextMenu() == null ?
                new ContextMenu() : mVocabularyTable.getContextMenu();
        menu.getItems().add(item);
        mVocabularyTable.setContextMenu(menu);
    }

    private List<Vocabulary> removeVocabulary(List<Vocabulary> vocabularyList, Vocabulary vocabulary) {
        List<Vocabulary> result = new ArrayList<>();
        if (vocabularyList != null && vocabulary != null) {
            for (Vocabulary voc : vocabularyList) {
                if (!TextUtils.equals(voc.getEnglish(), vocabulary.getEnglish())) {
                    result.add(voc);
                }
            }
        } else {
            return vocabularyList;
        }
        return result;
    }
}

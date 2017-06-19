package futin.util;

import futin.Vocabulary;
import futin.model.VocabularyListWrapper;
import javafx.scene.control.Alert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private static final String SAVE_PATH = "./favorite_list.xml";

    public static File getSaveFile() {
        return new File(SAVE_PATH);
    }

    public static List<Vocabulary> readVocabularyList(File file) {
        List<Vocabulary> vocabularyList = new ArrayList<>();
        try {
            JAXBContext context = JAXBContext.newInstance(VocabularyListWrapper.class);
            Unmarshaller um = context.createUnmarshaller();

            VocabularyListWrapper wrapper = (VocabularyListWrapper) um.unmarshal(file);
            vocabularyList = wrapper.getVocabularyList();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load favorite vocabularies");
            alert.setContentText("Could not load favorite vocabularies from file:\n" + file.getPath());

            alert.showAndWait();
        }
        if (vocabularyList == null) {
            vocabularyList = new ArrayList<>();
        }
        return vocabularyList;
    }

    public static void saveVocabularyList(File file, List<Vocabulary> vocabularyList) {
        if (vocabularyList != null) {
            try {
                JAXBContext context = JAXBContext.newInstance(VocabularyListWrapper.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                VocabularyListWrapper wrapper = new VocabularyListWrapper();
                wrapper.setVocabularyList(vocabularyList);

                m.marshal(wrapper, file);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Could not save favorite vocabularies");
                alert.setContentText("Could not save favorite vocabularies to file:\n" + file.getPath());

                alert.showAndWait();
            }
        }
    }
}

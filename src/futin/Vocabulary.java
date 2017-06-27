package futin;

import futin.util.FileUtils;
import futin.util.TextUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a Vocabulary.
 */
public class Vocabulary {

    private final StringProperty mEnglish;
    private final StringProperty mPhonetic;
    private final StringProperty mChinese;

    public Vocabulary() {
        this("");
    }

    public Vocabulary(String english) {
        this(english, "", "");
    }

    public Vocabulary(String english, String phonetic, String chinese) {
        english = english == null ? "" : english;
        phonetic = phonetic == null ? "" : phonetic;
        chinese = chinese == null ? "" : chinese;

        mEnglish = new SimpleStringProperty(english);
        mPhonetic = new SimpleStringProperty(phonetic);
        mChinese = new SimpleStringProperty(chinese);
    }

    public String getEnglish() {
        return mEnglish.get();
    }

    public void setEnglish(String english) {
        english = english == null ? "" : english;
        mEnglish.set(english);
    }

    public String getPhonetic() {
        return mPhonetic.get();
    }

    public void setPhonetic(String phonetic) {
        phonetic = phonetic == null ? "" : phonetic;
        mPhonetic.set(phonetic);
    }

    public String getChinese() {
        return mChinese.get();
    }

    public void setChinese(String chinese) {
        chinese = chinese == null ? "" : chinese;
        mChinese.set(chinese);
    }

    public static boolean isFavoriteVocabulary(Vocabulary vocabulary) {
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

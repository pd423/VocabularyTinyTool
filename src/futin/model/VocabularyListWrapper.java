package futin.model;

import futin.Vocabulary;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "vocabularies")
public class VocabularyListWrapper {

    private List<Vocabulary> mVocabularyList;

    @XmlElement(name = "vocabulary")
    public List<Vocabulary> getVocabularyList() {
        return mVocabularyList;
    }

    public void setVocabularyList(List<Vocabulary> vocabularyList) {
        mVocabularyList = vocabularyList;
    }
}

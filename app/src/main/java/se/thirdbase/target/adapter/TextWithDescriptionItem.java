package se.thirdbase.target.adapter;

/**
 * Created by alex on 4/1/16.
 */
public class TextWithDescriptionItem {

    private final String mDescription;
    private final String mText;

    public static TextWithDescriptionItem[] fromArrays(String[] texts, String[] descriptions) {
        int length = Math.min(texts.length, descriptions.length);

        TextWithDescriptionItem[] array = new TextWithDescriptionItem[length];

        for (int i = 0; i < length; i++) {
            array[i] = new TextWithDescriptionItem(texts[i], descriptions[i]);
        }

        return array;
    }

    public TextWithDescriptionItem(String text, String description) {
        mText = text;
        mDescription = description;
    }

    public String getText() {
        return mText;
    }

    public String getDescription() {
        return mDescription;
    }
}

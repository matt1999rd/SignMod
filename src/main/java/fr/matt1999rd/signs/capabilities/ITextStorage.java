package fr.matt1999rd.signs.capabilities;

import fr.matt1999rd.signs.util.Text;

import java.util.List;

public interface ITextStorage {
    Text getText(int n);
    void setText(Text newText,int n);
    void addText(Text t,boolean increaseLimit);
    List<Text> getTexts();
    int getMaxLength();
    void setMaxLength(int maxLength);
}

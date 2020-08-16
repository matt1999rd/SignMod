package fr.mattmouss.signs.capabilities;

import fr.mattmouss.signs.util.Text;

import java.util.List;

public interface ITextStorage {
    Text getText(int n);
    void setText(Text newText,int n);
    void addText(Text t,boolean increaseLimit);
    List<Text> getTexts();
    int getMaxLength();
    void setMaxLength(int maxLength);
}

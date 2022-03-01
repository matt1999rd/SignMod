package fr.matt1999rd.signs.capabilities;

import fr.matt1999rd.signs.util.Text;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextStorage implements ITextStorage, INBTSerializable<CompoundNBT> {

    List<Text> texts;
    int maxLength;

    TextStorage(){
        this.maxLength = 1;
        this.texts = new ArrayList<>();
    }

    public TextStorage(int maxLength,Text... texts){
        this.maxLength = maxLength;
        this.texts = new ArrayList<>();
        int lim= Math.min(maxLength,texts.length);
        this.texts.addAll(Arrays.asList(texts).subList(0, lim));
    }

    @Override
    public Text getText(int n) {
        if (n<texts.size() && n>=0){
            return texts.get(n);
        }
        return null;
    }

    @Override
    public List<Text> getTexts() {
        return texts;
    }


    @Override
    public int getMaxLength() {
        return maxLength;
    }

    @Override
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void setText(Text newText,int n) {
        if (n<texts.size() && n>=0){
            texts.set(n,newText);
        }
    }

    @Override
    public void addText(Text t,boolean increaseLimit) {
        if (increaseLimit){
            maxLength++;
        }
        if (texts.size() != maxLength){
            texts.add(t);
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT listNBT = new ListNBT();
        texts.forEach(text -> {
            CompoundNBT nbt1 = text.serializeNBT();
            listNBT.add(nbt1);
        });
        nbt.put("txts",listNBT);
        nbt.putInt("maxLength",maxLength);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        ListNBT nbt1 = (ListNBT) nbt.get("txts");
        maxLength = nbt.getInt("maxLength");
        if (nbt1.size()>maxLength){
            return;
        }
        nbt1.forEach(iNbt->{
            CompoundNBT nbt2 = (CompoundNBT)iNbt;
            setText(Text.getTextFromNBT(nbt2),0);
        });
    }
}

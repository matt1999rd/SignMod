package fr.matt1999rd.signs.capabilities;

import fr.matt1999rd.signs.util.Text;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextStorage implements ITextStorage, INBTSerializable<CompoundTag> {

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
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag listNBT = new ListTag();
        texts.forEach(text -> {
            CompoundTag nbt1 = text.serializeNBT();
            listNBT.add(nbt1);
        });
        nbt.put("txts",listNBT);
        nbt.putInt("maxLength",maxLength);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag nbt1 = (ListTag) nbt.get("txts");
        maxLength = nbt.getInt("maxLength");
        assert nbt1 != null;
        if (nbt1.size()>maxLength){
            return;
        }
        nbt1.forEach(iNbt->{
            CompoundTag nbt2 = (CompoundTag)iNbt;
            setText(Text.getTextFromNBT(nbt2),0);
        });
    }
}

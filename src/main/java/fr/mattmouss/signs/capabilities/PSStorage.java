package fr.mattmouss.signs.capabilities;


import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.enums.PSDisplayMode;
import fr.mattmouss.signs.enums.PSPosition;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Text;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class PSStorage implements IPSStorage, INBTSerializable<CompoundNBT> {
    PSPosition position;
    PSDisplayMode mode;
    List<Text> texts ;
    Color backgroundColor;
    Color foregroundColor;

    public PSStorage(){
        texts = new ArrayList<>();
        backgroundColor = Color.BLACK;
        foregroundColor = Color.WHITE;
    }

    @Override
    public List<Text> getTexts() {
        return texts;
    }

    @Override
    public int getMaxLength() {
        return 0;
    }

    @Override
    public void setMaxLength(int maxLength) {

    }



    @Override
    public Text getText(int n) {
        return null;
    }

    @Override
    public void setText(Text t, int ind) {
        try {
            texts.set(ind,t);
        } catch (IndexOutOfBoundsException e){
            SignMod.LOGGER.warn("Try to set text that is not with the right indice : "+ind);
        }
    }

    @Override
    public void addText(Text t, boolean increaseLimit) {

    }




    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putByte("mode",mode.getMeta());
        nbt.putByte("ps_pos",position.getMeta());
        ListNBT textsNBT = new ListNBT();
        for (Text t : texts){
            CompoundNBT txtNBT = t.serializeNBT();
            textsNBT.add(txtNBT);
        }
        nbt.put("texts",textsNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        byte modeMeta = nbt.getByte("mode");
        this.mode = PSDisplayMode.byIndex(modeMeta);
        byte posMeta = nbt.getByte("ps_pos");
        this.position = PSPosition.byIndex(posMeta);
        ListNBT textsNBT = (ListNBT) nbt.get("texts");
        for (INBT iNBT : textsNBT) {
            CompoundNBT textNBT = (CompoundNBT)iNBT;
            Text t =Text.getTextFromNBT(textNBT);
            if (!(texts.contains(t))){
                texts.add(t);
            }
        }
    }


    @Override
    public void setInternVariable(PSPosition position, PSDisplayMode mode) {
        this.mode = mode;
        this.position = position;
    }

    @Nonnull
    @Override
    public PSPosition getPosition() {
        return position;
    }

    @Nonnull
    @Override
    public PSDisplayMode getDisplayMode() {
        return mode;
    }

    @Override
    public void setBackgroundColor(int color) {
        Color newColor = new Color(color);
        this.backgroundColor = newColor;
    }

    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setForegroundColor(int color) {
        Color newColor = new Color(color);
        this.foregroundColor = newColor;
    }

    @Override
    public Color getForegroundColor() {
        return foregroundColor;
    }
}

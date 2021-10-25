package fr.mattmouss.signs.capabilities;


import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.PSDisplayMode;
import fr.mattmouss.signs.enums.PSPosition;
import fr.mattmouss.signs.util.Text;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.awt.*;


public class PSStorage implements IPSStorage, INBTSerializable<CompoundNBT> {
    PSPosition position;
    PSDisplayMode mode;
    Text[] texts;
    Color backgroundColor;
    Color foregroundColor;
    int arrowId;

    public PSStorage(){
        texts = new Text[6];
        for (int i=0;i<6;i++){
            texts[i] = Text.getDefaultText();
        }
        position = PSPosition.DOWN_LEFT;
        backgroundColor = Color.WHITE;
        foregroundColor = Color.BLACK;
        arrowId = 1;
    }

    @Override
    public Text[] getTexts() {
        return texts;
    }


    @Override
    public Text getText(int n) {
        if (n > 6) {
            SignMod.LOGGER.error("Unable to get text index : bad call of function getText");
            return null;
        }else {
            return texts[n];
        }
    }

    @Override
    public void setText(Text t, int ind) {
        try {
            texts[ind] = t;
        } catch (IndexOutOfBoundsException e){
            SignMod.LOGGER.warn("Try to set text that is not with the right indice : "+ind);
        }
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
        if (textsNBT.size() > 6){
            SignMod.LOGGER.error("Error when registering text of PS square panel : too much element registered");
            return;
        }
        int increment = 0;
        for (INBT iNBT : textsNBT) {
            CompoundNBT textNBT = (CompoundNBT)iNBT;
            Text t =Text.getTextFromNBT(textNBT);
            this.texts[increment] = t;
            increment++;
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
        this.backgroundColor = new Color(color);
    }

    @Override
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setForegroundColor(int color) {
        this.foregroundColor = new Color(color);
    }

    @Override
    public Color getForegroundColor() {
        return foregroundColor;
    }

    @Override
    public int getArrowId() {
        return arrowId;
    }

    @Override
    public void setArrowId(int arrowId) {
        this.arrowId = arrowId;
    }
}

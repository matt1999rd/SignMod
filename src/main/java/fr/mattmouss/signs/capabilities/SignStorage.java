package fr.mattmouss.signs.capabilities;


import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Text;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.INBTSerializable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SignStorage implements ISignStorage, INBTSerializable<CompoundNBT> {

    int[][] picture = new int[128][128];
    List<Text> texts = new ArrayList<>();


    public SignStorage(){
        for (int i=0;i<128;i++){
            for (int j=0;j<128;j++){
                picture[i][j] = Color.WHITE.getRGB();
            }
        }
    }

    public SignStorage(Text[] texts){
        this();
        int n = texts.length;
        for (int i=0;i<n;i++){
            this.texts.add(texts[i]);
        }
    }

    @Override
    public void setPixel(int x, int y, int rColor, int gColor, int bColor) {
        setPixel(x,y,MathHelper.rgb(rColor,gColor,bColor));
    }




    @Override
    public int getRGBPixel(int x, int y) {
        if (Functions.isValidCoordinate(x,y)){
            return picture[x][y];
        }else {
            return 0;
        }
    }

    @Override
    public void setPixel(int x, int y, int color) {
        if (Functions.isValidCoordinate(x,y)){
            picture[x][y] = color;
        }
    }

    @Override
    public void addText(Text t) {
        if (this.texts.contains(t)){
            return;
        }
        this.texts.add(t);
    }

    @Override
    public Text[] getTexts() {
        Text[] texts = (Text[]) this.texts.toArray();
        return texts;
    }

    @Override
    public int[] getAllPixel() {
        int[] allPixel = new int[128*128];
        for (int i=0;i<128*128;i++){
            allPixel[i] = picture[i/128][i%128];
        }
        return allPixel;
    }

    @Override
    public void setAllPixel(int[] pixels) {
        for (int i=0;i<128*128;i++){
            setPixel(i/128,i%128,pixels[i]);
        }
    }


    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT pixels = new ListNBT();
        for (int i=0;i<128;i++){
            for (int j=0;j<128;j++){
                CompoundNBT pixelNBT = new CompoundNBT();
                pixelNBT.putInt("pixel",getRGBPixel(i,j));
                pixels.add(pixelNBT);
            }
        }
        nbt.put("pixels",pixels);
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
        ListNBT pixelsNBT = (ListNBT) nbt.get("pixels");
        int i=0;
        int j=0;
        for (INBT iNBT : pixelsNBT) {
            if (i < 128) {
                CompoundNBT pixelNBT = (CompoundNBT) iNBT;
                setPixel(i, j, pixelNBT.getInt("pixel"));
                j++;
                if (j > 127) {
                    j = 0;
                    ++i;
                }
            }
        }
        ListNBT textsNBT = (ListNBT) nbt.get("texts");
        for (INBT iNBT : textsNBT) {
            Text t = new Text(0,0,"");
            CompoundNBT textNBT = (CompoundNBT)iNBT;
            t.deserializeNBT(textNBT);
            if (!(texts.contains(t))){
                texts.add(t);
            }
        }
    }



}

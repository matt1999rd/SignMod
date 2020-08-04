package fr.mattmouss.signs.capabilities;


import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Text;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.awt.Point;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class SignStorage implements ISignStorage, INBTSerializable<CompoundNBT> {

    int[][] picture = new int[128][128];
    List<Text> texts = new ArrayList<>();
    Color bg_color;


    public SignStorage(){
        for (int i=0;i<128;i++){
            for (int j=0;j<128;j++){
                picture[i][j] = 0;
            }
        }
        bg_color = Color.WHITE;
    }

    public SignStorage(Text[] texts){
        this();
        int n = texts.length;
        for (int i=0;i<n;i++){
            this.texts.add(texts[i]);
        }
    }


    @Override
    public int getRGBPixel(int x, int y) {
        if (Functions.isValidCoordinate(x,y)){
            int color = picture[x][y];
            return (color == 0) ? bg_color.getRGB() : color;
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
    public void setPixel(int x, int y, int color,int length) {
        int lateral_length = (length-1) /2;
        boolean even_length = (length % 2 == 0);
        for (int i=-lateral_length;i<=lateral_length+(even_length?1:0);i++){
            for (int j=-lateral_length;j<=lateral_length+(even_length?1:0);j++){
                setPixel(x+i,y+j,color);
            }
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
    public List<Text> getTexts() {
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
            setPixel(i/128,i%128,pixels[i],1);
        }
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
    public void delText(int ind) {
        texts.remove(ind);
    }

    public void setBackGround(int newColor){
        bg_color = new Color(newColor,true);
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
                setPixel(i, j, pixelNBT.getInt("pixel"),1);
                j++;
                if (j > 127) {
                    j = 0;
                    ++i;
                }
            }
        }
        ListNBT textsNBT = (ListNBT) nbt.get("texts");
        for (INBT iNBT : textsNBT) {
            Text t = new Text(0,0,"",Color.WHITE);
            CompoundNBT textNBT = (CompoundNBT)iNBT;
            t.deserializeNBT(textNBT);
            if (!(texts.contains(t))){
                texts.add(t);
            }
        }
    }

    public void fill(int x,int y,int color){
        int srcColor = getRGBPixel(x,y);
        boolean[][] hits = new boolean[128][128];
        Queue<Point> queue = new LinkedList();
        queue.add(new Point(x, y));

        while(!queue.isEmpty()) {
            Point p = queue.remove();
            if (doFillOperation(hits, p.x, p.y, srcColor, color)) {
                queue.add(new Point(p.x, p.y - 1));
                queue.add(new Point(p.x, p.y + 1));
                queue.add(new Point(p.x - 1, p.y));
                queue.add(new Point(p.x + 1, p.y));
            }
        }
    }

    private boolean doFillOperation(boolean[][] hits, int x, int y, int srcColor, int tgtColor) {
        if (y < 0) {
            return false;
        } else if (x < 0) {
            return false;
        } else if (y > 127) {
            return false;
        } else if (x > 127) {
            return false;
        } else if (hits[x][y]) {
            return false;
        } else if (getRGBPixel(x,y)!= srcColor) {
            return false;
        } else {
            setPixel(x,y,tgtColor);
            hits[x][y] = true;
            return true;
        }
    }



}

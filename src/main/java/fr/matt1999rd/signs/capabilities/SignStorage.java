package fr.matt1999rd.signs.capabilities;


import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.util.Text;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Mth;
import net.minecraftforge.common.util.INBTSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class SignStorage implements ISignStorage, INBTSerializable<CompoundTag> {

    private static final Logger log = LoggerFactory.getLogger(SignStorage.class);
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
    public void setText(Text t, int ind) {
        try {
            texts.set(ind,t);
        } catch (IndexOutOfBoundsException e){
            SignMod.LOGGER.warn("Try to set text that is not with the right indices : {}", ind);
        }
    }

    @Override
    public void setTextPosition(int ind, int newX, int newY, Form form) {
        if (ind == -1 || ind>texts.size()){
            SignMod.LOGGER.warn("skip bad move text operation. indices is invalid :{}", ind);
            return;
        }
        Text t = texts.get(ind);
        if (form.rectangleIsIn(newX,newX+t.getLength(true,true),
                newY,newY+t.getHeight()))t.setPosition(newX,newY);
        else SignMod.LOGGER.info("coordinate leads to out pass the limit of the text : no position set !");
    }

    private void makeLineBresenham(int dx,int dy,int x1,int y1,int x2,int y2,int length,int color,int octant){
        boolean nearHor = octant % 4 == 0 || octant % 4 == 1;
        int e = nearHor ? dx : dy;
        dx = nearHor ? e*2 : dx*2;
        dy = nearHor ? dy*2 : e*2;
        while (true){
            setPixel(x1,y1,color,length);
            if (nearHor){
                x1+=(octant > 3 && octant < 8) ? -1 : 1;
            }else {
                y1+=(octant > 3 && octant < 8) ? -1 : 1;
            }
            if (((x1 == x2) && nearHor) || ((y1 == y2) && !nearHor)){
                break;
            }
            e+=((octant % 4 == 1 || octant % 4 == 2) ? -1 : 1)*(nearHor ? dy : dx);
            // test to do :
            // e<0 if octant is 1st,2nd and 8th (-> octant mod 8 = 0,1,2)
            // e<=0 if octant is 3rd
            // e>=0 if octant is 4th,5th,6th (-> octant mod 4 = 0,1,2 and octant mod 8 >= 4)
            // e>0 if octant is 7th
            if ((octant % 8 <= 2 && e<0) ||
                    (octant == 3 && e<=0) ||
                    ((octant % 8 >= 4 && octant % 4 <= 2) && e>=0) ||
                    (octant == 7 && e>0)){
                if (nearHor){
                    y1 += (octant < 3 || octant == 4 || octant == 7) ? 1 : -1;
                }else {
                    x1 += (octant < 3 || octant == 4 || octant == 7) ? 1 : -1;
                }
                e+=nearHor ? dx : dy;
            }
        }
    }

    public int getOctant(int dx, int dy){
        double angle = Mth.atan2(dy,dx); //calculate the angle for -pi to pi not including -pi
        if (angle < 0){
            angle += 2*Mth.PI;
        }
        return Mth.fastFloor(4*angle/Mth.PI)+1; // first octant has value 1
    }

    @Override
    public void makeLine(int x1, int y1, int x2, int y2, int length, int color) {
        if (Functions.isValidCoordinate(x1,y1) && Functions.isValidCoordinate(x2,y2)){
            //Bresenham algorithm
            int dx = x2 - x1;
            int dy = y2 - y1;
            if (dx == 0 && dy == 0)return;
            if (dx == 0){ // vertical vector
                if (y1>y2){ // directing downward
                    int yEnd = y1;
                    y1 = y2;
                    y2 = yEnd;
                }
                for (int y = y1;y<=y2;y++){
                    setPixel(x1,y,color,length);
                }
                return;
            }
            if (dy == 0){  // horizontal vector
                if (x1>x2) { // directing left
                    int xEnd = x1;
                    x1 = x2;
                    x2 = xEnd;
                }
                for (int x = x1;x<=x2;x++){
                    setPixel(x,y1,color,length);
                }
                return;
            }
            int octant = getOctant(dx,dy);
            // Base function see fr wikipedia page for information about Bresenham algorithm
            makeLineBresenham(dx,dy,x1,y1,x2,y2,length,color,octant);
        }
    }

    @Override
    public void delText(int ind) {
        texts.remove(ind);
    }

    public void setBackGround(int newColor){
        bg_color = new Color(newColor,true);
    }

    public int getBackGround(){
        return bg_color.getRGB();
    }


    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag pixels = new ListTag();
        for (int i=0;i<128;i++){
            for (int j=0;j<128;j++){
                CompoundTag pixelNBT = new CompoundTag();
                pixelNBT.putInt("pixel",getRGBPixel(i,j));
                pixels.add(pixelNBT);
            }
        }
        nbt.put("pixels",pixels);
        ListTag textsNBT = new ListTag();
        for (Text t : texts){
            CompoundTag txtNBT = t.serializeNBT();
            textsNBT.add(txtNBT);
        }
        nbt.put("texts",textsNBT);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag pixelsNBT = (ListTag) nbt.get("pixels");
        int i=0;
        int j=0;
        assert pixelsNBT != null;
        for (Tag iNBT : pixelsNBT) {
            if (i < 128) {
                CompoundTag pixelNBT = (CompoundTag) iNBT;
                setPixel(i, j, pixelNBT.getInt("pixel"),1);
                j++;
                if (j > 127) {
                    j = 0;
                    ++i;
                }
            }
        }
        ListTag textsNBT = (ListTag) nbt.get("texts");
        assert textsNBT != null;
        for (Tag iNBT : textsNBT) {
            CompoundTag textNBT = (CompoundTag)iNBT;
            Text t =Text.getTextFromNBT(textNBT);
            if (!(texts.contains(t))){
                texts.add(t);
            }
        }
    }

    public void fill(int x,int y,int color){
        int srcColor = getRGBPixel(x,y);
        boolean[][] hits = new boolean[128][128];
        Queue<Point> queue = new LinkedList<>();
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

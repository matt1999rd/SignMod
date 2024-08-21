package fr.matt1999rd.signs.gui.widget;

import fr.matt1999rd.signs.gui.screenutils.ColorType;
import fr.matt1999rd.signs.gui.screenutils.Option;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class ColorSlider extends AbstractSliderButton {
    Option option;
    ColorType type;
    public ColorSlider(int xIn, int yIn, Option option, ColorType type,int width) {
        super(xIn, yIn, width, 20, Component.nullToEmpty(type.name() + " Slider"), option.getColor(type)/255.0);
        this.option = option;
        this.type = type;
    }

    @Override
    protected void updateMessage() {
        //add here setting that can be saved server side
    }

    @Override
    protected void applyValue() {
        option.setColor((int)(this.value*256),this.type);
    }

    public void updateSlider(int newColor){
        this.value = newColor/256.0F;
    }
}

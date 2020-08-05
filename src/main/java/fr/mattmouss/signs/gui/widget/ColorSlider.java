package fr.mattmouss.signs.gui.widget;

import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.screenutils.Option;
import fr.mattmouss.signs.gui.screenutils.PencilOption;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColorSlider extends AbstractSlider {
    Option option;
    ColorType type;
    public ColorSlider(int xIn, int yIn, PencilOption option, ColorType type) {
        super(xIn, yIn, 132, 20, option.getColor(type)/255.0);
        this.option = option;
        this.type = type;
    }

    public ColorSlider(int xIn, int yIn, LimitSizeTextField field, ColorType type) {
        super(xIn, yIn, 132, 20, field.getColor(type)/255.0);
        this.option = field;
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

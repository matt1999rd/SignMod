package fr.mattmouss.signs.gui.widget;

import fr.mattmouss.signs.gui.screenutils.ColorType;
import fr.mattmouss.signs.gui.screenutils.PencilOption;
import net.minecraft.client.gui.widget.AbstractSlider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ColorSlider extends AbstractSlider {
    PencilOption option;
    ColorType type;
    public ColorSlider(int xIn, int yIn, PencilOption option, ColorType type) {
        super(xIn, yIn, 132, 20, option.getColor(type)/255.0);
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
}

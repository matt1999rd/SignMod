package fr.mattmouss.signs.tileentity.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquareSignModel extends Model {
    private final RendererModel panel = new RendererModel(this,0,0);

    public SquareSignModel() {
        panel.addBox(-5,3,-2,10,10,1);
    }

    public void renderSign(){
        panel.render(0.0625F);
    }

    /*
    public RendererModel getGrid() {
        return grid_background;
    }

    public RendererModel getSupport() {
        return support_background;
    }

     */
}

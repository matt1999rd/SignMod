package fr.mattmouss.signs.tileentity.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquareSignModel extends Model {
    private final RendererModel panel = new RendererModel(this,0,0);
    private final RendererModel grid_background = new RendererModel(this,0,0);
    private final RendererModel support_background = new RendererModel(this,0,0);

    public SquareSignModel() {
        panel.addBox(-5,2,-2,10,10,1);
        //for the grid background multiple box
        //add the two horizontal barrier
        grid_background.addBox(-8,3,0,16,2,0);
        grid_background.addBox(-8,11,0,16,2,0);
        //add the limit thin vertical barrier
        grid_background.addBox(7,5,0,1,6,0);
        grid_background.addBox(-8,5,0,1,6,0);
        //add the central vertical barrier
        grid_background.addBox(-5,5,0,2,6,0);
        grid_background.addBox(-1,5,0,2,6,0);
        grid_background.addBox(3,5,0,2,6,0);
        //for the sign support only one box occur
        support_background.addBox(-1,0,-1,2,16,2);
    }

    public void renderSign(){
        panel.render(0.0625F);
        grid_background.render(0.0625F);
        support_background.render(0.0625F);
    }

    public RendererModel getGrid() {
        return grid_background;
    }

    public RendererModel getSupport() {
        return support_background;
    }
}

package fr.mattmouss.signs.tileentity.model;

import fr.mattmouss.signs.enums.PSDisplayMode;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;

public class PSSignModel extends Model {
    private final RendererModel panel = new RendererModel(this,0,0);
    public PSSignModel() {
        textureWidth = 66;
        textureHeight = 33;
        initModel();
    }

    private void initModel(){
        panel.setRotationPoint(0.0F, 24.0F, 0.0F);
        panel.addBox(-17.0F, -32.0F, 0.0F, 32, 32, 1);
    }

    public void renderSign(){
        panel.render(0.0625F);
    }
}

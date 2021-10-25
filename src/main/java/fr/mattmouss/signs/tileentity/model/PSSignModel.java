package fr.mattmouss.signs.tileentity.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;

public class PSSignModel extends Model {
    private final RendererModel panel = new RendererModel(this,0,0);
    public PSSignModel() {
        initModel();
    }

    private void initModel(){
        panel.addBox(-17.0F, -32.0F, -2, 32, 32, 1);
    }

    public void renderSign(){
        panel.render(0.0625F);
    }
}

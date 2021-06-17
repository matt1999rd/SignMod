package fr.mattmouss.signs.tileentity.model;

import fr.mattmouss.signs.enums.PSDisplayMode;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;

public class PSSignModel extends Model {
    private final RendererModel panel = new RendererModel(this,0,0);
    public PSSignModel() {
        initModel();
    }

    private void initModel(){
        panel.addBox(-8,8,-2,16,16,1);
    }

    public void setTextureSize(PSDisplayMode mode){
        int texWidth = (mode.is2by2())? 64 : 96;
        int texHeight = 64;
        panel.setTextureSize(texWidth,texHeight);
    }

    public void renderSign(){
        panel.render(0.0625F);
    }
}

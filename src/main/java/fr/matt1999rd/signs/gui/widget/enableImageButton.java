package fr.matt1999rd.signs.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.matt1999rd.signs.util.Vector2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.util.ResourceLocation;

public class enableImageButton extends ImageButton {
    private final Vector2i inactivePosition;
    private final ResourceLocation resource;

    public enableImageButton(int posX, int posY, int buttonLength, int buttonHeight, int posU, int posV, int inactivePosU, int inactivePosV, int offsetVHovered, ResourceLocation resource, IPressable pressable) {
        super(posX, posY, buttonLength, buttonHeight, posU, posV, offsetVHovered,resource, 512,256, pressable); //512 and 256 is the texture total length
        inactivePosition = new Vector2i(inactivePosU,inactivePosV);
        this.resource = resource;
    }

    @Override
    public void renderButton(MatrixStack stack, int p_230431_2_, int p_230431_3_, float p_230431_4_) {
        if (!this.active && this.visible){
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bind(this.resource);
            RenderSystem.disableDepthTest();
            blit(stack,this.x, this.y, (float)this.inactivePosition.getX(), (float)inactivePosition.getY(), this.width, this.height, 512, 256);
            RenderSystem.enableDepthTest();
        }else {
            super.renderButton(stack, p_230431_2_, p_230431_3_, p_230431_4_);
        }
    }
}

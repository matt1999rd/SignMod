package fr.mattmouss.signs.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.gui.DirectionScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class DirectionCursorButton extends Button {

    ResourceLocation CURSOR_BUTTONS = new ResourceLocation(SignMod.MODID,"textures/gui/buttons.png");
    boolean isMoving = false;
    boolean isRightPosition = false;
    int cursorPos = 0;
    int ind;
    DirectionScreen screen;


    public DirectionCursorButton(int relX, int relY, IPressable onPress, DirectionScreen screen,int ind) {
        super(40, 20, relX, relY, "", onPress);
        this.screen = screen;
        this.ind = ind;
    }

    @Override
    public void renderButton(int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(CURSOR_BUTTONS);
        if (isMoving){
            cursorPos = MathHelper.clamp(cursorPos,0,28);
            if (isRightPosition){
                if (cursorPos == 28){
                    isMoving = false;
                } else {
                    cursorPos++;
                }
            } else {
                if (cursorPos == 0){
                    isMoving = false;
                } else {
                    cursorPos--;
                }
            }
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        //rendering of background
        this.blit(this.x,this.y,0,50,this.width,this.height);
        //rendering of th moving cursor
        this.blit(this.x+cursorPos+1,this.y,0,70,11,18);
    }

    @Override
    public void onPress() {
        isMoving = true;
        isRightPosition = !isRightPosition;
        screen.changeArrowSide(ind,isRightPosition);
    }
}

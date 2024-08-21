package fr.matt1999rd.signs.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.gui.DirectionScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import net.minecraft.network.chat.Component;


public class DirectionCursorButton extends Button {

    ResourceLocation CURSOR_BUTTONS = new ResourceLocation(SignMod.MODID,"textures/gui/buttons.png");
    boolean isMoving = false;
    boolean isRightPosition ;
    int cursorPos ;
    int ind;
    DirectionScreen screen;


    public DirectionCursorButton(int relX, int relY, OnPress onPress, DirectionScreen screen,int ind,boolean isRightPosition) {
        super(relX+251, relY+16+52*ind, 40, 20, Component.nullToEmpty(""), onPress);
        this.screen = screen;
        this.ind = ind;
        this.isRightPosition = isRightPosition;
        cursorPos = (this.isRightPosition) ? 28 : 0;
    }

    @Override
    public void renderButton(PoseStack stack,int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderTexture(0,CURSOR_BUTTONS);
        if (isMoving){
            cursorPos = Mth.clamp(cursorPos,0,28);
            if (isRightPosition){
                if (cursorPos == 28){
                    isMoving = false;
                } else {
                    cursorPos+=4;
                }
            } else {
                if (cursorPos == 0){
                    isMoving = false;
                } else {
                    cursorPos-=4;
                }
            }
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);
        //rendering of background
        this.blit(stack,this.x,this.y,0,50,this.width,this.height);
        //rendering of the moving cursor
        this.blit(stack,this.x+cursorPos+1,this.y+1,0,70,11,18);
    }

    @Override
    public void onPress() {
        rawOnPress();
        screen.updateOtherArrowSide(ind);
    }

    public void rawOnPress(){
        isMoving = true;
        isRightPosition = !isRightPosition;
        screen.changeArrowSide(ind,isRightPosition);
    }
}

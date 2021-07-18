package fr.mattmouss.signs.tileentity.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.PSDisplayMode;
import fr.mattmouss.signs.enums.PSPosition;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.tileentity.model.PSSignModel;
import fr.mattmouss.signs.tileentity.primary.PlainSquareSignTileEntity;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.QuadPSPosition;
import fr.mattmouss.signs.util.Text;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.Color;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class PlainSquareSignTileEntityRenderer extends TileEntityRenderer<PlainSquareSignTileEntity> {

    private final PSSignModel model = new PSSignModel();

    public PlainSquareSignTileEntityRenderer() {
    }

    @Override
    public void render(PlainSquareSignTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        BlockState blockstate = tileEntityIn.getBlockState();
        //code for display of background model
        GlStateManager.pushMatrix();
        //handle grid rotation for this block
        float angle= getAngleFromBlockState(blockstate);
        GlStateManager.translatef((float)x + 0.5F, (float)y , (float)z + 0.5F);
        GlStateManager.rotatef(angle, 0.0F, 1.0F, 0.0F);
        //code for changing background display
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0F, 2.0F, 1.0F);
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
            this.bindTexture(new ResourceLocation(SignMod.MODID,"textures/block/sign.png"));
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.5F,0.5F,0.5F);
        GlStateManager.translatef(1.0F/16.0F,2.0F,0.0F);
        this.model.renderSign();
        GlStateManager.scalef(2.0F,2.0F,2.0F);
        renderPicture(tileEntityIn);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
        if (destroyStage>=0){
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }

    }


    private float getAngleFromBlockState(BlockState blockstate) {
        Direction facing = blockstate.get(BlockStateProperties.HORIZONTAL_FACING);
        //to transform the horizontal index by a rotation angle that is proportionnal to 90°
        //make 0->2 1->1 2->0 3->3 --> 0->2 1->1 2->0 3-> (-1%4) --> (2-x)%4
        float angle = 90.0F * ((2-facing.getHorizontalIndex())%4);

        if (blockstate.get(GridSupport.ROTATED)){
            //if rotation add 45° rotation to the block
            return angle+45.0F;
        }
        return angle;
    }

    private void renderPicture(PlainSquareSignTileEntity psste){
        GlStateManager.translatef(-3.5F/16F,-21F/16F,0);
        Functions.setWorldGLState(false);
        Color color = psste.getBackgroundColor();
        renderQuad(0,16,0,16,color,0); //background rendering
        renderLimit(psste);
        if (psste.getPosition() == PSPosition.DOWN_RIGHT){
            GlStateManager.enableTexture();
            renderText(psste);
            renderScheme(psste);
        }
        Functions.resetWorldGLState();
    }

    private void renderScheme(PlainSquareSignTileEntity psste) {
        PSDisplayMode mode = psste.getMode();
        Color color = psste.getForegroundColor();
        ResourceLocation location = new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/ps_arrow.png");
        float texLength = 84.0F;
        float texHeight = 61.0F;
        switch (mode){
            case EXIT:
                float length = 12.0F;
                float xBase = 1.0F;
                float yBase = 25.0F;
                //rendering of the arrow
                renderQuadWithTexture(xBase,xBase+length/2,yBase,yBase+length/2,0.0F,length/texLength,14.0F/texHeight,(14.0F+length)/texHeight,color,2,location);
                break;
            case DIRECTION:
                //arrowId = 0 -> left arrow / 1 -> center arrow / 2 -> right arrow
                int arrowId = psste.getArrowId();
                if (arrowId == -1)return;
                float u1 = 20.0F*arrowId;
                float v1 = 0.0F;
                float u2 = 20.0F*(arrowId+1);
                float v2 = 14.0F;
                xBase = 2.0F;
                yBase = 1.5F;
                renderQuadWithTexture(xBase,xBase+10.0F,yBase,yBase+7.0F,u1/texLength,u2/texLength,v1/texHeight,v2/texHeight,color,2,location);
                xBase += 16.0F;
                renderQuadWithTexture(xBase,xBase+10.0F,yBase,yBase+7.0F,u1/texLength,u2/texLength,v1/texHeight,v2/texHeight,color,2,location);
                xBase += 16.0F;
                renderQuadWithTexture(xBase,xBase+10.0F,yBase,yBase+7.0F,u1/texLength,u2/texLength,v1/texHeight,v2/texHeight,color,2,location);
                break;
            case SCH_EXIT:
                xBase = 26.0F;
                yBase = 0.5F;
                u1= 12.0F;
                u2= 32.0F;
                v1= 14.0F;
                v2= 52.0F;
                //render of scheme
                renderQuadWithTexture(xBase,xBase+10.0F,yBase,yBase+19.0F,u1/texLength,u2/texLength,v1/texHeight,v2/texHeight,color,2,location);
                break;
            case SCH_MUL_EXIT:
                xBase = 10.5F;
                yBase = 0.5F;
                u1= 32.0F;
                u2= 84.0F;
                v1= 14.0F;
                v2= 61.0F;
                //render of scheme
                renderQuadWithTexture(xBase,xBase+26.0F,yBase,yBase+23.5F,u1/texLength,u2/texLength,v1/texHeight,v2/texHeight,color,2,location);
                break;
        }
    }

    private void renderLimit(PlainSquareSignTileEntity psste){
        Color color = psste.getForegroundColor();
        PSPosition position = psste.getPosition();
        float limitLength = 0.5F;
        float panelLength = 16.0F;
        if (position.isRight()){
            renderQuad(0,limitLength,0,panelLength,color,1); //left limit
        }
        if (position.isLeft()){
            renderQuad(panelLength-limitLength,panelLength,0,panelLength,color,1); //right limit
        }
        if (position.isUp()){
            renderQuad(0, panelLength, panelLength - limitLength, panelLength, color,1); //down limit
        }else {
            renderQuad(0,panelLength,0,limitLength,color,1); //up limit
        }
    }

    private void renderText(PlainSquareSignTileEntity psste){
        Color color = psste.getForegroundColor();
        PSDisplayMode mode = psste.getMode();
        List<QuadPSPosition> quadPositions = mode.getTextPosition();
        for (QuadPSPosition quadPosition : quadPositions){
            float xBase = quadPosition.getPosition().x;
            float yBase = quadPosition.getPosition().y;
            float maxLength = quadPosition.getLengthMax();
            int maxNumber = quadPosition.getMaxText();
            for (int i=0;i<maxNumber;i++){
                renderQuad(xBase,xBase+maxLength,yBase+4.5F*i,yBase+4.5F*i+3,color,1);
            }
        }
    }

    //when rendering quad be careful to use directly the value in the png folder (/2) and avoid dividing by 16
    private void renderQuad(float x1,float x2,float y1,float y2,Color color,int layer){
        float z = -0.06F-layer*0.001F;
        int red,green,blue,alpha;
        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        alpha = color.getAlpha();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(x2/16.0F, y2/16.0F, z).color(red, green, blue, alpha).endVertex();
        builder.pos(x2/16.0F, y1/16.0F, z).color(red, green, blue, alpha).endVertex();
        builder.pos(x1/16.0F, y1/16.0F, z).color(red, green, blue, alpha).endVertex();
        builder.pos(x1/16.0F, y2/16.0F, z).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
    }


    private void renderQuadWithTexture(float x1,float x2,float y1,float y2,float u1,float u2,float v1,float v2, Color color,int layer,ResourceLocation texture){
        Minecraft.getInstance().getTextureManager().bindTexture(texture);
        GlStateManager.color4f(1.0F,1.0F,1.0F,1.0F);
        float z = -0.06F-layer*0.001F;
        int red,green,blue,alpha;

        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        alpha = color.getAlpha();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        builder.pos(x2/16.0F, y2/16.0F, z).tex(u1, v1).color(red, green, blue, alpha).endVertex();
        builder.pos(x2/16.0F, y1/16.0F, z).tex(u1, v2).color(red, green, blue, alpha).endVertex();
        builder.pos(x1/16.0F, y1/16.0F, z).tex(u2, v2).color(red, green, blue, alpha).endVertex();
        builder.pos(x1/16.0F, y2/16.0F, z).tex(u2, v1).color(red, green, blue, alpha).endVertex();

        tessellator.draw();
    }

}

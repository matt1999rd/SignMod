package fr.mattmouss.signs.tileentity.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.tileentity.DirectionSignTileEntity;
import fr.mattmouss.signs.tileentity.EditingSignTileEntity;
import fr.mattmouss.signs.tileentity.model.DirectionSignModel;
import fr.mattmouss.signs.tileentity.model.SpecialSignModel;
import fr.mattmouss.signs.util.Functions;
import fr.mattmouss.signs.util.Text;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL;

public class DirectionSignTileEntityRenderer<T extends DirectionSignTileEntity> extends TileEntityRenderer<T> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/block/sign.png");
    private final DirectionSignModel model ;
    private final Form form;

    public DirectionSignTileEntityRenderer(Form form) {
        if (!form.isForDirection())throw new IllegalArgumentException("no such form are authorised in direction tileentity");
        model = new DirectionSignModel(form);
        this.form = form;
    }

    @Override
    public void render(DirectionSignTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
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
            this.bindTexture(BACKGROUND);
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.model.renderSign(tileEntityIn);
        if (destroyStage<0){
            GlStateManager.translatef(0,0,-2.001F/16F);
            GlStateManager.enableDepthTest();
            GlStateManager.disableTexture();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            renderBackground(tileEntityIn);
            //renderText(tileEntityIn);
        }
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

    private void renderBackground(DirectionSignTileEntity tileEntityIn) {
        GlStateManager.pushMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7,DefaultVertexFormats.POSITION_COLOR);
        int flag = tileEntityIn.getLFlag();
        int pow2=1;
        for (int i=0;i<7;i++){
            if ((flag&pow2) == pow2){
                renderPanel(builder,i,tileEntityIn);
            }
            pow2*=2;
        }
        tessellator.draw();
        Functions.resetWorldGLState();
        GlStateManager.popMatrix();
    }

    private void renderPanel(BufferBuilder builder,int ind,DirectionSignTileEntity tileEntity){
        float limLength = 0.1F/16F;
        float x1 = -7F/16F;
        float x2 = 7F/16F;
        int limColor;
        int bgColor;
        float y1,y2;
        switch (ind){
            case 0:
                //L1P1
                limColor = tileEntity.getColor(1,false);
                bgColor = tileEntity.getColor(1,true);
                y1 = 13F/16F;
                y2 = 15F/16F;
                break;
            case 1:
                //L1P2
                limColor = tileEntity.getColor(2,false);
                bgColor = tileEntity.getColor(2,true);
                y1 = 9F/16F;
                y2 = 11F/16F;
                break;
            case 2:
                //L1P3
                limColor = tileEntity.getColor(3,false);
                bgColor = tileEntity.getColor(3,true);
                y1 = 5F/16F;
                y2 = 7F/16F;
                break;
            case 3:
                //L3P12
                limColor = tileEntity.getColor(1,false);
                bgColor = tileEntity.getColor(1,true);
                y1 = 9F/16F;
                y2 = 15F/16F;
                break;
            case 4:
                //L3P23
                limColor = tileEntity.getColor(2,false);
                bgColor = tileEntity.getColor(2,true);
                y1 = 5F/16F;
                y2 = 11F/16F;
                break;
            case 5:
                //L5
                limColor = tileEntity.getColor(1,false);
                bgColor = tileEntity.getColor(1,true);
                y1 = 5F/16F;
                y2 = 15F/16F;
                break;
            default:
                SignMod.LOGGER.warn("Rendering not done : skip bad value of case !");
                return;
        }
        //center rectangle
        renderRectangle(builder,x1+limLength,y1+limLength,x2-limLength,y2-limLength,bgColor);
        //down limit
        renderRectangle(builder,x1,y1,x2,y1+limLength,limColor);
        //up limit
        renderRectangle(builder,x1,y2-limLength,x2,y2,limColor);
        //left limit
        renderRectangle(builder,x1,y1,x1+limLength,y2,limColor);
        //right limit
        renderRectangle(builder,x2-limLength,y1,x2,y2,limColor);
    }

    private void renderRectangle(BufferBuilder builder,float x1,float y1,float x2,float y2,int color){
        float z= -0.001F/16F;
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int rColor = Functions.getRedValue(color);
        int gColor = Functions.getGreenValue(color);
        int bColor = Functions.getBlueValue(color);
        int aColor = Functions.getAlphaValue(color);
        builder.pos(x1,y1,z).color(rColor,gColor,bColor,aColor).endVertex();
        builder.pos(x1,y2,z).color(rColor,gColor,bColor,aColor).endVertex();
        builder.pos(x2,y2,z).color(rColor,gColor,bColor,aColor).endVertex();
        builder.pos(x2,y1,z).color(rColor,gColor,bColor,aColor).endVertex();
    }

    private void renderText(DirectionSignTileEntity tileEntity){
        GlStateManager.pushMatrix();
        Functions.setWorldGLState();
        //Text t = tileEntity.getText();
        GlStateManager.enableTexture();
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        //t.render(builder);
        tessellator.draw();
        Functions.resetWorldGLState();
        GlStateManager.popMatrix();
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

}

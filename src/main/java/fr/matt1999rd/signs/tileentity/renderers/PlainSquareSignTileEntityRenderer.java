package fr.matt1999rd.signs.tileentity.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.PSDisplayMode;
import fr.matt1999rd.signs.fixedpanel.panelblock.PanelBlock;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.tileentity.model.PSSignModel;
import fr.matt1999rd.signs.tileentity.primary.PlainSquareSignTileEntity;
import fr.matt1999rd.signs.util.DrawUtils;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.util.PictureRenderState;
import fr.matt1999rd.signs.util.Text;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Matrix4f;
import net.minecraft.world.phys.Vec2;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.Color;

@OnlyIn(Dist.CLIENT)
public class PlainSquareSignTileEntityRenderer implements BlockEntityRenderer<PlainSquareSignTileEntity> {

    private final PSSignModel model = new PSSignModel();

    public PlainSquareSignTileEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(PlainSquareSignTileEntity tileEntityIn, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        BlockState blockstate = tileEntityIn.getBlockState();
        //code for display of background model
        stack.pushPose();
        //handle grid rotation for this block
        float angle= getAngleFromBlockState(blockstate);
        stack.translate(0.5F,0.0F,0.5F);
        Functions.rotate(stack, Direction.Axis.Y,angle);
        //code for changing background display
        stack.pushPose();
        stack.scale(0.5F,0.5F,0.5F);
        stack.translate(1.0F/16.0F,2.0F,0.0F);
        VertexConsumer vertexBuilder = buffer.getBuffer(model.renderType(Functions.SIGN_BACKGROUND));
        this.model.renderSign(stack,vertexBuilder,combinedLight,combinedOverlay);
        stack.scale(2.0F,2.0F,2.0F);
        renderPicture(stack,buffer,tileEntityIn,combinedLight);
        stack.popPose();
        RenderSystem.depthMask(true);
        stack.popPose();
    }


    private float getAngleFromBlockState(BlockState blockstate) {
        Direction facing = blockstate.getValue(BlockStateProperties.HORIZONTAL_FACING);
        //to transform the horizontal index by a rotation angle that is proportional to 90°
        //make 0->2 1->1 2->0 3->3 --> 0->2 1->1 2->0 3-> (-1%4) --> (2-x)%4
        float angle = 90.0F * ((2-facing.get2DDataValue())%4);

        if (blockstate.getValue(GridSupport.ROTATED)){
            //if rotation add 45° rotation to the block
            return angle+45.0F;
        }
        return angle;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    private void renderPicture(PoseStack stack, MultiBufferSource buffer, PlainSquareSignTileEntity psste, int combinedLight){
        stack.translate(-3.5F/16F,-21F/16F,0);
        VertexConsumer pictureBuffer = buffer.getBuffer(PictureRenderState.pictureRenderType());
        int color = psste.getBackgroundColor().getRGB();
        Matrix4f matrix4f = stack.last().pose();
        stack.translate(-5.0F/16F,5.0F/16F,-0.1F/16F);
        if (psste.getPosition() == PanelBlock.DEFAULT_RIGHT_POSITION){
            PSDisplayMode mode = psste.getMode();
            renderQuad(matrix4f,pictureBuffer,0,16*(mode.is2by2()?2:3),0,32,color,0,combinedLight); //background rendering
            renderLimit(matrix4f,pictureBuffer,mode.is2by2(),psste.getForegroundColor(),combinedLight);
            RenderSystem.enableTexture();
            Functions.setWorldGLState();
            renderText(stack,buffer,psste,combinedLight);
            Functions.resetWorldGLState();
            renderScheme(stack,buffer,psste,combinedLight);
        }
    }

    private void renderScheme(PoseStack stack,MultiBufferSource buffer,PlainSquareSignTileEntity psste,int combinedLight) {
        PSDisplayMode mode = psste.getMode();
        Color color = psste.getForegroundColor();
        ResourceLocation texLocation = new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/ps_arrow.png");
        VertexConsumer texBuilder = buffer.getBuffer(PictureRenderState.getTextureRenderType(texLocation));
        Matrix4f matrix4f = stack.last().pose();
        float texLength = 84.0F;
        float texHeight = 61.0F;
        float xBase = mode.getTextureXOrigin();
        float yBase = mode.getTextureYOrigin();
        float length = mode.getTexLength();
        float height = mode.getTexHeight();
        Vec2 uvOrigin = mode.getUVOrigin(psste.getArrowId());
        Vec2 uvDimension = mode.getUVDimension();
        float panelLength = ((mode.is2by2())?2:3)*16.0F;
        float panelHeight = 2*16.0F;
        float spaceBetweenArrow = 7.0F;
        renderQuadWithTexture(matrix4f,texBuilder,
                panelLength-xBase, panelLength-xBase-length, panelHeight-yBase,panelHeight-yBase-height,
                uvOrigin.x/texLength,(uvOrigin.x+uvDimension.x)/texLength,
                uvOrigin.y/texHeight,(uvOrigin.y+uvDimension.y)/texHeight,
                color,2,combinedLight);
        if (mode == PSDisplayMode.DIRECTION){
            renderQuadWithTexture(matrix4f,texBuilder,
                    panelLength-xBase-spaceBetweenArrow-length,panelLength-xBase-spaceBetweenArrow-2*length,
                    panelHeight-yBase, panelHeight-yBase-height,
                    uvOrigin.x/texLength,(uvOrigin.x+uvDimension.x)/texLength,
                    uvOrigin.y/texHeight,(uvOrigin.y+uvDimension.y)/texHeight,
                    color,2,combinedLight);
            renderQuadWithTexture(matrix4f,texBuilder,
                    panelLength-xBase-2*(spaceBetweenArrow+length), panelLength-xBase-2*(spaceBetweenArrow+length)-length,
                    panelHeight-yBase,panelHeight-yBase-height,
                    uvOrigin.x/texLength,(uvOrigin.x+uvDimension.x)/texLength,
                    uvOrigin.y/texHeight,(uvOrigin.y+uvDimension.y)/texHeight,
                    color,2,combinedLight);
        }
    }

    private void renderLimit(Matrix4f matrix4f, VertexConsumer builder,  boolean is2by2, Color foregroundColor, int combinedLight){
        float limitLength = 0.5F;
        float panelLength = (is2by2?2:3)*16.0F;
        float panelHeight = 2*16.0F;
        int color = foregroundColor.getRGB();
        renderQuad(matrix4f,builder,0, limitLength,0,panelHeight,color,1,combinedLight); //right limit
        renderQuad(matrix4f,builder,panelLength-limitLength,panelLength,0,panelHeight,color,1,combinedLight); //left limit
        renderQuad(matrix4f,builder,0, panelLength,panelHeight - limitLength, panelHeight, color,1,combinedLight); //up limit
        renderQuad(matrix4f,builder,0, panelLength,0,limitLength,color,1,combinedLight); //down limit
    }

    private void renderText(PoseStack stack, MultiBufferSource buffer, PlainSquareSignTileEntity psste, int combinedLight){
        stack.pushPose();
        float z = -0.061F- 2 *0.001F;
        float pixelLength = 1.0F/32.0F;
        PSDisplayMode mode = psste.getMode();
        int nbText = mode.getTotalText();
        int xOrigin = (mode.is2by2())? 2 : 3;
        Vector3f origin = new Vector3f(xOrigin,2,z);
        Vec2 scale = new Vec2(pixelLength,pixelLength);
        for (int i=0;i<nbText;i++){
            Text t=psste.getText(i);
            t.render(stack,buffer,origin,scale,combinedLight);
        }
        stack.popPose();
    }

    //when rendering quad be careful to use directly the value in the png folder (/2) and avoid dividing by 16
    private void renderQuad(Matrix4f matrix4f, VertexConsumer builder,float x1, float x2, float y1, float y2, int color, int layer, int combinedLight){
        float z = -0.061F-layer*0.001F;
        DrawUtils.renderQuad(matrix4f,builder,x1/16.0F,y1/16.0F,x2/16.F,y2/16.0F,z,color,combinedLight);
    }


    private void renderQuadWithTexture(Matrix4f matrix4f,VertexConsumer builder,float x1,float x2,float y1,float y2,float u1,float u2,float v1,float v2, Color color,int layer,int combinedLight){
        //todo : change the argument place and position information to have a better understanding of the action made
        float z = -0.06F-layer*0.001F;
        int red,green,blue,alpha;
        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        alpha = color.getAlpha();
        builder.vertex(matrix4f,x1/16.0F, y1/16.0F, z).color(red, green, blue, alpha).uv(u1, v1).uv2(combinedLight).endVertex();
        builder.vertex(matrix4f,x1/16.0F, y2/16.0F, z).color(red, green, blue, alpha).uv(u1, v2).uv2(combinedLight).endVertex();
        builder.vertex(matrix4f,x2/16.0F, y2/16.0F, z).color(red, green, blue, alpha).uv(u2, v2).uv2(combinedLight).endVertex();
        builder.vertex(matrix4f,x2/16.0F, y1/16.0F, z).color(red, green, blue, alpha).uv(u2, v1).uv2(combinedLight).endVertex();
    }

}

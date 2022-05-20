package fr.matt1999rd.signs.tileentity.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.tileentity.DirectionSignTileEntity;
import fr.matt1999rd.signs.tileentity.model.DirectionSignModel;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.util.PictureRenderState;
import fr.matt1999rd.signs.util.Text;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import static fr.matt1999rd.signs.util.DirectionSignConstants.*;

public class DirectionSignTileEntityRenderer<T extends DirectionSignTileEntity> extends TileEntityRenderer<T> {

    private final DirectionSignModel model ;
    private final Form form;
    private static final float blockLength = 16F;

    public DirectionSignTileEntityRenderer(TileEntityRendererDispatcher dispatcher, Form form) {
        super(dispatcher);
        if (!form.isForDirection())throw new IllegalArgumentException("no such form are authorised in direction tileentity");
        model = new DirectionSignModel(form);
        this.form = form;
    }

    @Override
    public void render(DirectionSignTileEntity tileEntityIn, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        BlockState blockstate = tileEntityIn.getBlockState();
        //code for display of background model
        stack.pushPose();
        //handle grid rotation for this block
        float angle= getAngleFromBlockState(blockstate);
        stack.translate(0.5F,0.0F, 0.5F);
        Functions.rotate(stack, Direction.Axis.Y, angle);
        //code for changing background display
        stack.pushPose();

        IVertexBuilder vertexBuilder = buffer.getBuffer(model.renderType(Functions.SIGN_BACKGROUND));
        this.model.renderSign(stack,tileEntityIn,vertexBuilder,combinedLight,combinedOverlay);
        stack.translate(-totalLength /(2*blockLength),totalHeight/(2*blockLength),-2.001F/blockLength);

        renderBackground(stack,tileEntityIn,buffer,combinedLight);
        stack.translate(0,0,-0.1F/blockLength);
        renderText(stack,buffer,tileEntityIn,combinedLight);

        stack.popPose();
        RenderSystem.depthMask(true);
        stack.popPose();
    }

    private void renderBackground(MatrixStack stack,DirectionSignTileEntity tileEntityIn,IRenderTypeBuffer buffer,int combinedLight) {
        stack.pushPose();
        IVertexBuilder pictureBuilder =  buffer.getBuffer(PictureRenderState.pictureRenderType);

        int flag = tileEntityIn.getLFlag();
        int pow2=1;
        Matrix4f matrix4f = stack.last().pose();
        for (int i=0;i<7;i++){
            if ((flag&pow2) == pow2){
                renderPanel(matrix4f,pictureBuilder,i,tileEntityIn,combinedLight);
            }
            pow2*=2;
        }
        stack.popPose();
    }

    private void renderPanel(Matrix4f matrix4f,IVertexBuilder builder,int ind,DirectionSignTileEntity tileEntity,int combinedLight){
        float limLength = 0.1F/blockLength;
        float x1 = 0;
        float x2 = totalLength/blockLength;
        int panelInd;
        int panelLength;
        int colorInd;
        float y1,y2;
        if (ind <0 || ind > 5){
            SignMod.LOGGER.warn("Rendering not done : skip bad value of case !");
            return;
        }else if (ind < 3){
            panelLength = 1;
        }else if (ind < 5){
            ind -= 3;
            panelLength = 3;
        }else {
            ind -= 5;
            panelLength = 5;
        }

        y2 = (totalHeight - ind * (singlePanelHeight + gapBtwPanelHeight)) / blockLength;
        y1 = y2 - panelLength*singlePanelHeight/blockLength;
        colorInd = ind +1;
        panelInd = 2*ind;

        int limColor = tileEntity.getColor(colorInd,false);
        int bgColor = tileEntity.getColor(colorInd,true);
        //center rectangle
        renderRectangle(matrix4f,builder,x1+limLength,y1+limLength,x2-limLength,y2-limLength,bgColor,combinedLight);
        //down limit
        renderRectangle(matrix4f,builder,x1,y1,x2,y1+limLength,limColor,combinedLight);
        //up limit
        renderRectangle(matrix4f,builder,x1,y2-limLength,x2,y2,limColor,combinedLight);
        if (form == Form.ARROW){
            boolean isRightArrow = tileEntity.isRightArrow(panelInd);
            int rightColor = (isRightArrow) ? bgColor : limColor;
            int leftColor= (isRightArrow) ? limColor  : bgColor;
            float xDiff = -limLength;
            //right limit
            renderRectangle(matrix4f,builder, x1, y1+limLength, x1 + limLength, y2-limLength, rightColor,combinedLight);
            //left limit
            renderRectangle(matrix4f,builder, x2 - limLength, y1+limLength, x2, y2-limLength, leftColor, combinedLight);
            float xCommon = totalLength/blockLength;
            float xSolo = xCommon+panelLength*1F/blockLength;
            float ySolo = (y1+y2)/2;
            if (isRightArrow){
                xCommon = totalLength/blockLength-xCommon;
                xSolo = totalLength/blockLength - xSolo;
                xDiff *= -1;
            }
            //arrow of limit
            renderTriangle(matrix4f,builder,xCommon,xSolo,y1,ySolo,y2,limColor,combinedLight,false);
            //arrow of background
            renderTriangle(matrix4f,builder,xCommon,xSolo+xDiff,y1+limLength,ySolo,y2-limLength,bgColor,combinedLight,true);
        }else {
            //left limit
            renderRectangle(matrix4f,builder, x1, y1, x1 + limLength, y2, limColor,combinedLight);
            //right limit
            renderRectangle(matrix4f,builder, x2 - limLength, y1, x2, y2, limColor,combinedLight);
        }
    }


    private void renderRectangle(Matrix4f matrix4f,IVertexBuilder builder,float x1,float y1,float x2,float y2,int color,int combinedLight){
        float z= -0.001F/16F;
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        int rColor = Functions.getRedValue(color);
        int gColor = Functions.getGreenValue(color);
        int bColor = Functions.getBlueValue(color);
        int aColor = Functions.getAlphaValue(color);
        builder.vertex(matrix4f,x1,y1,z).color(rColor,gColor,bColor,aColor).uv2(combinedLight).endVertex();
        builder.vertex(matrix4f,x1,y2,z).color(rColor,gColor,bColor,aColor).uv2(combinedLight).endVertex();
        builder.vertex(matrix4f,x2,y2,z).color(rColor,gColor,bColor,aColor).uv2(combinedLight).endVertex();
        builder.vertex(matrix4f,x2,y1,z).color(rColor,gColor,bColor,aColor).uv2(combinedLight).endVertex();
    }

    //function to render triangle with a y axes direction as one of its three sides
    // the three point are A(xCommon,yDown) , B(xSolo,ySolo) and C(xCommon,yUp)
    // the point H(xCommon,ySolo) is the intersection of the altitude from B and the opposite side of the triangle
    private void renderTriangle(Matrix4f matrix4f,IVertexBuilder builder,float xCommon,float xSolo,float yDown,float ySolo,float yUp,int color,int combinedLight,boolean isBgColor){
        float z = (isBgColor)? -0.002F/16F :-0.001F/16F;
        RenderSystem.color4f(1.0F,1.0F,1.0F,1.0F);
        int rColor = Functions.getRedValue(color);
        int gColor = Functions.getGreenValue(color);
        int bColor = Functions.getBlueValue(color);
        int aColor = Functions.getAlphaValue(color);

        //startYUp boolean indicates the first vertex to draw in order to see the triangle (always render clockwise)
        // we always draw A first
        // startYUp = false
        /*
              C
           🡽 🡻
         🡽   🡻
        B     H
         🡼   🡻
           🡼 🡻
              A
         */
        // startYUp = true
        /*
         C
         🡹 🡾
         🡹   🡾
         H     B
         🡹   🡿
         🡹 🡿
         A
         */
        boolean startYUp = (xCommon<xSolo);
        builder.vertex(matrix4f,xCommon,yDown,z).color(rColor,gColor,bColor,aColor).uv2(combinedLight).endVertex(); //draw A
        if (startYUp){
            builder.vertex(matrix4f,xCommon,ySolo,z).color(rColor,gColor,bColor,aColor).uv2(combinedLight).endVertex(); //draw H
            builder.vertex(matrix4f,xCommon,yUp,z).color(rColor,gColor,bColor,aColor).uv2(combinedLight).endVertex(); //draw C
            builder.vertex(matrix4f,xSolo,ySolo,z).color(rColor,gColor,bColor,aColor).uv2(combinedLight).endVertex(); //draw B
        }else {
            builder.vertex(matrix4f,xSolo,ySolo,z).color(rColor,gColor,bColor,aColor).uv2(combinedLight).endVertex(); //draw B
            builder.vertex(matrix4f,xCommon,yUp,z).color(rColor,gColor,bColor,aColor).uv2(combinedLight).endVertex(); //draw C
            builder.vertex(matrix4f,xCommon,ySolo,z).color(rColor,gColor,bColor,aColor).uv2(combinedLight).endVertex(); //draw H
        }

    }

    private void renderText(MatrixStack stack,IRenderTypeBuffer buffer,DirectionSignTileEntity tileEntity,int combinedLight){
        boolean isTextCentered = tileEntity.isTextCentered();
        for (int i=0;i<5;i++) {
            if (tileEntity.isCellPresent(i)) {
                renderSpecificText(stack,buffer,i, false, tileEntity,combinedLight);
                if (!isTextCentered)renderSpecificText(stack,buffer,i, true, tileEntity,combinedLight);
            }
        }
    }

    private void renderSpecificText(MatrixStack stack,IRenderTypeBuffer buffer,int ind,boolean isEnd,DirectionSignTileEntity tileEntity,int combinedLight){
        stack.pushPose();
        Functions.setWorldGLState();
        Text t = tileEntity.getText(ind,isEnd);
        Vector3f origin = new Vector3f(totalLength/blockLength,totalHeight/blockLength,0.005F);
        float pixelLength = totalHeight/blockLength / verPixelNumber;
        Vector2f scale = new Vector2f(pixelLength,pixelLength);
        t.render(stack,buffer,origin,scale,combinedLight);
        Functions.resetWorldGLState();
        stack.popPose();
    }

    private float getAngleFromBlockState(BlockState blockstate) {
        Direction facing = blockstate.getValue(BlockStateProperties.HORIZONTAL_FACING);
        //to transform the horizontal index by a rotation angle that is proportionnal to 90°
        //make 0->2 1->1 2->0 3->3 --> 0->2 1->1 2->0 3-> (-1%4) --> (2-x)%4
        float angle = 90.0F * ((2-facing.get2DDataValue())%4);
        if (blockstate.getValue(GridSupport.ROTATED)){
            //if rotation add 45° rotation to the block
            return angle+45.0F;
        }
        return angle;
    }

}

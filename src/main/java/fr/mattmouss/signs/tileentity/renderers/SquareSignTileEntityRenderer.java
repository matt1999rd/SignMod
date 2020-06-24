package fr.mattmouss.signs.tileentity.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.SignMod;
import fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.tileentity.model.SquareSignModel;
import fr.mattmouss.signs.tileentity.primary.SquareSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SquareSignTileEntityRenderer extends TileEntityRenderer<SquareSignTileEntity> {
    private static final ResourceLocation SQUARE_BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/square.png");
    private final SquareSignModel model = new SquareSignModel();

    public SquareSignTileEntityRenderer() {
    }

    @Override
    public void render(SquareSignTileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
        BlockState blockstate = tileEntityIn.getBlockState();
        //code for display of background model
        GlStateManager.pushMatrix();
        //handle grid rotation for this block
        float angle= getAngleFromBlockState(blockstate);
        GlStateManager.translatef((float)x + 0.5F, (float)y , (float)z + 0.5F);
        if (blockstate.get(AbstractPanelBlock.GRID)){
            this.model.getGrid().showModel = true;
            this.model.getSupport().showModel = false;
        }else {
            //nothing to handle because support is not changing with blockstate
            this.model.getGrid().showModel = false;
            this.model.getSupport().showModel = true;
        }
        GlStateManager.rotatef(angle, 0.0F, 1.0F, 0.0F);
        //code for changing background display
        //todo : complete devellopement of render function
        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0F, 2.0F, 1.0F);
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        } else {
            this.bindTexture(SQUARE_BACKGROUND);
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        this.model.renderSign();
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
        float angle =0.0F;
        if (blockstate.get(BlockStateProperties.HORIZONTAL_FACING).getAxis() == Direction.Axis.Z){
            //axe NS -> 90° angle in addition to the rotation state
            angle+=90.0F;
        }

        if (blockstate.get(GridSupport.ROTATED)){
            //if rotation add 45° rotation to the block
            return angle+45.0F;
        }
        return angle;
    }

}

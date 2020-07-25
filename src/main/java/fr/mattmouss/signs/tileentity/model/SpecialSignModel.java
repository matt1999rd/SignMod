package fr.mattmouss.signs.tileentity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import fr.mattmouss.signs.enums.ExtendDirection;
import fr.mattmouss.signs.enums.Form;
import fr.mattmouss.signs.util.Functions;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelBox;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.image.BufferedImage;

import static fr.mattmouss.signs.fixedpanel.panelblock.AbstractPanelBlock.GRID;
import static fr.mattmouss.signs.fixedpanel.support.GridSupport.ROTATED;

@OnlyIn(Dist.CLIENT)
public class SpecialSignModel extends Model {
    private final RendererModel panel = new RendererModel(this,0,0);
    private final RendererModel grid = new RendererModel(this,0,0);
    private final RendererModel grid_rotated = new RendererModel(this,0,0);
    private final RendererModel support_post = new RendererModel(this,0,0);
    private final RendererModel support_side = new RendererModel(this,0,0);
    private final RendererModel support_diag = new RendererModel(this,0,0);
    private final Form form;

    public SpecialSignModel(Form form) {
        this.form = form;
        initModel();
    }

    private void initModel(){
        for (int i=0;i<128;i++){
            int j_beg =0,j_end=127;
            boolean isIn = false;
            boolean hasChanged = false;
            for (int j=0;j<128;j++){
                //if we arrived on the form space
                if (form.isIn(i,j) && !isIn){
                    j_beg = j;
                    hasChanged = true;
                }
                //if we quit the form space
                if (!form.isIn(i,j) && isIn){
                    j_end = j;
                    hasChanged = true;
                }
                isIn = form.isIn(i,j);
            }
            if (hasChanged){
                this.addPixelRow(i,j_beg,j_end);
            }
        }
        //initGridModel();
        //initSupportModel();

    }

    private void initSupportModel() {
        support_post.addBox(-1,0,-1,2,16,2);
        support_side.addBox(0,12,-8,0,2,7);
        support_side.addBox(0,2,-8,0,2,7);
        support_side.addBox(0,4,-5,0,8,2);
        support_side.addBox(0,4,-8,0,8,1);
        support_diag.addBox(0,12,-11,0,2,10);
        support_diag.addBox(0,2,-11,0,2,10);
        support_diag.addBox(0,4,-4.4142F,0,8,2);
        support_diag.addBox(0,4,-8.02426F,0,8,2);
        support_diag.addBox(0,4,-11,0,8,1);
    }

    private void initGridModel(){
        grid.addBox(0,2,-8,1,2,16);
        grid.addBox(0,12,-8,1,2,16);
        grid.addBox(0,4,-8,1,8,1);
        grid.addBox(0,4,-5,1,8,2);
        grid.addBox(0,4,-1,1,8,2);
        grid.addBox(0,4,3,1,8,2);
        grid.addBox(0,4,7,1,8,1);
        grid.rotateAngleY = (float)Math.PI/2;
        grid_rotated.addBox(0,2,-12,1,2,24);
        grid_rotated.addBox(0,12,-12,1,2,24);
        grid_rotated.addBox(0,4,-12,1,8,1);
        grid_rotated.addBox(0,4,11,1,8,1);
        grid_rotated.addBox(0,4,-9,1,8,2);
        grid_rotated.addBox(0,4,-5,1,8,2);
        grid_rotated.addBox(0,4,-1,1,8,2);
        grid_rotated.addBox(0,4,3,1,8,2);
        grid_rotated.addBox(0,4,7,1,8,2);
        grid_rotated.rotateAngleY = (float)Math.PI/2;
    }

    private void addPixelRow(int i, int j_beg, int j_end){
        float completeLength = form.getCompleteLength();
        float pixelLength = completeLength/128;
        panel.addBox(-5/pixelLength+i,5/pixelLength+10/pixelLength*(1-(j_end+1)/128.0F),-2,1,(j_end-j_beg+1),1);
    }

    public void renderSign(BlockState state){
        float completeLength = form.getCompleteLength();
        float pixelLength = completeLength/128;
        GlStateManager.scalef(pixelLength,pixelLength,1.0F);
        panel.render(0.0625F);
        GlStateManager.scalef(1/pixelLength,1/pixelLength,1.0F);
        /*
        if (state.get(GRID)){
            renderGrid(state);
        }else {
            renderSupport(state);
        }
         */
    }

    /*
    private void renderSupport(BlockState state) {
        if (state.get(ROTATED)){
            support_post.rotateAngleY = (float) (Math.PI/4);
        }else {
            support_post.rotateAngleY = 0.0F;
        }
        support_post.render(0.0625F);
        Direction facing = state.get(BlockStateProperties.HORIZONTAL_FACING);
        boolean[] flags = Functions.getFlagsFromState(state);
        for (int i=0;i<flags.length;i++){
            ExtendDirection direction = ExtendDirection.byIndex(i);
            boolean rotated = direction.isRotated();
            if (flags[i]) {
                //both side and diag model is apply without (all or multiple) rotation if the flags is for west direction
                float angle = direction.getAngleFrom(facing);
                if (rotated) {
                    support_diag.rotateAngleY = angle;
                    support_diag.render(0.0625F);
                } else {
                    //the side model is apply without rotation if the flags is for west direction
                    support_side.rotateAngleY = angle;
                    support_side.render(0.0625F);
                }
            }
        }
    }

    private void renderGrid(BlockState state){
        //rotation are already done by GlStateManager and need only to rotate of 90° because model is not on the right way
        if (state.get(ROTATED)){
            grid_rotated.render(0.0625F);
        }else {
            grid.render(0.0625F);
        }
    }


    public RendererModel getGrid() {
        return grid_background;
    }

    public RendererModel getSupport() {
        return support_background;
    }

     */
}

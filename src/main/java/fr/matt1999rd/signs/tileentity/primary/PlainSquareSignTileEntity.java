package fr.matt1999rd.signs.tileentity.primary;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.PSDisplayMode;
import fr.matt1999rd.signs.enums.PSPosition;
import fr.matt1999rd.signs.fixedpanel.ModBlock;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.matt1999rd.signs.fixedpanel.panelblock.PanelBlock;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.tileentity.PanelTileEntity;
import fr.matt1999rd.signs.tileentity.TEType;
import fr.matt1999rd.signs.util.Functions;
import fr.matt1999rd.signs.util.Text;
import fr.matt1999rd.signs.capabilities.PSCapability;
import fr.matt1999rd.signs.capabilities.PSStorage;
import fr.matt1999rd.signs.util.Vector2i;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;


public class PlainSquareSignTileEntity extends PanelTileEntity {

    private final LazyOptional<PSStorage> storage = LazyOptional.of(this::getStorage).cast();

    public static final int SCREEN_LENGTH = 128;

    //operation id is a code to indicate what action is done
    // 0 -> change display mode with left reduce button
    // 1 -> change display mode with right reduce button
    // 2 -> change arrow direction
    // 3 -> change bg color
    // 4 -> change edging color
    public static final int SET_MODE_WITH_LEFT_SELECTED = 0;
    public static final int SET_MODE_WITH_RIGHT_SELECTED = 1;
    public static final int SET_ARROW_ID = 2;
    public static final int CHANGE_BG_COLOR = 3;
    public static final int CHANGE_EDGING_COLOR = 4;

    public PlainSquareSignTileEntity() {
        super(TEType.PLAIN_SQUARE_SIGN);
    }

    @Override
    public double getViewDistance() {
        return 256.0D;
    }

    @Override
    public void tick() {
        BlockState state = getBlockState();
        if (!state.getValue(AbstractPanelBlock.GRID)){
            //if it is a support with grid
            super.tick();
        }
    }

    private PSStorage getStorage() {
        return new PSStorage();
    }

    public void renderOnScreen(MatrixStack stack,int guiLeft, int guiTop, int selTextIndex) {
        //display background
        AbstractGui.fill(stack,guiLeft,guiTop,guiLeft+SCREEN_LENGTH,guiTop+SCREEN_LENGTH,getBackgroundColor().getRGB());
        renderLimit(stack,guiLeft,guiTop);
        renderText(stack,guiLeft,guiTop,selTextIndex);
        renderScheme(guiLeft,guiTop);
    }

    private void renderLimit(MatrixStack stack,int guiLeft, int guiTop){
        int fg_color = getForegroundColor().getRGB();
        int limitLength = 2;
        //optimised so that no bar are recovered (bar of size sl-2 and 2)
        //top
        AbstractGui.fill(stack,guiLeft,guiTop,guiLeft+SCREEN_LENGTH-limitLength,guiTop+limitLength,fg_color);
        //right
        AbstractGui.fill(stack,guiLeft+SCREEN_LENGTH-limitLength,guiTop,guiLeft+SCREEN_LENGTH,guiTop+SCREEN_LENGTH-limitLength,fg_color);
        //bottom
        AbstractGui.fill(stack,guiLeft+limitLength,guiTop+SCREEN_LENGTH-limitLength,guiLeft+SCREEN_LENGTH,guiTop+SCREEN_LENGTH,fg_color);
        //left
        AbstractGui.fill(stack,guiLeft,guiTop+limitLength,guiLeft+limitLength,guiTop+SCREEN_LENGTH,fg_color);
    }

    private void renderText(MatrixStack stack,int guiLeft, int guiTop,int selTextIndex){
        PSDisplayMode mode = getMode();
        float scaleX = SCREEN_LENGTH/(mode.is2by2()?64.0F:96.0F);
        float scaleY = SCREEN_LENGTH/64.0F;
        Vector2i origin = new Vector2i(guiLeft,guiTop);
        Vector2f scale = new Vector2f(scaleX,scaleY);
        for (int i=0;i<mode.getTotalText();i++){
            Text t = getText(i);
            if (t.isEmpty()){
                renderEmptyTextRectangle(stack,guiLeft,guiTop,i,scaleX,scaleY,i == selTextIndex);
            }else {
                Text rescaleText = new Text(t);
                rescaleText.renderOnScreen(stack, origin, scale,i == selTextIndex,true);
            }
        }
    }

    private void renderScheme(int guiLeft, int guiTop){
        PSDisplayMode mode = getMode();
        RenderSystem.enableBlend();
        float scaleX =2*SCREEN_LENGTH/(mode.is2by2()?64.0F:96.0F);
        float scaleY =2*SCREEN_LENGTH/64.0F;
        float xBase = guiLeft+mode.getTextureXOrigin()*scaleX;
        float yBase = guiTop+ mode.getTextureYOrigin()*scaleY;
        float length = mode.getTexLength()*scaleX;
        float height = mode.getTexHeight()*scaleY;
        Vector2f uvOrigin = mode.getUVOrigin(getArrowId());
        Vector2f uvDimension = mode.getUVDimension();
        renderTexture(
                xBase,yBase,xBase+length,yBase+height,
                uvOrigin.x,uvOrigin.y,uvOrigin.x+uvDimension.x,uvOrigin.y+uvDimension.y);
        if (mode == PSDisplayMode.DIRECTION){
            float spaceBetweenArrow = 7.0F*scaleX;
            renderTexture(
                    xBase+spaceBetweenArrow+length,yBase,
                    xBase+spaceBetweenArrow+2*length,yBase+height,
                    uvOrigin.x, uvOrigin.y,
                    uvOrigin.x+uvDimension.x,uvOrigin.y+uvDimension.y
            );
            renderTexture(
                    xBase+2*(spaceBetweenArrow+length),yBase,
                    xBase+2*(spaceBetweenArrow+length)+length,yBase+height,
                    uvOrigin.x, uvOrigin.y,
                    uvOrigin.x+uvDimension.x,uvOrigin.y+uvDimension.y
            );
        }
    }

    private void renderTexture(float x1,float y1,float x2,float y2,float u1,float v1,float u2,float v2){
        ResourceLocation location = new ResourceLocation(SignMod.MODID,"textures/tileentityrenderer/ps_arrow.png");
        float texLength = 84.0F;
        float texHeight = 61.0F;
        Color color = getForegroundColor();
        Minecraft.getInstance().getTextureManager().bind(location);
        RenderSystem.color4f(1.0F,1.0F,1.0F,1.0F);
        int red,green,blue,alpha;

        red = color.getRed();
        green = color.getGreen();
        blue = color.getBlue();
        alpha = color.getAlpha();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);

        builder.vertex(x1, y1, 0.0F).color(red, green, blue, alpha).uv(u1/texLength, v1/texHeight).endVertex();
        builder.vertex(x1, y2, 0.0F).color(red, green, blue, alpha).uv(u1/texLength, v2/texHeight).endVertex();
        builder.vertex(x2, y2, 0.0F).color(red, green, blue, alpha).uv(u2/texLength, v2/texHeight).endVertex();
        builder.vertex(x2, y1, 0.0F).color(red, green, blue, alpha).uv(u2/texLength, v1/texHeight).endVertex();

        tessellator.end();
    }

    private void renderRectangle(MatrixStack stack,Rectangle2D rectangle2D,Color color){
        Tessellator tessellator = Tessellator.getInstance();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        Matrix4f matrix4f = stack.last().pose();
        BufferBuilder builder = tessellator.getBuilder();
        builder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();
        builder.vertex(matrix4f, (float) rectangle2D.getMinX(), (float) rectangle2D.getMinY(),0).color(red,green,blue,alpha).endVertex();
        builder.vertex(matrix4f, (float) rectangle2D.getMinX(), (float) rectangle2D.getMaxY(),0).color(red,green,blue,alpha).endVertex();
        builder.vertex(matrix4f, (float) rectangle2D.getMaxX(), (float) rectangle2D.getMaxY(),0).color(red,green,blue,alpha).endVertex();
        builder.vertex(matrix4f, (float) rectangle2D.getMaxX(), (float) rectangle2D.getMinY(),0).color(red,green,blue,alpha).endVertex();
        tessellator.end();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private void renderEmptyTextRectangle(MatrixStack stack, int guiLeft, int guiTop, int ind, float scaleX, float scaleY, boolean isSelected) {
        Rectangle2D.Float rectangle = getTextArea(guiLeft, guiTop, ind,scaleX,scaleY);
        Color bgColor = getBackgroundColor();
        Color emptyRectangleColor;
        if (Functions.colorDistance(bgColor,Color.DARK_GRAY) < 10F || Functions.colorDistance(bgColor,Color.GRAY) < 10F){
            emptyRectangleColor = (isSelected) ? Color.BLACK : Color.WHITE;
        }else {
            emptyRectangleColor = (isSelected) ? Color.DARK_GRAY : Color.GRAY;
        }
        this.renderRectangle(stack,rectangle,emptyRectangleColor);
    }

    public Rectangle2D.Float getTextArea(int guiLeft, int guiTop, int ind, float scaleX, float scaleY){
        Vector2i position = this.getMode().getTextBegPosition(ind);
        float x1 = guiLeft + position.getX()*scaleX -1;
        float y1 = guiTop + position.getY()*scaleY -1;
        float length = this.getMode().getMaxLength(ind) * scaleX + 1;
        float height = 7*scaleY +1;
        return new Rectangle2D.Float(x1,y1,length,height);
    }


    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 9, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT nbt = pkt.getTag();
        this.load(this.getBlockState(),nbt);
    }

    public void registerData(PSPosition position, PSDisplayMode mode){
        storage.ifPresent(psStorage -> psStorage.setInternVariable(position,mode));
        for (int i=0;i<mode.getTotalText();i++){
            Text t = Text.getDefaultText(mode.getTextBegPosition(i),getForegroundColor());
            setText(t,i);
        }
    }

    private void copyData(PlainSquareSignTileEntity dataTile, PSPosition newPosition) {
        storage.ifPresent(psStorage -> {
            psStorage.setPosition(newPosition);
            for (int i=0;i<6;i++){
                psStorage.setText(new Text(dataTile.getText(i)),i);
            }
            psStorage.setDisplayMode(dataTile.getMode());
            psStorage.setForegroundColor(dataTile.getForegroundColor().getRGB());
            psStorage.setBackgroundColor(dataTile.getBackgroundColor().getRGB());
            psStorage.setArrowId(dataTile.getArrowId());
        });
    }

    public PSPosition getPosition(){
        return storage.map(PSStorage::getPosition).orElse(PSPosition.DOWN_LEFT);
    }

    public void setPosition(PSPosition position){
        storage.ifPresent(psStorage -> psStorage.setPosition(position));
    }

    public PSDisplayMode getMode(){
        return storage.map(PSStorage::getDisplayMode).orElse(PSDisplayMode.DIRECTION);
    }

    public void setMode(PSDisplayMode newMode,boolean rightSelected){
        PSDisplayMode oldMode = getMode();
        if (this.getPosition() == PanelBlock.DEFAULT_RIGHT_POSITION){
            //update of neighbor block
            List<PlainSquareSignTileEntity> tiles = getTileEntitiesToUpdate();
            for (PlainSquareSignTileEntity psste : tiles){
                psste.setMode(newMode,rightSelected);
            }
        }
        storage.ifPresent(psStorage -> psStorage.setDisplayMode(newMode));
        if (this.getPosition() == PanelBlock.DEFAULT_RIGHT_POSITION) {
            PSPosition upPsPosition = (rightSelected) ? PSPosition.UP_RIGHT : PSPosition.UP_LEFT;
            PSPosition downPsPosition = (rightSelected) ? PSPosition.DOWN_RIGHT : PSPosition.DOWN_LEFT;
            if (oldMode.is2by2()) {
                //add the new panel in the world
                addPanelInWorld(upPsPosition,rightSelected);
                addPanelInWorld(downPsPosition,rightSelected);
            }else if (newMode.is2by2()){
                //remove the panel used for 3 by 2 panel
                removePanelInWorld(upPsPosition);
                removePanelInWorld(downPsPosition);
            }
        }
    }

    private void addPanelInWorld(PSPosition positionToMove, boolean rightSelected) {
        //move tile from the position of 2 by 2 to a 3 by 2
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos oldPos = PSPosition.DOWN_RIGHT.offsetPos(positionToMove,this.getBlockPos(),facing,true);
        BlockPos newPos = (rightSelected) ?
                PSPosition.DOWN_MIDDLE.offsetPos(positionToMove,this.getBlockPos(),facing,false):
                PSPosition.DOWN_RIGHT.offsetPos(positionToMove,this.getBlockPos(),facing,false);
        //block state definition
        assert level != null;
        BlockState oldState = level.getBlockState(oldPos);
        level.setBlock(newPos,oldState,3);
        //tile entity definition
        BlockState newState = level.getBlockState(newPos);
        TileEntity newTile = newState.getBlock().createTileEntity(newState,level);
        assert newTile != null;
        ((PlainSquareSignTileEntity) newTile).copyData(this,positionToMove);
        level.setBlockEntity(newPos,newTile);
        //redefine position of the middle tile
        TileEntity oldTile = level.getBlockEntity(oldPos);
        if (oldTile instanceof PlainSquareSignTileEntity) {
            PlainSquareSignTileEntity oldPsste = (PlainSquareSignTileEntity) oldTile;
            oldPsste.setPosition(positionToMove.centerPosition());
        }else {
            throw new IllegalStateException("Impossible state : the tile entity is not of the correct type !");
        }
    }



    private void removePanelInWorld(PSPosition positionToMove) {
        Direction facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        boolean rotated = getBlockState().getValue(GridSupport.ROTATED);
        BlockPos oldPos = PSPosition.DOWN_RIGHT.offsetPos(positionToMove,this.getBlockPos(),facing,false);
        BlockPos newPos = PSPosition.DOWN_RIGHT.offsetPos(positionToMove.centerPosition(),this.getBlockPos(),facing,false);
        //block state deletion
        assert level != null;
        level.setBlock(oldPos, ModBlock.GRID_SUPPORT.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_AXIS,facing.getClockWise().getAxis())
                .setValue(GridSupport.ROTATED,rotated)
                ,3);
        //tile entity deletion
        level.removeBlockEntity(oldPos);
        //redefine position for old middle tile
        TileEntity newTile = level.getBlockEntity(newPos);
        if (newTile instanceof PlainSquareSignTileEntity){
            PlainSquareSignTileEntity newPsste = (PlainSquareSignTileEntity) newTile;
            if (positionToMove == PanelBlock.DEFAULT_RIGHT_POSITION)newPsste.copyData(this,positionToMove);
            else newPsste.setPosition(positionToMove);
        }else {
            throw new IllegalStateException("Impossible state : the tile entity is not of the correct type !");
        }

    }

    public Color getBackgroundColor() { return storage.map(PSStorage::getBackgroundColor).orElse(Color.BLACK); }

    public Color getForegroundColor() { return storage.map(PSStorage::getForegroundColor).orElse(Color.BLACK); }

    public void setBackgroundColor(int color) { storage.ifPresent(psStorage -> psStorage.setBackgroundColor(color));}

    public void setForegroundColor(int color) { storage.ifPresent(psStorage -> psStorage.setForegroundColor(color));}

    public int getArrowId(){ return storage.map(PSStorage::getArrowId).orElse(-1);}

    public void setArrowId(int arrowId){ storage.ifPresent(psStorage -> psStorage.setArrowId(arrowId));}

    public Text getText(int ind) { return storage.map(psStorage -> psStorage.getText(ind)).orElse(Text.getDefaultText());}

    public void setText(Text newText,int ind) { storage.ifPresent(psStorage -> psStorage.setText(newText,ind));}

    public void doOperation(int operationId,int colorModeOrArrowDir){
        if (operationId == SET_MODE_WITH_LEFT_SELECTED) {
            this.setMode(PSDisplayMode.byIndex((byte) colorModeOrArrowDir), false);
        }else if (operationId == SET_MODE_WITH_RIGHT_SELECTED){
            this.setMode(PSDisplayMode.byIndex((byte) colorModeOrArrowDir),true);
        }else if (operationId == SET_ARROW_ID){
            this.setArrowId(colorModeOrArrowDir);
        }else if (operationId == CHANGE_BG_COLOR){
            this.setBackgroundColor(colorModeOrArrowDir);
        }else if (operationId == CHANGE_EDGING_COLOR){
            this.setForegroundColor(colorModeOrArrowDir);
        }
    }

    public List<PlainSquareSignTileEntity> getTileEntitiesToUpdate(){
        List<PlainSquareSignTileEntity> tiles = new ArrayList<>();
        Direction facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        EnumMap<PSPosition, BlockPos> positions = this.getPosition().getNeighborPosition(this.getBlockPos(), facing,this.getMode().is2by2());
        positions.remove(this.getPosition());
        for (PSPosition position : positions.keySet()){
            assert this.level != null;
            TileEntity tileEntity = this.level.getBlockEntity(positions.get(position));
            if (tileEntity instanceof PlainSquareSignTileEntity) {
                PlainSquareSignTileEntity psste = (PlainSquareSignTileEntity) tileEntity ;
                tiles.add(psste);
            }else {
                throw new IllegalStateException("Neighbor TE is not a plain square one : the world may be corrupted !");
            }
        }
        return tiles;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == PSCapability.PS_STORAGE){
            return storage.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(BlockState state,CompoundNBT compound) {
        CompoundNBT storage_tag = compound.getCompound("plain_square");
        getCapability(PSCapability.PS_STORAGE).ifPresent(s -> ((INBTSerializable<CompoundNBT>) s).deserializeNBT(storage_tag));
        super.load(state,compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        getCapability(PSCapability.PS_STORAGE).ifPresent(storage -> {
                CompoundNBT compoundNBT = ((INBTSerializable<CompoundNBT>) storage).serializeNBT();
                tag.put("plain_square", compoundNBT);
        });
        return super.save(tag);
    }

    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

}

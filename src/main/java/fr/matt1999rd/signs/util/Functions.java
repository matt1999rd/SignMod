package fr.matt1999rd.signs.util;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.ExtendDirection;
import fr.matt1999rd.signs.enums.PSPosition;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.matt1999rd.signs.fixedpanel.panelblock.PanelBlock;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.fixedpanel.support.SignSupport;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Functions {

    public static BooleanProperty NORTH_WEST,NORTH_EAST,SOUTH_WEST,SOUTH_EAST;
    public static final ResourceLocation SIGN_BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/block/sign.png");

    //function for authorisation of placing the four plain square panel : three of them are the 3 by 2 and one is 2 by 2.
    // 0 -> nothing, 1-> only 2 by 2, 2-> all >> for the placement (allowPanel = false)

    // 0 -> nothing, 1-> only 2 by 2, 2-> 3 by 2 in left, 3 -> 3 by 2 in right,
    // 4 -> all side >> for the enable/disable of reduce/expand side button in gui (allowPanel = true)
    public static final int NO_PANEL = 0;
    public static final int ONLY_2BY2_PANEL = 1;
    public static final int ONLY_3BY2_PANEL_IN_LEFT = 2;
    public static final int ONLY_3BY2_PANEL_IN_RIGHT = 3;
    public static final int ALL_PANEL = 4;


    static {
        NORTH_WEST = BooleanProperty.create("north_west");
        NORTH_EAST = BooleanProperty.create("north_east");
        SOUTH_EAST = BooleanProperty.create("south_east");
        SOUTH_WEST = BooleanProperty.create("south_west");
    }

    //give the direction after placement for updating block state

    public static Direction getDirectionFromEntity(LivingEntity placer, BlockPos pos) {
        Vec3 vec = placer.position();
        Direction d = Direction.getNearest(vec.x-pos.getX(),vec.y-pos.getY(),vec.z-pos.getZ());
        if (d== Direction.DOWN || d== Direction.UP){
            return Direction.NORTH;
        }
        return d;
    }

    // give a boolean table corresponding to the position of grid in object state of class BlockState of a support-like block
    // flags = 0x[(SE)(NE)(NW)(SW)ENWS] where each boolean value correspond to a specific value of boolean property
    // SE : ExtendDirection.SOUTH_EAST.getSupportProperty()
    public static int getFlagsFromState(BlockState state) {
        return ExtendDirection.makeFlagsFromFunction(extendDirection -> state.getValue(extendDirection.getSupportProperty()));
    }

    //give the block state with the new state property of signs stored in the flags. the flags are stored as follows :
    // flags = 0x[(SE)(NE)(NW)(SW)ENWS] where each boolean value correspond to a specific value of boolean property
    // SE : ExtendDirection.SOUTH_EAST.getSupportProperty()

    public static BlockState getNewBlockState(BlockState oldState,int flags){
        for (ExtendDirection direction : ExtendDirection.values()){
            oldState = oldState.setValue(direction.getSupportProperty(),(flags&1)==1);
            flags = flags >> 1;
        }
        return oldState;
    }

    //useful in support-like block to update the block state given in the position of grids into the flags table

    public static void setBlockState(Level world,BlockPos pos, BlockState state, int flags) {
        BlockState newState = getNewBlockState(state,flags);
        world.setBlockAndUpdate(pos,newState);
    }

    //convert a block position into vec3d and adding an offset on x and z coordinate
    // (use in the conversion to vec3d of support and grid center block position)

    public static Vec3 getVecFromBlockPos (BlockPos pos, float horOffset){
        return new Vec3(pos.getX()+horOffset,pos.getY(),pos.getZ()+horOffset);
    }

    //useful if we need to convert to degree (use for openGL rotation)

    public static double toDegree(double radianAngle){
        double degreeAngle = (180.0/Math.PI)*radianAngle;
        if (degreeAngle < 0){
            degreeAngle +=360.0D;
        }
        return degreeAngle;
    }

    //useful if we need to convert to radian

    public static float toRadian(double degreeAngle){
        double radianAngle = (Math.PI/180.0)*degreeAngle;
        if (radianAngle < 0){
            radianAngle += 2*Math.PI;
        }
        return (float)radianAngle;
    }

    //useful for the rotation of matrix stack -> rotate around an axis

    public static void rotate(PoseStack stack, Direction.Axis axis, float angle){
        Vector3f vector3f = (axis == Direction.Axis.X) ? Vector3f.XP :
                (axis == Direction.Axis.Y) ? Vector3f.YP : Vector3f.ZP;
        stack.mulPose(vector3f.rotationDegrees(angle));
    }

    //useful for text coordinate will be changed in the future

    public static boolean isValidCoordinate(float x, float y) {
        return (x>-1 && y>-1 && x<128 && y<128);
    }

    //notify if the block is a grid support or a panel with grid support background

    public static boolean isGridSupport(BlockState state){
        if (state.getBlock() instanceof GridSupport){
            return true;
        }
        if (state.getBlock() instanceof AbstractPanelBlock){
            return state.getValue(AbstractPanelBlock.GRID);
        }
        return false;
    }

    //notify if the block is a sign support or a panel with sign support background

    public static boolean isSignSupport(BlockState state){
        if (state.getBlock() instanceof SignSupport){
            return true;
        }
        if (state.getBlock() instanceof AbstractPanelBlock){
            return (!state.getValue(AbstractPanelBlock.GRID));
        }
        return false;
    }

    //delete Grid Row following direction dir from the grid at basePos in worldIn by player

    public static void deleteGridRow(ExtendDirection dir, BlockPos basePos, Level worldIn, Player player){
        BlockPos offset_pos = dir.relative(basePos);
        //check all the row of grid in the direction dir1 and stop when it is not a grid.
        while (isGridSupport(worldIn.getBlockState(offset_pos))){
            offset_pos = dir.relative(offset_pos);
        }
        if (isSignSupport(worldIn.getBlockState(offset_pos))) {
            //if it is stable do not bother with this
            return;
        }
        ExtendDirection oppositeDir = dir.getOpposite();
        //else we need to get back to the initial block and delete block in-between
        offset_pos = oppositeDir.relative(offset_pos);
        while (!offset_pos.equals(basePos)) {
            BlockState state = worldIn.getBlockState(offset_pos);
            if (isGridSupport(state)){
                deleteBlock(offset_pos,worldIn,player);
            }
            offset_pos = oppositeDir.relative(offset_pos);
        }
    }

    //manage the deletion of supported block : generic function for sign support and abstract panel block
    public static void manageDeletionOfSupportedBlock(Level world, BlockPos pos, BlockState state, Player player){
        Functions.deleteConnectingGrid(pos,world,player,state);
        BlockPos offset_pos = pos.above();
        BlockState upSupportState = world.getBlockState(offset_pos);
        //we delete all sign support block that this support block was handling
        if (Functions.isSignSupport(upSupportState)){
            Functions.manageDeletionOfSupportedBlock(world,offset_pos,upSupportState,player);
        }
        Functions.deleteBlock(pos,world,player);
    }

    //delete connecting grid of a sign support (with or without panel) at position pos in worldIn by player

    public static void deleteConnectingGrid(BlockPos pos, Level worldIn, Player player,BlockState state) {
        int flags = getFlagsFromState(state);
        for (ExtendDirection direction : ExtendDirection.values()){
            if ((flags&1)==1){
                Functions.deleteGridRow(direction,pos,worldIn,player);
            }
            flags = flags >> 1;
        }
    }

    //delete block at position pos by playerEntity in world

    public static void deleteBlock(BlockPos pos, Level world, Player playerEntity){
        ItemStack stack = playerEntity.getMainHandItem();
        BlockState state1 = world.getBlockState(pos);
        world.levelEvent(playerEntity,2001,pos,Block.getId(state1));
        if (!world.isClientSide && !playerEntity.isCreative()) {
            Block.dropResources(state1, world, pos, null, playerEntity, stack);
        }
        world.setBlock(pos, Blocks.AIR.defaultBlockState(),35);
    }

    //useful to delete the other grid of a grid or a support

    public static void deleteOtherGrid(BlockPos pos, Level worldIn, Player player,BlockState state) {
        boolean isRotated = state.getValue(GridSupport.ROTATED);
        Direction.Axis axis;
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)){
            axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        }else {
            axis = state.getValue(BlockStateProperties.HORIZONTAL_FACING).getClockWise().getAxis();
        }

        if (!isRotated) {
            ExtendDirection posDir = ExtendDirection.getExtendedDirection(
                    Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE),
                    false);
            ExtendDirection negDir = ExtendDirection.getExtendedDirection(
                    Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE),
                    false);
            assert posDir != null;
            Functions.deleteGridRow(posDir,pos,worldIn,player);
            assert negDir != null;
            Functions.deleteGridRow(negDir,pos,worldIn,player);
        }else {
            Direction posDir1 = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
            Direction posDir2 = posDir1.getCounterClockWise();
            ExtendDirection posExtDir = ExtendDirection.getExtendedDirection(posDir1,posDir2);
            Direction negDir1 = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
            Direction negDir2 = negDir1.getCounterClockWise();
            ExtendDirection negExtDir = ExtendDirection.getExtendedDirection(negDir1,negDir2);
            assert posExtDir != null;
            Functions.deleteGridRow(posExtDir,pos,worldIn,player);
            assert negExtDir != null;
            Functions.deleteGridRow(negExtDir,pos,worldIn,player);
        }
    }
    @OnlyIn(Dist.CLIENT)
    public static int getAlphaValue(int color){
        int res1 = (color & -16777216);
        return res1 >> 24;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getRedValue(int color){
        return (color & 16711680) >> 16;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getGreenValue(int color){
        return (color & '\uff00') >> 8;
    }

    @OnlyIn(Dist.CLIENT)
    public static int getBlueValue(int color){
        return (color & 255);
    }

    public static float colorDistance(Color firstColor, Color secondColor){
        int d = 0;
        int firstColorValue = firstColor.getRGB();
        int secondColorValue = secondColor.getRGB();
        int v;
        for (int i=0;i<4;i++){
            v = ((firstColorValue >> (8 * i) & 255) - (secondColorValue >> (8 * i) & 255));
            d += v*v;
        }
        return Mth.sqrt(d);
    }

    public static VoxelShape getSupportShape(){
        return Block.box(7,0,7,9,16,9);
    }

    public static VoxelShape getGridShape(boolean isRotated,Direction facing){
        VoxelDouble vi_plane = new VoxelDouble(7.5,2,0,1,12,8,true);
        VoxelDouble[] vi_diagonal = new VoxelDouble[]{
                new VoxelDouble(15,2,0,1,12,1, true),
                new VoxelDouble(14,2,1,1,12,1, true),
                new VoxelDouble(13,2,2,1,12,1, true),
                new VoxelDouble(12,2,3,1,12,1, true),
                new VoxelDouble(11,2,4,1,12,1, true),
                new VoxelDouble(10,2,5,1,12,1, true),
                new VoxelDouble(9 ,2,6,1,12,1, true),
                new VoxelDouble(8 ,2,7,1,12,1, true)
        };
        if (isRotated){
            VoxelShape vs = Shapes.empty();
            for (int i=0;i<7;i++){
                vs = Shapes.or(vs,vi_diagonal[i].rotate(Direction.EAST,facing).getAssociatedShape());
            }
            return vs;
        }else {
            return vi_plane.rotate(Direction.NORTH,facing).getAssociatedShape();
        }
    }

    public static void setWorldGLState() {
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(SourceFactor.SRC_ALPHA.value, DestFactor.ONE_MINUS_SRC_ALPHA.value, SourceFactor.ONE.value, DestFactor.ZERO.value);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void resetWorldGLState() {
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
    }

    public static char[] toCharArray(List<Character> list) {
        int n = list.size();
        char[] chars = new char[n];
        int i = 0;
        for (Character character : list) {
            chars[i] = character;
            i++;
        }
        return chars;
    }

    public static float distance(float x, float y) {
        return Mth.sqrt(x*x+y*y);
    }

    //has 4 grid return true if the grid support at blockPos pos
    public static boolean has4Grid(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Level world = context.getLevel();
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!(block instanceof GridSupport)){
            return false;
        }
        Direction.Axis axis = state.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        boolean isRotated = state.getValue(GridSupport.ROTATED);
        ExtendDirection direction = ExtendDirection.getExtendedDirection(Direction.get(Direction.AxisDirection.POSITIVE,axis),isRotated);
        for (int i=0;i<4;i++){
            ExtendDirection testDir = (i%2 == 0)? direction : direction.getOpposite();
            Direction YDir = (i/2==0)? Direction.UP : Direction.DOWN;
            BlockState stateY = world.getBlockState(pos.relative(YDir));
            assert testDir != null;
            BlockState stateCorner = world.getBlockState(testDir.relative(pos).relative(YDir));
            BlockState stateHor = world.getBlockState(testDir.relative(pos));
            Block blockY = stateY.getBlock();
            Block blockCorner = stateCorner.getBlock();
            Block blockHor = stateHor.getBlock();
            if (blockY instanceof GridSupport && blockCorner instanceof GridSupport && blockHor instanceof GridSupport){
                return true;
            }
        }
        return false;
    }

    //function for authorisation of placing the four plain square panel : three of them are the 3 by 2 and one is 2 by 2.
    // 0 -> nothing, 1-> only 2 by 2, 2-> all >> for the placement (allowPanel = false)

    // 0 -> nothing, 1-> only 2 by 2, 2-> 3 by 2 in left, 3 -> 3 by 2 in right,
    // 4 -> all side >> for the enable/disable of reduce/expand side button in gui (allowPanel = true)

    public static byte getAuthoring(Level world, BlockPos futurePos, Direction futureFacing,boolean allowPanel) {
        List<PSPosition> placementDir = PSPosition.listPlaceable(world,futurePos,futureFacing,true,allowPanel);
        if (placementDir.isEmpty()){
            return NO_PANEL;
        }
        List<PSPosition> placementDir2 = PSPosition.listPlaceable(world,futurePos,futureFacing,false,allowPanel);
        if (placementDir2.isEmpty()){
            return ONLY_2BY2_PANEL;
        }
        if (placementDir2.size() == 1){
            if (!allowPanel)return ALL_PANEL;
            PSPosition position = placementDir2.get(0);
            PSPosition defaultPosition = PanelBlock.DEFAULT_RIGHT_POSITION;
            if ((position == defaultPosition) == (defaultPosition.isRight())){
                return ONLY_3BY2_PANEL_IN_LEFT;
            }else {
                return ONLY_3BY2_PANEL_IN_RIGHT;
            }
        }
        return (byte) ((allowPanel)? ALL_PANEL : ONLY_3BY2_PANEL_IN_LEFT);
    }

    // function that indicates if vector2f "point" is in the rhombus of center vec2f "center" = (xC,yC) and diagonal length (xD,yD) with diagonal over X and Y axis
    // rhombus bounding equation (with axis XY as diagonal) are of the form : |y-yC|̀ ≤ yD (1 - |x-xC|/xD) (x,y) ∈ ℝ²
    // where the middle of the square (intersection of the two diagonals) is C(xC,yC) and
    // the length of the x diagonal is xD and the length of the y diagonal is yD
    public static boolean isInRhombus(Vec2 point,Vec2 center,float xD,float yD){
        return Mth.abs(point.y-center.y) <= yD * (1 - Mth.abs(point.x-center.x)/xD);
    }

    public static void renderTextLimit(float guiLeft,float guiTop,float L,float h,boolean isSelected){
        float u = (isSelected) ? 0 :1/256.0F;
        float v = 35/256.0F+u;
        float horDu = (L+2)/256.0F;
        float verDu = 1/256.0F;
        float horDv = verDu;
        float verDv = (h+2)/256.0F;
        //up bar
        renderTexture(new Rectangle2D.Float(guiLeft-1,guiTop-1,L+2,1),new Rectangle2D.Float(u,v,horDu,horDv));
        //down bar
        renderTexture(new Rectangle2D.Float(guiLeft-1,guiTop+h,L+2,1),new Rectangle2D.Float(u,v,horDu,horDv));
        //left bar
        renderTexture(new Rectangle2D.Float(guiLeft-1,guiTop-1,1,h+2),new Rectangle2D.Float(u,v,verDu,verDv));
        //right bar
        renderTexture(new Rectangle2D.Float(guiLeft+L,guiTop-1,1,h+2),new Rectangle2D.Float(u,v,verDu,verDv));
    }

    private static void renderTexture(Rectangle2D vertex,Rectangle2D uvMapping){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(vertex.getMinX(),vertex.getMinY(),0).uv((float) uvMapping.getMinX(), (float) uvMapping.getMinY()).endVertex();
        builder.vertex(vertex.getMinX(),vertex.getMaxY(),0).uv((float) uvMapping.getMinX(), (float) uvMapping.getMaxY()).endVertex();
        builder.vertex(vertex.getMaxX(),vertex.getMaxY(),0).uv((float) uvMapping.getMaxX(), (float) uvMapping.getMaxY()).endVertex();
        builder.vertex(vertex.getMaxX(),vertex.getMinY(),0).uv((float) uvMapping.getMaxX(), (float) uvMapping.getMinY()).endVertex();
        //RenderSystem.enableAlphaTest();
        tesselator.end();
    }

    //Function to bake model
    public static ModelPart bake(CubeListBuilder builder){
        List<ModelPart.Cube> modelCube = Lists.newArrayList();
        builder.getCubes().forEach(cubeDefinition -> modelCube.add(cubeDefinition.bake(0,0)));
        return new ModelPart(modelCube, Map.of());
    }

    @SafeVarargs
    public static ModelPart bake(CubeListBuilder builder, Pair<String,ModelPart>... child){
        List<ModelPart.Cube> modelCube = Lists.newArrayList();
        builder.getCubes().forEach(cubeDefinition -> modelCube.add(cubeDefinition.bake(0,0)));
        HashMap<String,ModelPart> children = new HashMap<>();
        for (Pair<String,ModelPart> c : child){
            children.put(c.getFirst(),c.getSecond());
        }
        return new ModelPart(modelCube, children);
    }


}

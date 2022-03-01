package fr.matt1999rd.signs.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import fr.matt1999rd.signs.SignMod;
import fr.matt1999rd.signs.enums.ExtendDirection;
import fr.matt1999rd.signs.enums.PSPosition;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.fixedpanel.support.SignSupport;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class Functions {

    public static BooleanProperty NORTH_WEST,NORTH_EAST,SOUTH_WEST,SOUTH_EAST;
    //for disposition of text in direction panel : 1(limit) 2(st gap) 144(beg text) 4(larger gap) 25(end text) 2(st gap) 1(limit)
    public static final int panelLength = 179;
    public static final int endTextLength = 25;
    public static final float xOrigin = -25.6F;
    public static final int st_gap = 3; //st gap+limit
    public static final int center_gap = 4; //larger gap
    public static final int begTextLength = panelLength-endTextLength-2*st_gap-center_gap;
    public static final ResourceLocation SIGN_BACKGROUND = new ResourceLocation(SignMod.MODID,"textures/block/sign.png");


    static {
        NORTH_WEST = BooleanProperty.create("north_west");
        NORTH_EAST = BooleanProperty.create("north_east");
        SOUTH_EAST = BooleanProperty.create("south_east");
        SOUTH_WEST = BooleanProperty.create("south_west");
    }

    //give the direction after placement for updating block state

    public static Direction getDirectionFromEntity(LivingEntity placer, BlockPos pos) {
        Vector3d vec = placer.position();
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

    //give the block state with the new state property of signs stored in the flags. the flags are stored as follow :
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

    public static void setBlockState(World world,BlockPos pos, BlockState state, int flags) {
        BlockState newState = getNewBlockState(state,flags);
        world.setBlockAndUpdate(pos,newState);
    }

    //convert a block position into vec3d and adding an offset on x and z coordinate
    // (use in the conversion to vec3d of support and grid center block position)

    public static Vector3d getVecFromBlockPos (BlockPos pos, float horOffset){
        return new Vector3d(pos.getX()+horOffset,pos.getY(),pos.getZ()+horOffset);
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

    public static void rotate(MatrixStack stack, Direction.Axis axis, float angle){
        Vector3f vector3f = (axis == Direction.Axis.X) ? Vector3f.XP :
                (axis == Direction.Axis.Y) ? Vector3f.YP : Vector3f.ZP;
        stack.mulPose(vector3f.rotationDegrees(angle));
    }

    //useful for text coordinate will be changed in the future

    public static boolean isValidCoordinate(int x, int y) {
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

    public static void deleteGridRow(ExtendDirection dir, BlockPos basePos, World worldIn, PlayerEntity player){
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

    //delete connecting grid of a sign support (with or without panel) at position pos in worldIn by player

    public static void deleteConnectingGrid(BlockPos pos, World worldIn, PlayerEntity player,BlockState state) {
        int flags = getFlagsFromState(state);
        for (ExtendDirection direction : ExtendDirection.values()){
            if ((flags&1)==1){
                BlockPos gridPos = direction.relative(pos);
                BlockState gridState = worldIn.getBlockState(gridPos);
                Functions.deleteOtherGrid(gridPos,worldIn,player,gridState);
                Functions.deleteBlock(gridPos,worldIn,player);
            }
            flags = flags >> 1;
        }
    }

    //delete block at position pos by playerEntity in world

    public static void deleteBlock(BlockPos pos, World world, PlayerEntity playerEntity){
        ItemStack stack = playerEntity.getMainHandItem();
        BlockState state1 = world.getBlockState(pos);
        world.levelEvent(playerEntity,2001,pos,Block.getId(state1));
        if (!world.isClientSide && !playerEntity.isCreative()) {
            Block.dropResources(state1, world, pos, null, playerEntity, stack);
        }
        world.setBlock(pos, Blocks.AIR.defaultBlockState(),35);
    }

    //useful to delete the other grid of a grid or a

    public static void deleteOtherGrid(BlockPos pos, World worldIn, PlayerEntity player,BlockState state) {
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

    public static VoxelShape getSupportShape(){
        return Block.box(7,0,7,9,16,9);
    }

    public static VoxelShape getGridShape(boolean isRotated,Direction facing){
        VoxelInts vi_plane = new VoxelInts(7.5,2,0,1,12,8,true);
        VoxelInts[] vi_diag = new VoxelInts[]{
                new VoxelInts(15,2,0,1,12,1, true),
                new VoxelInts(14,2,1,1,12,1, true),
                new VoxelInts(13,2,2,1,12,1, true),
                new VoxelInts(12,2,3,1,12,1, true),
                new VoxelInts(11,2,4,1,12,1, true),
                new VoxelInts(10,2,5,1,12,1, true),
                new VoxelInts(9 ,2,6,1,12,1, true),
                new VoxelInts(8 ,2,7,1,12,1, true)
        };
        if (isRotated){
            VoxelShape vs = VoxelShapes.empty();
            for (int i=0;i<7;i++){
                vs = VoxelShapes.or(vs,vi_diag[i].rotate(Direction.EAST,facing).getAssociatedShape());
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
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void resetWorldGLState() {
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
    }

    public static char[] toCharArray(List<Character> list){
        int n= list.size();
        char[] chars = new char[n];
        int i = 0;
        for (Character character : list) {
            chars[i] = character;
            i++;
        }
        return chars;
    }

    public static int getLength(String content){
        int n=content.length();
        int length = 0;
        for (int i=0;i<n;i++){
            char c0 = content.charAt(i);
            if (c0 == ' '){
                length+=4;
            }else {
                Letter l = new Letter(c0, 0, 0);
                length += l.length;
                char followingChar = (i != n - 1) ? content.charAt(i + 1) : ' ';
                if (followingChar > 97) {
                    length += 1;
                } else if (followingChar != ' ') {
                    length += 2;
                }
            }
        }
        return length;
    }

    public static float distance(float x, float y) {
        return MathHelper.sqrt(x*x+y*y);
    }

    //has 4 grid return true if the grid support at blockPos pos
    public static boolean has4Grid(ItemUseContext context) {
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
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
    //0 -> nothing, 1-> only 2 by 2, 2-> all

    public static byte getAuthoring(World world, BlockPos futurePos, Direction futureFacing) {
        List<PSPosition> placementDir = PSPosition.listPlaceable(world,futurePos,futureFacing,true);
        if (placementDir.isEmpty()){
            return 0;
        }
        List<PSPosition> placementDir2 = PSPosition.listPlaceable(world,futurePos,futureFacing,false);
        if (placementDir2.isEmpty()){
            return 1;
        }
        return 2;
    }

    // function that indicates if vector2f "point" is in the rhombus of center vec2f "center" = (xC,yC) and diagonal length (xD,yD) with diagonal over X and Y axis
    // rhombus bounding equation (with axis XY as diagonal) are of the form : |y-yC|̀ ≤ yD (1 - |x-xC|/xD) (x,y) ∈ ℝ²
    // where the middle of the square (intersection of the two diagonals) is C(xC,yC) and
    // the length of the x diagonal is xD and the length of the y diagonal is yD
    public static boolean isInRhombus(Vector2f point,Vector2f center,float xD,float yD){
        return MathHelper.abs(point.y-center.y) <= yD * (1 - MathHelper.abs(point.x-center.x)/xD);
    }


}

package fr.matt1999rd.signs.enums;

import com.google.common.collect.Lists;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.*;

public enum PSPosition implements StringRepresentable {
    UP_LEFT(0,"up_left"),
    UP_MIDDLE(1,"up_middle"),
    UP_RIGHT(2,"up_right"),
    DOWN_LEFT(3,"down_left"),
    DOWN_MIDDLE(4,"down_middle"),
    DOWN_RIGHT(5,"down_right");

    private final int meta;
    private final String name;
    PSPosition(int meta,String name){
        this.meta = meta;
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public static PSPosition[] list2by2(){
        PSPosition[] positions = new PSPosition[4];
        int k=0;
        for (PSPosition position : PSPosition.values()){
            if (!(position.isMiddle())){
                positions[k]=position;
                k++;
            }
        }
        return positions;
    }

    public byte getMeta(){
        return (byte)meta;
    }

    public static PSPosition byIndex(byte meta){
        PSPosition[] positions = PSPosition.values();
        if (meta<0 || meta>5){
            return null;
        }
        return positions[meta];
    }

    public boolean isLeft(){
        return meta%3 == 0;
    }

    public boolean isMiddle(){
        return meta%3 == 1;
    }

    public boolean isRight(){
        return meta%3 == 2;
    }

    public boolean isUp(){
        return meta/3 == 0;
    }

    public boolean isPlaceable(Level world, BlockPos pos, Direction facing,boolean isFor2by2,boolean allowPanel){
        List<BlockPos> posToCheck = Lists.newArrayList();
        Direction leftDirection = facing.getClockWise();
        if (isFor2by2 && isMiddle()){
            return false;
        }
        if (isUp()){
            posToCheck.add(pos.below());
        }else {
            posToCheck.add(pos.above());
        }
        if (isLeft() || isMiddle()) {
            posToCheck.add(pos.relative(leftDirection.getOpposite()));
        }
        if (isRight() || isMiddle()){
            posToCheck.add(pos.relative(leftDirection));
        }
        switch (meta){
            case 0:
                posToCheck.add(pos.below().relative(leftDirection.getOpposite()));
                if (isFor2by2)break;
                posToCheck.add(pos.relative(leftDirection.getOpposite(),2));
                posToCheck.add(pos.below().relative(leftDirection.getOpposite(),2));
                break;
            case 1:
                posToCheck.add(pos.below().relative(leftDirection.getOpposite()));
                posToCheck.add(pos.below().relative(leftDirection));
                break;
            case 2:
                posToCheck.add(pos.below().relative(leftDirection));
                if (isFor2by2)break;
                posToCheck.add(pos.relative(leftDirection,2));
                posToCheck.add(pos.below().relative(leftDirection,2));
                break;
            case 3:
                posToCheck.add(pos.above().relative(leftDirection.getOpposite()));
                if (isFor2by2)break;
                posToCheck.add(pos.relative(leftDirection.getOpposite(),2));
                posToCheck.add(pos.above().relative(leftDirection.getOpposite(),2));
                break;
            case 4:
                posToCheck.add(pos.above().relative(leftDirection.getOpposite()));
                posToCheck.add(pos.above().relative(leftDirection));
                break;
            case 5:
                posToCheck.add(pos.above().relative(leftDirection));
                if (isFor2by2)break;
                posToCheck.add(pos.relative(leftDirection,2));
                posToCheck.add(pos.above().relative(leftDirection,2));
                break;
        }
        for (BlockPos pos2 : posToCheck){
            Block block = world.getBlockState(pos2).getBlock();
            if (!(block instanceof GridSupport) && !(block instanceof AbstractPanelBlock && allowPanel)){
                return false;
            }
        }
        return true;
    }

    public static List<PSPosition> listPlaceable(Level world, BlockPos pos, Direction facing,boolean isFor2by2,boolean allowPanel){
        List<PSPosition> positions = Lists.newArrayList();
        for (PSPosition position : PSPosition.values()){
            if (position.isPlaceable(world,pos,facing,isFor2by2,allowPanel)){
                positions.add(position);
            }
        }
        return positions;
    }

    //BlockPos pos is the block position associated with the PSPosition this
    //the output of the function is the block position associated with the PSPosition position
    //facing and isFor2by2 are argument for calculation

    public BlockPos offsetPos(PSPosition position,BlockPos pos,Direction facing,boolean isFor2by2){
        BlockPos offset = new BlockPos(pos);
        Direction leftDirection = facing.getClockWise();
        if (position.isUp() && !this.isUp()){
            offset = offset.above();
        }else if (!position.isUp() && this.isUp()){
            offset = offset.below();
        }
        if (this.isRight()){ //the position is in the right
            int n = (position.isRight())? 0 : (position.isMiddle() || isFor2by2) ? 1 : 2;
            offset = offset.relative(leftDirection,n);
        } else if (this.isLeft()){ //the position is in the left
            int n = (position.isLeft())? 0 : (position.isMiddle() || isFor2by2) ? 1 : 2;
            offset = offset.relative(leftDirection.getOpposite(),n);
        } else { //the position is in the middle
            Direction offsetDir = (position.isLeft())? leftDirection : leftDirection.getOpposite();
            int n = (position.isMiddle())? 0 : 1;
            offset = offset.relative(offsetDir,n);
        }
        return offset;
    }

    public EnumMap<PSPosition, BlockPos> getNeighborPosition(BlockPos pos,Direction facing,boolean isFor2by2) {
        EnumMap<PSPosition, BlockPos> neighbor = new EnumMap<>(PSPosition.class);
        if (isFor2by2 && isMiddle()){
            return neighbor;
        }
        PSPosition[] listPosition = isFor2by2 ? PSPosition.list2by2() : PSPosition.values();
        for (PSPosition position : listPosition){
            neighbor.put(position,this.offsetPos(position,pos,facing,isFor2by2));
        }

        return neighbor;
    }

    public PSPosition centerPosition() {
        if (this.isMiddle())return this;
        if (this.isUp()) return PSPosition.UP_MIDDLE;
        else return PSPosition.DOWN_MIDDLE;
    }
}

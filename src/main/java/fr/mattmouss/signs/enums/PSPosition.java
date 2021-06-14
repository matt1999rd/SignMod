package fr.mattmouss.signs.enums;

import com.google.common.collect.Lists;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public enum PSPosition implements IStringSerializable {
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
    public String getName() {
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

    public boolean isPlaceable(World world, BlockPos pos, Direction facing,boolean isFor2by2){
        List<BlockPos> posToCheck = Lists.newArrayList();
        Direction leftDirection = facing.rotateY();
        if (isFor2by2 && isMiddle()){
            return false;
        }
        if (isUp()){
            posToCheck.add(pos.down());
        }else {
            posToCheck.add(pos.up());
        }
        if (isLeft() || isMiddle()) {
            posToCheck.add(pos.offset(leftDirection.getOpposite()));
        }
        if (isRight() || isMiddle()){
            posToCheck.add(pos.offset(leftDirection));
        }
        switch (meta){
            case 0:
                posToCheck.add(pos.down().offset(leftDirection.getOpposite()));
                if (isFor2by2)break;
                posToCheck.add(pos.offset(leftDirection.getOpposite(),2));
                posToCheck.add(pos.down().offset(leftDirection.getOpposite(),2));
                break;
            case 1:
                posToCheck.add(pos.down().offset(leftDirection.getOpposite()));
                posToCheck.add(pos.down().offset(leftDirection));
                break;
            case 2:
                posToCheck.add(pos.down().offset(leftDirection));
                if (isFor2by2)break;
                posToCheck.add(pos.offset(leftDirection,2));
                posToCheck.add(pos.down().offset(leftDirection,2));
                break;
            case 3:
                posToCheck.add(pos.up().offset(leftDirection.getOpposite()));
                if (isFor2by2)break;
                posToCheck.add(pos.offset(leftDirection.getOpposite(),2));
                posToCheck.add(pos.up().offset(leftDirection.getOpposite(),2));
                break;
            case 4:
                posToCheck.add(pos.up().offset(leftDirection.getOpposite()));
                posToCheck.add(pos.up().offset(leftDirection));
                break;
            case 5:
                posToCheck.add(pos.up().offset(leftDirection));
                if (isFor2by2)break;
                posToCheck.add(pos.offset(leftDirection,2));
                posToCheck.add(pos.up().offset(leftDirection,2));
                break;
        }
        for (BlockPos pos2 : posToCheck){
            if (!(world.getBlockState(pos2).getBlock() instanceof GridSupport)){
                return false;
            }
        }
        return true;
    }

    public static List<PSPosition> listPlaceable(World world, BlockPos pos, Direction facing,boolean isFor2by2){
        List<PSPosition> positions = Lists.newArrayList();
        for (PSPosition position : PSPosition.values()){
            if (position.isPlaceable(world,pos,facing,isFor2by2)){
                positions.add(position);
            }
        }
        return positions;
    }

    public BlockPos offsetPos(PSPosition position,BlockPos pos,Direction facing,boolean isFor2by2){
        BlockPos offset = new BlockPos(pos);
        Direction leftDirection = facing.rotateY();
        if (position.isUp() && !this.isUp()){
            offset = offset.up();
        }else if (!position.isUp() && this.isUp()){
            offset = offset.down();
        }
        if (this.isRight()){
            int n = (position.isRight())? 0 : (position.isMiddle() || isFor2by2) ? 1 : 2;
            offset = offset.offset(leftDirection,n);
        } else if (this.isLeft()){
            int n = (position.isLeft())? 0 : (position.isMiddle() || isFor2by2) ? 1 : 2;
            offset = offset.offset(leftDirection.getOpposite(),n);
        } else {
            Direction offsetDir = (position.isLeft())? leftDirection : leftDirection.getOpposite();
            int n = (position.isMiddle())? 0 : 1;
            offset = offset.offset(offsetDir,n);
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
}

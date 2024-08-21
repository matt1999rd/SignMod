package fr.matt1999rd.signs.tileentity;

import fr.matt1999rd.signs.tileentity.primary.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

import java.util.List;

public class TEType {

    @ObjectHolder("sign:huge_direction_panel")
    public static  BlockEntityType<PlainSquareSignTileEntity> PLAIN_SQUARE_SIGN ;
    @ObjectHolder("sign:direction_panel")
    public static BlockEntityType<ArrowSignTileEntity> ARROW_SIGN;
    @ObjectHolder("sign:stop_panel")
    public static BlockEntityType<OctagonSignTileEntity> OCTAGON_SIGN;
    @ObjectHolder("sign:let_way_panel")
    public static BlockEntityType<UpsideTriangleSignTileEntity> UPSIDE_TRIANGLE_SIGN;
    @ObjectHolder("sign:rectangle_panel")
    public static BlockEntityType<RectangleSignTileEntity> RECTANGLE_SIGN;
    @ObjectHolder("sign:diamond_panel")
    public static BlockEntityType<DiamondSignTileEntity> DIAMOND_SIGN;
    @ObjectHolder("sign:circle_panel")
    public static BlockEntityType<CircleSignTileEntity> CIRCLE_SIGN ;
    @ObjectHolder("sign:triangle_panel")
    public static BlockEntityType<TriangleSignTileEntity> TRIANGLE_SIGN ;
    @ObjectHolder("sign:square_panel")
    public static BlockEntityType<SquareSignTileEntity> SQUARE_SIGN;
}

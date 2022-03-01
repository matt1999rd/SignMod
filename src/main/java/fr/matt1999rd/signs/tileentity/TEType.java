package fr.matt1999rd.signs.tileentity;

import fr.matt1999rd.signs.tileentity.primary.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class TEType {
    @ObjectHolder("sign:huge_direction_panel")
    public static  TileEntityType<PlainSquareSignTileEntity> PLAIN_SQUARE_SIGN ;
    @ObjectHolder("sign:direction_panel")
    public static TileEntityType<ArrowSignTileEntity> ARROW_SIGN;
    @ObjectHolder("sign:stop_panel")
    public static TileEntityType<OctogoneSignTileEntity> OCTOGONE_SIGN;
    @ObjectHolder("sign:let_way_panel")
    public static TileEntityType<UpsideTriangleSignTileEntity> UPSIDE_TRIANGLE_SIGN;
    @ObjectHolder("sign:rectangle_panel")
    public static TileEntityType<RectangleSignTileEntity> RECTANGLE_SIGN;
    @ObjectHolder("sign:diamond_panel")
    public static TileEntityType<DiamondSignTileEntity> DIAMOND_SIGN;
    @ObjectHolder("sign:circle_panel")
    public static TileEntityType<CircleSignTileEntity> CIRCLE_SIGN ;
    @ObjectHolder("sign:triangle_panel")
    public static TileEntityType<TriangleSignTileEntity> TRIANGLE_SIGN ;
    @ObjectHolder("sign:square_panel")
    public static TileEntityType<SquareSignTileEntity> SQUARE_SIGN;
}

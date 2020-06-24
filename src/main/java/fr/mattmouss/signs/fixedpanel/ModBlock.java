package fr.mattmouss.signs.fixedpanel;

import fr.mattmouss.signs.fixedpanel.panelblock.*;
import fr.mattmouss.signs.fixedpanel.support.GridSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupport;
import fr.mattmouss.signs.fixedpanel.support.SignSupportTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlock {
    @ObjectHolder("sign:sign_support")
    public static SignSupport SIGN_SUPPORT = new SignSupport();
    @ObjectHolder("sign:sign_support")
    public static TileEntityType<SignSupportTileEntity> SIGN_SUPPORT_TE_TYPE;
    @ObjectHolder("sign:grid_support")
    public static GridSupport GRID_SUPPORT = new GridSupport();
    @ObjectHolder("sign:square_panel")
    public static SquarePanelBlock SQUARE_PANEL = new SquarePanelBlock();
    @ObjectHolder("sign:circle_panel")
    public static CirclePanelBlock CIRCLE_PANEL = new CirclePanelBlock();
    @ObjectHolder("sign:triangle_panel")
    public static TrianglePanelBlock TRIANGLE_PANEL = new TrianglePanelBlock();
    @ObjectHolder("sign:let_way_panel")
    public static LetWayPanelBlock LET_WAY_PANEL = new LetWayPanelBlock();
    @ObjectHolder("sign:priority_panel")
    public static DiamondPanelBlock PRIORITY_PANEL = new DiamondPanelBlock();
    @ObjectHolder("sign:direction_panel")
    public static ArrowPanelBlock DIRECTION_PANEL = new ArrowPanelBlock();
    @ObjectHolder("sign:huge_direction_panel")
    public static PlainSquarePanelBlock HUGE_DIRECTION_PANEL = new PlainSquarePanelBlock();
    @ObjectHolder("sign:stop_panel")
    public static OctogonePanelBlock STOP_PANEL = new OctogonePanelBlock();
    @ObjectHolder("sign:rectangle_panel")
    public static RectanglePanelBlock RECTANGLE_PANEL = new RectanglePanelBlock();



}

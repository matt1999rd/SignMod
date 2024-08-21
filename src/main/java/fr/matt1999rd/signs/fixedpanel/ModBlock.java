package fr.matt1999rd.signs.fixedpanel;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.fixedpanel.panelblock.*;
import fr.matt1999rd.signs.fixedpanel.support.GridSupport;
import fr.matt1999rd.signs.fixedpanel.support.SignSupport;
import fr.matt1999rd.signs.fixedpanel.support.SignSupportTileEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlock {
    @ObjectHolder("sign:sign_support")
    public static SignSupport SIGN_SUPPORT = new SignSupport();
    @ObjectHolder("sign:sign_support")
    public static BlockEntityType<SignSupportTileEntity> SIGN_SUPPORT_TE_TYPE;
    @ObjectHolder("sign:grid_support")
    public static GridSupport GRID_SUPPORT = new GridSupport();
    @ObjectHolder("sign:square_panel")
    public static PanelBlock SQUARE_PANEL = new PanelBlock(Form.SQUARE);
    @ObjectHolder("sign:circle_panel")
    public static PanelBlock CIRCLE_PANEL = new PanelBlock(Form.CIRCLE);
    @ObjectHolder("sign:triangle_panel")
    public static PanelBlock TRIANGLE_PANEL = new PanelBlock(Form.TRIANGLE);
    @ObjectHolder("sign:let_way_panel")
    public static PanelBlock LET_WAY_PANEL = new PanelBlock(Form.UPSIDE_TRIANGLE);
    @ObjectHolder("sign:diamond_panel")
    public static PanelBlock DIAMOND_PANEL = new PanelBlock(Form.DIAMOND);
    @ObjectHolder("sign:direction_panel")
    public static PanelBlock DIRECTION_PANEL = new PanelBlock(Form.ARROW);
    @ObjectHolder("sign:huge_direction_panel")
    public static PanelBlock HUGE_DIRECTION_PANEL = new PanelBlock(Form.PLAIN_SQUARE);
    @ObjectHolder("sign:stop_panel")
    public static PanelBlock STOP_PANEL = new PanelBlock(Form.OCTAGON);
    @ObjectHolder("sign:rectangle_panel")
    public static PanelBlock RECTANGLE_PANEL = new PanelBlock(Form.RECTANGLE);



}

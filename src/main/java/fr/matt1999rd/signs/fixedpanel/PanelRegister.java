package fr.matt1999rd.signs.fixedpanel;

import fr.matt1999rd.signs.enums.Form;
import fr.matt1999rd.signs.fixedpanel.panelblock.AbstractPanelBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegistryEvent;

import java.util.ArrayList;
import java.util.List;

public class PanelRegister {
    private static final List<AbstractPanelBlock> PANELS = new ArrayList<>();

    //registering of panel blocks

    public static final AbstractPanelBlock TRIANGLE_PANEL = register(Form.TRIANGLE); //warning
    public static final AbstractPanelBlock SQUARE_PANEL = register(Form.SQUARE); //information
    public static final AbstractPanelBlock LET_WAY_PANEL = register(Form.UPSIDE_TRIANGLE); //let way sign
    public static final AbstractPanelBlock DIAMOND_PANEL = register(Form.DIAMOND); //priority
    public static final AbstractPanelBlock DIRECTION_PANEL = register(Form.ARROW); //sign with arrow to notify direction
    public static final AbstractPanelBlock HUGE_DIRECTION_PANEL = register(Form.PLAIN_SQUARE); //sign on road to specify direction
    public static final AbstractPanelBlock STOP_PANEL = register(Form.OCTAGON); //sign that force the car to stop
    public static final AbstractPanelBlock RECTANGLE_PANEL = register(Form.RECTANGLE); // sign for indication with arrow or indication of town arriving
    public static final AbstractPanelBlock CIRCLE_PANEL = register(Form.CIRCLE); //sign of forbidden things to do


    private static AbstractPanelBlock register(Form form){
        AbstractPanelBlock panelBlock = AbstractPanelBlock.createPanelInstance(form);
        System.out.println("-----------------Block "+ form +" registered !------------------");
        System.out.println("------------------ Registry Name : "+panelBlock.getRegistryName()+"-------------");
        PANELS.add(panelBlock);
        return panelBlock;
    }


    public static void registerBlocks(RegistryEvent.Register<Block> event){
        PANELS.forEach(panelBlock -> event.getRegistry().register(panelBlock));
        PANELS.clear();
    }

    public static AbstractPanelBlock asPanel(Form form){
        return switch (form) {
            case UPSIDE_TRIANGLE -> ModBlock.LET_WAY_PANEL;
            case TRIANGLE -> ModBlock.TRIANGLE_PANEL;
            case OCTAGON -> ModBlock.STOP_PANEL;
            case CIRCLE -> ModBlock.CIRCLE_PANEL;
            case SQUARE -> ModBlock.SQUARE_PANEL;
            case RECTANGLE -> ModBlock.RECTANGLE_PANEL;
            case ARROW -> ModBlock.DIRECTION_PANEL;
            case PLAIN_SQUARE -> ModBlock.HUGE_DIRECTION_PANEL;
            case DIAMOND -> ModBlock.DIAMOND_PANEL;
        };
    }

}

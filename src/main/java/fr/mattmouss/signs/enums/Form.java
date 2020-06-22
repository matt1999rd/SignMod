package fr.mattmouss.signs.enums;

public enum Form {
    UPSIDE_TRIANGLE(0,"let_way_panel"),
    TRIANGLE(1,"triangle_panel"),
    OCTOGONE(2,"stop_panel"),
    CIRCLE(3,"circle_panel"),
    SQUARE(4,"square_panel"),
    RECTANGLE(5,"rectangle_panel"),
    ARROW(6,"direction_panel"),
    PLAIN_SQUARE(7,"huge_direction_panel"),
    DIAMOND(8,"priority_road_panel");

    private final String block_name;
    private final int meta;

    Form(int meta,String block_name){
        this.meta = meta;
        this.block_name = block_name;
    }

    public String getRegistryName(){
        return block_name;
    }

    public static Form byIndex(int meta){
        Form[] forms = Form.values();
        if (meta>8 || meta<0){
            return null;
        }
        return forms[meta];
    }
}

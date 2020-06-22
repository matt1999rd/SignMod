package fr.mattmouss.signs.tileentity;

import fr.mattmouss.signs.tileentity.primary.SquareSignTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class TEType {

    @ObjectHolder("sign:square_panel")
    public static TileEntityType<SquareSignTileEntity> SQUARE_SIGN;
}

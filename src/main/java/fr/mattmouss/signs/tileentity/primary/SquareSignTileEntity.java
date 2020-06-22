package fr.mattmouss.signs.tileentity.primary;

import fr.mattmouss.signs.capabilities.SignStorage;
import fr.mattmouss.signs.tileentity.TEType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;


public class SquareSignTileEntity extends TileEntity {

    public SquareSignTileEntity() {
        super(TEType.SQUARE_SIGN);
    }

    private LazyOptional<SignStorage> storage = LazyOptional.of(this::getStorage).cast();

    private SignStorage getStorage() {
        return new SignStorage();
    }

}

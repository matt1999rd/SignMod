package fr.matt1999rd.signs.setup;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ServerProxy implements IProxy {
    @Override
    public void init() {

    }

    @Override
    public Level getClientWorld() {
        throw new IllegalStateException("Only run this on the client");
    }

    @Override
    public Player getClientPlayer() {
        throw new IllegalStateException("Only run this on the client");
    }
}

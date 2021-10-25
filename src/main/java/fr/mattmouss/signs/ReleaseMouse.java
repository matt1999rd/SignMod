package fr.mattmouss.signs;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ReleaseMouse {
    public static boolean debuggerReleaseControl(){
        GLFW.glfwSetInputMode(Minecraft.getInstance().mainWindow.getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        return true;
    }
}

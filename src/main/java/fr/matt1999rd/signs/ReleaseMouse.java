package fr.matt1999rd.signs;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class ReleaseMouse {
    public static boolean debuggerReleaseControl(){
        GLFW.glfwSetInputMode(Minecraft.getInstance().getWindow().getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        return true;
    }
}

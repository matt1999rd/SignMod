package fr.matt1999rd.signs.util;

import com.mojang.blaze3d.vertex.VertexFormat;
import fr.matt1999rd.signs.SignMod;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class PictureRenderState extends RenderStateShard {

    public static RenderType pictureRenderType(){
        ShaderInstance panelShader = SignMod.getPanelShader();
        if (panelShader == null){
            throw new IllegalStateException("Custom shader has not been registered properly.");
        }
        RenderStateShard.ShaderStateShard PANEL_SHADER = new ShaderStateShard(() -> panelShader);

        return RenderType.create("picture_rendering_test",
                DefaultVertexFormat.POSITION_COLOR_TEX,
                VertexFormat.Mode.QUADS,
                65536,false,true,
                RenderType.CompositeState.builder()
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .setShaderState(PANEL_SHADER)
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setCullState(RenderStateShard.CULL)
                        .setLayeringState(RenderStateShard.NO_LAYERING)
                        .createCompositeState(false));
    }

    public static RenderType getTextureRenderType(ResourceLocation location){
        return RenderType.create("texture_rendering",
                DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
                VertexFormat.Mode.QUADS,
                256,true,false,
                RenderType.CompositeState.builder()
                        .setTextureState(new RenderStateShard.TextureStateShard(location, false, false))
                        .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                        .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_CUTOUT_SHADER)
                        //.setAlphaState(RenderStateShard.DEFAULT_ALPHA)
                        .setCullState(RenderStateShard.CULL)
                        .setLightmapState(RenderStateShard.LIGHTMAP)
                        .createCompositeState(false));
    }

    public PictureRenderState(String p_i225973_1_, Runnable p_i225973_2_, Runnable p_i225973_3_) {
        super(p_i225973_1_, p_i225973_2_, p_i225973_3_);
        throw new UnsupportedOperationException();
    }
}

package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.entity.misc.PiratWoodBoat;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

// 26.1: the vanilla BoatRenderer is render-state based and handles hurt wobble, bubble angle and the
// water patch itself; it derives the texture from the model layer path ("textures/entity/<path>.png"),
// which matches the existing rats boat texture layout.
public class PiratWoodBoatRenderer extends BoatRenderer {

	public PiratWoodBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
		super(context, chest ? createChestBoatModelName(PiratWoodBoat.Type.PIRAT) : createBoatModelName(PiratWoodBoat.Type.PIRAT));
	}

	private static ModelLayerLocation createLocation(String path) {
		return new ModelLayerLocation(Identifier.fromNamespaceAndPath(RatsMod.MODID, path), "main");
	}

	public static ModelLayerLocation createBoatModelName(PiratWoodBoat.Type type) {
		return createLocation("boat/" + type.getName());
	}

	public static ModelLayerLocation createChestBoatModelName(PiratWoodBoat.Type type) {
		return createLocation("chest_boat/" + type.getName());
	}
}

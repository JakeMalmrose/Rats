package com.github.alexthe666.rats.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;

// 26.1: HierarchicalModel was removed; this static chest needs no per-entity animation, so Model.Simple fits.
public class ChristmasChestModel extends Model.Simple {

	public ChristmasChestModel(ModelPart root) {
		super(root, RenderTypes::entityCutout);
	}
}

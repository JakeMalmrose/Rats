package com.github.alexthe666.rats.registry;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.equipment.trim.MaterialAssetGroup;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

// 26.1: TrimMaterial is just (MaterialAssetGroup, description) — the ingredient item link and
// item-model index moved to data (trim materials are referenced from items via components and
// the trim ingredient item tags).
public class RatlantisTrimRegistry {

	public static final ResourceKey<TrimMaterial> GEM_OF_RATLANTIS = registerKey("gem_of_ratlantis");
	public static final ResourceKey<TrimMaterial> ORATCHALCUM = registerKey("oratchalcum");

	private static ResourceKey<TrimMaterial> registerKey(String name) {
		return ResourceKey.create(Registries.TRIM_MATERIAL, Identifier.fromNamespaceAndPath(RatsMod.MODID, name));
	}

	public static void bootstrap(BootstrapContext<TrimMaterial> context) {
		register(context, GEM_OF_RATLANTIS, Style.EMPTY.withColor(10353514));
		register(context, ORATCHALCUM, Style.EMPTY.withColor(11243608));
	}

	private static void register(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> trimKey, Style color) {
		Component description = Component.translatable(Util.makeDescriptionId("trim_material", trimKey.location())).withStyle(color);
		context.register(trimKey, new TrimMaterial(MaterialAssetGroup.create(trimKey.location().getPath()), description));
	}
}

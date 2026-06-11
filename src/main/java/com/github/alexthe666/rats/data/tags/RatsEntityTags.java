package com.github.alexthe666.rats.data.tags;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.registry.RatsEntityRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;


// 26.1: stripped to a TagKey constant holder; the datagen provider half targeted the
// removed 1.21 datagen API and the shipped JSON under src/generated/resources is canonical.
public class RatsEntityTags {

	public static final TagKey<EntityType<?>> RATS = create("rats");
	public static final TagKey<EntityType<?>> RAT_MOUNTS = create("rat_mounts");
	public static final TagKey<EntityType<?>> PLAGUE_IMMUNE = create("plague_immune");
	public static final TagKey<EntityType<?>> PLAGUE_LEGION = create("plague_legion");


	private static TagKey<EntityType<?>> create(String name) {
		return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(RatsMod.MODID, name));
	}
}

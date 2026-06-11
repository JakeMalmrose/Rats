package com.github.alexthe666.rats.client.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * 26.1's render-state split means renderers/models no longer receive the live entity. Citadel's
 * animation system needs it, so a global RegisterRenderStateModifiersEvent hook (see ModClientEvents)
 * stashes the entity into every render state under these keys and the model bridge reads it back.
 */
public final class RatsClientKeys {

	public static final ContextKey<LivingEntity> RENDER_STATE_LIVING_ENTITY =
			new ContextKey<>(Identifier.fromNamespaceAndPath("rats", "render_state_living_entity"));
	public static final ContextKey<Entity> RENDER_STATE_ENTITY =
			new ContextKey<>(Identifier.fromNamespaceAndPath("rats", "render_state_entity"));

	private RatsClientKeys() {
	}

	public static LivingEntity getLiving(LivingEntityRenderState state) {
		return state.getRenderData(RENDER_STATE_LIVING_ENTITY);
	}

	public static Entity getEntity(EntityRenderState state) {
		return state.getRenderData(RENDER_STATE_ENTITY);
	}
}

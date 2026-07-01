package com.github.alexthe666.rats.client.util;

import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityRenderingUtil {
	private static final Map<Identifier, Entity> ENTITY_MAP = new HashMap<>();
	private static final Set<Identifier> IGNORED_ENTITIES = new HashSet<>();

	@Nullable
	public static LivingEntity fetchEntity(@Nullable Identifier entityName, @Nullable Level level) {
		if (entityName != null && level != null && !IGNORED_ENTITIES.contains(entityName)) {
			EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(entityName);
			if (type != null) {
				Entity entity;
				if (type == EntityType.PLAYER) {
					entity = Minecraft.getInstance().player;
				} else {
					entity = ENTITY_MAP.computeIfAbsent(entityName, t -> {
						EntityType<?> cached = BuiltInRegistries.ENTITY_TYPE.getValue(t);
						return cached == null ? null : cached.create(level, net.minecraft.world.entity.EntitySpawnReason.LOAD);
					});
				}
				if (entity instanceof LivingEntity living) {
					return living;
				} else {
					addEntityToBlacklist(entityName);
				}
			}
		}
		return null;
	}

	public static int getAdjustedMobScale(Identifier entityName) {
		LivingEntity entity = fetchEntity(entityName, Minecraft.getInstance().level);
		if (entity != null) {
			int scale = 35;
			float height = entity.getBbHeight();
			float width = entity.getBbWidth();
			if (height > 2.0F || width > 2.0F) {
				scale = (int) (64 / Math.max(height, width));
			}
			return scale;
		}
		return 0;
	}

	public static void addEntityToBlacklist(Identifier entityName) {
		IGNORED_ENTITIES.add(entityName);
		ENTITY_MAP.remove(entityName);
	}

	// 26.1: entity-in-GUI rendering goes through the vanilla extraction helper; the old
	// EntityRenderDispatcher immediate path (overrideCameraOrientation/runAsFancy) is gone.
	public static void drawEntityOnScreen(GuiGraphicsExtractor graphics, int posX, int posY, int scale, float mouseX, float mouseY, @Nullable LivingEntity entity) {
		if (entity != null) {
			float xAngle = (float) Math.atan(mouseX / 40.0F);
			float yAngle = (float) Math.atan(mouseY / 40.0F);
			int half = scale * 2;
			InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, posX - half, posY - scale * 4, posX + half, posY, scale, 0.0F, xAngle, yAngle, entity);
		}
	}
}

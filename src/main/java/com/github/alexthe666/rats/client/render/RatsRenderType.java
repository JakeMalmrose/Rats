package com.github.alexthe666.rats.client.render;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.render.block.RatlantisPortalRenderer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.List;

/**
 * Custom {@link RenderType} factories for Rats. Minecraft 26.1 uses {@link RenderSetup} instead of the
 * removed {@code RenderStateShard} / {@code CompositeState}; glints are built on the vanilla GLINT pipeline.
 */
public class RatsRenderType {

	private RatsRenderType() {
	}

	private static Matrix4f rainbowMatrix(long time) {
		long i = Util.getMillis() * time;
		float f = (float) (i % 10000L) / 10000.0F;
		Matrix4f matrix4f = new Matrix4f().translation(0.0F, f, 0.0F);
		matrix4f.scale(0.16F);
		return matrix4f;
	}

	private static final TextureTransform RAINBOW_GLINT_TEXTURING = new TextureTransform("entity_glint_texturing", () -> rainbowMatrix(8L));

	private static RenderType glintType(String name, Identifier texture, TextureTransform transform) {
		RenderSetup setup = RenderSetup.builder(RenderPipelines.GLINT)
				.withTexture("Sampler0", texture)
				.useLightmap()
				.useOverlay()
				.setTextureTransform(transform)
				.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
				.createRenderSetup();
		return RenderType.create(name, setup);
	}

	private static RenderType dyeGlint(String name) {
		return glintType(name + "_glint", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/misc/special_dyes/" + name + "_glint.png"), RAINBOW_GLINT_TEXTURING);
	}

	private static RenderType entityGlint(String name) {
		return glintType(name + "_glint", Identifier.fromNamespaceAndPath(RatsMod.MODID, "textures/misc/" + name + "_glint.png"), TextureTransform.GLINT_TEXTURING);
	}

	private static final RenderType RATLANTIS_PORTAL = Util.make(() -> {
		// 1.21.1 used a bespoke two-texture portal shader; the vanilla end-portal pipeline provides the
		// same layered effect in 26.1, fed with the Ratlantis portal textures.
		RenderSetup setup = RenderSetup.builder(RenderPipelines.END_PORTAL)
				.withTexture("Sampler0", RatlantisPortalRenderer.PORTAL_BG)
				.withTexture("Sampler1", RatlantisPortalRenderer.PORTAL_FG)
				.createRenderSetup();
		return RenderType.create("ratlantis_portal", setup);
	});

	private static final RenderType ACE_GLINT = dyeGlint("ace");
	private static final RenderType AGENDER_GLINT = dyeGlint("agender");
	private static final RenderType ARO_GLINT = dyeGlint("aro");
	private static final RenderType BI_GLINT = dyeGlint("bi");
	private static final RenderType ENBY_GLINT = dyeGlint("enby");
	private static final RenderType GAY_GLINT = dyeGlint("gay");
	private static final RenderType GENDERFLUID_GLINT = dyeGlint("genderfluid");
	private static final RenderType LESBIAN_GLINT = dyeGlint("lesbian");
	private static final RenderType PAN_GLINT = dyeGlint("pan");
	private static final RenderType RAINBOW_GLINT = dyeGlint("rainbow");
	private static final RenderType TRANS_GLINT = dyeGlint("trans");

	private static final RenderType PISS_GLINT = dyeGlint("piss");
	private static final RenderType UNPLEASANT_GLINT = dyeGlint("unpleasant");

	private static final RenderType GREEN_ENTITY_GLINT = entityGlint("green");
	private static final RenderType YELLOW_ENTITY_GLINT = entityGlint("yellow");
	private static final RenderType WHITE_ENTITY_GLINT = entityGlint("white");
	private static final RenderType GOLD_ENTITY_GLINT = entityGlint("gold");

	public static RenderType getYellowGlint() {
		return YELLOW_ENTITY_GLINT;
	}

	public static RenderType getGreenGlint() {
		return GREEN_ENTITY_GLINT;
	}

	public static RenderType getWhiteGlint() {
		return WHITE_ENTITY_GLINT;
	}

	public static RenderType getGoldGlint() {
		return GOLD_ENTITY_GLINT;
	}

	public static RenderType getRatlantisPortal() {
		return RATLANTIS_PORTAL;
	}

	public static RenderType getRainbowGlint() {
		return RAINBOW_GLINT;
	}

	/** 1.21.1 used the energy-swirl shader for this; the vanilla factory matches. */
	public static RenderType getGlowingTranslucent(Identifier location) {
		return RenderTypes.energySwirl(location, 0.0F, 0.0F);
	}

	private static boolean encounteredMultiConsumerError = false;

	public static VertexConsumer createMergedVertexConsumer(VertexConsumer consumer1, VertexConsumer consumer2) {
		VertexConsumer vertexConsumer = consumer2;
		if (!encounteredMultiConsumerError) {
			try {
				vertexConsumer = VertexMultiConsumer.create(consumer1, consumer2);
			} catch (Exception e) {
				RatsMod.LOGGER.warn("Encountered issue mixing two render types together. Likely an issue with a rendering mod. This warning will only display once.");
				encounteredMultiConsumerError = true;
			}
		}
		return vertexConsumer;
	}

	/** Replaces removed {@code ItemRenderer#getFoilBuffer} for entity cutouts. */
	public static VertexConsumer entityFoilBuffer(MultiBufferSource buffer, RenderType base, boolean foil) {
		if (!foil) {
			return buffer.getBuffer(base);
		}
		return createMergedVertexConsumer(buffer.getBuffer(base), buffer.getBuffer(RenderTypes.entityGlint()));
	}

	/** Replaces removed {@code ItemRenderer#getArmorFoilBuffer} for armor cutouts. */
	public static VertexConsumer armorFoilBuffer(MultiBufferSource buffer, RenderType base, boolean foil) {
		if (!foil) {
			return buffer.getBuffer(base);
		}
		return createMergedVertexConsumer(buffer.getBuffer(base), buffer.getBuffer(RenderTypes.armorEntityGlint()));
	}

	public enum GlintType {
		AGENDER(AGENDER_GLINT, true, "agender"),
		AROMANTIC(ARO_GLINT, true, "aromantic", "aro"),
		ASEXUAL(ACE_GLINT, true, "asexual", "ace"),
		BISEXUAL(BI_GLINT, true, "bisexual", "bi"),
		GAY(GAY_GLINT, true, "gay", "mlm"),
		GENDERFLUID(GENDERFLUID_GLINT, true, "genderfluid", "fluid"),
		NONBINARY(ENBY_GLINT, true, "non-binary", "nonbinary", "enby", "nb"),
		LESBIAN(LESBIAN_GLINT, true, "lesbian", "wlw"),
		PANSEXUAL(PAN_GLINT, true, "pansexual", "pan"),
		TRANSGENDER(TRANS_GLINT, true, "transgender", "trans", "tratsgender"),

		PISS(PISS_GLINT, false, "piss"),
		UNPLEASANT(UNPLEASANT_GLINT, false, "unpleasant");

		private final RenderType type;
		private final boolean changesTexture;
		private final List<String> keywords;

		GlintType(RenderType type, boolean changesItemTex, String... matchingKeywords) {
			this.type = type;
			this.changesTexture = changesItemTex;
			this.keywords = Arrays.stream(matchingKeywords).toList();
		}

		public boolean changesItemTexture() {
			return this.changesTexture;
		}

		public RenderType getRenderType() {
			return this.type;
		}

		@Nullable
		public static RenderType getRenderTypeBasedOnKeyword(String word) {
			for (GlintType type : GlintType.values()) {
				for (String possibleWord : type.getKeywords()) {
					if (possibleWord.equalsIgnoreCase(word)) {
						return type.getRenderType();
					}
				}
			}
			return null;
		}

		@Nullable
		public static GlintType getGlintBasedOnKeyword(String word) {
			for (GlintType type : GlintType.values()) {
				for (String possibleWord : type.getKeywords()) {
					if (possibleWord.equalsIgnoreCase(word)) {
						return type;
					}
				}
			}
			return null;
		}

		public List<String> getKeywords() {
			return this.keywords;
		}
	}
}

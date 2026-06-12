package com.github.alexthe666.rats.client.model.deco;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Unit;

public class RatWaterBottleModel extends Model<Unit> {

	public RatWaterBottleModel(ModelPart root) {
		super(root, RenderTypes::entityTranslucent);
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition partdefinition = mesh.getRoot();

		PartDefinition bottle = partdefinition.addOrReplaceChild("bottle", CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-2.5F, -12.0F, -2.5F, 5.0F, 11.0F, 5.0F)
						.texOffs(0, 16)
						.addBox(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F),
				PartPose.offset(0.0F, 18.0F, -6.0F));

		bottle.addOrReplaceChild("out", CubeListBuilder.create()
						.texOffs(0, 21)
						.addBox(-0.5F, -0.5F, -0.5F, 1.0F, 4.0F, 1.0F),
				PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6109F, 0.0F, 0.0F));

		return LayerDefinition.create(mesh, 32, 32);
	}
}

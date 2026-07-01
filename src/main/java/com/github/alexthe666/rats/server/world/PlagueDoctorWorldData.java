package com.github.alexthe666.rats.server.world;

import com.github.alexthe666.rats.RatsMod;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

// 26.1: SavedData is codec-driven now (see vanilla WanderingTraderData); the old
// Factory/save(CompoundTag) shape is gone. Field names keep the legacy NBT keys.
public class PlagueDoctorWorldData extends SavedData {
	public static final Codec<PlagueDoctorWorldData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("PlagueDoctorSpawnDelay", 0).forGetter(data -> data.doctorSpawnDelay),
			Codec.INT.optionalFieldOf("PlagueDoctorSpawnChance", 0).forGetter(data -> data.doctorSpawnChance),
			UUIDUtil.STRING_CODEC.optionalFieldOf("PlagueDoctorId").forGetter(data -> Optional.ofNullable(data.doctorID))
	).apply(instance, PlagueDoctorWorldData::new));

	public static final SavedDataType<PlagueDoctorWorldData> TYPE = new SavedDataType<>(
			Identifier.fromNamespaceAndPath(RatsMod.MODID, "rats_world_data"), PlagueDoctorWorldData::new, CODEC);

	private int doctorSpawnDelay;
	private int doctorSpawnChance;
	@Nullable
	private UUID doctorID;

	public PlagueDoctorWorldData() {
		this.setDirty();
	}

	private PlagueDoctorWorldData(int doctorSpawnDelay, int doctorSpawnChance, Optional<UUID> doctorID) {
		this.doctorSpawnDelay = doctorSpawnDelay;
		this.doctorSpawnChance = doctorSpawnChance;
		this.doctorID = doctorID.orElse(null);
	}

	@Nullable
	public static PlagueDoctorWorldData get(Level level) {
		if (level instanceof ServerLevel server) {
			return server.getDataStorage().computeIfAbsent(TYPE);
		}
		return null;
	}

	public int getDoctorSpawnDelay() {
		return this.doctorSpawnDelay;
	}

	public void setDoctorSpawnDelay(int delay) {
		this.doctorSpawnDelay = delay;
		this.setDirty();
	}

	public int getDoctorSpawnChance() {
		return this.doctorSpawnChance;
	}

	public void setDoctorSpawnChance(int chance) {
		this.doctorSpawnChance = chance;
		this.setDirty();
	}

	public void setPlagueDoctorID(UUID id) {
		this.doctorID = id;
		this.setDirty();
	}
}

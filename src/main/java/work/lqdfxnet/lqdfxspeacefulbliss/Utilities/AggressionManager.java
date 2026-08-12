package work.lqdfxnet.lqdfxspeacefulbliss.Utilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Mob;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AggressionManager {
    private static final Map<UUID, MobAggressionData> CACHE = new ConcurrentHashMap<>();

    public static MobAggressionData get(Mob mob) {
        return CACHE.computeIfAbsent(mob.getUUID(), uuid -> loadFromEntity(mob));
    }

    public static void update(Mob mob, MobAggressionData data) {
        CACHE.put(mob.getUUID(), data);
        saveToEntity(mob, data);
        // LqDFxsPeacefulBliss.LOGGER.info("Updated: {} Data: {}", mob.getUUID(), mob.getPersistentData().getCompoundOrEmpty("peaceful_bliss"));

    }

    public static void onMobRemoved(Mob mob) {
        CACHE.remove(mob.getUUID());
    }

    private static MobAggressionData loadFromEntity(Mob mob) {
        CompoundTag tag = mob.getPersistentData().getCompoundOrEmpty("peaceful_bliss");
        return MobAggressionData.load(tag);
    }

    private static void saveToEntity(Mob mob, MobAggressionData data) {
        CompoundTag root = mob.getPersistentData();
        CompoundTag tag = data.save();
        root.put("peaceful_bliss", tag);
        // LqDFxsPeacefulBliss.LOGGER.info("Saved to : {} Data: {} ", mob.getUUID(), mob.getPersistentData().getCompoundOrEmpty("peaceful_bliss"));
    }


}


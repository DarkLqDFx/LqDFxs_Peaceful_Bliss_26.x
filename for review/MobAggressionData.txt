package work.lqdfxnet.lqdfxspeacefulbliss.Utilities;

import net.minecraft.nbt.CompoundTag;

/**
 * Stores aggression state + cooldown timestamp for a mob.
 * This class is intentionally simple and fast for caching.
 */
public final class MobAggressionData {

    // 0 = vanilla, 1 = passive, 2 = aggressive
    private int mobAggression;

    // Last time (game ticks) the mob was set to aggressive
    private long lastAggressionTimestamp;

    public MobAggressionData() {
        this.mobAggression = 0;
        this.lastAggressionTimestamp = 0L;
    }

    public MobAggressionData(int mobAggression, long lastAggressionTimestamp) {
        this.mobAggression = mobAggression;
        this.lastAggressionTimestamp = lastAggressionTimestamp;
    }

    // -----------------------------
    // Getters / Setters
    // -----------------------------

    public int mobAggression() {
        return mobAggression;
    }

    public void setMobAggression(int value) {
        this.mobAggression = value;
    }

    public long lastAggressionTimestamp() {
        return lastAggressionTimestamp;
    }

    public void setLastAggressionTimestamp(long value) {
        this.lastAggressionTimestamp = value;
    }

    // -----------------------------
    // NBT Serialization
    // -----------------------------

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("mob_aggression", mobAggression);
        tag.putLong("aggression_timestamp", lastAggressionTimestamp);
        return tag;
    }

    public static MobAggressionData load(CompoundTag tag) {
        int aggression = tag.getInt("mob_aggression").orElse(0);
        long timestamp = tag.getLong("aggression_timestamp").orElse(0L);
        return new MobAggressionData(aggression, timestamp);
    }

}

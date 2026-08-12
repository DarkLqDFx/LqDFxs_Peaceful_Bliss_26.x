package work.lqdfxnet.lqdfxspeacefulbliss.event;

import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.AggressionManager;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.MobAggressionData;
import work.lqdfxnet.lqdfxspeacefulbliss.Config;
import work.lqdfxnet.lqdfxspeacefulbliss.LqDFxsPeacefulBliss;

@EventBusSubscriber(modid = LqDFxsPeacefulBliss.MODID)
public class onMobSpawns {

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {

        Mob mob = event.getEntity();
        if (mob.level().isClientSide()) return;
        if (mob.getType().getCategory() != MobCategory.MONSTER) return;
        EntitySpawnReason type = event.getSpawnType();

        // Excluded mobs always remain vanilla (state 0)
        if (Config.isExcluded(mob)) {
            AggressionManager.update(mob, new MobAggressionData(0, 0));
            return;
        }

        // Forced vanilla spawn types
        if (isForcedVanillaSpawn(type)) {
            AggressionManager.update(mob, new MobAggressionData(0, 0));
            return;
        }

        // Peaceful Nether
        if (!Config.peaceful_nether.getAsBoolean() && mob.level().dimension() == Level.NETHER) {
            AggressionManager.update(mob, new MobAggressionData(0, 0));
            return;
        }

        // Default aggression from config (0, 1, or 2)
        //int defaultState = Config.default_aggression.get();
        int defaultState = 1;
        AggressionManager.update(mob, new MobAggressionData(defaultState, 0));
    }

    private static boolean isForcedVanillaSpawn(EntitySpawnReason type) {
        return type == EntitySpawnReason.SPAWNER ||
                type == EntitySpawnReason.TRIAL_SPAWNER ||
                type == EntitySpawnReason.EVENT ||
                type == EntitySpawnReason.REINFORCEMENT ||
                type == EntitySpawnReason.JOCKEY ||
                type == EntitySpawnReason.TRIGGERED;
    }

}

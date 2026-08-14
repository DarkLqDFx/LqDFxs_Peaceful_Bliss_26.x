package work.lqdfxnet.lqdfxspeacefulbliss.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.MobAggressionData;
import work.lqdfxnet.lqdfxspeacefulbliss.Config;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.AggressionManager;
import work.lqdfxnet.lqdfxspeacefulbliss.LqDFxsPeacefulBliss;

@EventBusSubscriber(modid = LqDFxsPeacefulBliss.MODID)
public class MobEvents {

    private static void tickMob(Mob mob) {
        MobAggressionData data = AggressionManager.get(mob);

        if (data.mobAggression() != 2) return;

        long now = mob.level().getGameTime();
        long elapsed;
        if (data.lastAggressionTimestamp() != 0) { elapsed = now - data.lastAggressionTimestamp(); }
        else { elapsed = 0; }

        long cooldown = Config.cooldown_ticks.getAsInt(); // clamped between min/max

        // Build Debug Billboard
        if (Config.debuggerMode.getAsBoolean()) {
            LqDFxsPeacefulBliss.LOGGER.info("== onMobTick ====");
            LqDFxsPeacefulBliss.LOGGER.info(" | Mob: {}", mob.getName().getString());
            LqDFxsPeacefulBliss.LOGGER.info(" |  UUID: {}", mob.getUUID());
            LqDFxsPeacefulBliss.LOGGER.info(" |  Aggression : {}", data.mobAggression());
            LqDFxsPeacefulBliss.LOGGER.info(" |  Last Aggression: {}", data.lastAggressionTimestamp());
            LqDFxsPeacefulBliss.LOGGER.info(" |  Elapsed: {}", elapsed);
            boolean expired = elapsed >= cooldown;
            LqDFxsPeacefulBliss.LOGGER.info(" |  Expired: {}", expired );
        }

        if (elapsed >= cooldown) {
            // Set to Passive
            data.setMobAggression(1);
            data.setLastAggressionTimestamp(0);
            AggressionManager.update(mob, data);
            mob.setTarget(null);
            return;
        }

        if (data.mobAggression() == 1) {
            data.setLastAggressionTimestamp(0);
            AggressionManager.update(mob, data);
        }
    }


    @SubscribeEvent
    public static void onMobTickEvent(EntityTickEvent.Pre event) {
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (mob.level().isClientSide()) return;

        MobEvents.tickMob(mob);
    }

    @SubscribeEvent
    public static void onMobDeathEvent(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (entity instanceof Mob mob) AggressionManager.onMobRemoved(mob);

    }

    @SubscribeEvent
    public static void onFinalizeSpawn(FinalizeSpawnEvent event) {

        Mob mob = event.getEntity();
        if (mob.level().isClientSide()) return;
        if (mob.getType().getCategory() != MobCategory.MONSTER) return;
        EntitySpawnReason type = event.getSpawnType();

        // Overworld Surface Spawns
        if (Config.no_surface_spawns.getAsBoolean() && mob.getType() == EntityType.CREEPER || mob.getType() == EntityType.SKELETON || mob.getType() == EntityType.ZOMBIE ) {
            BlockPos pos =  event.getEntity().getOnPos();
            if (event.getLevel().canSeeSky(pos)) {
                event.setSpawnCancelled(true);
                return;
            }
        }

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
        if (Config.peaceful_nether.getAsBoolean() && mob.level().dimension() == Level.NETHER) {
            AggressionManager.update(mob, new MobAggressionData(0, 0));
            return;
        }

        // Default aggression is 1 for Passive Gameplay Mechanics
        // This is for any mob that is missed in the above rules
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

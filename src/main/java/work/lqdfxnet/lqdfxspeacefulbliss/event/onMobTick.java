package work.lqdfxnet.lqdfxspeacefulbliss.event;

import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.MobAggressionData;
import work.lqdfxnet.lqdfxspeacefulbliss.Config;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.AggressionManager;
import work.lqdfxnet.lqdfxspeacefulbliss.LqDFxsPeacefulBliss;

@EventBusSubscriber(modid = LqDFxsPeacefulBliss.MODID)
public class onMobTick {

    public static void tickMob(Mob mob) {
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

        onMobTick.tickMob(mob);
    }


}

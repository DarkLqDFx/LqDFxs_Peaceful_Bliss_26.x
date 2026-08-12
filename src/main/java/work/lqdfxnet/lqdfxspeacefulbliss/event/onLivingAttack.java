package work.lqdfxnet.lqdfxspeacefulbliss.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import work.lqdfxnet.lqdfxspeacefulbliss.Config;
import work.lqdfxnet.lqdfxspeacefulbliss.LqDFxsPeacefulBliss;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.AggressionManager;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.MobAggressionData;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.getSource;

public class onLivingAttack {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {

        LqDFxsPeacefulBliss.LOGGER.info("Who dealt it: {}", getSource.damageSource(event.getSource()));

        Entity whoDealt = getSource.damageSource(event.getSource());
        if (whoDealt == null) return;
        if ((whoDealt instanceof Mob mob) && (event.getEntity() instanceof Mob mobA)) {
            mobA.setTarget(mob);
            return;
        }

        if (!(event.getSource().getEntity() instanceof Player)) return;
        if (!(event.getEntity() instanceof Mob mob)) return;
        if (mob.level().isClientSide()) return;

        if (Config.isExcluded(mob)) return;

        MobAggressionData data = AggressionManager.get(mob);
        long now = mob.level().getGameTime();
        long elapsed;
        if (data.lastAggressionTimestamp() != 0) { elapsed = now - data.lastAggressionTimestamp(); }
        else { elapsed = 0; }
        long cooldown = Config.cooldown_ticks.getAsInt(); // clamped between min/max

        // Build Debug Billboard
        if (Config.debuggerMode.getAsBoolean()) {
            LqDFxsPeacefulBliss.LOGGER.info("== onLivingAttack ====");
            LqDFxsPeacefulBliss.LOGGER.info(" | Mob: {}", event.getEntity().getName().getString());
            LqDFxsPeacefulBliss.LOGGER.info(" |  UUID: {}", event.getEntity().getUUID());
            LqDFxsPeacefulBliss.LOGGER.info(" |  Aggression : {}", data.mobAggression());
            LqDFxsPeacefulBliss.LOGGER.info(" |  Last Aggression: {}", data.lastAggressionTimestamp());
            LqDFxsPeacefulBliss.LOGGER.info(" |  Elapsed: {} ticks", elapsed);
            boolean expired = elapsed >= cooldown;
            LqDFxsPeacefulBliss.LOGGER.info(" |  Expired: {}", expired );
        }

        if (elapsed >= cooldown && data.lastAggressionTimestamp() != 0 && data.mobAggression() == 2) {
            // Set to Passive
            data.setMobAggression(1);
            data.setLastAggressionTimestamp(0);
            AggressionManager.update(mob, data);
            mob.setTarget(null);
            mob.setAggressive(false);
        }
        else {
            // Set to Aggressive
            data.setMobAggression(2);
            data.setLastAggressionTimestamp(now);
            AggressionManager.update(mob, data);
        }
    }

}

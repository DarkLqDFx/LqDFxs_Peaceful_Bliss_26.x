package work.lqdfxnet.lqdfxspeacefulbliss.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.AggressionManager;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.MobAggressionData;
import work.lqdfxnet.lqdfxspeacefulbliss.Config;
import work.lqdfxnet.lqdfxspeacefulbliss.LqDFxsPeacefulBliss;

@EventBusSubscriber(modid = LqDFxsPeacefulBliss.MODID)
public class onLivingChangeTarget {

    @SubscribeEvent
    public static void livingChangeTarget(LivingChangeTargetEvent event) {
        LivingEntity entity = event.getEntity();
        LivingEntity newTarget = event.getNewAboutToBeSetTarget();

        if (!(newTarget instanceof Player)) return;
        if (!(entity instanceof Mob mob)) return;
        if (mob.getType().getCategory() != MobCategory.MONSTER) return;
        if (entity.level().isClientSide()) return;

        MobAggressionData data = AggressionManager.get(mob);
        long now = mob.level().getGameTime();
        long elapsed;
        if (data.lastAggressionTimestamp() != 0) { elapsed = now - data.lastAggressionTimestamp(); }
        else { elapsed = 0; }

        long cooldown = Config.cooldown_ticks.getAsInt();

        // Build Debug Billboard
        if (Config.debuggerMode.getAsBoolean()) {
            LqDFxsPeacefulBliss.LOGGER.info("== onLivingChangeTarget ====");
            LqDFxsPeacefulBliss.LOGGER.info(" | Mob: {}", entity.getName().getString());
            LqDFxsPeacefulBliss.LOGGER.info(" |  UUID: {}", entity.getUUID());
            LqDFxsPeacefulBliss.LOGGER.info(" |  Aggression : {}", data.mobAggression());
            LqDFxsPeacefulBliss.LOGGER.info(" |  Last Aggression: {}", data.lastAggressionTimestamp());
            LqDFxsPeacefulBliss.LOGGER.info(" |  Elapsed: {} ticks", elapsed);
            boolean expired = elapsed >= cooldown;
            LqDFxsPeacefulBliss.LOGGER.info(" |  Expired: {}", expired );
        }

        // Vanilla behavior and for excluded list
        if (data.mobAggression() == 0) return;

        // Preserve mob vs mob
        if (newTarget != null && !(newTarget instanceof Player)) return;

        // Passive to players
        if (data.mobAggression() == 1) {
            event.setNewAboutToBeSetTarget(null);
            return;
        }

        // Aggressive to players as normal
        if (data.mobAggression() == 2 && newTarget instanceof Player player) event.setNewAboutToBeSetTarget(player);

    }
}

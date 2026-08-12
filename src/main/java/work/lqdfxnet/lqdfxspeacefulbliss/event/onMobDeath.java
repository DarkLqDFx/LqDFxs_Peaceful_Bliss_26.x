package work.lqdfxnet.lqdfxspeacefulbliss.event;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import work.lqdfxnet.lqdfxspeacefulbliss.Utilities.AggressionManager;

@EventBusSubscriber
public class onMobDeath {

    @SubscribeEvent
    public static void onMobDeathEvent(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) return;
        if (entity instanceof Mob mob) {
            AggressionManager.onMobRemoved(mob);
            // LqDFxsPeacefulBliss.LOGGER.info("something died!");
        }

    }
}

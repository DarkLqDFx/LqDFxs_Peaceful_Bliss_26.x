package work.lqdfxnet.lqdfxspeacefulbliss.Utilities;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;

public class getSource {

    public static Entity damageSource(DamageSource source) {
        Entity sourceEntity = source.getEntity();
        Entity directEntity = source.getDirectEntity();

        //Melee
        if (sourceEntity instanceof Player player) { return player; }
        if (sourceEntity instanceof Mob mob) { return mob; }

        // Projectile owner
        if (directEntity instanceof Projectile proj && proj.getOwner() instanceof Player player) return player;
        if (directEntity instanceof Projectile proj && proj.getOwner() instanceof Player player) return player;

        // Thrown items
        if (directEntity instanceof ThrowableItemProjectile tip && tip.getOwner() instanceof Mob mob) return mob;
        if (directEntity instanceof ThrowableItemProjectile tip && tip.getOwner() instanceof Mob mob) return mob;

        return null;

    }
}

/*

    public static boolean isPlayerAttack(DamageSource source) {
        Entity direct = source.getDirectEntity();
        Entity attacker = source.getEntity();

        // Melee
        if (source.is(DamageTypes.PLAYER_ATTACK)) return true;
        if (source.is(DamageTypes.PLAYER_EXPLOSION)) return true;

        // Projectile owner
        if (direct instanceof Projectile proj && proj.getOwner() instanceof Player) return true;
        // Thrown items
        return direct instanceof ThrowableItemProjectile tip && tip.getOwner() instanceof Player;

    }

    public static boolean isMobAttack(DamageSource source) {
        Entity direct = source.getDirectEntity();
        Entity attacker = source.getEntity();

        // Melee
        if (source.is(DamageTypes.MOB_ATTACK)) return true;

        // Projectile/Thrown owner
        if (direct instanceof Projectile proj && proj.getOwner() instanceof Mob) return true;
        return direct instanceof ThrowableItemProjectile tip && tip.getOwner() instanceof Mob;

    }

 */
package work.lqdfxnet.lqdfxspeacefulbliss;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

@EventBusSubscriber
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Pacifier
    public static ModConfigSpec.BooleanValue debuggerMode;
    public static ModConfigSpec.BooleanValue no_surface_spawns;
    public static ModConfigSpec.ConfigValue<List<? extends String>> exclude_mobs;
    public static ModConfigSpec.BooleanValue peaceful_nether;
    public static ModConfigSpec.IntValue cooldown_ticks;

    static {

        // -------------------------------------------------
        // Pacifier
        // -------------------------------------------------

        //BUILDER.comment("Peaceful Bliss").push("peacefulbliss");

        debuggerMode = BUILDER
                .comment("Enable Debug Console output. Leave false unless otherwise asked!")
                .define("debug_output", false);

        no_surface_spawns = BUILDER
                .comment("Turn off Overworld surface spawning of Creepers, Skeletons, and Zombies")
                .define("no_surface_spawns", false);

        exclude_mobs = BUILDER
                .comment("Mobs excluded from pacification")
                .defineListAllowEmpty("exclude_mobs",
                        List.of("minecraft:ender_dragon", "minecraft:wither", "minecraft:elder_guardian","minecraft:enderman", "minecraft:pillager", "minecraft:vindicator",
                                "minecraft:evoker", "minecraft:ravager"), () -> "minecraft:creeper", Config::validateEntity);

        peaceful_nether = BUILDER
                .comment("Allow pacification in the Nether?")
                .define("in_nether", false);

        cooldown_ticks = BUILDER
                .comment("Aggression cooldown in ticks (min 100 [5 Sec], max 600 [30 sec])")
                .defineInRange("cooldown_ticks", 100, 100, 600);

        BUILDER.pop();
    }

    private static boolean validateEntity(final Object obj) {
        return obj instanceof String entityName && BuiltInRegistries.ENTITY_TYPE.containsKey(Identifier.parse(entityName));
    }

    public static boolean isExcluded(Mob mob) {
        EntityType<?> type = mob.getType();
        Identifier id = EntityType.getKey(type);

        return Config.exclude_mobs.get().contains(id.toString());
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}
package divineadditions.holders;

import divineadditions.DivineAdditions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class Fluids {
    public static final Fluid DNA = new Fluid(
            new ResourceLocation(DivineAdditions.MOD_NAME, "dna").toString(),
            new ResourceLocation(DivineAdditions.MOD_ID, "blocks/dna"),
            new ResourceLocation(DivineAdditions.MOD_ID, "blocks/dna_still")
    );
}

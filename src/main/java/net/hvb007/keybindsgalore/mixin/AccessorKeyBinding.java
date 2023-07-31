//IDK what this is
package net.hvb007.keybindsgalore.mixin;

import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface AccessorKeyBinding {
    @Accessor void setTimesPressed(int timesPressed);
    @Accessor void setPressed(boolean pressed);
}

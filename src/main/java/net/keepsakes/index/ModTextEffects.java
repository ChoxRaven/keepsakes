package net.keepsakes.index;

import net.keepsakes.textEffects.DelayedShakeEffect;
import net.keepsakes.textEffects.OffsetEffect;
import net.keepsakes.textEffects.PlaqueEffect;
import snownee.textanimator.effect.EffectFactory;

public interface ModTextEffects {
    static void initialize() {
        EffectFactory.register(DelayedShakeEffect::new);
        EffectFactory.register(PlaqueEffect::new);
        EffectFactory.register(OffsetEffect::new);
    }
}

package net.keepsakes.textEffects;

import net.minecraft.util.Util;
import net.minecraft.util.math.Vec2f;
import snownee.textanimator.TextAnimatorClient;
import snownee.textanimator.effect.BaseEffect;
import snownee.textanimator.effect.EffectSettings;
import snownee.textanimator.effect.params.Params;

public class DelayedShakeEffect extends BaseEffect {
    private final float amp, speed;

    public DelayedShakeEffect(Params params) {
        super(params);
        this.amp = (float) params.getDouble("a").orElse(1.0);
        this.speed = (float) params.getDouble("f").orElse(1.0);
    }

    @Override
    public void apply(EffectSettings settings) {
        long baseTime = (long) (Util.getMeasuringTimeMs() * 0.01F * speed + settings.codepoint + settings.index);

        Vec2f dir;
        if (settings.isShadow) {
            dir = TextAnimatorClient.getRandomDirection((int) (baseTime - 1));

            settings.x -= 1f;
            settings.y -= 1f;
        } else {
            dir = TextAnimatorClient.getRandomDirection((int) baseTime);
        }

        settings.x += dir.x * 0.6F * amp;
        settings.y += dir.y * 0.6F * amp;
    }

    @Override
    public String getName() {
        return "delayedshake";
    }
}

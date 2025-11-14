package net.keepsakes.textEffects;

import net.minecraft.util.Util;
import snownee.textanimator.effect.BaseEffect;
import snownee.textanimator.effect.EffectSettings;
import snownee.textanimator.effect.params.Params;

public class PlaqueEffect extends BaseEffect {
    private final float speed;
    private final float phase;
    private final float waveInterval;

    public PlaqueEffect(Params params) {
        super(params);
        this.speed = (float) params.getDouble("f").orElse(1.0);
        this.phase = (float) params.getDouble("w").orElse(1.0);
        this.waveInterval = (float) params.getDouble("s").orElse(3.0);
    }

    @Override
    public void apply(EffectSettings settings) {
        float t = (Util.getMeasuringTimeMs() * 0.001f * speed - settings.index * phase * 0.2f) % waveInterval;
        float HALF_PI = (float) (Math.PI / 2);
        t = t / waveInterval;

        float whiteness = 0;
        if (t < 0.3f) {
            whiteness = (float) Math.sin(t / 0.3f * HALF_PI);
        } else if (t < 0.7f) {
            whiteness = 1.0f;
        } else {
            whiteness = (float) Math.sin((1.0f - t) / 0.3f * HALF_PI);
        }

        if (!settings.isShadow) {
            settings.r = lerp(whiteness, 1.0f, settings.r);
            settings.g = lerp(whiteness, 1.0f, settings.g);
            settings.b = lerp(whiteness, 1.0f, settings.b);
        }
        settings.y += (whiteness * 2f) - 2f;
    }

    private float lerp(float delta, float start, float end) {
        return start + (end - start) * delta;
    }

    @Override
    public String getName() {
        return "plaque";
    }
}

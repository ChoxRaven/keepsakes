package net.keepsakes.textEffects;

import snownee.textanimator.effect.BaseEffect;
import snownee.textanimator.effect.EffectSettings;
import snownee.textanimator.effect.params.Params;

public class OffsetEffect extends BaseEffect {
    private final float offsetX;
    private final float offsetY;
    private final float offsetRot;

    public OffsetEffect(Params params) {
        super(params);
        this.offsetX = (float) params.getDouble("x").orElse(0.0);
        this.offsetY = (float) params.getDouble("y").orElse(0.0);
        this.offsetRot = (float) params.getDouble("z").orElse(0.0);
    }

    @Override
    public void apply(EffectSettings settings) {
        settings.x += offsetX;
        settings.y += offsetY;
        settings.rot += offsetRot;
    }

    @Override
    public String getName() {
        return "offset";
    }
}

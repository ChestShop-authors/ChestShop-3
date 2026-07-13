package com.Acrobot.ChestShop.Plugins.PlotSquared;

import com.plotsquared.core.configuration.caption.StaticCaption;
import com.plotsquared.core.plot.flag.types.BooleanFlag;
import org.checkerframework.checker.nullness.qual.NonNull;

public class AllowChestshopFlag extends BooleanFlag<AllowChestshopFlag> {

    public static final AllowChestshopFlag ALLOW_SHOP_TRUE = new AllowChestshopFlag(true);
    public static final AllowChestshopFlag ALLOW_SHOP_FALSE = new AllowChestshopFlag(false);

    protected AllowChestshopFlag(@NonNull Boolean value) {
        super(value, StaticCaption.of("Allow ChestShop shops on this plot or not"));
    }

    @Override
    protected AllowChestshopFlag flagOf(@NonNull Boolean value) {
        return value ? ALLOW_SHOP_TRUE : ALLOW_SHOP_FALSE;
    }
}

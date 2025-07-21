package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.ChestShop;
import com.plotsquared.core.configuration.caption.TranslatableCaption;
import com.plotsquared.core.plot.flag.types.BooleanFlag;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ChestshopAllowShopFlag extends BooleanFlag<ChestshopAllowShopFlag> {
    public static final ChestshopAllowShopFlag ALLOW_SHOP_FLAG_TRUE = new ChestshopAllowShopFlag(true);
    public static final ChestshopAllowShopFlag ALLOW_SHOP_FLAG_FALSE = new ChestshopAllowShopFlag(false);

    private ChestshopAllowShopFlag(final boolean value) {
        super(value, TranslatableCaption.of("flags.flag_description_allow_shop"));
    }

    static {
        // This is a workaround for the fact that PlotSquared's GlobalFlagContainer is not initialized at the time of plugin load.

        ChestShop.runInAsyncThread(() -> {
            com.plotsquared.core.plot.flag.GlobalFlagContainer flagContainer = com.plotsquared.core.plot.flag.GlobalFlagContainer.getInstance();

            while (flagContainer == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                flagContainer = com.plotsquared.core.plot.flag.GlobalFlagContainer.getInstance();
                if(flagContainer == null) continue;

                flagContainer.addFlag(ALLOW_SHOP_FLAG_TRUE);
            }
        });
    }

    @Override
    protected ChestshopAllowShopFlag flagOf(@NonNull final Boolean value) {
        return value ? ALLOW_SHOP_FLAG_TRUE : ALLOW_SHOP_FLAG_FALSE;
    }
}
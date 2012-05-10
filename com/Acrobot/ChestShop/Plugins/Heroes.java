package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class Heroes implements Listener {
    private com.herocraftonline.heroes.Heroes heroes;

    public Heroes(com.herocraftonline.heroes.Heroes heroes) {
        this.heroes = heroes;
    }

    @EventHandler
    public void shopCreated(ShopCreatedEvent event) {
        double heroExp = Config.getDouble(Property.HEROES_EXP);

        if (heroExp == 0) {
            return;
        }

        Hero hero = heroes.getCharacterManager().getHero(event.getPlayer());

        if (hero.hasParty()) {
            hero.getParty().gainExp(heroExp, HeroClass.ExperienceType.EXTERNAL, event.getPlayer().getLocation());
        } else {
            hero.gainExp(heroExp, HeroClass.ExperienceType.EXTERNAL);
        }
    }
}

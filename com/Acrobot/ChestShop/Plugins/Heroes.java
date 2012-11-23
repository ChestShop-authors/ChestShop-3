package com.Acrobot.ChestShop.Plugins;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

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
        double heroExp = Properties.HEROES_EXP;

        if (heroExp == 0) {
            return;
        }

        Hero hero = heroes.getCharacterManager().getHero(event.getPlayer());

        if (hero.hasParty()) {
            hero.getParty().gainExp(heroExp, HeroClass.ExperienceType.EXTERNAL, event.getPlayer().getLocation());
        } else {
            hero.gainExp(heroExp, HeroClass.ExperienceType.EXTERNAL, event.getPlayer().getLocation());
        }
    }

    public static Heroes getHeroes(Plugin plugin) {
        if (!(plugin instanceof com.herocraftonline.heroes.Heroes)) {
            return null;
        }

        return new Heroes((com.herocraftonline.heroes.Heroes) plugin);
    }
}

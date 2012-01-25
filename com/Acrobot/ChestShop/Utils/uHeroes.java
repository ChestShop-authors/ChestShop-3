package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.hero.Hero;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class uHeroes {
    public static Heroes heroes;

    public static void addHeroExp(Player p) {
        if (uHeroes.heroes != null) {
            Hero hero = uHeroes.heroes.getHeroManager().getHero(p);
            if (hero.hasParty()) {
                hero.getParty().gainExp(Config.getDouble(Property.HEROES_EXP), HeroClass.ExperienceType.EXTERNAL, p.getLocation());
            } else {
                hero.gainExp(Config.getDouble(Property.HEROES_EXP), HeroClass.ExperienceType.EXTERNAL);
            }
        }
    }
}

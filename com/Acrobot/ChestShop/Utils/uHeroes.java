package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.classes.HeroClass;
import com.herocraftonline.dev.heroes.persistence.Hero;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class uHeroes {
    public static Heroes heroes;

    public static void addHeroExp(Player p) {
        if (heroes != null) {
            Hero hero = heroes.getHeroManager().getHero(p);
            hero.gainExp(Config.getDouble(Property.HEROES_EXP), HeroClass.ExperienceType.EXTERNAL);
        }
    }
}
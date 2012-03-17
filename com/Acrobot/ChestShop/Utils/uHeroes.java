package com.Acrobot.ChestShop.Utils;

import com.Acrobot.ChestShop.Config.Config;
import com.Acrobot.ChestShop.Config.Property;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass;
import org.bukkit.entity.Player;

/**
 * @author Acrobot
 */
public class uHeroes {
    public static Heroes heroes;

    public static void addHeroExp(Player p) {
        if (heroes != null) {
            Hero hero = heroes.getCharacterManager().getHero(p);
            if (hero.hasParty()) {
                hero.getParty().gainExp(Config.getDouble(Property.HEROES_EXP), HeroClass.ExperienceType.EXTERNAL, p.getLocation());
            } else {
                hero.gainExp(Config.getDouble(Property.HEROES_EXP), HeroClass.ExperienceType.EXTERNAL);
            }
        }
    }
}

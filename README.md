<img src="/assets/ChestShop_mini.png" alt="ChestShop icon" height="32px"> ChestShop
================================

![Created At](https://img.shields.io/github/created-at/ChestShop-authors/ChestShop-3) 
[![ChestShop license: LGPL-2.1](https://img.shields.io/github/license/ChestShop-authors/ChestShop-3)](LICENSE) [![](https://badges.crowdin.net/chestshop-3/localized.svg)](https://crowdin.com/project/chestshop-3/)
[![Latest Release](https://img.shields.io/github/v/release/ChestShop-authors/ChestShop-3)](https://github.com/ChestShop-authors/ChestShop-3/releases/latest)
[![Commits since latest release](https://img.shields.io/github/commits-since/ChestShop-authors/ChestShop-3/latest?color=red)](https://github.com/ChestShop-authors/ChestShop-3/commits/master/) [![bStats Server Count](https://img.shields.io/bstats/servers/1109.svg) ![bStats Player Count](https://img.shields.io/bstats/players/1109.svg)](https://bstats.org/plugin/bukkit/ChestShop)

ChestShop is an awesome plugin for managing your server's economy. 
By using chests and signs, you can effectively create your own market!

ChestShop also makes administrators' lives easier. 
Simply drag-and-drop the .jar to your "plugins" folder, install [Vault](http://dev.bukkit.org/server-mods/vault/) or [Reserve](https://www.spigotmc.org/resources/reserve.50739/) and a [compatible Economy plugin](https://www.spigotmc.org/wiki/chestshop-economy-plugins/) which provides money, and you're done!

It's never been that easy to create shops! With features like shop protection and anti-lag protection, you won't have to worry about your server's economy anymore! It even is [compatible](https://dev.bukkit.org/projects/chestshop/#:~:text=Compatibility,provide%20additional%20functionality%3A) with a good chunk of other plugins and there is an ever-growing list of [addon plugins](https://dev.bukkit.org/projects/chestshop/#:~:text=Additional%20modules%20for%20ChestShop%3A) to add even more compatibility and features!

If you need any help check the wiki or just ask in IRC or Discord!

Helpful Links
--------------------------------
* [Wiki](https://www.spigotmc.org/wiki/chestshop-3/)
* [List of known compatible Economy plugins](https://www.spigotmc.org/wiki/chestshop-economy-plugins/)
* [IRC channel](https://kiwiirc.com/client/irc.esper.net/#chestshop): `#chestshop` on `irc.esper.net`
* [ChestShop channel](https://discord.gg/FuTujm6Egd) in Phoenix616's Discord
* [Latest Dev Build](https://ci.minebench.de/job/ChestShop-3/) ![](https://img.shields.io/github/commits-since/ChestShop-authors/ChestShop-3/latest?color=red)
* [Build for 1.12](https://ci.minebench.de/view/ChestShop/job/ChestShop-3-1.12/)
* [Build for 1.8.8](https://ci.minebench.de/view/ChestShop/job/ChestShop-3-1.8.8/)

Project Pages
--------------------------------
* [BukkitDev site](http://dev.bukkit.org/projects/chestshop/)
* [Modrinth page](https://modrinth.com/plugin/chestshop)
* [SpigotMC resource](https://www.spigotmc.org/resources/chestshop.51856/)
* [PaperMC's Hangar](https://hangar.papermc.io/ChestShop/ChestShop)
* [Original dbo Thread](http://forums.bukkit.org/threads/4150/)

Building
--------------------------------

#### Installing

To build ChestShop, you'll need a Maven installation.
* [Maven download](http://maven.apache.org/download.cgi)
* Launch `mvn clean install` -- that's it!

Pre-build versions can also be found on the [Jenkins server](https://ci.minebench.de/job/ChestShop-3/).

#### Installing external dependencies

To install new external dependencies that aren't available in a maven repo, place your .jar into the main folder and launch the `install_dependency_to_repo.sh` script - it'll guide you through the process.

Development Links
--------------------------------
* [Dev Builds](https://ci.minebench.de/job/ChestShop-3/)
* [Bounties via IssueHunt](https://issuehunt.io/r/ChestShop-authors/ChestShop-3?tab=idle)
* [Localization](https://crowdin.com/project/chestshop-3)
* [Qodana code quality](https://qodana.cloud/projects/zxDG5/)
* [Old bug Tracker](http://dev.bukkit.org/server-mods/chestshop/tickets/?status=+) (please use GitHub issues!)

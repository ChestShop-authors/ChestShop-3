package com.nijikokun.register.payment.forChestShop.methods;

import com.nijikokun.register.payment.forChestShop.Method;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.api.Economy;

import org.bukkit.plugin.Plugin;

/**
 * Essentials 17 Implementation of Method
 *
 * @author Nijikokun <nijikokun@shortmail.com> (@nijikokun)
 * @author Snowleo
 * @author Acrobot
 * @author KHobbits
 * @copyright (c) 2011
 * @license AOL license <http://aol.nexua.org>
 */
public class EE17 implements Method {
    private Essentials Essentials;

    public Essentials getPlugin() {
        return this.Essentials;
    }

    public String getName() {
        return "Essentials";
    }

    public String getVersion() {
        return "2.2";
    }
    
    public int fractionalDigits() {
    	return -1;
    }

    public String format(double amount) {
        return Economy.format(amount);
    }

    public boolean hasBanks() {
        return false;
    }

    public boolean hasBank(String bank) {
        return false;
    }

    public boolean hasAccount(String name) {
        return Economy.playerExists(name);
    }

    public boolean hasBankAccount(String bank, String name) {
        return false;
    }

    public boolean createAccount(String name) {
        if(hasAccount(name))
            return false;
        
        Economy.createNPC(name);
        return true;
    }

    public boolean createAccount(String name, double balance) {
        if(hasAccount(name))
            return false;
        
        Economy.createNPC(name);

        try {
            Economy.setMoney(name, balance);
        } catch (Exception ex) {
            System.out.println("[REGISTER] Error in Essentials Economy: " + ex.getMessage());
            return false;
        }

        return true;
    }

    public MethodAccount getAccount(String name) {
        if(!hasAccount(name)) 
            return null;

        return new EEcoAccount(name);
    }

    public MethodBankAccount getBankAccount(String bank, String name) {
        return null;
    }
	
    public boolean isCompatible(Plugin plugin) {
        try { Class.forName("com.earth2me.essentials.api.Economy"); }
        catch(Exception e) { return false; }

        return plugin.getDescription().getName().equalsIgnoreCase("essentials")
            && plugin instanceof Essentials;
    }

    public void setPlugin(Plugin plugin) {
        Essentials = (Essentials)plugin;
    }

    public class EEcoAccount implements MethodAccount {
        private String name;

        public EEcoAccount(String name) {
            this.name = name;
        }

        public double balance() {
            Double balance = 0.0;

            try {
                balance = Economy.getMoney(this.name);
            } catch (Exception ex) {
                System.out.println("[REGISTER] Error in Essentials Economy: " + ex.getMessage());
            }

            return balance;
        }

        public boolean set(double amount) {
            try {
                Economy.setMoney(name, amount);
            } catch (Exception ex) {
                System.out.println("[REGISTER] Error in Essentials Economy: " + ex.getMessage());
                return false;
            }

            return true;
        }

        public boolean add(double amount) {
            try {
                Economy.add(name, amount);
            } catch (Exception ex) {
                System.out.println("[REGISTER] Error in Essentials Economy: " + ex.getMessage());
                return false;
            }

            return true;
        }

        public boolean subtract(double amount) {
            try {
                Economy.subtract(name, amount);
            } catch (Exception ex) {
                System.out.println("[REGISTER] Error in Essentials Economy: " + ex.getMessage());
                return false;
            }

            return true;
        }

        public boolean multiply(double amount) {
            try {
                Economy.multiply(name, amount);
            } catch (Exception ex) {
                System.out.println("[REGISTER] Error in Essentials Economy: " + ex.getMessage());
                return false;
            }

            return true;
        }

        public boolean divide(double amount) {
            try {
                Economy.divide(name, amount);
            } catch (Exception ex) {
                System.out.println("[REGISTER] Error in Essentials Economy: " + ex.getMessage());
                return false;
            }

            return true;
        }

        public boolean hasEnough(double amount) {
            try {
                return Economy.hasEnough(name, amount);
            } catch (Exception ex) {
                System.out.println("[REGISTER] Error in Essentials Economy: " + ex.getMessage());
            }

            return false;
        }

        public boolean hasOver(double amount) {
            try {
                return Economy.hasMore(name, amount);
            } catch (Exception ex) {
                System.out.println("[REGISTER] Error in Essentials Economy: " + ex.getMessage());
            }

            return false;
        }

        public boolean hasUnder(double amount) {
            try {
                return Economy.hasLess(name, amount);
            } catch (Exception ex) {
                System.out.println("[REGISTER] Error in Essentials Economy: " + ex.getMessage());
            }

            return false;
        }

        public boolean isNegative() {
            try {
                return Economy.isNegative(name);
            } catch (Exception ex) {
                System.out.println("[REGISTER] Error in Essentials Economy: " + ex.getMessage());
            }

            return false;
        }

        public boolean remove() {
            return false;
        }
    }
}
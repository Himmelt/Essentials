package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.ChatColor;

import static com.earth2me.essentials.I18n.tl;

public class SignWeather extends EssentialsSign {

    public SignWeather() {
        super("Weather");
    }

    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        return onSignCreate(sign, player, username, (com.earth2me.essentials.IEssentials) ess);
    }

    protected boolean onSignCreate(final ISign sign, final User player, final String username, final com.earth2me.essentials.IEssentials ess) throws SignException {
        validateTrade(sign, 2, ess);
        final String timeString = sign.getLine(1);
        if ("Sun".equalsIgnoreCase(timeString)) {
            sign.setLine(1, ChatColor.DARK_GREEN + "Sun");
            return true;
        }
        if ("Storm".equalsIgnoreCase(timeString)) {
            sign.setLine(1, ChatColor.DARK_GREEN + "Storm");
            return true;
        }
        sign.setLine(1, ChatColor.RED + "<sun|storm>");
        throw new SignException(tl("onlySunStorm"));
    }

    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        return onSignInteract(sign, player, username, (com.earth2me.essentials.IEssentials) ess);
    }

    protected boolean onSignInteract(final ISign sign, final User player, final String username, final com.earth2me.essentials.IEssentials ess) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 2, ess);
        charge.isAffordableFor(player);
        final String weatherString = sign.getLine(1);
        if ((ChatColor.DARK_GREEN + "Sun").equalsIgnoreCase(weatherString)) {
            player.getWorld().setStorm(false);
            charge.charge(player);
            Trade.log("Sign", "WeatherSun", "Interact", username, null, username, charge, sign.getBlock().getLocation(), ess);
            return true;
        }
        if ((ChatColor.DARK_GREEN + "Storm").equalsIgnoreCase(weatherString)) {
            player.getWorld().setStorm(true);
            charge.charge(player);
            Trade.log("Sign", "WeatherStorm", "Interact", username, null, username, charge, sign.getBlock().getLocation(), ess);
            return true;
        }
        throw new SignException(tl("onlySunStorm"));
    }

}

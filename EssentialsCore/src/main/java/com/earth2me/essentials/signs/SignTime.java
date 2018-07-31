package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.ChatColor;

import static com.earth2me.essentials.I18n.tl;

public class SignTime extends EssentialsSign {

    public SignTime() {
        super("Time");
    }

    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        return onSignCreate(sign, player, username, (com.earth2me.essentials.IEssentials) ess);
    }

    protected boolean onSignCreate(final ISign sign, final User player, final String username, final com.earth2me.essentials.IEssentials ess) throws SignException {
        validateTrade(sign, 2, ess);
        final String timeString = sign.getLine(1);
        if ("Day".equalsIgnoreCase(timeString)) {
            sign.setLine(1, ChatColor.DARK_GREEN + "Day");
            return true;
        }
        if ("Night".equalsIgnoreCase(timeString)) {
            sign.setLine(1, ChatColor.DARK_GREEN + "Night");
            return true;
        }
        throw new SignException(tl("onlyDayNight"));
    }

    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        return onSignInteract(sign, player, username, (com.earth2me.essentials.IEssentials) ess);
    }

    protected boolean onSignInteract(final ISign sign, final User player, final String username, final com.earth2me.essentials.IEssentials ess) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 2, ess);
        charge.isAffordableFor(player);
        final String timeString = sign.getLine(1);
        long time = player.getWorld().getTime();
        time -= time % 24000;
        if ((ChatColor.DARK_GREEN + "Day").equalsIgnoreCase(timeString)) {
            player.getWorld().setTime(time + 24000);
            charge.charge(player);
            Trade.log("Sign", "TimeDay", "Interact", username, null, username, charge, sign.getBlock().getLocation(), ess);
            return true;
        }
        if ((ChatColor.DARK_GREEN + "Night").equalsIgnoreCase(timeString)) {
            player.getWorld().setTime(time + 37700);
            charge.charge(player);
            Trade.log("Sign", "TimeNight", "Interact", username, null, username, charge, sign.getBlock().getLocation(), ess);
            return true;
        }
        throw new SignException(tl("onlyDayNight"));
    }

}

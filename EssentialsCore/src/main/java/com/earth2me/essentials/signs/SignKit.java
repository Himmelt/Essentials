package com.earth2me.essentials.signs;

import com.earth2me.essentials.*;
import com.earth2me.essentials.commands.NoChargeException;
import org.bukkit.ChatColor;

import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;


public class SignKit extends EssentialsSign {
    public SignKit() {
        super("Kit");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final com.earth2me.essentials.IEssentials ess) throws SignException {
        validateTrade(sign, 3, ess);

        final String kitName = sign.getLine(1).toLowerCase(Locale.ENGLISH).trim();

        if (kitName.isEmpty()) {
            sign.setLine(1, ChatColor.LIGHT_PURPLE + "Kit name!");
            return false;
        } else {
            try {
                ess.getSettings().getKit(kitName);
            } catch (Exception ex) {
                throw new SignException(ex.getMessage(), ex);
            }
            final String group = sign.getLine(2);
            if ("Everyone".equalsIgnoreCase(group) || "Everybody".equalsIgnoreCase(group)) {
                sign.setLine(2, ChatColor.DARK_GREEN + "Everyone");
            }
            return true;
        }
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        final String kitName = sign.getLine(1).toLowerCase(Locale.ENGLISH).trim();
        final String group = sign.getLine(2).trim();
        if ((!group.isEmpty() && ((ChatColor.DARK_GREEN + "Everyone").equals(group) || player.inGroup(group)))
                || (group.isEmpty() && (player.isAuthorized("essentials.kits." + kitName)))) {
            final Trade charge = getTrade(sign, 3, ess);
            charge.isAffordableFor(player);
            try {
                final Kit kit = new Kit(kitName, ess);
                kit.checkDelay(player);
                kit.setTime(player);
                kit.expandItems(player);

                charge.charge(player);
                Trade.log("Sign", "Kit", "Interact", username, null, username, charge, sign.getBlock().getLocation(), ess);
            } catch (NoChargeException ex) {
                return false;
            } catch (Exception ex) {
                throw new SignException(ex.getMessage(), ex);
            }
            return true;
        } else {
            if (group.isEmpty()) {
                throw new SignException(tl("noKitPermission", "essentials.kits." + kitName));
            } else {
                throw new SignException(tl("noKitGroup", group));
            }
        }
    }
}

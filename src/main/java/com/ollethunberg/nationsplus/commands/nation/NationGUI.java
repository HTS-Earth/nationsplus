package com.ollethunberg.nationsplus.commands.nation;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.ollethunberg.nationsplus.lib.GUIManager;
import com.ollethunberg.nationsplus.lib.MoneyFormat;
import com.ollethunberg.nationsplus.lib.models.Nation;

public class NationGUI extends GUIManager implements Listener {
    public NationGUI() {
        GUITitles.put("nations", "§6Nations");
    }

    public void openNationUI(Nation nation, Player player) {
        Inventory inventory = player.getServer().createInventory(player, rowsToSize(2), GUITitles.get("nations"));
        ItemStack item = createGuiItem(Material.DIAMOND, "§r§a" + nation.name, "§6" + nation.name,
                "§7Prefix: §r§l§a" + nation.prefix,
                "§7Balance: §r§l§a" + MoneyFormat.dollarFormat.format(nation.balance),
                "§7Income tax: §r§l§a" + nation.income_tax + "%",
                "§7Market tax: §r§l§a" + nation.market_tax + "%",
                "§7VAT: §r§l§a" + nation.vat_tax + "%",
                "§7Money transfer tax: §r§l§a" + nation.transfer_tax + "%",
                "§7King: §r§l§a" + nation.king_name,
                "§7Successor: §r§l§a" + nation.successor_name,
                "§7Members: §r§l§a" + nation.membersCount,
                "§7Kills: §r§l§a" + nation.kills

        );
        inventory.addItem(item);
        player.openInventory(inventory);
    }

    public void openNationsGUI(List<Nation> nations, Player player) {

        Inventory inventory = player.getServer().createInventory(player, rowsToSize(2), GUITitles.get("nations"));
        // for loop with index
        for (int i = 0; i < nations.size(); i++) {
            Nation nation = nations.get(i);
            // create item
            // add item to inventory
            ItemStack item = createGuiItem(Material.DIAMOND, nation.name, "§6" + nation.name,
                    "§7Prefix: §r§l§a" + nation.prefix,
                    "§7Balance: §r§l§a" + MoneyFormat.dollarFormat.format(nation.balance),
                    "§7Income tax: §r§l§a" + nation.income_tax + "%",
                    "§7Market tax: §r§l§a" + nation.market_tax + "%",
                    "§7VAT: §r§l§a" + nation.vat_tax + "%",
                    "§7Money transfer tax: §r§l§a" + nation.transfer_tax + "%",
                    "§7King: §r§l§a" + nation.king_name,
                    "§7Members: §r§l§a" + nation.membersCount,
                    "§7Kills: §r§l§a" + nation.kills

            );
            inventory.setItem(i, item);
        }
        player.openInventory(inventory);
    }

    @EventHandler()
    public void onInventoryClick(final InventoryClickEvent e) {
        String title = e.getView().getTitle();
        // get the player
        Player player = (Player) e.getWhoClicked();
        try {
            if (title.equals(GUITitles.get("nations"))) {
                e.setCancelled(true);
                final ItemStack clickedItem = e.getCurrentItem();
                // verify current item is not null
                if (clickedItem == null || clickedItem.getType() == Material.AIR)
                    return;

                // get the identifier of the item, last line of lore
                String identifier = this.getIdentifier(clickedItem);
                if (!identifier.startsWith("nation_"))
                    return;
            }
        } catch (Error error) {
            handleError(player, error);
        }
    }

}

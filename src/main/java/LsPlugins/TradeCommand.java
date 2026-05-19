package LsPlugins;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeCommand implements CommandExecutor {
    private final Map<UUID, UUID> pendingRequests = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("lsplugins.admin")) {
                    sender.sendMessage("§cSem permissão.");
                    return true;
                }
                LsSafeTrade.getInstance().reloadConfig();
                sender.sendMessage("§b[LsSafeTrade] §aConfigurações recarregadas.");
                return true;
            }

            if (args[0].equalsIgnoreCase("forcecancel")) {
                if (!sender.hasPermission("lsplugins.admin")) {
                    sender.sendMessage("§cSem permissão.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cUse: /safetrade forcecancel <player>");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    Tracker.forceClear(target.getUniqueId());
                    sender.sendMessage("§b[LsSafeTrade] §aTroca de " + target.getName() + " cancelada e jogador destravado.");
                } else {
                    sender.sendMessage("§cJogador offline.");
                }
                return true;
            }
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            p.sendMessage("§b[LsSafeTrade] §7Uso: /safetrade <jogador|aceitar>");
            return true;
        }

        if (args[0].equalsIgnoreCase("aceitar")) {
            UUID requesterUUID = pendingRequests.remove(p.getUniqueId());
            if (requesterUUID == null) {
                p.sendMessage("§b[SafeTrade] §cVocê não tem convites pendentes.");
                return true;
            }

            Player target = Bukkit.getPlayer(requesterUUID);
            if (target == null || !target.isOnline()) {
                p.sendMessage("§b[SafeTrade] §cO jogador que enviou o convite saiu.");
                return true;
            }

            if (Tracker.getActiveTrade(p) != null || Tracker.getActiveTrade(target) != null) {
                p.sendMessage("§b[SafeTrade] §cUm dos jogadores já está em uma troca.");
                return true;
            }

            Trade trade = new Trade(target, p);
            Tracker.addActiveTrade(trade);
            trade.open(target);
            trade.open(p);

            target.sendMessage("§b[SafeTrade] §a" + p.getName() + " aceitou seu convite de troca.");
            p.sendMessage("§b[SafeTrade] §aIniciando troca com " + target.getName() + ".");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            p.sendMessage("§b[SafeTrade] §cJogador não encontrado ou offline.");
            return true;
        }

        if (target.equals(p)) {
            p.sendMessage("§b[SafeTrade] §cVocê não pode trocar consigo mesmo.");
            return true;
        }

        if (Tracker.getActiveTrade(p) != null || Tracker.getActiveTrade(target) != null) {
            p.sendMessage("§b[SafeTrade] §cUm dos jogadores já está em uma troca.");
            return true;
        }

        pendingRequests.put(target.getUniqueId(), p.getUniqueId());
        p.sendMessage("§b[SafeTrade] §aConvite enviado para " + target.getName() + ".");
        target.sendMessage("§b[SafeTrade] §a" + p.getName() + " deseja trocar com você. Use §e/safetrade aceitar§a.");

        return true;
    }
}

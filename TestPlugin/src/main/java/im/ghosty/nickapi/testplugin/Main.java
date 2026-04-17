package im.ghosty.nickapi.testplugin;

import im.ghosty.nickapi.NickAPI;
import org.bukkit.entity.Player;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	@Override
	public void onEnable() {
		super.onEnable();
		NickAPI.setupConfig(new File(getDataFolder(), "config.yml"));
		NickAPI.setPlugin(this);
		
		getCommand("nick").setExecutor((sender, cmd, label, args) -> {
			if (args.length == 0) {
				NickAPI.resetNick((Player) sender);
				NickAPI.resetSkin((Player) sender);
				NickAPI.resetGameProfileName((Player) sender);
				NickAPI.resetUniqueId((Player) sender);
				NickAPI.refreshPlayer((Player) sender);
				sender.sendMessage("§cRemoved nick.");
				return true;
			}
			String skin = args[0];
			Player target = (Player) sender;
			if (args.length >= 2) {
				target = getServer().getPlayer(args[1]);
			}
			NickAPI.nick(target, skin);
			NickAPI.setSkin(target, skin);
			NickAPI.setGameProfileName(target, skin);
			NickAPI.setUniqueId(target, skin);
			NickAPI.refreshPlayer(target);
			sender.sendMessage("§aNicked §6" + target.getName() + " §aas §6" + skin);
			return true;
		});
		
		getCommand("nickinfo").setExecutor((sender, cmd, label, args) -> {
			Player target = (Player) sender;
			if (args.length >= 1) {
				target = getServer().getPlayer(args[0]);
			}
			sender.sendMessage(
				"§2Name: §f" + NickAPI.getName(target),
				"§aOrig Name: §f" + NickAPI.getOriginalName(target),
				"§eOrig GP Name: §f" + NickAPI.getOriginalGameProfileName(target),
				"§cUUID: §f" + NickAPI.getUniqueId(target)
			);
			return true;
		});
	}
	
}

package xyz.haoshoku.nick.handler;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.haoshoku.nick.NickAPI;
import xyz.haoshoku.nick.impl.AImplement;
import xyz.haoshoku.nick.packetlistener.IInject;
import xyz.haoshoku.nick.user.NickHandler;
import xyz.haoshoku.nick.user.NickUser;

public class Handler {
	
	@Getter
	private final String version;
	private IInject iInject;
	private AImplement aImplement;
	
	public Handler() {
		this.version = MCVersion.find().name();
		try {
			this.initializeVersion();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void initializeVersion() throws Exception {
		this.iInject = (IInject) Class.forName("xyz.haoshoku.nick.versions." + this.version + ".Injector").getConstructor(new Class[0]).newInstance(new Object[0]);
		this.aImplement = (AImplement) Class.forName("xyz.haoshoku.nick.versions." + this.version + ".Implement").getConstructor(new Class[0]).newInstance(new Object[0]);
	}
	
	public void executeReloadOnEnable() {
		if (!Bukkit.getOnlinePlayers().isEmpty()) {
			for (final Player player : Bukkit.getOnlinePlayers()) {
				final NickUser user = NickHandler.createUser(player.getUniqueId());
				user.setLogin(true);
				user.setPlayer(player);
				user.setNickAble(true);
				this.iInject.applyGameProfile(player);
				this.iInject.inject(player);
				this.aImplement.sendPackets(player, true, true);
			}
		}
	}
	
	public void executeReloadOnDisable() {
		try {
			try {
				Bukkit.getScheduler().getClass().getMethod("cancelAllTasks").invoke(Bukkit.getScheduler());
			} catch (final Exception ignored) {
			}
		} catch (final NoSuchMethodError ignored) {
		}
		try {
			Bukkit.getScheduler().cancelTasks(NickAPI.getPlugin());
		} catch (final NoSuchMethodError ignored) {
		}
		for (final Player player : Bukkit.getOnlinePlayers()) {
			NickAPI.resetNick(player);
			NickAPI.resetSkin(player);
			NickAPI.resetGameProfileName(player);
			NickAPI.resetUniqueId(player);
			this.aImplement.refreshPlayerSync(player, false);
			this.iInject.uninject(player);
		}
	}
	
	public IInject getIInject() {
		return this.iInject;
	}
	
	public AImplement getAImplement() {
		return this.aImplement;
	}
	
}

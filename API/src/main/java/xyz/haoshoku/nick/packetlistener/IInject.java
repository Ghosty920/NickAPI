package xyz.haoshoku.nick.packetlistener;

import org.bukkit.entity.Player;
import xyz.haoshoku.nick.NickAPI;

public interface IInject {
	
	void inject(final Player player);
	
	void uninject(final Player player);
	
	void applyGameProfile(final Player player);
	
	static String pipelineName() {
		return "nickapi_"+NickAPI.getPlugin().getName().toLowerCase();
	}
	
}

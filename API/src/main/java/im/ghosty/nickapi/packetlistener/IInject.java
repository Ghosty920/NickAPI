package im.ghosty.nickapi.packetlistener;

import org.bukkit.entity.Player;
import im.ghosty.nickapi.NickAPI;

public interface IInject {
	
	void inject(final Player player);
	
	void uninject(final Player player);
	
	void applyGameProfile(final Player player);
	
	static String pipelineName() {
		return "nickapi_"+NickAPI.getPlugin().getName().toLowerCase();
	}
	
}

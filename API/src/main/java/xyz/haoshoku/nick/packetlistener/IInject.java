package xyz.haoshoku.nick.packetlistener;

import org.bukkit.entity.*;

public interface IInject
{
	void inject(final Player p0);
	
	void uninject(final Player p0);
	
	void applyGameProfile(final Player p0);
}

package im.ghosty.nickapi.testplugin;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate;

public class PacketsListener implements PacketListener {
	
	@Override
	public void onPacketSend(PacketSendEvent event) {
		if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO_UPDATE) {
			WrapperPlayServerPlayerInfoUpdate packet = new WrapperPlayServerPlayerInfoUpdate(event);
		}
	}
	
}

package xyz.haoshoku.nick;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class NickScoreboard {
	
	private static final Map<String, Object[]> SCOREBOARD_MAP = new HashMap<>();
	
	public static void write(@NotNull final String nickedName, @NotNull final String teamName, @NotNull final String prefix, @NotNull final String suffix) {
		write(nickedName, teamName, prefix, suffix, true, ChatColor.BLUE);
	}
	
	public static void write(@NotNull final String nickedName, @NotNull final String teamName, @NotNull final String prefix, @NotNull final String suffix, final boolean newScoreboard, final ChatColor color) {
		NickScoreboard.SCOREBOARD_MAP.put(nickedName, new Object[]{teamName, prefix, suffix, newScoreboard, color});
	}
	
	public static void write(@NotNull final String nickedName, @NotNull final String teamName, @NotNull final String prefix, @NotNull final String suffix, final ChatColor color) {
		NickScoreboard.SCOREBOARD_MAP.put(nickedName, new Object[]{teamName, prefix, suffix, true, color});
	}
	
	public static void delete(final String nickedName) {
		NickScoreboard.SCOREBOARD_MAP.remove(nickedName);
	}
	
	public static void updateAllScoreboard() {
		for (final Map.Entry<String, Object[]> entry : NickScoreboard.SCOREBOARD_MAP.entrySet()) {
			final String teamName = (String) entry.getValue()[0];
			final String prefix = (String) entry.getValue()[1];
			final String suffix = (String) entry.getValue()[2];
			final boolean newScoreboard = (boolean) entry.getValue()[3];
			final ChatColor color = (ChatColor) entry.getValue()[4];
			for (final Player player : Bukkit.getOnlinePlayers()) {
				if (newScoreboard && player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard()) {
					player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
				final Scoreboard scoreboard = player.getScoreboard();
				final Team team = (scoreboard.getTeam(teamName) != null) ? scoreboard.getTeam(teamName) : scoreboard.registerNewTeam(teamName);
				team.setPrefix(prefix);
				team.setSuffix(suffix);
				try {
					if (color != null) {
						team.setColor(color);
					}
				} catch (final NoSuchMethodError noSuchMethodError) {
				}
				team.addEntry(entry.getKey());
			}
		}
	}
	
	public static void updateScoreboard(@NotNull final String nickedName) {
		if (!NickScoreboard.SCOREBOARD_MAP.containsKey(nickedName)) {
			return;
		}
		Bukkit.getScheduler().runTask(NickAPI.getPlugin(), () -> {
			final Object[] values = NickScoreboard.SCOREBOARD_MAP.get(nickedName);
			final String teamName = (String) values[0];
			final String prefix = (String) values[1];
			final String suffix = (String) values[2];
			final boolean newScoreboard = (boolean) values[3];
			final ChatColor color = (ChatColor) values[4];
			Bukkit.getOnlinePlayers().iterator();
			final Iterator iterator = null;
			while (iterator.hasNext()) {
				final Player player = (Player) iterator.next();
				if (newScoreboard && player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard()) {
					player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
				}
				final Scoreboard scoreboard = player.getScoreboard();
				final Team team = (scoreboard.getTeam(teamName) != null) ? scoreboard.getTeam(teamName) : scoreboard.registerNewTeam(teamName);
				team.setPrefix(prefix);
				team.setSuffix(suffix);
				try {
					team.setColor(color);
				} catch (final NoSuchMethodError noSuchMethodError) {
				}
				team.addEntry(nickedName);
			}
		});
	}
}

package me.becja10.CustomPollsRevote;

import java.util.logging.Logger;

import net.lightshard.custompolls.CustomPolls;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomPollsRevote extends JavaPlugin implements Listener{

	public final Logger logger = Logger.getLogger("Minecraft");
		
	public void onEnable(){
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has Been Enabled!");
		CustomPolls.getInstance().getCommandManager().setup(new PollCommandExtender());
	}
}

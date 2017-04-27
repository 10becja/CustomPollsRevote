package me.becja10.CustomPollsRevote;

import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import net.lightshard.custompolls.CustomPolls;

public class CustomPollsRevote extends JavaPlugin implements Listener{

	public final Logger logger = Logger.getLogger("Minecraft");
	
	private static CustomPolls cp;
	
	public void onEnable(){
		PluginDescriptionFile pdfFile = getDescription();
		this.logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " Has Been Enabled!");
		
		cp = CustomPolls.getInstance();
		
		cp.getCommandManager().setup(new PollCommandExtender());
	}

}

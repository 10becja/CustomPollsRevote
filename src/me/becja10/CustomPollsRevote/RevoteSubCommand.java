package me.becja10.CustomPollsRevote;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.lightshard.custompolls.CustomPolls;
import net.lightshard.custompolls.command.CommandProvider;
import net.lightshard.custompolls.command.SubCommand;
import net.lightshard.custompolls.persistence.database.Callback;
import net.lightshard.custompolls.persistence.file.F;
import net.lightshard.custompolls.persistence.file.config.MessageConfig;
import net.lightshard.custompolls.persistence.file.config.SettingsConfig;
import net.lightshard.custompolls.poll.Poll;
import net.lightshard.custompolls.poll.PollChoice;
import net.md_5.bungee.api.ChatColor;

public class RevoteSubCommand extends SubCommand{

	public RevoteSubCommand(CommandProvider provider) {
		super(provider);
	}

	public List<String> getAliases() {
		List<String> list = new ArrayList<String>();
		list.add(getShownAlias());
		return list;
	}

	public String getHelpMessage() {
		return MessageConfig.COMMAND_HELP_FORMAT.getString()
				.replace("%EXAMPLE%", "/" + getProvider().getAlias() + " " + getShownAlias() + " <poll> <choice>")
				.replace("%DESCRIPTION%", "Change your vote for the poll");
	}

	public String getShownAlias() {
		return "revote";
	}

	public boolean hasPermission(CommandSender sender) {
		return sender.hasPermission("poll.command.revote");
	}

	public boolean hasToBePlayer() {
		return true;
	}

	public void onCommand(CommandSender sender, String[] args) {
		if (args.length < 2)
		{
			getProvider().sendHelp(sender, this);
			return;
		}

		final Poll poll = CustomPolls.getInstance().getPollManager().getByName(args[0]);
		if (poll == null)
		{
			sender.sendMessage(MessageConfig.COMMAND_POLL_DOESNTEXIST.getString());
			return;
		}
		
		PollChoice choice = null;
		for (PollChoice choice_ : poll.getChoices())
		{
			if ((String.valueOf(choice_.getId()).equalsIgnoreCase(args[1])) || (choice_.getText().equalsIgnoreCase(args[1])))
			{

				choice = choice_;
				break;
			}
		}
		if (choice == null)
		{
			sender.sendMessage(MessageConfig.COMMAND_POLL_VOTE_CHOICENOTFOUND.getString());
			return;
		}

		final PollChoice finalChoice = choice;
		final Player player = (Player)sender;
		CustomPolls.getDb().hasVoted(poll, player.getUniqueId(), new Callback<Boolean>()
		{

			public void callback(Boolean result)
			{
				
				boolean hasVoted = result.booleanValue();
				if (!hasVoted)
				{
					player.sendMessage(ChatColor.RED + "You haven't voted for this poll yet. Use '/poll vote' instead");
					return;
				}

				for (String line : SettingsConfig.VOTE_VOTED_FORMAT.getStringList())
				{
					player.sendMessage(line.replace("%CHOICE%", finalChoice.getText()));
				}
				
				CustomPolls.getDb().getVote(poll, player.getUniqueId(), new Callback<Integer>(){
					
					public void callback(Integer voteId){
						
						PollChoice oldChoice = poll.getChoiceById(voteId);
						
						F pFile = new F(new File(CustomPolls.getInstance().getDataFolder() + "/Files/Votes/"), player.getUniqueId().toString());
						if (!pFile.exists())
					    {
							pFile.create();
					    }
						pFile.load();
						pFile.set(poll.getName().toLowerCase(), Integer.valueOf(finalChoice.getId()));
						pFile.save();
					    
					    oldChoice.setVotes(oldChoice.getVotes() - 1);
					    finalChoice.setVotes(finalChoice.getVotes() + 1);
					    
					    F file = new F(new File(CustomPolls.getInstance().getDataFolder() + "/Files/Database/"), poll.getName().toLowerCase());
				        file.load();
				        file.set("Choice." + String.valueOf(oldChoice.getId()) + ".votes", oldChoice.getVotes());
				        file.set("Choice." + String.valueOf(finalChoice.getId()) + ".votes", finalChoice.getVotes());
				        file.save();
					}
				});
				
				
			}
		});
	}
}

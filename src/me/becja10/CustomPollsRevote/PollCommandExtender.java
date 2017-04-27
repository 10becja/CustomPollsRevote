package me.becja10.CustomPollsRevote;

import net.lightshard.custompolls.command.poll.PollCommand;

public class PollCommandExtender extends PollCommand{

	public PollCommandExtender(){
		super();
		
		getSubCommands().add(new RevoteSubCommand(this));
	}
}

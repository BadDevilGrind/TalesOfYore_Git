package tales.of.yore.Commands;

import java.util.ArrayList;
import java.util.List;

import tales.of.yore.VampirePlayerExtender;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CheckInfo implements ICommand {

	
	
	
	private List aliases;
	public CheckInfo()
	{
		this.aliases = new ArrayList();
		this.aliases.add("vampCheck");
		this.aliases.add("vampC");
		this.aliases.add("vampireCheck");
	}
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "vampireCheck";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		// TODO Auto-generated method stub
		return "vampireCheck";
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring) {
		System.out.println("Vampire check command started");
		if(astring.length == 0)
		{
			EntityPlayer player;
			VampirePlayerExtender vampire;
	        
	        if(icommandsender instanceof EntityPlayer)
	        {
	                player = (EntityPlayer)icommandsender;
	                vampire = VampirePlayerExtender.get((EntityPlayer) icommandsender);
	                player.addChatMessage("Blood level is: "+ vampire.currentBlood+"/"+vampire.maxBlood);
	                player.addChatMessage("[DEBUG]: " + vampire.isVampire()); 
	                System.out.println("Vampire check command used.");
	        }
			return;
		}
		 
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender,
			String[] astring) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i) {
		// TODO Auto-generated method stub
		return false;
	}


}

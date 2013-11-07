package tales.of.yore;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;



public class VampirePacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager,
	                Packet250CustomPayload packet, Player player) {
	        
	        if (packet.channel.equals("VampireChannel")) {
	                handlePacket(packet, player);
	        }
	}
	
	private void handlePacket(Packet250CustomPayload packet, Player player) 
	{
        DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		VampirePlayerExtender VampPlayer = VampirePlayerExtender.get((EntityPlayer) player);

        boolean isPlayerVampire;
        int age;
        int power;
        
        try 
        {
            isPlayerVampire = inputStream.readBoolean();
            age = inputStream.readInt();
            power = inputStream.readInt();
        } catch (IOException e) {
                e.printStackTrace();
                return;
        }
        VampPlayer.isVampire = isPlayerVampire;
        VampPlayer.age = age;
        VampPlayer.setPower(power);
        //System.out.println("Packet value is: "+isPlayerVampire);
        //System.out.println("Pakcet age is: " +age);
	}

	
	
}

package buildcraft.additionalpipes.gui;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessageTelePipeData;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.pipes.TeleportManager;
import buildcraft.core.gui.BuildCraftContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class ContainerTeleportPipe extends BuildCraftContainer {

	public int connectedPipes = 0;

	private int ticks = 0;
	private PipeTeleport pipe;
	private int freq;
	private byte state;
	private boolean isPublic;

	public ContainerTeleportPipe(EntityPlayer player, PipeTeleport pipe) {
		super(0);
		this.pipe = pipe;

		state = -1;
		isPublic = !pipe.isPublic;
		freq = -1;
		
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			List<PipeTeleport> connectedPipes = TeleportManager.instance.getConnectedPipes(pipe, false);
			int[] locations = new int[connectedPipes.size() * 3];
			for(int i = 0; i < connectedPipes.size() && i < 9; i++) {
				PipeTeleport connectedPipe = connectedPipes.get(i);
				locations[3 * i] = connectedPipe.container.xCoord;
				locations[3 * i + 1] = connectedPipe.container.yCoord;
				locations[3 * i + 2] = connectedPipe.container.zCoord;
			}
			
			MessageTelePipeData message = new MessageTelePipeData(pipe.container.xCoord, pipe.container.yCoord, pipe.container.zCoord, locations, pipe.owner);
			PacketHandler.INSTANCE.sendTo(message, (EntityPlayerMP) player);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		int connectedPipesNew = connectedPipes;
		if(ticks % 40 == 0) { // reduce lag
			ticks = 0;
			AdditionalPipes.instance.logger.info("Old connected:" + connectedPipesNew);
			connectedPipesNew = TeleportManager.instance.getConnectedPipes(pipe, false).size();
			AdditionalPipes.instance.logger.info("New connected:" + connectedPipesNew);
		}
		ticks++;
		for(Object crafter : crafters) {
			if(freq != pipe.getFrequency()) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 0, pipe.getFrequency());
			}
			if(state != pipe.state) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 1, pipe.state);
			}
			if(connectedPipesNew != connectedPipes) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 2, connectedPipesNew);
			}
			if(isPublic != pipe.isPublic) {
				((ICrafting) crafter).sendProgressBarUpdate(this, 3, pipe.isPublic ? 1 : 0);
			}
		}
		state = pipe.state;
		freq = pipe.getFrequency();
		isPublic = pipe.isPublic;
		connectedPipes = connectedPipesNew;
	}

	@Override
	public void updateProgressBar(int i, int j) {
		switch(i) {
		case 0:
			pipe.setFrequency(j);
			break;
		case 1:
			pipe.state = (byte) j;
			break;
		case 2:
			connectedPipes = j;
			break;
		case 3:
			pipe.isPublic = (j == 1);
			break;
		}
	}

}

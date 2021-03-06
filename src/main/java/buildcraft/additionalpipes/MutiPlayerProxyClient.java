package buildcraft.additionalpipes;

import java.util.logging.Level;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.additionalpipes.keyboard.KeyInputEventHandler;
import buildcraft.additionalpipes.keyboard.Keybindings;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.TransportProxyClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MutiPlayerProxyClient extends MultiPlayerProxy
{

	@Override
	public void registerKeyHandler()
	{
		
		AdditionalPipes.instance.logger.info("Registering key handler(s)");

		Keybindings.lasers = new KeyBinding("key.lasers", AdditionalPipes.laserKeyCode, AdditionalPipes.NAME);
		ClientRegistry.registerKeyBinding(Keybindings.lasers);
		
		FMLCommonHandler.instance().bus().register(new KeyInputEventHandler());
	}

	@Override
	public void registerPipeRendering(Item res)
	{
		MinecraftForgeClient.registerItemRenderer(res, TransportProxyClient.pipeItemRenderer);
	}

	@Override
	public void createPipeSpecial(ItemPipe item, Class<? extends Pipe<?>> clas)
	{
		try
		{
			Pipe<?> dummyPipe = clas.getConstructor(Item.class).newInstance(item);
			if(dummyPipe != null)
			{
				item.setPipesIcons(dummyPipe.getIconProvider());
				// TODO look around
				item.setPipeIconIndex(dummyPipe.getIconIndex(ForgeDirection.VALID_DIRECTIONS[0]));
				// item.setTextureIndex(dummyPipe.getTextureIndexForItem());
			}
		} 
		catch(Exception e)
		{
			AdditionalPipes.instance.logger.log(Level.SEVERE, "MultiPlayerProxyClient.createPipeSpecial() failed with exception!");
			
			e.printStackTrace();
		}
	}
}

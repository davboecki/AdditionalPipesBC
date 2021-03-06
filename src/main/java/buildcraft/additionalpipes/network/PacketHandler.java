package buildcraft.additionalpipes.network;

import buildcraft.additionalpipes.AdditionalPipes;
import buildcraft.additionalpipes.network.message.MessageAdvWoodPipe;
import buildcraft.additionalpipes.network.message.MessageChunkloadData;
import buildcraft.additionalpipes.network.message.MessageChunkloadRequest;
import buildcraft.additionalpipes.network.message.MessageDistPipe;
import buildcraft.additionalpipes.network.message.MessageTelePipe;
import buildcraft.additionalpipes.network.message.MessageTelePipeData;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler
{
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(AdditionalPipes.MODID.toLowerCase());

    public static void init()
    {
        INSTANCE.registerMessage(MessageDistPipe.class, MessageDistPipe.class, 0, Side.SERVER);
        INSTANCE.registerMessage(MessageTelePipe.class, MessageTelePipe.class, 1, Side.SERVER);
        INSTANCE.registerMessage(MessageAdvWoodPipe.class, MessageAdvWoodPipe.class, 2, Side.SERVER);
        INSTANCE.registerMessage(MessageTelePipeData.class, MessageTelePipeData.class, 3, Side.CLIENT);
        INSTANCE.registerMessage(MessageChunkloadRequest.class, MessageChunkloadRequest.class, 4, Side.SERVER);
        INSTANCE.registerMessage(MessageChunkloadData.class, MessageChunkloadData.class, 5, Side.CLIENT);
    }
}

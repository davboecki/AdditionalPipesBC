package buildcraft.additionalpipes.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import buildcraft.BuildCraftCore;
import buildcraft.additionalpipes.network.PacketHandler;
import buildcraft.additionalpipes.network.message.MessageTelePipe;
import buildcraft.additionalpipes.pipes.PipeTeleport;
import buildcraft.additionalpipes.textures.Textures;
import buildcraft.core.CoreIconProvider;
import buildcraft.core.gui.GuiBuildCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTeleportPipe extends GuiBuildCraft {

	protected class TeleportPipeLedger extends Ledger {

		int headerColour = 0xe1c92f;
		int subheaderColour = 0xaaafb8;
		int textColour = 0x000000;

		public TeleportPipeLedger() {
			maxHeight = 99;
			overlayColor = 0xd46c1f;
		}

		@Override
		public void draw(int x, int y) {

			// Draw background
			drawBackground(x, y);

			// Draw icon
			Minecraft.getMinecraft().renderEngine.bindTexture(Textures.ITEMS);
			drawIcon(BuildCraftCore.iconProvider.getIcon(CoreIconProvider.ENERGY), x + 3, y + 4);

			if(!isFullyOpened())
				return;

			fontRendererObj.drawStringWithShadow("Teleport Pipe", x + 22, y + 8, headerColour);
			fontRendererObj.drawStringWithShadow("Owner:", x + 22, y + 20, subheaderColour);
			fontRendererObj.drawString(pipe.owner, x + 22, y + 32, textColour);
			fontRendererObj.drawStringWithShadow("Outputs: ", x + 22, y + 44, subheaderColour);
			fontRendererObj.drawString(String.valueOf(container.connectedPipes), x + 66, y + 45, textColour);
			int[] net = pipe.network;
			if(net.length > 0) {
				fontRendererObj.drawString(new StringBuilder("(").append(net[0]).append(", ").append(net[1]).append(", ").append(net[2]).append(")").toString(), x + 22, y + 56, textColour);
			}
			if(net.length > 3) {
				fontRendererObj.drawString(new StringBuilder("(").append(net[3]).append(", ").append(net[4]).append(", ").append(net[5]).append(")").toString(), x + 22, y + 68, textColour);
			}
			if(net.length > 6) {
				fontRendererObj.drawString(new StringBuilder("(").append(net[6]).append(", ").append(net[7]).append(", ").append(net[8]).append(")").toString(), x + 22, y + 80, textColour);
			}
		}

		@Override
		public String getTooltip() {
			return "Owner: " + pipe.owner;
		}
	}

	private final PipeTeleport pipe;
	private final ContainerTeleportPipe container;
	private final GuiButton[] buttons = new GuiButton[8];

	public GuiTeleportPipe(EntityPlayer player, PipeTeleport pipe) {
		super(new ContainerTeleportPipe(player, pipe), null, Textures.GUI_TELEPORT);
		this.pipe = pipe;
		container = (ContainerTeleportPipe) inventorySlots;
		xSize = 228;
		ySize = 117;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		int x = (width - xSize) / 2, y = (height - ySize) / 2 + 16;
		int bw = xSize - 24;
		buttonList.add(buttons[0] = new GuiButton(1, x + 12, y + 32, bw / 6, 20, "-100"));
		buttonList.add(buttons[1] = new GuiButton(2, x + 12 + bw / 6, y + 32, bw / 6, 20, "-10"));
		buttonList.add(buttons[2] = new GuiButton(3, x + 12 + bw * 2 / 6, y + 32, bw / 6, 20, "-1"));
		buttonList.add(buttons[3] = new GuiButton(4, x + 12 + bw * 3 / 6, y + 32, bw / 6, 20, "+1"));
		buttonList.add(buttons[4] = new GuiButton(5, x + 12 + bw * 4 / 6, y + 32, bw / 6, 20, "+10"));
		buttonList.add(buttons[5] = new GuiButton(6, x + 12 + bw * 5 / 6, y + 32, bw / 6, 20, "+100"));

		buttonList.add(buttons[6] = new GuiButton(7, x + 12, y + 10, bw / 2, 20, "Send Only"));
		buttonList.add(buttons[7] = new GuiButton(8, x + 12 + bw * 3 / 6, y + 10, bw / 2, 20, "Private"));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p1, int p2) {
		super.drawGuiContainerForegroundLayer(p1, p2);
		fontRendererObj.drawString("Frequency: " + pipe.getFrequency(), 16, 12, 0x404040);
		fontRendererObj.drawString(new StringBuilder("(")
			.append(pipe.container.xCoord).append(", ")
			.append(pipe.container.yCoord).append(", ")
			.append(pipe.container.zCoord).append(")").toString(), 128, 12, 0x404040);
		switch(pipe.state) {
		case 3:
			buttons[6].displayString = "Send & Receive";
			break;
		case 2:
			buttons[6].displayString = "Receive Only";
			break;
		case 1:
			buttons[6].displayString = "Send Only";
			break;
		default:
			buttons[6].displayString = "Disabled";
			break;
		}
		if(pipe.isPublic) {
			buttons[7].displayString = "Public";
		} else {
			buttons[7].displayString = "Private";
		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {
		int freq = pipe.getFrequency();
		byte state = pipe.state;
		boolean isPublic = pipe.isPublic;
		switch(guibutton.id) {
		case 1:
			freq -= 100;
			break;
		case 2:
			freq -= 10;
			break;
		case 3:
			freq -= 1;
			break;
		case 4:
			freq += 1;
			break;
		case 5:
			freq += 10;
			break;
		case 6:
			freq += 100;
			break;
		case 7:
			state = (byte) ((state + 1) % 4);
			break;
		case 8:
			isPublic = !isPublic;
			break;
		}
		if(freq < 0) {
			freq = 0;
		}

		MessageTelePipe packet = new MessageTelePipe(pipe.container.xCoord, pipe.container.yCoord, pipe.container.zCoord, freq, isPublic, state);
		PacketHandler.INSTANCE.sendToServer(packet);
	}

	@Override
	protected void initLedgers(IInventory inventory) {
		super.initLedgers(inventory);
		ledgerManager.add(new TeleportPipeLedger());
	}
}

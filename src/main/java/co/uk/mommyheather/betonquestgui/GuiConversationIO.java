package co.uk.mommyheather.betonquestgui;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class GuiConversationIO implements Listener, ConversationIO
{
    private final Map<Integer, String> options = new HashMap<>();
    private final Conversation conversation;
    private final Player player;
    private String response = null;
    private int optionsIndex = 0;
    private String npcName;
    private boolean allowClose = false;
    private boolean fallbackIO = false;
    private ConversationIO io;

    public GuiConversationIO(Conversation conversation, OnlineProfile profile)
    {
        this.conversation = conversation;
        this.player = profile.getPlayer();
        if (!BetonQuestGui.INSTANCE.players.getOrDefault(profile.getPlayer().getName(), false)) { 
            fallbackIO = true;
            final String rawConvIO = BetonQuest.getInstance().getPluginConfig().getString("default_conversation_IO", "menu,chest");
            String convIO = "";

            // check if all data is valid (or at least exist)
            for (final String s : rawConvIO.split(",")) {
                if (s.trim().equals("gui")) continue;
                if (BetonQuest.getInstance().getConvIO(s.trim()) != null) {
                    convIO = s.trim();
                    break;
                }
            }

            final Class<? extends ConversationIO> convIOC = BetonQuest.getInstance().getConvIO(convIO);
            try {
                io = convIOC.getConstructor(Conversation.class, OnlineProfile.class).newInstance(conversation, profile);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (!fallbackIO) {
            PacketHandler.sendPacketCreateGui(this.player);
        }
        Bukkit.getPluginManager().registerEvents(this, BetonQuestGui.INSTANCE.getBetonQuest());
    }

    @Override
    public void setNpcResponse(String npcName, String response)
    {
        if (fallbackIO) {
            io.setNpcResponse(npcName, response);
            return;
        }
        this.npcName = npcName;
        this.response = response.replace("%quester%", npcName).replace("%player%", this.player.getName()).replace('&', '§');
    }

    @Override
    public void addPlayerOption(String option)
    {
        if (fallbackIO) {
            io.addPlayerOption(option);
            return;
        }
        this.optionsIndex += 1;
        this.options.put(this.optionsIndex, option.replace("%quester%", this.npcName).replace("%player%", this.player.getName()).replace('&', '§'));
    }

    @Override
    public void display()
    {
        if (fallbackIO) {
            io.display();
            return;
        }
        if (this.response == null) {
            this.end();
            return;
        }
        if (this.options.isEmpty()) {
            this.end();
        }
        PacketHandler.sendPacketNpcDialog(this.player, this.npcName, this.response);
        for (int i = 0; i < this.options.size(); i++) {
            PacketHandler.sendPacketPlayerChoice(this.player, i, this.options.get(i + 1));
        }
    }

    @Override
    public void clear()
    {
        if (fallbackIO) {
            io.clear();
            return;
        }
        this.response = null;
        this.options.clear();
        this.optionsIndex = 0;
    }

    @Override
    public void end()
    {
        if (fallbackIO) {
            io.end();
        }
        this.allowClose = true;
        if (this.response == null) {
            PacketHandler.sendPacketCloseGui(this.player);
        } else if (this.options.isEmpty()) {
            PacketHandler.sendPacketAllowCloseGui(this.player);
        }
    }

    public void checkClose()
    {
        if (this.allowClose) {
            HandlerList.unregisterAll(this);
            return;
        }
        if (this.conversation.isMovementBlock()) {
            new BukkitRunnable()
            {
                public void run()
                {
                    GuiConversationIO.this.player.teleport(GuiConversationIO.this.conversation.getCenter());
                    PacketHandler.sendPacketOpenGui(GuiConversationIO.this.player);
                }
            }.runTask(BetonQuestGui.INSTANCE.getBetonQuest());
        } else {
            this.conversation.endConversation();
            HandlerList.unregisterAll(this);
        }
    }
}

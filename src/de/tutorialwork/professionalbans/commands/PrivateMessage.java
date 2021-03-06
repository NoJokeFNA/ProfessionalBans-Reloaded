package de.tutorialwork.professionalbans.commands;

import de.tutorialwork.professionalbans.main.Main;
import de.tutorialwork.professionalbans.utils.MessagesManager;
import de.tutorialwork.professionalbans.utils.UUIDFetcher;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PrivateMessage extends Command {
    public PrivateMessage(String msg) {
        super(msg);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(!Main.ban.isMuted(p.getUniqueId().toString())){
                if(args.length > 1){
                    ProxiedPlayer target = BungeeCord.getInstance().getPlayer(args[0]);
                    if(target != null){
                        String message = "";
                        for(int i = 1; i < args.length; i++){
                            message = message + " " + args[i];
                        }
                        if(!Main.ban.isMuted(target.getUniqueId().toString())){
                            if(!MSGToggle.toggle.contains(target)){
                                MessagesManager.sendMessage(p, target, message);
                            } else {
                                p.sendMessage(Main.data.Prefix+Main.messages.getString("msg_toggled"));
                            }
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("target_muted"));
                        }
                    } else {
                        if(MessagesManager.hasApp(UUIDFetcher.getUUID(args[0]))){
                            try{
                                File file = new File(Main.main.getDataFolder(), "config.yml");
                                Configuration cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

                                String message = "";
                                for(int i = 1; i < args.length; i++){
                                    message = message + " " + args[i];
                                }
                                p.sendMessage(ChatColor.translateAlternateColorCodes('&', cfg.getString("CHATFORMAT.MSG").replace("%from%", Main.messages.getString("you")).replace("%message%", message)));
                                MessagesManager.insertMessage(p.getUniqueId().toString(), UUIDFetcher.getUUID(args[0]), message);
                                if(MessagesManager.getFirebaseToken(UUIDFetcher.getUUID(args[0])) != null){
                                    MessagesManager.sendPushNotify(MessagesManager.getFirebaseToken(UUIDFetcher.getUUID(args[0])), Main.messages.getString("messages_from")+" "+p.getName(), message);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            p.sendMessage(Main.data.Prefix+Main.messages.getString("player_404"));
                        }
                    }
                } else {
                    p.sendMessage(Main.data.Prefix+"/msg <"+Main.messages.getString("player")+"> <"+Main.messages.getString("message")+">");
                }
            } else {
                File config = new File(Main.main.getDataFolder(), "config.yml");
                try{
                    Configuration configcfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(config);

                    String MSG = configcfg.getString("LAYOUT.TEMPMUTE");
                    MSG = MSG.replace("%grund%", Main.ban.getReasonString(p.getUniqueId().toString()));
                    MSG = MSG.replace("%dauer%", Main.ban.getEnd(p.getUniqueId().toString()));
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', MSG));
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        } else {
            BungeeCord.getInstance().getConsole().sendMessage(Main.data.Prefix+Main.messages.getString("only_player_cmd"));
        }
    }
}

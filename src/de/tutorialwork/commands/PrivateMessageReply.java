package de.tutorialwork.commands;

import de.tutorialwork.main.Main;
import de.tutorialwork.utils.MessagesManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PrivateMessageReply extends Command {
    public PrivateMessageReply(String r) {
        super(r);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(MessagesManager.getLastChatPlayer(p) != null){
                if(args.length > 0){
                    String message = "";
                    for(int i = 0; i < args.length; i++){
                        message = message + " " + args[i];
                    }
                    MessagesManager.sendMessage(p, MessagesManager.getLastChatPlayer(p), message);
                } else {
                    p.sendMessage(Main.Prefix+"/r <Nachricht> - §8§oAntwortet §e§l"+MessagesManager.getLastChatPlayer(p).getName());
                }
            } else {
                p.sendMessage(Main.Prefix+"§cDu kannst niemanden antworten");
            }
        } else {
            BungeeCord.getInstance().getConsole().sendMessage(Main.Prefix+"§cDieser Befehl kann nur als Spieler genutzt werden");
        }
    }
}
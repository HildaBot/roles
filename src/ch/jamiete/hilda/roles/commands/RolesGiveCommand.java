/*******************************************************************************
 * Copyright 2017 jamietech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.jamiete.hilda.roles.commands;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Util;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.roles.RolesPlugin;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Iterator;

public class RolesGiveCommand extends ChannelSubCommand {
    private final RolesPlugin plugin;

    public RolesGiveCommand(final Hilda hilda, final ChannelSeniorCommand senior, final RolesPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("give");
        this.setDescription("Gives or removes roles.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (!message.getGuild().getSelfMember().hasPermission(Permission.MANAGE_ROLES)) {
            this.reply(message, "I can't manage roles. Please contact an administrator of the server to fix this.");
            return;
        }

        if (arguments.length == 0) {
            this.usage(message, "<role>", label);
            return;
        }

        final Member member = message.getGuild().getMember(message.getAuthor());
        final Configuration cfg = this.plugin.getHilda().getConfigurationManager().getConfiguration(this.plugin, message.getGuild().getId());

        final JsonArray array = cfg.get().getAsJsonArray("roles");

        if (array == null) {
            return;
        }

        final Iterator<JsonElement> iterator = array.iterator();

        String sought = Util.combineSplit(0, arguments, " ").trim();
        Role role = null;

        while (iterator.hasNext()) {
            role = message.getGuild().getRoleById(iterator.next().getAsString());

            if (role == null) {
                continue;
            }

            if (role.getName().toLowerCase().equalsIgnoreCase(sought)) {
                break;
            }

            role = null;
        }

        if (role == null) {
            this.reply(message, "I couldn't find that role on my list.");
            return;
        }

        if (!message.getGuild().getSelfMember().canInteract(role)) {
            this.reply(message, "I can't do that. Please ask a server administrator to modify the order of the roles.");
            return;
        }

        if (member.getRoles().contains(role)) {
            message.getGuild().getController().removeRolesFromMember(member, role).reason("I took this role from the user because they asked me to. If you don't want them to have access to this role, please remove it from the roles list.").queue();
            this.reply(message, "OK " + message.getAuthor().getAsMention() + ", I've removed " + role.getName() + " from you!");
        } else {
            message.getGuild().getController().addRolesToMember(member, role).reason("I gave this role to the user because they asked me to. If you don't want them to have access to this role, please remove it from the roles list.").queue();
            this.reply(message, "OK " + message.getAuthor().getAsMention() + ", I've given " + role.getName() + " to you!");
        }
    }

}

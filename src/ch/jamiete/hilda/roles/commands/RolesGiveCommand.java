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

import java.util.Iterator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.roles.RolesPlugin;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

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
            this.usage(message, "<role>");
            return;
        }

        final Member member = message.getGuild().getMember(message.getAuthor());
        final Configuration cfg = this.plugin.getHilda().getConfigurationManager().getConfiguration(this.plugin, message.getGuild().getId());

        final JsonArray array = cfg.get().getAsJsonArray("roles");

        if (array == null) {
            return;
        }

        final Iterator<JsonElement> iterator = array.iterator();

        Role role = null;

        while (iterator.hasNext()) {
            role = message.getGuild().getRoleById(iterator.next().getAsString());

            if (role == null) {
                continue;
            }

            if (role.getName().toLowerCase().equalsIgnoreCase(arguments[0])) {
                break;
            }

            role = null;
        }

        if (role == null) {
            this.reply(message, "I couldn't find that role on my list.");
            return;
        }

        if (member.getRoles().contains(role)) {
            message.getGuild().getController().removeRolesFromMember(member, role).queue();
            this.reply(message, "I've removed " + role.getName() + " from you!");
        } else {
            message.getGuild().getController().addRolesToMember(member, role).queue();
            this.reply(message, "I've given " + role.getName() + " to you!");
        }
    }

}

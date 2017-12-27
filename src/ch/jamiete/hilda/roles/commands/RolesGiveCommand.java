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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

        final String sought = Util.combineSplit(0, arguments, " ").trim();
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

        String category = null;
        final List<Role> roles = message.getGuild().getRoles();

        for (int i = roles.indexOf(role); i > 0; i--) {
            final Role r = roles.get(i);
            final Matcher matcher = RolesPlugin.PATTERN.matcher(r.getName());

            if (matcher.matches()) {
                if (matcher.group(2) != null) {
                    category = matcher.group(1).trim();
                }

                break;
            }

        }

        final List<Role> add = new ArrayList<>();
        final List<Role> remove = new ArrayList<>();

        for (final Role r : add) {
            if (!message.getGuild().getSelfMember().canInteract(r)) {
                this.reply(message, "I can't do that. Please ask a server administrator to modify the order of the roles.");
                return;
            }
        }

        for (final Role r : remove) {
            if (!message.getGuild().getSelfMember().canInteract(r)) {
                this.reply(message, "I can't do that. Please ask a server administrator to modify the order of the roles.");
                return;
            }
        }

        if (member.getRoles().contains(role)) {
            remove.add(role);
        } else {
            add.add(role);
        }

        if (category != null) {
            remove.addAll(RolesPlugin.getRoles(message.getGuild(), category));

            if (!add.isEmpty()) {
                remove.remove(add.get(0));
            }
        }

        message.getGuild().getController().modifyMemberRoles(member, add, remove).reason("I performed this action because the user asked me to. If you don't want the user to have access to any role granted, please remove it or them from the permitted roles list.").queue();

        final StringBuilder sb = new StringBuilder();

        sb.append("OK, I've ");

        if (!add.isEmpty()) {
            sb.append("given ").append(add.get(0).getName()).append(" to");
        } else {
            sb.append("removed ").append(remove.get(0).getName()).append(" from");
        }

        sb.append(" you!");

        this.reply(message, sb.toString());
    }

}

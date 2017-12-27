/*
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
 */
package ch.jamiete.hilda.roles.commands;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Util;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.roles.RolesPlugin;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class RolesPermitAllCommand extends ChannelSubCommand {
    private final RolesPlugin plugin;

    public RolesPermitAllCommand(final Hilda hilda, final ChannelSeniorCommand senior, final RolesPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("permitall");
        this.setDescription("Adds or removes roles in a role category to the give list.");
        this.setMinimumPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (arguments.length == 0) {
            this.usage(message, "<role_category>");
            return;
        }

        final String name = Util.combineSplit(0, arguments, " ");
        final List<Role> roles = RolesPlugin.getRoles(message.getGuild(), name);

        if (roles.isEmpty()) {
            this.reply(message, "I couldn't find any roles matching that category.");
            return;
        }

        final Configuration cfg = this.plugin.getHilda().getConfigurationManager().getConfiguration(this.plugin, message.getGuild().getId());

        JsonArray array = cfg.get().getAsJsonArray("roles");

        if (array == null) {
            array = new JsonArray();
        }

        final List<String> added = new ArrayList<>();
        final List<String> ignored = new ArrayList<>();
        final List<String> issues = new ArrayList<>();

        for (final Role role : roles) {
            final String rname = role == null ? null : Util.sanitise(role.getName());

            if (!array.contains(new JsonPrimitive(role.getId()))) {
                array.add(role.getId());

                if (role != null) {
                    added.add(rname);

                    if (!message.getGuild().getSelfMember().canInteract(role)) {
                        issues.add(rname);
                    }
                }
            } else if (role != null) {
                ignored.add(rname);
            }
        }

        cfg.get().add("roles", array);
        cfg.save();

        final MessageBuilder mb = new MessageBuilder();
        mb.append("OK, ");

        if (added.isEmpty()) {
            mb.append("I couldn't find any roles to permit.");
        } else {
            mb.append("I permitted ").append(Util.getAsList(added)).append(".");
        }

        if (!ignored.isEmpty()) {
            mb.append(" ");
            mb.append(Util.getAsList(ignored)).append(" ");
            mb.append(ignored.size() == 1 ? "was" : "were").append(" already permitted so I left them alone.");
        }

        if (!issues.isEmpty()) {
            mb.append(" You asked me to permit ").append(Util.getAsList(issues));
            mb.append(" but I can't interact with ").append(issues.size() == 1 ? "that role" : "those roles");
            mb.append(". You will need to move my role above ");
            mb.append(issues.size() == 1 ? "that role" : "those roles").append("for me to be able to give ");
            mb.append(issues.size() == 1 ? "it" : "them").append("to users. Alternatively, you may give me administrator permissions.");
        }

        mb.buildAll().forEach(m -> this.reply(message, m));
    }

}

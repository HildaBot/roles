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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Util;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.commands.CommandManager;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.roles.RolesPlugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

public class RolesListCommand extends ChannelSubCommand {
    private final RolesPlugin plugin;

    public RolesListCommand(final Hilda hilda, final ChannelSeniorCommand senior, final RolesPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("list");
        this.setDescription("Lists roles that I can give users.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        final Configuration cfg = this.plugin.getHilda().getConfigurationManager().getConfiguration(this.plugin, message.getGuild().getId());

        final JsonArray array = cfg.get().getAsJsonArray("roles");

        if (array == null || array.size() < 1) {
            this.reply(message, "I can't give any roles!");
            return;
        }

        final EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Server roles");
        eb.setColor(Color.decode("#0A564D"));

        eb.setDescription("Say **" + CommandManager.PREFIX + "giveme <role>** to get (or remove) any of these roles:\n\n");

        String name = null;
        final List<String> roles = new ArrayList<>();

        for (final Role role : message.getGuild().getRoles()) {
            final Matcher matcher = RolesPlugin.PATTERN.matcher(role.getName());

            if (matcher.matches()) {
                if (!roles.isEmpty()) {
                    eb.addField(name == null ? "Roles" : name, Util.getAsList(roles), false);
                    roles.clear();
                }

                name = matcher.group(1).trim();
            }

            if (array.contains(new JsonPrimitive(role.getId()))) {
                roles.add(Util.sanitise(role.getName()));
            }
        }

        if (!roles.isEmpty()) {
            if (name == null) {
                eb.getDescriptionBuilder().append(Util.getAsList(roles));
            } else {
                eb.addField(name, Util.getAsList(roles), false);
            }
        }

        this.reply(message, eb.build());
    }

}

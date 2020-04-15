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

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Util;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.roles.RolesPlugin;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

public class RolesPermitCommand extends ChannelSubCommand {
    private final RolesPlugin plugin;

    public RolesPermitCommand(final Hilda hilda, final ChannelSeniorCommand senior, final RolesPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("permit");
        this.setDescription("Adds or removes roles to the give list.");
        this.setMinimumPermission(Permission.ADMINISTRATOR);
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (arguments.length == 0) {
            this.usage(message, "<role>");
            return;
        }

        final Role role = message.getGuild().getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(Util.combineSplit(0, arguments, " "))).findFirst().orElse(null);
        final Configuration cfg = this.plugin.getHilda().getConfigurationManager().getConfiguration(this.plugin, message.getGuild().getId());

        if (role == null) {
            this.reply(message, "I couldn't find that role.");
            return;
        }

        JsonArray array = cfg.get().getAsJsonArray("roles");

        if (array == null) {
            array = new JsonArray();
        }

        if (!array.contains(new JsonPrimitive(role.getId()))) {
            array.add(role.getId());

            final StringBuilder sb = new StringBuilder();
            sb.append("Added ").append(role.getName()).append(" to role list!");

            if (!message.getGuild().getSelfMember().canInteract(role)) {
                sb.append(" Note, however, that I can't interact with that role. You will need to move my role above it for me to be able to give it to users. Alternatively, you may give me administrator permissions.");
            }

            this.reply(message, sb.toString());
        } else {
            array.remove(new JsonPrimitive(role.getId()));
            this.reply(message, "Removed " + role.getName() + " from role list!");
        }

        cfg.get().add("roles", array);
        cfg.save();
    }

}

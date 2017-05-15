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
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

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

        final Role role = message.getGuild().getRoles().stream().filter(r -> r.getName().equalsIgnoreCase(arguments[0])).findFirst().orElse(null);
        final Configuration cfg = this.plugin.getHilda().getConfigurationManager().getConfiguration(this.plugin, message.getGuild().getId());

        JsonArray array = cfg.get().getAsJsonArray("roles");

        if (array == null) {
            array = new JsonArray();
        }

        final Iterator<JsonElement> iterator = array.iterator();

        Role find = null;
        JsonElement element = null;

        while (iterator.hasNext()) {
            element = iterator.next();
            find = message.getGuild().getRoleById(element.getAsString());

            if (find == null) {
                continue;
            }

            if (find == role) {
                break;
            }

            find = null;
        }

        if (find == null) {
            array.add(role.getId());
            this.reply(message, "Added " + role.getName() + " to role list!");
        } else {
            array.remove(element);
            this.reply(message, "Removed " + role.getName() + " from role list!");
        }

        cfg.get().add("roles", array);
        cfg.save();
    }

}

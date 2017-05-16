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
import ch.jamiete.hilda.commands.CommandManager;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.roles.RolesPlugin;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.MessageBuilder.Formatting;
import net.dv8tion.jda.core.MessageBuilder.SplitPolicy;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

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

        if (array == null) {
            this.reply(message, "I can't give any roles!");
            return;
        }

        final MessageBuilder mb = new MessageBuilder();
        final Iterator<JsonElement> iterator = array.iterator();

        mb.append("Roles", Formatting.UNDERLINE).append("\n");
        mb.append("Use ").append(CommandManager.PREFIX + "giveme <role>", Formatting.BOLD).append(" to get any of these roles:\n");

        while (iterator.hasNext()) {
            final Role role = message.getGuild().getRoleById(iterator.next().getAsString());

            if (role != null) {
                mb.append('\n');
                mb.append(role.getName());
            }
        }

        mb.buildAll(SplitPolicy.NEWLINE).forEach(m -> message.getTextChannel().sendMessage(m).queue());
    }

}

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
import java.util.Arrays;
import java.util.List;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.roles.RolesPlugin;
import net.dv8tion.jda.core.entities.Message;

public class RolesBaseCommand extends ChannelSeniorCommand {

    public RolesBaseCommand(final Hilda hilda, final RolesPlugin plugin) {
        super(hilda);

        this.setName("roles");
        this.setAliases(Arrays.asList(new String[] { "giverole", "getrole", "giveme", "iam" }));
        this.setDescription("Manages the giving of roles.");

        this.registerSubcommand(new RolesGiveCommand(hilda, this, plugin));
        this.registerSubcommand(new RolesListCommand(hilda, this, plugin));
        this.registerSubcommand(new RolesPermitCommand(hilda, this, plugin));
    }

    @Override
    public void execute(final Message message, String[] arguments, String label) {
        if (this.hasAlias(label)) {
            final List<String> newargs = new ArrayList<String>();
            newargs.add("give");
            newargs.addAll(Arrays.asList(arguments));

            label = "roles";
            arguments = newargs.toArray(new String[newargs.size()]);
        }

        super.execute(message, arguments, label);
    }

}

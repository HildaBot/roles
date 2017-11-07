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
package ch.jamiete.hilda.roles;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.plugins.HildaPlugin;
import ch.jamiete.hilda.roles.commands.RolesBaseCommand;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RolesPlugin extends HildaPlugin {
    public static final Pattern PATTERN = Pattern.compile("===\\[(.*)\\]===([Ee])?");

    public RolesPlugin(final Hilda hilda) {
        super(hilda);
    }

    @Override
    public void onEnable() {
        this.getHilda().getCommandManager().registerChannelCommand(new RolesBaseCommand(this.getHilda(), this));
    }

    /**
     * Gets a list of {@link Role}s that match the category name.
     * @param guild The Guild against whom to test roles.
     * @param category The category which is sought.
     * @return A (possibly empty) list of Roles that match the category name.
     */
    public static List<Role> getRoles(Guild guild, String category) {
        List<Role> roles = new ArrayList<>();
        boolean adding = false;

        for (Role role : guild.getRoles()) {
            Matcher matcher = PATTERN.matcher(role.getName());

            if (matcher.matches()) {
                adding = matcher.group(1).trim().equalsIgnoreCase(category);
                continue;
            }

            if (adding) {
                roles.add(role);
            }
        }

        return roles;
    }

}

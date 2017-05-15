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

public class RolesPlugin extends HildaPlugin {

    public RolesPlugin(final Hilda hilda) {
        super(hilda);
    }

    @Override
    public void onEnable() {
        this.getHilda().getCommandManager().registerChannelCommand(new RolesBaseCommand(this.getHilda(), this));
    }

}

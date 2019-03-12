/*
 * Copyright (C) 2019 Dylan Vicchiarelli
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.florence.model.update;

import com.florence.model.Entity;
import com.florence.model.EntityRegistry;
import java.util.HashMap;
import java.util.Iterator;

public class UpdateServiceListener<T extends Entity> {

    private EntityRegistry<T> registry;
    private HashMap<Class<? extends UpdateService<T>>, UpdateService<T>> services = new HashMap<>();

    public UpdateServiceListener(EntityRegistry<T> registry) {
        this.registry = registry;
    }

    public void register(Class<? extends UpdateService<T>> namespace, UpdateService<T> service) {
        services.put(namespace, service);
    }

    public void remove(Class<? extends UpdateService<T>> namespace) {
        services.remove(namespace);
    }

    public void execute(Class<? extends UpdateService<T>> namespace) {
        final UpdateService service = services.get(namespace);
        final Iterator<T> iterator = registry.iterator();
        while (iterator.hasNext()) {
            final T entity = iterator.next();
            service.update(entity);
        }
    }

    public EntityRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(EntityRegistry registry) {
        this.registry = registry;
    }

    public HashMap<Class<? extends UpdateService<T>>, UpdateService<T>> getServices() {
        return services;
    }

    public void setServices(HashMap<Class<? extends UpdateService<T>>, UpdateService<T>> services) {
        this.services = services;
    }
}

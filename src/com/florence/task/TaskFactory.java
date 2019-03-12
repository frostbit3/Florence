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
package com.florence.task;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Iterator;

public class TaskFactory {

    /**
     * A re-sizable array that is used to store the tasks that are currently
     * active in the system.
     */
    private final ArrayList<Task> tasks = new ArrayList<>();

    public void cancel(Object source) {

        /**
         * Cancels all tasks that have been submitted by a specified source.
         */
        tasks.stream().filter((task) -> !(task == null)).filter((task)
                -> (task.getSource().equals(source))).forEachOrdered((task) -> {
            synchronized (task) {
                task.stop();
            }
        });
    }

    public void schedule(Task task) {
        if (task.getSource() == null)
            throw new InvalidParameterException("Task must have a valid registration source.");
        synchronized (tasks) {

            /**
             * Appends this task to the end of this list
             */
            tasks.add(task);
        }
    }

    public void run() {
        final Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            final Task task = iterator.next();
            if (task.isActive()) {
                task.execute();

                /**
                 * Increments the iteration count. This count should never
                 * require a reset. It would take several years of continuous
                 * execution for this number to overflow.
                 */
                task.setTicks(task.getTicks() + 1);
            } else {
                task.cancel();

                /**
                 * Removes this task from the underlying collection.
                 */
                iterator.remove();
            }
        }
    }
}

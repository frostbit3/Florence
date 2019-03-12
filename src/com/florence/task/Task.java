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

public abstract class Task {

    /**
     * The source that generated this task.
     */
    private Object source;

    /**
     * The name of this task.
     */
    private String name;

    /**
     * Denotes if this task is currently running.
     */
    private boolean active = true;

    /**
     * Denotes the number of times that this task has executed.
     */
    protected long ticks;

    /**
     * Executes this task.
     */
    public abstract void execute();

    /**
     * Executes the last wishes of this task.
     */
    public abstract void cancel();

    public Task(Object source) {
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public boolean isActive() {
        return active;
    }

    public void stop() {
        active = false;
    }

    public long getTicks() {
        return ticks;
    }

    public void setTicks(long ticks) {
        this.ticks = ticks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

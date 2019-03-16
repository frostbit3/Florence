package com.florence.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Graham Edgecombe
 * @author Ryley Kimmel
 * @author Dylan Vicchiarelli
 * @param <T> The type of {@link Entity} to register.
 *
 * An indexed registry responsible for the containment of entities. Available
 * cells are denoted by a null value.
 */
public class EntityRegistry<T extends Entity> implements Iterable<T> {

    private final class EntityRegistryIterator implements Iterator<T> {

        private final EntityRegistry<T> registry;

        /**
         * The current index.
         */
        private int current;

        /**
         * The last index.
         */
        private int last = -1;

        public EntityRegistryIterator(EntityRegistry<T> registry) {
            this.registry = registry;
        }

        @Override
        public boolean hasNext() {
            int index = current;
            while (index <= registry.size()) {
                Entity entity = registry.entities[index++];
                if (entity != null)

                    /**
                     * A next element exists.
                     */
                    return true;
            }

            /**
             * A next element does not exist.
             */
            return false;
        }

        @Override
        public T next() {
            while (current <= registry.size()) {
                Entity entity = registry.entities[current++];
                if (entity != null) {
                    last = current;
                    return (T) entity;
                }
            }
            throw new NoSuchElementException("There are no more elements.");
        }

        @Override
        public void remove() {
            if (last == -1)
                throw new IllegalStateException(
                        "Method 'remove' may only be called once per call to 'next'.");
            registry.remove(last);
            last = -1;
        }
    }

    private final Entity[] entities;
    private int size;

    public EntityRegistry(int capacity) {
        entities = new Entity[capacity];
    }

    public boolean add(T entity) {
        if (size == entities.length)
            return false;
        for (int index = 0; index < entities.length; index++) {
            if (entities[index] != null)
                continue;

            entities[index] = entity;
            entity.setIndex(index + 1);

            /**
             * Expand the current size.
             */
            size++;
            return true;
        }
        return false;
    }

    public T get(int index) {
        if (index < 1 || index >= entities.length + 1)
            throw new IndexOutOfBoundsException("Index is out of bounds.");
        return (T) entities[index - 1];
    }

    public void remove(int index) {
        Entity entity = get(index);
        if (entity.getIndex() != index)
            throw new IllegalArgumentException("Index mismatch, can't remove entity.");

        entities[index - 1] = null;
        entity.setIndex(-1);

        /**
         * Decrease the current size.
         */
        size--;
    }

    @Override
    public Iterator<T> iterator() {
        return new EntityRegistryIterator(this);
    }

    /**
     * Returns the amount of elements that are currently in this registry.
     * @return The returned amount.
     */
    public int size() {
        return size;
    }
}

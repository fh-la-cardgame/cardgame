package cardgame.classes;

import java.util.*;

/**
 * Set die auf die Referenz gleichheit überprüft
 *
 * @param <E>
 */
public class IdentityHashSet<E> extends AbstractSet<E> implements Set<E>,Cloneable {
    private IdentityHashMap<E, Object> identityMap;

    private static final Object PRESENT = new Object();

    public IdentityHashSet() {
        identityMap = new IdentityHashMap<>();
    }

    public IdentityHashSet(Collection<? extends E> c) {
        identityMap = new IdentityHashMap<>(Math.max((int) (c.size()/.75f) + 1, 16));
        addAll(c);
    }

    @Override
    public Iterator<E> iterator() {
        return identityMap.keySet().iterator();
    }

    @Override
    public int size() {
        return identityMap.size();
    }

    public boolean isEmpty() {
        return identityMap.isEmpty();
    }

    public boolean contains(Object o) {
        return identityMap.containsKey(o);
    }

    public boolean add(E e) {
        return identityMap.put(e, PRESENT) == null;
    }

    public boolean remove(Object o) {
        return identityMap.remove(o) == PRESENT;
    }

    public void clear() {
        identityMap.clear();
    }

    public Object clone() {
        try{
            IdentityHashSet<E> newSet = (IdentityHashSet<E>) super.clone();
            newSet.identityMap = (IdentityHashMap<E, Object>) identityMap.clone();
            return newSet;
        }catch (CloneNotSupportedException e){
            throw new InternalError(e);
        }
    }
}

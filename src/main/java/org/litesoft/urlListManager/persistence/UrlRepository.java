package org.litesoft.urlListManager.persistence;

import java.util.List;

import lombok.NonNull;

public interface UrlRepository {
    /**
     * Return the lines from the source.
     *
     * @return nonNull list, might be empty
     */
    @NonNull
    List<String> load();

    /**
     * Add the URL unless it is already in the list.
     *
     * note: an exception says nothing about the state of the backing store (file)!
     *
     * @param url to add (no validation)
     * @return true indicates added, false indicates already there
     * @throws IoRuntimeException if the implementation generated an Exception
     */
    boolean add( String url )
            throws IoRuntimeException;

    /**
     * Delete the URL unless it is not currently in the list.
     *
     * note: an exception says nothing about the state of the backing store (file)!
     *
     * @param url to delete (no validation)
     * @return true indicates deleted, false indicates it wasn't in the list
     * @throws IoRuntimeException if the implementation generated an Exception
     */
    boolean delete( String url )
            throws IoRuntimeException;

    /**
     * Move the URL (to the bottom of the list) unless it is not currently in the list OR is already at the bottom.
     *
     * note: an exception says nothing about the state of the backing store (file)!
     *
     * @param url to move (no validation)
     * @return true indicates moved, false indicates it was already at the bottom, and null indicates it wasn't in the list!
     * @throws IoRuntimeException if the implementation generated an Exception
     */
    Boolean move2Bottom( String url )
            throws IoRuntimeException;
}

package org.litesoft.urlListManager.persistence;

public interface UrlRepositoryFactory {
    UrlRepository repositoryFor( String fileName )
            throws FileNotFoundRuntimeException, IoRuntimeException;
}

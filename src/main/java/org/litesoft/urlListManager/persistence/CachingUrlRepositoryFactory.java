package org.litesoft.urlListManager.persistence;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CachingUrlRepositoryFactory implements UrlRepositoryFactory {
    private final UrlRepositoryFactory proxied;
    private UrlRepository repository;
    private String currentFileName;

    @Override
    public synchronized UrlRepository repositoryFor( String fileName )
            throws FileNotFoundRuntimeException, IoRuntimeException {
        if ( fileName == null ) {
            throw new FileNotFoundRuntimeException( "null fileName not supported" );
        }
        fileName = fileName.trim();
        if ( fileName.isEmpty() ) {
            throw new FileNotFoundRuntimeException( "blank fileName not supported" );
        }
        if ( !fileName.equals( currentFileName ) ) {
            repository = proxied.repositoryFor( fileName );
            currentFileName = fileName;
        }
        return repository;
    }
}

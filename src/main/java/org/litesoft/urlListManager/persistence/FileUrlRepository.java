package org.litesoft.urlListManager.persistence;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class FileUrlRepository implements UrlRepository {
    private final File file;
    private final String fileName;
    private List<String> lines;

    @Override
    public @NonNull List<String> load() {
        return new ArrayList<>( lines );
    }

    @Override
    public synchronized boolean add( String url )
            throws IoRuntimeException {
        if ( lines.contains( url ) ) {
            return false;
        }
        List<String> newLines = new ArrayList<>( lines.size() + 1 );
        newLines.add( url );
        newLines.addAll( lines );
        return updateFile( newLines );
    }

    @Override
    public synchronized boolean delete( String url )
            throws IoRuntimeException {
        int offset = lines.indexOf( url );
        if ( offset == -1 ) {
            return false;
        }
        return updateFile( remove( lines, offset ) );
    }

    @Override
    public synchronized Boolean move2Bottom( String url )
            throws IoRuntimeException {
        int offset = lines.indexOf( url );
        if ( offset == -1 ) {
            return null;
        }
        if ( (offset + 1) == lines.size() ) {
            return false; // already at bottom
        }
        List<String> newLines = remove( lines, offset );
        newLines.add( url );
        return updateFile( newLines );
    }

    private boolean updateFile( List<String> newLines ) {
        return IoRuntimeException.with( fileName, () -> {
            Files.write( file.toPath(), newLines, StandardCharsets.UTF_8 );
            lines = newLines;
            return true;
        } );
    }

    private static List<String> remove( List<String> currentLines, int offset ) {
        int linesSize = currentLines.size();
        List<String> newLines = new ArrayList<>( linesSize ); // new lines will have room for 1 more!
        if ( offset > 0 ) {
            newLines.addAll( currentLines.subList( 0, offset ) );
        }
        offset++;
        if ( offset < linesSize ) {
            newLines.addAll( currentLines.subList( offset, linesSize ) );
        }
        return newLines;
    }
}

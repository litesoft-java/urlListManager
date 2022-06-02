package org.litesoft.urlListManager.persistence;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUrlRepositoryFactory implements UrlRepositoryFactory {
    private final Path filesDir;

    public FileUrlRepositoryFactory( String dirPathForFiles ) {
        String context = "Files dir (" + dirPathForFiles + ")";
        filesDir = IoRuntimeException.with( context, () -> Path.of( dirPathForFiles ).toAbsolutePath() );
        File file = filesDir.toFile();
        if ( !file.isDirectory() ) {
            throw new IoRuntimeException( context + ": not a Directory" );
        }
        checkRW( context, file, "Directory" );
    }

    @Override
    public UrlRepository repositoryFor( String fileName )
            throws FileNotFoundRuntimeException, IoRuntimeException {
        File file = filesDir.getFileSystem().getPath( fileName + ".txt" ).toFile();
        String context = "File (" + fileName + ".txt)";
        String context2 = "dir: " + filesDir;
        if ( !file.exists() ) {
            throw new FileNotFoundRuntimeException( context + " not found in " + context2 );
        }
        checkRW( context, file, context2 );
        List<String> lines = new ArrayList<>();
        IoRuntimeException.with( context,
                                 () -> Files.readAllLines( file.toPath(), StandardCharsets.UTF_8 ) )
                .forEach( s -> {
                    if ( (s != null) && !s.isBlank() ) {
                        lines.add( s.trim() );
                    }
                } );
        return new FileUrlRepository( file, fileName, lines );
    }

    private static void checkRW( String context, File file, String type ) {
        if ( !file.canRead() ) {
            throw new IoRuntimeException( context + ": can't read from " + type );
        }
        if ( !file.canWrite() ) {
            throw new IoRuntimeException( context + ": can't write to " + type );
        }
    }
}

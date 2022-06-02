package org.litesoft.urlListManager.web;

import java.util.concurrent.Callable;

import lombok.extern.slf4j.Slf4j;
import org.litesoft.urlListManager.persistence.CachingUrlRepositoryFactory;
import org.litesoft.urlListManager.persistence.FileNotFoundRuntimeException;
import org.litesoft.urlListManager.persistence.IoRuntimeException;
import org.litesoft.urlListManager.persistence.UrlRepository;
import org.litesoft.urlListManager.persistence.UrlRepositoryFactory;
import org.litesoft.utils.Hexadecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/{fileName}")
@Slf4j
public class PageHandler {
    private final PageBuilder pageBuilder;
    private final UrlRepositoryFactory factory;

    public PageHandler( PageBuilder pageBuilder, UrlRepositoryFactory factory ) {
        this.pageBuilder = pageBuilder;
        this.factory = (factory instanceof CachingUrlRepositoryFactory) ? factory
                                                                        : new CachingUrlRepositoryFactory( factory );
    }

    @GetMapping("/list")
    public ResponseEntity<String> showListPage( @PathVariable String fileName ) {
        return process( "PageHandler.showListPage: /" + fileName + "/list",
                        () -> {
                            UrlRepository repo = factory.repositoryFor( fileName );
                            return ResponseEntity.ok( pageBuilder.generateList( repo.load(), fileName, null ) );
                        } );
    }

    @PostMapping("/list")
    public ResponseEntity<String> updateList( @PathVariable String fileName,
                                              @ModelAttribute("encodedURL") String encodedURL,
                                              @ModelAttribute("action") String action ) {
        return process( "PageHandler.updateList: /" + fileName + "/list   (" + action + ":" + encodedURL + ")",
                        () -> {
                            String url = Hexadecimal.decode( encodedURL );
                            UrlRepository repo = factory.repositoryFor( fileName );
                            String changes;
                            if ( PageBuilder.ACTION_DELETE.equals( action ) ) {
                                changes = delete( repo, url );
                            } else if ( PageBuilder.ACTION_MOVE_TO_BOTTOM.equals( action ) ) {
                                changes = move2Bottom( repo, url );
                            } else {
                                throw new IllegalArgumentException( "unknown form action: " + action );
                            }
                            return ResponseEntity.ok( pageBuilder.generateList( repo.load(), fileName, changes ) );
                        } );
    }

    @GetMapping("/add")
    public ResponseEntity<String> showAddPage( @PathVariable String fileName ) {
        return process( "PageHandler.showAddPage: /" + fileName + "/add",
                        () -> ResponseEntity.ok( pageBuilder.generateAddPage( fileName, null ) )
        );
    }

    @PostMapping("/add")
    public ResponseEntity<String> addEntry( @PathVariable String fileName,
                                            @ModelAttribute("entryURL") String entryURL ) {
        return process( "PageHandler.addEntry: /" + fileName + "/add   (" + entryURL + ")",
                        () -> {
                            UrlRepository repo = factory.repositoryFor( fileName );
                            return ResponseEntity.ok( pageBuilder.generateAddPage( fileName, add( repo, entryURL ) ) );
                        }
        );
    }

    private static ResponseEntity<String> process( String context,
                                                   Callable<ResponseEntity<String>> callable ) {
        String msg;
        HttpStatus status;
        try {
            return callable.call();
        }
        catch ( FileNotFoundRuntimeException | IllegalArgumentException e ) {
            msg = "Error: " + e.getMessage();
            status = HttpStatus.BAD_REQUEST;
        }
        catch ( Exception e ) {
            log.error( context, e );
            msg = "Unexpected error: " + e.getMessage() + "\rfrom: " + context;
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status( status ).header( "Content-Type", "text/plain" )
                .body( msg );
    }

    private static String move2Bottom( UrlRepository repo, String url ) {
        String message = "Moved to Bottom: ";
        try {
            Boolean state = repo.move2Bottom( url );
            if ( state == null ) {
                message = "Error: not found: ";
            } else if (!state) {
                message = "Error: already at the bottom: ";
            }
        }
        catch ( IoRuntimeException e ) {
            message = "Error: (" + e.getMessage() + ") unable to move to bottom: ";
        }
        return message + url;
    }

    private static String delete( UrlRepository repo, String url ) {
        String message = "Deleted: ";
        try {
            if ( !repo.delete( url ) ) {
                message = "Error: not found: ";
            }
        }
        catch ( IoRuntimeException e ) {
            message = "Error: (" + e.getMessage() + ") unable to delete: ";
        }
        return message + url;
    }

    private static String add( UrlRepository repo, String url ) {
        if ((url == null) || url.isBlank()) {
            return "Error: can't add empty URL";
        }
        String message = "Added: ";
        try {
            if ( !repo.add( url.trim() ) ) {
                message = "Error: already exists: ";
            }
        }
        catch ( IoRuntimeException e ) {
            message = "Error: (" + e.getMessage() + ") unable to add: ";
        }
        return message + url;
    }
}

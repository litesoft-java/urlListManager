package org.litesoft.urlListManager.web;

import java.util.List;

import org.litesoft.utils.Hexadecimal;
import org.springframework.stereotype.Component;

@Component
public class PageBuilder {
    public static final String ACTION_DELETE = "Delete";
    public static final String ACTION_MOVE_TO_BOTTOM = "2Bottom";

    private static final char NL = 10;

    private static final String CHANGE_ERROR_PREFIX_LOWERCASE = "error:";

    private static final String PROTOCOL_DOMAIN_SEP = "://";
    private static final String HTTPS = "https" + PROTOCOL_DOMAIN_SEP;
    private static final String HTTP = "http" + PROTOCOL_DOMAIN_SEP;

    private static final String[] PAGE_COMMON_PREFIX = {
            "<html lang=\"en\">",
            "   <head>",
            "      <meta charset=\"UTF-8\"><title>URL list Manager</title>",
            "      <style>",
            "         body,html{text-rendering:optimizeLegibility}",
            "         html{line-height:1.15;-webkit-text-size-adjust:100%}",
            "         body{margin:0}",
            "         main{display:block}",
            "         h1{font-size:2em;margin:.67em 0}",
            "         hr{box-sizing:content-box;height:0;overflow:visible}",
            "         pre{font-family:monospace,monospace;font-size:1em}",
            "         a{background-color:transparent}",
            "         abbr[title]{border-bottom:none;text-decoration:underline;text-decoration:underline dotted}",
            "         b,strong{font-weight:bolder}",
            "         code,kbd,samp{font-family:monospace,monospace;font-size:1em}",
            "         small{font-size:80%}",
            "         .OtherPage{font-size:150%}",
            "         sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}",
            "         sub{bottom:-.25em}",
            "         sup{top:-.5em}",
            "         img{border-style:none}",
            "         button,input,optgroup,select,textarea{font-family:inherit;font-size:100%;line-height:1.15;margin:0}",
            "         button,input{overflow:visible}",
            "         button,select{text-transform:none}",
            "         [type=button],[type=reset],[type=submit],button{-webkit-appearance:button}",
            "         [type=button]::-moz-focus-inner,[type=reset]::-moz-focus-inner,[type=submit]::-moz-focus-inner,button::-moz-focus-inner{border-style:none;padding:0}",
            "         [type=button]:-moz-focusring,[type=reset]:-moz-focusring,[type=submit]:-moz-focusring,button:-moz-focusring{outline:1px dotted ButtonText}",
            "         fieldset{padding:.35em .75em .625em}",
            "         legend{box-sizing:border-box;color:inherit;display:table;max-width:100%;padding:0;white-space:normal}",
            "         progress{vertical-align:baseline}",
            "         textarea{overflow:auto}",
            "         [type=checkbox],[type=radio]{box-sizing:border-box;padding:0}[type=number]::-webkit-inner-spin-button,[type=number]::-webkit-outer-spin-button{height:auto}",
            "         details{display:block}",
            "         summary{display:list-item}",
            "         template{display:none}",
            "         [hidden]{display:none}",
            "         html{font-family:sans-serif}",
            "         body{margin:10}",
            "         .noscript{padding:2rem}",
            "         .noscript a{color:#3d96f7}",
            "      </style>",
            "   </head>",
            "   <body><br>",
            };

    private static final String[] PAGE_COMMON_SUFFIX = {
            "   </body>",
            "</html>",
            };

    private static final String ERROR_PREFIX = "<br><div> ***** ";
    private static final String CHANGE_PREFIX = "<br><div>";
    private static final String ERROR_SUFFIX = " ***** </div><br>" + NL;
    private static final String CHANGE_SUFFIX = "</div><br>" + NL;

    private static final String REPLACE_KEY_FILE_NAME = "{fileName}";
    private static final String REPLACE_KEY_ENCODED_URL = "{encodedURL}";
    private static final String REPLACE_KEY_ENTRY_URL = "{entryURL}";
    private static final String REPLACE_KEY_SHORTER_URI = "{shorterURI}";

    private static final String ADD_PAGE = "<div><form action=\"/" + REPLACE_KEY_FILE_NAME + "/add\" method=\"post\">" + NL +
                                           "    <input type=\"text\" name=\"entryURL\" size=\"63\"/><br>" + NL +
                                           "    <button type=\"submit\" value=\"Submit\">add</button>" + NL +
                                           "</form></div><br>" + NL;

    private static final String LINE;

    static {
        LINE = "<div><form action=\"/" + REPLACE_KEY_FILE_NAME + "/list\" method=\"post\">" + NL +
               "    <input type=\"hidden\" name=\"encodedURL\" value=\"" + REPLACE_KEY_ENCODED_URL + "\">" + NL +
               "    <button type=\"submit\" name=\"action\" value=\"" + ACTION_DELETE + "\">delete</button> &nbsp;" +
               "    <button type=\"submit\" name=\"action\" value=\"" + ACTION_MOVE_TO_BOTTOM + "\">&#8595;</button> &nbsp;" +
               "    <a href=\"" + REPLACE_KEY_ENTRY_URL + "\" target=\"_blank\">" + REPLACE_KEY_SHORTER_URI + "</a>" + NL +
               "</form></div><br>" + NL;
    }

    private static final String LINK_LIST = "<div class=\"OtherPage\"><a href=\"/" + REPLACE_KEY_FILE_NAME + "/list\">List</a></div><br><br>" + NL;
    private static final String LINK_ADD = "<div class=\"OtherPage\"><a href=\"/" + REPLACE_KEY_FILE_NAME + "/add\">Add</a></div><br><br>" + NL;

    public String generateList( List<String> lines, String fileName, Object changes ) {
        StringBuilder sb = addPageCommon();
        addAltPageLink( sb, fileName, LINK_ADD );
        addChanges( sb, changes );
        addFileLines( sb, fileName, lines );
        return addPageCommonSuffix( sb );
    }

    public String generateAddPage( String fileName, Object changes ) {
        StringBuilder sb = addPageCommon();
        addAltPageLink( sb, fileName, LINK_LIST );
        addChanges( sb, changes );
        addAddPage( sb, fileName );
        return addPageCommonSuffix( sb );
    }

    private void addAltPageLink( StringBuilder sb, String fileName, String link ) {
        substitute( sb, link
                , new Replace( REPLACE_KEY_FILE_NAME, fileName )
        );
    }

    private void addURL( StringBuilder sb, String fileName, String url ) {
        String encodedURL = Hexadecimal.encode( url );
        String shorterURI = shortenURL( url );
        substitute( sb, LINE
                , new Replace( REPLACE_KEY_FILE_NAME, fileName )
                , new Replace( REPLACE_KEY_ENCODED_URL, encodedURL )
                , new Replace( REPLACE_KEY_SHORTER_URI, shorterURI )
                , new Replace( REPLACE_KEY_ENTRY_URL, url )
        );
    }

    private void addAddPage( StringBuilder sb, String fileName ) {
        substitute( sb, ADD_PAGE
                , new Replace( REPLACE_KEY_FILE_NAME, fileName )
        );
    }

    private void addFileLines( StringBuilder sb, String fileName, List<String> fileLines ) {
        for ( String line : fileLines ) {
            if ( line != null ) {
                line = line.trim();
                if ( !line.isEmpty() ) {
                    addURL( sb, fileName, line );
                }
            }
        }
    }

    private StringBuilder addPageCommon() {
        return addLines( new StringBuilder(), PAGE_COMMON_PREFIX );
    }

    private void addChanges( StringBuilder sb, Object changes ) {
        if ( changes != null ) {
            String msg = changes.toString();
            if ( isError( msg ) ) {
                sb.append( ERROR_PREFIX ).append( msg ).append( ERROR_SUFFIX );
            } else {
                sb.append( CHANGE_PREFIX ).append( msg ).append( CHANGE_SUFFIX );
            }
        }
    }

    private String addPageCommonSuffix( StringBuilder sb ) {
        return addLines( sb, PAGE_COMMON_SUFFIX ).toString();
    }

    private static StringBuilder addLines( StringBuilder sb, String[] lines ) {
        for ( String line : lines ) {
            sb.append( line ).append( NL );
        }
        return sb;
    }

    private static boolean isError( String msg ) {
        return msg.toLowerCase().startsWith( CHANGE_ERROR_PREFIX_LOWERCASE );
    }

    private static void substitute( StringBuilder sb, String template, Replace... replacements ) {
        if ( (replacements == null) || (replacements.length == 0) ) {
            sb.append( template );
            return;
        }
        StringBuilder tsb = new StringBuilder( template );
        for ( Replace replace : replacements ) {
            String key = replace.key;
            int at = tsb.indexOf( key );
            if ( at == -1 ) {
                System.err.println( "key '" + key + "' not found in: " + template );
                continue;
            }
            tsb.replace( at, at + key.length(), replace.with );
        }
        sb.append( tsb );
    }

    private record Replace(String key, String with) {
    }

    private static String shortenURL( String url ) {
        if ( url.startsWith( HTTPS ) || url.startsWith( HTTP ) ) {
            url = url.substring( url.indexOf( PROTOCOL_DOMAIN_SEP ) + PROTOCOL_DOMAIN_SEP.length() );
        }
        int at = url.indexOf( '/' );
        return (at == -1) ? url : "..." + url.substring( at );
    }
}

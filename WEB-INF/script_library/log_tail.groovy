import org.apache.commons.io.CopyUtils

// Show the last 1000 log lines

pr = Runtime.getRuntime().exec( "tail -1000 ../../../../logs/catalina.out " )
CopyUtils.copy( pr.getInputStream(), System.out )
pr.waitFor()


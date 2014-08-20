/*-------------------------------------------------------------------------
 |   RXTX License v 2.3 - LGPL v 2.1 + Linking Over Controlled Interface.
 |   RXTX is a native interface to serial ports in java.
 |   Copyright 1997-2009 by Trent Jarvi tjarvi@qbang.org and others who
 |   actually wrote it.  See individual source files for more information.
 |
 |   A copy of the LGPL v 2.1 may be found at
 |   http://www.gnu.org/licenses/lgpl.txt on March 4th 2007.  A copy is
 |   here for your convenience.
 |
 |   This library is free software; you can redistribute it and/or
 |   modify it under the terms of the GNU Lesser General Public
 |   License as published by the Free Software Foundation; either
 |   version 2.1 of the License, or (at your option) any later version.
 |
 |   This library is distributed in the hope that it will be useful,
 |   but WITHOUT ANY WARRANTY; without even the implied warranty of
 |   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 |   Lesser General Public License for more details.
 |
 |   An executable that contains no derivative of any portion of RXTX, but
 |   is designed to work with RXTX by being dynamically linked with it,
 |   is considered a "work that uses the Library" subject to the terms and
 |   conditions of the GNU Lesser General Public License.
 |
 |   The following has been added to the RXTX License to remove
 |   any confusion about linking to RXTX.   We want to allow in part what
 |   section 5, paragraph 2 of the LGPL does not permit in the special
 |   case of linking over a controlled interface.  The intent is to add a
 |   Java Specification Request or standards body defined interface in the 
 |   future as another exception but one is not currently available.
 |
 |   http://www.fsf.org/licenses/gpl-faq.html#LinkingOverControlledInterface
 |
 |   As a special exception, the copyright holders of RXTX give you
 |   permission to link RXTX with independent modules that communicate with
 |   RXTX solely through the Sun Microsytems CommAPI interface version 2,
 |   regardless of the license terms of these independent modules, and to copy
 |   and distribute the resulting combined work under terms of your choice,
 |   provided that every copy of the combined work is accompanied by a complete
 |   copy of the source code of RXTX (the version of RXTX used to produce the
 |   combined work), being distributed under the terms of the GNU Lesser General
 |   Public License plus this exception.  An independent module is a
 |   module which is not derived from or based on RXTX.
 |
 |   Note that people who make modified versions of RXTX are not obligated
 |   to grant this special exception for their modified versions; it is
 |   their choice whether to do so.  The GNU Lesser General Public License
 |   gives permission to release a modified version without this exception; this
 |   exception also makes it possible to release a modified version which
 |   carries forward this exception.
 |
 |   You should have received a copy of the GNU Lesser General Public
 |   License along with this library; if not, write to the Free
 |   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 |   All trademarks belong to their respective owners.
 --------------------------------------------------------------------------*/
package gnu.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class to keep the current version in
 */
public class RXTXVersion {

    private static final Logger LOGGER = Logger.getLogger(RXTXVersion.class.getName());
    private static final String EXPECTED_NATIVE_VERSION = "RXTX-2.2-20081207 Cloudhopper Build rxtx.cloudhopper.net";
    private static final String BASE_NAME = "rxtxSerial";
    private static final String OSNAME = System.getProperty("os.name");
    private static final String OSARCH = System.getProperty("os.arch");
    private static final String SHARED_LIBRARY;

    static {
        SHARED_LIBRARY = resourceName();
        provideNativeLibraries();
        displayWelcome();
    }

    private static String resourceName() {
        if (SHARED_LIBRARY != null) {
            throw new IllegalStateException();
        }
        String retVal = BASE_NAME;
        retVal += "-";
        retVal += OSARCH;
        if (OSNAME.toLowerCase().indexOf("windows") != -1) {
            retVal += ".dll";
        } else {
            retVal += ".so";
        }
        return retVal;
    }

    /**
     * static method to return the current version of RXTX unique to RXTX.
     *
     * @return a string representing the version "RXTX-1.4-9"
     */
    public static String getExpectedNativeVersion() {
        return EXPECTED_NATIVE_VERSION;
    }

    public static native String nativeGetVersion();

    private static void provideNativeLibraries() {
        try {
            System.out.println("We will use: " + SHARED_LIBRARY);
            System.out.println(OSNAME);
            File outFile = new File(System.getProperty("java.io.tmpdir"));
            outFile = new File(outFile, SHARED_LIBRARY);
            try (InputStream is = RXTXVersion.class.getResourceAsStream(SHARED_LIBRARY);
                    FileOutputStream fos = new FileOutputStream(outFile)) {
                copy(is, fos);
            }
            System.load(outFile.getAbsolutePath());
            outFile.deleteOnExit();
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }

    private static long copy(InputStream input, OutputStream output) throws IOException {
        final byte[] buffer = new byte[1024];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Perform a crude check to make sure people don't mix versions of the Jar
     * and native lib
     *
     * Mixing the libs can create a nightmare.
     *
     * It could be possible to move this over to RXTXVersion but All we want to
     * do is warn people when first loading the Library.
     */
    static void displayWelcome() {
        final String JarVersion = RXTXVersion.getExpectedNativeVersion();
        final String LibVersion = RXTXVersion.nativeGetVersion();
        LOGGER.info("Stable Library");
        LOGGER.info("=========================================");
        LOGGER.log(Level.INFO, "Native lib Version = {0}", LibVersion);
        LOGGER.log(Level.INFO, "Java lib Version   = {0}", JarVersion);

        if (!JarVersion.equals(LibVersion)) {
            LOGGER.warning("RXTX Version mismatch");
        }
    }

    static void ensureNativeCodeLoaded() {
        if (!getExpectedNativeVersion().equals(nativeGetVersion())) {
            System.out.print("");
        }
    }

    public static String getOsName() {
        return OSNAME;
    }

    public static String getOsArch() {
        return OSARCH;
    }

}

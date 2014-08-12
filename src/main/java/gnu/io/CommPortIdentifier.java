/*-------------------------------------------------------------------------
 |   RXTX License v 2.1 - LGPL v 2.1 + Linking Over Controlled Interface.
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

import java.io.FileDescriptor;
import java.util.HashMap;
import java.util.Vector;
import java.util.Enumeration;
import java.util.logging.Logger;

/**
 * @author Trent Jarvi
 * @author Bartłomiej P. Prokop
 * @version 2.3
 */
public class CommPortIdentifier {

    private static final Logger LOGGER = Logger.getLogger(CommPortIdentifier.class.getName());

    public static final int PORT_SERIAL = 1;  // rs232 Port
    public static final int PORT_PARALLEL = 2;  // Parallel Port
    public static final int PORT_I2C = 3;  // i2c Port
    public static final int PORT_RS485 = 4;  // rs485 Port
    public static final int PORT_RAW = 5;  // Raw Port
    private final String portName;
    private boolean Available = true;
    private String Owner;
    private CommPort commPort;
    private CommDriver RXTXDriver;
    static CommPortIdentifier CommPortIndex;
    CommPortIdentifier next;
    private final int portType;
    static final Object Sync = new Object();
    Vector ownershipListener;

    /**
     * static {} aka initialization accept: - perform: load the rxtx driver
     * return: - exceptions: Throwable comments: static block to initialize the
     * class
     */
    static {
        try {
            CommDriver RXTXDriver = (CommDriver) Class.forName("gnu.io.RXTXCommDriver").newInstance();
            RXTXDriver.initialize();
        } catch (Throwable e) {
            System.err.println(e + " thrown while loading " + "gnu.io.RXTXCommDriver");
        }

        String OS = System.getProperty("os.name");
        if (OS.toLowerCase().indexOf("linux") == -1) {
            LOGGER.warning("Have not implemented native_psmisc_report_owner(PortName)); in CommPortIdentifier");
        }
        RXTXVersion.ensureNativeCodeLoaded();
    }

    CommPortIdentifier(String portName, CommPort commPort, int portType, CommDriver driver) {
        this.portName = portName;
        this.commPort = commPort;
        this.portType = portType;
        this.next = null;
        this.RXTXDriver = driver;
    }

    /**
     * addPortName accept: Name of the port s, Port type, reverence to
     * RXTXCommDriver. perform: place a new CommPortIdentifier in the linked
     * list return: none. exceptions: none. comments:
     *
     * @param s - port name
     * @param type - port type
     * @param c - reference for ComDriver
     */
    public static void addPortName(String s, int type, CommDriver c) {
        LOGGER.fine("CommPortIdentifier:addPortName(" + s + ")");
        AddIdentifierToList(new CommPortIdentifier(s, null, type, c));
    }

    /**
     * AddIdentifierToList() accept: The cpi to add to the list. perform:
     * return: exceptions: comments:
     */
    private static void AddIdentifierToList(CommPortIdentifier cpi) {
        LOGGER.fine("CommPortIdentifier:AddIdentifierToList()");
        synchronized (Sync) {
            if (CommPortIndex == null) {
                CommPortIndex = cpi;
                LOGGER.fine("CommPortIdentifier:AddIdentifierToList() null");
            } else {
                CommPortIdentifier index = CommPortIndex;
                while (index.next != null) {
                    index = index.next;
                    LOGGER.fine("CommPortIdentifier:AddIdentifierToList() index.next");
                }
                index.next = cpi;
            }
        }
    }

    /**
     * addPortOwnershipListener()
     *
     * @param c - listener to add
     */
    public void addPortOwnershipListener(CommPortOwnershipListener c) {
        LOGGER.fine("CommPortIdentifier:addPortOwnershipListener()");

        /*  is the Vector instantiated? */
        if (ownershipListener == null) {
            ownershipListener = new Vector();
        }

        if (!ownershipListener.contains(c)) {
            ownershipListener.addElement(c);
        }
    }

    public String getCurrentOwner() {
        return Owner;
    }

    public String getName() {
        return portName;
    }

    static public CommPortIdentifier getPortIdentifier(String s) throws NoSuchPortException {
        LOGGER.fine("CommPortIdentifier:getPortIdentifier(" + s + ")");
        CommPortIdentifier index;

        synchronized (Sync) {
            index = CommPortIndex;
            while (index != null && !index.portName.equals(s)) {
                index = index.next;
            }
            if (index == null) {
                /* This may slow things down but if you pass the string for the port after
                 a device is plugged in, you can find it now.

                 http://bugzilla.qbang.org/show_bug.cgi?id=48
                 */
                getPortIdentifiers();
                index = CommPortIndex;
                while (index != null && !index.portName.equals(s)) {
                    index = index.next;
                }
            }
        }
        if (index != null) {
            return index;
        } else {
            LOGGER.fine("not found!" + s);
            throw new NoSuchPortException();
        }
    }

    /**
     * Gets communication port identifier
     *
     * @param p - port for which identifier object is returned
     * @return - port identifier object
     * @throws NoSuchPortException - in case the non existing serial port found
     */
    static public CommPortIdentifier getPortIdentifier(CommPort p) throws NoSuchPortException {
        LOGGER.fine("CommPortIdentifier:getPortIdentifier(CommPort)");

        CommPortIdentifier c;
        synchronized (Sync) {
            c = CommPortIndex;
            while (c != null && c.commPort != p) {
                c = c.next;
            }
        }
        if (c != null) {
            return (c);
        }

        LOGGER.fine("not found!" + p.getName());
        throw new NoSuchPortException();
    }

    /**
     * getPortIdentifiers()
     *
     * @return enumeration of available communication ports
     */
    static public Enumeration<CommPortIdentifier> getPortIdentifiers() {

        LOGGER.fine("static CommPortIdentifier:getPortIdentifiers()");

        //Do not allow anybody get any ports while we are re-initializing
        //because the CommPortIndex points to invalid instances during that time
        synchronized (Sync) {
            //Remember old ports in order to restore them for ownership events later
            HashMap oldPorts = new HashMap();
            CommPortIdentifier p = CommPortIndex;
            while (p != null) {
                oldPorts.put(p.portName, p);
                p = p.next;
            }
            CommPortIndex = null;
            try {
                //Initialize RXTX: This leads to detecting all ports
                //and writing them into our CommPortIndex through our method
                //{@link #addPortName(java.lang.String, int, gnu.io.CommDriver)}
                //This works while lock on Sync is held
                CommDriver RXTXDriver = (CommDriver) Class.forName("gnu.io.RXTXCommDriver").newInstance();
                RXTXDriver.initialize();
                //Restore old CommPortIdentifier objects where possible, 
                //in order to support proper ownership event handling.
                //Clients might still have references to old identifiers!
                CommPortIdentifier curPort = CommPortIndex;
                CommPortIdentifier prevPort = null;
                while (curPort != null) {
                    CommPortIdentifier matchingOldPort = (CommPortIdentifier) oldPorts.get(curPort.portName);
                    if (matchingOldPort != null && matchingOldPort.portType == curPort.portType) {
                        //replace new port by old one
                        matchingOldPort.RXTXDriver = curPort.RXTXDriver;
                        matchingOldPort.next = curPort.next;
                        if (prevPort == null) {
                            CommPortIndex = matchingOldPort;
                        } else {
                            prevPort.next = matchingOldPort;
                        }
                        prevPort = matchingOldPort;
                    } else {
                        prevPort = curPort;
                    }
                    curPort = curPort.next;
                }
            } catch (Throwable e) {
                System.err.println(e + " thrown while loading " + "gnu.io.RXTXCommDriver");
                System.err.flush();
            }
        }
        return new CommPortEnumerator();
    }

    public int getPortType() {
        return portType;
    }

    public synchronized boolean isCurrentlyOwned() {
        return (!Available);
    }

    /**
     * open
     *
     * @param f - file descriptor
     * @return - communication port
     * @throws UnsupportedCommOperationException - always as is not implemented
     * yet
     */
    public synchronized CommPort open(FileDescriptor f) throws UnsupportedCommOperationException {
        LOGGER.fine("CommPortIdentifier:open(FileDescriptor)");
        throw new UnsupportedCommOperationException();
    }

    private native String native_psmisc_report_owner(String PortName);

    /**
     * open() accept: application making the call and milliseconds to block
     * during open. perform: open the port if possible return: CommPort if
     * successful exceptions: PortInUseException if in use. comments:
     */
    private boolean HideOwnerEvents;

    public CommPort open(String TheOwner, int i)
            throws gnu.io.PortInUseException {

        LOGGER.fine("CommPortIdentifier:open(" + TheOwner + ", " + i + ")");

        boolean isAvailable;
        synchronized (this) {
            isAvailable = this.Available;
            if (isAvailable) {
                //assume ownership inside the synchronized block
                this.Available = false;
                this.Owner = TheOwner;
            }
        }
        if (!isAvailable) {
            long waitTimeEnd = System.currentTimeMillis() + i;
            //fire the ownership event outside the synchronized block
            fireOwnershipEvent(CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED);
            long waitTimeCurr;
            synchronized (this) {
                while (!Available && (waitTimeCurr = System.currentTimeMillis()) < waitTimeEnd) {
                    try {
                        wait(waitTimeEnd - waitTimeCurr);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                isAvailable = this.Available;
                if (isAvailable) {
                    //assume ownership inside the synchronized block
                    this.Available = false;
                    this.Owner = TheOwner;
                }
            }
        }
        if (!isAvailable) {
            throw new gnu.io.PortInUseException(getCurrentOwner());
        }
        //At this point, the CommPortIdentifier is owned by us.
        try {
            if (commPort == null) {
                commPort = RXTXDriver.getCommPort(portName, portType);
            }
            if (commPort != null) {
                fireOwnershipEvent(CommPortOwnershipListener.PORT_OWNED);
                return commPort;
            } else {
                String err_msg;
                try {
                    err_msg = native_psmisc_report_owner(portName);
                } catch (Throwable t) {
                    err_msg = "Port " + portName + " already owned... unable to open.";
                }
                throw new gnu.io.PortInUseException(err_msg);
            }
        } finally {
            if (commPort == null) {
                //something went wrong reserving the commport -> unown the port
                synchronized (this) {
                    this.Available = true;
                    this.Owner = null;
                }
            }
        }
    }

    /**
     * removes PortOwnership listener
     *
     * @param c - listener to remove
     */
    public void removePortOwnershipListener(CommPortOwnershipListener c) {
        LOGGER.fine("CommPortIdentifier:removePortOwnershipListener()");
        /* why is this called twice? */
        if (ownershipListener != null) {
            ownershipListener.removeElement(c);
        }
    }

    /**
     * clean up the Ownership information and send the event
     */
    void internalClosePort() {
        synchronized (this) {
            LOGGER.fine("CommPortIdentifier:internalClosePort()");
            Owner = null;
            Available = true;
            commPort = null;
            /*  this tosses null pointer?? */
            notifyAll();
        }
        fireOwnershipEvent(CommPortOwnershipListener.PORT_UNOWNED);
    }

    void fireOwnershipEvent(int eventType) {
        LOGGER.fine("CommPortIdentifier:fireOwnershipEvent( " + eventType + " )");
        if (ownershipListener != null) {
            CommPortOwnershipListener c;
            for (Enumeration e = ownershipListener.elements();
                    e.hasMoreElements();
                    c.ownershipChange(eventType)) {
                c = (CommPortOwnershipListener) e.nextElement();
            }
        }
    }
}

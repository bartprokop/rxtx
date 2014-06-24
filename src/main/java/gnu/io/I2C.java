/*-------------------------------------------------------------------------
 |   RXTX License v 2.1 - LGPL v 2.1 + Linking Over Controlled Interface.
 |   RXTX is a native interface to serial ports in java.
 |   Copyright 1997-2007 by Trent Jarvi tjarvi@qbang.org and others who
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

import java.io.*;
import java.util.*;

/**
 * @author Trent Jarvi
 * @version,
 * @since JDK1.0
 */
/**
 * I2C
 */
final class I2C extends I2CPort {

    static {
        System.loadLibrary("rxtxI2C");
        Initialize();
    }

    /**
     * Initialize the native library
     */
    private native static void Initialize();

    /**
     * Actual I2CPort wrapper class
     */
    /**
     * Open the named port
     */
    public I2C(String name) throws PortInUseException {
        fd = open(name);
    }

    private native int open(String name) throws PortInUseException;

    /**
     * File descriptor
     */
    private int fd;

    /**
     * DSR flag *
     */
    static boolean dsrFlag = false;

    /**
     * Output stream
     */
    private final I2COutputStream out = new I2COutputStream();

    @Override
    public OutputStream getOutputStream() {
        return out;
    }

    /**
     * Input stream
     */
    private final I2CInputStream in = new I2CInputStream();

    @Override
    public InputStream getInputStream() {
        return in;
    }

    /**
     * Set the I2CPort parameters
     */
    @Override
    public void setI2CPortParams(int b, int d, int s, int p) throws UnsupportedCommOperationException {
        nativeSetI2CPortParams(b, d, s, p);
        speed = b;
        dataBits = d;
        stopBits = s;
        parity = p;
    }

    /**
     * Set the native I2C port parameters
     */
    private native void nativeSetI2CPortParams(int speed, int dataBits,
            int stopBits, int parity) throws UnsupportedCommOperationException;

    /**
     * Line speed in bits-per-second
     */
    private int speed = 9600;

    @Override
    public int getBaudRate() {
        return speed;
    }

    /**
     * Data bits port parameter
     */
    private int dataBits = DATABITS_8;

    @Override
    public int getDataBits() {
        return dataBits;
    }

    /**
     * Stop bits port parameter
     */
    private int stopBits = I2CPort.STOPBITS_1;

    @Override
    public int getStopBits() {
        return stopBits;
    }

    /**
     * Parity port parameter
     */
    private int parity = I2CPort.PARITY_NONE;

    @Override
    public int getParity() {
        return parity;
    }

    /**
     * Flow control
     */
    private int flowmode = I2CPort.FLOWCONTROL_NONE;

    @Override
    public void setFlowControlMode(int flowcontrol) {
        try {
            setflowcontrol(flowcontrol);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        flowmode = flowcontrol;
    }

    @Override
    public int getFlowControlMode() {
        return flowmode;
    }

    native void setflowcontrol(int flowcontrol) throws IOException;


    /*
     linux/drivers/char/n_hdlc.c? FIXME
     taj@www.linux.org.uk
     */
    /**
     * Receive framing control
     */
    @Override
    public void enableReceiveFraming(int f) throws UnsupportedCommOperationException {
        throw new UnsupportedCommOperationException("Not supported");
    }

    @Override
    public void disableReceiveFraming() {
    }

    @Override
    public boolean isReceiveFramingEnabled() {
        return false;
    }

    @Override
    public int getReceiveFramingByte() {
        return 0;
    }

    /**
     * Receive timeout control
     */
    private int timeout = 0;

    public native int NativegetReceiveTimeout();

    public native boolean NativeisReceiveTimeoutEnabled();

    public native void NativeEnableReceiveTimeoutThreshold(int time, int threshold, int InputBuffer);

    @Override
    public void disableReceiveTimeout() {
        enableReceiveTimeout(0);
    }

    @Override
    public void enableReceiveTimeout(int time) {
        if (time >= 0) {
            timeout = time;
            NativeEnableReceiveTimeoutThreshold(time, threshold, InputBuffer);
        } else {
            System.out.println("Invalid timeout");
        }
    }

    @Override
    public boolean isReceiveTimeoutEnabled() {
        return (NativeisReceiveTimeoutEnabled());
    }

    @Override
    public int getReceiveTimeout() {
        return (NativegetReceiveTimeout());
    }

    /**
     * Receive threshold control
     */
    private int threshold = 0;

    @Override
    public void enableReceiveThreshold(int thresh) {
        if (thresh >= 0) {
            threshold = thresh;
            NativeEnableReceiveTimeoutThreshold(timeout, threshold, InputBuffer);
        } else /* invalid thresh */ {
            System.out.println("Invalid Threshold");
        }
    }

    @Override
    public void disableReceiveThreshold() {
        enableReceiveThreshold(0);
    }

    @Override
    public int getReceiveThreshold() {
        return threshold;
    }

    @Override
    public boolean isReceiveThresholdEnabled() {
        return (threshold > 0);
    }

    /**
     * Input/output buffers
     */
    /**
     * FIXME I think this refers to FOPEN(3)/SETBUF(3)/FREAD(3)/FCLOSE(3)
     * taj@www.linux.org.uk
     *
     * These are native stubs...
     */
    private int InputBuffer = 0;
    private int OutputBuffer = 0;

    @Override
    public void setInputBufferSize(int size) {
        InputBuffer = size;
    }

    @Override
    public int getInputBufferSize() {
        return (InputBuffer);
    }

    @Override
    public void setOutputBufferSize(int size) {
        OutputBuffer = size;
    }

    @Override
    public int getOutputBufferSize() {
        return (OutputBuffer);
    }

    /**
     * Line status methods
     */
    @Override
    public native boolean isDTR();

    @Override
    public native void setDTR(boolean state);

    @Override
    public native void setRTS(boolean state);

    private native void setDSR(boolean state);

    @Override
    public native boolean isCTS();

    @Override
    public native boolean isDSR();

    @Override
    public native boolean isCD();

    @Override
    public native boolean isRI();

    @Override
    public native boolean isRTS();

    /**
     * Write to the port
     */
    @Override
    public native void sendBreak(int duration);

    private native void writeByte(int b) throws IOException;

    private native void writeArray(byte b[], int off, int len)
            throws IOException;

    private native void drain() throws IOException;

    /**
     * I2C read methods
     */
    private native int nativeavailable() throws IOException;

    private native int readByte() throws IOException;

    private native int readArray(byte b[], int off, int len)
            throws IOException;

    /**
     * I2C Port Event listener
     */
    private I2CPortEventListener SPEventListener;

    /**
     * Thread to monitor data
     */
    private MonitorThread monThread;

    /**
     * Process I2CPortEvents
     */
    native void eventLoop();

    public void sendEvent(int event, boolean state) {
        switch (event) {
            case I2CPortEvent.DATA_AVAILABLE:
                if (monThread.Data) {
                    break;
                }
                return;
            case I2CPortEvent.OUTPUT_BUFFER_EMPTY:
                if (monThread.Output) {
                    break;
                }
                return;
            /*
             if( monThread.DSR ) break;
             return;
             if (isDSR())
             {
             if (!dsrFlag) 
             {
             dsrFlag = true;
             I2CPortEvent e = new I2CPortEvent(this, I2CPortEvent.DSR, !dsrFlag, dsrFlag );
             }
             }
             else if (dsrFlag)
             {
             dsrFlag = false;
             I2CPortEvent e = new I2CPortEvent(this, I2CPortEvent.DSR, !dsrFlag, dsrFlag );
             }
             */
            case I2CPortEvent.CTS:
                if (monThread.CTS) {
                    break;
                }
                return;
            case I2CPortEvent.DSR:
                if (monThread.DSR) {
                    break;
                }
                return;
            case I2CPortEvent.RI:
                if (monThread.RI) {
                    break;
                }
                return;
            case I2CPortEvent.CD:
                if (monThread.CD) {
                    break;
                }
                return;
            case I2CPortEvent.OE:
                if (monThread.OE) {
                    break;
                }
                return;
            case I2CPortEvent.PE:
                if (monThread.PE) {
                    break;
                }
                return;
            case I2CPortEvent.FE:
                if (monThread.FE) {
                    break;
                }
                return;
            case I2CPortEvent.BI:
                if (monThread.BI) {
                    break;
                }
                return;
            default:
                System.err.println("unknown event:" + event);
                return;
        }
        I2CPortEvent e = new I2CPortEvent(this, event, !state, state);
        if (SPEventListener != null) {
            SPEventListener.I2CEvent(e);
        }
    }

    /**
     * Add an event listener
     */
    @Override
    public void addEventListener(I2CPortEventListener lsnr) throws TooManyListenersException {
        if (SPEventListener != null) {
            throw new TooManyListenersException();
        }
        SPEventListener = lsnr;
        monThread = new MonitorThread();
        monThread.setDaemon(true);
        monThread.start();
    }

    /**
     * Remove the I2C port event listener
     */
    @Override
    public void removeEventListener() {
        SPEventListener = null;
        if (monThread != null) {
            monThread.interrupt();
            monThread = null;
        }
    }

    @Override
    public void notifyOnDataAvailable(boolean enable) {
        monThread.Data = enable;
    }

    @Override
    public void notifyOnOutputEmpty(boolean enable) {
        monThread.Output = enable;
    }

    @Override
    public void notifyOnCTS(boolean enable) {
        monThread.CTS = enable;
    }

    @Override
    public void notifyOnDSR(boolean enable) {
        monThread.DSR = enable;
    }

    @Override
    public void notifyOnRingIndicator(boolean enable) {
        monThread.RI = enable;
    }

    @Override
    public void notifyOnCarrierDetect(boolean enable) {
        monThread.CD = enable;
    }

    @Override
    public void notifyOnOverrunError(boolean enable) {
        monThread.OE = enable;
    }

    @Override
    public void notifyOnParityError(boolean enable) {
        monThread.PE = enable;
    }

    @Override
    public void notifyOnFramingError(boolean enable) {
        monThread.FE = enable;
    }

    @Override
    public void notifyOnBreakInterrupt(boolean enable) {
        monThread.BI = enable;
    }

    /**
     * Close the port
     */
    private native void nativeClose();

    @Override
    public void close() {
        setDTR(false);
        setDSR(false);
        nativeClose();
        super.close();
        fd = 0;
    }

    /**
     * Finalize the port
     */
    @Override
    protected void finalize() {
        if (fd > 0) {
            close();
        }
    }

    /**
     * Inner class for I2COutputStream
     */
    class I2COutputStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {
            writeByte(b);
        }

        @Override
        public void write(byte b[]) throws IOException {
            writeArray(b, 0, b.length);
        }

        @Override
        public void write(byte b[], int off, int len) throws IOException {
            writeArray(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            drain();
        }
    }

    /**
     * Inner class for I2CInputStream
     */
    class I2CInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            return readByte();
        }

        @Override
        public int read(byte b[]) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(byte b[], int off, int len) throws IOException {
            int i = 0, Minimum = 0;
            int intArray[]
                    = {
                        b.length,
                        InputBuffer,
                        len
                    };
            /*
             find the lowest nonzero value
             timeout and threshold are handled on the native side
             see  NativeEnableReceiveTimeoutThreshold in
             I2CImp.c
             */
            while (intArray[i] == 0 && i < intArray.length) {
                i++;
            }
            Minimum = intArray[i];
            while (i < intArray.length) {
                if (intArray[i] > 0) {
                    Minimum = Math.min(Minimum, intArray[i]);
                }
                i++;
            }
            Minimum = Math.min(Minimum, threshold);
            if (Minimum == 0) {
                Minimum = 1;
            }
            available();
            return readArray(b, off, Minimum);
        }

        @Override
        public int available() throws IOException {
            return nativeavailable();
        }
    }

    class MonitorThread extends Thread {

        /**
         * Note: these have to be separate boolean flags because the
         * I2CPortEvent constants are NOT bit-flags, they are just defined as
         * integers from 1 to 10 -DPL
         */
        private boolean CTS = false;
        private boolean DSR = false;
        private boolean RI = false;
        private boolean CD = false;
        private boolean OE = false;
        private boolean PE = false;
        private boolean FE = false;
        private boolean BI = false;
        private boolean Data = false;
        private boolean Output = false;

        MonitorThread() {
        }

        @Override
        public void run() {
            eventLoop();
        }
    }
}

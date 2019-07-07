# Tests on Raspberry Pi

```
pi@Malina2:~/fps $ java -cp drivers-1.0.1-SNAPSHOT-jar-with-dependencies.jar name.prokop.bart.rxtx.Demo1
/tmp
32
arm
Linux
4.14.98+
lip 07, 2019 11:34:57 PM gnu.io.RXTXInitializer provideNativeLibraries
INFO: Copied rxtxSerial-arm.so to /tmp/rxtxSerial-arm-2.2.2.so
lip 07, 2019 11:34:58 PM gnu.io.RXTXInitializer <clinit>
INFO: Native lib Version = RXTX-2.2pre2
lip 07, 2019 11:34:58 PM gnu.io.RXTXInitializer <clinit>
INFO: Java lib Version   = RXTX-2.2pre2
1 port(ow):/dev/ttyUSB0
*************************************************
1 port(ow) szeregowych:
/dev/ttyUSB0 otwieram OK

0 port(ow) rownoleglych:
```

```
pi@Malina2:~/fps $ java -cp drivers-1.0.1-SNAPSHOT-jar-with-dependencies.jar name.prokop.bart.rxtx.Demo1
/tmp
32
arm
Linux
4.14.98+
lip 07, 2019 11:39:25 PM gnu.io.RXTXInitializer provideNativeLibraries
INFO: Native library rxtxSerial-arm.so already exists at /tmp/rxtxSerial-arm-2.2.2.so
lip 07, 2019 11:39:26 PM gnu.io.RXTXInitializer <clinit>
INFO: Native lib Version = RXTX-2.2pre2
lip 07, 2019 11:39:26 PM gnu.io.RXTXInitializer <clinit>
INFO: Java lib Version   = RXTX-2.2pre2
1 port(ow):/dev/ttyUSB0
*************************************************
1 port(ow) szeregowych:
/dev/ttyUSB0 otwieram OK

0 port(ow) rownoleglych:
```

That's it - Seems proper binary .so file is loaded on Raspberry Pi. Needs more tests.

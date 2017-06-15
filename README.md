
# Cortrium Android SDK

## Installation

To install the SDK follow the instructions for adding a Android library to your project here:

https://developer.android.com/studio/projects/android-library.html#AddDependency


## Connection

To control scanning and connection to the Cortrium C3 device use an instance of the ConnectionManager class. 

```java
ConnectionManager connectionManager = ConnectionManager.getInstance(this);
```

To start and stop scanning for Cortrium devices use the following methods:

```java
connectionManager.startScanning();
connectionManager.stopScanning();
```

To connect and disconnect the Cortrium device:

```java
connectionManager.connect(CortriumC3 device);
connectionManager.disconnect();
```

To receive callbacks from the ConnectionManager implement the OnConnectionManagerListener and call the setConnectionManagerListener method on the ConnectionManager

The following callbacks are available:

```java
public interface OnConnectionManagerListener
    {
        void startedScanning(ConnectionManager manager);
        void stoppedScanning(ConnectionManager manager);
        void discoveredDevice(CortriumC3 device);
        void connectedToDevice(CortriumC3 device);
        void disconnectedFromDevice(CortriumC3 device);
    }
``` 

After a connection is established start receiving measurement callbacks by implementing the EcgDataListener interface:

```java
    public interface EcgDataListener
    {
        void ecgDataUpdated(EcgData ecgData);
        void modeRead(SensorMode sensorMode);
        void deviceInformationRead(CortriumC3 device);
    }
```

And call the setEcgDataListener method on the ConnectionManager. The EcgData class contains all the raw and filtered measurements.

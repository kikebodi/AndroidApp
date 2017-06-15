
# Cortrium Android app example

This app was released to allow the developer community to use Cortrium C3 ECG Monitor.

## Get started 

Download the ZIP file of this repository or clone it by using the following command:
```
git clone https://github.com/cortrium/AndroidApp.git
```
## How to contribute
All contributions are appreciated. We encourage to work in your own branch and create a pull request once we make sure everything works fine.
Here is how:
#### 1- Fork the repository
Click the "Fork" button (just above in this page).
#### 2- Clone your copy to your computer
```
git clone https://github.com/username/AndroidApp.git
```
Where username is YOUR username.
#### 3- Add a connection to the original repository
```
cd AndroidApp
git remote add cortrium https://github.com/cortrium/AndroidApp
```
#### 4- Check the remote add
```
git remote -V
```

#### 5- It's done!
Now we have everything set up.
You can pull code from the original owner's repository by:
```
git pull cortrium master
```
and push the changes to your repo
```
git push
```


# API Documentation

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

# 蓝牙通讯案例

## 名词

* Bluetooth 蓝牙
* Bluetooth LE 低能耗蓝牙
* RFCOMM 协议: 一种仿真串口的数据传输协议, 就像好早的调制解调器使用的协议
* SDP : 服务发现协议,用于发现蓝牙设备都开了哪些服务

## Android 蓝牙API

### 结构

1. BluetoothAdapter

    这个API是蓝牙应用的起始入口,通过这个API来完成蓝牙的开启,监听,扫描

1. BluetoothDevice

    扫描到的设备,就是用这个类来进行描述,包括 连接和绑定

### 抽象层次



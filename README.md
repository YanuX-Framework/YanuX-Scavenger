# YanuX Scavenger
This is part of the [__YanuX Framework__](https://yanux-framework.github.io/).

This repository was started to create an application that explored what kind of contextual information can an Android device provide in order to build pervasive applications.

It also served as an initial testing ground for an __Indoor Positioning Solution__ since it integrated directly with the [__YanuX Broker__](https://github.com/YanuX-Framework/YanuX-Broker) before a separate initiative lead to the creation of the [__Indoor Positioning Server__](https://github.com/YanuX-Framework/YanuX-IPSServer) and the respective clients.

Currently its main purpose is to serve has the counterpart of the [__YanuX Desktop Client__](https://github.com/YanuX-Framework/YanuX-DesktopClient) by taking upon itself the functions of the __YanuX Orchestrator__ for [__Android__](https://www.android.com/). Currently, those functions are limited to the gathering of information about the device where it runs and submits that information to the [__YanuX Broker__](https://github.com/YanuX-Framework/YanuX-Broker).

## Documentation
- Requires Android 6.0 (tested up to Android 10)
- Requires Wi-Fi and Bluetooth Low Energy support
	- The scanning of Bluetooth Low Energy Beacons (iBeacon) is achieved thanks to the [__Android Beacon Library__](https://github.com/AltBeacon/android-beacon-library)

## License
This work is licensed under [__GNU General Public License Version 3__](LICENSE)
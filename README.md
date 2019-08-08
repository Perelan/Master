# The CESAR Project - Nidra

This repository contains Android applications created as a part of my Master's thesis in Computer Science at University of Oslo, 2019.

## Folder Structure

### ModuleTemplate
A template project for a module application - instructions for changes can be found in the thesis (Appendix B).

### TestModule
A module created during experiment (D); finds the record with the highest sample count. 

### Nidra
The application allows for collecting breathing data collected with the Flow sensor (using the DSDM). Also, the support for integrating modules (e.g., TestModule).

### DataStreamsDispatchingModule - [Code by Bugajski](https://github.com/prezemyb/DMMS)
Module responsible for dispatching of data packets, providing sensor-capability model, publish-subscribe interface and controll of collection frequency. 

### Flow (Sensor Wrapper)
Driver application tailored for the Flow sensor over BlueTooth LE API's, customized for the cooperation with DataStreamsDispatchcingModule.

### Bitalino - [Code by Gjøby](https://github.com/sveinpg/DMMS)
Driver application tailored for the BITalino sensorboard, customized for the cooperation with DataStreamsDispatchcingModule.

## Getting Started with the Project.
1. Install the DataStreamsDispatchingModule on the Android device. 
2. Install the Flow (Sensor Wrapper) on the Android device; launch the application and select the desired Flow sensor (make sure to turn BlueTooth and location on, as well as granting permission to the application).
3. Install Nidra on the Android device - you are now good to go! (Note: in order to import records, you have to manually grant the permission of Storage to Nidra). 

## Contact
Having troubles with the code? Create an issue in the repository and I'll help you out as soon as I can!



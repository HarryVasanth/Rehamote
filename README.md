# Rehamote
Android port of the legacy app: https://github.com/athanoid/rehamote

## Description:
RehaMote is a mobile app for smartphones and tablets running Android OS for transmitting (via UDP) the available sensor data of the phone to the RehabNet CP or any other software compatible with the RehabNet protocol. Additionally, RehaMote can receive data via UDP for bidirectional communication of the phone with the CP or a Virtual Environment available online: http://neurorehabilitation.m-iti.org/tools/.
Phone data is streamed over UDP to any application that is able to read a UDP socket with the following structure:

*[$]device_type,[$$]device_name,[$$$]data_type,data_name,X,Y,Z,P;*

## Additional features:
In addition to streaming the data via UDP, this app stores the sensor data in individual sensor files. 

# ocpp-backend-emulator
OCPP Backend Emulator

a simple java application that emulates a OCPP 1.6 backend and output the communication to the console.


```
Start the ocpp-backend-emulator with 2 parameters:
  java -jar OcppDeviceEMulator.jar [port] [scenario]
where
  [port] is the local port to listen to
  [scenario] is the scenario to execute (see content in data/scenario
  
  Then configure your charge point to connect to the bridge ip and port:
    ws://[IP]:[port]/ocpp

The op tag in scenario files is mapped to the files in data/req.
If the file does not exist, a dummy file will be created.


The content of the automatic answers are stored in data/conf

Just edit the files as you want, mess around with the parameters and check how the charger handles it.

Variables used in the json files:
${msgId}         - the message id of the OCPP REQ for conf message, or a new random id for req
${now}           - the UTC date time for now
${transactionId} - the transaction id, will be randomly set at StartTransaction


```

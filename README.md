# WHY: Physiology-triggered multi-modal context gatherer

_Mobile Computing in Biomedical Engineering - CMEB_


Nowadays, context awareness is everywhere in our lives. Our smartphones are equipped with an exhaustive array of sensorization options (some of them continuously monitoring) and the convergence of the wearables and miniaturization trends allows for even more (and, sometimes, more invasive) sensors to be cheaply available in comfortable, ready-to-use packages. The use of these devices (fitness bands, heart rate monitors, EDA monitors, blood pressure monitors) is becoming more and more common, especially within the newer generations, born and bred in technology.

The idea behind WHY is simple: why (_eheh_) not to use this array of readily available sensors, which are nothing but different viewpoints into our daily life and its surroundings, to contextualize potentially critical events of our day? Under the scope of WHY, a _potentially critical event_ will be defined by physiological parameters - more specifically, cardiac parameters, and the several viewpoints used to contextualize it may be of two types:

+ raw sensor information;
+ details derived from sensor outputs, potentially through its processing by 3rd parties (_webservices_);

Particularly useful for individuals with cardiac events triggered by apparently unknown/random patterns or to build profiles danger/alert profiles of workers (in high-risk professions), Why aims to gather around a cardiac event multiple modalities of information, captured around the trigger moment. This information will be shown in the Android application which, besides acting as central viewer, will also contribute to event context through its sensorization capabilites.

## Physiolgical Trigger
 
The chosen cardiac trigger will be heart rate. More specifically, the trigger will be a 2D entity, as heart rate will have to be above a certain threshold (ideally, dynamically generated) for a certain period of time. VitalJacket will be used as the heart rate monitor, as it's both an ECG capturing and ECG processing device, which allows us to offload most of the ECG signal processing to a dedicated and optimized device - VitalJacket's processing already outputs heart rate values over a certain time period. Com,unication with the Android smartphone will be done via Bluetooth.

## Initial viewpoint ideas

Obviously, in construction:

+ Physiological parameters (besides average heart rate during the event, maybe average R-R peak time); 
+ Smartphone localization (through GPS); 
+ Pictures by the smartphone camera (both front and rear cameras, in case of wrong orientation);
+ User temperature (if the Vital Jacket in use allows it);
+ Through the localization, weather information at the event's moment; 
+ Through the localization, traffic status at the event's moment; 
+ The National Authority for Civil Protection, *ProCiv*, which responds to natural catastrophes and trauma events, offers a real-time online service detailing its operational status. Nearby events for whose ProCiv has dispatched teams to could also be an interesting viewpoint; 

_Exhilarated, were we go, embarking on the most daunting and challenging, yet highly rewarding of the quests: the quest for *WHY*._


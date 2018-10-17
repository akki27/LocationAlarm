# LocationAlarm: A location based alarm.
Available Feature:
1. Login Screen.
   NOTE: As register API not available, so for now to do login API test below credentials is hardcode in api (API1)
          userName = "3403243489"
          password = "masterPass"

2. Alarm Screen:  It has following features:
    1. List of saved alarm, if any: Alarm Attributes on UI: Alarm name, Alarm Description, Toggle button (ON/OFF)
       Features available for the saved alarm:
         (a) Toggle (ON/OFF) Alarm
         (b) Delete/Restore: Swipe the alarm item left to delete the alarm. Click on "UNDO" button to restore the deleted alarm.
         (c) Edit Alarm: Click on the saved alarm and do the desired updation for the selected alarm.
    2. New Alarm: Open new screen and provide options to add a new alarm
    3. Location History: Open new screen and show device location history data. [API3]

3. Add new alarm screen:For a new alarm below features need to select/set:
    1. Title/Name (Mandatory)
    2. Description (Optional)
    3. Location: Latitude, Longitude and Altitude (Mandatory)
    4. Ringtone (Mandatory)
    5. IsRepeat (Mandatory)
    6. Repeat Interval (Mandatory) ==> If isRepeat is "YES" show this field to user else hide
    7. IsVibrate (Mandatory)

4. Device Location Feed service (API2): In background, Device location, if updated, saved at server periodically. Current interval is 1 Min only for debugging purpose [For final build it should be more, say >=5 min]

5. Location History Screen: This screen shows device location history, which was saved previously.

TODO:
1. UI enhancements [Current version is a demo app, focused mainly on functionality].
2. Register API implementation, once available.

# LocationAlarm
A location based alarm.
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
4. Location History Screen: This screen shows device location history

TODO:
1. Device Location Feed [API2] is implemented but it is not working for now, as server returning "Un-Authorized" error when using the same login token which the login API returns. Gettig the same error with Google's Postman app. Need to re-check this.
2. Handle multiple alarm at the same location (within range of 100m): Possible way ==> Do not Allow to save new alarm if one with the same location attributes is already saved.
3. Register API implementation once available.

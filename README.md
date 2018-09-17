# easyserver
1-  upload to esi-soft.com

2-  change latitude to lat - longtiude to long - Changed on the table structure on 20180909

20180909:
3-  on the Merchant screen show Version No

4 on our Administrator's desktop-  When client connects show administrator IP and its physical Merchant Number
    
5-  When a new Merchant Registers, a desktop application should popup on our Administrator's desktop to indicate new registeration for the administrator to contact the new Merchant and confirm his registeration

20180911:

5-  For Both [Customers] & [Merchants]:

6-  A new Temp Table should be created for both [CUSTOMER / MERCHANTS] tables witht the same fields plus [Fake] field as boolean and a default value of [Flase]

7-  When a new [Cutomer / Merchant] registers on his mobile, his informatio should be saved on the corresponding temp table

8-  The server should send the same record to the corresponding temp table on the [merchant / Administrator] database indicating a new reisteration request through the [Merchant / Administrator]'s desktop application

9-  on the [merchant / Administrator]'s application a form should show the requests for registeration in a table where [Fake] field is false

10- The [merchant / Administrator] should review the request and calls the [customer / Merchant] to confirm if the request is true or fake, then he clicks on either [True] / [Fake] buttons.

11- if the request is true, the entry in the temp table should be inserted in the [customers / Merchants] table and deleted from the temp table

12- if the request is fake, the entry in the temp table should be set to true in the [Fake] field


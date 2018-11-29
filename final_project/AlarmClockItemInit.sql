CREATE DATABASE "AlarmClock";

GRANT ALL ON DATABASE "AlarmClock" TO "Killua4564";

DROP TABLE "AlarmClockItem";

CREATE TABLE "AlarmClockItem"
(
	"AlarmClockItemID" SERIAL PRIMARY KEY,
	"AlarmClockItemHour" INT NOT NULL DEFAULT 0,
	"AlarmClockItemMinute" INT NOT NULL DEFAULT 0,
	"AlarmClockItemName" VARCHAR (15) DEFAULT NULL,
	"AlarmClockItemFlag" BOOLEAN DEFAULT true
);

REVOKE ALL ON TABLE "AlarmClockItem" FROM "Killua4564";
GRANT ALL ON TABLE "AlarmClockItem" TO "Killua4564";

INSERT INTO "AlarmClockItem" ("AlarmClockItemHour", "AlarmClockItemMinute", "AlarmClockItemName", "AlarmClockItemFlag") 
VALUES (3, 30, 'insert sql test', true);
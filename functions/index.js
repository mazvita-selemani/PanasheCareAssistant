const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.notifyNewShift = functions.database
    .onValueCreated({
      ref: "/shifts/{shiftId}",
      region: "europe-west1",
    }, (snapshot, context) => {
      const shiftData = snapshot.data._data;
      const payload = {
        notification: {
          title: "New Shift Available",
          body: `${shiftData.healthAideName.fullName}
          you have a new shift starts at ${shiftData.shiftDate}`,
        },
        topic: `${shiftData.healthAideName.id}`,
      };

      return admin.messaging().send(payload);
    },
    );

exports.notifyNewVitalsLog = functions.database
    .onValueCreated({
      ref: "/vitals/{vitalsId}",
      region: "europe-west1",
    }, (snapshot, context) => {
      const vitalsData = snapshot.data._data;
      const payload = {
        notification: {
          title: "New Log Today",
          body: `New vitals log created by ${vitalsData.loggerId}`,
        },
        topic: "vitals",
      };

      return admin.messaging().send(payload);
    },
    );
